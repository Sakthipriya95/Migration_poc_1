/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;

/**
 * @author bne4cob
 */
@Provider
public class AllExceptionMapper implements ExceptionMapper<Exception> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Response toResponse(final Exception exp) {

    CnsObjectStore.getLogger().error(exp.getMessage(), exp);

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).type(MediaType.APPLICATION_JSON)
        .entity(exp.getMessage()).build();
  }

}
