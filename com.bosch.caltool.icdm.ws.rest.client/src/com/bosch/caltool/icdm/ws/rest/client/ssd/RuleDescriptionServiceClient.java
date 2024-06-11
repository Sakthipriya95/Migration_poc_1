/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.ssd;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.ssd.OEMRuleDescriptionInput;
import com.bosch.caltool.icdm.model.ssd.RuleIdIcdmDescriptionModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dja7cob Service client to get functions based on search criteria
 */
public class RuleDescriptionServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public RuleDescriptionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_SSD, WsCommonConstants.RWS_CONTEXT_RULE_DESC);
  }


  /**
   * @param input Rule id List
   * @return the map of RuleIdIcdmDescriptionModel
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<String, RuleIdIcdmDescriptionModel> getRuleDescription(final List<OEMRuleDescriptionInput> input)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Map<String, RuleIdIcdmDescriptionModel>> type =
        new GenericType<Map<String, RuleIdIcdmDescriptionModel>>() {};
    return post(wsTarget, input, type);

  }
}
