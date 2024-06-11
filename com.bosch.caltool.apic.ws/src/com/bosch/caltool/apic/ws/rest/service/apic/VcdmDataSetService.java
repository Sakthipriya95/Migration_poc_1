/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetLoader;
import com.bosch.caltool.icdm.bo.cdr.VCDMInterface;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.icdm.model.apic.VcdmDataSet;
import com.bosch.caltool.icdm.model.cdr.VcdmHexFileData;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_VCDM_DATASET)
public class VcdmDataSetService extends AbstractRestService {

  /**
   * get VcdmDataSet for given PIDC ID
   *
   * @param pidcID     PIDC ID
   * @param timePeriod time period
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_DATA_BY_PIDCID)
  @CompressData
  public Response getStatisticsByPidcId(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcID,
      @QueryParam(value = WsCommonConstants.RWS_QP_TIME_PERIOD) final int timePeriod)
      throws IcdmException {

    // Create loader object
    VcdmDataSetLoader loader = new VcdmDataSetLoader(getServiceData());

    // get data sets of the given PIDC
    Set<VcdmDataSet> retSet = loader.getStatisticsByPidcId(pidcID, timePeriod);

    getLogger().info("VcdmDataSetService.getStatisticsByPidcId() completed. Item count = {}.", retSet.size());

    return Response.ok(retSet).build();
  }


  /**
   * get VcdmDataSet for given sdom info
   *
   * @param sdomPverName     sdom pver name
   * @param sdomPverVar      sdom pver variant
   * @param sdomPverRevision sdom pver revision
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_VCDM_DATASETS)
  @CompressData
  public Response getDataSet(@QueryParam(value = WsCommonConstants.RWS_QP_SDOM_PVER_NAME) final String sdomPverName,
      @QueryParam(value = WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT) final String sdomPverVar,
      @QueryParam(value = WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT_REV) final String sdomPverRevision)
      throws IcdmException {

    // Create loader object
    VcdmDataSetLoader loader = new VcdmDataSetLoader(getServiceData());

    // get data sets of the given PIDC
    List<VCDMApplicationProject> retSet = loader.getDataSets(sdomPverName, sdomPverVar, sdomPverRevision);

    getLogger().info("VcdmDataSetService.getDataSet() completed. Item count = {}.", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * load hex file based on vcdm data set version
   *
   * @param versNumber vernumber
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_LOAD_HEX_FILE)
  @CompressData
  public Response loadHexFile(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final long versNumber)
      throws IcdmException {

    final VCDMInterface serviceHandler = getVcdmInterfaceForSuperUser();
    VcdmHexFileData hexFileData = serviceHandler.loadHexFile(versNumber, null);

    return Response.ok(hexFileData).build();
  }

  /**
   * Gets the vcdm interface for super user.
   *
   * @return the vcdm interface for super user
   * @throws DataException
   */
  private VCDMInterface getVcdmInterfaceForSuperUser() throws DataException {
    VCDMInterface vCdmSuperUser = null;
    try {
      vCdmSuperUser = new VCDMInterface(EASEELogger.getInstance());
    }
    catch (Exception exp) {
      throw new DataException("VCDM Webservice login failed. Opening A2L Files is not possible. " + exp.getMessage(),
          exp);
    }
    return vCdmSuperUser;
  }
}
