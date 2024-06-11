/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.dataobject.ErrorMessages;
import com.bosch.caltool.dmframework.common.ObjectStore;

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
    ObjectStore.getInstance().getLogger().error(exp.getMessage(), exp);

    ErrorMessages errRet = new ErrorMessages();
    errRet.getErrors().put(WSErrorCodes.INT_SERVER_ERROR,
        "Internal server error occured. Please contact iCDM Hotline.");

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).type(MediaType.APPLICATION_JSON)
        .entity(errRet).build();
  }

}
