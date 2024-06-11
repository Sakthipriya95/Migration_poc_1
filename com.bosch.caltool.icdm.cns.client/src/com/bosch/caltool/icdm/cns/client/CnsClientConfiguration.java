/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.client.utils.Utils;

/**
 * @author bne4cob
 */
public class CnsClientConfiguration {

  private static CnsClientConfiguration config;

  private String baseUrl;

  private String user;

  private String password;

  private String sessionId;

  /**
   * Port number of data producer. Applicable only if the current node is producer
   */
  private Integer producerPort;

  private ILoggerAdapter logger;

  /**
   * Timezone offset in minutes
   */
  private int timezoneOffset;

  /**
   * @return default client configuration
   * @throws IllegalStateException if CNS client configuration is not initialized yet
   */
  public static final CnsClientConfiguration getDefaultConfig() {
    if (config == null) {
      throw new IllegalStateException("CNS client configuration is not initialized yet");
    }
    return config;
  }

  /**
   * @param config1 CnsClientConfiguration
   * @throws CnsServiceClientException error during initialization
   */
  public static final void initialize(final CnsClientConfiguration config1) throws CnsServiceClientException {
    config = new CnsClientConfiguration();
    copyProperties(config1, config);

    validateProps();

    config.logger.info("CNS Server connection details initialized. URL - {}", config.baseUrl);
  }

  /**
   * @throws CnsServiceClientException error during validation
   */
  private static void validateProps() throws CnsServiceClientException {
    if (Utils.isNullOrEmpty(config.baseUrl)) {
      throw new CnsServiceClientException("1000", "CNS Server URL not provided");
    }
    if (config.logger == null) {
      throw new CnsServiceClientException("1001", "Logger not provided");
    }

  }

  private static void copyProperties(final CnsClientConfiguration from, final CnsClientConfiguration to) {
    to.baseUrl = from.baseUrl;
    to.user = from.user;
    to.password = from.password;
    to.logger = from.logger;
    to.sessionId = from.sessionId;
    to.producerPort = from.producerPort;
    to.timezoneOffset = from.timezoneOffset;

  }

  /**
   * @return true if configuration is initialized
   */
  public static boolean isInitialized() {
    return config != null;
  }

  /**
   * @return the baseUrl
   */
  public String getBaseUrl() {
    return this.baseUrl;
  }


  /**
   * @param baseUrl the baseUrl to set
   */
  public void setBaseUrl(final String baseUrl) {
    this.baseUrl = baseUrl;
  }


  /**
   * @return the user
   */
  public String getUser() {
    return this.user;
  }


  /**
   * @param user the user to set
   */
  public void setUser(final String user) {
    this.user = user;
  }


  /**
   * @return the password
   */
  public String getPassword() {
    return this.password;
  }


  /**
   * @param password the password to set
   */
  public void setPassword(final String password) {
    this.password = password;
  }


  /**
   * @return the sessionId
   */
  public String getSessionId() {
    return this.sessionId;
  }

  /**
   * @param sessionId the sessionId to set
   */
  public void setSessionId(final String sessionId) {
    this.sessionId = sessionId;
  }

  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }


  /**
   * @param logger the logger to set
   */
  public void setLogger(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * @return Create a copy of this configuration
   */
  public CnsClientConfiguration createCopy() {
    CnsClientConfiguration ret = new CnsClientConfiguration();
    copyProperties(this, ret);
    return ret;
  }

  /**
   * @return the producerPort
   */
  public Integer getProducerPort() {
    return this.producerPort;
  }

  /**
   * @param producerPort the producerPort to set
   */
  public void setProducerPort(final Integer producerPort) {
    this.producerPort = producerPort;
  }

  /**
   * @return the timezoneOffset
   */
  public int getTimezoneOffset() {
    return this.timezoneOffset;
  }

  /**
   * @param timezoneOffset the timezoneOffset to set
   */
  public void setTimezoneOffset(final int timezoneOffset) {
    this.timezoneOffset = timezoneOffset;
  }


}
