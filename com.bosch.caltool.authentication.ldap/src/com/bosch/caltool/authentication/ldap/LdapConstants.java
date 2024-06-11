/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;


/**
 * Constants used in LdapAuth plugin
 *
 * @author bne4cob
 */
final class LdapConstants {


  /**
   * New Constant for Manager password
   */
  static final String LDAP_AUTHENTICATION_LDAP_MGR_USER_PASS = "LDAP_AUTHENTICATION.LDAP_MGR_USER_PASS";

  /**
   * Manager User key
   */
  static final String KEY_LDAP_MGR_USER_DN = "LDAP_AUTHENTICATION.LDAP_MGR_USER_DN";

  /**
   * Default size of Initial Context hash table
   */
  static final int DEFAULT_HT_SIZE = 5;

  /**
   * Icdm-1127 - Mnimum Search filter Size
   */
  static final int MIN_SEARCH_FILTER = 1;

  /**
   * LDAP configuration bundle name
   */
  static final String RESOURCE_BUNDLE_NAME = "com.bosch.caltool.authentication.ldap.LdapAuthentication"; //$NON-NLS-1$

  /**
   * Context factory class
   */
  static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

  /**
   * Authentication type
   */
  static final String LDAP_AUTH_TYPE = "simple";
  /**
   * LDAP read timeout
   */
  static final String LDAP_READ_TIMEOUT = "20000";
  /**
   * LDAP read timeout property
   */
  static final String LDAP_READ_TIMEOUT_PROP = "com.sun.jndi.ldap.read.timeout";

  /**
   * Key Base string for LDAP directory search
   */
  static final String KEY_LDAP_SEARCH_BASE = "LDAP_AUTHENTICATION.LDAP_SEARCH_BASE";

  /**
   * Key LDAP URL
   */
  static final String KEY_LDAP_URL = "LDAP_AUTHENTICATION.LDAP_URL";

  /**
   * Key no network
   */
  static final String KEY_MSG_NO_NETWORK = "LDAP_AUTHENTICATION.MSG_NO_NW_CONNECTION";

  /**
   * Key Failed Initialization
   */
  static final String KEY_MSG_INIT_FAILURE = "LDAP_AUTHENTICATION.MSG_AD_INIT_FAILURE";

  /**
   * Invalid Credentials. Invalid Password provided for
   */
  static final String KEY_MSG_INVALID_CRED = "LDAP_AUTHENTICATION.MSG_INVALID_CREDENTIALS";

  /**
   * Key invalid password
   */
  static final String KEY_MSG_INVALID_PASS = "LDAP_AUTHENTICATION.MSG_INVALID_PASSWORD";

  /**
   * Key Error closing context. Contact your administrator!
   */
  static final String KEY_MSG_CNTXT_CLOSE_ERR = "LDAP_AUTHENTICATION.MSG_CONTEXT_CLOSING_ERROR";
  
  static final String LDAP_AUTHENTICATION_SSD_USER_PASS = "ldap.authentication.userPass";


  /**
   * Private constructor to avoid instantiation
   */
  private LdapConstants() {
    // Keep private constructor
  }

}
