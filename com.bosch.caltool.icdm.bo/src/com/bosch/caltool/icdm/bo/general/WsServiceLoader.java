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
import com.bosch.caltool.icdm.database.entity.general.TWsService;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.WsService;

/**
 * @author bne4cob
 */
public class WsServiceLoader extends AbstractBusinessObject<WsService, TWsService> {

  /**
   * @param serviceData Service Data
   */
  public WsServiceLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WS_SERVICE, TWsService.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WsService createDataObject(final TWsService entity) throws DataException {
    WsService object = new WsService();

    setCommonFields(object, entity);

    object.setModule(entity.getModule());
    object.setName(createServiceKey(entity.getServMethod(), entity.getServUri()));
    object.setServiceScope(entity.getServiceScope());
    object.setServMethod(entity.getServMethod());
    object.setServUri(entity.getServUri());
    object.setDescription(entity.getServDesc());
    object.setDeleted(yOrNToBoolean(entity.getDeleteFlag()));

    return object;
  }


  /**
   * @param method service method
   * @param uri URI
   * @return key
   */
  public static String createServiceKey(final String method, final String uri) {
    return method + ":" + uri;
  }

  /**
   * Get all the existing Web Services from database
   *
   * @return Map. Key - service ID, Value - WsService object
   * @throws DataException error while creating data
   */
  public Map<Long, WsService> getAllWsServices() throws DataException {
    Map<Long, WsService> retMap = new HashMap<>();

    final TypedQuery<TWsService> query =
        getServiceData().getEntMgr().createNamedQuery(TWsService.NQ_ALL_T_WS_SERVICE, TWsService.class);
    final List<TWsService> dbServList = query.getResultList();

    for (TWsService entity : dbServList) {
      retMap.put(entity.getWsServId(), createDataObject(entity));
    }

    return retMap;
  }

  private TWsService getDbService(final String method, final String uri) {
    Long serviceId = GeneralCache.INSTANCE.getServiceId(createServiceKey(method, uri));
    return getEntityObject(serviceId);
  }

  boolean isInternalService(final String method, final String uri) {
    return WServiceScope.INTERNAL.getCode().equals(getDbService(method, uri).getServiceScope());
  }

  static boolean isSameService(final TWsService dbService, final String method, final String uri) {
    return (dbService.getServMethod().equals(method) && dbService.getServUri().equals(uri));
  }
}
