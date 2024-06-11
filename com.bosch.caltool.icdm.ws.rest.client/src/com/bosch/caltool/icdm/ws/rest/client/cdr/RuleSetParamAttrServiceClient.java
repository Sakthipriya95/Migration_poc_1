/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.IParamAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class RuleSetParamAttrServiceClient extends AbstractRestServiceClient
    implements IParamAttrServiceClient<RuleSetParameterAttr> {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public RuleSetParamAttrServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_RULEST_PARAM_ATTR);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuleSetParameterAttr create(final RuleSetParameterAttr rulesetParamAttr) throws ApicWebServiceException {
    return create(getWsBase(), rulesetParamAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(final RuleSetParameterAttr rulesetParamAttr) throws ApicWebServiceException {
    delete(getWsBase(), rulesetParamAttr);

  }

}
