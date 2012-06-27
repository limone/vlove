package vlove.virt.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.VirtException;
import vlove.virt.VirtManager;

import com.ning.http.client.websocket.WebSocket;

public class AgentWebSocketMessageHandler {
  private static final Logger log = LoggerFactory.getLogger(AgentWebSocketMessageHandler.class);

  private final VirtManager vm = new VirtManager();
  private WebSocket webSocket;

  public AgentWebSocketMessageHandler() {
    vm.init();
    vm.connect();
  }

  public void setWebSocket(WebSocket webSocket) {
    this.webSocket = webSocket;
  }

  public void onMessage(String message) {
    log.debug("Firing message handler.");

    if (webSocket == null || !webSocket.isOpen()) { throw new RuntimeException("WebSocket connection not open."); }

    try {
      if (message.contains("Send your connection")) {
        webSocket.sendTextMessage("server data: " + vm.getCapabilities().toString());
      }
    } catch (VirtException ve) {
      log.error("Could not retrieve information from libvirt.", ve);
      webSocket.sendTextMessage("errrrrrror.");
    }
  }
}