package vlove.web.vms.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.libvirt.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.VirtException;
import vlove.virt.VirtManager;

public class Step3Network extends WizardStep {
	transient final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	VirtManager vm;

	public Step3Network() {
		List<String> networks = new ArrayList<String>();
		try {
			networks.add("manual");
			networks.addAll(vm.getNetworks());
		} catch (VirtException ve) {
			log.error("Could not retrieve networks.", ve);
			// TODO bomb out properly
		}
		
		final WebMarkupContainer bridgeCont = new WebMarkupContainer("bridgeContainer");
		add(bridgeCont.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));
		
		bridgeCont.add(new RequiredTextField<String>("bridge"));

		final DropDownChoice<String> ddNetworks = new DropDownChoice<String>("networks", networks, new IChoiceRenderer<String>() {
			@Override
			public Object getDisplayValue(String object) {
				return object;
			}

			@Override
			public String getIdValue(String object, int index) {
				return object;
			}
		});

		ddNetworks.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					final String network = getFormComponent().getValue();
					if (network == null || network.length() == 0) { return; }

					if (network.equalsIgnoreCase("manual")) {
						// Pop up the text field asking for the bridge
						target.addComponent(bridgeCont.setVisible(true));
					} else {
						target.addComponent(bridgeCont.setVisible(false));
						Network n = vm.getNetwork(network);
						log.debug("Bridge name: {}", n.getBridgeName());
					}
				} catch (Exception ex) {
					log.error("Could not process selected network.", ex);
					// TODO bomb out
				}
			}
		});
		add(ddNetworks.setRequired(true));
	}
}