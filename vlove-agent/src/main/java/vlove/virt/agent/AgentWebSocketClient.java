package vlove.virt.agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import jline.console.ConsoleReader;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.model.json.AgentConnectionMessage;
import vlove.model.json.AgentDisconnectionMessage;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class AgentWebSocketClient implements AgentSocketCallback {
  protected static final Logger log = LoggerFactory.getLogger(AgentWebSocketClient.class);

  private AsyncHttpClient c;
  private final ObjectMapper om = new ObjectMapper();
  private WebSocket websocket;

  private final ConsoleReader reader;

  private boolean wantsToClose = false;
  private String uuid = null;

  public AgentWebSocketClient(ConsoleReader reader) {
    this.reader = reader;
  }

  public void connect() {
    try {
      if (c != null && !c.isClosed()) {
        disconnect();
      }
      
      c = new AsyncHttpClient();
      c.prepareGet("ws://localhost:8080/vlove/s/agent").execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new AgentWebSocketListener(this, reader)).build()).get();
    } catch (Exception ex) {
      try {
        reader.println("Could not establish WebSocketConnection.  " + ex.getMessage());
        reader.flush();
      } catch (IOException e) {
        // ignore this
      }
    }
  }

  public void disconnect() {
    wantsToClose = true;
    if (c != null && !c.isClosed()) {
      if (websocket != null && websocket.isOpen()) {
        try {
          websocket.sendTextMessage(om.writeValueAsString(new AgentDisconnectionMessage(System.currentTimeMillis(), uuid)));
        } catch (Exception ex) {
          log.error("Could not write disconnection message to server.", ex);
        }
      }
      c.close();
    }
    uuid = null;
  }

  @Override
  public void onClose() {
    if (!wantsToClose) {
      try {
        reader.println("Connection to server closed - reconnecting.");
      } catch (IOException ie) {
        // ignore this
      }
      connect();
    }
  }

  @Override
  public void onError() {
    // FIXME - we really need to track the error and see if we should reconnect

    try {
      reader.println("Error in server communication - reconnecting.");
    } catch (IOException ie) {
      // ignore this
    }
    connect();
  }

  @Override
  public void onOpen(WebSocket websocket) {
    this.websocket = websocket;

    if (uuid == null) {
      try {
        // We haven't connected before, so let's let the server know
        reader.println("Connecting to remote server to let them know this agent has fired up.");
        uuid = UUID.randomUUID().toString();

        List<String> ipAddresses = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
          final NetworkInterface iface = networkInterfaces.nextElement();
          Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
          while (inetAddresses.hasMoreElements()) {
            final InetAddress inetAddress = inetAddresses.nextElement();
            ipAddresses.add(inetAddress.getHostAddress());
          }
        }

        websocket.sendTextMessage(om.writeValueAsString(new AgentConnectionMessage(ipAddresses, System.currentTimeMillis(), uuid)));
      } catch (Exception ex) {
        log.error("Could not notify server of our connection, shutting down.");
      }
    }
  }
}