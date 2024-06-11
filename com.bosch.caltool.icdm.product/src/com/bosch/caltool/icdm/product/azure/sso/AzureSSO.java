/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.azure.sso;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.eclipse.ui.PartInitException;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.AzureUserModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.UserLoginInfo;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.AzureAuthServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.CommonParamServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserLoginInfoServiceClient;
import com.bosch.rcputils.browser.BrowserUtil;

/**
 * @author MSP5COB
 */
public class AzureSSO {

  /**
   *
   */
  private static final String AUTHORIZE = "authorize";

  /**
   *
   */
  private static final String QUERY = "query";

  /**
   *
   */
  private static final String RESPONSE_MODE = "response_mode";

  /**
   *
   */
  private static final String CLIENT_ID = "client_id";

  /**
   *
   */
  private static final String STATE = "state";

  /**
   *
   */
  private static final String REDIRECT_URI = "redirect_uri";

  /**
   *
   */
  private static final String CODE = "code";

  /**
   *
   */
  private static final String RESPONSE_TYPE = "response_type";

  private static final String SCOPE = "scope";

  private String clientId;
  private String redirectUri;
  private String azureAuthUri;
  private String azureScope;

  /**
   * @return Username
   * @throws ApicWebServiceException Web Service Exception
   * @throws PartInitException PartInitException
   * @throws MalformedURLException MalformedURLException
   * @throws UnsupportedEncodingException UnsupportedEncodingException
   */
  public AzureUserModel azureAdSSOLogin()
      throws ApicWebServiceException, PartInitException, MalformedURLException, UnsupportedEncodingException {
    initializeProperties();
    String clientKey = generateRandomValue();
    String url = getIcdmAzureAuthUrl(clientKey);
    BrowserUtil.getInstance().openExternalBrowser(url);

    AzureUserModel azureUserModel = getUserDetailsFromToken(clientKey);
    CDMLogger.getInstance().info("User Authenticated via Azure AD : {}", azureUserModel.getUserName());
    insertUserLoginInfo(azureUserModel.getUserName(), 1L, 0L);
    return azureUserModel;
  }

  private String getIcdmAzureAuthUrl(final String clientKey) throws UnsupportedEncodingException {
    return this.azureAuthUri + "/" + AUTHORIZE + "?" + SCOPE + "=" + this.azureScope + "&" + RESPONSE_TYPE + "=" +
        CODE + "&" + REDIRECT_URI + "=" + URLEncoder.encode(this.redirectUri, StandardCharsets.UTF_8.toString()) + "&" +
        STATE + "=" + clientKey + "&" + CLIENT_ID + "=" + this.clientId + "&" + RESPONSE_MODE + "=" + QUERY;
  }

  private void initializeProperties() throws ApicWebServiceException {
    CommonParamServiceClient cmnParamSrvClient = new CommonParamServiceClient();
    this.redirectUri = ClientConfiguration.getDefault().getBaseUri() + "/" + WsCommonConstants.RWS_CONTEXT_ROOT + "/" +
        WsCommonConstants.RWS_CONTEXT_OAUTH + "/" + WsCommonConstants.RWS_OAUTH_ACCEPT_TOKEN;
    this.clientId = cmnParamSrvClient.getParameterValue(CommonParamKey.ICDM_AZURE_AUTH_CLIENT_ID.getParamName());
    this.azureAuthUri = cmnParamSrvClient.getParameterValue(CommonParamKey.ICDM_AZURE_AUTH_URI.getParamName());
    this.azureScope = cmnParamSrvClient.getParameterValue(CommonParamKey.ICDM_AZURE_SCOPE.getParamName());
  }

  private AzureUserModel getUserDetailsFromToken(final String clientKey) throws ApicWebServiceException {
    return (new AzureAuthServiceClient()).get(clientKey);
  }

  private String generateRandomValue() {
    int random = new Random().nextInt();
    CDMLogger.getInstance().debug("Random Value : {}", random);
    return Integer.toString(random);
  }

  /**
   * @param userName userName
   * @param azureCount azureCount
   * @param ldapCount ldapCount
   */
  public static void insertUserLoginInfo(final String userName, final long azureCount, final long ldapCount) {
    UserLoginInfoServiceClient srvClient = new UserLoginInfoServiceClient();
    UserLoginInfo userLoginInfo = new UserLoginInfo();
    userLoginInfo.setUserNtId(userName);
    userLoginInfo.setAzureLoginCount(azureCount);
    userLoginInfo.setLdapLoginCount(ldapCount);
    try {
      srvClient.update(userLoginInfo);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error("Saving UserLoginInfo Failed");
    }
  }

}
