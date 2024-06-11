/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.ssd;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class SSDReleaseServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public SSDReleaseServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CONTEXT_SSD_RELEASE);
  }

  /**
   * @param swVersionId software version Id
   * @return set of SSD Release
   * @throws ApicWebServiceException Exception
   */
  public List<SSDReleaseIcdmModel> getSSDReleaseList(final Long swVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SSD_RELEASE)
        .queryParam(WsCommonConstants.RWS_QP_SW_VERSION_ID, swVersionId);
    GenericType<List<SSDReleaseIcdmModel>> type = new GenericType<List<SSDReleaseIcdmModel>>() {};
    return get(wsTarget, type);
  }


  /**
   * @param dependencyList List of FeatureValueModel
   * @return SSDFeatureICDMAttrModel list
   * @throws ApicWebServiceException Exception
   */
  public List<SSDFeatureICDMAttrModel> getSSDFeatureICDMAttrModelList(final List<FeatureValueICDMModel> dependencyList)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SSD_FEATURE_ATTR_MODEL);
    GenericType<List<SSDFeatureICDMAttrModel>> type = new GenericType<List<SSDFeatureICDMAttrModel>>() {};
    return post(wsTarget, dependencyList, type);
  }

}
