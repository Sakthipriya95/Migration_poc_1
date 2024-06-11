/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;

import java.io.Closeable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.security.Decryptor;


/**
 * LDAP authentication class.
 * <p>
 * IMPORTANT : <br>
 * a) the instance of this class is not thread safe <br>
 * b) the instance should be closed after use by invoking the {@link #close()} method or with a <code> try...with</code>
 * statement
 * <p>
 * Sample Usage :
 *
 * <pre>
 * {@code
 try (LdapAuthentication ldap = new LdapAuthentication(LOGGER)) {
      // Validate credentials
      ldap.validate(userNtId, password);

      // Fetch user details
      UserInfo userInfo = ldap.getUserDetails(userNtId);
    }
 }
 * </pre>
 *
 * <code>LdapAuthentication</code> caches the user details and invalid user IDs already retrieved, and reuses across the
 * instance. To disable this, use the constructor with <code>enableCaching = false</code>. To clear the cache, invoke
 * {@link #clearCache()}
 *
 * @author adn1cob
 */
public class LdapAuthentication implements Closeable {

  /**
   *
   */
  private static final String GROUP_NAME = "Group name : {}";

  /**
   * Message
   */
  private static final String MSG_SEARCH_RESULT_EMPTY = "Search result is empty";

  /**
   * Message
   */
  private static final String MSG_SEARCH_TIMED_OUT = "LDAP search timed out. Use refined search criteria.";

  /**
   * Base string for LDAP directory search
   */
  private static final String SEARCH_BASE_STRING = getPropertyValue(LdapConstants.KEY_LDAP_SEARCH_BASE);

  /**
   * Instance of logger
   */
  private final ILoggerAdapter logger;

  /**
   * Stores the user details
   */
  private static final Map<String, UserInfo> userDetails = new ConcurrentHashMap<>();

  /**
   * Invalid user names
   */
  // Keeping a map, to use Concurrency of maps
  private static final ConcurrentMap<String, Object> invalidUserNames = new ConcurrentHashMap<>();


  private final boolean enableCaching;

  private String ldapUrl;

  /*
   * System Name where LDAP is used
   */
  private final String systemName;

  private static final int PAGESIZE = 1500;

  // Maximum page number to be considered
  // Maximum users that can be pulled = 1500 * (400 + 1) = 601500
  private static final int MAX_RANGE = 400;

  /**
   * Dir context for reference user
   */
  private DirContext refDirContext;


  /**
   * Constructor. Caching is enabled
   *
   * @param systemName systemName where ldap is used
   * @param logger logger
   */
  public LdapAuthentication(final String systemName, final ILoggerAdapter logger) {
    this(systemName, logger, true);

  }

  /**
   * Constructor
   *
   * @param systemName systemName where ldap is used
   * @param logger logger
   * @param enableCaching if set to true, user info caching is enabled
   */
  public LdapAuthentication(final String systemName, final ILoggerAdapter logger, final boolean enableCaching) {
    this.logger = logger;
    assertLoggerAvailable();
    this.enableCaching = enableCaching;
    this.systemName = systemName;
  }


