/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.emr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.emr.EmrPidcVariantCommand;
import com.bosch.caltool.icdm.bo.emr.EmrPidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.emr.EmrFileEmsVariantMapping;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;

/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_EMISSION_ROBUSTNESS + "/" +
    WsCommonConstants.RWS_EMR_FILE_VARIANT)
public class EmrFileVariantService extends AbstractRestService {

  /**
   * Returns Emr File-Variant-Ems mapping.
   *
   * @param emrFileIds Emr file Ids
   * @return mapping data
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_EMS_VARIANT_MAP)
  @CompressData
  public Response getFileVariantEmsMapping(
      @QueryParam(value = WsCommonConstants.RWS_EMR_FILE_ID) final Set<Long> emrFileIds)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    EmrPidcVariantLoader loader = new EmrPidcVariantLoader(serviceData);
    EmrFileEmsVariantMapping objects = loader.getFileVariantEmsMapping(emrFileIds);
    WSObjectStore.getLogger().info("EMR file-variant-ems map count : " + objects.getEmrFileEmsVariantMap().size());
    ResponseBuilder response = Response.ok(objects);
    return response.build();
  }

  /**
   * Saves Emr File-Variant-Ems mapping.
   *
   * @param emrPidcVariants mapping objects
   * @return mapping data
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response saveFileVariantEmsMapping(final Set<EmrPidcVariant> emrPidcVariants) throws IcdmException {
    ServiceData serviceData = getServiceData();
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    for (EmrPidcVariant data : emrPidcVariants) {
      EmrPidcVariantCommand cmd = new EmrPidcVariantCommand(serviceData, data);
      cmdList.add(cmd);
    }
    executeCommand(cmdList);
    Map<Long, EmrPidcVariant> createdEmrPidcVarMap = new HashMap<>();
    for (AbstractSimpleCommand cmd : cmdList) {
      EmrPidcVariant newData = ((EmrPidcVariantCommand) cmd).getNewData();
      createdEmrPidcVarMap.put(newData.getId(), newData);
    }
    ResponseBuilder response = Response.ok(createdEmrPidcVarMap);
    return response.build();
  }

  /**
   * Deletes Emr File-Variant-Ems mapping.
   *
   * @param emrPidcVariantIds mapping object ids
   * @return mapping data
   * @throws IcdmException IcdmException
   */
  @DELETE
  @CompressData
  public Response deleteFileVariantEmsMapping(
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> emrPidcVariantIds)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    EmrPidcVariantLoader loader = new EmrPidcVariantLoader(serviceData);
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    for (Long id : emrPidcVariantIds) {
      EmrPidcVariantCommand cmd = new EmrPidcVariantCommand(serviceData, loader.getDataObjectByID(id), true);
      cmdList.add(cmd);
    }
    executeCommand(cmdList);
    ResponseBuilder response = Response.ok("Success");
    return response.build();
  }
}
