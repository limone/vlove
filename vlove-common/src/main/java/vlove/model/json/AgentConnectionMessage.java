package vlove.model.json;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("connect")
public class AgentConnectionMessage extends AgentBaseMessage {
  private List<String> ipAddresses;
  
  public AgentConnectionMessage(List<String> ipAddresses, Long timestamp,
      String clientId) {
    super(timestamp, clientId);
    this.ipAddresses = ipAddresses;
  }

  public List<String> getIpAddresses() {
    return ipAddresses;
  }

  public void setIpAddresses(List<String> ipAddresses) {
    this.ipAddresses = ipAddresses;
  }
}