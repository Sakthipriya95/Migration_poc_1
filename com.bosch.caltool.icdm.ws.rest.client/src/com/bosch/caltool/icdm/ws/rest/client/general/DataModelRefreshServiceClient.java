/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 * @deprecated not used
 */
@Deprecated
public class DataModelRefreshServiceClient extends AbstractRestServiceClient {

  /**
   * Instantiates a new data model refresh service client.
   */
  public DataModelRefreshServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_SYS, WsCommonConstants.RWS_CQN_STATE);
  }

  /**
   * Get the status of Collective Query Notification(CQN) based refresh in web service server
   *
   * @return <code>OK</code> if refresh is running fine, else <code>ERROR</code>
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public String getCqnRefreshState() throws ApicWebServiceException {
    return get(getWsBase(), String.class);
  }
}