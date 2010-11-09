package vlove.virt;

import java.io.File;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.xpath.XPathConstants;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.Network;
import org.libvirt.StoragePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import vlove.VirtException;
import vlove.dao.ConfigDao;
import vlove.model.Capabilities;
import vlove.model.ConfigItem;
import vlove.model.InternalDomain;
import vlove.model.Pair;
import vlove.util.XPathUtils;

import com.sun.management.OperatingSystemMXBean;

@Service
public class VirtManager implements Serializable {
	private transient final Logger log = LoggerFactory.getLogger(getClass());
	private boolean isConnected = false;

	@Autowired
	private ConfigDao cd;

	// Connection information
	private String libvirtUrl;
	private Connect connection;

	// MBean
	private OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

	public VirtManager() {
		// empty, for Spring
	}

	private void checkConnected() {
		if (!isConnected) {
			log.error("Not connected to libvirt, failing operation.");
			throw new RuntimeException("Not connected to libvirt, failing operation.");
		}
	}

	@PostConstruct
	public void init() {
		final String os = System.getProperty("os.name");
		log.debug("Loading driver for OS: {}", os);

		if (os != null && os.toLowerCase().contains("windows")) {
			final URL resource = getClass().getResource("/libvirt-0.dll");
			if (resource != null) {
				try {
					System.load(resource.toURI().getPath());
				} catch (URISyntaxException use) {
					log.warn("Could not parse URI path for libvirt.dll.");
					throw new RuntimeException("Could not parse URI path for libvirt.dll.");
				}
			} else {
				throw new RuntimeException("Could not load libvirt.dll.");
			}
		} else if (os != null && os.toLowerCase().contains("linux")) {
			final String driver = "/usr/lib/libvirt.so.0";
			if (new File(driver).exists()) {
				System.load(driver);
			} else {
				throw new RuntimeException("Could not load /usr/lib/libvirt.so.0.");
			}
		}

		// Set the default URL
		libvirtUrl = cd.getConfigItem("libvirt.url").getValue();
		if (libvirtUrl == null || libvirtUrl.length() == 0) {
			log.warn("No libvirt URL defined, setting to default: qemu:///session");
			libvirtUrl = "qemu:///session";
		}
	}

	/**
	 * Used to rebuild the connection to libvirt on the fly.
	 * 
	 * @param newLibvirtUrl
	 *          URL for libvirt
	 */
	public void init(String newLibvirtUrl) {
		cd.saveConfigItem(new ConfigItem("libvirt.url", newLibvirtUrl));
		init();
	}

	/**
	 * Doesn't shut down a VM, but the VirtManager itself.
	 */
	@PreDestroy
	public void shutdown() {
		disconnect();
	}

	public boolean isConnected() {
		return isConnected;
	}

	public boolean validateConfig() {
		log.debug("Validating config.");
		if (cd.getConfigItem("libvirt.url").getValue() == null || cd.getConfigItem("vmbuilder.location").getValue() == null) {
			log.warn("One of the configuration options was missing.");
			return false;
		}
		log.debug("Configuration was fine.");
		return true;
	}

	public boolean connect(boolean restart) {
		if (restart) {
			libvirtUrl = cd.getConfigItem("libvirt.url").getValue();
		}
		return connect();
	}

	public boolean connect() {
		try {
			log.debug("Attempting connection to {}.", libvirtUrl);
			connection = new Connect(libvirtUrl);
			isConnected = true;
			log.debug("Connected to libvirt.");
			return true;
		} catch (LibvirtException le) {
			log.error("Could not connect to libvirt.", le);
			return false;
		}
	}

	public void disconnect() {
		if (isConnected) {
			try {
				log.debug("Disconnecting from libvirt.");
				connection.close();
			} catch (LibvirtException le) {
				log.warn("Could not close connection to libvirt.", le);
			}
			isConnected = false;
		} else {
			log.info("Not connected to libvirt, no need to disconnect.");
		}
	}

