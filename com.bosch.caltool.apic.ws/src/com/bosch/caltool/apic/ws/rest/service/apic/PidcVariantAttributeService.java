package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantAttributeCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantAttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;


/**
 * Service class for Pidc Variant Attribute
 *
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCVARIANTATTRIBUTE)
public class PidcVariantAttributeService extends AbstractRestService {

  /**
   * Rest web service path for Pidc Variant Attribute
   */
  public final static String RWS_PIDCVARIANTATTRIBUTE = "pidcvariantattribute";

  /**
   * Get Pidc Variant Attribute using its id
   *
   * @param objId object's id
   * @return Rest response, with PidcVariantAttribute object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    PidcVariantAttribute ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Update a Pidc Variant Attribute record
   *
   * @param obj object to update
   * @return Rest response, with updated PidcVariantAttribute object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final PidcVariantAttribute obj) throws IcdmException {
    PidcVariantAttributeCommand cmd = new PidcVariantAttributeCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    PidcVariantAttribute ret = cmd.getNewData();
    getLogger().info("Updated Pidc Variant Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * @param pidcVariantId pidcVersionId
   * @param includeDeleted boolean
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_VAR_ATTR_FOR_VARIANT)
  @CompressData
  public Response getAttrForVariant(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVariantId)
      throws IcdmException {
    PidcVariantAttributeLoader pidcVariantAttrLoader = new PidcVariantAttributeLoader(getServiceData());

    Map<Long, PidcVariantAttribute> varAttrMap = pidcVariantAttrLoader.getVarAttrForVariant(pidcVariantId);
    WSObjectStore.getLogger().info(
        "PidcVariantAttributeService.getAttrForVariant() completed. Number of variant attributes= {}",
        varAttrMap.size());

    return Response.ok(varAttrMap).build();
  }
}
