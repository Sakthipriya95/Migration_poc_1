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
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrColumn;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrColumn;

/**
 * @author bru2cob
 */
public class EmrColumnLoader extends AbstractBusinessObject<EmrColumn, TEmrColumn> {

  /**
   * @param serviceData Service Data
   */
  public EmrColumnLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_COLUMN, TEmrColumn.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrColumn createDataObject(final TEmrColumn entity) throws DataException {
    EmrColumn column = new EmrColumn();
    column.setId(entity.getColId());
    column.setName(entity.getColumnName());
    column.setNomalizedFlag(entity.getNomalizedFlag());
    column.setNumericFlag(entity.getNumericFlag());
    column.setVersion(entity.getVersion());
    return column;
  }

  /**
   * @return the Map of columns with id as key
   * @throws DataException DataException
   */
  public Map<String, EmrColumn> getAllColumn() throws DataException {
    Map<String, EmrColumn> colMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrColumn> tQuery = getEntMgr().createNamedQuery(TEmrColumn.GET_ALL, TEmrColumn.class);

    List<TEmrColumn> dbColumns = tQuery.getResultList();

    for (TEmrColumn dbEmrCol : dbColumns) {
      colMap.put(dbEmrCol.getColumnName(), createDataObject(dbEmrCol));
    }
    return colMap;
  }
}
