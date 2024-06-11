/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeUpdateExternal;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributeUpdateExternalInput;

/**
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_ATTR_UPDATE_EXTERNAL)
public class ProjectAttributeUpdateExternalService extends AbstractRestService {

  /**
   * @param projAttrModelExt ProjectAttributeUpdationModelExt
   * @return Response
   * @throws IcdmException exception during update
   * @throws UnAuthorizedAccessException exception with access rights
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response updateProjectAttributeExternal(final ProjectAttributeUpdateExternalInput projAttrModelExt)
      throws IcdmException {
    getLogger().info("Project Attribute Updation External Service for {} - STARTED", projAttrModelExt);

    String message = new ProjectAttributeUpdateExternal(getServiceData()).update(projAttrModelExt);

    getLogger().info("Project Attribute Updation External Service - COMPLETED. Response: {}", message);

    return Response.ok(message).build();
  }

}
