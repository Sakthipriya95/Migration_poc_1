/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.ArrayList;
import java.util.List;


/**
 * @author bru2cob
 */
public class BitValue {

  /**
   * Total number of bits
   */
  public static final int NO_OF_BITS = 32;

  /**
   * refers the type of bit (set/notset/undef)
   */
  private final String bitType;

  /**
   * List of bits set for the corresponding bit type
   */
  private final List<Integer> bitsList = new ArrayList<>();


  /**
   * @param bitType type of bit
   */
  public BitValue(final String bitType) {
    super();
    this.bitType = bitType;
  }


  /**
   * @return the bitsIndexList
   */
  public List<Integer> getBitsList() {
    return this.bitsList;
  }

  /**
   * @param selIndex columber number of the bit / bit index
   * @return column number of the table corresponding to bit position / bit position corresponding to given col number
   */
  public int getColNumOrBitIndex(final int selIndex) {
    return NO_OF_BITS - selIndex;
  }


  /**
   * @return the bitType
   */
  public String getBitType() {
    return this.bitType;
  }

  /**
   * Add the bit to the list
   *
   * @param bitIndex bitIndex
   */
  public void addBit(final int bitIndex) {
    this.bitsList.add(bitIndex);
  }

  /**
   * Remove the bit from the list
   *
   * @param bitIndex bitIndex
   */
  public void removeBit(final int bitIndex) {
    this.bitsList.remove(this.bitsList.indexOf(bitIndex));
  }
}
