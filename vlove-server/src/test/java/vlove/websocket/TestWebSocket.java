package vlove.websocket;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class TestWebSocket {
  protected static final Logger log = LoggerFactory.getLogger(TestWebSocket.class);
  
  @Test
  public void testWebSocketConnection() {
    AsyncHttpClient c = new AsyncHttpClient();
    try {
      c.prepareGet("ws://localhost:80/vlove/s/1").execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
        @Override
        public void onOpen(WebSocket websocket) {
          log.debug("Socket opened.");
        }

        @Override
        public void onClose(WebSocket websocket) {
          log.debug("Socket closed.");
        }

        @Override
        public void onError(Throwable t) {
          log.error("Error.", t);
        }

        @Override
        public void onMessage(String message) {
          log.debug("Message: {}", message);
        }

        @Override
        public void onFragment(String fragment, boolean last) {
          // TODO Auto-generated method stub
        }
      }).build()).get();
    } catch (Exception ex) {
      log.error("Could not do websocket stuff.", ex);
    }
  }
}