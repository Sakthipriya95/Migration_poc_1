/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;

/**
 * @author bne4cob
 */
@Provider
public class CnsServiceExceptionMapper implements ExceptionMapper<CnsServiceException> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Response toResponse(final CnsServiceException exp) {

    CnsObjectStore.getLogger().error(exp.getMessage(), exp);

    return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).type(MediaType.APPLICATION_JSON)
        .entity(exp.getErrorCode() + " : " + exp.getMessage()).build();


  }


}
