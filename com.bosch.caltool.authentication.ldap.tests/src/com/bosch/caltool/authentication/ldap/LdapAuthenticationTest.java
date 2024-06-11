/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.security.Decryptor;

/**
 * Tests the <code>LdapAuthentication</code> class.
 *
 * @author bne4cob
 */
public final class LdapAuthenticationTest extends JUnitTest {


  /**
   *
   */
  private static final String SYSTEM_NAME = "TEST";

  /**
   * A valid user in the LDAP system.
   */
  private static final String VALID_USER = "HFZ2SI";

  /**
   * Special test data : for this user, there are 2 records in LDAP server WIR1SO and WIR1SO1, but only one WIR1SO is
   * the exact match
   */
  private static final String VALID_USER_MULTIPLE_MATCH = "WIR1SO";

  /**
   * Special test data : for this user, there are 2 records in LDAP server WIR1SO and WIR1SO1, but none is the exact
   * match
   */
  private static final String INVALID_USER_MULTIPLE_MATCH = "WIR1";

  /**
   * A valid user in the LDAP system.
   */
  private static final String VALID_USER2 = System.getProperty("user.name");

  /**
   * Password key
   */
  private static final String KEY_LDAP_MGR_PASS = "LDAP_AUTHENTICATION.LDAP_MGR_USER_PASS";

  /**
   * LDAP configuration bundle name
   */
  private static final String BUNDLE_NAME = "com.bosch.caltool.authentication.ldap.LdapAuthentication"; //$NON-NLS-1$

  /**
   * An invalid user name.
   */
  private static final String INVALID_USER = "InvalidUser";

  /**
   * An invalid password.
   */
  private static final String INVALID_PASS = "InvalidPassword";

  /**
   * Test object
   */
  private LdapAuthentication ldap;

  /**
   * This class instantiates the <code>LdapAuthentication</code> class and sets a default logger.
   */
  @Before
  public void initialize() {
    LdapAuthentication.clearCache();
    this.ldap = new LdapAuthentication(SYSTEM_NAME, AUT_LOGGER);
  }

  /**
   * Close LDAP resources
   */
  @After
  public void closeLdap() {
    if (this.ldap != null) {
      this.ldap.close();
    }
  }


  /**
   * Test for {@link LdapAuthentication#LdapAuthentication(ILoggerAdapter) }
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testLdapAuthenticationLogger() throws LdapException {
    try (LdapAuthentication ldapTest = new LdapAuthentication(SYSTEM_NAME, AUT_LOGGER)) {
      assertNotNull("LDAP instace created", ldapTest);

      UserInfo ret1 = ldapTest.getUserDetails(VALID_USER);
      UserInfo ret2 = ldapTest.getUserDetails(VALID_USER);

      assertEquals("Same user's info retrieved by 2 queries should be same if caching is YES", ret1, ret2);

    }
  }

  /**
   * Test for {@link LdapAuthentication#LdapAuthentication(ILoggerAdapter) }
   * <p>
   * Tests whether appropriate exception is thrown if logger is not initialised in <code>LdapAuthentication</code> class
   * before calling different methods.
   */
  @Test
  public void testLdapAuthenticationLoggerNegative() {
    this.thrown.expect(LdapRuntimeException.class);
    this.thrown.expectMessage("Logger not initialised");

    try (LdapAuthentication ldapTest = new LdapAuthentication(SYSTEM_NAME, null)) {
      assertNull("LDAP instace should not have been created", ldapTest);
    }
  }

