package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for ActiveDirectoryGroupUser
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupUserServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public ActiveDirectoryGroupUserServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ACTIVEDIRECTORYGROUPUSER);
  }

  /**
   * Create a ActiveDirectoryGroupUser record
   *
   * @param obj object to create
   * @return created ActiveDirectoryGroupUser object
   * @throws ApicWebServiceException exception while invoking service
   */
  public ActiveDirectoryGroupUser create(final ActiveDirectoryGroupUser obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a ActiveDirectoryGroupUser record
   *
   * @param obj object to update
   * @return updated ActiveDirectoryGroupUser object
   * @throws ApicWebServiceException exception while invoking service
   */
  public ActiveDirectoryGroupUser update(final ActiveDirectoryGroupUser obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a ActiveDirectoryGroupUser record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final ActiveDirectoryGroupUser obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

  /**
   * Get all TActiveDirectoryGroupUser records in system
   *
   * @param adGroupId groupId
   * @return Map. Key - id, Value - ActiveDirectoryGroupUser object
   * @throws ApicWebServiceException exception while invoking service
   */
  public List<ActiveDirectoryGroupUser> getByGroupId(final long adGroupId) throws ApicWebServiceException {
    LOGGER.debug("Get all TActiveDirectoryGroupUser records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_AD_GRP_USERS_BY_GROUP_ID)
        .queryParam(WsCommonConstants.RWS_QP_AD_GROUP_ID, adGroupId);
    GenericType<List<ActiveDirectoryGroupUser>> type = new GenericType<List<ActiveDirectoryGroupUser>>() {};
    List<ActiveDirectoryGroupUser> retMap = get(wsTarget, type);
    LOGGER.debug("TActiveDirectoryGroupUser records loaded count = {}", retMap.size());
    return retMap;
  }
}
