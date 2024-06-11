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
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrMeasureUnit;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrMeasureUnit;

/**
 * @author bru2cob
 */
public class EmrMeasureUnitLoader extends AbstractBusinessObject<EmrMeasureUnit, TEmrMeasureUnit> {

  /**
   * @param serviceData Service Data
   */
  public EmrMeasureUnitLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_MEASURE_UNIT, TEmrMeasureUnit.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrMeasureUnit createDataObject(final TEmrMeasureUnit entity) throws DataException {
    EmrMeasureUnit unitLoader = new EmrMeasureUnit();
    unitLoader.setId(entity.getMuId());
    unitLoader.setMeasureUnitName(entity.getMeasureUnitName());
    unitLoader.setVersion(entity.getVersion());
    return unitLoader;
  }

  /**
   * @return the Map of measure unit with id as key
   * @throws DataException DataException
   */
  public Map<String, EmrMeasureUnit> getAllMeasureUnits() throws DataException {
    Map<String, EmrMeasureUnit> measureUnitMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrMeasureUnit> tQuery = getEntMgr().createNamedQuery(TEmrMeasureUnit.GET_ALL, TEmrMeasureUnit.class);

    List<TEmrMeasureUnit> dbMeasureUnit = tQuery.getResultList();

    for (TEmrMeasureUnit dbEmrMeasureUnit : dbMeasureUnit) {
      measureUnitMap.put(dbEmrMeasureUnit.getMeasureUnitName(), createDataObject(dbEmrMeasureUnit));
    }
    return measureUnitMap;
  }
}
