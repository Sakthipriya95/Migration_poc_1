/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwParticipantCommand;
import com.bosch.caltool.icdm.bo.cdr.RvwParticipantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;


/**
 * Service class for Review Paticipants
 *
 * @author bru2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVWPARTICIPANT)
public class RvwParticipantService extends AbstractRestService {


  /**
   * Get CDR Result Participant using its id
   *
   * @param objId object's id
   * @return Rest response, with CDRResultParameter object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwParticipantLoader loader = new RvwParticipantLoader(getServiceData());
    RvwParticipant ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Get Review Paticipants using ResultId id
   *
   * @param resultId Result Id id
   * @return Rest response, with RvwParticipant object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_BY_RESULT_ID)
  @CompressData
  public Response getByResultId(@QueryParam(WsCommonConstants.RWS_QP_RESULT_ID) final Long resultId)
      throws IcdmException {
    RvwParticipantLoader loader = new RvwParticipantLoader(getServiceData());
    CDRReviewResultLoader resultLoader = new CDRReviewResultLoader(getServiceData());
    TRvwResult entityObject = resultLoader.getEntityObject(resultId);
    Map<Long, RvwParticipant> retMap = loader.getByResultObj(entityObject);
    getLogger().info(" Review Paticipants getByResultId completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Get CDR Result participants using its id. Note : this is a POST request
   *
   * @param objIdSet set of object IDs
   * @return Rest response, with RvwParticipant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @Path(WsCommonConstants.RWS_MULTIPLE)
  public Response getMultiple(final Set<Long> objIdSet) throws IcdmException {
    RvwParticipantLoader loader = new RvwParticipantLoader(getServiceData());
    Map<Long, RvwParticipant> ret = loader.getDataObjectByID(objIdSet);
    return Response.ok(ret).build();
  }


  /**
   * Create a Review Paticipants record
   *
   * @param obj object to create
   * @return Rest response, with created RvwParticipant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final List<RvwParticipant> partciList) throws IcdmException {


    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    List<RvwParticipant> createdList = new ArrayList<>();
    Set<Long> paramIdSet = new HashSet<>();
    for (RvwParticipant particpant : partciList) {
      RvwParticipantCommand cmd = new RvwParticipantCommand(getServiceData(), particpant, false, false);
      cmdList.add(cmd);

      paramIdSet.add(particpant.getId());
    }
    executeCommand(cmdList);
    for (AbstractSimpleCommand cmd : cmdList) {
      RvwParticipant newData = ((RvwParticipantCommand) cmd).getNewData();
      createdList.add(newData);
    }
    StringBuilder paramIds = new StringBuilder();
    for (Long id : paramIdSet) {
      paramIds.append(id).append("-");
    }
    getLogger().info("Created Review Paticipants Id : {}", paramIds.toString());


    return Response.ok(createdList).build();
  }

  /**
   * Update a Review Paticipants record
   *
   * @param obj object to update
   * @return Rest response, with updated RvwParticipant object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final RvwParticipant obj) throws IcdmException {
    RvwParticipantCommand cmd = new RvwParticipantCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    RvwParticipant ret = cmd.getNewData();
    getLogger().info("Updated Review Paticipants Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a Review Paticipants record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> objId) throws IcdmException {

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();

    RvwParticipantLoader loader = new RvwParticipantLoader(getServiceData());
    Set<Long> paramIdSet = new HashSet<>();
    for (Long particpantId : objId) {
      RvwParticipantCommand cmd =
          new RvwParticipantCommand(getServiceData(), loader.getDataObjectByID(particpantId), false, true);
      cmdList.add(cmd);
      paramIdSet.add(particpantId);
    }
    executeCommand(cmdList);

    StringBuilder paramIds = new StringBuilder();
    for (Long id : paramIdSet) {
      paramIds.append(id).append("-");
    }
    getLogger().info("Deleted Review Paticipants Id : {}", paramIds.toString());

    return Response.ok().build();
  }

}