  /**
   * Validate the user ID and password with the LDAP server
   *
   * @param user userID
   * @param password password
   * @return User's details, retrieved from LDAP server
   * @throws LdapException error during user validation
   */
  public UserInfo validate(final String user, final String password) throws LdapException {
    // Keeps the upper case of the input user for all references
    final String username = user.toUpperCase(Locale.ENGLISH);

    String invalidUserMsg = getPropertyValue(LdapConstants.KEY_MSG_INVALID_CRED) + " " + username;
    checkInvalid(username, invalidUserMsg);

    NamingEnumeration<SearchResult> userAttrEnum = null;
    try {
      // Search this user in the context

      Map<UserAttributes, String> searchCriteria = new ConcurrentHashMap<>();
      searchCriteria.put(UserAttributes.LDAP_ATTR_COMMON_NAME, username);

      userAttrEnum = searchAllUsers(getRefDirContext(), searchCriteria, null);

      if ((userAttrEnum == null) || !userAttrEnum.hasMoreElements()) {
        markInvalid(username);
        throw new LdapException(invalidUserMsg);
      }

      final SearchResult userAttr = userAttrEnum.next();

      // Set User attributes to UserInfo class
      final UserInfo userInfo = createUserInfo(userAttr);
      addToCache(username, userInfo);

      final String userDN = userAttr.getName() + "," + LdapAuthentication.SEARCH_BASE_STRING;

      // log as debug message
      this.logger.debug("Distinguished name for {} is : {}", username, userDN);

      // validate user DN
      validateUserDN(userInfo, userDN, password);

      this.logger.info("User {} is validated", username);

      return userInfo;
    }
    catch (NamingException exp) {
      throw new LdapException(getPropertyValue(LdapConstants.KEY_MSG_INVALID_CRED) + " " + username, exp);
    }
    finally {
      close(userAttrEnum);
    }

  }

  /**
   * This method validates the NT user (during single sign on) with the LDAP user details<br>
   * 1) Searches the context with the logged in NT user's SID <br>
   * 2) Validates the ldap domain name against the NT user domain
   *
   * @return User's details, retrieved from LDAP server
   * @throws LdapException error during user validation
   */
  // iCDM-438
  public UserInfo validateSSO() throws LdapException {
    // Get the NT user
    String uName = getNTUserName();
    final String username = uName.toUpperCase(Locale.ENGLISH);

    String invalidUserMsg = "User account " + username + " in this domain is not valid or expired!";
    checkInvalid(username, invalidUserMsg);
    NamingEnumeration<SearchResult> userAttrEnum = null;
    try {
      // Search this user in the context
      Map<UserAttributes, String> searchCriteria = new ConcurrentHashMap<>();
      searchCriteria.put(UserAttributes.LDAP_ATTR_OBJ_SID, getNTUserSID());

      userAttrEnum = searchAllUsers(getRefDirContext(), searchCriteria, null);
      if ((userAttrEnum == null) || !userAttrEnum.hasMoreElements()) {
        markInvalid(username);
        throw new LdapException(invalidUserMsg);
      }

      final SearchResult userAttr = userAttrEnum.next();

      // Set User attributes to UserInfo class
      final UserInfo userInfo = createUserInfo(userAttr);

      addToCache(username, userInfo);

      final String userDN = userAttr.getName() + "," + LdapAuthentication.SEARCH_BASE_STRING;
      this.logger.debug("Distinguished name for {} is : {}", username, userDN);

      this.logger.info("User {} is SSO validated", username);

      return userInfo;
    }
    catch (NamingException exp) {
      throw new LdapException("User account is not valid! " + username, exp);
    }
    finally {
      close(userAttrEnum);
    }

  }


  /**
   * Returns the details of the input user.
   *
   * @param user the user ID
   * @return instance of <code>UserInfo</code> class
   * @throws LdapException error during user details retrieval
   */
  public UserInfo getUserDetails(final String user) throws LdapException {
    // Keeps the upper case of the input user for all references
    final String userName = user.toUpperCase(Locale.ENGLISH);

    this.logger.debug("Fetching User Info for user : {}", userName);

    UserInfo userInfo = getUserInfoFromCache(userName);
    if (userInfo != null) {
      this.logger.debug("  User info for {} found in cache", userName);
      return userInfo;
    }

    String invalidUserMsg = "Invalid user - " + userName;
    checkInvalid(userName, invalidUserMsg);

    Set<UserInfo> searchResult = search(UserAttributes.LDAP_ATTR_COMMON_NAME, userName, null);

    try {
      UserInfo ret = getSearchResult(searchResult, UserAttributes.LDAP_ATTR_COMMON_NAME, userName);
      addToCache(userName, ret);
      return ret;
    }
    catch (LdapException e) {
      if (MSG_SEARCH_RESULT_EMPTY.equals(e.getMessage())) {
        markInvalid(userName);
        throw new LdapException(invalidUserMsg, e);
      }
      throw e;
    }
  }

