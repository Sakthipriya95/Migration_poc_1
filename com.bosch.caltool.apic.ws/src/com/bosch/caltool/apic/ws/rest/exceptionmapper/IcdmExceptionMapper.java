/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.exceptionmapper;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.common.dataobject.ErrorMessages;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.util.ErrorCodeHandler;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.StaleDataException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;

/**
 * @author bne4cob
 */
@Provider
public class IcdmExceptionMapper implements ExceptionMapper<IcdmException> {

  @Context
  private HttpServletRequest request;

  /**
   * {@inheritDoc}
   */
  @Override
  public Response toResponse(final IcdmException exp) {
    getLogger().error(exp.getMessage(), exp);

    ErrorMessages errRet = new ErrorMessages();

    String errMsg = (new ErrorCodeHandler(getServiceData()).getErrorMessage(exp));
    String errorCode = exp.getErrorCode();
    if (!ErrorCodeHandler.isValidErrorMessageFormat(errorCode)) {
      errorCode = findErrorCodeFromExType(exp);
    }

    errRet.getErrors().put(errorCode, errMsg);

    logErrorModel(errRet);

    ResponseBuilder responseBuilder = createErrorResponse(exp, errRet);
    for (Entry<String, String> additionalInfo : exp.getAdditionalInfo().entrySet()) {
      responseBuilder.header(additionalInfo.getKey(), additionalInfo.getValue());
    }
    return responseBuilder.build();
  }


  /**
   * @param errRet
   */
  private void logErrorModel(final ErrorMessages errRet) {
    StringBuilder err = new StringBuilder("Error model - ");
    for (Entry<String, String> entry : errRet.getErrors().entrySet()) {
      err.append(entry.getKey()).append(" : ").append(entry.getValue()).append('\n');
    }

    getLogger().error(err.toString());
  }


  /**
   * Defined Error response status
   *
   * @param hasErrorCode
   * @param errRet
   * @return
   */
  private ResponseBuilder createErrorResponse(final Exception exp, final ErrorMessages errRet) {
    if (exp instanceof StaleDataException) {
      return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(errRet);
    }
    if (exp instanceof DataNotFoundException) {
      return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(errRet);
    }
    if (exp instanceof CommandException) {
      return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).type(MediaType.APPLICATION_JSON)
          .entity(errRet);
    }
    if (exp instanceof UnAuthorizedAccessException) {
      return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).type(MediaType.APPLICATION_JSON)
          .entity(errRet);
    }
    if (exp instanceof InvalidInputException) {
      return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).type(MediaType.APPLICATION_JSON)
          .entity(errRet);
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).type(MediaType.APPLICATION_JSON)
        .entity(errRet);
  }


  private String findErrorCodeFromExType(final Exception exp) {
    if (exp instanceof StaleDataException) {
      return WSErrorCodes.STALE_DATA;
    }
    if (exp instanceof DataNotFoundException) {
      return WSErrorCodes.DATA_NOT_FOUND;
    }
    if (exp instanceof CommandException) {
      return WSErrorCodes.COMMAND_ERROR;
    }
    if (exp instanceof UnAuthorizedAccessException) {
      return WSErrorCodes.UNAUTHORIZED;
    }
    if (exp instanceof InvalidInputException) {
      return WSErrorCodes.BAD_REQUEST;
    }
    return WSErrorCodes.ICDM_ERROR;
  }

  /**
   * @return the service data object
   */
  private ServiceData getServiceData() {
    return (ServiceData) this.request.getAttribute(WsCommonConstants.RWS_REQUEST_ATTR_COMMON_SER_DATA);
  }

  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }
}
