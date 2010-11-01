package vlove.web.vms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.web.BasePage;
import vlove.web.vms.wizard.NewVmWizard;

@MountPath(path = "/vms/create")
public class VmCreatePage extends BasePage {
	transient final Logger log = LoggerFactory.getLogger(getClass());
	
	public VmCreatePage() {
		add(new NewVmWizard("wizard"));
	}
}