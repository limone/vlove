package vlove.web.vms.wizard;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.VirtException;
import vlove.model.InternalStoragePool;
import vlove.service.VirtManager;

import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class Step2Hardware extends WizardStep {
  transient final Logger log = LoggerFactory.getLogger(getClass());

  @SpringBean
  VirtManager            vm;

  public Step2Hardware() {
    // Retrieve all of the hardware info
    int tmpProcs = 1;
    try {
      tmpProcs = vm.getCapabilities().getNumProcs();
    } catch (Exception ex) {
      log.error("Could not retrieve VM capabilities, using stingy defaults.");
    }

    final int numProcs = tmpProcs;

    OperatingSystemMXBean ob = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    final long totalMem = ob.getTotalPhysicalMemorySize() / 1024 / 1024;

    setTitleModel(new ResourceModel("step2title"));
    add(new RequiredTextField<Integer>("numProcs").setMarkupId("numProcs").setOutputMarkupId(true));
    add(new RequiredTextField<Integer>("memSize").setMarkupId("memSize").setOutputMarkupId(true));
    add(new WebMarkupContainer("jsVars") {
      @Override
      public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, String.format("\n\tvar maxProcs=%d;\n\tvar maxMem = %d;", numProcs, totalMem));
      }
    });

    add(new Label("totalProcs", Integer.toString(numProcs)));
    add(new Label("totalMem", Long.toString(totalMem)));

    List<InternalStoragePool> storagePools = null;
    try {
      storagePools = vm.getStoragePools();
    } catch (VirtException ve) {
      log.error("Could not list storage pools.", ve);
      storagePools = new ArrayList<>();
      // TODO - this should actually bomb out to an error page, as this screws
      // the whole process
    }

    final WebMarkupContainer sCont = new WebMarkupContainer("storageContainer");
    add(sCont.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));

    sCont.add(new Label("totalDisk", Long.toString(0)).setOutputMarkupId(true));
    sCont.add(new RequiredTextField<Integer>("diskSize").setMarkupId("diskSize").setOutputMarkupId(true));

    final DropDownChoice<InternalStoragePool> ddStorage = new DropDownChoice<>("storagePool", storagePools, new IChoiceRenderer<InternalStoragePool>() {
      @Override
      public Object getDisplayValue(InternalStoragePool object) {
        return object.getName();
      }

      @Override
      public String getIdValue(InternalStoragePool object, int index) {
        return object.getUuid();
      }
    });

    ddStorage.add(new OnChangeAjaxBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        final String sPool = getFormComponent().getValue();
        log.debug("Looking for storage pool {}.", sPool);
        try {
          InternalStoragePool sp = vm.getStoragePool(sPool);
          final long maxDisk = sp.getAvailable() / 1024 / 1024 / 1024;

          // Update the label with the real value
          Label newTotalDisk = new Label("totalDisk", Long.toString(maxDisk));
          sCont.addOrReplace(newTotalDisk.setOutputMarkupId(true));
          target.add(newTotalDisk.setOutputMarkupId(true));

          target.add(sCont.setVisible(true));

          target.appendJavaScript(String.format("renderDisk(%d);", maxDisk));
        } catch (Exception ex) {
          log.error("Could not retrieve storage pool.", ex);
          // TODO - this should actually bomb out to an error page, as this
          // screws the whole process
        }
      }
    });
    add(ddStorage.setRequired(true));
  }
}