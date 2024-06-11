/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.utility;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.common.utility.SSDEntityManagerUtil;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.SSDConfigEnums.TransactionState;

/**
 * @author SSN9COB
 */
public class ServiceLogAndTransactionUtil {

  private ServiceLogAndTransactionUtil() {
    // hidden constructor
  }

  /**
   * Log the entry or exit of every service methods Begin or close transaction at end of every service methods
   *
   * @param className classNameWhereMethodisAvailable
   * @param methodName nameOfMethodToLog
   * @param isEntry isEntryPoint or exit of the method
   * @param em EntityManager
   * @param isRollback isRollback at method exit
   */
  public static void handleLoggingAndTransaction(final String className, final String methodName, final boolean isEntry,
      final EntityManager em, final boolean isRollback) {
    if (isEntry) {
      logEntryAndExitOfMethods(isEntry, className, methodName, null);
      // Begin new transaction after validations
      SSDEntityManagerUtil.handleTransaction(TransactionState.TRANSACTION_BEGIN, em);
    }
    else {
      // Commit / Rollback transaction at the service end
      SSDEntityManagerUtil.handleTransaction(
          isRollback ? TransactionState.TRANSACTION_ROLLBACK : TransactionState.TRANSACTION_COMMIT, em);
      logEntryAndExitOfMethods(isEntry, className, methodName, null);
    }
  }


  /**
   * To Log at DEBUG LEVEL for each method and exit - this would be useful when analysing performance issues
   *
   * @param isEntryPoint IsMethod ENtry or Exit
   * @param className Class Name
   * @param methodName Method Name
   * @param customMessage Any Custom Message
   */
  public static void logEntryAndExitOfMethods(final boolean isEntryPoint, final String className,
      final String methodName, final String customMessage) {
    // Get log message prefix as entry or exit
    String logPrefix = isEntryPoint ? "Method Entry: " : "Method Exit: ";
    StringBuilder logMsg = new StringBuilder(logPrefix);
    // append the method name to the logMsg
    logMsg.append(methodName);
    logMsg.append(" in ");
    // append the class name
    logMsg.append(className);
    // if any custom message available, append it
    if (Objects.nonNull(customMessage)) {
      logMsg.append(" - ");
      logMsg.append(customMessage);
    }
    // Log the constructed message as DEBUG
    SSDiCDMInterfaceLogger.logMessage(logMsg.toString(), ILoggerAdapter.LEVEL_DEBUG, null);
  }

  /**
   * To Log at DEBUG LEVEL Time Taken for each query - this would be useful when analysing performance issues
   *
   * @param dbNamedQueryName queryName with method name(optional)
   * @param customMessage Any Custom Message
   * @param milliSeconds time taken for the query to complete
   */
  public static void logTimeTakenForEachDBQuery(final String dbNamedQueryName, final String customMessage,
      final long milliSeconds) {
    StringBuilder logMsg = new StringBuilder(200);
    // Append the query Name
    logMsg.append("Query Name: ");
    logMsg.append(dbNamedQueryName);
    // if any custom message available, append it
    if (Objects.nonNull(customMessage)) {
      logMsg.append(" - ");
      logMsg.append(customMessage);
    }
    // Append Time taken in ms, s & m
    logMsg.append(". Time Taken for the Query (in milliseconds) - " + milliSeconds + "; (in seconds) - " +
        TimeUnit.MILLISECONDS.toSeconds(milliSeconds) + "; (in Minutes) - " +
        TimeUnit.MILLISECONDS.toMinutes(milliSeconds));
    // Log the constructed message
    SSDiCDMInterfaceLogger.logMessage(logMsg.toString(), ILoggerAdapter.LEVEL_DEBUG, null);
  }
}
