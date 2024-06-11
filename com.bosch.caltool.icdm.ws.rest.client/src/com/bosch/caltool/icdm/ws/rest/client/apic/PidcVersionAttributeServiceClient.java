/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */

public class PidcVersionAttributeServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public PidcVersionAttributeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCVERSIONATTRIBUTE);
  }

  /**
   * Get PidcVersionAttribute by its ID
   *
   * @param objId PidcVersionAttribute ID
   * @return PidcVersionAttribute
   * @throws ApicWebServiceException any exception
   */
  public PidcVersionAttribute getById(final Long objId) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcVersionAttribute.class);
  }

  /**
   * Get Attribute value in PIDC version using its id
   *
   * @param pidcVersionId pidc Version Id
   * @param pidcVersAttrId Attribute Id
   * @return response with Attribute Value in the project
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVersionAttribute getPidcVersAttributeById(final Long pidcVersionId, final String pidcVersAttrId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VERS_ATTR_BY_ID)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ATTR_ID, pidcVersAttrId);
    GenericType<PidcVersionAttribute> type = new GenericType<PidcVersionAttribute>() {};
    return get(wsTarget, type);
  }
  
  /**
   * Get Pidc Version Attribute using its id
   *
   * @param pidcVersionId object's id
   * @return
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVersionAttribute getQnaireConfigAttribute(final Long pidcVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_QNAIRE_CONFIG_ATTR)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId);
    GenericType<PidcVersionAttribute> type = new GenericType<PidcVersionAttribute>() {};
    return get(wsTarget, type);
  }

  /**
   * @param pidcVersionId
   * @param pidcVariantId
   * @return
   * @throws ApicWebServiceException
   */
  public PidcVersionAttribute getPidcVersionAttribute(final Long pidcVersionId, final Long attributeId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VERSION_ATTR_FOR_ATTR)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID, attributeId);
    GenericType<PidcVersionAttribute> type = new GenericType<PidcVersionAttribute>() {};
    return get(wsTarget, type);
  }

  /**
   * @param pidcVariantId pidcVariantId
   * @return PidcVersionWithDetails
   * @throws ApicWebServiceException Exception
   */
  public PidcVersionWithDetails getPidcVersionAttrModel(final Long pidcVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VARIANT_ATTR_FOR_ATTR)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcVersionId);
    GenericType<PidcVersionWithDetails> type = new GenericType<PidcVersionWithDetails>() {};
    return get(wsTarget, type);
  }
}
