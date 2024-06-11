/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrColumnValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrColumnValue;

/**
 * @author bru2cob
 */
public class EmrColumnValueLoader extends AbstractBusinessObject<EmrColumnValue, TEmrColumnValue> {

  /**
   * @param serviceData Service Data
   */
  public EmrColumnValueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_COLUMN_VALUE, TEmrColumnValue.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrColumnValue createDataObject(final TEmrColumnValue entity) throws DataException {
    EmrColumnValue colValue = new EmrColumnValue();
    colValue.setId(entity.getColValueId());
    colValue.setValue(entity.getColValue());
    colValue.setVersion(entity.getVersion());
    colValue.setColId(entity.getTEmrColumn().getColId());
    return colValue;
  }

  /**
   * @return the Map of columns value with id as key
   * @throws DataException DataException
   */
  public Map<String, EmrColumnValue> getAllColumnVal() throws DataException {
    Map<String, EmrColumnValue> colValMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrColumnValue> tQuery = getEntMgr().createNamedQuery(TEmrColumnValue.GET_ALL, TEmrColumnValue.class);

    List<TEmrColumnValue> dbColumns = tQuery.getResultList();

    for (TEmrColumnValue dbEmrCol : dbColumns) {
      colValMap.put(dbEmrCol.getColValue(), createDataObject(dbEmrCol));
    }
    return colValMap;
  }
}
