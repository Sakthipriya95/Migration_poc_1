package com.bosch.caltool.apic.ws.rest.service.uc;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.bosch.caltool.icdm.bo.uc.UcpAttrCommand;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.uc.UcpAttr;


/**
 * Service class for Ucp Attr
 *
 * @author MKL2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_UC + "/" + WsCommonConstants.RWS_UCPATTR)
public class UcpAttrService extends AbstractRestService {


  /**
   * Get Ucp Attr using its id
   *
   * @param objId object's id
   * @return Rest response, with UcpAttr object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    UcpAttrLoader loader = new UcpAttrLoader(getServiceData());
    UcpAttr ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all Ucp Attr records
   *
   * @return Rest response, with Map of UcpAttr objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {
    UcpAttrLoader loader = new UcpAttrLoader(getServiceData());
    Map<Long, UcpAttr> retMap = loader.getAll();
    getLogger().info(" Ucp Attr getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Create a Ucp Attr record
   *
   * @param objList objects to create
   * @return Rest response, with created UcpAttr object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final List<UcpAttr> objList) throws IcdmException {
    Map<Long, UcpAttr> ret = new HashMap<>();
    List<AbstractSimpleCommand> ucpAttrCmdList = new ArrayList<>();
    for (UcpAttr ucpAttr : objList) {
      UcpAttrCommand cmd = new UcpAttrCommand(getServiceData(), ucpAttr, false, false);
      ucpAttrCmdList.add(cmd);
    }
    executeCommand(ucpAttrCmdList);
    for (AbstractSimpleCommand command : ucpAttrCmdList) {
      ret.put(((UcpAttrCommand) command).getNewData().getId(), ((UcpAttrCommand) command).getNewData());
    }
    getLogger().info("Created Ucp Attr count : {}", ret.size());
    return Response.ok(ret).build();
  }

  /**
   * Create a Ucp Attr record
   *
   * @param objList objects to create
   * @return Rest response, with created UcpAttr object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final Map<Long, UcpAttr> objList) throws IcdmException {
    Map<Long, UcpAttr> ucpAttrMap = new HashMap<>();

    List<AbstractSimpleCommand> ucpAttrCmdList = new ArrayList<>();

    for (UcpAttr ucpAttr : objList.values()) {
      UcpAttrCommand command = new UcpAttrCommand(getServiceData(), ucpAttr, true, false);
      ucpAttrCmdList.add(command);
    }
    executeCommand(ucpAttrCmdList);

    for (AbstractSimpleCommand command : ucpAttrCmdList) {
      ucpAttrMap.put(((UcpAttrCommand) command).getNewData().getId(), ((UcpAttrCommand) command).getNewData());
    }

    getLogger().info("Updated Ucp Attr count : {}", ucpAttrMap.size());
    return Response.ok(ucpAttrMap).build();
  }


  /**
   * Delete a Ucp Attr record
   *
   * @param objIdSet set of objects to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> objIdSet) throws IcdmException {
    UcpAttrLoader loader = new UcpAttrLoader(getServiceData());
    for (Long objId : objIdSet) {
      UcpAttr obj = loader.getDataObjectByID(objId);
      UcpAttrCommand cmd = new UcpAttrCommand(getServiceData(), obj, false, true);
      executeCommand(cmd);
    }
    return Response.ok().build();
  }

}
