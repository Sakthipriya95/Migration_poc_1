/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author bru2cob
 */
public class BitwiseLimitConfig {

  /**
   * X denotes undefined bit
   */
  private static final String UNDEF_BIT = "X";
  /**
   * 0 denotes not set bit
   */
  private static final String NOTSET_BIT = "0";
  /**
   * 1 denotes set bit
   */
  private static final String SET_BIT = "1";
  /**
   * List of bit types to be used for table input
   */
  List<BitValue> bitTypes = new ArrayList<>();
  /**
   * instance for set bit
   */
  private BitValue setBit;
  /**
   * instance for not set bit
   */
  private BitValue notSetBit;
  /**
   * instance for undef bit
   */
  private BitValue undefBit;
  /**
   * Selected cdr rule
   */
  private final ReviewRule cdrRule;

  /**
   * @return the bitTypes
   */
  public List<BitValue> getBitTypes() {
    return this.bitTypes;
  }


  /**
   * @param cdrRule cdrRule
   */
  public BitwiseLimitConfig(final ReviewRule cdrRule) {
    this.cdrRule = cdrRule;
  }


  /**
   * Create the bitvalues to display in the pop up dialog
   */
  public void createBits() {
    // create the set bit instance
    this.setBit = new BitValue("Set");
    // create the not set bit instance
    this.notSetBit = new BitValue("Not Set");
    // create the undef bit instance
    this.undefBit = new BitValue("Undef");

    if ((null != this.cdrRule) && (null != this.cdrRule.getBitWiseRule()) && !this.cdrRule.getBitWiseRule().isEmpty()) {
      char[] bitValues = this.cdrRule.getBitWiseRule().replace(" ", "").toCharArray();
      int bitIndex = BitValue.NO_OF_BITS - 1;
      for (int index = 0; index <= (BitValue.NO_OF_BITS - 1); index++) {
        // add the bits
        if (bitValues[index] == '1') {
          this.setBit.addBit(bitIndex);
        }
        else if (bitValues[index] == '0') {
          this.notSetBit.addBit(bitIndex);
        }
        else if (bitValues[index] == 'X') {
          this.undefBit.addBit(bitIndex);
        }
        bitIndex--;
      }
    }
    // add the bits to the bit list
    this.bitTypes.add(this.setBit);
    this.bitTypes.add(this.notSetBit);
    this.bitTypes.add(this.undefBit);

  }

  /**
   * reset bitwise dialog
   */
  public void resetBitInput() {
    // create the set bit instance
    this.setBit = new BitValue("Set");
    // create the not set bit instance
    this.notSetBit = new BitValue("Not Set");
    // create the undef bit instance
    this.undefBit = new BitValue("Undef");
    // add the bits to the bit list
    this.bitTypes.add(this.setBit);
    this.bitTypes.add(this.notSetBit);
    this.bitTypes.add(this.undefBit);

  }

  /**
   * Get the bitvalues display text
   *
   * @return bit values string
   */
  public String getBitValueDisplayString() {
    StringBuilder bitValue = new StringBuilder();
    int bitSpace = 0;
    for (int index = BitValue.NO_OF_BITS - 1; index >= 0; index--) {
      bitSpace++;
      if (this.setBit.getBitsList().contains(index)) {
        bitValue.append(SET_BIT);
      }
      else if (this.notSetBit.getBitsList().contains(index)) {
        bitValue.append(NOTSET_BIT);
      }
      else {
        bitValue.append(UNDEF_BIT);
      }
      if (bitSpace == 4) {
        bitValue.append(" ");
        bitSpace = 0;
      }
    }
    return bitValue.toString();

  }
}
