package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for AttrNValueDependency
 *
 * @author dmo5cob
 */
public class AttrNValueDependencyServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public AttrNValueDependencyServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ATTRNVALUEDEPENDENCY);
  }


  /**
   * Get AttrNValueDependency using its id
   *
   * @param objId object's id
   * @return AttrNValueDependency object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrNValueDependency getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, AttrNValueDependency.class);
  }

  /**
   * Create a AttrNValueDependency record
   *
   * @param obj object to create
   * @return created AttrNValueDependency object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrNValueDependency create(final AttrNValueDependency obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a AttrNValueDependency record
   *
   * @param obj object to update
   * @return updated AttrNValueDependency object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttrNValueDependency update(final AttrNValueDependency obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * get all dependencies for the given attribute
   *
   * @param attrId Attribute id
   * @return Set<AttrNValueDependency> - set of AttrNValueDependency objects
   * @throws ApicWebServiceException error while retrieving data
   */
  public Set<AttrNValueDependency> getDependenciesByAttribute(final Long attrId) throws ApicWebServiceException {
    LOGGER.debug("Get all dependencies for attribute {}", attrId);

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_ATTR_DEPN).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, attrId);

    GenericType<Set<AttrNValueDependency>> type = new GenericType<Set<AttrNValueDependency>>() {};
    Set<AttrNValueDependency> retSet = get(wsTarget, type);

    LOGGER.debug("Attribute dependencies loaded");

    return retSet;
  }

  /**
   * get all dependencies for the given value
   *
   * @param valueId value id
   * @return Set<AttrNValueDependency> - set of AttrNValueDependency objects
   * @throws ApicWebServiceException error while retrieving data
   */
  public Set<AttrNValueDependency> getDependenciesByValue(final Long valueId) throws ApicWebServiceException {
    LOGGER.debug("Get all dependencies for value {}", valueId);

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_VALUE_DEPN).queryParam(WsCommonConstants.RWS_ATTRIBUTE_VALUE, valueId);

    GenericType<Set<AttrNValueDependency>> type = new GenericType<Set<AttrNValueDependency>>() {};
    Set<AttrNValueDependency> retSet = get(wsTarget, type);

    LOGGER.debug("Value dependencies loaded");

    return retSet;
  }

  /**
   * Get dependencies for level attributes
   *
   * @return Map<Long, Map<Long, Long>>
   * @throws ApicWebServiceException error while retrieving data
   */
  public Map<Long, Map<Long, Set<Long>>> getValDepnForLvlAttrValues() throws ApicWebServiceException {
    LOGGER.debug("Started Fetching value dependencies of level attribute values");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VAL_DEPN_LVL_ATTRS);
    GenericType<Map<Long, Map<Long, Set<Long>>>> type = new GenericType<Map<Long, Map<Long, Set<Long>>>>() {};
    Map<Long, Map<Long, Set<Long>>> valDepnMap = get(wsTarget, type);
    LOGGER.debug("Fetching value dependencies of level attribute values");
    return valDepnMap;
  }

}
