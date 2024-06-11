/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class UserServiceClient.
 *
 * @author NIP4COB
 */
public class UserServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor.
   */
  public UserServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_USER);
  }

  /**
   * Get Apic User by username.
   *
   * @param userId userid
   * @return User object
   * @throws ApicWebServiceException icdm exception
   */
  public User getApicUserById(final Long userId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, userId);
    return get(wsTarget, User.class);
  }


  /**
   * Gets the all.
   *
   * @param includeMonicaAuditor boolean
   * @return SortedSet<user>
   * @throws ApicWebServiceException the apic web service exception
   */
  public SortedSet<User> getAll(final boolean includeMonicaAuditor) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL)
        .queryParam(WsCommonConstants.RWS_INCLUDE_MONICA_AUDITOR, includeMonicaAuditor);

    GenericType<SortedSet<User>> users = new GenericType<SortedSet<User>>() {};
    SortedSet<User> retList = get(wsTarget, users);

    LOGGER.debug("Users loaded. Number of items = ", retList.size());

    return retList;

  }

  /**
   * Gets the all.
   *
   * @param includeApicWriteUsers boolean
   * @param includeMonicaAuditor boolean
   * @param nodeType NODE_TYPE
   * @param nodeId node ID
   * @return SortedSet<user>
   * @throws ApicWebServiceException the apic web service exception
   */
  public SortedSet<User> getAllByNode(final boolean includeApicWriteUsers, final boolean includeMonicaAuditor,
      final IModelType nodeType, final Long... nodeId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_NODE)
        .queryParam(WsCommonConstants.RWS_QP_NODE_TYPE, nodeType.getTypeCode())
        .queryParam(WsCommonConstants.RWS_QP_NODE_ID, nodeId)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_APIC_WRITE_USERS, includeApicWriteUsers)
        .queryParam(WsCommonConstants.RWS_INCLUDE_MONICA_AUDITOR, includeMonicaAuditor);

    GenericType<SortedSet<User>> users = new GenericType<SortedSet<User>>() {};
    SortedSet<User> retList = get(wsTarget, users);

    LOGGER.debug("Users loaded. Number of items = ", retList.size());

    return retList;

  }

  /**
   * Get Apic User by username.
   *
   * @param usernameList usernameList
   * @return User object
   * @throws ApicWebServiceException icdm exception
   */
  public Map<String, User> getApicUserByUsername(final List<String> usernameList) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_APIC_USER_BY_USERNAME);
    GenericType<Map<String, User>> users = new GenericType<Map<String, User>>() {};

    return post(wsTarget, usernameList, users);

  }

  /**
   * @param userName ntuserid
   * @return the user for a single user name
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public User getApicUserByUsername(final String userName) throws ApicWebServiceException {
    List<String> userNameList = new ArrayList<>();
    userNameList.add(userName);
    return getApicUserByUsername(userNameList).get(userName);

  }

  /**
   * Get details of current user.
   *
   * @return User current user object
   * @throws ApicWebServiceException icdm exception
   */
  public User getCurrentApicUser() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_APIC_CURRENT_USER);
    return get(wsTarget, User.class);
  }

  /**
   * @param user user object
   * @return created user
   * @throws ApicWebServiceException error during webservice call
   */
  public User create(final User user) throws ApicWebServiceException {
    return create(getWsBase(), user);
  }

  /**
   * @param user user object
   * @return the updated user
   * @throws ApicWebServiceException error during webservice call
   */
  public User update(final User user) throws ApicWebServiceException {
    return update(getWsBase(), user);
  }
}