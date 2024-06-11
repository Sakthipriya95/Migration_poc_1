/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributeUpdateExternalInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author mkl2cob
 */
public class ProjectAttributeUpdateExternalServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  protected ProjectAttributeUpdateExternalServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_ATTR_UPDATE_EXTERNAL);
  }

  /**
   * update project attribute external
   *
   * @param projAttrModelExt ProjectAttributeUpdationModelExt
   * @return String
   * @throws ApicWebServiceException exception in service
   */
  public String updateProjectAttributeExternal(final ProjectAttributeUpdateExternalInput projAttrModelExt)
      throws ApicWebServiceException {

    LOGGER.info("Updating attribute in project (External Service). Input : {}", projAttrModelExt);

    String successMsg = put(getWsBase(), projAttrModelExt, String.class);

    LOGGER.info("Response from service : {}", successMsg);

    return successMsg;
  }
}
