/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.comppkg;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

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
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.comppkg.CompPackageLoader;
import com.bosch.caltool.icdm.bo.comppkg.CompPkgCommand;
import com.bosch.caltool.icdm.bo.comppkg.CompPkgCreateCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamInput;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.model.comppkg.CompPkgResponse;
import com.bosch.caltool.icdm.model.comppkg.CompPkgRuleResponse;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_COMP + "/" + WsCommonConstants.RWS_COMP_PKG)
public class CompPkgService extends AbstractRestService {


  /**
   * Get all Comp Definitions
   *
   * @return Rest response
   * @throws IcdmException IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllCompPkg() throws IcdmException {

    // Create parameter properties loader object
    CompPackageLoader loader = new CompPackageLoader(getServiceData());

    // Fetch FC2WP mappings
    SortedSet<CompPackage> compPackages = loader.getAllCompPkg();

    WSObjectStore.getLogger().info("ComponentPkgService.getAllCompPkg() completed. Number of definitions = {}",
        compPackages.size());

    return Response.ok(compPackages).build();
  }

  /**
   * Create a TCompPkg record
   *
   * @param obj object to create
   * @return Rest response, with created CompPackage object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final CompPackage obj) throws IcdmException {
    CompPkgResponse compPkgResponse = new CompPkgResponse();
    compPkgResponse.setCompPackage(obj);
    compPkgResponse.setNodeAccess(new NodeAccess());
    CompPkgCreateCommand createCommand = new CompPkgCreateCommand(getServiceData(), compPkgResponse);
    //create the component package
    executeCommand(createCommand);
    getLogger().info("Created TCompPkgBc Id : {}", compPkgResponse.getCompPackage().getId());
    return Response.ok(compPkgResponse).build();
  }

  /**
   * Update a TCompPkg record
   *
   * @param obj object to update
   * @return Rest response, with updated CompPackage object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final CompPackage obj) throws IcdmException {
    CompPkgCommand cmd = new CompPkgCommand(getServiceData(), obj, true, false);
    //update the component package
    executeCommand(cmd);
    CompPackage ret = cmd.getNewData();
    getLogger().info("Updated TCompPkgBc Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param compPkgId to get the data object using ID
   * @return Rest response,with CompPackage object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_COMP_PKG_OBJ)
  @CompressData
  public Response getCompPackageById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long compPkgId)
      throws IcdmException {
    //Create parameter properties loader object
    CompPackageLoader compPkgLoader = new CompPackageLoader(getServiceData());
    // fetch the component package
    CompPackage compPkg = compPkgLoader.getDataObjectByID(compPkgId);
    getLogger().info(" CompPkgBc getCompPkgBc = {}", compPkg.getName());
    return Response.ok(compPkg).build();
  }

  /**
   * @param compPackage compPackage
   * @return ReviewRule Map
   * @throws IcdmException Exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_BY_SSD_NODE_ID)
  @CompressData
  public Map<String, List<ReviewRule>> getBySSDNodeId(final CompPackage compPackage) throws IcdmException {
    CompPackageLoader loader = new CompPackageLoader(getServiceData());
    return loader.getBySSDNodeId(compPackage);
  }

  /**
   * @param compPkgParamInput compPkgParamInput
   * @return CompPkgParameter Map
   * @throws IcdmException Exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_COMP_PKG_PARAM)
  @CompressData
  public Map<String, CompPkgParameter> getCompPkgParam(final CompPkgParamInput compPkgParamInput) throws IcdmException {
    ParameterLoader parameterLoader = new ParameterLoader(getServiceData());
    CompPackageLoader loader = new CompPackageLoader(getServiceData());
    return loader.getCompPkgParamMap(parameterLoader.getParamsByName(compPkgParamInput.getParamName(), null),
        compPkgParamInput.getCompPkgId());
  }

  /**
   * @param compPkgId compPkgId
   * @return CompPkgRuleResponse
   * @throws IcdmException Exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public CompPkgRuleResponse getCompPkgRule(@QueryParam(WsCommonConstants.RWS_QP_COMP_PKG_ID) final Long compPkgId)
      throws IcdmException {
    CompPackageLoader loader = new CompPackageLoader(getServiceData());
    //fetch the component package rule and return it
    return loader.getCompPkgRule(compPkgId);
  }
}
