/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;

/**
 * The Class RvwQnaireAnswerService.
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_QNAIRE_ANSWER)
public class RvwQnaireAnswerService extends AbstractRestService {


  /**
   * Get RvwQnaireAnswer using its id
   *
   * @param objId object's id
   * @return Rest response, with RvwQnaireAnswer object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwQnaireAnswerLoader loader = new RvwQnaireAnswerLoader(getServiceData());
    RvwQnaireAnswer ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Creates the.
   *
   * @param inputData the input data
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final RvwQnaireAnswer inputData) throws IcdmException {
    // Invoke command
    RvwQnaireAnswerCommand cmd = new RvwQnaireAnswerCommand(getServiceData(), inputData, COMMAND_MODE.CREATE);
    executeCommand(cmd);

    RvwQnaireAnswer ret = cmd.getNewData();

    WSObjectStore.getLogger().info("RvwQnaireAnswerService.create completed. ID = {}", ret.getId());

    return Response.ok(ret).build();
  }

  /**
   * Update.
   *
   * @param inputData the input data
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final RvwQnaireAnswer inputData) throws IcdmException {
    // Invoke command
    RvwQnaireAnswerCommand cmd = new RvwQnaireAnswerCommand(getServiceData(), inputData, COMMAND_MODE.UPDATE);
    executeCommand(cmd);

    RvwQnaireAnswer ret = cmd.getNewData();

    WSObjectStore.getLogger().info("RvwQnaireAnswerService.update completed. ID = {}", ret.getId());

    return Response.ok(ret).build();
  }

  /**
   * Delete.
   *
   * @param ansId the review questionaire answer id
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long ansId) throws IcdmException {
    // Invoke command
    RvwQnaireAnswerCommand cmd = new RvwQnaireAnswerCommand(getServiceData(),
        new RvwQnaireAnswerLoader(getServiceData()).getDataObjectByID(ansId), COMMAND_MODE.DELETE);
    executeCommand(cmd);

    WSObjectStore.getLogger().info("RvwQnaireAnswerService.delete completed Review Qnaire Answer Id:{}. ", ansId);

    return Response.ok().build();
  }
}
