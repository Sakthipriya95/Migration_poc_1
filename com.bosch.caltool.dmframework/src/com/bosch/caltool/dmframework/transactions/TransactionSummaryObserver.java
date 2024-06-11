/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.transactions;


/**
 * This interface is used to add statements of changes (commands) in one session to the transaction summary view part
 * 
 * @author dmo5cob
 */
public interface TransactionSummaryObserver {

  /**
   * @param summaryData : details
   */
  void refreshView(TransactionSummary summaryData);

}
