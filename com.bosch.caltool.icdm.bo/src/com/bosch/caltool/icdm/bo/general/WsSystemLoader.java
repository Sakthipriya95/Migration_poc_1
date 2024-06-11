/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.general.TWsSystem;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.WsSystem;

/**
 * @author bne4cob
 */
public class WsSystemLoader extends AbstractBusinessObject<WsSystem, TWsSystem> {

  private static final String WS_SYSTEM_ICDM = "ICDM";

  /**
   * @param serviceData Service Data
   */
  public WsSystemLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WS_SYSTEM, TWsSystem.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WsSystem createDataObject(final TWsSystem entity) throws DataException {
    WsSystem object = new WsSystem();

    setCommonFields(object, entity);

    object.setSystemType(entity.getSystemType());
    object.setSystemToken(entity.getSystemToken());
    object.setServAccessType(entity.getServAccessType());

    return object;
  }

  /**
   * @return true if the request comes from a valid WS system
   */
  public boolean isValidWsSystem() {
    String token = getServiceData().getPassword();
    if (CommonUtils.isEmptyString(token)) {
      return false;
    }

    String wsSystem = GeneralCache.INSTANCE.getWsSystem(token);
    getLogger().info("WS client system is : {}, User is : {}", wsSystem, getServiceData().getUsername());

    return !CommonUtils.isEmptyString(wsSystem);
  }

  /**
   * Get the token for the given WS system
   *
   * @param wsSystem WS System
   * @return token for the WS system
   */
  public String getToken(final String wsSystem) {
    return GeneralCache.INSTANCE.getWsToken(wsSystem);
  }

  /**
   * @return WS token for iCDM system
   */
  public String getTokenIcdm() {
    return getToken(WS_SYSTEM_ICDM);
  }

  /**
   * @return Map. Key - system type. Value - system ID
   * @throws DataException error while creating data
   */
  Map<String, WsSystem> getAllWsSystems() throws DataException {
    Map<String, WsSystem> retMap = new HashMap<>();

    final TypedQuery<TWsSystem> query =
        getServiceData().getEntMgr().createNamedQuery(TWsSystem.ALL_T_WS_SYSTEM, TWsSystem.class);
    final List<TWsSystem> dbSysList = query.getResultList();

    for (TWsSystem entity : dbSysList) {
      retMap.put(entity.getSystemType(), createDataObject(entity));
    }

    return retMap;
  }

  WsSystem getSystemByToken(final String token) throws DataException {
    Long objId = GeneralCache.INSTANCE.getWsSystemIdByToken(token);
    return createDataObject(getEntityObject(objId));

  }

}
