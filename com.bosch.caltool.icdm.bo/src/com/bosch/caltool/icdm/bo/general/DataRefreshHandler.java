/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.util.LoaderProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.DataRefreshInput;
import com.bosch.caltool.icdm.model.general.DataRefreshResult;


/**
 * @author bne4cob
 */
public class DataRefreshHandler extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData Service Data
   */
  public DataRefreshHandler(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param input DataRefreshInput
   * @return DataRefreshResult
   */
  public DataRefreshResult refreshData(final DataRefreshInput input) {
    getLogger().debug("Refreshing data object(s). Input details : {}", input);

    DataRefreshResult ret = new DataRefreshResult();

    String status;

    for (Entry<String, Set<Long>> entry : input.getInput().entrySet()) {
      AbstractBusinessObject<?, ?> loader = null;
      status = null;

      try {
        loader = new LoaderProvider(getServiceData()).createInstance(entry.getKey());
      }
      catch (IcdmException e) {
        getLogger().error(e.getMessage(), e);
        status = e.getMessage();
      }
      doRefreshData(ret, status, entry, loader);
    }

    getLogger().debug("Refreshing data objects completed. Result : {}", ret);

    return ret;
  }

  private void doRefreshData(final DataRefreshResult ret, final String startStatus,
      final Entry<String, Set<Long>> entry, final AbstractBusinessObject<?, ?> loader) {

    Object entity;
    String status = startStatus;
    for (Long objId : entry.getValue()) {
      if (loader != null) {
        if (loader.isValidId(objId)) {
          entity = loader.getEntityObject(objId);
          getEntMgr().refresh(entity);
          status = "OK";
        }
        else {
          status = "Invalid ID " + objId;
        }
      }
      ret.getRefreshStatus().computeIfAbsent(entry.getKey(), v -> new TreeMap<>()).put(objId, status);
    }
  }


}
