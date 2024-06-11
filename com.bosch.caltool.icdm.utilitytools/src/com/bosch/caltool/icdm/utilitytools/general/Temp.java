/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general;

import java.util.Arrays;

/**
 * @author bne4cob
 */
public class Temp {

  public static void main(final String args[]) {
    hexStringToByteArray("425a");// Bzip
    hexStringToByteArray("1f9d");// Compress
    hexStringToByteArray("1f8b");// gzip
    hexStringToByteArray("504b0304");// pkzip

  }

  public static byte[] hexStringToByteArray(final String s) {
    System.out.println("Input\t: " + s);

    byte[] b = new byte[s.length() / 2];
    for (int i = 0; i < b.length; i++) {
      int index = i * 2;
      int v = Integer.parseInt(s.substring(index, index + 2), 16);
      b[i] = (byte) v;
    }
    System.out.println("Output\t: " + Arrays.toString(b));
    return b;
  }

}
