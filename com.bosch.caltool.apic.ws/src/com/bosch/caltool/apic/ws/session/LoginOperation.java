/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.session;

import com.bosch.caltool.apic.ws.Login;
import com.bosch.caltool.apic.ws.LoginResponse;


/**
 * @author imi2si
 */
public class LoginOperation {

  private static final String DEFAULT_TIMEZONE = "UTC";
  private static final String OPERATION = "Login";
  private static final String OPERATION_PARAMETER = "Login";

  private final Login login;

  private Session session = null;

  public LoginOperation(final Login login) {
    this.login = login;
    createSession(this.login.getPassword());
  }

  public final String getSessionId() {
    return this.session.getSessionId();
  }

  public final Session getSession() {
    return this.session;
  }

  public final LoginResponse getLoginResponse() {
    LoginResponse response = new LoginResponse();
    response.setSessID(this.session.getSessionId());

    return response;
  }

  private String getTimeZome() {
    if (this.login.getTimezone() != null) {
      return this.login.getTimezone();
    }

    return DEFAULT_TIMEZONE;
  }

  private void createSession(final String passWord) {
    this.session = new Session(this.login.getUserName(), OPERATION, OPERATION_PARAMETER, getTimeZome(), passWord);
  }
}
