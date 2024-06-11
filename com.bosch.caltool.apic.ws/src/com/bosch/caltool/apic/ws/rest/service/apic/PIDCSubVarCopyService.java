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
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVarCopyCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_SUB_VAR_COPY)
public class PIDCSubVarCopyService extends AbstractRestService {

  /**
   * Create a Project variant record
   *
   * @param obj object to create
   * @return Rest response, with created ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final PidcSubVariantData obj) throws IcdmException {
    PidcSubVarCopyCommand pidcVarCopyCommand = new PidcSubVarCopyCommand(getServiceData(), obj, true);
    PidcVariantLoader pidcvarLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVariant pidcVar = pidcvarLoader.getDataObjectByID(obj.getPidcVariantId());
    PidcSubVarPasteOutput output = new PidcSubVarPasteOutput();
    output.setPidcVarBeforeUpdate(pidcVar);
    PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    output.setPidcVersBeforeUpdate(pidcVersion);

    executeCommand(pidcVarCopyCommand);

    PidcSubVariant copiedVar = pidcVarCopyCommand.getDestVariant();
    output.setValAlreadyExists(pidcVarCopyCommand.getValAlreadyExists());
    output.setPastedSubVariant(copiedVar);
    pidcVar = pidcvarLoader.getDataObjectByID(obj.getPidcVariantId());
    output.setPidcVarAfterUpdate(pidcVar);
    pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    output.setPidcVersAfterUpdate(pidcVersion);
    getLogger().info("Copied Project variant Id : {}", copiedVar.getId());
    return Response.ok(output).build();
  }

  /**
   * Update a Project variant record
   *
   * @param obj object to update
   * @return Rest response, with updated ProjectVariant object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final PidcSubVariantData obj) throws IcdmException {
    PidcSubVarCopyCommand pidcVarCopyCommand = new PidcSubVarCopyCommand(getServiceData(), obj, false);

    PidcVariantLoader pidcvarLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVariant pidcVar = pidcvarLoader.getDataObjectByID(obj.getDestPidcSubVar().getPidcVariantId());

    PidcSubVarPasteOutput output = new PidcSubVarPasteOutput();
    output.setPidcSubVarBeforeUpdate(obj.getDestPidcSubVar());
    output.setPidcVarBeforeUpdate(pidcVar);
    PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    output.setPidcVersBeforeUpdate(pidcVersion);

    executeCommand(pidcVarCopyCommand);

    PidcSubVariant copiedSubVar = pidcVarCopyCommand.getDestVariant();
    pidcVar = pidcvarLoader.getDataObjectByID(obj.getDestPidcSubVar().getPidcVariantId());
    output.setPidcVarAfterUpdate(pidcVar);
    pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    output.setPidcVersAfterUpdate(pidcVersion);
    output.setValAlreadyExists(pidcVarCopyCommand.getValAlreadyExists());
    output.setPastedSubVariant(copiedSubVar);
    getLogger().info("Copied Project variant Id : {}", copiedSubVar.getId());
    return Response.ok(output).build();
  }

}
