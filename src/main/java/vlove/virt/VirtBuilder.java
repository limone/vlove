package vlove.virt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vlove.dao.ConfigDao;
import vlove.model.NewVmWizardModel;

@Service
public class VirtBuilder {
	private transient final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ConfigDao cd;
	
	public void createVirtMachine(NewVmWizardModel newVm) {
		List<String> command = new ArrayList<String>();
		command.add("vmbuilder");
		command.add("kvm");
		command.add("ubuntu");
		command.add("-v");
		command.add(String.format("--libvirt=%s", cd.getConfigItem("libvirt.url").getValue()));
		command.add(String.format("--hostname=%s", newVm.getVmName()));
		command.add(String.format("--rootsize=%d", newVm.getDiskSize()*1024));
		command.add(String.format("--swapsize=%d", newVm.getMemSize()));
		command.add(String.format("--suite=%s", newVm.getSuite()));
		command.add("--flavour=virtual");
		command.add(String.format("--arch=%s", newVm.getArch()));
		
		if (newVm.getNetworks().equalsIgnoreCase("manual")) {
			command.add(String.format("--bridge=%s", newVm.getBridge()));
		} else {
			command.add(String.format("--network=%s", newVm.getNetworks()));
		}
		
		command.add(String.format("--cpus=%d", newVm.getNumProcs()));
		command.add(String.format("--mem=%d", newVm.getMemSize()));
		
		log.debug("Command: {}", command);
	}
}