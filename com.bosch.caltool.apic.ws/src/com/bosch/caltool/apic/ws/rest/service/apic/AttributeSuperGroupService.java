/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
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
import com.bosch.caltool.icdm.bo.apic.attr.AttrSuperGroupCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttrSuperGroupLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_ATTRIBUTE_SUPER_GROUP)
public class AttributeSuperGroupService extends AbstractRestService {

  /**
   * Get an Attribute Super Group by ID
   *
   * @param objId Super Group ID
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    AttrSuperGroupLoader loader = new AttrSuperGroupLoader(getServiceData());

    // Fetch AttrSuperGroup
    AttrSuperGroup ret = loader.getDataObjectByID(objId);

    return Response.ok(ret).build();

  }

  /**
   * Get Attribute Group model, consisting of super groups, groups and their relationships
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ATTR_GRP_MODEL)
  @CompressData
  public Response getAttrGroupModel() throws IcdmException {

    // Create loader object
    AttrSuperGroupLoader loader = new AttrSuperGroupLoader(getServiceData());
    AttrGroupModel ret = loader.getAttrGroupModel(true);

    return Response.ok(ret).build();
  }

  /**
   * Create a AttrSuperGroup record
   *
   * @param obj object to create
   * @return Rest response, with created AttrGroup object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final AttrSuperGroup obj) throws IcdmException {
    AttrSuperGroupCommand cmd = new AttrSuperGroupCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    AttrSuperGroup ret = cmd.getNewData();
    getLogger().info("Created AttrSuperGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a AttrSuperGroup record
   *
   * @param obj object to update
   * @return Rest response, with updated AttrGroup object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final AttrSuperGroup obj) throws IcdmException {
    AttrSuperGroupCommand cmd = new AttrSuperGroupCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    AttrSuperGroup ret = cmd.getNewData();
    getLogger().info("Updated AttrSuperGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }
}
