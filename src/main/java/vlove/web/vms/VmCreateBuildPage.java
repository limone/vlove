package vlove.web.vms;

import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.model.NewVmWizardModel;
import vlove.virt.VirtBuilder;
import vlove.virt.VirtBuilderThread;
import vlove.web.BasePage;

@MountPath(path = "/vms/create/build")
public class VmCreateBuildPage extends BasePage {
	transient final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	VirtBuilder vb;

	public VmCreateBuildPage() {
		log.debug("Non-parameter page invoked, bombing out.");
		setRedirect(true);
		setResponsePage(VmListPage.class);
	}

	public VmCreateBuildPage(final NewVmWizardModel wizMod) {
		if (wizMod == null) {
			log.warn("The passed in model was null.");
			setRedirect(true);
			setResponsePage(VmListPage.class);
			return;
		}

		final WebMarkupContainer status = new WebMarkupContainer("status");
		add(status.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true).setVisible(false));

		status.add(new Label("output", ""));

		// Populate the confirmation page
		log.debug("Showing build fragment.");
		final Form<NewVmWizardModel> confirmForm = new Form<NewVmWizardModel>("confirmForm", new CompoundPropertyModel<NewVmWizardModel>(wizMod));
		add(confirmForm.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));

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

		confirmForm.add(new AjaxFallbackButton("submit", confirmForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(status.setVisible(true));
				target.addComponent(confirmForm.setVisible(false));
				
				new VirtBuilderContentThread(WebApplication.get(), wizMod).start();
			}
		});
	}
	
	protected class VirtBuilderContentThread extends Thread {
		private final WebApplication app;
		private final NewVmWizardModel wizMod;
		
		public VirtBuilderContentThread(WebApplication app, NewVmWizardModel wizMod) {
			this.app = app;
			this.wizMod = wizMod;
		}

		@Override
		public void run() {
			log.debug("Creating thread and starting.");
			Application.set(app);
			VirtBuilderThread t =  new VirtBuilderThread(vb, wizMod, new ContentListener(app, false), new ContentListener(app, true));
			t.start();
			
			while (!t.isComplete()) {
				try {
					sleep(50);
				} catch (InterruptedException ie) {
					log.warn("Thread interrupted.", ie);
				}
			}
			if (t.isHadError()) {
				log.warn("Error running thread.", t.getError());
			}
		}
	}
	
	protected class ContentListener implements StreamConsumer {
		private final WebApplication app;
		private final boolean isErr;
		
		public ContentListener(WebApplication app, boolean isErr) {
			this.app = app;
			this.isErr = isErr;
		}
		
		@Override
		public void consumeLine(String line) {
			Application.set(app);
		}
	}
}