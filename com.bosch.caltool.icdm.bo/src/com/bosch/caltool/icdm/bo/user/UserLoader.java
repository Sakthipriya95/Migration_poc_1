/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.WsSystemLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.AzureTokenModel;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author bne4cob
 */
public class UserLoader extends AbstractBusinessObject<User, TabvApicUser> {


  /**
   * User authentication status
   *
   * @author bne4cob
   */
  public enum Status {
                      /**
                       * Authenticated
                       */
                      SUCCESS,
                      /**
                       * User is empty
                       */
                      EMPTY_USER,
                      /**
                       * WS System code is invalid
                       */
                      INVALID_WS_SYSTEM,
                      /**
                       * User not available in ICDM
                       */
                      USER_NOT_IN_ICDM;

  }

  /**
   * @param serviceData service Data
   */
  public UserLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.APIC_USER, TabvApicUser.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected User createDataObject(final TabvApicUser entity) throws DataException {
    User user = new User();

    setCommonFields(user, entity);

    user.setDepartment(entity.getDepartment());
    user.setFirstName(entity.getFirstname());
    user.setLastName(entity.getLastname());
    user.setName(entity.getUsername().toUpperCase());
    user.setDescription(getDisplayName(entity));
    user.setDisclaimerAcceptedDate(timestamp2String(entity.getDisclaimerAcceptnceDate()));
    return user;
  }

  /**
   * @param entity
   * @return
   */
  private String getDisplayName(final TabvApicUser entity) {
    return formDisplayName(entity.getFirstname(), entity.getLastname(), entity.getDepartment(), entity.getUsername());
  }


  /**
   * @param entity
   * @return
   */
  String formDisplayName(final String firstName, final String lastName, final String department,
      final String userName) {
    StringBuilder displayName = new StringBuilder();

    if (!CommonUtils.isEmptyString(lastName)) {
      displayName.append(lastName).append(", ");
    }
    if (!CommonUtils.isEmptyString(firstName)) {
      displayName.append(firstName);
    }

    if (CommonUtils.isEmptyString(displayName.toString())) {
      displayName.append(userName);
    }

    if (!CommonUtils.isEmptyString(department)) {
      displayName.append(" (").append(department).append(")");
    }

    return displayName.toString();
  }

  /**
   * @param username user name
   * @return entity
   */
  public TabvApicUser getEntityObjectByUserName(final String username) {

    TabvApicUser ret = null;

    String ntUser = formatNtId(username);
    Long userId = UserCache.INSTANCE.getUserId(ntUser);

    if (userId == null) {
      TypedQuery<TabvApicUser> dbQuery =
          getEntMgr().createNamedQuery(TabvApicUser.NQ_GET_APIC_USER_BY_NAME, TabvApicUser.class);
      dbQuery.setParameter("username", ntUser);

      List<TabvApicUser> dbResList = dbQuery.getResultList();
      if (dbResList.isEmpty()) {
        getLogger().info("User '{}' not found in iCDM", username);
      }
      else {
        ret = dbResList.get(0);
        UserCache.INSTANCE.addUserId(ntUser, ret.getUserId());
      }
    }
    else {
      ret = getEntityObject(userId);
    }
    return ret;
  }

  /**
   * @param state unique code to identify client
   * @param azureTokenModel Token details
   */
  public void addUserStateToken(final String state, final AzureTokenModel azureTokenModel) {
    UserCache.INSTANCE.addUserStateToken(state, azureTokenModel);
  }

  /**
   * @param state unique code to identify client
   */
  public void removeUserStateToken(final String state) {
    UserCache.INSTANCE.removeUserStateToken(state);
  }

  /**
   * @param state unique code to identify client
   * @return azureTokenModel Token details
   */
  public AzureTokenModel getUserStateToken(final String state) {
    return UserCache.INSTANCE.getUserStateToken(state);
  }

