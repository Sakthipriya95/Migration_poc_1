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
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;


/**
 * Loader class for Secondary Review Results
 *
 * @author bru2cob
 */
public class RvwResultsSecondaryLoader extends AbstractBusinessObject<RvwResultsSecondary, TRvwResultsSecondary> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwResultsSecondaryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RESULT_SECONDARY, TRvwResultsSecondary.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwResultsSecondary createDataObject(final TRvwResultsSecondary entity) throws DataException {
    RvwResultsSecondary object = new RvwResultsSecondary();

    setCommonFields(object, entity);

    object.setResultId(entity.getTRvwResult().getResultId());
    object.setRsetId(entity.getTRuleSet() == null ? null : entity.getTRuleSet().getRsetId());
    object.setSsdReleaseId(entity.getSsdReleaseID());
    object.setSource(entity.getSource());
    object.setSsdVersionId(entity.getSsdVersID());
    if (null != entity.getTRuleSet()) {
      object.setRuleSetName(
          new RuleSetLoader(getServiceData()).getDataObjectByID(entity.getTRuleSet().getRsetId()).getName());
    }

    return object;
  }

  /**
   * Get Secondary Review Results records using ResultId
   *
   * @param entityObject review result
   * @return Map. Key - id, Value - RvwResultsSecondary object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwResultsSecondary> getByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, RvwResultsSecondary> objMap = new ConcurrentHashMap<>();
    Set<TRvwResultsSecondary> dbObj = entityObject.getTRvwResultsSecondaries();
    for (TRvwResultsSecondary entity : dbObj) {
      objMap.put(entity.getSecReviewId(), createDataObject(entity));
    }
    return objMap;
  }

}
