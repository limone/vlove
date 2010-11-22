package vlove.virt;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vlove.VirtException;
import vlove.dao.ConfigDao;
import vlove.model.NewVmWizardModel;

@Service
public class VirtBuilder {
	private transient final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ConfigDao cd;
	
	private List<String> createVirtMachineCommand(NewVmWizardModel newVm) {
		// TODO Validate that the model is configured properly
		
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
		return command;
	}
	
	/**
	 * Returns the exit value of the process.
	 * 
	 * @param out
	 * @param err
	 * @return
	 * @throws VirtException
	 */
	public int createVirtualMachine(NewVmWizardModel newVm, StreamConsumer out, StreamConsumer err) throws VirtException {
		List<String> command = createVirtMachineCommand(newVm);
		if (command == null || command.size() < 1) {
			throw new VirtException("Command needs to be provided.");
		}
		
		try {
			Commandline cs = new Commandline();
			cs.setExecutable(command.get(0));
			if (command.size() > 1) {
				cs.addArguments(command.subList(1, command.size()).toArray(new String[]{}));
			}
			return CommandLineUtils.executeCommandLine(cs, out, err);
		} catch (CommandLineException ce) {
			throw new VirtException("Could not execute command.", ce);
		}
	}
}