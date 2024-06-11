/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.general.AzureTokenModel;
import com.bosch.caltool.icdm.model.general.AzureUserModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.security.Decryptor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author msp5cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_OAUTH + "/" + WsCommonConstants.RWS_OAUTH_ACCEPT_TOKEN))
public class AzureAuthService extends AbstractRestService {

  /**
   *
   */
  private static final String ICDM_AZURE_ICDM_CLIENT_SECRET = "ICDM.AZURE_ICDM_CLIENT_SECRET";
  /**
   *
   */
  private static final String MS_GRAPH_SELECT_FILTER = "?$select=department";
  /**
   *
   */
  private static final String MS_GRAPH_URL = "https://graph.microsoft.com/v1.0/me";
  /**
   *
   */
  private static final String AUTHORIZATION = "Authorization";
  /**
   *
   */
  private static final String DEPARTMENT = "department";
  /**
   *
   */
  private static final String FAMILY_NAME = "family_name";
  /**
   *
   */
  private static final String GIVEN_NAME = "given_name";
  /**
   *
   */
  private static final String UNIQUE_NAME = "unique_name";
  /**
   *
   */
  private static final String BOSCH_COM = "@bosch.com";
  /**
   *
   */
  private static final String MAX_RETRY_ATTEMPT_FOR_TOKEN_REACHED = "Max Retry attempt for token reached.";
  /**
   *
   */
  private static final String CLIENT_SECRET = "client_secret";
  /**
   *
   */
  private static final String AUTHORIZATION_CODE = "authorization_code";
  /**
   *
   */
  private static final String GRANT_TYPE = "grant_type";
  /**
   *
   */
  private static final String CLIENT_ID = "client_id";

  /**
   * @param code Authorization code
   * @param state unique Client code
   * @return response
   * @throws IcdmException Exception
   * @throws FileNotFoundException FileNotFoundException
   */
  @GET
  @Produces({ MediaType.TEXT_HTML })
  @CompressData
  public InputStream getAzureToken(@QueryParam(WsCommonConstants.RWS_AZURE_AUTH_CODE) final String code,
      @QueryParam(WsCommonConstants.RWS_CLIENT_STATE) final String state)
      throws IcdmException, FileNotFoundException {

    String htmlFilePath = Messages.getString("ICDM.WELCOME_PAGE_LOCATION");
    File htmlFile = new File(htmlFilePath);

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      CommonParamLoader commonParamLoader = new CommonParamLoader(getServiceData());
      String azureAuthUrl = commonParamLoader.getValue(CommonParamKey.ICDM_AZURE_AUTH_URI) + "/token";
      HttpPost httppost = new HttpPost(azureAuthUrl);


      httppost.setEntity(new UrlEncodedFormEntity(getRequestParams(commonParamLoader, code), "UTF-8"));

      // Execute and get the response.
      HttpResponse response = httpclient.execute(httppost);

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
      AzureTokenModel azureTokenModel = getAzureTokenModelFromJson(jsonNode);
      (new UserLoader(getServiceData())).addUserStateToken(state, azureTokenModel);
    }
    catch (IOException | UnsupportedOperationException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }

