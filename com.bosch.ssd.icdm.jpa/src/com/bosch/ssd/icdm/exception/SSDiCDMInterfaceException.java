/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.exception;

import java.util.StringJoiner;

import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;

/**
 * Class that handles all the exception in the FileEditor Module
 *
 * @author SSN9COB
 */
public class SSDiCDMInterfaceException extends Exception {


  /**
   * Defaule Serial version UID
   */
  private static final long serialVersionUID = 1L;
  private final SSDiCDMInterfaceErrorCodes errorCode;
  private final String detailedErrorMessage;

  /**
   * Enum that contains the Error Codes & General Messages
   *
   * @author SSN9COB
   */
  public enum SSDiCDMInterfaceErrorCodes {
                                          /**
                                           * All exceptions related to Java code
                                           */
                                          GENERAL_EXCEPTION(1, SSDiCDMInterfaceConstants.EXCEPTION_GENERAL),
                                          /**
                                           * All exceptions related to Database & db queries
                                           */
                                          DATABASE_EXCEPTION(2, SSDiCDMInterfaceConstants.EXCEPTION_DATABASE),
                                          /**
                                           * All exceptions related to invalid Inputs
                                           */
                                          INVALID_INPUT_EXCEPTION(2, SSDiCDMInterfaceConstants.EXCEPTION_INVALID_INPUT);

    private int errCode;
    private String errMsg;

    private SSDiCDMInterfaceErrorCodes(final int errCode, final String errMsg) {
      this.errCode = errCode;
      this.errMsg = errMsg;
    }

    /**
     * Return the Error Prefix for Logger
     *
     * @return String
     */
    public String getErrorPrefix() {
      return new StringJoiner(SSDiCDMInterfaceConstants.DELIMITER_HIFEN,
          SSDiCDMInterfaceConstants.EXCEPTION_ERROR_CODE + this.errCode, this.errMsg).toString();
    }
  }

  /**
   * Create a new SSDiCDMInterfaceException with error code, message
   *
   * @param errorCode custom Errorcode
   * @param message message
   */
  protected SSDiCDMInterfaceException(final SSDiCDMInterfaceErrorCodes errorCode, final String message) {
    super(message);
    this.errorCode = errorCode;
    this.detailedErrorMessage = message;
  }


  /**
   * Get the error code of the exception.
   *
   * @return the error code
   */
  public final SSDiCDMInterfaceErrorCodes getErrorCode() {
    return this.errorCode;
  }

  /**
   * Return Detailed Error Message with Error Code, Error Type & Actual Error Message
   *
   * @return the detailedErrorMessage
   */
  public String getDetailedErrorMessage() {
    StringJoiner errorMsg = new StringJoiner(SSDiCDMInterfaceConstants.DELIMITER_HIFEN);
    errorMsg.add(getErrorCode().getErrorPrefix());
    errorMsg.add(this.detailedErrorMessage);
    return errorMsg.toString();
  }

  /**
   * @param cause the cause to set
   */
  public void setErrorCause(final Throwable cause) {
    initCause(cause);
  }
}
