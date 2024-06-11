package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;

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
import com.bosch.caltool.icdm.bo.apic.attr.AttrGroupCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttrGroupLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;


/**
 * Service class for AttrGroup
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_ATTRIBUTE_GROUP)
public class AttrGroupService extends AbstractRestService {

  /**
   * Rest web service path for AttrGroup
   */
  public final static String RWS_ATTRGROUP = "attrgroup";

  /**
   * Get AttrGroup using its id
   *
   * @param objId object's id
   * @return Rest response, with AttrGroup object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    AttrGroupLoader loader = new AttrGroupLoader(getServiceData());
    AttrGroup ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all AttrGroup records
   *
   * @return Rest response, with Map of AttrGroup objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {
    AttrGroupLoader loader = new AttrGroupLoader(getServiceData());
    Map<Long, AttrGroup> retMap = loader.getAll();
    getLogger().info(" AttrGroup getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }


  /**
   * Create a AttrGroup record
   *
   * @param obj object to create
   * @return Rest response, with created AttrGroup object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final AttrGroup obj) throws IcdmException {
    AttrGroupCommand cmd = new AttrGroupCommand(getServiceData(), obj, false);
    executeCommand(cmd);
    AttrGroup ret = cmd.getNewData();
    getLogger().info("Created AttrGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a AttrGroup record
   *
   * @param obj object to update
   * @return Rest response, with updated AttrGroup object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final AttrGroup obj) throws IcdmException {
    AttrGroupCommand cmd = new AttrGroupCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    AttrGroup ret = cmd.getNewData();
    getLogger().info("Updated AttrGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


}
