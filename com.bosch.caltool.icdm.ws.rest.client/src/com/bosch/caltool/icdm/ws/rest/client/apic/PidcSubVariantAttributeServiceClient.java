package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Pidc Sub Variant Attribute
 *
 * @author mkl2cob
 */
public class PidcSubVariantAttributeServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PidcSubVariantAttributeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCSUBVARIANTATTRIBUTE);
  }


  /**
   * Get Pidc Sub Variant Attribute using its id
   *
   * @param objId object's id
   * @return PidcSubVariantAttribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVariantAttribute getbyId(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcSubVariantAttribute.class);
  }


  /**
   * @param id subvariant id
   * @return Map<Long, PidcSubVariantAttribute>
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcSubVariantAttribute> getSubVarAttrForSubVar(final Long id) throws ApicWebServiceException {
    LOGGER.debug("Get all Pidc Sub Variant Attribute records for a variant");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VAR_ATTR_FOR_VARIANT)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, id);
    GenericType<Map<Long, PidcSubVariantAttribute>> type = new GenericType<Map<Long, PidcSubVariantAttribute>>() {};
    Map<Long, PidcSubVariantAttribute> retMap = get(wsTarget, type);
    LOGGER.debug("Pidc Variant Attribute records loaded count = {}", retMap.size());
    return retMap;
  }


}