  /**
   * Returns the details of the input user.
   *
   * @param user the user ID
   * @return instance of <code>UserInfo</code> class
   * @throws LdapException error during user details retrieval
   * @deprecated use {@link #getUserDetails(String)} instead
   */
  @Deprecated
  public UserInfo getUserDetail(final String user) throws LdapException {
    return getUserDetails(user);
  }


  /**
   * @param displayName - display name of the user
   * @return the user information related to the display name
   * @throws LdapException error during user detail retrieval
   */
  public UserInfo getUserDetailsForFullName(final String displayName) throws LdapException {
    UserInfo ret = null;

    try {
      Set<UserInfo> searchResult = search(UserAttributes.LDAP_ATTR_DISP_NAME, displayName, null);
      ret = getSearchResult(searchResult, UserAttributes.LDAP_ATTR_DISP_NAME, displayName);
    }
    catch (LdapException e) {
      if (MSG_SEARCH_RESULT_EMPTY.equals(e.getMessage())) {
        // If no records found for the display name, just return null.
        this.logger.info("User not found with the display name " + displayName, e);
      }
      else {
        throw e;
      }
    }
    return ret;
  }

  /**
   * @param field - An Attribute of UserInfo
   * @param searchValue - value for the UserAttribute
   * @param domainName - domain name to be searched for (can be null too)
   * @return Set<UserInfo> - collection of UserInfo that matches the search criteria
   * @throws LdapException error during search
   */
  public Set<UserInfo> search(final UserAttributes field, final String searchValue, final String domainName)
      throws LdapException {
    Map<UserAttributes, String> searchCriteria = new EnumMap<>(UserAttributes.class);

    searchCriteria.put(field, searchValue);
    return search(searchCriteria, domainName);
  }

  /**
   * @param searchCriteria searchCriteria
   * @param domainName domainName searched for
   * @return the set of UserInfo Objects matching the search Criteria serach for the Ldap users based on the critetia of
   *         UserAttributes
   * @throws LdapException error during search
   */
  public Set<UserInfo> search(final Map<UserAttributes, String> searchCriteria, final String domainName)
      throws LdapException {
    this.logger.debug("LDAP Search criteria : {}; Domain Name : {}", searchCriteria, domainName);
    Set<UserInfo> userInfoSet = new HashSet<>();

    NamingEnumeration<SearchResult> userAttrEnum = null;
    try {
      // Search this user in the context
      userAttrEnum = searchAllUsers(getRefDirContext(), searchCriteria, domainName);
      while ((userAttrEnum != null) && userAttrEnum.hasMoreElements()) {
        final SearchResult userAttr = userAttrEnum.next();
        final UserInfo userInfo = createUserInfo(userAttr);
        userInfoSet.add(userInfo);
      }

      this.logger.debug("LDAP Search : Number of matching users found = {}", userInfoSet.size());

      return userInfoSet;
    }
    catch (NamingException exp) {
      String message = checkNull(exp.getMessage());
      message = message.contains("LDAP response read timed out") ? MSG_SEARCH_TIMED_OUT : "Invalid Value";
      throw new LdapException(message, exp);
    }
    finally {
      close(userAttrEnum);
    }

  }

