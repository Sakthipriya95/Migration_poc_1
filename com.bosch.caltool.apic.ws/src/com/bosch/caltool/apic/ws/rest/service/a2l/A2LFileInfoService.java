/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.caltool.icdm.model.a2l.VCDMA2LFileDetail;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;

/**
 * Rest service to insert the group params.
 *
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FILE_INFO)
public class A2LFileInfoService extends AbstractRestService {


  /**
   * Get byte array of serialized a2l file based on A2L file Id.
   *
   * @param a2lFileId a2l file id
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Path(WsCommonConstants.RWS_SERIALIZED_A2L_FILE_INFO)
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response getA2LFileInfoSerialized(
      @QueryParam(value = WsCommonConstants.RWS_QP_A2L_FILE_ID) final Long a2lFileId)
      throws IcdmException {

    byte[] ret = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfoSerialized(a2lFileId);
    return Response.ok(ret).build();
  }

  /**
   * Gets A2L system constants.
   *
   * @return A2L system constants
   * @throws IcdmException the icdm exception
   */
  @GET
  @Path(WsCommonConstants.RWS_GET_A2L_SYS_CONSTANTS)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getA2LSystemConstants() throws IcdmException {

    Map<String, A2LSystemConstant> model = new A2LFileInfoLoader(getServiceData()).getAllA2lSystemConstants();
    getLogger().info("A2lFileInfoService.getA2LSystemConstants() completed. Number of system constants = {}",
        model.size());

    return Response.ok(model).build();
  }


  /**
   * Get fcbcusage for bc.
   *
   * @param bcName name bc
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_FCBC_USAGE_BY_BC)
  @CompressData
  public Response getBCUsage(@QueryParam(value = WsCommonConstants.RWS_QP_BC_NAME) final String bcName)
      throws IcdmException {

    getLogger().info("A2lFileInfoService.getBCUsage started. User Inputs : bcName = {}", bcName);

    // Fetch fcbcUsage for bc
    List<FCBCUsage> retMap = new A2LFileInfoLoader(getServiceData()).getBCUsage(bcName);
    getLogger().info("A2lFileInfoService.getBCUsage completed. Number of records = {}", retMap.size());

    return Response.ok(retMap).build();

  }


  /**
   * Get all PVER Variants, for the given input
   *
   * @param sdomPverName PVER name
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PVER_VARS)
  @CompressData
  public Response getA2LFilePVERVars(
      @QueryParam(value = WsCommonConstants.RWS_QP_SDOM_PVER_NAME) final String sdomPverName)
      throws IcdmException {

    SortedSet<String> retSet = new A2LFileInfoLoader(getServiceData()).getA2LFilePVERVars(sdomPverName);
    getLogger().info("A2lFileInfoService.getA2LFilePVERVars() completed. Number of variants = {}", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * Get fcbcusage for fc.
   *
   * @param fcName name fc
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_FCBC_USAGE_BY_FC)
  @CompressData
  public Response getFCUsage(@QueryParam(value = WsCommonConstants.RWS_QP_FC_NAME) final String fcName)
      throws IcdmException {

    getLogger().info("A2lFileInfoService.getFCUsage started. User Inputs : fcName = {}", fcName);

    // Fetch fcbcUsage for bc
    List<FCBCUsage> retMap = new A2LFileInfoLoader(getServiceData()).getFCUsage(fcName);
    getLogger().info("A2lFileInfoService.getFCUsage completed. Number of records = {}", retMap.size());

    return Response.ok(retMap).build();

  }

  /**
   * Gets the vcdm data sets.
   *
   * @param vcdmA2lFileID long
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_VCDM_DATASETS)
  @CompressData
  public Response getVcdmDataSets(
      @QueryParam(value = WsCommonConstants.RWS_QP_VCDM_A2LFILE_ID) final Long vcdmA2lFileID)
      throws IcdmException {

    List<VCDMApplicationProject> retList = new A2LFileInfoLoader(getServiceData()).getDataSets(vcdmA2lFileID);
    getLogger().info("getVcdmDataSets service completed. Number of records = {}", retList.size());

    return Response.ok(retList).build();

  }

  /**
   * Gets the A2L base components.
   *
   * @param a2lFileID the A2L file ID
   * @return the A2L base components
   * @throws IcdmException the icdm exception
   */
  @GET
  @Path(WsCommonConstants.RWS_GET_A2L_BASE_COMP)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getA2LBaseComponents(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long a2lFileID)
      throws IcdmException {

    Map<String, A2LBaseComponents> model = new A2LFileInfoLoader(getServiceData()).getA2lBaseComponents(a2lFileID);
    getLogger().info("A2lFileInfoService.getA2LBaseComponents() completed. Number of items = {}", model.size());

    return Response.ok(model).build();
  }

  /**
   * Gets the VCDM A2L file details.
   *
   * @param a2lFileCheckSum the A2L file check sum
   * @return the VCDM A2L file details
   * @throws IcdmException the icdm exception
   */
  @GET
  @Path(WsCommonConstants.RWS_GET_VCDM_A2L_FILE_DETAIL)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getVCDMA2LFileDetails(
      @QueryParam(value = WsCommonConstants.RWS_QP_A2L_FILE_CHECKSUM) final String a2lFileCheckSum)
      throws IcdmException {

    Set<VCDMA2LFileDetail> retSet = new A2LFileInfoLoader(getServiceData()).getVCDMA2LFileDetails(a2lFileCheckSum);
    getLogger().info("A2lFileInfoService.getVCDMA2LFileDetails() completed. Number of items = {}", retSet.size());

    return Response.ok(retSet).build();
  }
}
