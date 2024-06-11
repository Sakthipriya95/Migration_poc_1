package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for AttrGroup
 *
 * @author dmo5cob
 */
public class AttrGroupServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public AttrGroupServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ATTRIBUTE_GROUP);
  }


  /**
   * Get AttrGroup using its id
   *
   * @param objId object's id
   * @return AttrGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrGroup getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, AttrGroup.class);
  }

  /**
   * Create a AttrGroup record
   *
   * @param obj object to create
   * @return created AttrGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrGroup create(final AttrGroup obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a AttrGroup record
   *
   * @param obj object to update
   * @return updated AttrGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrGroup update(final AttrGroup obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }


  /**
   * Get all AttrGroup records in system
   *
   * @return Map. Key - id, Value - AttrGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, AttrGroup> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all AttrGroup records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, AttrGroup>> type = new GenericType<Map<Long, AttrGroup>>() {};
    Map<Long, AttrGroup> retMap = get(wsTarget, type);
    LOGGER.debug("AttrGroup records loaded count = {}", retMap.size());
    return retMap;
  }
}
