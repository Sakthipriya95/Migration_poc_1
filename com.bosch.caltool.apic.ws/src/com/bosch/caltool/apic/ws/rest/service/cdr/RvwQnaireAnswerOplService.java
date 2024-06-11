/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.ArrayList;
import java.util.List;

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
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerOplCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerOplLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;

/**
 * @author apj4cob
 */
/**
 * The Class RvwQnaireAnswerOplService.
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_QNAIRE_ANSWER_OPL)
public class RvwQnaireAnswerOplService extends AbstractRestService {

  /**
   * Get RvwQnaireAnswerOpl using its id
   *
   * @param objId object's id
   * @return Rest response, with RvwQnaireAnswer object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwQnaireAnswerOplLoader loader = new RvwQnaireAnswerOplLoader(getServiceData());
    RvwQnaireAnswerOpl ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Creates the.
   *
   * @param oplList List<RvwQnaireAnswerOpl> the input data
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final List<RvwQnaireAnswerOpl> oplList) throws IcdmException {
    List<AbstractSimpleCommand> oplCmdList = new ArrayList<>();
    for (RvwQnaireAnswerOpl opl : oplList) {
      // Invoke command
      RvwQnaireAnswerOplCommand cmd = new RvwQnaireAnswerOplCommand(getServiceData(), opl, COMMAND_MODE.CREATE);
      oplCmdList.add(cmd);
    }
    executeCommand(oplCmdList);
    List<RvwQnaireAnswerOpl> newOplList = new ArrayList<>();
    for (AbstractSimpleCommand cmd : oplCmdList) {
      newOplList.add(((RvwQnaireAnswerOplCommand) cmd).getNewData());
    }

    WSObjectStore.getLogger().info("RvwQnaireAnswerOplService.create completed.");

    return Response.ok(newOplList).build();
  }

  /**
   * Update.
   *
   * @param oplList List<RvwQnaireAnswerOpl> the input data
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final List<RvwQnaireAnswerOpl> oplList) throws IcdmException {
    List<AbstractSimpleCommand> oplCmdList = new ArrayList<>();
    for (RvwQnaireAnswerOpl opl : oplList) {
      RvwQnaireAnswerOplCommand cmd = new RvwQnaireAnswerOplCommand(getServiceData(), opl, COMMAND_MODE.UPDATE);
      oplCmdList.add(cmd);
    }

    executeCommand(oplCmdList);

    List<RvwQnaireAnswerOpl> newOplList = new ArrayList<>();
    for (AbstractSimpleCommand cmd : oplCmdList) {
      newOplList.add(((RvwQnaireAnswerOplCommand) cmd).getNewData());
    }


    WSObjectStore.getLogger().info("RvwQnaireAnswerOplService.update completed.");

    return Response.ok(newOplList).build();
  }

  /**
   * Delete.
   *
   * @param ansOplId the Rvw Qnaire Answer Opl id
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long ansOplId)
      throws IcdmException {
    // Invoke command
    RvwQnaireAnswerOplCommand cmd = new RvwQnaireAnswerOplCommand(getServiceData(),
        new RvwQnaireAnswerOplLoader(getServiceData()).getDataObjectByID(ansOplId), COMMAND_MODE.DELETE);
    executeCommand(cmd);

    WSObjectStore.getLogger().info("RvwQnaireAnswerOplService.delete completed.Review Qnaire Answer Id:{} ", ansOplId);
    return Response.ok().build();

  }
}

