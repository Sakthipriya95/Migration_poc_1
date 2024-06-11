/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class AttributeSuperGroupServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public AttributeSuperGroupServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ATTRIBUTE_SUPER_GROUP);
  }

  /**
   * Get all attributes
   *
   * @return Map of attributes. Key - attribute ID, value - attribute
   * @throws ApicWebServiceException error during service call
   */

  public AttrGroupModel getAttrGroupModel() throws ApicWebServiceException {
    LOGGER.debug("Get attribute group model ... ");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ATTR_GRP_MODEL);

    AttrGroupModel ret = get(wsTarget, AttrGroupModel.class);

    LOGGER.debug("Attribute model loaded. Number of super groups = {}. Number of groups = {}",
        ret.getAllSuperGroupMap().size(), ret.getAllGroupMap().size());

    return ret;
  }

  /**
   * Get an Attribute Super Group by ID
   *
   * @param objId Super Group ID
   * @return attribute
   * @throws ApicWebServiceException any exception
   */
  public AttrSuperGroup get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);

    return get(wsTarget, AttrSuperGroup.class);
  }

  /**
   * Create a AttrSuperGroup record
   *
   * @param obj object to create
   * @return created AttrGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrSuperGroup create(final AttrSuperGroup obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a AttrSuperGroup record
   *
   * @param obj object to update
   * @return updated AttrSuperGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrSuperGroup update(final AttrSuperGroup obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }
}