  /**
   * @param userNameSet Set of NT ids whose details are to be fetched
   * @return Set of TabvApicUser object
   */
  public Map<String, TabvApicUser> getEntityObjectsByUserNameSet(final Set<String> userNameSet) {

    Map<String, TabvApicUser> dbApicUserMap = new HashMap<>();

    Set<String> userNtIds = userNameSet.stream().map(this::formatNtId).collect(Collectors.toSet());

    Set<String> ntIdsNotInCache = new HashSet<>();
    userNtIds.stream().forEach(ntUser -> {
      Long userId = UserCache.INSTANCE.getUserId(ntUser);
      if (userId == null) {
        ntIdsNotInCache.add(ntUser);
      }
      else {
        dbApicUserMap.put(ntUser, getEntityObject(userId));
      }
    });

    // For the NT IDs not in cache, fetch details from DB
    if (CommonUtils.isNotEmpty(ntIdsNotInCache)) {
      TypedQuery<TabvApicUser> dbQuery =
          getEntMgr().createNamedQuery(TabvApicUser.NQ_GET_APIC_USER_BY_NAMES_LIST, TabvApicUser.class);
      dbQuery.setParameter("usernamecoll", ntIdsNotInCache);

      List<TabvApicUser> dbResList = dbQuery.getResultList();

      dbResList.forEach(dbRes -> {
        UserCache.INSTANCE.addUserId(formatNtId(dbRes.getUsername()), dbRes.getUserId());
        dbApicUserMap.put(formatNtId(dbRes.getUsername()), dbRes);
      });
    }

    return dbApicUserMap;
  }

  /**
   * Get the User ID from user name
   *
   * @param username user's NT ID
   * @return User ID
   */
  public Long getUserIdByUserName(final String username) {
    TabvApicUser entity = getEntityObjectByUserName(username);
    return entity == null ? null : entity.getUserId();
  }

  /**
   * @param userId user's ID
   * @return user's NT ID
   * @throws DataException error while retrieving user
   */
  public String getUsernameById(final Long userId) throws DataException {
    return getDataObjectByID(userId).getName();
  }

  /**
   * @param username user name
   * @return entity
   * @throws DataException if object cannot be found
   */
  public User getDataObjectByUserName(final String username) throws DataException {
    TabvApicUser dbUser = getEntityObjectByUserName(username);
    if (dbUser == null) {
      getLogger().warn("DB User with NT ID '" + username + "' not found");
      throw new DataException("User with NT ID '" + username + "' not found");
    }
    return createDataObject(dbUser);
  }

  /**
   * @param username user name
   * @return DisplayName Name to be displayed
   */
  public String getDisplayName(final String username) {
    String displayName = username;

    TabvApicUser dbUser = getEntityObjectByUserName(username);
    if (dbUser == null) {
      try {
        displayName = new LdapAuthenticationWrapper().getUserDetails(username).getDisplayName();
      }
      catch (LdapException e) {
        getLogger().error(e.getMessage(), e);
      }
    }
    else {
      displayName = getDisplayName(dbUser);
    }

    return displayName;
  }

  /**
   * @return data
   * @throws DataException if object cannot be found
   */
  public User getDataObjectCurrentUser() throws DataException {
    if (getServiceData().isAuthenticatedUser()) {
      return getDataObjectByID(getServiceData().getUserId());
    }
    // This additional step is added since the method is used to get user id, first time, from user name
    String username = getServiceData().getUsername();
    if (CommonUtils.isNotEmptyString(username)) {
      return getDataObjectByUserName(username);
    }

    // User name is empty
    throw new DataException("User is empty");

  }

  /**
   * Load the NT IDs and User IDs of all users in the DB, to the map
   *
   * @param userIdMap
   */
  Map<String, Long> getAllUserIds() {
    final Map<String, Long> userIdMap = new HashMap<>();
    final TypedQuery<TabvApicUser> qDbUser =
        getEntMgr().createNamedQuery(TabvApicUser.NQ_GET_ALL_APIC_USERS, TabvApicUser.class);
    final List<TabvApicUser> dbUsers = qDbUser.getResultList();

    for (TabvApicUser dbUser : dbUsers) {
      userIdMap.put(formatNtId(dbUser.getUsername()), dbUser.getUserId());
    }
    return userIdMap;
  }


