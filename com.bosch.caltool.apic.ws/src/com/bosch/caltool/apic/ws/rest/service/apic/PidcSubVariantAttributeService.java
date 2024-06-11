package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantAttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;


/**
 * Service class for Pidc Sub Variant Attribute
 *
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCSUBVARIANTATTRIBUTE)
public class PidcSubVariantAttributeService extends AbstractRestService {

  /**
   * Rest web service path for Pidc Sub Variant Attribute
   */
  public final static String RWS_PIDCSUBVARIANTATTRIBUTE = "pidcsubvariantattribute";

  /**
   * Get Pidc Sub Variant Attribute using its id
   *
   * @param objId object's id
   * @return Rest response, with PidcSubVariantAttribute object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    PidcSubVariantAttribute ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * @param pidcSubVariantId pidcVersionId
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_VAR_ATTR_FOR_VARIANT)
  @CompressData
  public Response getAttrForSubVariant(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcSubVariantId)
      throws IcdmException {
    PidcSubVariantAttributeLoader pidcVariantLoader = new PidcSubVariantAttributeLoader(getServiceData());
    Map<Long, PidcSubVariantAttribute> varAttrMap = pidcVariantLoader.getSubVarAttrForSubVarId(pidcSubVariantId);
    WSObjectStore.getLogger().info(
        "PidcSubVariantAttributeService.getAttrForSubVariant() completed. Number of sub variant attributes= {}",
        varAttrMap.size());

    return Response.ok(varAttrMap).build();
  }
}
