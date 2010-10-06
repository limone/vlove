package vlove.virt;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-config.xml")
public class TestVirtManager {
	@Autowired
	private VirtManager vm;
	
	@Test
	public void testVirtManager() {
		assertNotNull(vm);
	}

	/*@Test
	public void testVirtManagerConfigDao() {
		fail("Not yet implemented");
	}

	@Test
	public void testInit() {
		fail("Not yet implemented");
	}

	@Test
	public void testConnectBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testConnect() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDomains() {
		fail("Not yet implemented");
	}*/
}