/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.compli;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwHex;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResultHex;


/**
 * @author dmo5cob
 */
public class CompliReviewResultHexLoader extends AbstractBusinessObject<CompliReviewResultHex, TCompliRvwHex> {


  /**
   * @param serviceData Service Data
   */
  public CompliReviewResultHexLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.COMPLI_RVW_RES_HEX, TCompliRvwHex.class);
  }


  /**
   * @return map of CompliReviewResultHex objects
   * @throws DataException error while creating data object
   */
  public Map<Long, CompliReviewResultHex> getCompliResultsHex() throws DataException {

    Map<Long, CompliReviewResultHex> colValMap = new ConcurrentHashMap<>();

    TypedQuery<TCompliRvwHex> tQuery = getEntMgr().createNamedQuery(TCompliRvwHex.GET_ALL, TCompliRvwHex.class);

    List<TCompliRvwHex> dbColumns = tQuery.getResultList();

    for (TCompliRvwHex complResult : dbColumns) {
      colValMap.put(complResult.getCompliRvwHexId(), createDataObject(complResult));
    }
    return colValMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompliReviewResultHex createDataObject(final TCompliRvwHex entity) throws DataException {

    CompliReviewResultHex data = new CompliReviewResultHex();

    data.setId(entity.getCompliRvwHexId());
    data.setVersion(entity.getVersion());
    data.setCompliNoRule(entity.getCompliNoRule());
    data.setCreatedDate(entity.getCreatedDate());
    data.setCssdFail(entity.getCssdFail());
    data.setResultOk(entity.getResultOk());
    data.setHexFileName(entity.getHexFileName());
    data.setIndexNum(entity.getIndexNum());
    data.setSsd2rvFail(entity.getSsd2rvFail());
    data.setQssdFail(entity.getQssdFail());
    data.setQssdOk(entity.getQssdOk());
    data.setQssdNoRule(entity.getQssdNoRule());
    return data;
  }


}
