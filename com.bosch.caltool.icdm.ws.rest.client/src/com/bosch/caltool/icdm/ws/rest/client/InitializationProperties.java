/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;

/**
 * @author bne4cob
 */
public class InitializationProperties {

  private String server;
  private ILoggerAdapter logger;

  private String userName;

  private String password;

  private boolean passwordEncrypted = true;

  private String language;

  private String timezone;

  private String userTempDirectory;

  private String icdmTempDirectory;

  private Boolean cnsEnabled;


  /**
   * @return the server
   */
  String getServer() {
    return this.server;
  }

  /**
   * @param server the server to set
   */
  public void setServer(final String server) {
    this.server = server;
  }

  /**
   * @return the logger
   */
  ILoggerAdapter getLogger() {
    return this.logger;
  }

  /**
   * @param logger the logger to set
   */
  public void setLogger(final ILoggerAdapter logger) {
    this.logger = logger;
  }


  /**
   * @return the appUserName
   */
  String getUserName() {
    return this.userName;
  }


  /**
   * @param appUserName the appUserName to set
   */
  public void setUserName(final String appUserName) {
    this.userName = appUserName;
  }


  /**
   * @return the password
   */
  String getPassword() {
    return this.password;
  }


  /**
   * @param password the password to set
   */
  public void setPassword(final String password) {
    this.password = password;
  }


  /**
   * @return the passwordEncrypted
   */
  boolean isPasswordEncrypted() {
    return this.passwordEncrypted;
  }

  /**
   * @param passwordEncrypted the passwordEncrypted to set. default is true
   */
  public void setPasswordEncrypted(final boolean passwordEncrypted) {
    this.passwordEncrypted = passwordEncrypted;
  }

  /**
   * @return the language
   */
  String getLanguage() {
    return this.language;
  }


  /**
   * @param language the language to set
   */
  public void setLanguage(final String language) {
    this.language = language;
  }

  /**
   * @return the timezone
   */
  String getTimezone() {
    return this.timezone;
  }

  /**
   * @param timezone the timezone to set
   */
  public void setTimezone(final String timezone) {
    this.timezone = timezone;
  }

  /**
   * @return the userTempDirectory
   */
  String getUserTempDirectory() {
    return this.userTempDirectory;
  }

  /**
   * @param userTempDirectory the userTempDirectory to set
   */
  public void setUserTempDirectory(final String userTempDirectory) {
    this.userTempDirectory = userTempDirectory;
  }

  /**
   * @return the icdmTempDirectory
   */
  String getIcdmTempDirectory() {
    return this.icdmTempDirectory;
  }

  /**
   * @param icdmTempDirectory the icdmTempDirectory to set
   */
  public void setIcdmTempDirectory(final String icdmTempDirectory) {
    this.icdmTempDirectory = icdmTempDirectory;
  }

  /**
   * @return true if Change notification event triggering is enabled
   */
  Boolean isCnsEnabled() {
    return this.cnsEnabled;
  }

  /**
   * Set whether Change notification event should be enabled or not
   *
   * @param cnsEnabled the cnsEnabled to set
   */
  public void setCnsEnabled(final boolean cnsEnabled) {
    this.cnsEnabled = cnsEnabled;
  }


}
