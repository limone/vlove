package vlove.virt.agent;

import java.io.IOException;

import jline.console.ConsoleReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class AgentWebSocketClient {
  protected static final Logger log = LoggerFactory.getLogger(AgentWebSocketClient.class);
  private final AsyncHttpClient c = new AsyncHttpClient();
  protected final ConsoleReader reader;

  public AgentWebSocketClient(ConsoleReader reader) {
    this.reader = reader;
  }

  public void connect() {
    try {
      c.prepareGet("ws://localhost:80/vlove/s/agent").execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
        @Override
        public void onOpen(WebSocket websocket) {
          try {
            reader.println("WebSocket connection established.");
            reader.flush();
          } catch (IOException e) {
            // empty
          }
        }

        @Override
        public void onClose(WebSocket websocket) {
          try {
            reader.println("WebSocket connection terminated.");
            reader.flush();
          } catch (IOException e) {
            // empty
          }
        }

        @Override
        public void onError(Throwable t) {
          try {
            reader.println("Error occured in WebSocket communication.  " + t.getMessage());
            reader.flush();
          } catch (IOException e) {
            // empty
          }
        }

        @Override
        public void onMessage(String message) {
          try {
            reader.println("Message from server: " + message);
            reader.flush();
          } catch (IOException e) {
            // empty
          }
        }

        @Override
        public void onFragment(String fragment, boolean last) {
          // empty
        }
      }).build()).get();
    } catch (Exception ex) {
      try {
        reader.println("Could not establish WebSocketConnection.  " + ex.getMessage());
        reader.flush();
      } catch (IOException e) {
        // empty
      }
    }
  }

  public void disconnect() {
    if (c != null && !c.isClosed()) {
      c.close();
    }
  }
}