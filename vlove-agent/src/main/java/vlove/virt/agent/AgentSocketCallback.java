package vlove.virt.agent;

import com.ning.http.client.websocket.WebSocket;

public interface AgentSocketCallback {
  public void onOpen(WebSocket websocket);
  
  public void onClose();
  
  public void onError();
}