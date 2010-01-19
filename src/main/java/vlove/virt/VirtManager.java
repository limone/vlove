package vlove.virt;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vlove.dao.ConfigDao;

@Service
public class VirtManager implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(VirtManager.class);
	
	@Autowired
	private ConfigDao cd;
	
	// Connection information
	private String libvirtUrl;
	private Connect connection;
	
	@PostConstruct
	public void init() {
		final String os = System.getProperty("os.name");
		log.debug("Loading driver for OS: {}", os);
		
		if (os != null && os.toLowerCase().contains("windows")) {
			final URL resource = getClass().getResource("/lib/libvirt.dll");
			if (resource != null) {
				System.load(resource.toString());
				connect();
			} else {
				log.warn("Could not load libvirt.dll.");
			}
		}
		
		libvirtUrl = cd.getConfigItem("libvirt.url").getValue();
	}
	
	public boolean connect() {
		try {
			connection = new Connect(libvirtUrl);
			return true;
		} catch (LibvirtException le) {
			log.error("Could not connect to libvirt.", le);
			return false;
		}
	}
	
	public List<Domain> getDomains() {
		List<Domain> domains = new ArrayList<Domain>();
		try {
			for (int did : connection.listDomains()) {
				domains.add(connection.domainLookupByID(did));
			}
		} catch (LibvirtException le) {
			log.error("Could not list domains.", le);
		}
		return domains;
	}
}