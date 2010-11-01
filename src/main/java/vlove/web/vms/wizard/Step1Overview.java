package vlove.web.vms.wizard;

import java.util.Arrays;

import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.ResourceModel;

public class Step1Overview extends WizardStep {
	public Step1Overview() {
		setTitleModel(new ResourceModel("step1title"));
		add(new RequiredTextField<String>("vmName"));
		add(new DropDownChoice<String>("suite", Arrays.asList(new String[]{"dapper", "hardy", "intrepid", "jaunty", "karmic", "lucid"})).setRequired(true));
		add(new DropDownChoice<String>("arch", Arrays.asList(new String[]{"amd64", "i386", "lpia"})).setRequired(true));
	}
}