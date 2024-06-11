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
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrCategory;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrCategory;

/**
 * @author bru2cob
 */
public class EmrCategoryLoader extends AbstractBusinessObject<EmrCategory, TEmrCategory> {

  /**
   * @param serviceData Service Data
   */
  public EmrCategoryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_CATEGORY, TEmrCategory.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrCategory createDataObject(final TEmrCategory entity) throws DataException {
    EmrCategory category = new EmrCategory();
    category.setId(entity.getCatId());
    category.setName(entity.getCatName());
    return category;
  }

  /**
   * @return the Map of categories with id as key
   * @throws DataException DataException
   */
  public Map<String, EmrCategory> getCategories() throws DataException {
    Map<String, EmrCategory> catMap = new ConcurrentHashMap<>();


    TypedQuery<TEmrCategory> tQuery = getEntMgr().createNamedQuery(TEmrCategory.GET_ALL, TEmrCategory.class);


    List<TEmrCategory> dbCategories = tQuery.getResultList();

    for (TEmrCategory dbEmrCat : dbCategories) {
      catMap.put(dbEmrCat.getCatName().toUpperCase(), createDataObject(dbEmrCat));
    }
    return catMap;
  }
}
