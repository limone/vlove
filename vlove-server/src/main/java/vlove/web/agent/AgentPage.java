package vlove.web.agent;

import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.web.BasePage;

import com.hazelcast.core.Hazelcast;

@MountPath("/agent")
public class AgentPage extends BasePage {
  protected static final Logger log = LoggerFactory.getLogger(AgentPage.class);
  
  public AgentPage() {
    add(new AjaxLink<Object>("injectLink") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        log.debug("Putting agent message on the topic.");
        final String randomId = UUID.randomUUID().toString();
        Hazelcast.getDefaultInstance().getTopic("").publish("Agent message " + randomId);
        final String jsFormattedString = StringEscapeUtils.escapeEcmaScript("Send agent(s) message: " + randomId);
        target.appendJavaScript(String.format("updateOutput('sentMessageContainer', '%s');", jsFormattedString));
      }
    });
  }
}