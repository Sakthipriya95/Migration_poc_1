/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.exceptionmapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.bosch.caltool.apic.ws.common.dataobject.ErrorMessages;
import com.bosch.caltool.apic.ws.rest.authentication.UnAuthorizedRestAccessException;
import com.bosch.caltool.dmframework.common.ObjectStore;

/**
 * @author bne4cob
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Response toResponse(final WebApplicationException exp) {
    ObjectStore.getInstance().getLogger().error(exp.getMessage(), exp);

    int statusCode = exp.getResponse().getStatus();

    ErrorMessages errRet = new ErrorMessages();
    errRet.getErrors().put(String.valueOf(statusCode), exp.getMessage());

    if (exp instanceof UnAuthorizedRestAccessException) {
      return Response.status(statusCode).header("WWW-Authenticate", "Basic realm=\"iCDM\"")
          .type(MediaType.APPLICATION_JSON).entity(errRet).build();
    }

    return Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(errRet).build();

  }

}