  /**
   * Fetch the domain names from BOSCH LDAP
   *
   * @return the domain names as a set
   * @throws LdapException error during search
   */
  public Set<String> getDomainNames() throws LdapException {
    this.logger.debug("Fetching domain names from LDAP ...");

    Set<String> retSet = new HashSet<>();

    NamingEnumeration<SearchResult> domainNamingEnum = null;
    try {
      domainNamingEnum = searchDomains();
      while (domainNamingEnum.hasMoreElements()) {
        SearchResult result = domainNamingEnum.next();
        String name = result.getName();
        if (name.startsWith("DC=")) {
          name = name.substring("DC=".length());
          retSet.add(name);
        }
      }
    }
    catch (NamingException e) {
      throw new LdapException("Error while searching domains : " + e.getMessage(), e);
    }
    finally {
      close(domainNamingEnum);
    }

    this.logger.debug("Domains count = {}. Domain names : {}", retSet.size(), retSet);

    return retSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    close(this.refDirContext);
  }

  /**
   * Clears the cached information
   */
  public static void clearCache() {
    userDetails.clear();
    invalidUserNames.clear();
  }

  /**
   * Get the list of users available in an CN - Active Directory
   *
   * @param groupName CN Name
   * @return list of users
   * @throws LdapException exception
   * @deprecated use {@link #searchUsersByGroup(String)} instead
   */
  @Deprecated
  public Set<String> searchUsersByGroupName(final String groupName) throws LdapException {
    return searchUsersByGroup(groupName);
  }


  /**
   * Checks whether the given displayname of a group is a valid Active Directory group in LDAP
   *
   * @param groupName Group Name or Display name of AD Group
   * @return group name
   * @throws LdapException exception
   * @deprecated use {@link #fetchGroupInfo(String)} instead
   */
  @Deprecated
  public UserInfo getGroupSID(final String groupName) throws LdapException {
    return fetchGroupInfo(groupName);
  }

