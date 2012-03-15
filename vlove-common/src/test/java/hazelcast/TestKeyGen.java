package hazelcast;

import org.junit.Test;

import com.hazelcast.enterprise.KeyGenUtil;
import com.hazelcast.enterprise.KeyGenUtilMort;

public class TestKeyGen {
  @Test
  public void testKeyGen() {
    String key = new String(KeyGenUtilMort.generateKey(true, true, 1, 1, 15, 50));
    System.out.println("Key: " + key);
    
    System.out.println(KeyGenUtil.generateKey(true, true, 1, 1, 2015, 10));
  }
}