  /**
   * Test for {@link LdapAuthentication#LdapAuthentication(ILoggerAdapter, boolean) }
   * <p>
   * Tests whether appropriate exception is thrown if logger is not initialised in <code>LdapAuthentication</code> class
   * before calling different methods.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testLdapAuthenticationLoggerEnableCache() throws LdapException {
    try (LdapAuthentication ldapTest = new LdapAuthentication(SYSTEM_NAME, AUT_LOGGER, false)) {
      assertNotNull("LDAP instace created", ldapTest);

      UserInfo ret1 = ldapTest.getUserDetails(VALID_USER);
      UserInfo ret2 = ldapTest.getUserDetails(VALID_USER);

      assertNotEquals("Same user's info retrieved by 2 queries should be different if caching is NO", ret1, ret2);
    }
  }

  /**
   * Test for {@link LdapAuthentication#LdapAuthentication(ILoggerAdapter, boolean) }
   * <p>
   * Tests whether appropriate exception is thrown if logger is not initialised in <code>LdapAuthentication</code> class
   * before calling different methods.
   */
  @Test
  public void testLdapAuthenticationLoggerEnableCacheNegative() {
    this.thrown.expect(LdapRuntimeException.class);
    this.thrown.expectMessage("Logger not initialised");

    try (LdapAuthentication ldapTest = new LdapAuthentication(SYSTEM_NAME, null, true)) {
      assertNull("LDAP instace should not have been created", ldapTest);
    }
  }

