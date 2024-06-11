/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParametersSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;


/**
 * Loader class for Review Parameters Secondary
 *
 * @author bru2cob
 */
public class RvwParametersSecondaryLoader
    extends AbstractBusinessObject<RvwParametersSecondary, TRvwParametersSecondary> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwParametersSecondaryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RESULT_PARAM_SECONDARY, TRvwParametersSecondary.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwParametersSecondary createDataObject(final TRvwParametersSecondary entity) throws DataException {
    RvwParametersSecondary object = new RvwParametersSecondary();

    setCommonFields(object, entity);

    object.setRvwParamId(entity.getTRvwParameter().getRvwParamId());
    object.setName(entity.getTRvwParameter().getTParameter().getName());
    object.setSecReviewId(entity.getTRvwResultsSecondary().getSecReviewId());
    object.setLowerLimit(entity.getLowerLimit() == null ? null : entity.getLowerLimit());
    object.setUpperLimit(entity.getUpperLimit() == null ? null : entity.getUpperLimit());
    object.setRefValue(entity.getRefValue());
    object.setRefUnit(entity.getRefUnit());
    object.setResult(entity.getResult());
    object.setChangeFlag(entity.getChangeFlag());
    object.setMatchRefFlag(entity.getMatchRefFlag());
    object.setBitwiseLimit(entity.getBitwiseLimit());
    object.setIsbitwise(entity.getIsbitwise());
    object.setLabObjId(entity.getLabObjId());
    object.setRevId(entity.getRevId());
    object.setRvwMethod(entity.getRvwMethod());

    return object;
  }

  /**
   * @param entity
   * @return
   */
  public Map<Long, Map<Long, RvwParametersSecondary>> getSecondaryParams(final TRvwParameter dbParam)
      throws DataException {
    Set<TRvwParametersSecondary> tRvwSecondaryParams = dbParam.getTRvwParametersSecondaries();
    Map<Long, Map<Long, RvwParametersSecondary>> secondaryParamMap = new ConcurrentHashMap<>();
    for (TRvwParametersSecondary entity : tRvwSecondaryParams) {

      Map<Long, RvwParametersSecondary> files = secondaryParamMap.get(dbParam.getRvwParamId());
      if (null == files) {
        files = new ConcurrentHashMap<>();
        secondaryParamMap.put(dbParam.getRvwParamId(), files);
      }
      files.put(entity.getSecRvwParamId(), createDataObject(entity));
    }
    return secondaryParamMap;

  }

  /**
   * @param secondaryResultId
   * @return
   * @throws DataException
   */
  public Map<Long, RvwParametersSecondary> getResultSecondaryParams(final Long secondaryResultId) throws DataException {
    TRvwResultsSecondary secondaryResultEntity =
        new RvwResultsSecondaryLoader(getServiceData()).getEntityObject(secondaryResultId);
    Set<TRvwParametersSecondary> tRvwSecondaryParams = secondaryResultEntity.getTRvwParametersSecondaries();
    Map<Long, RvwParametersSecondary> secondaryParamMap = new ConcurrentHashMap<>();
    if (tRvwSecondaryParams != null) {
      for (TRvwParametersSecondary entity : tRvwSecondaryParams) {
        secondaryParamMap.put(entity.getSecRvwParamId(), createDataObject(entity));
      }
    }
    return secondaryParamMap;

  }
}
