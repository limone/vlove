package vlove.web.wicket;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.ValidationErrorFeedback;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public class FormValidatorVisitor implements IVisitor<FormComponent<?>, Void> {
  private AjaxRequestTarget target;

  public FormValidatorVisitor(AjaxRequestTarget target) {
    this.target = target;
  }

  @Override
  public void component(FormComponent<?> formComponent, IVisit<Void> visit) {
      if (!formComponent.isValid() && formComponent.isEnabled() && formComponent.isRequired()) {
        String errorMessage = null;
        FeedbackMessage message = formComponent.getFeedbackMessage();
        if (message != null) {
          message.markRendered();

          ValidationErrorFeedback feedback = (ValidationErrorFeedback) message.getMessage();
          errorMessage = StringEscapeUtils.escapeEcmaScript(feedback.getMessage());
        }

        if (formComponent instanceof RadioGroup<?>) {
          target.appendJavaScript(String.format("groupRequired('%s','%s');", formComponent.getMarkupId(), errorMessage));
        } else {
          target.appendJavaScript(String.format("inputRequired('%s','%s');", formComponent.getMarkupId(), errorMessage));
        }
      } else {
        String compId = formComponent.getMarkupId();
        target.appendJavaScript(String.format("clearError('%s');", compId));
      }
  }
}