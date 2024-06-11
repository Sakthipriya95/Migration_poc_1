/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.AliasDefLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationDetails;

/**
 * Rest service for Pidc creation
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_CREATION)
public class PidcCreationService extends AbstractRestService {

  /**
   * Get existing Pidc names
   *
   * @param projNameAttrLvl proj name attr level
   * @return Rest response, with AttributeValue object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_PIDC_CREATION_DETAILS)
  @CompressData
  public Response getExistingPidcNames(
      @QueryParam(WsCommonConstants.RWS_QP_PROJ_NAME_ATTR_LVL) final int projNameAttrLvl) throws IcdmException {
    PidcCreationDetails createPidcDetails = new PidcCreationDetails();
    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
    // Load all the existing PIDC names
    Set<String> existingPidcs = loader.getExistingPidcNames(projNameAttrLvl);
    createPidcDetails.setExistingPidcNames(existingPidcs);
    AliasDefLoader aliasLoader = new AliasDefLoader(getServiceData());
    // Load all alias definitions
    createPidcDetails.setAliasDefMap(aliasLoader.getAllByName());
    return Response.ok(createPidcDetails).build();
  }
}