	public Capabilities getCapabilities() throws VirtException {
		checkConnected();

		Capabilities c = new Capabilities();

		try {
			final String capabilities = connection.getCapabilities();
			Document d = XPathUtils.loadDocument(capabilities.getBytes());
			c.setCpuArch(((String) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/arch", XPathConstants.STRING)));
			c.setModel(((String) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/model", XPathConstants.STRING)));
			c.setVendor(((String) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/vendor", XPathConstants.STRING)));

			// To be added later, just dump it to the log for now:
			Node t = (Node) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/topology", XPathConstants.NODE);
			Integer sockets = Integer.parseInt(t.getAttributes().getNamedItem("sockets").getNodeValue());
			Integer cores = Integer.parseInt(t.getAttributes().getNamedItem("cores").getNodeValue());
			Integer threads = Integer.parseInt(t.getAttributes().getNamedItem("threads").getNodeValue());

			// log.debug("Topology - sockets: {}, cores: {}, threads: {}", new Object[] { sockets, cores, threads });
			c.setNumProcs(sockets * cores * threads);
		} catch (LibvirtException le) {
			log.error("Could not retrieve capabilities from libvirt.", le);
			throw new VirtException("Could not retrieve capabilities from libvirt.", le);
		}

		return c;
	}

	public List<InternalDomain> getDomains() {
		checkConnected();
		log.debug("Retrieving all domains.");
		List<InternalDomain> domains = new ArrayList<InternalDomain>();
		try {
			for (int did : connection.listDomains()) {
				Domain d = connection.domainLookupByID(did);
				domains.add(new InternalDomain(did, d.getUUIDString(), d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory));
			}

			for (String domainName : connection.listDefinedDomains()) {
				Domain d = connection.domainLookupByName(domainName);
				domains.add(new InternalDomain(0, d.getUUIDString(), d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory));
			}
		} catch (LibvirtException le) {
			log.error("Could not list domains.", le);
		}
		log.debug("Domain: {}", domains);
		return domains;
	}

	public InternalDomain getDomain(Integer domainId) throws VirtException {
		checkConnected();
		try {
			Domain d = connection.domainLookupByID(domainId);
			return new InternalDomain(domainId, d.getUUIDString(), d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
		} catch (LibvirtException le) {
			throw new VirtException(String.format("Could not retrieve domain for ID %d.", domainId));
		}
	}

	public InternalDomain getDomain(String domainName) throws VirtException {
		checkConnected();
		try {
			Domain d = connection.domainLookupByName(domainName);
			return new InternalDomain(d.getID(), d.getUUIDString(), d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
		} catch (LibvirtException le) {
			throw new VirtException(String.format("Could not retrieve domain for name %s.", domainName), le);
		}
	}
	
	public InternalDomain getDomainByUUID(String uuid) throws VirtException{
		try {
			Domain d = connection.domainLookupByUUIDString(uuid);
			return new InternalDomain(d.getID(), d.getUUIDString(), d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
		} catch (LibvirtException le) {
			throw new VirtException(String.format("Could not retrieve domain for UUID %s.", uuid));
		}
	}

	// TODO convert from a PAIR to a TRIPLE so that we can capture the error
	// message, if any
	public Pair<Boolean, Integer> start(String domainName) {
		checkConnected();
		try {
			Domain d = connection.domainLookupByName(domainName);
			log.debug("Starting domain {}.", domainName);
			d.create();
			d = connection.domainLookupByName(domainName);
			return new Pair<Boolean, Integer>(Boolean.TRUE, d.getID());
		} catch (LibvirtException le) {
			log.error(String.format("Could not start domain %s.", domainName), le);
			return new Pair<Boolean, Integer>(Boolean.FALSE, 0);
		}
	}

	public Pair<Boolean, String> resume(Integer domainId) {
		checkConnected();
		try {
			Domain d = connection.domainLookupByID(domainId);
			log.debug("Resuming domain {}.", domainId);
			d.resume();
			return new Pair<Boolean, String>(Boolean.TRUE, "Domain resumed.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not resume domain %d.", domainId), le);
			return new Pair<Boolean, String>(Boolean.FALSE, le.getMessage());
		}
	}

	public Pair<Boolean, String> pause(Integer domainId) {
		checkConnected();
		try {
			log.debug("Pausing domain {}.", domainId);
			Domain d = connection.domainLookupByID(domainId);
			d.suspend();
			return new Pair<Boolean, String>(Boolean.TRUE, "Domain paused.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not pause domain %d.", domainId), le);
			return new Pair<Boolean, String>(Boolean.FALSE, le.getMessage());
		}
	}

	public Pair<Boolean, String> shutdown(Integer domainId) {
		checkConnected();
		try {
			log.debug("Shutting down domain {}.", domainId);
			Domain d = connection.domainLookupByID(domainId);
			d.shutdown();
			return new Pair<Boolean, String>(Boolean.TRUE, "Domain shutdown.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not shutdown domain %d.", domainId), le);
			return new Pair<Boolean, String>(Boolean.FALSE, le.getMessage());
		}
	}

	public Pair<Boolean, String> destroy(Integer domainId) {
		checkConnected();
		try {
			log.debug("Destroying domain {}.", domainId);
			Domain d = connection.domainLookupByID(domainId);
			d.destroy();
			return new Pair<Boolean, String>(Boolean.TRUE, "Domain nuked.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not destroy domain %d.", domainId), le);
			return new Pair<Boolean, String>(Boolean.FALSE, le.getMessage());
		}
	}
	
	public List<String> getStoragePools() throws VirtException {
		try {
			return Arrays.asList(connection.listStoragePools());
		} catch (LibvirtException le) {
			throw new VirtException("Could not retrieve list of storage pools.", le);
		}
	}
	
	public StoragePool getStoragePool(String name) throws VirtException {
		try {
			return connection.storagePoolLookupByName(name);
		} catch (LibvirtException le) {
			throw new VirtException("Could not retrieve storage pool.", le);
		}
	}
	
	public List<String> getNetworks() throws VirtException {
		try {
			return Arrays.asList(connection.listNetworks());
		} catch (LibvirtException le) {
			throw new VirtException("Could not retrieve list of networks.", le);
		}
	}
	
	public Network getNetwork(String name) throws VirtException {
		try {
			return connection.networkLookupByName(name);
		} catch (LibvirtException le) {
			throw new VirtException("Could not retrieve network information.", le);
		}
	}
}