package vlove.web.websocket;

import java.util.UUID;

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
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

@Configurable
public class VloveWebSocketHandler extends WebSocketHandler {
  protected static final Logger log = LoggerFactory.getLogger(VloveWebSocketHandler.class);
  
  private final ITopic<String> messageQueue;
  private static final String uuid = UUID.randomUUID().toString();
  private AtmosphereResource r;
  
  public VloveWebSocketHandler() {
    messageQueue = Hazelcast.getDefaultInstance().getTopic("websocket-messaging");
    messageQueue.addMessageListener(new MessageListener<String>() {
      @Override
      public void onMessage(Message<String> message) {
        log.debug("Sending message.");
        VloveWebSocketHandler.this.broadcast(message.getMessageObject());
      }
    });
  }

  @Override
  public void onTextMessage(WebSocket webSocket, String message) {
    log.debug("Received a message from an agent: {}", message);
    
    /*AtmosphereResource r = webSocket.resource();
    Broadcaster b = lookupBroadcaster(r.getRequest().getPathInfo());

    if (message != null && message.indexOf("message") != -1) {
      b.broadcast(message.substring("message=".length()));
    }*/
  }

  @Override
  public void onOpen(WebSocket webSocket) {
    log.debug("WebSocket agent connection established.");
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
      log.debug("Sending message to agent.");
      r.getBroadcaster().broadcast(message);
    } else {
      log.warn("{} not connected, could not broadcast message.", uuid);
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