  /**
   * Test for {@link LdapAuthentication#validate(String, String) }
   * <p>
   * Tests the LDAP authentication with a valid user name and password.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testValidate() throws LdapException {
    UserInfo userInfo = this.ldap.validate(VALID_USER, getUserPassFromProps());
    TESTER_LOGGER.info("User {} validated successfully", VALID_USER);

    assertNotNull("User info returned should not be null", userInfo);

    assertEquals("Check user info's name", VALID_USER, userInfo.getUserName());
  }

  /**
   * Test for {@link LdapAuthentication#validate(String, String) }
   * <p>
   * Tests the LDAP authentication with an invalid user name.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testValidateInvalidUser() throws LdapException {
    this.thrown.expect(LdapException.class);
    this.thrown.expectMessage("Invalid Credentials " + INVALID_USER.toUpperCase());

    this.ldap.validate(INVALID_USER, getUserPassFromProps());
  }

  /**
   * Test for {@link LdapAuthentication#validate(String, String) }
   * <p>
   * Tests the LDAP authentication with a valid user name and invalid password.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testValidateInvalidPassword() throws LdapException {
    this.thrown.expect(LdapException.class);
    this.thrown.expectMessage("Invalid Credentials. Invalid Password provided for " + VALID_USER);
    this.ldap.validate(VALID_USER, INVALID_PASS);
  }


  /**
   * Test for {@link LdapAuthentication#validate(String, String) }
   * <p>
   * Tests the LDAP authentication multiple times in the following order.<br>
   * a) Invalid User<br>
   * b) Valid User and invalid password<br>
   * c) Valid user and password<br>
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testValidateMultipleTrials() throws LdapException {
    TESTER_LOGGER.info("LDAP authentication multiple times - trial 1");
    try {
      this.ldap.validate(INVALID_USER, getUserPassFromProps());
    }
    catch (LdapException e) {
      assertEquals("Inalid user error expected", "Invalid Credentials INVALIDUSER", e.getMessage());
    }

    TESTER_LOGGER.info("LDAP authentication multiple times - trial 2");
    try {
      this.ldap.validate(VALID_USER, INVALID_PASS);
    }
    catch (LdapException e) {
      assertEquals("Inalid password error expected", "Invalid Credentials. Invalid Password provided for " + VALID_USER,
          e.getMessage());
    }

    TESTER_LOGGER.info("LDAP authentication multiple times - trial 3");
    UserInfo userInfo = this.ldap.validate(VALID_USER, getUserPassFromProps());
    assertNotNull("User info returned not null", userInfo);
    assertEquals("User name check", VALID_USER, userInfo.getUserName());
  }

  /**
   * Test for {@link LdapAuthentication#validateSSO() }
   * <p>
   * Icdm-515 Junit for SSO Tests the LDAP authentication with SSO Icdm-515
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testValidateSSO() throws LdapException {
    UserInfo userInfo = this.ldap.validateSSO();
    assertNotNull("User info returned not null", userInfo);

    String curUser = System.getProperty("user.name").toUpperCase();

    TESTER_LOGGER.info("LDAP authentication - user {} authenticated", curUser);
    assertEquals("User name check", curUser, userInfo.getUserName());
  }


  /**
   * Test for {@link LdapAuthentication#getUserDetails(String) }
   * <p>
   * Tests whether the details of a specific LDAP user can be retrieved using the given User ID.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetUserDetails() throws LdapException {
    UserInfo userInfo = this.ldap.getUserDetails(VALID_USER);

    assertEquals("User Details Retrieval Validation - User Name comparison", VALID_USER, userInfo.getUserName());
    assertEquals("User Details Retrieval Validation - Common Name comparison", VALID_USER, userInfo.getCommonName());
    assertEquals("User Details Retrieval Validation - Company comparison", "Robert Bosch GmbH", userInfo.getCompany());
    assertEquals("User Details Retrieval Validation - Country comparison", "DE", userInfo.getCountry());
    assertEquals("User Details Retrieval Validation - Department comparison", "PS-SC/EBT3", userInfo.getDepartment());
    assertEquals("User Details Retrieval Validation - Description comparison", "295207", userInfo.getDescription());
    assertEquals("User Details Retrieval Validation - Display Name comparison", "iCDM TechnicalUser (PS-SC/EBT3)",
        userInfo.getDisplayName());
    assertEquals("User Details Retrieval Validation - Email Address comparison", "", userInfo.getEmailAddress());
    assertEquals("User Details Retrieval Validation - Given Name comparison", "TechnicalUser", userInfo.getGivenName());
    assertEquals("User Details Retrieval Validation - Location comparison", "Schwieberdingen", userInfo.getLocation());
    assertTrue("User Details Retrieval Validation - Member Of comparison",
        (userInfo.getMemberOf() != null) && (userInfo.getMemberOf().indexOf("DGS_CDM_PRO-Users") >= 0));

    assertEquals("User Details Retrieval Validation - Physical Delivery Office Name comparison", "Si 501/0/West",
        userInfo.getPhysicalDeliveryOfficeName());
    assertEquals("User Details Retrieval Validation - Postal Code comparison", "71701", userInfo.getPostalCode());
    assertEquals("User Details Retrieval Validation - Street Address comparison", "Robert-Bosch-Strasse 2",
        userInfo.getStreetAddress());
    assertEquals("User Details Retrieval Validation - Sur Name comparison", "iCDM", userInfo.getSurName());
    assertNotNull("User Details Retrieval Validation - Telephone Number not null", userInfo.getTelephoneNumber());

  }

  /**
   * Test for {@link LdapAuthentication#getUserDetails(String) }
   * <p>
   * Tests whether the details of a specific LDAP user can be retrieved using the given User ID. The user ID matches
   * multiple records, but only one valid(exact match) record is present.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetUserDetailsMultipleMatchesValid() throws LdapException {
    UserInfo userInfo = this.ldap.getUserDetails(VALID_USER_MULTIPLE_MATCH);
    assertEquals("User Details Retrieval Validation - User Name comparison", VALID_USER_MULTIPLE_MATCH,
        userInfo.getUserName());
  }

  /**
   * Test for {@link LdapAuthentication#getUserDetails(String) }
   * <p>
   * Tests whether the details of a specific LDAP user can be retrieved using the given User ID. The user ID matches
   * multiple records, but no valid(exact match) record is present.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetUserDetailsMultipleMatchesInvalid() throws LdapException {
    this.thrown.expect(LdapException.class);
    this.thrown.expectMessage("Invalid user - " + INVALID_USER_MULTIPLE_MATCH.toUpperCase());

    this.ldap.getUserDetails(INVALID_USER_MULTIPLE_MATCH);
  }


  /**
   * Test for {@link LdapAuthentication#getUserDetails(String) }
   * <p>
   * Tests the user retrival, when the user is queried multiple times.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testValidUserDetailsRetrievalMultipleTimes() throws LdapException {
    UserInfo userInfo = this.ldap.getUserDetails(VALID_USER2);
    assertEquals("User Details Retrieval Validation - Check user name", VALID_USER2.toUpperCase(),
        userInfo.getCommonName());

    UserInfo userInfo2 = this.ldap.getUserDetails(VALID_USER2);
    assertEquals("User Details Retrieval Validation - same object is returned", userInfo, userInfo2);
  }

  /**
   * Test for {@link LdapAuthentication#getUserDetails(String) }
   * <p>
   * Tests the retrieval of an invalid user ID.
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetUserDetailsInvalidUserDetailsRetrieval() throws LdapException {
    this.thrown.expect(LdapException.class);
    this.thrown.expectMessage("Invalid user - " + INVALID_USER.toUpperCase());

    this.ldap.getUserDetails(INVALID_USER);
  }

  /**
   * Test for {@link LdapAuthentication#getUserDetailsForFullName(String) }
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetUserDetailsForFullName() throws LdapException {
    // A valid user's full name as in the outlook should be given along with the department
    String displayName = "Nelson Bebith (MS/EMT2-MC)";
    UserInfo userInfo = this.ldap.getUserDetailsForFullName(displayName);

    assertNotNull("Search for Users with the given display name", userInfo);
    assertEquals("Search for Users with the given display name", displayName, userInfo.getDisplayName());
  }


  /**
   * Test for {@link LdapAuthentication#getUserDetailsForFullName(String) }
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetUserDetailsForFullNameWithInvalidName() throws LdapException {
    String displayName = INVALID_USER;

    UserInfo ret = this.ldap.getUserDetailsForFullName(displayName);
    assertNull("Return should be null", ret);
  }


  /**
   * Test for {@link LdapAuthentication#search(Map, String) }
   * <p>
   * add non ICDM users Search the Ldap users metching the condition
   *
   * @throws LdapException error from LDAP
   */
  // ICdm-1127
  @Test
  public void testSearchMapString() throws LdapException {
    Map<UserAttributes, String> userInfoMap = new ConcurrentHashMap<>();
    userInfoMap.put(UserAttributes.LDAP_ATTR_GIVEN_NAME, "TechnicalUser");
    userInfoMap.put(UserAttributes.LDAP_ATTR_SURNAME, "iCDM");
    userInfoMap.put(UserAttributes.LDAP_ATTR_DEPARTMENT, "*EBT");

    final Set<UserInfo> search = this.ldap.search(userInfoMap, "de");

    assertNotNull("Search for Users with the given name", search);
    assertEquals("Check user search return", 1, search.size());
  }

