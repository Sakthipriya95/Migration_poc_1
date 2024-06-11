/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.model.general.AzureTokenModel;

/**
 * @author bne4cob
 */
enum UserCache {

                /**
                 * Singleton instance
                 */
                INSTANCE;


  /**
   * User NT IDs and User IDs<br>
   * Key - User NT ID in upper case<br>
   * Value - User ID
   */
  private final ConcurrentMap<String, Long> userIdMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, AzureTokenModel> clientTokenMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   */
  UserCache() {
    try (ServiceData servData = new ServiceData()) {
      loadUserIds(servData);
    }
  }

  /**
   * @param servData
   */
  private void loadUserIds(final ServiceData servData) {
    getLogger().debug("fetching users from database ...");

    UserLoader loader = new UserLoader(servData);
    this.userIdMap.putAll(loader.getAllUserIds());

    getLogger().debug("users retrieved from database: {}", this.userIdMap.size());
  }

  /**
   * IMPORTANT : Do NOT use this method directly. To get User ID/object from User NT ID, use UserLoader methods
   *
   * @param userNtId user NT ID
   * @return user ID
   */
  Long getUserId(final String userNtId) {
    return this.userIdMap.get(userNtId);
  }

  /**
   * Add the user information to cache
   *
   * @param userNtId user NT ID
   * @param userId user ID
   */
  void addUserId(final String userNtId, final long userId) {
    this.userIdMap.put(userNtId, userId);
  }

  /**
   * Add the user Token information to cache
   *
   * @param state unique code to identify client
   * @param azureTokenModel Token details
   */
  void addUserStateToken(final String state, final AzureTokenModel azureTokenModel) {
    this.clientTokenMap.put(state, azureTokenModel);
  }

  /**
   * Remove the user Token information to cache
   *
   * @param state unique code to identify client
   */
  void removeUserStateToken(final String state) {
    this.clientTokenMap.remove(state);
  }

  /**
   * Get the user Token information for client state
   *
   * @param state unique code to identify client
   * @return azureTokenModel Token details
   */
  AzureTokenModel getUserStateToken(final String state) {
    return this.clientTokenMap.get(state);
  }

  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

}
