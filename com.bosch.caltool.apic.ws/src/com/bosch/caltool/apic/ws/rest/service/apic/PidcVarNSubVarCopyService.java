/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVarNSubVarCopyCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;

/**
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_VARNSUBVAR_COPY)
public class PidcVarNSubVarCopyService extends AbstractRestService {

  /**
   * Create a Project variant record
   *
   * @param pidcVariantData object to create
   * @return Rest response, with created ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final PidcVariantData pidcVariantData) throws IcdmException {
    PidcVarNSubVarCopyCommand pidcVarNSubVarCopyCommand =
        new PidcVarNSubVarCopyCommand(getServiceData(), pidcVariantData, true);
    executeCommand(pidcVarNSubVarCopyCommand);
    PidcVarPasteOutput output = new PidcVarPasteOutput();
    PidcVariant copiedVar = pidcVarNSubVarCopyCommand.getDestVariant();
    output.setValAlreadyExists(pidcVarNSubVarCopyCommand.getValAlreadyExistsForVariant());
    output.setValAlreadyExistsForSubVarMap(pidcVarNSubVarCopyCommand.getValAlreadyExistsForSubVarMap());
    output.setPastedVariant(copiedVar);

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    output.setPidcVersion(pidcVersionLoader.getDataObjectByID(copiedVar.getPidcVersionId()));

    getLogger().info("Copied Project variant Id : {}", copiedVar.getId());
    return Response.ok(output).build();
  }

  /**
   * Update a Project variant record
   *
   * @param pidcVariantData object to update
   * @return Rest response, with updated ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final PidcVariantData pidcVariantData) throws IcdmException {
    PidcVarNSubVarCopyCommand pidcVarNSubVarCopyCommand =
        new PidcVarNSubVarCopyCommand(getServiceData(), pidcVariantData, false);
    executeCommand(pidcVarNSubVarCopyCommand);
    PidcVarPasteOutput output = new PidcVarPasteOutput();
    PidcVariant copiedVar = pidcVarNSubVarCopyCommand.getDestVariant();
    output.setValAlreadyExists(pidcVarNSubVarCopyCommand.getValAlreadyExistsForVariant());
    output.setValAlreadyExistsForSubVarMap(pidcVarNSubVarCopyCommand.getValAlreadyExistsForSubVarMap());
    output.setPastedVariant(copiedVar);

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    output.setPidcVersion(pidcVersionLoader.getDataObjectByID(copiedVar.getPidcVersionId()));

    getLogger().info("Copied Project variant Id : {}", copiedVar.getId());
    return Response.ok(output).build();
  }

}
