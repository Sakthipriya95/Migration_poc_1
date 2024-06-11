/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.transactions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Holds the list of DB changes in the current user session
 *
 * @author dmo5cob
 */
public class TransactionSummary implements Comparable<TransactionSummary> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   * A static variable to generate IDs
   */
  private static long idCounter;

  /**
   * Unique id of this object
   */
  private final long summaryID;
  /**
   * Modification details
   */
  private SortedSet<TransactionSummaryDetails> trnDetails = new TreeSet<TransactionSummaryDetails>();

  /**
   * Child set of transactions
   */
  private final SortedSet<TransactionSummary> childSummarySet = new TreeSet<TransactionSummary>();

  /**
   * Timestamp when this operation is performed
   */
  private final Calendar performedOn;

  /**
   * Holds the reference for the command for this summary (useful for undo)
   */
  private final AbstractDataCommand cmdReference;

  /**
   * Holds the db operation
   */
  private String operationType;
  /**
   * Object type
   */
  private String objectType;
  /**
   * Object name
   */
  private String objectName;

  /**
   * Constructor
   *
   * @param dataCommand , the main command for the summary is created
   */
  public TransactionSummary(final AbstractDataCommand dataCommand) {
    this.summaryID = idCounter;
    idCounter++;
    this.cmdReference = dataCommand;
    this.performedOn = new GregorianCalendar();
  }

  /**
   * @return the objectType
   */
  public String getObjectType() {
    return this.objectType;
  }

  /**
   * @param objType object type
   */
  public void setObjectType(final String objType) {
    this.objectType = objType;
  }

  /**
   * @return the objectName
   */
  public String getObjectName() {
    return this.objectName;
  }

  /**
   * @param objName object name
   */
  public void setObjectName(final String objName) {
    this.objectName = objName;
  }


  /**
   * @return the operation
   */
  public String getOperation() {
    return this.operationType;
  }

  /**
   * Explicitly set the Command mode string ( especially for UN-DELETE)
   *
   * @param commandMode String representation of command mode
   */
  public void setOperation(final String commandMode) {
    this.operationType = commandMode;
  }

  /**
   * @return the performedOn time
   */
  public Date getPerformedOn() {
    return this.performedOn.getTime();
  }

  /**
   * @return the trnDetails
   */
  public SortedSet<TransactionSummaryDetails> getTrnDetails() {
    return new TreeSet<>(this.trnDetails);
  }


  /**
   * @param trsnDetails the trnDetails to set
   */
  public void setTrnDetails(final SortedSet<TransactionSummaryDetails> trsnDetails) {
    this.trnDetails = trsnDetails;
  }


  /**
   * @return the listTrsnAttrs
   */
  public SortedSet<TransactionSummary> getSummaryList() {
    return this.childSummarySet;
  }

  /**
   * @return the cmdReference
   */
  public AbstractDataCommand getCommand() {
    return this.cmdReference;
  }

  /**
   * @return true, if details object is only one
   */
  private boolean hasOneChild() {
    return (this.trnDetails != null) && (this.trnDetails.size() == 1);
  }

  /**
   * @return true, if more than one details found
   */
  public boolean canExpandRow() {
    return (this.trnDetails != null) && (this.trnDetails.size() > 1);
  }

  /**
   * In case of one details child, get the modified item
   *
   * @return modified item of the first child
   */
  public String getModifiedItem() {
    if (hasOneChild()) {
      return this.trnDetails.first().getModifiedItem();
    }
    return null;
  }

  /**
   * In case of one details child, get the old value
   *
   * @return the oldValue
   */
  public String getOldValue() {
    if (hasOneChild()) {
      return this.trnDetails.first().getOldValue();
    }
    return null;
  }

  /**
   * In case of one details child, get the new value
   *
   * @return the newValue
   */
  public String getNewValue() {
    if (hasOneChild()) {
      return this.trnDetails.first().getNewValue();
    }
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final TransactionSummary arg0) {
    int compareResult = ApicUtil.compareCalendar(arg0.performedOn, this.performedOn);
    if (compareResult == 0) {
      compareResult = ApicUtil.compareLong(this.summaryID, arg0.summaryID);
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + (int) (this.summaryID ^ (this.summaryID >>> 32));
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
    TransactionSummary other = (TransactionSummary) obj;
    return CommonUtils.isEqual(this.summaryID, other.summaryID);
  }
}
