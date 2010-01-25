package vlove.web.config;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.dao.ConfigDao;
import vlove.model.ConfigForm;
import vlove.model.ConfigItem;
import vlove.virt.VirtManager;
import vlove.web.BasePage;
import vlove.web.wicket.FormValidatorVisitor;
import vlove.web.wicket.OutputMarkupIdAlwaysVisitor;

@MountPath(path="/config")
public class ConfigPage extends BasePage {
	private static final Logger log = LoggerFactory.getLogger(ConfigPage.class);
	
	@SpringBean
	ConfigDao cd;
	
	@SpringBean
	VirtManager vm;
	
	public ConfigPage() {
		final ConfigForm configModel = new ConfigForm();
		final ConfigItem ci = cd.getConfigItem("libvirt.url");
		configModel.setLibvirtUrl(ci.getValue());
		
		Form<ConfigForm> configForm = new Form<ConfigForm>("configForm", new CompoundPropertyModel<ConfigForm>(configModel));
		configForm.add(new RequiredTextField<String>("libvirtUrl"));
		configForm.add(new AjaxButton("save", configForm) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> nestedForm) {
				log.debug("Setting libvirt URL to: {}", configModel.getLibvirtUrl());
				ci.setValue(configModel.getLibvirtUrl());
				cd.saveConfigItem(ci);
				
				vm.connect(true);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> nestedForm) {
				nestedForm.visitFormComponents(new FormValidatorVisitor(target));
			}
		});
		add(configForm);
		
		visitChildren(new OutputMarkupIdAlwaysVisitor());
	}
}