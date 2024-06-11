/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;
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
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSdomPverLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_SDOM_PVER)
public class SdomPverService extends AbstractRestService {

  /**
   * Get all PVER names in the system
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PVER_NAMES)
  @CompressData
  public Response getAllPverNames() throws IcdmException {

    // Create loader object
    SdomPverLoader loader = new SdomPverLoader(getServiceData());
    SortedSet<String> retSet = loader.getAllPverNames();

    getLogger().info("SdomPverService.getAllPverNames() completed. Number of PVERs = {}", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * Get all PVER Variants, for the given input
   *
   * @param pver PVER name
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PVER_VARS)
  @CompressData
  public Response getPverVariantNames(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String pver)
      throws IcdmException {

    // Create loader object
    SdomPverLoader loader = new SdomPverLoader(getServiceData());
    SortedSet<String> retSet = loader.getPverVariantNames(pver);

    getLogger().info("SdomPverService.getPverVariantNames() completed. Number of variants = {}", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * Get all PVER Variant versions, for the given inputs
   *
   * @param pver PVER name
   * @param pverVariant PVER variant
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PVER_VAR_VERSIONS)
  @CompressData
  public Response getPverVariantVersions(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String pver,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT) final String pverVariant)
      throws IcdmException {

    // Create loader object
    SdomPverLoader loader = new SdomPverLoader(getServiceData());
    SortedSet<Long> retSet = loader.getPverVariantVersions(pver, pverVariant);

    getLogger().info("SdomPverService.getPverVariantVersions() completed. Number of versions = {}", retSet.size());

    return Response.ok(retSet).build();
  }


  /**
   * @param pidcVersionId pidcversionid
   * @return the set of pver names associated with a pidcversion
   * @throws IcdmException - error during web service call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_SDOM_FOR_PIDC_VERSION)
  @CompressData
  public Response getAllPverNamesForPidcVersion(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersionId)
      throws IcdmException {

    SortedSet<String> retSet = new PidcSdomPverLoader(getServiceData()).getSdomPverNamesByPidcVers(pidcVersionId);
    getLogger().info("Number of sdom pvers found = {}", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * Get SDOM PVERs by PIDC
   *
   * @param pidcId PIDC ID
   * @return the Map of pver names associated with a PIDC. Key - pidc version ID, value set of SDOM-PVER names
   * @throws IcdmException - error during web service call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_SDOMPVER_NAMES_BY_PIDC)
  @CompressData
  public Response getSdomPverByPidc(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId)
      throws IcdmException {

    Map<Long, SortedSet<String>> pidcSdomMap = new PidcSdomPverLoader(getServiceData()).getSdomPverByPidc(pidcId);
    getLogger().info("PIDC : {} - PIDC Versions in response = {}", pidcId, pidcSdomMap.size());

    return Response.ok(pidcSdomMap).build();
  }

  /**
   * Get the SDOM PVER for the given PIDC/Variant
   *
   * @param pidcVersionId PIDC Version ID
   * @param pidcVariantId Variant ID
   * @return SDOM PVER name
   * @throws IcdmException error while retrieving data
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_SDOM_PVER)
  @CompressData
  public Response getSDOMPverName(@QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VARIANT_ID) final Long pidcVariantId)
      throws IcdmException {

    return Response.ok(new PidcSdomPverLoader(getServiceData()).getSdomPverName(pidcVersionId, pidcVariantId)).build();
  }
}
