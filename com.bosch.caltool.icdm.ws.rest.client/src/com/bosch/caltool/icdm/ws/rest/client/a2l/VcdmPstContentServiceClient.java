/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for VcdmPstContent.
 *
 * @author bne4cob
 */
public class VcdmPstContentServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor.
   */
  public VcdmPstContentServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_VCDM_PST_CONTENT);
  }

  /**
   * Get VcdmPstContent records for the given A2L object ID
   *
   * @param a2lId A2l File Id
   * @return Map of VcdmPstContent
   * @throws ApicWebServiceException error in service call
   */
  public Set<VcdmPstContent> getVcdmPstContentsForA2l(final Long a2lId) throws ApicWebServiceException {
    LOGGER.debug("Get VcdmPstContent records for A2L object ID - {}", a2lId);
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_BY_A2L).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, a2lId);
    Set<VcdmPstContent> retSet = get(wsTarget, new GenericType<Set<VcdmPstContent>>() {});
    LOGGER.debug("Get VcdmPstContent records loaded. count = {}", retSet.size());
    return retSet;
  }
}
