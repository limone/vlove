package vlove.virt;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import vlove.VirtException;
import vlove.model.InternalDomain;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-config.xml")
public class TestVirtManager {
	private transient final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private VirtManager vm;

	@Before
	public void init() {
		// empty
	}

	@Test
	public void testVirtManager() {
		assertNotNull(vm);
	}

	@Test
	public void testInit() {
		vm.init("qemu+unix:///system");
	}

	@Test
	public void testConnect() {
		vm.init("qemu+unix:///system");
		vm.connect();
	}

	@Test
	public void testGetDomains() throws VirtException {
		vm.init("qemu+unix:///system");
		if (vm.connect()) {
			List<InternalDomain> domains = vm.getDomains();
			for (InternalDomain domain : domains) {
				log.debug("Domain: {}", domain.getDomainName());
			}
		} else {
			throw new RuntimeException("Could not connect to libvirt.");
		}
	}

	@Test
	public void testGetCapabilities() throws VirtException {
		vm.init("qemu+unix:///system");
		if (vm.connect()) {
			assertNotNull(vm.getCapabilities());
		} else {
			throw new RuntimeException("Could not connect to libvirt.");
		}
	}
}