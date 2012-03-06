package vlove.web.config;

import java.io.File;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
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

@MountPath("/config")
public class ConfigPage extends BasePage {
  transient final Logger log = LoggerFactory.getLogger(getClass());

  @SpringBean
  ConfigDao              cd;

  @SpringBean
  VirtManager            vm;

  public ConfigPage() {
    final ConfigForm configModel = new ConfigForm();
    final ConfigItem ci = cd.getConfigItem("libvirt.url");
    configModel.setLibvirtUrl(ci.getValue());

    final ConfigItem vl = cd.getConfigItem("vmbuilder.location");
    configModel.setVmbuilderLocation(vl.getValue());

    final ConfigItem sp = cd.getConfigItem("sudo.password");
    configModel.setSudoPassword(sp.getValue());

    Form<ConfigForm> configForm = new Form<>("configForm", new CompoundPropertyModel<>(configModel));
    configForm.add(new RequiredTextField<String>("libvirtUrl"));
    configForm.add(new PasswordTextField("sudoPassword"));

    final RequiredTextField<String> vmBuilderField = new RequiredTextField<>("vmbuilderLocation");
    configForm.add(vmBuilderField.setOutputMarkupId(true).setMarkupId("vmbuilderLocation"));
    
    configForm.add(new AjaxButton("save", configForm) {
      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> nestedForm) {
        log.debug("Setting libvirt URL to: {}", configModel.getLibvirtUrl());
        ci.setValue(configModel.getLibvirtUrl());
        cd.saveConfigItem(ci);

        final String vmbuilderLocation = configModel.getVmbuilderLocation();
        log.debug("Setting vmbuilder path to: {}", vmbuilderLocation);
        final File vmbuilder = new File(vmbuilderLocation, "vmbuilder");
        if (!vmbuilder.exists()) {
          // Error, doesn't exist
          log.warn("Could not find vmbuilder at the specified path.");
          target.appendJavaScript(String.format("inputRequired('%s', 'We could not find vmbuilder at the specified path.');", vmBuilderField.getId()));
          return;
        }

        if (!vmbuilder.isFile()) {
          // It isn't a file?!
          log.warn("vmbuilder exists, but apparently it isn't a file.  Whoah?");
          target.appendJavaScript(String.format("inputRequired('%s', 'vmbuilder exists, but apparently it isn't a file.  Whoah?');", vmBuilderField.getId()));
          return;
        }

        if (!vmbuilder.canExecute()) {
          // We can't run it
          log.warn("vmbuilder exists and is a file, we just cannot execute it.  Weak.");
          target.appendJavaScript(String.format("inputRequired('%s', 'vmbuilder exists and is a file, we just cannot execute it.  Weak.');", vmBuilderField.getId()));
          return;
        }

        vl.setValue(vmbuilderLocation);
        cd.saveConfigItem(vl);

        if (configModel.getSudoPassword() != null && configModel.getSudoPassword().length() > 0) {
          log.debug("Saving sudo password.");
          sp.setValue(configModel.getSudoPassword());
          cd.saveConfigItem(sp);
        }

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