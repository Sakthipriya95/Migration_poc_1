/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.ProjectStructureModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author hnu1cob
 */
public class ProjectStructureValidationServiceClient extends AbstractRestServiceClient {


  /**
   * New instance of client
   */
  public ProjectStructureValidationServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_STRUCTURE_VALIDATION);
  }

  /**
   * @param inputModel ProjectHierarchyInput
   * @return response, with the string "valid", if the project structire is valid
   * @throws ApicWebServiceException error from service
   */
  public String validateProjectStructure(final ProjectStructureModel inputModel) throws ApicWebServiceException {
    LOGGER.debug("Validating of Project Structure. Input Details: {} ", inputModel);

    String response = post(getWsBase(), inputModel, String.class);

    LOGGER.debug("Validation of Project Structure completed.");
    return response;
  }

}
