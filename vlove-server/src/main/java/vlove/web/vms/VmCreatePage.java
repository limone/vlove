package vlove.web.vms;

import org.wicketstuff.annotation.mount.MountPath;

import vlove.web.BasePage;
import vlove.web.vms.wizard.NewVmWizard;

@MountPath("/vms/create")
public class VmCreatePage extends BasePage {
  public VmCreatePage() {
    add(new NewVmWizard("wizard"));
  }
}