/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;

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
import com.bosch.caltool.icdm.bo.apic.AliasDetailLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AliasDetailCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.AliasDetail;


/**
 * Service class for Alias Detail
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_ALIASDETAIL)
public class AliasDetailService extends AbstractRestService {


  /**
   * Get Alias Detail using its id
   *
   * @param objId object's id
   * @return Rest response, with AliasDetail object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    AliasDetailLoader loader = new AliasDetailLoader(getServiceData());
    AliasDetail ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get Alias Detail using Alias Definition ID
   *
   * @param adId Alias Definition ID
   * @return Rest response, with Map of AliasDetail objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_BY_AD_ID)
  @CompressData
  public Response getByAdId(@QueryParam(WsCommonConstants.RWS_QP_AD_ID) final Long adId) throws IcdmException {
    AliasDetailLoader loader = new AliasDetailLoader(getServiceData());
    Map<Long, AliasDetail> retMap = loader.getByAdId(adId);
    getLogger().info("Alias Detail getByAdId completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Create a Alias Detail record
   *
   * @param obj object to create
   * @return Rest response, with created AliasDetail object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final AliasDetail obj) throws IcdmException {
    AliasDetailCommand cmd = new AliasDetailCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    AliasDetail ret = cmd.getNewData();
    getLogger().info("Created Alias Detail Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Alias Detail record
   *
   * @param obj object to update
   * @return Rest response, with updated AliasDetail object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final AliasDetail obj) throws IcdmException {
    AliasDetailCommand cmd = new AliasDetailCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    AliasDetail ret = cmd.getNewData();
    getLogger().info("Updated Alias Detail Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a Alias Detail record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    AliasDetailLoader loader = new AliasDetailLoader(getServiceData());
    AliasDetail obj = loader.getDataObjectByID(objId);
    AliasDetailCommand cmd = new AliasDetailCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

}