  /**
   * Test for {@link LdapAuthentication#search(UserAttributes, String, String)}
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testSearchUserAttributesStringString() throws LdapException {
    Set<UserInfo> results = this.ldap.search(UserAttributes.LDAP_ATTR_SURNAME, "iCDM", "de");

    assertNotNull("Result not null", results);
    TESTER_LOGGER.info("Records retrieved  = {}", results.size());
    assertEquals("Search result = 1", 1, results.size());

    UserInfo userInfo = results.iterator().next();
    assertEquals("Check user name", VALID_USER, userInfo.getUserName());
  }

  /**
   * Test for {@link LdapAuthentication#search(UserAttributes, String, String)}
   *
   * @throws LdapException exception with message as search timed out
   */
  @Test
  public void testSearchUserAttributesStringStringTimeout() throws LdapException {
    this.thrown.expect(LdapException.class);
    this.thrown.expectMessage("LDAP search timed out. Use refined search criteria.");

    this.ldap.search(UserAttributes.LDAP_ATTR_USER_NAME, "*4cob", null);
  }


  /**
   * Test for {@link LdapAuthentication#clearCache() }
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testClearCache() throws LdapException {
    UserInfo retBeforeClearing1 = this.ldap.getUserDetails(VALID_USER);
    UserInfo retBeforeClearing2 = this.ldap.getUserDetails(VALID_USER);

    assertEquals("Same user's info retrieved by 2 queries should same before clearing", retBeforeClearing1,
        retBeforeClearing2);

    LdapAuthentication.clearCache();
    UserInfo retAfterClearing = this.ldap.getUserDetails(VALID_USER);

    assertNotEquals("Same user's info retrieved by 2 queries should be different after clearing", retBeforeClearing1,
        retAfterClearing);
  }

  /**
   * Test for {@link LdapAuthentication#getDomainNames() }
   *
   * @throws LdapException error from LDAP
   */
  @Test
  public void testGetDomainNames() throws LdapException {
    Set<String> domains = this.ldap.getDomainNames();

    assertNotNull("Domains not null", domains);

    assertFalse("Domains shoud not be empty", domains.isEmpty());

    TESTER_LOGGER.debug("Domains retrieved : {}", domains);
    assertTrue("Domains should contain APAC", domains.contains("APAC"));
  }