  /**
   * Checks whether the given displayname of a group is a valid Active Directory group in LDAP
   *
   * @param groupName Group Name or Display name of AD Group
   * @return group name
   * @throws LdapException exception
   */
  public UserInfo fetchGroupInfo(final String groupName) throws LdapException {
    this.logger.debug(GROUP_NAME, groupName);
    SearchControls searchCtls = new SearchControls();
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      searchCtls.setReturningAttributes(enumNameToStringArray(UserAttributes.values()));
      NamingEnumeration<SearchResult> searchResults;
      // Normal Security groups
      searchResults = getRefDirContext().search(LdapAuthentication.SEARCH_BASE_STRING,
          "(&(objectclass=group)(|((displayName=" + groupName + ")(CN=" + groupName + "))))", searchCtls);
      if (searchResults.hasMoreElements()) {
        SearchResult result = searchResults.next();
        return createUserInfo(result);
      }
    }
    catch (NamingException e) {
      throw new LdapException(getPropertyValue(LdapConstants.KEY_MSG_INVALID_CRED) + " " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Search Users by group
   *
   * @param groupName group name
   * @param checkDisplayName CheckDisplay Name
   * @return set
   * @throws LdapException ex
   */
  public Set<String> searchUsersByGroup(final String groupName, final boolean checkDisplayName) throws LdapException {
    this.logger.debug("Search Users by group: Search Input = {}, checkDisplayName = {}", groupName, checkDisplayName);
    SearchControls searchCtls = new SearchControls();
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    int range = 0;
    try {
      NamingEnumeration<?> members = null;
      Set<String> retSet = new HashSet<>();

      // If the member range has reached it's limit, it will be set as false
      boolean doSearch = true;
      while ((range <= MAX_RANGE) && doSearch) {
        searchCtls.setReturningAttributes(new String[] { generateMemberAttrRangeString(range) });
        NamingEnumeration<SearchResult> searchResults;
        String filter = "(&(objectclass=group)(cn=" + groupName + "))";
        if (checkDisplayName) {
          filter = "(&(objectclass=group)(|((displayName=" + groupName + ")(CN=" + groupName + "))))";
        }

        searchResults = getRefDirContext().search(LdapAuthentication.SEARCH_BASE_STRING, filter, searchCtls);
        if (searchResults.hasMoreElements()) {

          SearchResult searchResult = searchResults.next();
          Attribute memberRange = searchResult.getAttributes().get(generateMemberAttrRangeString(range));
          if (memberRange != null) {
            // memberRange Sample: "member;range= 0 - 1499"
            members = memberRange.getAll();
          }
          else {
            // memberRange Sample: "member;range= 1500 - *"
            Attribute memberAttribute = searchResult.getAttributes().get(generateMemberAttrLastRangeString(range));
            if (memberAttribute == null) {
              return retSet;
            }
            members = memberAttribute.getAll();
            doSearch = false;
          }
          range++;
        }
        else {
          doSearch = false;
        }

        while ((members != null) && members.hasMoreElements()) {
          addMemberAttributes(members, retSet);
        }
      }
      this.logger.debug("LDAP Active Directory Search Found {} users", retSet.size());
      return retSet;
    }
    catch (NamingException e) {
      throw new LdapException(getPropertyValue(LdapConstants.KEY_MSG_INVALID_CRED) + " " + e);
    }
  }

  /**
   * Search Users by group
   *
   * @param groupName name(CN)
   * @return set
   * @throws LdapException ex
   */
  public Set<String> searchUsersByGroup(final String groupName) throws LdapException {
    return searchUsersByGroup(groupName, false);
  }

  /**
   * Search Users by Display Name
   *
   * @param grpName name
   * @return set
   * @throws LdapException ex
   * @deprecated use {@link #searchUsersByGroup(String, boolean)} instead with <code>checkDisplayName = true</code>
   */
  @Deprecated
  public Set<String> searchUsersByDisplayName(final String grpName) throws LdapException {
    return searchUsersByGroup(grpName, true);
  }

  private void addMemberAttributes(final NamingEnumeration<?> members, final Set<String> retSet)
      throws NamingException, LdapException {
    String userInfo = members.next().toString();
    // TODO : Streamline source code by accessing USERATRRIBUTES ENUM For the naming enumerations
    if (userInfo.toUpperCase().contains("OU=SECURITYGROUPS") || userInfo.toUpperCase().contains("OU=WOM")) {
      String[] memberAttrs = userInfo.split(",");
      // Get the list of Attributes by splitting with ','
      // Sample: CN=SSN9COB,OU=S,OU=Useraccounts,OU=Cob,DC=APAC,DC=bosch,DC=com
      for (String memberAttr : memberAttrs) {
        // Check for attribute 'cn'
        // Sample: CN=SON9COB
        if (memberAttr.toLowerCase(Locale.ENGLISH).startsWith("cn")) {
          // GET The NT ID by splitting with =
          Set<String> subUsers = searchUsersByGroup(memberAttr.split("=")[1]);
          retSet.addAll(subUsers);
          break;
        }
      }
    }
    else {
      String[] memberAttrs = userInfo.split(",");
      // Get the list of Attributes by splitting with ','
      // Sample: CN=SSN9COB,OU=S,OU=Useraccounts,OU=Cob,DC=APAC,DC=bosch,DC=com
      for (String memberAttr : memberAttrs) {
        // Check for attribute 'cn'
        // Sample: CN=SON9COB
        if (memberAttr.toLowerCase(Locale.ENGLISH).startsWith("cn")) {
          // GET The NT ID by splitting with =
          retSet.add(memberAttr.split("=")[1]);
          break;
        }
      }
    }
  }

  /**
   * Method to create UserInfo object and set the required user attributes
   *
   * @param userAttr LDAP user attributes
   * @param password password
   * @return the user's information
   */
  private UserInfo createUserInfo(final SearchResult userAttr) {
    final UserInfo userInfo = new UserInfo();

    String attrVal;

    // Get all attributes of this user
    final Attributes allAttr = userAttr.getAttributes();

    for (UserAttributes attrName : UserAttributes.values()) {
      // get the attribute value
      if (allAttr.get(attrName.getUserAttr()) == null) {
        attrVal = "";
      }
      else {
        attrVal = allAttr.get(attrName.getUserAttr()).toString();
        // retrieved val is in format-> sn: Smith
        attrVal = attrVal.substring(attrVal.indexOf(':') + 1).trim();
      }
      attrName.setUserInfoValue(userInfo, attrVal);
    }

    this.logger.debug("User Info created for : {}", userInfo.getUserName());

    return userInfo;
  }

  /**
   * This method validates the user's DN (Distinguished Name)
   *
   * @param userInfo User infomation
   * @param userDN DN of the user
   * @param password
   * @throws LdapException
   */
  private void validateUserDN(final UserInfo userInfo, final String userDN, final String password)
      throws LdapException {

    DirContext ctx = null;
    try {
      // Create the initial context
      ctx = new InitialDirContext(prepareLdapEnv(userDN, password));
    }
    catch (NamingException exp) {
      throw new LdapException(getPropertyValue(LdapConstants.KEY_MSG_INVALID_PASS) + " " + userInfo.getUserName(), exp);
    }
    finally {
      close(ctx);
    }

  }

  private Hashtable<String, String> prepareLdapEnv(final String userDN, final String password) {
    Hashtable<String, String> env = new Hashtable<>(LdapConstants.DEFAULT_HT_SIZE);

    env.put(Context.INITIAL_CONTEXT_FACTORY, LdapConstants.LDAP_CTX_FACTORY);
    env.put(Context.PROVIDER_URL, getLdapUrl());
    env.put(Context.SECURITY_AUTHENTICATION, LdapConstants.LDAP_AUTH_TYPE);
    env.put(Context.SECURITY_PRINCIPAL, userDN);
    env.put(Context.SECURITY_CREDENTIALS, password);
    env.put(LdapConstants.LDAP_READ_TIMEOUT_PROP, LdapConstants.LDAP_READ_TIMEOUT);

    return env;

  }

  /**
   * Get the LDAP URL from password service
   */
  private String findLdapUrl() {
    String ldapUrlKey = getPropertyValue(LdapConstants.KEY_LDAP_URL);
    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(this.logger);
    return passWordWrapper.getPassword(ldapUrlKey);
  }

  /**
   * This method closes the context
   */
  private void close(final Context ctx) {
    if (ctx != null) {
      try {
        ctx.close();
      }
      catch (NamingException e) {
        this.logger.warn(getPropertyValue(LdapConstants.KEY_MSG_CNTXT_CLOSE_ERR), e);
      }
    }
  }

  /**
   * Close the naming enumeration after use
   */
  private void close(final NamingEnumeration<?> nme) {
    if (nme != null) {
      try {
        nme.close();
      }
      catch (NamingException e) {
        this.logger.warn("Attempted to close NamingEnumeration: " + e.getMessage(), e);
      }
    }
  }


  /**
   * @return the refDirContext
   * @throws LdapException
   */
  private DirContext getRefDirContext() throws LdapException {
    if (this.refDirContext == null) {
      this.refDirContext = createReferenceDirContext();
    }
    return this.refDirContext;
  }

  /**
   * @return the ldapUrl
   */
  private String getLdapUrl() {
    if (this.ldapUrl == null) {
      this.ldapUrl = findLdapUrl();
    }
    return this.ldapUrl;
  }

  /**
   * Generic method to obtain a reference to a DirContext. Uses the LDAP manager credentials.
   *
   * @return DirContext
   */
  private DirContext createReferenceDirContext() throws LdapException {
    final String managerDN = getPropertyValue(LdapConstants.KEY_LDAP_MGR_USER_DN);
    // Get the passord from the Web service
    final String passwordKey = getPasswordKey();

    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(this.logger);
    final String managerPswd = Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), this.logger);

    DirContext dirCtx = null;
    // Create the initial context
    try {
      dirCtx = new InitialDirContext(prepareLdapEnv(managerDN, managerPswd));
      this.logger.debug("Reference user DirContext created");
    }
    catch (CommunicationException cme) {
      throw new LdapException(getPropertyValue(LdapConstants.KEY_MSG_NO_NETWORK), cme);
    }
    catch (NamingException e) {
      throw new LdapException(getPropertyValue(LdapConstants.KEY_MSG_INIT_FAILURE), e);
    }
    return dirCtx;
  }

