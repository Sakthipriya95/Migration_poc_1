/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.service;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.common.dataobject.ErrorMessages;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Response handler for rest web services
 *
 * @author bne4cob
 * @param <R> response return type
 */
public class ResponseHandler<R> {

  /**
   * Logger
   */
  private static final ILoggerAdapter LOGGER = ClientConfiguration.getDefault().getLogger();

  /**
   * Web target
   */
  private WebTarget target;

  /**
   * class of return type
   */
  private Class<R> retTypeClass;

  /**
   * Generic type, to handle generic type of responses
   */
  private GenericType<R> genericRetType;

  private Future<Response> responseFuture;

  /**
   * Client configuration to use
   */
  private final ClientConfiguration clientConfig;

  private Response serviceResponse;

  /**
   * Constructor for defined data model responses
   *
   * @param clientConfig Client configuration to use
   * @param target web target of this rest request
   * @param retTypeClass class of return type
   */
  public ResponseHandler(final ClientConfiguration clientConfig, final WebTarget target, final Class<R> retTypeClass) {
    this.clientConfig = clientConfig;
    this.target = target;
    this.retTypeClass = retTypeClass;
  }


  /**
   * Constructor for generic type responses. e.g. Map, Collection
   *
   * @param clientConfig Client configuration to use
   * @param target web target of this rest request
   * @param genericRetType Generic type
   */
  public ResponseHandler(final ClientConfiguration clientConfig, final WebTarget target,
      final GenericType<R> genericRetType) {
    this.clientConfig = clientConfig;
    this.target = target;
    this.genericRetType = genericRetType;
  }

  /**
   * Constructor for asynchronous response futures with defined data model responses
   *
   * @param clientConfig Client configuration to use
   * @param responseFuture asynchronous response future
   * @param retTypeClass class of return type
   */
  public ResponseHandler(final ClientConfiguration clientConfig, final Future<Response> responseFuture,
      final Class<R> retTypeClass) {
    this.clientConfig = clientConfig;
    this.responseFuture = responseFuture;
    this.retTypeClass = retTypeClass;
  }


  /**
   * Get response. Common request headers will be set. Response media type is JSON
   *
   * @return R response data object
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public R getResponse() throws ApicWebServiceException {
    return getResponse(MediaType.APPLICATION_JSON);
  }


  /**
   * Get response as the given media type. Common request headers will be set
   *
   * @param mediaType media type
   * @return R response data object
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public R getResponse(final String mediaType) throws ApicWebServiceException {

    Builder reqBuilder =
        this.target.request().header(HttpHeaders.ACCEPT_ENCODING, WsCommonConstants.ENCODING_GZIP).accept(mediaType);

    reqBuilder.header(WsCommonConstants.REQ_LANGUAGE, this.clientConfig.getLanguage());
    reqBuilder.header(WsCommonConstants.REQ_TIMEZONE, this.clientConfig.getTimezone());
    String cnsSessionId = this.clientConfig.getCnsSessionId();
    if (cnsSessionId != null) {
      reqBuilder.header(WsCommonConstants.REQ_CNS_SESSION_ID, cnsSessionId);
    }
    try {
      this.serviceResponse = reqBuilder.get();
      checkErrors(this.serviceResponse);

      return this.retTypeClass == null ? this.serviceResponse.readEntity(this.genericRetType)
          : this.serviceResponse.readEntity(this.retTypeClass);

    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (ProcessingException exp) {
      if (exp.getCause() instanceof ConnectException) {
        throw new ApicWebServiceException(WSErrorCodes.CONNECTION_FAILED,
            WsCommonConstants.ICDM_SERVER_CONNECT_ERROR_MSG, exp);
      }
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
  }


  /**
   * Check the response for errors
   *
   * @param resp response
   * @throws ApicWebServiceException if there are errors, with the error message
   */
  public static void checkErrors(final Response resp) throws ApicWebServiceException {
    logRespInfo(resp);

    if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
      // No errors
      return;
    }

    ErrorMessages err;
    try {
      // Retrieve error messages set by the server
      err = resp.readEntity(ErrorMessages.class);
    }
    catch (ProcessingException exp) {
      // In some cases(for e.g. wrong URL), readEntity() will fail with ZipException
      // For this, get the error message from the response status directly.
      LOGGER.warn("Failed to retrieve error details from server. " + exp.getMessage(), exp);

      if (resp.getStatusInfo().getStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR,
            WsCommonConstants.ICDM_SERVER_CONNECT_ERROR_MSG);
      }
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, resp.getStatusInfo().getReasonPhrase());
    }
    if ((err != null) && (err.getErrors() != null) && !err.getErrors().isEmpty()) {
      logErrorMessages(err);
      // Throw exception using the error model
      throw new ApicWebServiceException(err);
    }
    // If err model is empty, and if status is "NOT found" consider it as Server not available
    if (resp.getStatusInfo().getStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, WsCommonConstants.ICDM_SERVER_CONNECT_ERROR_MSG);
    }
    // Any other error from server
    throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, resp.readEntity(String.class));
  }


  /**
   * Log error messages
   *
   * @param err Error Messages from service
   */
  private static void logErrorMessages(final ErrorMessages err) {
    err.getErrors().entrySet().forEach(e -> LOGGER.error(" Service Error - " + e.getKey() + " : " + e.getValue()));
  }

  /**
   * Log response info
   *
   * @param resp response
   */
  public static void logRespInfo(final Response resp) {
    LOGGER.debug("  Response : Status code = {}, content type = {}, length = {} bytes", resp.getStatus(),
        resp.getHeaderString(HttpHeaders.CONTENT_TYPE), resp.getHeaderString(HttpHeaders.CONTENT_LENGTH));
  }

  /**
   * @return return as defined
   * @throws ApicWebServiceException error while invoking service
   */
  public R getResponseAsync() throws ApicWebServiceException {

    R ret = null;

    try {
      Long startTime = System.currentTimeMillis();
      LOGGER.debug("Invoking asynchronous call : Expected return type = {}", this.retTypeClass);
      // get() waits for the response to be ready
      this.serviceResponse = this.responseFuture.get();

      checkErrors(this.serviceResponse);

      ret = this.retTypeClass == null ? this.serviceResponse.readEntity(this.genericRetType)
          : this.serviceResponse.readEntity(this.retTypeClass);

      LOGGER.debug("Invoking asynchronous call : response Received. Time taken = {} ms",
          System.currentTimeMillis() - startTime);

    }
    catch (InterruptedException exp) {
      Thread.currentThread().interrupt();
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    catch (ExecutionException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }
    finally {
      closeResponse(this.serviceResponse);
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
        LOGGER.debug(ex.getMessage(), ex);
      }
    }
  }


  /**
   * @return the rawResposne
   */
  public Response getServiceResposne() {
    return this.serviceResponse;
  }

}
