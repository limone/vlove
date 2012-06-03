package vlove.model.json;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("disconnect")
public class AgentDisconnectionMessage extends AgentBaseMessage {
  public AgentDisconnectionMessage(Long timestamp, String clientId) {
    super(timestamp, clientId);
  }
}