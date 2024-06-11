/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TAlternateAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.AlternateAttr;

/**
 * @author dmr1cob
 */
public class AlternateAttrLoader extends AbstractBusinessObject<AlternateAttr, TAlternateAttr> {


  /**
   * @param servData Service Data
   */
  public AlternateAttrLoader(final ServiceData servData) {
    super(servData, MODEL_TYPE.ALTERNATE_ATTR, TAlternateAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AlternateAttr createDataObject(final TAlternateAttr entity) throws DataException {
    AlternateAttr alternateAttr = new AlternateAttr();
    alternateAttr.setAttrPk(entity.getAttrPk());
    alternateAttr.setAlternateAttrId(entity.getAlternateAttrId().longValue());
    alternateAttr.setAttrId(entity.getAttrId().longValue());
    alternateAttr.setVersion(entity.getVersion().longValue());
    return alternateAttr;
  }

  public Map<Long, Long> getAllAlternateAttrs() {

    Map<Long, Long> alternateAttrMap = new HashMap<>();
    final String query = "SELECT t FROM TAlternateAttr t";
    final TypedQuery<TAlternateAttr> typeQuery = getEntMgr().createQuery(query, TAlternateAttr.class);
    final List<TAlternateAttr> tAlternateAttrList = typeQuery.getResultList();

    for (TAlternateAttr alternateAttr : tAlternateAttrList) {
      alternateAttrMap.put(alternateAttr.getAttrId().longValue(), alternateAttr.getAlternateAttrId().longValue());
    }
    return alternateAttrMap;
  }
}
