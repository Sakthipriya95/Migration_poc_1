/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.client.service.GZIPDecompressionInterceptor;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * @author bne4cob
 */
class CnsRestClientThreadLocal extends ThreadLocal<Client> {

  private final ClientConfig restConfig;

  /**
   * @param user user
   * @param password password
   */
  public CnsRestClientThreadLocal(final String user, final String password) {
    this.restConfig = createClientConfig(user, password);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Client initialValue() {
    Client ret = ClientBuilder.newClient(this.restConfig);
    getLogger().debug("Jersey client created for CNS services - {}", ret);
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove() {
    Client client = get();
    client.close();
    getLogger().debug("Jersey client for CNS services closed - {}", client);

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
    ClientConfig configCompressed = new ClientConfig();

    // Jackson integration
    configCompressed.register(JacksonJsonProvider.class);

    // GZIP compressed response handling
    configCompressed.register(GZIPDecompressionInterceptor.class);

    // register json feature Basic Authentication uses Base64 standard for encoding the username and password.
    configCompressed.register(HttpAuthenticationFeature.basic(userName, passwd));

    return configCompressed;
  }

  /**
   * Logger
   *
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return CnsClientConfiguration.getDefaultConfig().getLogger();
  }


}
