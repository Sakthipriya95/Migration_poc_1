/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.session;

import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;


/**
 * @author imi2si
 */
public class Session {

  private final static Double PERCENTAGE_MIN = 0D;
  private final static Double PERCENTAGE_MAX = 100D;

  public static enum STATUS {
                             ACTIVE,
                             FINISHED,
                             CANCELLED
  }

  private final String sessionId;
  private final String userName;
  private final Date startDate;
  private String operation;
  private String operationParameters;

  private final TimeZone timezone;

  private Double percentageFinished = new Double(Session.PERCENTAGE_MIN);
  private STATUS status = STATUS.ACTIVE;
  private Date endDate = null;
  private boolean cancelRequested = false;
  /**
   * passWord in session
   */
  private final String passWord;


  /**
   * @return the passWord
   */
  public String getPassWord() {
    return this.passWord;
  }

  public Session(final String userName, final String operation, final String operationParameters, final String timezone,
      final String passWord) {
    this.sessionId = UUID.randomUUID().toString();
    this.userName = userName;
    this.passWord = passWord;
    this.startDate = new Date();
    this.operation = operation;
    this.operationParameters = operationParameters;
    this.timezone = TimeZone.getTimeZone(timezone);
  }

  public Session(final String userName, final String operation, final String operationParameters) {
    this(userName, operation, operationParameters, "GMT+00:00", null);
  }

  public void setSessionFinished() {
    this.endDate = new Date();
    this.status = STATUS.FINISHED;
    this.percentageFinished = Session.PERCENTAGE_MAX;
  }

  public void setSessionCancelled() {
    this.endDate = new Date();
    this.status = STATUS.CANCELLED;
  }

  /**
   * @param percentageFinished the percentageFinished to set
   */
  public void setPercentageFinished(final Double percentageFinished) {
    if (!this.status.toString().equalsIgnoreCase(STATUS.ACTIVE.toString())) {
      throw new IllegalArgumentException("Status of Session is not active, thus progress can't be modified.");
    }

    this.percentageFinished = percentageFinished;
  }

  public boolean isActive() {
    return this.status == STATUS.ACTIVE;
  }

  /**
   * @param cancelRequested the cancelRequested to set
   */
  public void setCancelRequested(final boolean cancelRequested) {
    this.cancelRequested = cancelRequested;
  }

  /**
   * @param operation the operation to set
   */
  public void setOperation(final String operation) {
    this.operation = operation;
  }


  /**
   * @param operationParameters the operationParameters to set
   */
  public void setOperationParameters(final String operationParameters) {
    this.operationParameters = operationParameters;
  }

  /**
   * @return the cancelRequested
   */
  public boolean isCancelRequested() {
    return this.cancelRequested;
  }

  /**
   * @return the sessionId
   */
  public String getSessionId() {
    return this.sessionId;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @return the startDate
   */
  public Date getStartDate() {
    return this.startDate != null ? (Date) this.startDate.clone() : null;
  }

  /**
   * @return the operation
   */
  public String getOperation() {
    return this.operation;
  }

  /**
   * @return the operationParameters
   */
  public String getOperationParameters() {
    return this.operationParameters;
  }

  /**
   * @return the percentageFinished
   */
  public Double getPercentageFinished() {
    return this.percentageFinished;
  }

  /**
   * @return the status
   */
  public STATUS getStatus() {
    return this.status;
  }

  /**
   * @return the endDate
   */
  public Date getEndDate() {
    return this.endDate != null ? (Date) this.endDate.clone() : null;
  }

  /**
   * @return the timezone
   */
  public TimeZone getTimezone() {
    return this.timezone;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Session [sessionId=" + this.sessionId + ", userName=" + this.userName + ", timezone=" + this.timezone +
        ", passWord=" + this.passWord + "]";
  }
}
