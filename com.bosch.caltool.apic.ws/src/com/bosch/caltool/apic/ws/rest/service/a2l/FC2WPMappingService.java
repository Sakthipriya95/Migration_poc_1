/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPMappingCommand;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPMappingLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.RvwFuncDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;


/**
 * Get the FC2WP mapping
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FC2WP_MAPPING)
public class FC2WPMappingService extends AbstractRestService {

  /**
   * Get the FC2WP mapping in the given FC2WP Definition version
   *
   * @param fc2wpVersID FC2WP Mapping version
   * @return Rest response
   * @throws IcdmException any execution error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_VERSION)
  @CompressData
  public Response getFC2WPMappingForVersion(
      @QueryParam(value = WsCommonConstants.RWS_QP_FC2WP_VERS_ID) final Long fc2wpVersID)
      throws IcdmException {

    WSObjectStore.getLogger().info("FC2WPMappingService started. User Inputs : fc2wpVersID = {}", fc2wpVersID);

    // Create parameter properties loader object
    FC2WPMappingLoader loader = new FC2WPMappingLoader(getServiceData());

    // Fetch FC2WP mappings
    FC2WPMappingWithDetails ret = loader.getFC2WPMapping(fc2wpVersID);

    // Remove ent manager close here

    WSObjectStore.getLogger().info("FC2WPMappingService completed. Number of mappings = {}",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();

  }

  /**
   * Get the FC2WP mapping in the given FC2WP Definition version
   *
   * @param fc2wpMappingID FC2WP Mapping ID
   * @return Rest response
   * @throws IcdmException any execution error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_ID)
  @CompressData
  public Response getFC2WPMappingByID(
      @QueryParam(value = WsCommonConstants.RWS_QP_FC2WP_MAPPING_ID) final Long fc2wpMappingID)
      throws IcdmException {

    WSObjectStore.getLogger().info("getFC2WPMappingByID() started. User Inputs : fc2wpMappingID = {}", fc2wpMappingID);

    // Create parameter properties loader object
    FC2WPMappingLoader loader = new FC2WPMappingLoader(getServiceData());

    // Fetch FC2WP mapping
    FC2WPMappingWithDetails ret = loader.getDataObjectType2ByID(fc2wpMappingID);

    // Remove ent maneger close here

    WSObjectStore.getLogger().info("getFC2WPMappingByID() completed. Number of mappings = {}",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();

  }

  /**
   * Modify multiple FC2WP mapping records
   *
   * @param mappingList FC2WPMappingWithDetails update
   * @return Rest response
   * @throws IcdmException any execution error
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response updateFC2WPMapping(final List<FC2WPMapping> mappingList) throws IcdmException {

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    Set<Long> mappingIdSet = new HashSet<>();
    for (FC2WPMapping fc2wpMapping : mappingList) {
      FC2WPMappingCommand cmd = new FC2WPMappingCommand(getServiceData(), fc2wpMapping, true);
      cmdList.add(cmd);
      mappingIdSet.add(fc2wpMapping.getId());
    }
    executeCommand(cmdList);

    FC2WPMappingWithDetails ret = (new FC2WPMappingLoader(getServiceData())).getDataObjectType2ByID(mappingIdSet);

    WSObjectStore.getLogger().info("FC2WPMappingService.updateFC2WPMapping completed. Number of mappings = {}",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();
  }

  /**
   * Find FC to WP mappings for the given 'PIDC A2L ID'
   *
   * @param pidcA2LId PIDC A2L ID
   * @return FC2WPVersMapping
   * @throws IcdmException any exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_PIDC_A2L)
  @CompressData
  public Response findByPidcA2lId(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2LId)
      throws IcdmException {

    WSObjectStore.getLogger().info("FC2WPMappingService.findByPidcA2lId() started. User Inputs : pidcA2LId = {}",
        pidcA2LId);

    // Create parameter properties loader object
    FC2WPMappingLoader loader = new FC2WPMappingLoader(getServiceData());

    // Fetch FC2WP mapping
    FC2WPMappingWithDetails ret = loader.findByPidcA2lId(pidcA2LId);

    WSObjectStore.getLogger().info("FC2WPMappingService.findByPidcA2lId() completed. Number of mappings = {}",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();
  }

  /**
   * Find FC2WP mapping for the given A2L, FC2WP name and division
   *
   * @param a2lId a2l file Id
   * @param divValId Division ID
   * @param fc2wpName fc2wpName of FC2WP definition
   * @return FC2WPVersMapping
   * @throws IcdmException error from service call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_FC2WP_MAPPING_BY_A2L_N_NAME_N_DIV)
  @CompressData
  public Response findByA2lId(@QueryParam(value = WsCommonConstants.RWS_QP_A2L_FILE_ID) final Long a2lId,
      @QueryParam(value = WsCommonConstants.RWS_QP_DIV_VALUE_ID) final Long divValId,
      @QueryParam(value = WsCommonConstants.RWS_QP_FC2_WP_NAME) final String fc2wpName)
      throws IcdmException {

    WSObjectStore.getLogger().info(
        "FC2WPMappingService.findByA2lId() started. User Inputs : a2lId = {}; divValId = {}; nameValId = {}", a2lId,
        divValId, fc2wpName);

    // Create parameter properties loader object
    FC2WPMappingLoader loader = new FC2WPMappingLoader(getServiceData());


    // Fetch FC2WP mapping
    FC2WPMappingWithDetails ret = loader.findByA2LId(a2lId, divValId, fc2wpName);

    WSObjectStore.getLogger().info("FC2WPMappingService.findByA2lId() completed. Number of mappings = {}",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();
  }

  /**
   * @param rvwFuncDetails Model to hold Division Id & list of functions used in data review
   * @return FC2WPVersMapping
   * @throws IcdmException error from service call
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_Q_FC2WP_MAPPING_BY_DIV_ID)
  @CompressData
  public Response getQFC2WPMappingByDivId(final RvwFuncDetails rvwFuncDetails) throws IcdmException {

    WSObjectStore.getLogger().info("FC2WPMappingService.getQFC2WPMappingByDivId() started. User Inputs : divValId = {}",
        rvwFuncDetails.getDivId());

    // Create parameter properties loader object
    FC2WPMappingLoader loader = new FC2WPMappingLoader(getServiceData());

    // Fetch FC2WP mapping
    FC2WPMappingWithDetails ret = loader.getQFc2WpMappingByDivId(rvwFuncDetails);

    WSObjectStore.getLogger().info("FC2WPMappingService.getQFC2WPMappingByDivId() completed. Number of mappings = {}",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();
  }

  /**
   * @param newFc2WpMappingList list of new mappings
   * @return mapping objects with details
   * @throws IcdmException any error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createFC2WPMapping(final List<FC2WPMapping> newFc2WpMappingList) throws IcdmException {

    ServiceData servData = getServiceData();

    // Create data
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    for (FC2WPMapping fc2wpMapping : newFc2WpMappingList) {
      FC2WPMappingCommand cmd = new FC2WPMappingCommand(servData, fc2wpMapping);
      cmdList.add(cmd);
    }
    executeCommand(cmdList);

    // Retrieve the new objects and create response
    Set<Long> newMappingSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : cmdList) {
      newMappingSet.add(((FC2WPMappingCommand) cmd).getObjId());
    }
    FC2WPMappingLoader fwLoader = new FC2WPMappingLoader(servData);
    FC2WPMappingWithDetails ret = fwLoader.getDataObjectType2ByID(newMappingSet);

    WSObjectStore.getLogger().info("FC2WMappingService.createFC2WPMapping completed. Record count = {} ",
        ret.getFc2wpMappingMap().size());

    return Response.ok(ret).build();

  }
}
