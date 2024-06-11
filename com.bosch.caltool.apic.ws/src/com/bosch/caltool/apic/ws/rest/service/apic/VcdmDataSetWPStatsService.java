/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

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
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetWPStatsLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.VcdmDataSetWPStats;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_VCDM_DATASET_WP_STATS)
public class VcdmDataSetWPStatsService extends AbstractRestService {

  /**
   * get VcdmDataSetWPStats for given PIDC ID
   *
   * @param pidcID PIDC ID. Mandatory
   * @param vcdmDstId vcdm DST ID. Optional
   * @param timePeriod time period. Optional
   * @return Rest response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_DATA_BY_PIDCID)
  @CompressData
  public Response getStatisticsByPidcId(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcID,
      @QueryParam(value = WsCommonConstants.RWS_QP_DSTID) final long vcdmDstId,
      @QueryParam(value = WsCommonConstants.RWS_QP_TIME_PERIOD) final int timePeriod) throws IcdmException {

    // Create loader object
    VcdmDataSetWPStatsLoader loader = new VcdmDataSetWPStatsLoader(getServiceData());

    // get data sets of the given PIDC
    Set<VcdmDataSetWPStats> retSet = loader.getStatisticsByPidcId(pidcID, vcdmDstId, timePeriod);

    getLogger().info("VcdmDataSetWPStatsService.getStatisticsByPidcId() completed. Item count = {}.", retSet.size());

    return Response.ok(retSet).build();
  }

}
