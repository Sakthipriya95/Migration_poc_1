/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;


/**
 * Loader class for CDR Result Function
 *
 * @author BRU2COB
 */
public class CDRResultFunctionLoader extends AbstractBusinessObject<CDRResultFunction, TRvwFunction> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CDRResultFunctionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RES_FUNCTION, TRvwFunction.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDRResultFunction createDataObject(final TRvwFunction entity) throws DataException {
    CDRResultFunction object = new CDRResultFunction();

    setCommonFields(object, entity);
    object.setName(entity.getTFunction().getName());
    object.setDescription(entity.getTFunction().getLongname());
    object.setResultId(entity.getTRvwResult() == null ? null : entity.getTRvwResult().getResultId());
    object.setFunctionId(entity.getTFunction().getId());
    object.setFunctionVers(entity.getTFuncVers());

    return object;
  }

  /**
   * Get all CDR Result Function records in system
   *
   * @return Map. Key - id, Value - CDRResultFunction object
   * @throws DataException error while retrieving data
   */
  public Map<Long, CDRResultFunction> getAll() throws DataException {
    Map<Long, CDRResultFunction> objMap = new ConcurrentHashMap<>();
    TypedQuery<TRvwFunction> tQuery = getEntMgr().createNamedQuery(TRvwFunction.GET_ALL, TRvwFunction.class);
    List<TRvwFunction> dbObj = tQuery.getResultList();
    for (TRvwFunction entity : dbObj) {
      objMap.put(entity.getRvwFunId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get CDR Result Function records using ResultId
   *
   * @param entityObject result id
   * @return Map. Key - id, Value - CDRResultfunction object
   * @throws DataException error while retrieving data
   */
  public Map<Long, CDRResultFunction> getByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, CDRResultFunction> objMap = new ConcurrentHashMap<>();
    Set<TRvwFunction> dbObj = entityObject.getTRvwFunctions();
    if (null != dbObj) {
      for (TRvwFunction entity : dbObj) {
        objMap.put(entity.getRvwFunId(), createDataObject(entity));
      }
    }
    return objMap;
  }


}
