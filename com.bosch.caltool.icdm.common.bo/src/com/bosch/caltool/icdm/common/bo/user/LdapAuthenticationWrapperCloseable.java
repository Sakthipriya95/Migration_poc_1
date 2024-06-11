/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.user;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.authentication.ldap.LdapAuthentication;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserAttributes;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.logger.LdapLogger;

/**
 * @author bne4cob
 */
public class LdapAuthenticationWrapperCloseable implements Closeable {

  private final LdapAuthentication ldap =
      new LdapAuthentication(LdapAuthenticationWrapper.ICDM_LDAP_PREFIX, LdapLogger.getInstance());

  /**
   * Returns the details of the input user.
   *
   * @param user the user ID
   * @return instance of <code>UserInfo</code> class
   * @throws LdapException error from LDAP
   */
  public UserInfo getUserDetails(final String user) throws LdapException {
    return this.ldap.getUserDetails(user);
  }

  /**
   * @param displayName - display name of the user
   * @return the user information related to the display name
   * @throws LdapException error from LDAP
   */
  public UserInfo getUserDetailsForFullName(final String displayName) throws LdapException {
    return this.ldap.getUserDetailsForFullName(displayName);
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
    return this.ldap.search(searchCriteria, domainName);
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
    return this.ldap.search(field, searchValue, domainName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    this.ldap.close();
  }
}
