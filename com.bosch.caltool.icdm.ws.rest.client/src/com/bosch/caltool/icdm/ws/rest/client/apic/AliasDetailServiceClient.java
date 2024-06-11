/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Alias Detail
 *
 * @author bne4cob
 */
public class AliasDetailServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public AliasDetailServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ALIASDETAIL);
  }

  /**
   * Get Alias Detail using its id
   *
   * @param objId object's id
   * @return AliasDetail object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AliasDetail get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, AliasDetail.class);
  }

  /**
   * Get Alias Detail records using Alias Definition ID
   *
   * @param adId Alias Definition ID
   * @return Map. Key - id, Value - AliasDetail object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, AliasDetail> getByAdId(final Long adId) throws ApicWebServiceException {
    LOGGER.debug("Get Alias Detail records. Input - adId = {}", adId);
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_BY_AD_ID).queryParam(WsCommonConstants.RWS_QP_AD_ID, adId);
    GenericType<Map<Long, AliasDetail>> type = new GenericType<Map<Long, AliasDetail>>() {};
    Map<Long, AliasDetail> retMap = get(wsTarget, type);
    LOGGER.debug("Alias Detail records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Create a Alias Detail record
   *
   * @param obj object to create
   * @return created AliasDetail object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AliasDetail create(final AliasDetail obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a Alias Detail record
   *
   * @param obj object to update
   * @return updated AliasDetail object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AliasDetail update(final AliasDetail obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a Alias Detail record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final AliasDetail obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

}
