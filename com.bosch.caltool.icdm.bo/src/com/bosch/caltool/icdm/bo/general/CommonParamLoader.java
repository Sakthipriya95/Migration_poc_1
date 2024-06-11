/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;
import com.bosch.caltool.icdm.database.entity.apic.TabvCommonParam;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author bne4cob
 */
public class CommonParamLoader {

  private final ServiceData servData;

  /**
   * @param servData Service Data
   */
  public CommonParamLoader(final ServiceData servData) {
    this.servData = servData;
  }

  /**
   * @return Map ; Key - parameter ID, Value - Parameter value
   */
  Map<CommonParamKey, String> getAllCommonParams() {

    final TypedQuery<TabvCommonParam> query =
        this.servData.getEntMgr().createNamedQuery(TabvCommonParam.NQ_GET_ALL, TabvCommonParam.class);

    Map<String, String> dbParamsMap = query.getResultList().stream()
        .collect(Collectors.toMap(TabvCommonParam::getParamId, TabvCommonParam::getParamValue));

    EnumMap<CommonParamKey, String> retMap = new EnumMap<>(CommonParamKey.class);

    for (CommonParamKey cpk : CommonParamKey.values()) {
      String val = dbParamsMap.get(cpk.getParamName());
      if (val == null) {
        throw new IcdmRuntimeException("Configuration parameter not found in DB '" + cpk.getParamName() + "'");
      }
      retMap.put(cpk, val);
      dbParamsMap.remove(cpk.getParamName());
    }

    if (!dbParamsMap.isEmpty()) {
      getLogger().info("Extra common parameters found in DB. List : {}", dbParamsMap.keySet());
    }

    return retMap;
  }

  /**
   * Get the value of the given common parameter key
   *
   * @param key parameter key
   * @return parameter value
   */
  public final String getValue(final CommonParamKey key) {
    return GeneralCache.INSTANCE.getCommonParamValue(key);
  }

  /**
   * All common parameters and their values. <br>
   * Use this only when all parameters are required. For specific parameter value, use {@link #getValue(CommonParamKey)}
   *
   * @return Map : Key - parameter Key, Value - Parameter value
   */
  public Map<CommonParamKey, String> getAll() {
    return GeneralCache.INSTANCE.getAllCommonParams();
  }

  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }
}
