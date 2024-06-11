/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TUnit;
import com.bosch.caltool.icdm.model.apic.Unit;

/**
 * @author rgo7cob
 */
public class UnitLoader extends AbstractBusinessObject<Unit, TUnit> {

  /**
   * @param serviceData Service Data
   */
  public UnitLoader(final ServiceData serviceData) {
    super(serviceData, "UNIT", TUnit.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Unit createDataObject(final TUnit entity) throws DataException {
    Unit unit = new Unit();
    unit.setUnitName(entity.getUnit());
    unit.setId(entity.getUnitId());
    unit.setVersion(entity.getVersion());
    unit.setCreatedDate(entity.getCreatedDate());
    return unit;
  }

  /**
   * @return the unit set
   * @throws DataException DataException
   */
  public SortedSet<Unit> getAllUnits() throws DataException {
    SortedSet<Unit> unitSet = new TreeSet<>();
    TypedQuery<TUnit> tQuery = getEntMgr().createNamedQuery(TUnit.GET_ALL, TUnit.class);
    List<TUnit> dbObj = tQuery.getResultList();
    for (TUnit entity : dbObj) {
      unitSet.add(createDataObject(entity));
    }
    return unitSet;
  }

}
