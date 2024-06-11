/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.a2l.PidcA2lCommand;
import com.bosch.caltool.icdm.bo.apic.PidcA2lDetailsByAPRJLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.apic.PidcA2lDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.cdr.SSDProjectVersion;

/**
 * Rest service to insert the group params.
 *
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_CONTEXT_A2L)
public class PidcA2lService extends AbstractRestService {


  /**
   * Get PidcA2L by Id
   *
   * @param pidcA2lId PidcA2LId
   * @param httpRequest the http request
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getPidcA2LFileDetails(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @Context final HttpServletRequest httpRequest)
      throws IcdmException {

    getLogger().info("PidcA2lService.getPidcA2LFile() started. User Inputs : {} = {}; ",
        WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);

    // Fetch the review information from database
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    // fetch the details
    PidcA2lFileExt pidcA2lFile = loader.getPidcA2LDetails(pidcA2lId);

    // log the returned result details
    getLogger().info("PidcA2LService.getPidcA2LFile() completed.");

    return Response.ok(pidcA2lFile).build();
  }

  /**
   * Get PidcA2l using its id
   *
   * @param objId object's id
   * @return Rest response, with PidcA2l object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_QP_OBJ_ID)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    PidcA2l ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Get all PidcA2L by projectVersionID
   *
   * @param pidcVersionId pidcVersionId
   * @param sdomPverName SDOM PVER name
   * @return Rest response
   * @throws IcdmException data retreval error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PIDC_A2L_BY_PIDC_VERS_ID)
  @CompressData
  public Response getA2LFileBySdom(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersionId,
      @QueryParam(value = WsCommonConstants.RWS_QP_SDOM_PVER_NAME) final String sdomPverName)
      throws IcdmException {

    getLogger().info("PidcA2lService.getAllPidcA2LFile() started. User Inputs : {} = {}",
        WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersionId);

    // Fetch the review information from database
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    // fetch the details
    Map<Long, PidcA2l> pidcA2lFiles = loader.getAllBySdomPver(pidcVersionId, sdomPverName);

    // log the returned result details
    getLogger().info("PidcA2LService.getAllPidcA2LFile() completed. Number of pidcA2ls = {}", pidcA2lFiles.size());

    return Response.ok(pidcA2lFiles).build();
  }

  /**
   * Fet all pidc-a2l records for the given pidc id
   * 
   * @param pidcId PIDC ID
   * @return map of pidc a2ls
   * @throws IcdmException data retrieval error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_A2L_BY_PIDC_ID)
  @CompressData
  public Response getAllDetByPidc(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId)
      throws IcdmException {

    // validate null and invalid input
    new PidcLoader(getServiceData()).validateId(pidcId);

    Map<Long, PidcA2lFileExt> retMap = new PidcA2lLoader(getServiceData()).getAllDetByPidcId(pidcId);
    getLogger().info("PidcA2LService.getAllA2lByPidc() completed.Number of pidcA2ls = {};", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Checks if is pidc A 2 l present.
   *
   * @param pidcVersionId pidcVersionId
   * @param sdomPverName the sdom pver name
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PIDC_A2L_AVAILBLTY)
  @CompressData
  public Response isPidcA2lPresent(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersionId,
      @QueryParam(value = WsCommonConstants.RWS_QP_SDOM_PVER_NAME) final String sdomPverName)
      throws IcdmException {
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    return Response.ok(loader.isA2lForSdomPverPresent(pidcVersionId, sdomPverName)).build();
  }


  /**
   * Fetch A 2 l workpackage mapping.
   *
   * @param pidcA2lId PidcA2LId
   * @param httpRequest the http request
   * @return Rest response
   * @throws IcdmException the icdm exception
   * @deprecated not used
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_A2L_WP_MAPPING)
  @CompressData
  @Deprecated
  public Response fetchA2lWorkpackageMapping(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @Context final HttpServletRequest httpRequest)
      throws IcdmException {

    getLogger().info("PidcA2lService.getPidcA2LFile() started. User Inputs : {} = {}; ",
        WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);

    // Fetch the review information from database
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    // fetch the details
    A2lWpMapping pidcA2lFile = loader.getWorkpackageMapping(pidcA2lId);

    // log the returned result details
    getLogger().info("PidcA2LService.fetchA2lWorkpackageMapping() completed.");

    return Response.ok(pidcA2lFile).build();
  }


  /**
   * Resolve A 2 l wp resp.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @param httpRequest the http request
   * @return the response
   * @throws IcdmException the icdm exception
   * @deprecated not used
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_RESOLVE_A2L_WP_RESP)
  @CompressData
  @Deprecated
  public Response resolveA2lWpResp(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @Context final HttpServletRequest httpRequest)
      throws IcdmException {

    getLogger().info("PidcA2lService.resolveA2lWpResp() started. User Inputs : {} = {}; ",
        WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);

    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    // fetch the details
    A2lWpMapping a2lWpMapping = loader.resolveA2lRespWorkpackage(pidcA2lId);

    // log the returned result details
    getLogger().info("PidcA2LService.resolveA2lWpResp() completed.");

    return Response.ok(a2lWpMapping).build();
  }

  /**
   * @param pidcA2ls set of pidca2l
   * @return response
   * @throws IcdmException - exception during service call
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final Set<PidcA2l> pidcA2ls) throws IcdmException {

    List<AbstractSimpleCommand> pidcA2lCmdList = new ArrayList<>();

    for (PidcA2l pidcA2l : pidcA2ls) {
      PidcA2lCommand cmd = new PidcA2lCommand(getServiceData(), pidcA2l);
      pidcA2lCmdList.add(cmd);
    }
    executeCommand(pidcA2lCmdList);


    Set<PidcA2l> retSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : pidcA2lCmdList) {
      retSet.add(((PidcA2lCommand) cmd).getNewData());
    }
    return Response.ok(retSet).build();
  }

  /**
   * @param pidcA2ls to update
   * @return response
   * @throws IcdmException - exception during service call
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final Set<PidcA2l> pidcA2ls) throws IcdmException {
    List<AbstractSimpleCommand> pidcA2lCmdList = new ArrayList<>();
    for (PidcA2l pidcA2l : pidcA2ls) {

      PidcA2lCommand cmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, false, false);
      pidcA2lCmdList.add(cmd);

    }
    executeCommand(pidcA2lCmdList);

    Set<PidcA2l> retSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : pidcA2lCmdList) {
      retSet.add(((PidcA2lCommand) cmd).getNewData());
    }
    getLogger().info("Updating pidca2l is completed");
    return Response.ok(retSet).build();
  }


  /**
   * @param swProjNodeId swProjNodeId
   * @return set of SSDProjectVersion
   * @throws IcdmException Exception during service call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_SSD_PROJECT_VERSIONS)
  @CompressData
  public Response getSSDProjectVersion(
      @QueryParam(value = WsCommonConstants.RWS_QP_SW_PROJ_NODE_ID) final Long swProjNodeId)
      throws IcdmException {

    SortedSet<SSDProjectVersion> swVersionSet =
        new SSDServiceHandler(getServiceData()).getSwVersionListBySwProjId(swProjNodeId);

    return Response.ok(swVersionSet).build();
  }

  /**
   * @param pidcA2lId Selected pidc A2l Id
   * @return boolean value
   * @throws IcdmException exception
   */
  @POST
  @Path(WsCommonConstants.RWS_GET_PIDC_A2L_ASSIGN_VALIDATION)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getPidcA2lAssignmentValidation(final Set<Long> pidcA2lId) throws IcdmException {
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    boolean a2lWpRespMapValidation = loader.getPidcA2lAssignmentValidation(pidcA2lId);
    return Response.ok(a2lWpRespMapValidation).build();
  }

  /**
   * Fetch the project A2l details of the given project using APRJ name and variant name
   *
   * @param aprjName - aprj name
   * @param variantName - pver variant name
   * @param vcdmA2lFileId - vcdm a2l file id
   * @return data checker model map
   * @throws IcdmException - exception while invoking service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_A2L_DETAILS_BY_APRJ)
  @CompressData
  public Response getPidcDetailsByAPRJInfo(
      @QueryParam(value = WsCommonConstants.RWS_QP_APRJ_NAME) final String aprjName,
      @QueryParam(value = WsCommonConstants.RWS_QP_APRJ_VARIANT) final String variantName,
      @QueryParam(WsCommonConstants.RWS_QP_VCDM_A2LFILE_ID) final Long vcdmA2lFileId)
      throws IcdmException {

    PidcA2lDetailsByAPRJLoader loader = new PidcA2lDetailsByAPRJLoader(getServiceData());
    Map<Long, PidcA2lDetails> retMap = loader.getPidcDetails(aprjName, variantName, vcdmA2lFileId);
    return Response.ok(retMap).build();
  }

}
