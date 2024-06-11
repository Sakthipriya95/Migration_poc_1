/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.exception;

import java.util.Objects;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.common.utility.SSDEntityManagerUtil;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.SSDConfigEnums.TransactionState;

/**
 * Util class that contains Exception invoking methods
 *
 * @author SSN9COB
 */
public final class ExceptionUtils {

  private ExceptionUtils() {
    // default constructor
  }

  /**
   * Throw Custom Exception
   *
   * @param e exception if available (OPTIONAL)
   * @param message custom / error localized message message (MANDATORY)
   * @param errorCode ErrorCode (MANDATORY)
   * @param isCreate isCreate Exception or throw existing
   * isCreate - boolean variable created because same error cause occuring in different methods. To avoid the same log message, this boolean variable is created
   * @return CustomException
   */
  public static SSDiCDMInterfaceException createAndThrowException(final Exception e, final String message,
      final SSDiCDMInterfaceErrorCodes errorCode, final boolean isCreate) {
    if (isCreate) {
    SSDiCDMInterfaceException exception = new SSDiCDMInterfaceException(errorCode, message);
    
    if (Objects.nonNull(e)) {
      exception.setErrorCause(e);
      
    }
    SSDiCDMInterfaceLogger.logMessage(exception.getDetailedErrorMessage(), ILoggerAdapter.LEVEL_ERROR, null);
    return exception;
  }
    return (SSDiCDMInterfaceException) e;
  }
  /**
   * rollback the transaction & Throw Custom Exception
   *
   * @param e exception if available (OPTIONAL)
   * @param message custom / error localized message message (MANDATORY)
   * @param errorCode ErrorCode (MANDATORY)
   * @param entityManager (MANDATORY)
   * @param isCreate isCreate Exception or throw existing
   * isCreate - boolean variable created because same error cause occuring in different methods. To avoid the same log message, this boolean variable is created
   * @return CustomException
   */
  public static SSDiCDMInterfaceException rollbackAndThrowException(final Exception e, final String message,
      final SSDiCDMInterfaceErrorCodes errorCode, final EntityManager entityManager, final boolean isCreate) {
    if (Objects.nonNull(entityManager) && (errorCode == SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION)) {
      // invoke rollback if there is any database expection
      SSDEntityManagerUtil.handleTransaction(TransactionState.TRANSACTION_ROLLBACK, entityManager);
    }
    return isCreate ? createAndThrowException(e, message, errorCode, true) : (SSDiCDMInterfaceException) e;
  }
}
