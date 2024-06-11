package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Pidc Variant Attribute
 *
 * @author mkl2cob
 */
public class PidcVariantAttributeServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PidcVariantAttributeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCVARIANTATTRIBUTE);
  }


  /**
   * Get Pidc Variant Attribute using its id
   *
   * @param objId object's id
   * @return PidcVariantAttribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVariantAttribute getbyId(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcVariantAttribute.class);
  }


  /**
   * Update a Pidc Variant Attribute record
   *
   * @param obj object to update
   * @return updated PidcVariantAttribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVariantAttribute update(final PidcVariantAttribute obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }


  /**
   * @param variantId Long
   * @return Map<Long, PidcVariantAttribute>
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcVariantAttribute> getVarAttrsForVariant(final Long variantId) throws ApicWebServiceException {
    LOGGER.debug("Get all Pidc Variant Attribute records for a variant");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VAR_ATTR_FOR_VARIANT)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, variantId);
    GenericType<Map<Long, PidcVariantAttribute>> type = new GenericType<Map<Long, PidcVariantAttribute>>() {};
    Map<Long, PidcVariantAttribute> retMap = get(wsTarget, type);
    LOGGER.debug("Pidc Variant Attribute records loaded count = {}", retMap.size());
    return retMap;
  }

}