  /**
   * Utility to convert ENUM to STRING array
   *
   * @param values values
   * @param <T> type
   * @param values
   * @return the string array
   */
  private <T extends Enum<T>> String[] enumNameToStringArray(final T[] values) {
    int counter = 0;
    String[] result = new String[values.length];
    for (T value : values) {
      result[counter] = ((UserAttributes) value).getUserAttr();
      counter++;
    }
    return result;
  }


  private UserInfo getSearchResult(final Set<UserInfo> searchResult, final UserAttributes searchAttr,
      final String searchValue)
      throws LdapException {

    if (searchResult.isEmpty()) {
      throw new LdapException(MSG_SEARCH_RESULT_EMPTY);
    }

    UserInfo ret = searchResult.size() > 1 ? findFromSearchResults(searchResult, searchAttr, searchValue)
        : searchResult.iterator().next();

    this.logger.debug("  LDAP search sucessful. User = {}", ret.getUserName());

    return ret;
  }

  private UserInfo findFromSearchResults(final Set<UserInfo> searchResult, final UserAttributes searchAttr,
      final String searchValue)
      throws LdapException {

    this.logger.debug("    Checking for EXACT match with criteria {} = {} ", searchAttr, searchValue);

    for (UserInfo userInfo : searchResult) {
      String userInfoValue = checkNull(searchAttr.getUserInfoValue(userInfo));
      // Added additional condition to avoid returning Technical account of a NT ID
      if (userInfoValue.equalsIgnoreCase(searchValue) &&
          !userInfo.getDistinguishedName().toLowerCase().contains("CN=auto.home".toLowerCase())) {
        this.logger.debug("    Exact match found for {}", searchValue);
        return userInfo;
      }
    }

    throw new LdapException(MSG_SEARCH_RESULT_EMPTY);
  }

