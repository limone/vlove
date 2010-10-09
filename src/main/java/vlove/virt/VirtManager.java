package vlove.virt;

import java.io.File;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vlove.dao.ConfigDao;
import vlove.model.InternalDomain;
import vlove.model.Pair;

import com.sun.management.OperatingSystemMXBean;

@Service
public class VirtManager implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(VirtManager.class);
	
	@Autowired
	private ConfigDao cd;
	
	// Connection information
	private String libvirtUrl;
	private Connect connection;
	
	// MBean
	private OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	
	public VirtManager() {
		// empty, for Spring
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
	}
	
	@PreDestroy
	public void shutdown() {
		disconnect();
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
			log.debug("Connected to libvirt.");
			return true;
		} catch (LibvirtException le) {
			log.error("Could not connect to libvirt.", le);
			return false;
		}
	}
	
	public void disconnect() {
		try {
			log.debug("Disconnecting from libvirt.");
			connection.close();
		} catch (LibvirtException le) {
			log.warn("Could not close connection to libvirt.", le);
		}
	}
	
	public List<InternalDomain> getDomains() {
		List<InternalDomain> domains = new ArrayList<InternalDomain>();
		try {
			for (int did : connection.listDomains()) {
				Domain d = connection.domainLookupByID(did);
				domains.add(new InternalDomain(did, d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory));
			}
			
			for (String domainName : connection.listDefinedDomains()) {
				Domain d = connection.domainLookupByName(domainName);
				domains.add(new InternalDomain(0, d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory));
			}
		} catch (LibvirtException le) {
			log.error("Could not list domains.", le);
		}
		return domains;
	}
	
	public InternalDomain getDomain(Integer domainId) {
		try {
			Domain d = connection.domainLookupByID(domainId);
			return new InternalDomain(domainId, d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
		} catch (LibvirtException le) {
			log.error(String.format("Could not retrieve domain for ID %d.", domainId));
			return null;
		}
	}
	
	public InternalDomain getDomain(String domainName) {
		try {
			Domain d = connection.domainLookupByName(domainName);
			return new InternalDomain(d.getID(), d.getName(), d.getInfo().state, osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
		} catch (LibvirtException le) {
			log.error(String.format("Could not retrieve domain for name %s.", domainName), le);
			return null;
		}
	}
	
	// TODO convert from a PAIR to a TRIPLE so that we can capture the error message, if any
	public Pair<Boolean,Integer> start(String domainName) {
		try {
			Domain d = connection.domainLookupByName(domainName);
			log.debug("Starting domain {}.", domainName);
			d.create();
			d = connection.domainLookupByName(domainName);
			return new Pair<Boolean,Integer>(Boolean.TRUE, d.getID());
		} catch (LibvirtException le) {
			log.error(String.format("Could not start domain %d.", domainName), le);
			return new Pair<Boolean,Integer>(Boolean.FALSE, 0);
		}
	}
	
	public Pair<Boolean,String> resume(Integer domainId) {
		try {
			Domain d = connection.domainLookupByID(domainId);
			log.debug("Resuming domain {}.", domainId);
			d.resume();
			return new Pair<Boolean,String>(Boolean.TRUE, "Domain resumed.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not resume domain %d.", domainId), le);
			return new Pair<Boolean,String>(Boolean.FALSE, le.getMessage());
		}
	}
	
	public Pair<Boolean,String> pause(Integer domainId) {
		try {
			log.debug("Pausing domain {}.", domainId);
			Domain d = connection.domainLookupByID(domainId);
			d.suspend();
			return new Pair<Boolean,String>(Boolean.TRUE, "Domain paused.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not pause domain %d.", domainId), le);
			return new Pair<Boolean,String>(Boolean.FALSE, le.getMessage());
		}
	}
	
	public Pair<Boolean,String> shutdown(Integer domainId) {
		try {
			log.debug("Shutting down domain {}.", domainId);
			Domain d = connection.domainLookupByID(domainId);
			d.shutdown();
			return new Pair<Boolean,String>(Boolean.TRUE, "Domain shutdown.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not shutdown domain %d.", domainId), le);
			return new Pair<Boolean,String>(Boolean.FALSE, le.getMessage());
		}
	}
	
	public Pair<Boolean,String> destroy(Integer domainId) {
		try {
			log.debug("Destroying domain {}.", domainId);
			Domain d = connection.domainLookupByID(domainId);
			d.destroy();
			return new Pair<Boolean,String>(Boolean.TRUE, "Domain nuked.");
		} catch (LibvirtException le) {
			log.error(String.format("Could not destroy domain %d.", domainId), le);
			return new Pair<Boolean,String>(Boolean.FALSE, le.getMessage());
		}
	}
}