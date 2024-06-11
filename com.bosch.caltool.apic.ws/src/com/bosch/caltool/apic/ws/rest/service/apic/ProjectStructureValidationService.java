/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.ProjectStructureValidator;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ProjectStructureModel;

/**
 * REST Service to check the Project Structure is valid
 *
 * @author hnu1cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_STRUCTURE_VALIDATION)
public class ProjectStructureValidationService extends AbstractRestService {

  /**
   * Validates the ids in ProjectStructureModel,<br>
   * returns string "valid" if the ids are valid, else throws exception
   *
   * @param inputModel StructureInputModel
   * @return Response , with the string "valid"
   * @throws IcdmException, If validation fails
   */
  @POST
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response validateProjectStructure(final ProjectStructureModel inputModel) throws IcdmException {
    getLogger().info("Validation of structure inputs started");

    new ProjectStructureValidator(getServiceData()).validateStructure(inputModel);

    getLogger().info("Validation of structure inputs completed");

    return Response.ok("valid").build();
  }


}
