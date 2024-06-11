/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TRmCategory;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategory.CATEGORY_TYPE;
import com.bosch.caltool.icdm.model.rm.RmCategory.CATEGORY_VALUE;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class RmCategoryLoader extends AbstractBusinessObject<RmCategory, TRmCategory> {

  /**
   * @param serviceData serviceData
   */
  public RmCategoryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RM_CATEGORY, TRmCategory.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RmCategory createDataObject(final TRmCategory dbRmCategoty) throws DataException {
    RmCategory category = new RmCategory();
    category.setId(dbRmCategoty.getCategoryId());
    category.setCode(dbRmCategoty.getCategoryCode());
    category.setNameEng(dbRmCategoty.getCategoryEng());
    category.setNameGer(dbRmCategoty.getCategoryGer());
    String name = getLangSpecTxt(dbRmCategoty.getCategoryEng(), dbRmCategoty.getCategoryGer());
    category.setName(name);
    category.setCategoryType(CATEGORY_TYPE.getType(dbRmCategoty.getCatType()));
    category.setValue(CATEGORY_VALUE.getCategoryValue(dbRmCategoty.getCategoryCode()));
    return category;
  }

  /**
   * @return the Map of categories with id as key
   * @throws DataException DataException
   */
  public Map<Long, RmCategory> getCategories() throws DataException {
    Map<Long, RmCategory> catMap = new ConcurrentHashMap<>();


    TypedQuery<TRmCategory> tQuery = getEntMgr().createNamedQuery(TRmCategory.GET_ALL, TRmCategory.class);


    List<TRmCategory> dbCategories = tQuery.getResultList();

    for (TRmCategory dbRmCat : dbCategories) {
      catMap.put(dbRmCat.getCategoryId(), createDataObject(dbRmCat));
    }
    return catMap;
  }


}
