package hazelcast;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class TestHzInstance {
  protected static final Logger log = LoggerFactory.getLogger(TestHzInstance.class);
  boolean exit = false;
  
  @Test
  public void testHazelCast() {
    HazelcastInstance hz = Hazelcast.getDefaultInstance();
    IMap<String,Object> m = hz.getMap("default");
    m.put("test", "hurk");
    
    log.debug("Size of map: {}", m.size());
    
    new Thread() {
      @Override
      public void run() {
        while (true) {
          try {
            int i = System.in.read();
            if (i == -1) {
              exit = true;
              break;
            }
            String s = new String(new char[]{(char)i});
            log.debug("String: {}", s);
            if (s.matches("\r|\n|\r\n")) {
              exit = true;
              break;
            }
          } catch (IOException e) {
            // empty
          }
        }
      }
    }.start();
    
    while (!exit) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}