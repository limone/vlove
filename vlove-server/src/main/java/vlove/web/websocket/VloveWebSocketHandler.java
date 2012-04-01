package vlove.web.websocket;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.atmosphere.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

@Configurable
public class VloveWebSocketHandler extends WebSocketHandler {
  protected static final Logger log = LoggerFactory.getLogger(VloveWebSocketHandler.class);
  
  private final IQueue<String> messageQueue;
  private AtmosphereResource r;
  
  public VloveWebSocketHandler() {
    messageQueue = Hazelcast.getDefaultInstance().getQueue("websocket-messaging");
    messageQueue.addItemListener(new ItemListener<String>() {
      @Override
      public void itemRemoved(ItemEvent<String> item) {
        // we don't care
      }
      
      @Override
      public void itemAdded(ItemEvent<String> item) {
        log.debug("Broadcasting message.");
        VloveWebSocketHandler.this.broadcast(item.getItem());
      }
    }, true);
  }

  @Override
  public void onTextMessage(WebSocket webSocket, String message) {
    AtmosphereResource r = webSocket.resource();
    Broadcaster b = lookupBroadcaster(r.getRequest().getPathInfo());

    if (message != null && message.indexOf("message") != -1) {
      b.broadcast(message.substring("message=".length()));
    }
  }

  @Override
  public void onOpen(WebSocket webSocket) {
    // Accept the handshake by suspending the response.
    r = webSocket.resource();
    // Create a Broadcaster based on the path
    Broadcaster b = lookupBroadcaster(r.getRequest().getPathInfo());
    r.setBroadcaster(b);
    r.addEventListener(new WebSocketEventListenerAdapter());
    r.suspend(-1);
  }
  
  public void broadcast(String message) {
    if (r != null) {
      r.getBroadcaster().broadcast(message);
    }
  }

  /**
   * Retrieve the {@link Broadcaster} based on the request's path info.
   *
   * @param pathInfo
   * @return the {@link Broadcaster} based on the request's path info.
   */
  private Broadcaster lookupBroadcaster(String pathInfo) {
    String[] decodedPath = pathInfo.split("/");
    Broadcaster b = BroadcasterFactory.getDefault().lookup(decodedPath[decodedPath.length - 1], true);
    return b;
  }
}