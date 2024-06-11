package com.bosch.caltool.icdm.ws.rest.client.general;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.UserLoginInfo;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for UserLoginInfo
 *
 * @author msp5cob
 */
public class UserLoginInfoServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public UserLoginInfoServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_USERLOGININFO);
  }


  /**
   * Get TActiveDirectoryGroup using its name
   *
   * @param userNtId userNtId
   * @return UserLoginInfo object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UserLoginInfo get(final String userNtId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_NAME, userNtId);
    return get(wsTarget, UserLoginInfo.class);
  }

  /**
   * Create a UserLoginInfo record
   *
   * @param obj object to create
   * @return created UserLoginInfo object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UserLoginInfo create(final UserLoginInfo obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a UserLoginInfo record
   *
   * @param obj object to update
   * @return updated UserLoginInfo object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UserLoginInfo update(final UserLoginInfo obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

}
