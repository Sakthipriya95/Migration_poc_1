/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PidcChangeHistory
 *
 * @author dmr1cob
 */
public class PidcChangeHistoryServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PidcChangeHistoryServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCCHANGEHISTORY);
  }

  public PidcDiffsResponseType getPidcDiffs(final PidcDiffsType pidcDiff) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_DIFFS);
    GenericType<PidcDiffsResponseType> type = new GenericType<PidcDiffsResponseType>() {};
    return post(wsTarget, pidcDiff, type);
  }

  public List<AttrDiffType> getPidcAttrDiffForVersion(final PidcDiffsForVersType allPidcDiffsForVersType)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_ATTR_DIFF_FOR_VERSION);
    GenericType<List<AttrDiffType>> type = new GenericType<List<AttrDiffType>>() {};
    return post(wsTarget, allPidcDiffsForVersType, type);
  }

  public PidcDiffsResponseType getAllPidcDiffForVersion(final PidcDiffsForVersType allPidcDiffsForVersType)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_DIFF_FOR_VERSION);
    GenericType<PidcDiffsResponseType> type = new GenericType<PidcDiffsResponseType>() {};
    return post(wsTarget, allPidcDiffsForVersType, type);
  }
}