    return new FileInputStream(htmlFile);
  }

  private String getRedirectUri() {
    return Messages.getString(CommonUtilConstants.ICDM_AZURE_ICDM_WS_REDIRECT_URI);
  }

  private List<NameValuePair> getRequestParams(final CommonParamLoader commonParamLoader, final String code) {

    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair(CLIENT_ID, commonParamLoader.getValue(CommonParamKey.ICDM_AZURE_AUTH_CLIENT_ID)));
    params.add(new BasicNameValuePair(GRANT_TYPE, AUTHORIZATION_CODE));
    params.add(new BasicNameValuePair(CLIENT_SECRET, getAzureIcdmClientSecret()));

    String redirectUri = getRedirectUri();
    getLogger().info("Azure SSO Redirect URI - {}", redirectUri);

    params.add(new BasicNameValuePair("redirect_uri", redirectUri));
    params.add(new BasicNameValuePair("code", code));

    return params;
  }

  /**
   * @param state unique Client code
   * @return Response
   * @throws IcdmException IcdmException
   */
  @GET
  @Path(WsCommonConstants.RWS_OAUTH_USER_TOKEN)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getTokenForCLient(@QueryParam(WsCommonConstants.RWS_CLIENT_STATE) final String state)
      throws IcdmException {
    CommonParamLoader commonParamLoader = new CommonParamLoader(getServiceData());
    boolean retry = true;
    int count = 0;
    int maxRetry = Integer.parseInt(commonParamLoader.getValue(CommonParamKey.ICDM_AZURE_MAX_RETRY));
    int retryIntervalInMs = Integer.parseInt(commonParamLoader.getValue(CommonParamKey.ICDM_AZURE_RETRY_INTERVAL));
    AzureUserModel azureUserModel = new AzureUserModel();
    ObjectMapper objectMapper = new ObjectMapper();
    UserLoader userLdr = new UserLoader(getServiceData());
    while (retry) {

      try {

        if (count > maxRetry) {
          throw new IcdmException(MAX_RETRY_ATTEMPT_FOR_TOKEN_REACHED);
        }
        if (userLdr.getUserStateToken(state) == null) {
          sleep(retryIntervalInMs);
        }
        else {
          retry = false;
          AzureTokenModel azureTokenModel = userLdr.getUserStateToken(state);
          String accessToken = azureTokenModel.getAccessToken();
          loadUserFieldsFromToken(azureUserModel, accessToken, objectMapper);
          loadOptionalFieldsUsingMsGraph(azureUserModel, accessToken, objectMapper);
          userLdr.removeUserStateToken(state);
        }
        count++;
      }
      catch (UnAuthorizedAccessException | IOException exp) {
        throw new IcdmException(exp.getMessage(), exp);
      }
    }
    return Response.ok(azureUserModel).build();
  }

  private void loadUserFieldsFromToken(final AzureUserModel azureUserModel, final String accessToken,
      final ObjectMapper objectMapper)
      throws IOException {
    DecodedJWT jwtAccessToken = JWT.decode(accessToken);
    JsonNode jwtAcsTknPayloadNode = objectMapper.readTree(decodeToken(jwtAccessToken.getPayload()));
    String username = jwtAcsTknPayloadNode.get(UNIQUE_NAME).asText();
    azureUserModel.setUserName(username.replace(BOSCH_COM, CommonUtilConstants.EMPTY_STRING).toUpperCase());
    azureUserModel.setFirstName(jwtAcsTknPayloadNode.get(GIVEN_NAME).asText());
    azureUserModel.setLastName(jwtAcsTknPayloadNode.get(FAMILY_NAME).asText());
  }

  private String getAzureIcdmClientSecret() {
    PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(getLogger());
    return Decryptor.getInstance().decrypt(passWordWrapper.getPassword(ICDM_AZURE_ICDM_CLIENT_SECRET), getLogger());
  }

  private void loadOptionalFieldsUsingMsGraph(final AzureUserModel azureUserModel, final String accessToken,
      final ObjectMapper objectMapper)
      throws IcdmException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      String graphQueryUrl = MS_GRAPH_URL + MS_GRAPH_SELECT_FILTER;
      HttpGet get = new HttpGet(graphQueryUrl);
      get.addHeader(AUTHORIZATION, accessToken);
      HttpResponse response = httpclient.execute(get);
      JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
      azureUserModel.setDepartment(jsonNode.get(DEPARTMENT).asText());
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
  }

  /**
   * Sleeps the current thread for the given time
   *
   * @param milliSeconds time to sleep
   */
  private void sleep(final long milliSeconds) {
    try {
      Thread.sleep(milliSeconds);
    }
    catch (InterruptedException exp) {
      getLogger().warn(exp.getMessage(), exp);
      Thread.currentThread().interrupt();
    }
  }

  private String decodeToken(final String base64) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
  }

  private AzureTokenModel getAzureTokenModelFromJson(final JsonNode jsonNode) {
    return new AzureTokenModel(jsonNode.get("token_type").asText(), jsonNode.get("scope").asText(),
        jsonNode.get("expires_in").asText(), jsonNode.get("ext_expires_in").asText(),
        jsonNode.get("access_token").asText(), jsonNode.get("refresh_token").asText(),
        jsonNode.get("id_token").asText());
  }

}