  /**
   * Verify whether logger is initialised.
   */
  private void assertLoggerAvailable() {
    if (this.logger == null) {
      throw new LdapRuntimeException("Logger not initialised");
    }
  }


  /**
   * Get the SID of the current NT user
   *
   * @return
   */
  // iCDM-438
  private String getNTUserSID() {
    return new com.sun.security.auth.module.NTSystem().getUserSID();
  }

  /**
   * Get the user name of the current NT user
   *
   * @return
   */
  private String getNTUserName() {
    return new com.sun.security.auth.module.NTSystem().getName();
  }


  /**
   * This method searches the user from the context
   *
   * @param ctx directory context
   * @param domainName
   * @param username user id
   * @return the search results
   * @throws NamingException when search fails in LDAP server Method searches for users matching the Search Criteria-
   *           From input
   */
  private NamingEnumeration<SearchResult> searchAllUsers(final DirContext ctx,
      final Map<UserAttributes, String> searchCriteria, final String domainName)
      throws NamingException {

    final String searchFilter = createSearchFilter(searchCriteria);

    StringBuilder searchBase = new StringBuilder();

    // Since domain name cannot be added as a User Attribute
    if ((domainName != null) && (domainName.length() > 0)) {
      searchBase.append("dc=").append(domainName).append(',').append(LdapAuthentication.SEARCH_BASE_STRING);
    }
    // Serach base String by Default needs to be appended to get the Correct results.
    else {
      searchBase.append(LdapAuthentication.SEARCH_BASE_STRING);
    }

    // first convert enum attrs to string array
    final String[] userAttrArr = enumNameToStringArray(UserAttributes.values());

    // Set the attributes which needs to be retrieved
    final SearchControls searchCtls = new SearchControls();
    searchCtls.setReturningAttributes(userAttrArr);
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    return ctx.search(searchBase.toString(), searchFilter, searchCtls);
  }

