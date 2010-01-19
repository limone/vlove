package vlove.web.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.Component.IVisitor;

public class OutputMarkupIdAlwaysVisitor implements IVisitor<Component> {
	@Override
	public Object component(Component component) {
		component.setOutputMarkupId(true);
		return CONTINUE_TRAVERSAL;
	}
}