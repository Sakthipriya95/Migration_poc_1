package com.bosch.calcomp.hexparser.model;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 unknown Frank Henze First draft.<br>
 */

/**
 * A HexSegment is a block of data with a maximum size of 64kB.
 * 
 * @author DS/SAE2-Henze
 */
public class HexSegment extends HexBlock {

  private final short[] data;

  /**
   * Create a new data segment
   * 
   * @param newData         the data byte array
   * @param newStartAddress the start address of the segment
   * @param newLength       the length in byte
   */
  public HexSegment(short[] newData, long newStartAddress, long newLength) {
    super(newStartAddress, newLength);
    data = newData;
  }

  /**
   * Get one byte at the given offset from the start address
   * 
   * @param offset
   * @return the byte
   */
  public short getByteAt(int offset) {
    return data[offset];
  }

}