/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import java.util.TimeZone;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceRuntimeException;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.security.Decryptor;

/**
 * @author bne4cob
 */
public final class ClientConfiguration {

  /**
   * iCDM url https
   */
  private static final String URL_HTTPS = "https://";

  /**
   * iCDM url http
   */
  private static final String URL_HTTP = "http://";

  /**
   * Default client configuration
   */
  private static final ClientConfiguration DEFAULT_CONFIG = new ClientConfiguration();

  private boolean initialized;

  private String server = "LOCAL_SERVER";
  private String baseUri;
  private ILoggerAdapter logger;
  private String userName;
  private String password;
  private String language = "English";
  private String timezone = TimeZone.getDefault().getID();
  private String userTempDirectory = System.getProperty("java.io.tmpdir");
  private String icdmTempDirectory = System.getProperty("java.io.tmpdir");
  private boolean cnsEnabled;
  private String cnsSessionId;


  private ClientConfiguration() {
    // Private constructor
  }


  /**
   * Creates client configuration using the properties.
   *
   * @param prop client properties
   * @throws ApicWebServiceException error during initialization
   */
  public ClientConfiguration(final InitializationProperties prop) throws ApicWebServiceException {
    initialize(prop);
  }

  /**
   * Creates a new client configuration from another configuration. AFter creation, properties can be customized 'once'
   * using {@link #initialize(InitializationProperties)} method
   *
   * @param config client properties
   */
  public ClientConfiguration(final ClientConfiguration config) {
    if (config == null) {
      throw new ApicWebServiceRuntimeException("Input client configuration cannot be null");
    }

    this.initialized = false;

    this.server = config.server;
    this.baseUri = config.baseUri;
    this.logger = config.logger;
    this.userName = config.userName;
    this.password = config.password;
    this.language = config.language;
    this.timezone = config.timezone;
    this.userTempDirectory = config.userTempDirectory;
    this.icdmTempDirectory = config.icdmTempDirectory;
    this.cnsEnabled = config.cnsEnabled;
    this.cnsSessionId = config.cnsSessionId;
  }

  /**
   * Intialize the client configuration with the given properties. Note that the method can be called only once on a
   * configuratoin instance.
   *
   * @param prop service client properties
   * @throws ApicWebServiceException error during initialization
   */
  public final void initialize(final InitializationProperties prop) throws ApicWebServiceException {

    if (this.initialized) {
      if (this.logger != null) {
        this.logger.warn("ICDM WS Client configuration already initialized. Base URL - " + this.baseUri);
      }
      return;
    }
    if (prop.getLogger() != null) {
      this.logger = prop.getLogger();
    }

    if (prop.getServer() != null) {
      this.server = prop.getServer();
      initializeBaseUri();
    }

    if (prop.getUserName() != null) {
      this.userName = prop.getUserName();
    }

    if (prop.getPassword() != null) {
      this.password =
          prop.isPasswordEncrypted() ? Decryptor.INSTANCE.decrypt(prop.getPassword(), this.logger) : prop.getPassword();
    }

    if (prop.getLanguage() != null) {
      this.language = prop.getLanguage();
    }

    if (prop.getTimezone() != null) {
      this.timezone = prop.getTimezone();
    }

    if (prop.getUserTempDirectory() != null) {
      this.userTempDirectory = prop.getUserTempDirectory();
    }

    if (prop.getIcdmTempDirectory() != null) {
      this.icdmTempDirectory = prop.getIcdmTempDirectory();
    }

    if (prop.isCnsEnabled() != null) {
      this.cnsEnabled = prop.isCnsEnabled();
    }

    this.initialized = true;

    this.logger.info("ICDM WS Client initialized. Base URL - {}", this.baseUri);
  }


  /**
   * @return the default client configuration
   */
  public static ClientConfiguration getDefault() {
    return DEFAULT_CONFIG;
  }

  /**
   * Initialize the base URI
   *
   * @throws ApicWebServiceException
   */
  private void initializeBaseUri() throws ApicWebServiceException {
    // If iCDM server url is directly available in messages.properties file, use the url directly
    if (this.server.startsWith(URL_HTTP) || this.server.startsWith(URL_HTTPS)) {
      this.logger.debug("ICDM web-service URL directly available in config file : {}", this.server);
      this.baseUri = this.server;
    }
    else {
      ICDM_WS_SERVER wsServer = ICDM_WS_SERVER.getServer(this.server);
      this.baseUri = (wsServer == null) ? fetchBaseUriFromPwdService() : wsServer.getBaseUri();
    }

    // Remove trailing '/' char
    if (getBaseUri().endsWith("/")) {
      this.baseUri = getBaseUri().substring(0, getBaseUri().length() - 2);
    }
  }

  private String fetchBaseUriFromPwdService() throws ApicWebServiceException {
    // Try to use the password service to resolve server type
    this.logger.debug("ICDM server type {} not defined locally. Retrieving from server ...", this.server);
    try {
      return new PasswordService().getPassword(this.server);
    }
    catch (PasswordNotFoundException ex) {
      throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR, "Invalid iCDM web service definition " + this.server,
          ex);
    }
  }

  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }


  /**
   * @return the language
   */
  public String getLanguage() {
    return this.language;
  }


  /**
   * @param language the language to set
   */
  public void setLanguage(final String language) {
    this.language = language;
  }


  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(final String userName) {
    this.userName = userName;
  }


  /**
   * @return the password
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * @return the timezone
   */
  public String getTimezone() {
    return this.timezone;
  }

  /**
   * @return the baseUri
   */
  public String getBaseUri() {
    return this.baseUri;
  }

  /**
   * @return the userTempDirectory
   */
  public String getUserTempDirectory() {
    return this.userTempDirectory;
  }

  /**
   * @return the icdmTempDirectory
   */
  public String getIcdmTempDirectory() {
    return this.icdmTempDirectory;
  }

  /**
   * @return true if Change notification event triggering is enabled
   */
  public boolean isCnsEnabled() {
    return this.cnsEnabled;
  }

  /**
   * @return the cnsSessionId
   */
  public String getCnsSessionId() {
    return this.cnsSessionId;
  }

  /**
   * @param cnsSessionId the cnsSessionId to set
   */
  public void setCnsSessionId(final String cnsSessionId) {
    this.cnsSessionId = cnsSessionId;
  }

}
