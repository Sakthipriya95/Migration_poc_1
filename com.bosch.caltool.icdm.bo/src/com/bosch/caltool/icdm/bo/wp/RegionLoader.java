/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */


package com.bosch.caltool.icdm.bo.wp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TRegion;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.wp.Region;


/**
 * Loader class for Region
 *
 * @author apj4cob
 */
public class RegionLoader extends AbstractBusinessObject<Region, TRegion> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RegionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.REGION, TRegion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Region createDataObject(final TRegion entity) throws DataException {
    Region object = new Region();
    setCommonFields(object, entity);
    object.setRegionName(getLangSpecTxt(entity.getRegionNameEng(), entity.getRegionNameEng()));
    object.setRegionCode(entity.getRegionCode());
    object.setRegionNameEng(entity.getRegionNameEng());
    object.setRegionNameGer(entity.getRegionNameGer());
    return object;
  }

  /**
   * Get all Region records in system
   *
   * @return Map. Key - id, Value - Region object
   * @throws DataException error while retrieving data
   */
  public Map<String, Region> getAll() throws DataException {
    Map<String, Region> objMap = new ConcurrentHashMap<>();
    TypedQuery<TRegion> tQuery = getEntMgr().createNamedQuery(TRegion.GET_ALL_REGION, TRegion.class);
    List<TRegion> dbObj = tQuery.getResultList();
    for (TRegion entity : dbObj) {
      objMap.put(entity.getRegionNameEng(), createDataObject(entity));
    }
    return objMap;
  }

}
