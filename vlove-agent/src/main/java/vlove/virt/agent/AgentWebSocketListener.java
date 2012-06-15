package vlove.virt.agent;

import java.io.IOException;

import jline.console.ConsoleReader;

import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;

public class AgentWebSocketListener implements WebSocketTextListener {
  private final AgentSocketCallback callback;
  private final ConsoleReader reader;
  private final AgentWebSocketMessageHandler handler = new AgentWebSocketMessageHandler();
  
  public AgentWebSocketListener(AgentSocketCallback callback, ConsoleReader reader) {
    this.callback = callback;
    this.reader = reader;
  }

  @Override
  public void onOpen(WebSocket websocket) {
    try {
      reader.println("WebSocket connection established.");
      reader.flush();
      callback.onOpen(websocket);
      handler.setWebSocket(websocket);
    } catch (IOException e) {
      // empty
    }
  }

  @Override
  public void onClose(WebSocket websocket) {
    try {
      reader.println("WebSocket connection terminated.");
      reader.flush();
      callback.onClose();
    } catch (IOException e) {
      // empty
    }
  }

  @Override
  public void onError(Throwable t) {
    try {
      reader.println("Error occured in WebSocket communication.  "
          + t.getMessage());
      reader.flush();
      callback.onError();
    } catch (IOException e) {
      // empty
    }
  }

  @Override
  public void onMessage(String message) {
    try {
      reader.println("Message from server: " + message);
      reader.flush();
      handler.onMessage(message);
    } catch (IOException e) {
      // empty
    }
  }

  @Override
  public void onFragment(String fragment, boolean last) {
    // empty
  }
}