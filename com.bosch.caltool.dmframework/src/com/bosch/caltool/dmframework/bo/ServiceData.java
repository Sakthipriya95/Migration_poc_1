/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;

import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;

/**
 * @author bne4cob
 */
public class ServiceData implements AutoCloseable {

  /**
   * ID of default entity manager
   */
  private static final String DEF_ENT_MGR_ID = "SD-Default-Entity-Manager";

  private Language language = Language.ENGLISH;

  private final ConcurrentMap<String, ConcurrentMap<String, Object>> dataStore = new ConcurrentHashMap<>();

  private Long userId;

  private String username;
  private String password;
  private String timezone;

  private String cnsSessionId;

  private String serviceId;

  private final CommandExecuter commandExecutor = new CommandExecuter(this);

  /**
   * Map of entity managers created. Usually only one entity manager is created for a service data. For special cases
   * like SSD service interface, a different entity manager can be used.
   * <p>
   * Key - entity manager ID, value - entity manager
   * <p>
   * Entity managers are closed during the closure of service data.
   */
  private final ConcurrentMap<String, EntityManager> emMap = new ConcurrentHashMap<>();

  /**
   * Common service data
   */
  public ServiceData() {
    // Default timezone is UTC
    this.timezone = "UTC";
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return this.username == null ? "" : this.username.toUpperCase(Locale.getDefault());
  }

  /**
   * @param username the username to set
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * @return the passwordEnc
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * @param password the passwordEnc to set
   */
  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * @return the timezone
   */
  public String getTimezone() {
    return this.timezone;
  }

  /**
   * @param timezone the timezone to set
   */
  public void setTimezone(final String timezone) {
    if (!CommonUtils.isEmptyString(timezone)) {
      this.timezone = timezone;
    }
  }


  /**
   * @return Language
   */
  public String getLanguage() {
    return this.language.getText();
  }

  /**
   * @return the language
   */
  public Language getLanguageObj() {
    return this.language;
  }

  /**
   * @param language the language to set
   */
  public void setLanguage(final String language) {
    this.language = Language.getLanguage(language);
  }

  /**
   * @return the cnsSessionId
   */
  public String getCnsSessionId() {
    return this.cnsSessionId;
  }

  /**
   * @param cnsSessionId the cnsSessionId to set
   */
  public void setCnsSessionId(final String cnsSessionId) {
    this.cnsSessionId = cnsSessionId;
  }

  /**
   * @return the default entMgr
   */
  public EntityManager getEntMgr() {
    return getEntMgr(DEF_ENT_MGR_ID);
  }


  /**
   * @param emId Entity Manager ID
   * @return the entMgr for the given ID.
   */
  public EntityManager getEntMgr(final String emId) {
    return this.emMap.computeIfAbsent(emId,
        (id) -> JPAUtils.createEntityManager(ObjectStore.getInstance().getEntityManagerFactory()));
  }

  /**
   * Close the service resources
   */
  @Override
  public void close() {
    closeEMs();
    clearAllData();
  }

  private void closeEMs() {
    this.emMap.values().forEach(JPAUtils::closeEntityManager);
    this.emMap.clear();
  }

  /**
   * Store data in session cache
   *
   * @param owner owner of data
   * @param key key
   * @param data data to store
   */
  public void storeData(final Class<?> owner, final String key, final Object data) {
    String ownerName = owner.getName();
    ConcurrentMap<String, Object> dataMap =
        this.dataStore.computeIfAbsent(ownerName, (id) -> new ConcurrentHashMap<>());

    dataMap.put(key, data);
  }

  /**
   * Retrieve the data already stored in session cache.
   *
   * @param owner owner of data
   * @param key key
   * @return the data, if already stored. else null
   */
  public Object retrieveData(final Class<?> owner, final String key) {
    String ownerName = owner.getName();
    ConcurrentMap<String, Object> dataMap = this.dataStore.get(ownerName);
    if (dataMap == null) {
      return null;
    }
    return dataMap.get(key);
  }

  /**
   * Clear the data stored in session cache with the given key by the owner
   *
   * @param owner data owner
   * @param key key
   */
  public void clearData(final Class<?> owner, final String key) {
    String ownerName = owner.getName();
    ConcurrentMap<String, Object> dataMap = this.dataStore.get(ownerName);
    if (dataMap != null) {
      dataMap.remove(key);
    }
  }

  /**
   * Clear all data stored in session cache by the given owner
   *
   * @param owner data owner
   */
  public void clearAllData(final Class<?> owner) {
    String ownerName = owner.getName();
    ConcurrentMap<String, Object> dataMap = this.dataStore.get(ownerName);
    if (dataMap != null) {
      dataMap.clear();
    }
  }

  /**
   * Clear all data stored in session cache
   */
  public void clearAllData() {
    this.dataStore.clear();
  }

  /**
   * @return true if user is valid user in iCDM
   */
  public boolean isAuthenticatedUser() {
    return this.userId != null;
  }

  /**
   * @return the userId
   */
  public Long getUserId() {
    return this.userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  /**
   * Copies the service data information from this object to the given input
   *
   * @param sdTo service data to be copied to
   * @param copyStore if true, data in store is also copied. Note : this poses a risk for concurrent operations if data
   *          stored inside does not support concurrency
   */
  public void copyTo(final ServiceData sdTo, final boolean copyStore) {
    sdTo.setUsername(getUsername());
    sdTo.setPassword(getPassword());
    sdTo.setUserId(getUserId());
    sdTo.setServiceId(getServiceId());
    sdTo.setLanguage(getLanguage());
    sdTo.setTimezone(getTimezone());
    sdTo.setCnsSessionId(getCnsSessionId());

    if (copyStore) {
      sdTo.dataStore.putAll(this.dataStore);
    }
  }

  /**
   * @return the commandExecutor
   */
  public CommandExecuter getCommandExecutor() {
    return this.commandExecutor;
  }


  /**
   * @return the serviceId
   */
  public String getServiceId() {
    return this.serviceId;
  }


  /**
   * @param serviceId the serviceId to set
   */
  public void setServiceId(final String serviceId) {
    this.serviceId = serviceId;
  }

}
