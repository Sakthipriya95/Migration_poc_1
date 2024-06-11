package com.bosch.caltool.icdm.ws.rest.client.general;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.general.AzureUserModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for AzureAuthService
 *
 * @author MSP5COB
 */
public class AzureAuthServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public AzureAuthServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_OAUTH, WsCommonConstants.RWS_OAUTH_ACCEPT_TOKEN);
  }


  /**
   * Get User Token
   *
   * @param state Client unique code
   * @return String User info
   * @throws ApicWebServiceException exception while invoking service
   */
  public AzureUserModel get(final String state) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_OAUTH_USER_TOKEN).queryParam(WsCommonConstants.RWS_CLIENT_STATE, state);
    return get(wsTarget, AzureUserModel.class);
  }


}
