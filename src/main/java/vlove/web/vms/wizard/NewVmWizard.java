package vlove.web.vms.wizard;

import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.model.CompoundPropertyModel;

import vlove.model.NewVmWizardModel;
import vlove.web.vms.VmCreateBuildPage;
import vlove.web.vms.VmListPage;

public class NewVmWizard extends Wizard {
	final NewVmWizardModel wizMod = new NewVmWizardModel();

	public NewVmWizard(String id) {
		super(id);
		setDefaultModel(new CompoundPropertyModel<NewVmWizardModel>(wizMod));
		WizardModel mod = new WizardModel();
		mod.add(new Step1Overview());
		mod.add(new Step2Hardware());
		mod.add(new Step3Network());
		init(mod);
	}

	@Override
	public void onCancel() {
		setRedirect(true);
		setResponsePage(VmListPage.class);
	}

	@Override
	public void onFinish() {
		// Redirect to the confirmation page
		setRedirect(true);
		setResponsePage(new VmCreateBuildPage(wizMod));
	}
}