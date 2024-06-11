/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class AttributeServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public AttributeServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ATTRIBUTE);
  }

  /**
   * Get all attributes, including deleted
   *
   * @return Map of attributes. Key - attribute ID, value - attribute
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, Attribute> getAll() throws ApicWebServiceException {
    return getAll(true);
  }

  /**
   * Get all attributes
   *
   * @param includeDeleted if true, deleted attributes are also included
   * @return Map of attributes. Key - attribute ID, value - attribute
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, Attribute> getAll(final boolean includeDeleted) throws ApicWebServiceException {

    LOGGER.debug("Get all Attributes. Include deleted = {}", includeDeleted);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted);

    GenericType<Map<Long, Attribute>> type = new GenericType<Map<Long, Attribute>>() {};
    Map<Long, Attribute> retMap = get(wsTarget, type);

    LOGGER.debug("Attributes loaded. Number of items = {}", retMap.size());

    return retMap;
  }

  /**
   * Get an attribute by its ID
   *
   * @param objId attribute ID
   * @return attribute
   * @throws ApicWebServiceException any exception
   */
  public Attribute get(final Long objId) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, Attribute.class);
  }

  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return created Attribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Attribute create(final Attribute obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a Attribute record
   *
   * @param obj object to update
   * @return updated Attribute object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Attribute update(final Attribute obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Get an attribute by its ID
   *
   * @return set of attribute ids having uncleared values
   * @throws ApicWebServiceException any exception
   */
  public List<Long> getUnClearedAttrIds() throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_UNCLEARED_ATTRIDS);
    GenericType<List<Long>> type = new GenericType<List<Long>>() {};

    return get(wsTarget, type);
  }

  /**
   * @return the sorted set of attributes
   */
  public SortedSet<Attribute> getMappedAttrs() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_MAPPED_ATTR);
    GenericType<SortedSet<Attribute>> type = new GenericType<SortedSet<Attribute>>() {};

    return get(wsTarget, type);
  }


  /**
   * Get Atrribute Export Model
   *
   * @return
   * @throws ApicWebServiceException
   */
  public AttrExportModel getAttrExportModel() throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ATTRIBUTE_EXPORT_MODEL);
    return get(wsTarget, AttrExportModel.class);
  }

  /**
   * Get all Quotation relevant Attributes mapped to usecase for the given List of MCR_ID
   *
   * @param mcrIds set of mcr Id
   * @return Map of Quotation relevant Attributes - key - Attr id, value -Attribute model
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, Attribute> getQuotRelAttrByMcrId(final Set<String> mcrIds) throws ApicWebServiceException {
    LOGGER.debug("Get Quotation Relevant Attributes by Mcr Id");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_QUO_REL_ATTR_BY_MCR_ID);
    if (mcrIds != null) {
      for (String mcrId : mcrIds) {
        wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_MCR_ID, mcrId);
      }
    }
    GenericType<Map<Long, Attribute>> type = new GenericType<Map<Long, Attribute>>() {};
    Map<Long, Attribute> attrMap = get(wsTarget, type);
    LOGGER.debug("Attribute records loaded count = {}", attrMap.size());
    return attrMap;
  }

}
