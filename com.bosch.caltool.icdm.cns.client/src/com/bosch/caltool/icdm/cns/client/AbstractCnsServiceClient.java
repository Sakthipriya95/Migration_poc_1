/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;

/**
 * @author bne4cob
 */
public abstract class AbstractCnsServiceClient {

  /**
   * Base URI target for the service
   */
  private WebTarget wsBase;

  private final String serviceBase;

  private CnsClientConfiguration clientConfiguration;

  /**
   * @param serviceBase service base URL
   */
  protected AbstractCnsServiceClient(final String serviceBase) {
    this.serviceBase = serviceBase;
  }

  /**
   * @return client configuration being used for service
   */
  private CnsClientConfiguration getClientConfiguration() {
    return this.clientConfiguration == null ? CnsClientConfiguration.getDefaultConfig() : this.clientConfiguration;
  }

  /**
   * @param clientConfiguration the clientConfiguration to set
   */
  public void setClientConfiguration(final CnsClientConfiguration clientConfiguration) {
    this.clientConfiguration = clientConfiguration;
  }

  /**
   * Create a local configuration, using default configuration. if local configuration is already created, then it is
   * returned
   *
   * @return the local configuration
   */
  protected final CnsClientConfiguration createLocalConfiguration() {
    if (this.clientConfiguration == null) {
      this.clientConfiguration = CnsClientConfiguration.getDefaultConfig().createCopy();
    }
    return this.clientConfiguration;
  }

  /**
   * create rest target
   *
   * @return base web target for the services
   */
  private WebTarget createRestWebTarget() {
    CnsClientConfiguration cnsConfig = getClientConfiguration();
    Client restClient = CnsRestClientProvider.getClient(cnsConfig);

    URI baseURI = UriBuilder.fromUri(cnsConfig.getBaseUrl()).build();
    return restClient.target(baseURI).path(this.serviceBase);
  }

