/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author bne4cob
 */
enum GeneralCache {

                   /**
                    * Singleton instance
                    */
                   INSTANCE;

  /**
   * common parameters and values<br>
   * Key - Parameter Key<br>
   * Value - Parameter value
   */
  private final ConcurrentMap<CommonParamKey, String> commonParamsMap = new ConcurrentHashMap<>();

  /**
   * Messages<br>
   * Key - combinatin of group name and name <br>
   * Value - String[2]; item[0] - Eng message, item[1] - Ger message
   */
  private final ConcurrentMap<String, String[]> messageMap = new ConcurrentHashMap<>();


  /**
   * WS Systems<br>
   * Key - token <br>
   * Value - WS System
   */
  private final ConcurrentMap<String, String> wsSystemMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Long> wsSystemIdMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Long> wsServiceMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   */
  GeneralCache() {
    try (ServiceData servData = new ServiceData()) {
      loadCommonParams(servData);
      loadMessages(servData);
      loadWsSystems(servData);
      loadWsServices(servData);
    }
  }

  /**
   * @param servData
   */
  private void loadWsSystems(final ServiceData servData) {
    getLogger().debug("fetching Web service client types from database ...");

    try {
      new WsSystemLoader(servData).getAllWsSystems().values().forEach(sys -> {
        this.wsSystemIdMap.put(sys.getSystemType(), sys.getId());
        this.wsSystemMap.put(sys.getSystemToken(), sys.getSystemType());
      });
    }
    catch (DataException e) {
      getLogger().fatal("Failed to load service client types : " + e.getMessage(), e);
    }

    getLogger().debug("Web service client types fecthed from database: {}", this.wsSystemMap.size());
  }

  private void loadWsServices(final ServiceData servData) {
    getLogger().debug("fetching Web services from database ...");

    try {
      new WsServiceLoader(servData).getAllWsServices().values()
          .forEach(srvc -> this.wsServiceMap.put(srvc.getName(), srvc.getId()));
    }
    catch (DataException e) {
      getLogger().fatal("Failed to load services : " + e.getMessage(), e);
    }

    getLogger().debug("Web services fecthed from database: {}", this.wsServiceMap.size());
  }

  /**
   * @param servData
   */
  private void loadMessages(final ServiceData servData) {
    getLogger().debug("fetching messages from database ...");

    MessageLoader loader = new MessageLoader(servData);
    this.messageMap.putAll(loader.getAllMessages());

    getLogger().debug("messages retrieved from database: {}", this.messageMap.size());

  }

  /**
   * @param servData
   */
  private void loadCommonParams(final ServiceData servData) {
    getLogger().debug("fetching common params from database ...");

    CommonParamLoader cpLoader = new CommonParamLoader(servData);
    this.commonParamsMap.putAll(cpLoader.getAllCommonParams());

    getLogger().debug("common params fetched from database: {}", this.commonParamsMap.size());

  }

  /**
   * IMPORTANT : Do NOT use this method directly. To get messages, use CommonParamLoader methods
   *
   * @param name parameter name
   * @return parameter value
   */
  String getCommonParamValue(final CommonParamKey name) {
    return this.commonParamsMap.get(name);
  }

  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * IMPORTANT : Do NOT use this method directly. To get messages, use MessageLoader methods
   *
   * @param msgKey key
   * @return String[2]; item[0] - Eng message, item[1] - Ger message
   */
  String[] getMessage(final String msgKey) {
    return this.messageMap.get(msgKey);
  }

  /**
   * IMPORTANT : Do NOT use this method directly. Use WSSystemLoader methods instead
   *
   * @param token
   * @return WS System
   */
  String getWsSystem(final String token) {
    return this.wsSystemMap.get(token);
  }

  /**
   * IMPORTANT : Do NOT use this method directly. Use WSSystemLoader methods instead
   *
   * @param token
   * @return WS System ID
   */
  Long getWsSystemIdByToken(final String token) {
    return this.wsSystemIdMap.get(this.wsSystemMap.get(token));
  }

  String getWsToken(final String wsSystem) {
    String ret = null;
    for (Entry<String, String> entry : this.wsSystemMap.entrySet()) {
      if (entry.getValue().equals(wsSystem)) {
        ret = entry.getKey();
        break;
      }
    }
    return ret;
  }

  /**
   * Get All common params
   *
   * @return map
   */
  public Map<CommonParamKey, String> getAllCommonParams() {
    EnumMap<CommonParamKey, String> commonParams = new EnumMap<>(CommonParamKey.class);
    commonParams.putAll(this.commonParamsMap);
    return commonParams;
  }

  /**
   * Get All messages
   *
   * @return map
   */
  public Map<String, String[]> getAllMessages() {
    Map<String, String[]> messages = new HashMap<>();
    messages.putAll(this.messageMap);
    return messages;
  }

  /**
   * @param serviceKey service key
   * @return service ID
   */
  public Long getServiceId(final String serviceKey) {
    return this.wsServiceMap.get(serviceKey);
  }
}
