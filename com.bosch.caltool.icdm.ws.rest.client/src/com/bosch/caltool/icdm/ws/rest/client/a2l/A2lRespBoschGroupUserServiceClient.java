package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2L Responsibility Bosch Group User
 *
 * @author PDH2COB
 */
public class A2lRespBoschGroupUserServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public A2lRespBoschGroupUserServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2LRESPBOSCHGROUPUSER);
  }

  /**
   * Get A2L Responsibility Bosch Group User using its id
   *
   * @param objId object's id
   * @return A2lRespBoschGroupUser object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lRespBoschGroupUser get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lRespBoschGroupUser.class);
  }

  /**
   * Create a A2L Responsibility Bosch Group User record
   *
   * @param obj object to create
   * @return created A2lRespBoschGroupUser object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lRespBoschGroupUser create(final A2lRespBoschGroupUser obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Delete a A2L Responsibility Bosch Group User record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final A2lRespBoschGroupUser obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

  /**
   * @param respId A2l Responsibilty Id
   * @return List<A2lRespBoschGroupUser>
   * @throws ApicWebServiceException exception from service
   */
  public Map<Long, A2lRespBoschGroupUser> getA2lRespBoschGrpUsersForRespId(final Long respId)
      throws ApicWebServiceException {

    LOGGER.info("Get A2lRespBoschGroupUser list for Responsibility ID");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_A2LRESP_BSHGRP_USER_FOR_RESP)
        .queryParam(WsCommonConstants.RWS_A2L_RESP_ID, respId);
    GenericType<Map<Long, A2lRespBoschGroupUser>> type = new GenericType<Map<Long, A2lRespBoschGroupUser>>() {};

    return get(wsTarget, type);
  }

}
