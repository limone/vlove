package vlove.web.vms;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.model.NewVmWizardModel;
import vlove.virt.VirtBuilder;
import vlove.web.BasePage;

@MountPath(path="/vms/create/confirm")
public class VmCreateConfirmPage extends BasePage {
	transient final Logger log = LoggerFactory.getLogger(getClass());
	
	@SpringBean
	VirtBuilder vb;
	
	public VmCreateConfirmPage() {
		log.debug("Non-parameter page invoked, bombing out.");
		setRedirect(true);
		setResponsePage(VmListPage.class);
	}
	
	public VmCreateConfirmPage(final NewVmWizardModel wizMod) {
		if (wizMod == null) {
			log.warn("The passed in model was null.");
			setRedirect(true);
			setResponsePage(VmListPage.class);
			return;
		}
		
		// Populate the confirmation page
		log.debug("Showing confirmation page.");
		Form<NewVmWizardModel> confirmForm = new Form<NewVmWizardModel>("confirmForm", new CompoundPropertyModel<NewVmWizardModel>(wizMod)) {
			@Override
			protected void onSubmit() {
				vb.createVirtMachine(wizMod);
				setRedirect(true);
				setResponsePage(VmListPage.class);
			}
		};
		add(confirmForm);
		
		confirmForm.add(new TextField<String>("vmName").setEnabled(false));
		confirmForm.add(new TextField<String>("suite").setEnabled(false));
		confirmForm.add(new TextField<String>("arch").setEnabled(false));
		confirmForm.add(new TextField<Integer>("numProcs").setEnabled(false));
		confirmForm.add(new TextField<Integer>("memSize").setEnabled(false));
		confirmForm.add(new TextField<String>("storagePools").setEnabled(false));
		confirmForm.add(new TextField<Integer>("diskSize").setEnabled(false));
		confirmForm.add(new TextField<String>("networks").setEnabled(false));
		
		WebMarkupContainer bridgeContainer = new WebMarkupContainer("bridgeContainer");
		confirmForm.add(bridgeContainer.setVisible(wizMod.getNetworks() != null && wizMod.getNetworks().equalsIgnoreCase("manual")));
		
		bridgeContainer.add(new TextField<String>("bridge").setEnabled(false));
	}
}