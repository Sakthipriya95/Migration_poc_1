/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ParamAttrServiceClient extends AbstractRestServiceClient
    implements IParamAttrServiceClient<ParameterAttribute> {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public ParamAttrServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_PARAM_ATTR);

  }


  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return created Attribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  @Override
  public ParameterAttribute create(final ParameterAttribute paramAttr) throws ApicWebServiceException {
    return create(getWsBase(), paramAttr);
  }


  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return
   * @return created Attribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  @Override
  public void delete(final ParameterAttribute paramAttr) throws ApicWebServiceException {
    delete(getWsBase(), paramAttr);
  }


}
