/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.ProjectAttributesUpdationCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionWithDetailsHandler;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;

/**
 * Rest service for updating project attrs
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_ATTRS_CREATION)
public class ProjectAttributesUpdationService extends AbstractRestService {


  /**
   * @param inputModel ProjectAttributesUpdationModel
   * @return setOfUpdatedAttrs objects with details
   * @throws IcdmException any error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response updatePidcAttrs(final ProjectAttributesUpdationModel inputModel) throws IcdmException {

    if ((inputModel == null) || (inputModel.getPidcVersion() == null)) {
      throw new InvalidInputException("Invalid input for project attributes update.");
    }

    new PidcVersionLoader(getServiceData()).validateId(inputModel.getPidcVersion().getId());

    PidcVersionAttributeModel beforeUpdateModel =
        new ProjectAttributeLoader(getServiceData()).createModel(inputModel.getPidcVersion().getId());

    ProjectAttributesUpdationCommand mainCmd = new ProjectAttributesUpdationCommand(getServiceData(), inputModel);
    executeCommand(mainCmd);


    PidcVersionAttributeModel afterUpdateModel =
        new ProjectAttributeLoader(getServiceData()).createModel(inputModel.getPidcVersion().getId());
    PidcVersionWithDetailsHandler handler = new PidcVersionWithDetailsHandler(getServiceData());
    ProjectAttributesUpdationModel updatedModel = handler.getChanges(afterUpdateModel, beforeUpdateModel, inputModel);

    return Response.ok(updatedModel).build();
  }

}