  /**
   * Execute get() request for generic type response
   *
   * @param wsTarget web target to hit
   * @param genericRetType generic type for response type R's collection
   * @param <R> response type
   * @return R response data object
   * @throws CnsServiceClientException if any error occurs during the webservice call
   */
  protected final <R> R get(final WebTarget wsTarget, final GenericType<R> genericRetType)
      throws CnsServiceClientException {

    getLogger().debug("Invoking get(): target - {}; response type - {}", wsTarget, genericRetType);

    long startTime = System.currentTimeMillis();

    R ret = getResponse(wsTarget, null, genericRetType);

    getLogger().debug("Invoking get(): response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Execute get() request for generic type response
   *
   * @param wsTarget web target to hit
   * @param retTypeClass response type R
   * @param <R> response type
   * @return R response data object
   * @throws CnsServiceClientException if any error occurs during the webservice call
   */
  protected final <R> R get(final WebTarget wsTarget, final Class<R> retTypeClass) throws CnsServiceClientException {

    getLogger().debug("Invoking get(): target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    R ret = getResponse(wsTarget, retTypeClass, null);

    getLogger().debug("Invoking get(): response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Execute post() request for POJO response
   *
   * @param wsTarget web target to hit
   * @param input input post input of type I
   * @param retTypeClass class of response type
   * @return response of type R
   * @throws CnsServiceClientException if any error occurs during the webservice call
   */
  protected final <R, I> R post(final WebTarget wsTarget, final I input, final Class<R> retTypeClass)
      throws CnsServiceClientException {

    getLogger().debug("Invoking post(): target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = wsTarget.request().header(CnsCommonConstants.REQ_USER_ID, getClientConfiguration().getUser())
        .header(CnsCommonConstants.REQHDR_TIMEZONE, getClientConfiguration().getTimezoneOffset())
        .header(HttpHeaders.ACCEPT_ENCODING, CnsCommonConstants.ENCODING_GZIP).accept(MediaType.APPLICATION_JSON);
    String cnsSessionId = getClientConfiguration().getSessionId();
    if (cnsSessionId != null) {
      builder.header(CnsCommonConstants.REQ_SESSION_ID, cnsSessionId);
    }

    R ret = null;
    Response resp = null;

    try {
      Entity<I> entity = input == null ? Entity.json(null) : Entity.entity(input, MediaType.APPLICATION_JSON);
      resp = builder.post(entity);
      checkErrors(resp);
      ret = resp.readEntity(retTypeClass);
    }
    catch (Exception exp) {
      throwCnsClientException(exp);
    }
    finally {
      closeResponse(resp);
    }

    getLogger().debug("Invoking post(): response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Wrap all exceptions in CnsServiceClientException and throw
   *
   * @param exp exception
   * @throws CnsServiceClientException exception
   */
  private void throwCnsClientException(final Exception exp) throws CnsServiceClientException {
    if (exp instanceof CnsServiceClientException) {
      throw (CnsServiceClientException) exp;
    }

    if (exp instanceof ProcessingException) {
      if ((exp.getCause() instanceof ConnectException) || (exp.getCause() instanceof UnknownHostException)) {
        throw new CnsServiceClientException("CNSC-100", "Failed to contact CNS server", exp);
      }
      throw new CnsServiceClientException("CNSC-101", exp.getMessage(), exp);
    }

    throw new CnsServiceClientException("CNSC-102", exp.getMessage(), exp);
  }

  /**
   * Get the response from the web target
   *
   * @param target web target
   * @param retTypeClass if response type is a class
   * @param genericRetType if response type is generic
   * @return response as class or generic type
   * @throws CnsServiceClientException any error from service
   */
  private <R> R getResponse(final WebTarget target, final Class<R> retTypeClass, final GenericType<R> genericRetType)
      throws CnsServiceClientException {

    Builder reqBuilder =
        target.request().header(CnsCommonConstants.REQ_SESSION_ID, getClientConfiguration().getSessionId())
            .header(CnsCommonConstants.REQHDR_TIMEZONE, getClientConfiguration().getTimezoneOffset())
            .header(HttpHeaders.ACCEPT_ENCODING, CnsCommonConstants.ENCODING_GZIP).accept(MediaType.APPLICATION_JSON);

    R ret = null;
    Response resp = null;

    try {
      resp = reqBuilder.get();
      checkErrors(resp);
      ret = retTypeClass == null ? resp.readEntity(genericRetType) : resp.readEntity(retTypeClass);
      resp.close();
    }
    catch (Exception exp) {
      throwCnsClientException(exp);
    }
    finally {
      closeResponse(resp);
    }

    return ret;
  }

  /**
   * Close response object
   *
   * @param resp response
   */
  private void closeResponse(final Response resp) {
    if (resp != null) {
      try {
        resp.close();
      }
      catch (Exception ex) {
        getLogger().debug(ex.getMessage(), ex);
      }
    }
  }

  /**
   * Log response info
   *
   * @param resp response
   */
  protected final void logRespInfo(final Response resp) {
    getLogger().debug("Response : Status code = {}, content type = {}, length = {} bytes", resp.getStatus(),
        resp.getHeaderString(HttpHeaders.CONTENT_TYPE), resp.getHeaderString(HttpHeaders.CONTENT_LENGTH));
  }

  /**
   * Create future handle for asynchronous services
   *
   * @param request web service request
   * @param input input
   * @param callback calback
   * @return Future
   */
  protected Future<Response> createFuture(final Builder request, final Object input,
      final InvocationCallback<Response> callback) {

    Builder reqBuilder = request.header(CnsCommonConstants.REQ_SESSION_ID, getClientConfiguration().getSessionId());
    Integer producerPort = getClientConfiguration().getProducerPort();
    if (producerPort != null) {
      reqBuilder = reqBuilder.header(CnsCommonConstants.REQHDR_PRODUCER_PORT, producerPort);
    }

    final AsyncInvoker asyncInvoker = reqBuilder.async();
    return asyncInvoker.post(Entity.entity(input, MediaType.APPLICATION_JSON), callback);

  }

  /**
   * @param responseFuture Response Future
   * @param retTypeClass return type class
   * @return return
   * @throws CnsServiceClientException error while invoking service
   */
  protected <R> R getResponse(final Future<Response> responseFuture, final Class<R> retTypeClass)
      throws CnsServiceClientException {

    Response resp = null;
    R ret = null;

    try {
      Long startTime = System.currentTimeMillis();
      getLogger().debug("Invoking asynchronous post() : Expected return type = {}", retTypeClass);
      // get() waits for the response to be ready
      resp = responseFuture.get();
      checkErrors(resp);

      ret = resp.readEntity(retTypeClass);

      getLogger().debug("Invoking asynchronous post() : response Received. Time taken = {} ms",
          System.currentTimeMillis() - startTime);


    }
    catch (InterruptedException | ExecutionException e) {
      throw new CnsServiceClientException("CNSC-103", e.getMessage(), e);
    }
    finally {
      closeResponse(resp);
    }

    return ret;

  }

  /**
   * Execute delete() request for POJO response
   *
   * @param wsTarget web target to hit
   * @throws CnsServiceClientException if any error occurs during the webservice call
   */
  protected final void delete(final WebTarget wsTarget) throws CnsServiceClientException {

    getLogger().debug("Invoking delete(): target - {}; response type - {}", wsTarget);

    long startTime = System.currentTimeMillis();

    Builder builder = wsTarget.request().header(CnsCommonConstants.REQ_USER_ID, getClientConfiguration().getUser())
        .header(CnsCommonConstants.REQHDR_TIMEZONE, getClientConfiguration().getTimezoneOffset())
        .header(HttpHeaders.ACCEPT_ENCODING, CnsCommonConstants.ENCODING_GZIP);

    Response response = null;

    try {
      response = builder.delete();
      checkErrors(response);
    }
    catch (Exception exp) {
      throwCnsClientException(exp);
    }
    finally {
      closeResponse(response);
    }

    getLogger().debug("Invoking delete(): completed. Time taken = {} ms", System.currentTimeMillis() - startTime);
  }

  /**
   * Check the response for errors
   *
   * @param resp response
   * @throws ApicWebServiceException if there are errors, with the error message
   */
  private void checkErrors(final Response resp) throws CnsServiceClientException {
    logRespInfo(resp);

    if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
      // No errors
      return;
    }

    String respMsg = resp.readEntity(String.class);

    getLogger().error("Error while accessing CNS service: " + resp.getStatus() + " - " + respMsg);

    if (resp.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
      throw new CnsServiceClientException("CNSC-105", "Failed to contact CNS server");
    }

    throw new CnsServiceClientException("CNSC-104", resp.getStatus() + ": " + respMsg);
  }

  /**
   * @return the logger
   */
  protected ILoggerAdapter getLogger() {
    return getClientConfiguration().getLogger();
  }

  /**
   * @return the wsBase
   */
  protected final WebTarget getWsBase() {
    if (this.wsBase == null) {
      this.wsBase = createRestWebTarget();
    }
    return this.wsBase;
  }

  /**
   * @return client session ID
   */
  protected final String getSessionId() {
    return getClientConfiguration().getSessionId();
  }


}