  private String formatNtId(final String userNtId) {
    return userNtId.trim().toUpperCase(Locale.ENGLISH);
  }

  /**
   * @return status code, if authenticated. status is also set to serviceData
   */
  public Status authenticateCurrentUser() {
    String userName = getServiceData().getUsername();
    if (CommonUtils.isEmptyString(userName)) {
      getLogger().debug("User '{}' authentication error. User name not provided.", userName);
      return Status.EMPTY_USER;
    }

    // Validate System token
    WsSystemLoader wsSysLdr = new WsSystemLoader(getServiceData());
    if (!wsSysLdr.isValidWsSystem()) {
      getLogger().debug("User '{}' authentication error. Missing/invalid web service token.", userName);
      return Status.INVALID_WS_SYSTEM;
    }

    try {
      User user = getDataObjectCurrentUser();
      getServiceData().setUserId(user.getId());

      getLogger().debug("User '{}' authenticated.", userName);
      return Status.SUCCESS;
    }
    catch (DataException exp) {
      // Only at debug level, since 'user' authentication is not a must
      getLogger().debug("User '" + userName + "' authentication error. " + exp.getMessage(), exp);
      return Status.USER_NOT_IN_ICDM;
    }
  }

  /**
   * @param includeMonicaAuditor true, if MoniCa user also to be included
   * @return users as sorted set
   * @throws DataException data retrieval error
   */
  public SortedSet<User> getAllApicUsers(final boolean includeMonicaAuditor) throws DataException {
    final TypedQuery<TabvApicUser> qDbUser =
        getEntMgr().createNamedQuery(TabvApicUser.NQ_GET_ALL_APIC_USERS, TabvApicUser.class);
    final List<TabvApicUser> dbUsers = qDbUser.getResultList();

    SortedSet<User> userSet = new TreeSet<>();
    for (TabvApicUser tabvApicUser : dbUsers) {
      userSet.add(createDataObject(tabvApicUser));
    }
    if (!includeMonicaAuditor) {
      userSet.remove(getDataObjectByID(ApicConstants.MONICA_USER_ID));
    }
    return userSet;
  }

  /**
   * @return
   * @throws DataException
   */
  Set<User> internalGetAllApicWriteUsers() throws DataException {
    Set<User> retSet = new HashSet<>();

    final TypedQuery<TabvApicAccessRight> query =
        getEntMgr().createNamedQuery(TabvApicAccessRight.NQ_GET_ALL_APIC_WRITE_USERS, TabvApicAccessRight.class);
    final List<TabvApicAccessRight> retList = query.getResultList();

    for (TabvApicAccessRight entity : retList) {
      retSet.add(createDataObject(entity.getTabvApicUser()));
    }
    return retSet;
  }

  /**
   * Get all users for node access right selection
   *
   * @param nodeType NODE_TYPE
   * @param idSet nodeId
   * @param includeApicWriteUsers boolean
   * @param includeMonicaAuditor boolean
   * @return users for node access selection
   * @throws DataException exception
   */
  public SortedSet<User> getAllUsersForNodeAccess(final String nodeType, final Set<Long> idSet,
      final boolean includeApicWriteUsers, final boolean includeMonicaAuditor)
      throws DataException {
    NodeAccessDetails nodeAccessDet =
        new NodeAccessLoader(getServiceData()).getAllNodeAccessByNode(nodeType, null, null, idSet);
    SortedSet<User> allUsers = getAllApicUsers(includeMonicaAuditor);
    // remove existing users
    allUsers.removeAll(nodeAccessDet.getUserMap().values());
    if (!includeApicWriteUsers) {
      allUsers.removeAll(internalGetAllApicWriteUsers());
    }
    return allUsers;
  }
}