  /**
   * Success scenario that fetches list of users with CN=idm2bcd_PS_SSD_Application_User role
   *
   * @throws LdapException e
   */
  @Test
  public void testSearchActiveDirectoryUserListForSSDUsers() throws LdapException {
    Set<String> users = this.ldap.searchUsersByGroup("idm2bcd_PS_SSD_Application_User");
    assertNotNull(users);
    assertTrue(!users.isEmpty());
  }

  /**
   * Failure scenario that returns 0 users if no users are present
   *
   * @throws LdapException e
   */
  @Test
  public void testSearchGroupwithNoUsers() throws LdapException {
    Set<String> users = this.ldap.searchUsersByGroup("PS-EC/EBT all employees GER");
    assertTrue(users.isEmpty());
  }

  /**
   * Failure scenario that returns 0 users if no users are present
   *
   * @throws LdapException e
   */
  @Test
  public void testSearchGroupwithInvalidName() throws LdapException {
    Set<String> users = this.ldap.searchUsersByGroup("invalid");
    assertTrue(users.isEmpty());
  }

  /**
   * Success scenario that fetches list of users with displayName=MS/ETB5-PS-Delivery Leads
   *
   * @throws LdapException e
   */
  @Test
  public void testSearchActiveDirectoryGroupListWithDisplayName() throws LdapException {
    Set<String> users = this.ldap.searchUsersByGroup("MS/EMT22-MC Internal Employees", true);
    assertNotNull(users);
    assertTrue(!users.isEmpty());
  }

  /**
   * Success scenario that fetches list of users with CN=COB_RBEI_MS_ETB5_PS-DeliveryLeads
   *
   * @throws LdapException e
   */
  @Test
  public void testSearchActiveDirectoryGroupListByCN() throws LdapException {
    Set<String> users = this.ldap.searchUsersByGroup("WOM.MS_EMT22-MC-Int");
    assertNotNull(users);
    assertTrue(!users.isEmpty());
  }

  /**
   * @throws LdapException e
   */
  @Test
  public void testSearchActiveDirectoryIDM2BCDGroup() throws LdapException {
    Set<String> users = this.ldap.searchUsersByGroup("idm2bcd_PS_SSD_Application_User");
    assertNotNull(users);
    assertTrue(!users.isEmpty());
  }


  /**
   * Success scenario that fetches list of users with displayName=MS/ETB5-PS-Delivery Leads
   *
   * @throws LdapException e
   */
  @Test
  public void testcheckActiveDirectoryGroup() throws LdapException {
    UserInfo groupSID = this.ldap.fetchGroupInfo("MS/EMT22-MC-iCDM-DevTeam");
    assertNotNull(groupSID);
  }

  /**
   * Success scenario that fetches list of users with CN=COB_RBEI_MS_ETB5_PS-DeliveryLeads
   *
   * @throws LdapException e
   */
  @Test
  public void testcheckActiveDirectoryGroupByCN() throws LdapException {
    UserInfo groupSID = this.ldap.fetchGroupInfo("COB_BGSW_MS_EMT22_MC-ICDM-TEAM");
    assertNotNull(groupSID);
  }

  /**
   * @throws LdapException e
   */
  @Test
  public void testSearchActiveDirectoryHWOMGroupList() throws LdapException {

    Set<String> users = this.ldap.searchUsersByGroup("PS-SC/ECS Internal Employees All", true);
    assertNotNull(users);
    assertTrue(!users.isEmpty());
  }

  /**
   */
  private static String getString(final ResourceBundle resBundle, final String key) {
    String message = null;
    try {
      message = resBundle.getString(key);
    }
    catch (MissingResourceException e) {
      message = '!' + key + '!';
    }

    return message;
  }


  private String getUserPassFromProps() {
    String passwordKey = SYSTEM_NAME + ".LDAP_MGR_PASSWORD";
    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(TESTER_LOGGER);
    return Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), TESTER_LOGGER);
  }

}
