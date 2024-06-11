package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcSubVarCocWpLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * Service class for Pidc Sub Variant
 *
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCSUBVARIANT)
public class PidcSubVariantService extends AbstractRestService {

  /**
   * Rest web service path for Pidc Sub Variant
   */
  public static final String RWS_PIDCSUBVARIANT = "pidcsubvariant";

  /**
   * Get Pidc Sub Variant using its id
   *
   * @param objId object's id
   * @return Rest response, with PidcSubVariant object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcSubVariantLoader loader = new PidcSubVariantLoader(getServiceData());
    PidcSubVariant ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a Pidc Sub Variant record
   *
   * @param obj object to create
   * @return Rest response, with created PidcSubVariant object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final PidcSubVariantData obj) throws IcdmException {
    PidcSubVariantCommand cmd = new PidcSubVariantCommand(getServiceData(), obj, false);

    PidcVariantLoader pidcvarLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVariant pidcVar = pidcvarLoader.getDataObjectByID(obj.getPidcVariantId());
    obj.setPidcVarBeforeUpdate(pidcVar);
    PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    obj.setPidcVersBeforeUpdate(pidcVersion);

    executeCommand(cmd);
    // setting pidc subvarcoc wp set
    obj.setCreatedPidcSubVarCocWpSet(
        new PidcSubVarCocWpLoader(getServiceData()).getAllPidcSubVarCocWpBySubVarId(cmd.getNewData().getId()));

    PidcSubVariant ret = cmd.getNewData();
    obj.setDestPidcSubVar(ret);
    pidcVar = pidcvarLoader.getDataObjectByID(obj.getPidcVariantId());
    obj.setPidcVarAfterUpdate(pidcVar);
    pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    obj.setPidcVersAfterUpdate(pidcVersion);
    getLogger().info("Created Pidc Sub Variant Id : {}", ret.getId());
    return Response.ok(obj).build();
  }

  /**
   * Update a Pidc Sub Variant record
   *
   * @param obj object to update
   * @return Rest response, with updated PidcSubVariant object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final PidcSubVariantData obj) throws IcdmException {
    PidcSubVariantCommand cmd = new PidcSubVariantCommand(getServiceData(), obj, true);

    PidcVariantLoader pidcvarLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVariant pidcVar = pidcvarLoader.getDataObjectByID(obj.getSrcPidcSubVar().getPidcVariantId());
    obj.setPidcVarBeforeUpdate(pidcVar);
    PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    obj.setPidcVersBeforeUpdate(pidcVersion);

    executeCommand(cmd);

    PidcSubVariant ret = cmd.getNewData();
    obj.setDestPidcSubVar(ret);
    pidcVar = pidcvarLoader.getDataObjectByID(obj.getSrcPidcSubVar().getPidcVariantId());
    obj.setPidcVarAfterUpdate(pidcVar);
    pidcVersion = pidcVersLoader.getDataObjectByID(pidcVar.getPidcVersionId());
    obj.setPidcVersAfterUpdate(pidcVersion);

    getLogger().info("Updated Pidc Sub Variant Id : {}", ret.getId());
    return Response.ok(obj).build();
  }


  /**
   * @param pidcVersionId pidcVersionId
   * @param includeDeleted boolean
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_VAR_FOR_VERSION)
  @CompressData
  public Response getVariantsForVersion(@QueryParam(WsCommonConstants.RWS_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED) final boolean includeDeleted)
      throws IcdmException {
    PidcSubVariantLoader pidcVariantLoader = new PidcSubVariantLoader(getServiceData());
    Map<Long, PidcSubVariant> variantsMap = pidcVariantLoader.getSubVariantsForVersion(pidcVersionId, false);
    WSObjectStore.getLogger().info("PidcVariantService.getVariantsForVersion() completed. Number of variants = {}",
        variantsMap.size());

    return Response.ok(variantsMap).build();
  }
}
