/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;

import java.util.Locale;


/**
 * Define ENUM to handle all user attributes
 */
public enum UserAttributes {

                            /**
                             * Surname attribute. LDAP key 'sn'
                             */
                            LDAP_ATTR_SURNAME("sn") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setSurName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getSurName();
                              }
                            },
                            /**
                             * Given name attribute. LDAP key 'givenName'
                             */
                            LDAP_ATTR_GIVEN_NAME("givenName") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setGivenName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getGivenName();
                              }
                            },
                            /**
                             * Account name attribute. LDAP key 'sAMAccountName'
                             */
                            LDAP_ATTR_ACCOUNT_NAME("sAMAccountName") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setUserName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getUserName();
                              }
                            },
                            /**
                             * Common Name attribute. LDAP key 'cn'
                             */
                            LDAP_ATTR_COMMON_NAME("cn") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setCommonName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getCommonName();
                              }
                            },
                            /**
                             * Distinguished Name attribute. LDAP key 'distinguishedName'
                             */
                            LDAP_ATTR_DN_NAME("distinguishedName") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setDistinguishedName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getDistinguishedName();
                              }
                            },
                            /**
                             * Display Name attribute. LDAP key 'displayName'
                             */
                            LDAP_ATTR_DISP_NAME("displayName") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setDisplayName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getDisplayName();
                              }
                            },
                            /**
                             * Principal Name attribute. LDAP key 'userPrincipalName'
                             */
                            LDAP_ATTR_USER_PRINCIPAL_NAME("userPrincipalName") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setUserPrincipalName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getUserPrincipalName();
                              }
                            },
                            /**
                             * Name attribute. LDAP key 'name'
                             */
                            LDAP_ATTR_USER_NAME("name") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setUserName(value.toUpperCase(Locale.getDefault()));// ICDM-134

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getUserName();
                              }
                            },
                            /**
                             * Company attribute. LDAP key 'company'
                             */
                            LDAP_ATTR_COMPANY("company") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setCompany(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getCompany();
                              }
                            },
                            /**
                             * Department attribute. LDAP key 'department'
                             */
                            LDAP_ATTR_DEPARTMENT("department") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setDepartment(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getDepartment();
                              }
                            },
                            /**
                             * Country attribute. LDAP key 'c'
                             */
                            LDAP_ATTR_COUNTRY("c") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setCountry(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getCountry();
                              }
                            },
                            /**
                             * Location attribute. LDAP key 'l'
                             */
                            LDAP_ATTR_LOCATION("l") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setLocation(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getLocation();
                              }
                            },
                            /**
                             * physical delivery office name attribute. LDAP key 'physicaldeliveryofficename'
                             */
                            LDAP_ATTR_PHY_DELIV_OFF_NAME("physicaldeliveryofficename") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setPhysicalDeliveryOfficeName(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getPhysicalDeliveryOfficeName();
                              }
                            },
                            /**
                             * ZIP code attribute. LDAP key 'postalcode'
                             */
                            LDAP_ATTR_POSTAL_CODE("postalcode") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setPostalCode(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getPostalCode();
                              }
                            },
                            /**
                             * mail attribute. LDAP key 'mail'
                             */
                            LDAP_ATTR_EMAIL("mail") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setEmailAddress(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getEmailAddress();
                              }
                            },
                            /**
                             * Street Address attribute. LDAP key 'streetAddress'
                             */
                            LDAP_ATTR_STREET("streetAddress") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setStreetAddress(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getStreetAddress();
                              }
                            },
                            /**
                             * telephone attribute. LDAP key 'telephonenumber'
                             */
                            LDAP_ATTR_PHONE("telephonenumber") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setTelephoneNumber(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getTelephoneNumber();
                              }
                            },
                            /**
                             * description attribute. LDAP key 'description'
                             */
                            LDAP_ATTR_DESC("description") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setDescription(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getDescription();
                              }
                            },
                            /**
                             * member of attribute. LDAP key 'memberof'
                             */
                            LDAP_ATTR_MEMBER_OF("memberof") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setMemberOf(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getMemberOf();
                              }
                            },

                            /**
                             * SID Attribute. LDAP key 'displayName'
                             */
                            LDAP_ATTR_OBJ_SID("objectSID") {

                              /**
                               * {@inheritDoc}
                               */
                              @Override
                              public void setUserInfoValue(final UserInfo userInfo, final String value) {
                                userInfo.setUserSID(value);

                              }

                              @Override
                              public String getUserInfoValue(final UserInfo userInfo) {
                                return userInfo.getUserSID();
                              }
                            };

  /**
   * defines the parameterized name
   */
  private String attrName;

  /**
   * Enum Constructor
   *
   * @param attr parameterized name
   */
  private UserAttributes(final String attr) {
    this.attrName = attr;
  }

  /**
   * Get the parameterized name
   *
   * @return attrName parameterized name
   */
  public String getUserAttr() {
    return this.attrName;
  }

  /**
   * Sets the value to the given <code>UserInfo</code> instance
   *
   * @param userInfo instance
   * @param value info value
   */
  public abstract void setUserInfoValue(UserInfo userInfo, String value);

  /**
   * Sets the value of the attribute from the given <code>UserInfo</code> instance
   *
   * @param userInfo instance
   * @return info value
   */
  public abstract String getUserInfoValue(UserInfo userInfo);


}