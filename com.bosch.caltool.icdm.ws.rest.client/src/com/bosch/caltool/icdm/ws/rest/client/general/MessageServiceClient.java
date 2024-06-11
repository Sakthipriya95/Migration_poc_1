/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class MessageServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public MessageServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_MESSAGE);
  }

  /**
   * Gets all messages.
   *
   * @return Map of messages. <br>
   *         Key - Message ID, combination of group name and obj name.<br>
   *         Value - message
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<String, String> getAll() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);

    GenericType<Map<String, String>> messages = new GenericType<Map<String, String>>(){};
    return get(wsTarget, messages);
  }
}
