/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.general.TWsSystem;
import com.bosch.caltool.icdm.database.entity.general.TWsSystemService;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.WsSystem;
import com.bosch.caltool.icdm.model.general.WsSystemService;

/**
 * @author bne4cob
 */
public class WsSystemServiceLoader extends AbstractBusinessObject<WsSystemService, TWsSystemService> {

  /**
   * @param serviceData Service Data
   */
  public WsSystemServiceLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WS_SYSTEM_SERVICE, TWsSystemService.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WsSystemService createDataObject(final TWsSystemService entity) throws DataException {
    WsSystemService object = new WsSystemService();

    setCommonFields(object, entity);

    object.setSystemId(entity.getTWsSystem().getSystemId());
    object.setServiceId(entity.getTWsService().getWsServId());

    return object;
  }


  /**
   * Check whether the web service call is authorized for the service client
   *
   * @param method service method
   * @param uri service URI
   * @return true, if service call is valid for the given client
   * @throws DataException error while retrieving data
   */
  public boolean isAuthorised(final String method, final String uri) throws DataException {
    WsSystem system = new WsSystemLoader(getServiceData()).getSystemByToken(getServiceData().getPassword());
    if (WsServerAccessType.ALL.getCode().equals(system.getServAccessType())) {
      return true;
    }

    // return true if service TYPE IS ALL internal and service is internal
    if (WsServerAccessType.ALL_INTERNAL.getCode().equals(system.getServAccessType()) &&
        new WsServiceLoader(getServiceData()).isInternalService(method, uri)) {
      return true;
    }

    // return true, if service access mapping is available
    return isMappedToService(system.getId(), method, uri);
  }

  private boolean isMappedToService(final Long wsSystemId, final String method, final String uri) {
    TWsSystem dbSys = new WsSystemLoader(getServiceData()).getEntityObject(wsSystemId);
    for (TWsSystemService dbSysSrvc : dbSys.getTWsSystemServices()) {
      if (WsServiceLoader.isSameService(dbSysSrvc.getTWsService(), method, uri)) {
        return true;
      }
    }
    return false;
  }


}

