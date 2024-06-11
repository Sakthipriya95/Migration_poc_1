/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.user;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.authentication.ldap.LdapAuthentication;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserAttributes;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.logger.LdapLogger;

/**
 * @author apj4cob
 */
public class LdapAuthenticationWrapper {

  /**
   *
   */
  public static final String ICDM_LDAP_PREFIX = "ICDM";

  /**
   * Returns the details of the input user.
   *
   * @param user the user ID
   * @return instance of <code>UserInfo</code> class
   * @throws LdapException error from LDAP
   */
  public UserInfo getUserDetails(final String user) throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.getUserDetails(user);
    }
  }

  /**
   * @param displayName - display name of the user
   * @return the user information related to the display name
   * @throws LdapException error from LDAP
   */
  public UserInfo getUserDetailsForFullName(final String displayName) throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.getUserDetailsForFullName(displayName);
    }
  }


  /**
   * @param searchCriteria searchCriteria
   * @param domainName domainName searched for
   * @return the set of UserInfo Objects matching the search Criteria serach for the Ldap users based on the critetia of
   *         UserAttributes
   * @throws LdapException error from LDAP
   */
  public Set<UserInfo> search(final Map<UserAttributes, String> searchCriteria, final String domainName)
      throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.search(searchCriteria, domainName);
    }
  }

  /**
   * @param field - An Attribute of UserInfo
   * @param searchValue - value for the UserAttribute
   * @param domainName - domain name to be searched for (can be null too)
   * @return Set<UserInfo> - collection of UserInfo that matches the search criteria
   * @throws LdapException error from LDAP
   */
  public Set<UserInfo> search(final UserAttributes field, final String searchValue, final String domainName)
      throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.search(field, searchValue, domainName);
    }
  }

  /**
   * Validate the user ID and password with the LDAP server
   *
   * @param user userID
   * @param password password
   * @return true, if user is valid, else false
   * @throws LdapException error from LDAP
   */
  public UserInfo validate(final String user, final String password) throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.validate(user, password);
    }
  }

  /**
   * This method validates the NT user (during single sign on ) with the LDAP user details
   * <p>
   * 1) Searches the context with the logged in NT user's SID 2)
   * </p>
   * <p>
   * Validates the ldap domain name against the NT user domain
   * </p>
   *
   * @return true if the user is valid
   * @throws LdapException error from LDAP
   */
  // iCDM-438
  public UserInfo validateSSO() throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.validateSSO();
    }
  }

  /**
   *
   */
  public Set<String> searchByDisplayName(final String groupADName) throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.searchUsersByDisplayName(groupADName);
    }
  }

  /**
   * Checks if a displayname of a group exists in LDAP and returns SID if found. Else Null
   *
   * @param groupName Display Name of Active Directory Group
   * @return Group SID
   * @throws LdapException ex
   */
  public UserInfo getGroupInfo(final String groupName) throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.fetchGroupInfo(groupName);
    }
  }

  /**
   * Returns the details of the input user.
   *
   * @param user the user ID
   * @return instance of <code>UserInfo</code> class
   * @throws LdapException error from LDAP
   */
  public UserInfo getUserDetail(final String user) throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return ldap.getUserDetail(user);
    }
  }

  /**
   * @return the domains
   * @throws LdapException error from LDAP
   */
  public SortedSet<String> getDomainNames() throws LdapException {
    try (LdapAuthentication ldap = createLdapObject()) {
      return new TreeSet<>(ldap.getDomainNames());
    }
  }

  private LdapAuthentication createLdapObject() {
    return new LdapAuthentication(ICDM_LDAP_PREFIX, LdapLogger.getInstance());
  }
}
