/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog;

import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.ThreadContext;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.applicationlog.request.AbstractRequest;
import com.bosch.caltool.apic.ws.applicationlog.response.Response;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.application.log.client.ApplicationEvent;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.WSLogger;

/**
 * Singleton Enum for logging web server events to the application logger. In the messages.properties file, there should
 * be an entry CommonUtils.APPLICATION_EVENT_LOGGING = {DISABLED|ENABLED} to make the logging working. If this entry
 * doens't exists, logging is not active.
 *
 * @author imi2si
 * @since 1.19
 */
public enum ServerEventLogger {

                               /**
                                * The Singleton instance of the application logger
                                */
                               INSTANCE;

  /**
   * The application name which should be passed to the application logger
   */
  private static final String APPLICATION_NAME = "iCDM WebService";

  /**
   * Decides if logging is enabled on base of an entry in the messages.properties file
   */
  private static final String LOGGING_ENABLED = Messages.getString("CommonUtils.APPLICATION_EVENT_LOGGING");

  /**
   * The max length of a database column
   */
  private static final int MAX_DB_COLUMN_LENGTH = 4000;

  /**
   * The logger for this class
   */
  private ILoggerAdapter logger = WSObjectStore.getLogger();

  /**
   * The length of the parameter field to be logged
   */
  private int remarkLength = ServerEventLogger.MAX_DB_COLUMN_LENGTH;

  /**
   * Valid values for logging enabled/diasbled in the messages.properties file
   */
  private enum LoggingEnabled {
                               /**
                                * Value for 'Logging is enabled'
                                */
                               ENABLED,
                               /**
                                * Value for 'Logging is disabled'
                                */
                               DISABLED
  }

  /**
   * Private constructor of this class (because it is a singleton enum)
   */
  private ServerEventLogger() {
    this.logger = WSLogger.getInstance();
  }

  /**
   * Logs an application event. Any String can be passed. SHould be an event like starting X or getting X.
   *
   * @param applicationEvent the application event to log. can be the name of the web service method which has benn
   *          called by the user.
   */
  public void logServerEvent(final String applicationEvent) {
    this.logServerEvent(applicationEvent, "");
  }

  /**
   * Logs an application event. Any String can be passed. Should be an event like starting X or getting X.
   *
   * @param applicationEvent the application event to log. can be the name of the web service method which has benn
   *          called by the user.
   * @param requestObject the AbstractRequest which represents a String of the web service request.
   */
  public void logServerEvent(final String applicationEvent, final AbstractRequest requestObject) {
    this.logger.info(applicationEvent + " started with parameters: " + requestObject.toString());
    this.logServerEvent(applicationEvent, requestObject.toString());
  }

  public void logStartOfReq(final AbstractRequest requestObject) {
    String messageText = "started with parameters: " + requestObject;
    this.logger.info(getMdcMethod() + " " + messageText);
    this.logServerEvent(getMdcMethod(), "(" + getMdcId() + ") " + messageText);
  }

  public void logStartOfReq(final String methodName, final AbstractRequest requestObject) {
    ThreadContext.put("method", methodName);
    ThreadContext.put("requestId", UUID.randomUUID().toString());
    String messageText = "started with parameters: " + requestObject;
    this.logger.info(getMdcMethod() + " " + messageText);
    this.logServerEvent(getMdcMethod(), "(" + getMdcId() + ") " + messageText);
  }

  public void logEndOfReq(final int numOfRecords) {
    String messageText = "ended successfully. Number of Records returned: " + numOfRecords;
    this.logger.info(getMdcMethod() + " " + messageText);
    this.logServerEvent(getMdcMethod(), "(" + getMdcId() + ") " + messageText);
  }

  public void logEndOfReq(final Response responseObject) {
    String messageText = "ended successfully. Number of Records returned: " + responseObject;
    this.logger.info(getMdcMethod() + " " + messageText);
    this.logServerEvent(getMdcMethod(), "(" + getMdcId() + ") " + messageText);
  }

  public void logSession(final String sessionInfo) {
    this.logger.info("Using session " + sessionInfo);
    this.logServerEvent(getMdcMethod(), "(" + getMdcId() + ") Session-Info: " + sessionInfo);
  }

  public void logError(final Exception exp) {
    this.logger.error(getMdcMethod() + " ended with error: " + exp.getMessage(), exp);
    this.logServerEvent(getMdcMethod(), "(" + getMdcId() + ") " + "ended with error " + exp.toString());
  }

  public String getMdcMethod() {
    return Optional.ofNullable(ThreadContext.get("method")).orElse("No Method Defined");
  }

  public String getMdcId() {
    return Optional.ofNullable(ThreadContext.get("requestId")).orElse("Anonymous");
  }

  public void clearContext() {
    ThreadContext.clearAll();
  }

  /**
   * Logs an application event. Any String can be passed. Should be an event like starting X or getting X. As
   * application remark any String can additionally passed. This can be an information like a where clause.
   *
   * @param applicationEvent the application event to log. can be the name of the web service method which has benn
   *          called by the user.
   * @param applicationRemark the String remark that should be logged.
   */
  public void logServerEvent(final String applicationEvent, final String applicationRemark) {
    if (!isLoggingEnabled()) {
      return;
    }

    ApplicationEvent.logApplicationEvent(ServerEventLogger.APPLICATION_NAME, applicationEvent,
        getApplRemarkDb(applicationRemark));
  }

  /**
   * Returns if logging is enabled or disable based on the messages.properties entry for logging.
   *
   * @return true if logging is set to ENABLED, false if logging is set to disabled
   */
  public boolean isLoggingEnabled() {
    return ServerEventLogger.LOGGING_ENABLED.equalsIgnoreCase(LoggingEnabled.ENABLED.toString());
  }

  /**
   * Returns if logging is enabled or disable based on the messages.properties entry for logging.
   *
   * @param applicationRemark the remark to pass to the DB.
   * @return a String which consideres the max length defined by setAttributeLength()
   */
  private String getApplRemarkDb(final String applicationRemark) {
    if (applicationRemark.length() <= this.remarkLength) {
      return applicationRemark;
    }

    return applicationRemark.substring(0, this.remarkLength);
  }

  /**
   * @return the remarkLength
   */
  public int getRemarkLength() {
    return this.remarkLength;
  }

  /**
   * @param remarkLength the remarkLength to set
   */
  public void setRemarkLength(final int remarkLength) {
    this.remarkLength = remarkLength;
  }
}
