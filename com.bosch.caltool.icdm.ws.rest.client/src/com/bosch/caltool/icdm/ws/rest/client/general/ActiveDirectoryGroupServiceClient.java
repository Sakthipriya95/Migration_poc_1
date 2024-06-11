package com.bosch.caltool.icdm.ws.rest.client.general;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for ActiveDirectoryGroup
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public ActiveDirectoryGroupServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ACTIVEDIRECTORYGROUP);
  }


  /**
   * Get TActiveDirectoryGroup using its name
   *
   * @param groupName object's name
   * @return ActiveDirectoryGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public ActiveDirectoryGroup get(final String groupName) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_NAME, groupName);
    return get(wsTarget, ActiveDirectoryGroup.class);
  }

  /**
   * Create a ActiveDirectoryGroup record
   *
   * @param obj object to create
   * @return created ActiveDirectoryGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public ActiveDirectoryGroup create(final ActiveDirectoryGroup obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a ActiveDirectoryGroup record
   *
   * @param obj object to update
   * @return updated ActiveDirectoryGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public ActiveDirectoryGroup update(final ActiveDirectoryGroup obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Sync all ActiveDirectoryGroups with LDAP Data
   *
   * @throws ApicWebServiceException exception while invoking service
   */
  public void syncActiveDirectoryGroupUsers() throws ApicWebServiceException {
    put(getWsBase().path(WsCommonConstants.RWS_SYNC_ACTIVEDIRECTORYGROUPUSERS), "", Void.class);
  }

  /**
   * Delete a ActiveDirectoryGroup record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final ActiveDirectoryGroup obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

}
