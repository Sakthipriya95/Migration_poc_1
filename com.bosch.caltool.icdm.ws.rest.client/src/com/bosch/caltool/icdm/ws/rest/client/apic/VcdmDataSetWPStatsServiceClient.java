/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.VcdmDataSetWPStats;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class VcdmDataSetWPStatsServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public VcdmDataSetWPStatsServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_VCDM_DATASET_WP_STATS);
  }

  /**
   * @param pidcID PIDC ID
   * @param vcdmDstId vCDM DST ID
   * @param timePeriod time period
   * @return set of vCDM data sets
   * @throws ApicWebServiceException error from web service call
   */
  public Set<VcdmDataSetWPStats> getStatisticsByPidcId(final Long pidcID, final long vcdmDstId, final int timePeriod)
      throws ApicWebServiceException {
    LOGGER.debug("Loading VcdmDataSetWPStatsByPidcId for pidcID {}", pidcID);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DATA_BY_PIDCID)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcID).queryParam(WsCommonConstants.RWS_QP_DSTID, vcdmDstId)
        .queryParam(WsCommonConstants.RWS_QP_TIME_PERIOD, timePeriod);

    GenericType<Set<VcdmDataSetWPStats>> type = new GenericType<Set<VcdmDataSetWPStats>>() {};

    Set<VcdmDataSetWPStats> retSet = get(wsTarget, type);

    LOGGER.debug("VcdmDataSetWPStatsByPidcId data loaded. No. of records : {}", retSet.size());

    return retSet;
  }

}
