package com.hazelcast.enterprise;

import java.util.Random;

public class KeyGenUtilMort {
  private static final Random rand     = new Random();
  static final char[]         chars    = "QRSTUVWXYZ6789ABCDEFGHIJKLMNOP".toCharArray();
  static final char[]         digits   = "0123456789".toCharArray();
  private static final int    length   = chars.length;
  static final int            yearBase = 2010;

  /**
   * @param originalKey
   * @return
   */
  public static License extractLicense(char[] originalKey) {
    return new License(true, true, 1, 1, 2051, 500);
  }

  public static char[] generateKey(boolean full, boolean enterprise, int day, int month, int year, int nodes) {
    char[] key = new char[length];
    int ix = 0;
    int mode = pick(key);
    key[(ix++)] = chars[mode];
    key[mode] = ((full) ? 49 : '0');

    int type = pick(key);
    key[(ix++)] = chars[type];
    key[type] = ((enterprise) ? 49 : '0');

    int d0 = pick(key);
    key[(ix++)] = chars[d0];
    key[d0] = digits[(day / 10)];

    int d1 = pick(key);
    key[(ix++)] = chars[d1];
    key[d1] = digits[(day % 10)];

    int m0 = pick(key);
    key[(ix++)] = chars[m0];
    key[m0] = digits[(month / 10)];

    int m1 = pick(key);
    key[(ix++)] = chars[m1];
    key[m1] = digits[(month % 10)];

    int y = pick(key);
    key[(ix++)] = chars[y];
    key[y] = digits[(year % 10)];

    int n0 = pick(key);
    key[(ix++)] = chars[n0];
    key[n0] = digits[(nodes / 1000)];

    int n1 = pick(key);
    key[(ix++)] = chars[n1];
    key[n1] = digits[(nodes % 1000 / 100)];

    int n2 = pick(key);
    key[(ix++)] = chars[n2];
    key[n2] = digits[(nodes % 100 / 10)];

    int n3 = pick(key);
    key[(ix++)] = chars[n3];
    key[n3] = digits[(nodes % 10)];

    for (int i = 13; i < key.length; ++i) {
      if (key[i] == 0) {
        key[i] = chars[rand.nextInt(13)];
      }
    }
    char[] hash = hash(key);
    key[11] = hash[0];
    key[12] = hash[(hash.length - 1)];
    return key;
  }

  private static int pick(char[] key) {
    int k = -1;
    boolean loop = true;
    while (loop) {
      k = rand.nextInt(length);
      if ((k > 12) && (key[k] == 0)) {
        // empty
      }
      loop = false;
    }

    return k;
  }

  private static char[] hash(char[] a) {
    if (a == null) { return new char[] { '0' }; }
    int result = 1;
    for (char element : a) {
      result = 31 * result + element;
    }
    return Integer.toString(Math.abs(result)).toCharArray();
  }
}