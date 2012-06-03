package vlove.model.json;

import java.io.Serializable;

public abstract class AgentBaseMessage implements Serializable {
  private final Long timestamp;
  private final String clientId;
  private final String messageType;

  public AgentBaseMessage(Long timestamp, String clientId) {
    this.timestamp = timestamp;
    this.clientId = clientId;
    messageType = this.getClass().getName();
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getClientId() {
    return clientId;
  }

  public String getMessageType() {
    return messageType;
  }
}