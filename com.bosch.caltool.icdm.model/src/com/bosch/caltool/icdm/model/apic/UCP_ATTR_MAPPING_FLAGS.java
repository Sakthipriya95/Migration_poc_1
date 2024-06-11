/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import java.util.BitSet;

/**
 * Flag for Quotation Relevant UCP Attr
 *
 * @author pdh2cob
 */
public enum UCP_ATTR_MAPPING_FLAGS {
                                    /**
                                     * Quotation Relevant
                                     */
                                    QUOTATION_RELEVANT(0);

  private final int index;

  UCP_ATTR_MAPPING_FLAGS(final int bitIndex) {
    this.index = bitIndex;
  }

  /**
   * Sets this flag to the given input
   *
   * @param input input
   * @return modified input after flag is set
   */
  public Long setFlag(final Long input) {
    BitSet bitSet = BitSet.valueOf(new long[] { input });
    bitSet.set(this.index);
    long[] bitArray = bitSet.toLongArray();
    if (bitArray.length == 0) {
      return 0L;
    }
    return bitArray[0];
  }

  /**
   * Sets this flag to the given input
   *
   * @param input input
   * @return modified input after flag is set
   */
  public Long removeFlag(final Long input) {
    BitSet bitSet = BitSet.valueOf(new long[] { input });
    bitSet.clear(this.index);
    long[] bitArray = bitSet.toLongArray();
    if (bitArray.length == 0) {
      return 0L;
    }
    return bitArray[0];
  }

  /**
   * Checks whether this flag is set in the input
   *
   * @param input input
   * @return true/false
   */
  public boolean isSet(final Long input) {
    BitSet bitSet = BitSet.valueOf(new long[] { input });
    return bitSet.get(this.index);
  }
}