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
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwA2l;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResult;


/**
 * @author dmo5cob
 */
public class CompliReviewResultLoader extends AbstractBusinessObject<CompliReviewResult, TCompliRvwA2l> {


  /**
   * @param serviceData Service Data
   */
  public CompliReviewResultLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.COMPLI_RVW_RES, TCompliRvwA2l.class);
  }


  /**
   * @return map of CompliReviewResult objects
   * @throws DataException error while creating data object Method not used . Need to check the usage.
   */
  public Map<Long, CompliReviewResult> getCompliResults() throws DataException {

    Map<Long, CompliReviewResult> colValMap = new ConcurrentHashMap<>();

    TypedQuery<TCompliRvwA2l> tQuery = getEntMgr().createNamedQuery(TCompliRvwA2l.GET_ALL, TCompliRvwA2l.class);

    List<TCompliRvwA2l> dbColumns = tQuery.getResultList();

    for (TCompliRvwA2l complResult : dbColumns) {
      colValMap.put(complResult.getResultId(), createDataObject(complResult));
    }
    return colValMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompliReviewResult createDataObject(final TCompliRvwA2l entity) throws DataException {

    CompliReviewResult data = new CompliReviewResult();
    data.setId(entity.getResultId());
    data.setVersion(entity.getVersion());
    data.setA2lFileId(entity.getA2lFileId());
    if (entity.getTabvIcdmFile() != null) {
      data.setIcdmFileId(entity.getTabvIcdmFile().getFileId());
    }
    data.setA2lFileName(entity.getA2lFileName());
    data.setSdomPverName(entity.getSdomPverName());
    data.setSdomPverRevision(entity.getSdomPverRevision());
    data.setNumCompli(entity.getNumCompli());
    data.setNumQssd(entity.getNumQssd());
    return data;
  }


}
