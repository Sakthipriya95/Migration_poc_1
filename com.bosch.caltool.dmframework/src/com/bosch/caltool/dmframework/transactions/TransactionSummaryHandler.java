/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.transactions;

import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author dmo5cob
 */
public class TransactionSummaryHandler {

  /**
   * Summary set of all DB transactions for this current user session
   */
  private final SortedSet<TransactionSummary> summarySet;

  /**
   * Observer tells where refresh is required
   */
  private TransactionSummaryObserver observer;


  /**
   * Constructor
   */
  public TransactionSummaryHandler() {
    this.summarySet = new TreeSet<TransactionSummary>();
  }

  /**
   * @param summaryData summary details
   */
  public void addToTransactionSummary(final TransactionSummary summaryData) {
    this.summarySet.add(summaryData);
    if (null != this.observer) {
      this.observer.refreshView(summaryData);
    }
  }


  /**
   * @return the summarySet
   */
  public SortedSet<TransactionSummary> getSummarySet() {
    return new TreeSet<>(this.summarySet);
  }


  /**
   * @param observer the observer to set
   */
  public void setObserver(final TransactionSummaryObserver observer) {
    this.observer = observer;
  }


}
