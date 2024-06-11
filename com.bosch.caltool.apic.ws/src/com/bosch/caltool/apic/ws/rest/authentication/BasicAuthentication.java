/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.authentication;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserCommand;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader.Status;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author svj7cob
 */
public class BasicAuthentication extends AbstractSimpleBusinessObject {

  /**
   * The type of authentication
   */
  private static final String BASIC_AUTH_TYPE = "Basic";

  /**
   * Error message if credential format is invalid, for e.g. if not encoded with 'UTF-8'
   */
  private static final String MSG_INVALID_CRED_FORMAT = "Invalid user credential format";
  private static final String MSG_INVALID_CREDENTIALS = "Invalid user name or password";
  private static final String MSG_UNSUPPORTED_AUTH_TYPE = "Unsupported authentication type";
  private final HttpServletRequest httpRequest;

  /**
   * @param request the request object
   * @param sData the service data
   */
  public BasicAuthentication(final HttpServletRequest request, final ServiceData sData) {
    super(sData);
    this.httpRequest = request;
  }

  /**
   * Sets the credential token into service data
   */
  public void authenticate() {
    String authHeader = this.httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null) {
      getLogger().debug("No authorization header present");
      return;
    }

    if (!authHeader.startsWith(BASIC_AUTH_TYPE + " ")) {
      throw new UnAuthorizedRestAccessException(MSG_UNSUPPORTED_AUTH_TYPE);
    }

    final String authHdrVal = authHeader.replaceFirst(BASIC_AUTH_TYPE + " ", "");
    byte[] decodedBytes = Base64.getDecoder().decode(authHdrVal);

    String decodedStr;

    try {
      decodedStr = new String(decodedBytes, WsCommonConstants.CHARACTER_ENCODING_FORMAT);
    }
    catch (UnsupportedEncodingException exp) {
      getLogger().error(exp.getMessage(), exp);
      throw new UnAuthorizedRestAccessException(MSG_INVALID_CRED_FORMAT);
    }

    String[] userNPassArr = decodedStr.split(":");

    if (userNPassArr.length != 2) {
      throw new UnAuthorizedRestAccessException(MSG_INVALID_CREDENTIALS);
    }

    doAuthenticate(userNPassArr[0], userNPassArr[1]);
  }

  /**
   * Authenticate the current user inside the service data
   *
   * @param user
   * @param pass
   */
  private void doAuthenticate(final String user, final String pass) {
    if (CommonUtils.isEmptyString(user) || CommonUtils.isEmptyString(pass)) {
      throw new UnAuthorizedRestAccessException(MSG_INVALID_CREDENTIALS);
    }

    getServiceData().setUsername(user);
    getServiceData().setPassword(pass);

    Status status = new UserLoader(getServiceData()).authenticateCurrentUser();

    if (status == Status.SUCCESS) {
      return;
    }

    if (status != Status.USER_NOT_IN_ICDM) {
      throw new UnAuthorizedRestAccessException(MSG_INVALID_CREDENTIALS);
    }

    createUserFromLdap(user);
  }

  /**
   * @param userNtId
   */
  private void createUserFromLdap(final String userNtId) {
    getLogger().debug("User '{}' not found in iCDM. Checking LDAP...", userNtId);

    UserInfo userInfo = null;
    try {
      userInfo = new LdapAuthenticationWrapper().getUserDetails(userNtId);
    }
    catch (LdapException e1) {
      getLogger().warn("User '" + userNtId + "' NOT found in LDAP", e1);
      throw new UnAuthorizedRestAccessException(MSG_INVALID_CREDENTIALS, e1);
    }

    getLogger().debug("User '{}' found in LDAP. Creating user in ICDM...", userNtId);

    User userObj = new User();
    userObj.setName(userInfo.getUserName());
    userObj.setFirstName(userInfo.getGivenName());
    userObj.setLastName(userInfo.getSurName());
    userObj.setDepartment(userInfo.getDepartment());

    try {
      UserCommand command = new UserCommand(getServiceData(), userObj);
      new CommandExecuter(getServiceData()).execute(command);
    }
    catch (IcdmException e) {
      throw new UnAuthorizedRestAccessException(MSG_INVALID_CREDENTIALS, e);
    }

    // Authenticate the user again, to initialize the properties
    new UserLoader(getServiceData()).authenticateCurrentUser();
  }
}
