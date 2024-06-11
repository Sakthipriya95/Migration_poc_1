/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Review Paticipants
 *
 * @author bru2cob
 */
public class RvwParticipantServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public RvwParticipantServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVWPARTICIPANT);
  }

  /**
   * Get CDR Result Participant using its id
   *
   * @param objId object's id
   * @return CDRResultParameter object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwParticipant getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, RvwParticipant.class);
  }

  /**
   * Get Review Paticipants records using ResultId
   *
   * @param resultId Result Id id
   * @return Map. Key - id, Value - RvwParticipant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, RvwParticipant> getByResultId(final Long resultId) throws ApicWebServiceException {
    LOGGER.debug("Get Review Paticipants records using ResultId ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_RESULT_ID)
        .queryParam(WsCommonConstants.RWS_QP_RESULT_ID, resultId);
    GenericType<Map<Long, RvwParticipant>> type = new GenericType<Map<Long, RvwParticipant>>() {};
    Map<Long, RvwParticipant> retMap = get(wsTarget, type);
    LOGGER.debug("Review Paticipants records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get CDR Result participants using its ids. Note : this is a POST request
   *
   * @param objIdSet set of object ids
   * @return RvwParticipant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, RvwParticipant> getMultiple(final Set<Long> objIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE);

    GenericType<Map<Long, RvwParticipant>> type = new GenericType<Map<Long, RvwParticipant>>() {};
    return post(wsTarget, objIdSet, type);
  }

  /**
   * Create a Review Paticipants record
   *
   * @param obj object to create
   * @return created RvwParticipant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public List<RvwParticipant> create(final List<RvwParticipant> paramList) throws ApicWebServiceException {
    GenericType<List<RvwParticipant>> type = new GenericType<List<RvwParticipant>>() {};
    return create(getWsBase(), paramList, type);
  }

  /**
   * Update a Review Paticipants record
   *
   * @param obj object to update
   * @return updated RvwParticipant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwParticipant update(final RvwParticipant obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a Review Paticipants record
   *
   * @param objs objects to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Set<RvwParticipant> objs) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    delete(wsTarget, objs);
  }

}
