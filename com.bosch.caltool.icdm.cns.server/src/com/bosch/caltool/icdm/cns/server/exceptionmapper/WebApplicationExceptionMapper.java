/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.exceptionmapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;

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

    CnsObjectStore.getLogger().error(exp.getMessage(), exp);

    int statusCode = exp.getResponse().getStatus();


    // if (exp instanceof UnAuthorizedRestAccessException) {
    // return Response.status(statusCode).header("WWW-Authenticate", "Basic realm=\"iCDM\"")
    // .type(MediaType.APPLICATION_JSON).entity(errRet).build();
    // }

    return Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(exp.getMessage()).build();

  }

}
