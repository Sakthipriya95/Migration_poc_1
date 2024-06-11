package com.bosch.calcomp.hexparser.model;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 unknown Frank Henze First draft.<br>
 */

/**
 * A HexBlock is just only an address range without data. The HexBlock is used only to define the address ranges in a
 * Hex file which do not have any gaps.
 * 
 * @author DS/SAE2-Henze
 * @version 1.0.0
 */
public class HexBlock {

  /**
   * startAddress
   */
  private final long startAddress;

  /**
   * endAddress
   */
  private final long endAddress;

  /**
   * Create a new HEX block
   * 
   * @param newStartAddress the start address
   * @param newLength       the length in byte
   */
  public HexBlock(long newStartAddress, long newLength) {
    // start Address is address of the first byte
    startAddress = newStartAddress;
    // end Address is address of the last byte
    endAddress = startAddress + newLength - 1;
  }

  /**
   * Get the start address of a HEX block
   * 
   * @return the start address
   */
  public long getStartAddress() {
    return startAddress;
  }

  /**
   * Get the start address of a HEX block in hexadecimal
   * 
   * @return the start address
   */
  public String getHexStartAddress() {
    return Long.toHexString(getStartAddress());
  }

  /**
   * Get the end address of a HEX block
   * 
   * @return the end address
   */
  public long getEndAddress() {
    return endAddress;
  }

  /**
   * Get the end address of a HEX block in hexadecimal
   * 
   * @return hex end address
   */
  public String getHexEndAddress() {
    return Long.toHexString(getEndAddress());
  }

  /**
   * Get the size of the HEX block in byte
   * 
   * @return the size
   */
  public long getSize() {
    return endAddress - startAddress + 1;
  }

  /**
   * Get the hexadecimal size of the HEX block in byte
   * 
   * @return Hex size
   */
  public String getHexSize() {
    return Long.toHexString(getSize());
  }

}
