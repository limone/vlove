package vlove.virt.agent;

import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    System.setProperty("jna.nosys", "true");
    try {
      Terminal t = TerminalFactory.get();
      t.init();
      
      ConsoleReader reader = new ConsoleReader("vlove", System.in, System.out, t);
      reader.setPrompt("vlove> ");
      
      AgentWebSocketClient client = new AgentWebSocketClient(reader);
      client.connect();

      String line;
      // PrintWriter out = new PrintWriter(reader.getOutput());

      while ((line = reader.readLine()) != null) {
        // If we input the special word then we will mask
        // the next line.
        if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
          client.disconnect();
          break;
        } else if (line.equals("reconnect")) {
          client.disconnect();
          client.connect();
        }
      }
    } catch (Exception ex) {
      log.warn("Problem with jline.", ex);
      System.exit(-1);
    }
  }
}