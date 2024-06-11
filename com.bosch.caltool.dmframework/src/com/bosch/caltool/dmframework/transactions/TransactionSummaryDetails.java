/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.transactions;

import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * Stores the list of changes in the current session
 *
 * @author dmo5cob
 */
public class TransactionSummaryDetails implements Comparable<TransactionSummaryDetails> {

  /**
   * Item which is modified (value, used flag...)
   */
  private String modifiedItem;
  /**
   * Old value, before modification
   */
  private String oldValue;
  /**
   * New value, after modification
   */
  private String newValue;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the modifiedItem
   */
  public String getModifiedItem() {
    return this.modifiedItem;
  }

  /**
   * @param modifiedItem the modifiedItem to set
   */
  public void setModifiedItem(final String modifiedItem) {
    this.modifiedItem = modifiedItem;
  }

  /**
   * @return the oldValue
   */
  public String getOldValue() {
    return this.oldValue;
  }

  /**
   * @param oldValue the oldValue to set
   */
  public void setOldValue(final String oldValue) {
    this.oldValue = oldValue;
  }

  /**
   * @return the newValue
   */
  public String getNewValue() {
    return this.newValue;
  }

  /**
   * @param newValue the newValue to set
   */
  public void setNewValue(final String newValue) {
    this.newValue = newValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final TransactionSummaryDetails arg0) {
    // default sort by modified item name
    return getModifiedItem().compareTo(arg0.getModifiedItem());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((this.modifiedItem == null) ? 0 : this.modifiedItem.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TransactionSummaryDetails other = (TransactionSummaryDetails) obj;
    return CommonUtils.isEqual(getModifiedItem(), other.getModifiedItem());
  }
}
