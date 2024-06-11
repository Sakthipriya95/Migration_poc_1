/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TRmCategoryMeasure;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class RmCategoryMeasuresLoader extends AbstractBusinessObject<RmCategoryMeasures, TRmCategoryMeasure> {

  /**
   * @param serviceData serviceData
   */
  public RmCategoryMeasuresLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RM_CATEGORY_MEASURE, TRmCategoryMeasure.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RmCategoryMeasures createDataObject(final TRmCategoryMeasure dbRmCatMeasure) throws DataException {
    RmCategoryMeasures catMeasure = new RmCategoryMeasures();
    catMeasure.setId(dbRmCatMeasure.getCategoryMeasureId());
    catMeasure.setEngMeasure(dbRmCatMeasure.getMeasureEng());
    catMeasure.setGerMeasure(dbRmCatMeasure.getMeasureGer());
    String name = getLangSpecTxt(dbRmCatMeasure.getMeasureEng(), dbRmCatMeasure.getMeasureGer());
    catMeasure.setName(name);
    catMeasure.setCategoryId(dbRmCatMeasure.getTRmCategory().getCategoryId());
    catMeasure.setRiskLevel(dbRmCatMeasure.getTRmRiskLevel().getRiskLevelId());
    return catMeasure;
  }

  /**
   * @return the Map of categories with id as key
   * @throws DataException DataException
   */
  public Map<Long, RmCategoryMeasures> getMeasures() throws DataException {
    Map<Long, RmCategoryMeasures> catMap = new ConcurrentHashMap<>();


    TypedQuery<TRmCategoryMeasure> tQuery =
        getEntMgr().createNamedQuery(TRmCategoryMeasure.GET_ALL, TRmCategoryMeasure.class);


    List<TRmCategoryMeasure> dbCategories = tQuery.getResultList();

    for (TRmCategoryMeasure dbRmCat : dbCategories) {
      catMap.put(dbRmCat.getCategoryMeasureId(), createDataObject(dbRmCat));
    }
    return catMap;
  }

  /**
   * @deprecated
   */
  /**
   * @param catMeasureMap catmeasureMap
   * @return the Map of categories with id as key
   * @throws DataException DataException
   */
  @Deprecated
  public List<RmCategoryMeasures> getMeasures(final Map<Long, Long> catMeasureMap) throws DataException {
    List<RmCategoryMeasures> rmcatMeasuresList = new ArrayList<>();
    TypedQuery<TRmCategoryMeasure> tQuery =
        getEntMgr().createNamedQuery(TRmCategoryMeasure.GET_ALL, TRmCategoryMeasure.class);


    List<TRmCategoryMeasure> dbmeasures = tQuery.getResultList();

    for (Entry<Long, Long> catRisk : catMeasureMap.entrySet()) {
      for (TRmCategoryMeasure dbmeasure : dbmeasures) {
        if (catRisk.getKey().equals(dbmeasure.getTRmCategory().getCategoryId()) &&
            catRisk.getValue().equals(dbmeasure.getTRmRiskLevel().getRiskLevelId())) {
          rmcatMeasuresList.add(createDataObject(dbmeasure));
        }
      }
    }
    return rmcatMeasuresList;

  }


}
