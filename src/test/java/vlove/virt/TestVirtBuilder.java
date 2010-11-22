package vlove.virt;

import java.io.StringWriter;

import org.codehaus.plexus.util.cli.WriterStreamConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import vlove.model.NewVmWizardModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-config.xml")
public class TestVirtBuilder {
	transient final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired VirtBuilder vb;
	
	@Test
	public void testExecute() {
		NewVmWizardModel vm = new NewVmWizardModel();
		vm.setArch("");
		vm.setNetworks("manual");
		vm.setBridge("br0");
		vm.setDiskSize(50);
		vm.setMemSize(512);
		vm.setNumProcs(1);
		vm.setSuite("");
		vm.setVmName("test");
		
		StringWriter os = new StringWriter();
		final WriterStreamConsumer out = new WriterStreamConsumer(os);
		StringWriter es = new StringWriter();
		final WriterStreamConsumer err = new WriterStreamConsumer(es);
		
		VirtBuilderThread t =  new VirtBuilderThread(vb, vm, out, err);
		t.start();
		
		while (!t.isComplete()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				// blah
			}
		}
		
		log.debug("Output: {}", os.toString());
		log.debug("Error: {}", es.toString());
		
		if (t.isHadError()) {
			log.error("Error.", t.getError());
		}
	}	
}