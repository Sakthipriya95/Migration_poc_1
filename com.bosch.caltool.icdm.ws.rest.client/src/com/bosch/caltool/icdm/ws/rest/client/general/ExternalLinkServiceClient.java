/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class ExternalLinkServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public ExternalLinkServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_EXT_LINK);
  }

  /**
   * Fetch the external link info for the given linkable object
   *
   * @param object linkable object
   * @return external link info
   * @throws ApicWebServiceException error while invoking service
   */
  public ExternalLinkInfo getLinkInfo(final IModel object) throws ApicWebServiceException {
    
    WebTarget target = createBaseWsTarget(object);

    ExternalLinkInfo ret = get(target, ExternalLinkInfo.class);

    LOGGER.debug("External link info retrieved. URL = {}", ret.getUrl());

    return ret;

  }

  /**
   * @param object
   * @return
   */
  private WebTarget createBaseWsTarget(final IModel object) {
    IModelType type = ModelTypeRegistry.INSTANCE.getTypeOfModel(object);

    String typeCode = type.getTypeCode();
    Long objId = object.getId();

    LOGGER.debug("Fetching external link info for object : type = {}, ID = {}", typeCode, objId);

    return getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId).queryParam(WsCommonConstants.RWS_QP_TYPE,
        typeCode);
  }

  /**
   * Fetch the external link info for the given linkable object,passing two object ids as the objects involved in the
   * link are not directly related
   *
   * @param object linkable object
   * @param additionalDetails containing other ids
   * @return external link info
   * @throws ApicWebServiceException error while invoking service
   */
  public ExternalLinkInfo getLinkInfo(final IModel object, final Map<String, String> additionalDetails)
      throws ApicWebServiceException {

    WebTarget wsTarget = createBaseWsTarget(object);

    for (Entry<String, String> entryDetail : additionalDetails.entrySet()) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_DETAIL,
          entryDetail.getKey() + WsCommonConstants.QP_SEPARATOR + entryDetail.getValue());
    }

    ExternalLinkInfo ret = get(wsTarget, ExternalLinkInfo.class);

    LOGGER.debug("External link info retrieved. URL = {}", ret.getUrl());

    return ret;

  }


}
