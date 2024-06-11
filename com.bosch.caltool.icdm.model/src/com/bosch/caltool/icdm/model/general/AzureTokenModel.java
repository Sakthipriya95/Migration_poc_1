/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import java.io.Serializable;

/**
 * @author MSP5COB
 */
public class AzureTokenModel implements Serializable {


  /**
   * @param tokenType
   * @param scope
   * @param expiresIn
   * @param extExpiresIn
   * @param accessToken
   * @param refreshToken
   * @param idToken
   */
  public AzureTokenModel(final String tokenType, final String scope, final String expiresIn, final String extExpiresIn,
      final String accessToken, final String refreshToken, final String idToken) {
    super();
    this.tokenType = tokenType;
    this.scope = scope;
    this.expiresIn = expiresIn;
    this.extExpiresIn = extExpiresIn;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.idToken = idToken;
  }

  private static final long serialVersionUID = -6468868830789142493L;
  private String tokenType;
  private String scope;
  private String expiresIn;
  private String extExpiresIn;
  private String accessToken;
  private String refreshToken;
  private String idToken;

  /**
   * @return the tokenType
   */
  public String getTokenType() {
    return this.tokenType;
  }

  /**
   * @param tokenType the tokenType to set
   */
  public void setTokenType(final String tokenType) {
    this.tokenType = tokenType;
  }

  /**
   * @return the scope
   */
  public String getScope() {
    return this.scope;
  }

  /**
   * @param scope the scope to set
   */
  public void setScope(final String scope) {
    this.scope = scope;
  }

  /**
   * @return the expiresIn
   */
  public String getExpiresIn() {
    return this.expiresIn;
  }

  /**
   * @param expiresIn the expiresIn to set
   */
  public void setExpiresIn(final String expiresIn) {
    this.expiresIn = expiresIn;
  }

  /**
   * @return the extExpiresIn
   */
  public String getExtExpiresIn() {
    return this.extExpiresIn;
  }

  /**
   * @param extExpiresIn the extExpiresIn to set
   */
  public void setExtExpiresIn(final String extExpiresIn) {
    this.extExpiresIn = extExpiresIn;
  }

  /**
   * @return the accessToken
   */
  public String getAccessToken() {
    return this.accessToken;
  }

  /**
   * @param accessToken the accessToken to set
   */
  public void setAccessToken(final String accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * @return the refreshToken
   */
  public String getRefreshToken() {
    return this.refreshToken;
  }

  /**
   * @param refreshToken the refreshToken to set
   */
  public void setRefreshToken(final String refreshToken) {
    this.refreshToken = refreshToken;
  }

  /**
   * @return the idToken
   */
  public String getIdToken() {
    return this.idToken;
  }

  /**
   * @param idToken the idToken to set
   */
  public void setIdToken(final String idToken) {
    this.idToken = idToken;
  }

}
