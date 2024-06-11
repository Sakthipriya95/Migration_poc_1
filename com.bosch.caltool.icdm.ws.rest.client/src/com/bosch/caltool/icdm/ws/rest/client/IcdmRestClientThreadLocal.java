/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.ws.rest.client.service.GZIPDecompressionInterceptor;
import com.bosch.caltool.icdm.ws.rest.client.util.Utils;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Thread local for jersey client creation
 *
 * @author bne4cob
 */
class IcdmRestClientThreadLocal extends ThreadLocal<Client> {

  /**
   * Jersey client configuration
   */
  private final Configuration config;

  /**
   * @param user user
   * @param password password
   */
  public IcdmRestClientThreadLocal(final String user, final String password) {
    this.config = createClientConfig(user, password);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Client initialValue() {
    Client ret = ClientBuilder.newClient(this.config);

    getLogger().debug("Jersey client created - {}", ret);

    return ret;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void remove() {
    Client client = get();
    client.close();
    getLogger().debug("Jersey client closed - {}", client);

    super.remove();
  }

  /**
   * Create client configuration
   *
   * @param userName user
   * @param passwd password
   * @param configCompressed
   */
  private ClientConfig createClientConfig(final String userName, final String passwd) {

    ClientConfig jerseyConfig = new ClientConfig();

    // Jackson integration, for JSON responses
    jerseyConfig.register(JacksonJsonProvider.class);
    jerseyConfig.register(MultiPartFeature.class);
    jerseyConfig.register(JacksonFeature.class);

    // GZIP compression
    jerseyConfig.register(GZIPDecompressionInterceptor.class);

    // Basic Authentication - uses Base64 standard for encoding the username and password.
    if (!Utils.isEmptyString(userName) && !Utils.isEmptyString(passwd)) {
      jerseyConfig.register(HttpAuthenticationFeature.basic(userName, passwd));
    }

    return jerseyConfig;
  }

  /**
   * Logger
   *
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return ClientConfiguration.getDefault().getLogger();
  }


}