  private NamingEnumeration<SearchResult> searchDomains() throws NamingException, LdapException {
    final SearchControls searchCtls = new SearchControls();
    // Set the attributes which needs to be retrieved
    searchCtls.setReturningAttributes(new String[] { "name" });
    // Set the scope as the immedate children of the domain base
    searchCtls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

    return getRefDirContext().search(LdapAuthentication.SEARCH_BASE_STRING, "name=*", searchCtls);
  }

  /**
   * Create search filter
   *
   * @param searchCriteria search conditions
   */
  private String createSearchFilter(final Map<UserAttributes, String> searchCriteria) {

    // Filter pattern : (&(key1=value1*)(key2=value2*)(key3=value3*)...)
    // For example : (&(givenName=TechnicalUser*)(sn=iCDM*))

    StringBuilder searchFilter = new StringBuilder();
    for (Entry<UserAttributes, String> userInfoElement : searchCriteria.entrySet()) {
      searchFilter.append('(').append(userInfoElement.getKey().getUserAttr()).append('=')
          .append(userInfoElement.getValue()).append("*)");
    }

    // If there are more than one conditions
    if (searchCriteria.size() > LdapConstants.MIN_SEARCH_FILTER) {
      // Prefix
      searchFilter.insert(0, "(&");
      // Suffix
      searchFilter.append(')');
    }

    return searchFilter.toString();
  }

  /**
   * Enabled only if <code>enableCaching</code> is true
   */
  private void addToCache(final String userName, final UserInfo userInfo) {
    if (this.enableCaching) {
      userDetails.put(userName, userInfo);
      this.logger.debug("User details for {} added to cache", userName);
    }
  }

  /**
   * Enabled only if <code>enableCaching</code> is true
   */
  private UserInfo getUserInfoFromCache(final String userName) {
    UserInfo ret = null;
    if (this.enableCaching) {
      ret = userDetails.get(userName);
    }
    return ret;
  }

  /**
   * Enabled only if <code>enableCaching</code> is true
   */
  private void markInvalid(final String userName) {
    if (this.enableCaching) {
      // Add the user name along with a dummy object
      invalidUserNames.put(userName, Boolean.TRUE);
      this.logger.debug("User name {} added to INVALID users list", userName);
    }
  }

  /**
   * Enabled only if <code>enableCaching</code> is true
   */
  private void checkInvalid(final String userName, final String message) throws LdapException {
    if (this.enableCaching && invalidUserNames.containsKey(userName)) {
      this.logger.info("User name {} is present in INVALID users list", userName);
      throw new LdapException(message);
    }
  }

  /**
   * Gets the message for the given key from the resource bundle. If the key is not available,
   * <code>'!' + key + '!'</code> is returned
   *
   * @param resBundle resource bundle
   * @param key the key
   * @return the message for the given key
   */
  private static String getPropertyValue(final String key) {
    try {
      return ResourceBundle.getBundle(LdapConstants.RESOURCE_BUNDLE_NAME).getString(key);
    }
    catch (MissingResourceException e) {
      throw new LdapRuntimeException("Could not find configuration value for key - " + key + ". " + e.getMessage(), e);
    }
  }

  private String checkNull(final String string) {
    return string == null ? "" : string;
  }

  private String getPasswordKey() {
    return this.systemName + ".LDAP_MGR_PASSWORD";
  }

  private static String generateMemberAttrRangeString(final int i) {
    return "member;range=" + (i * PAGESIZE) + "-" + (((i + 1) * PAGESIZE) - 1);
  }

  private static String generateMemberAttrLastRangeString(final int i) {
    return "member;range=" + (i * PAGESIZE) + "-" + "*";
  }


}
