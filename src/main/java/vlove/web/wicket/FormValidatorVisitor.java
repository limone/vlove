package vlove.web.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IFormVisitorParticipant;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.ValidationErrorFeedback;
import org.apache.wicket.markup.html.form.FormComponent.IVisitor;

public class FormValidatorVisitor implements IVisitor {
	private AjaxRequestTarget target;

	public FormValidatorVisitor(AjaxRequestTarget target) {
		this.target = target;
	}

	@Override
	public Object formComponent(IFormVisitorParticipant component) {
		if (component instanceof FormComponent<?>) {
			FormComponent<?> formComponent = (FormComponent<?>) component;
			if (!formComponent.isValid() && formComponent.isEnabled() && formComponent.isRequired()) {
				String errorMessage = null;
				FeedbackMessage message = formComponent.getFeedbackMessage();
				if (message != null) {
					message.markRendered();

					ValidationErrorFeedback feedback = (ValidationErrorFeedback) message.getMessage();
					errorMessage = feedback.getMessage();
				}

				if (formComponent instanceof RadioGroup<?>) {
					target.appendJavascript(String.format("groupRequired('%s','%s');", formComponent.getMarkupId(), errorMessage));
				} else {
					target.appendJavascript(String.format("inputRequired('%s','%s');", formComponent.getMarkupId(), errorMessage));
				}
			} else {
				String compId = formComponent.getMarkupId();
				target.appendJavascript(String.format("clearError('%s')", compId));
			}

			if (component.processChildren()) { return Component.IVisitor.CONTINUE_TRAVERSAL; }
			return Component.IVisitor.CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
		}
		return component;
	}
}