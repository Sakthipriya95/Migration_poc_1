/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwCommentTemplate;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;

/**
 * @author say8cob
 */
public class RvwCommentTemplateLoader extends AbstractBusinessObject<RvwCommentTemplate, TRvwCommentTemplate> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwCommentTemplateLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RVW_COMMENT_TEMPLATE, TRvwCommentTemplate.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwCommentTemplate createDataObject(final TRvwCommentTemplate tRvwCommentTemplate) throws DataException {
    RvwCommentTemplate rvwCommentTemplate = new RvwCommentTemplate();
    rvwCommentTemplate.setId(tRvwCommentTemplate.getCommentId());
    rvwCommentTemplate.setName(tRvwCommentTemplate.getCommentDesc());
    setCommonFields(rvwCommentTemplate, tRvwCommentTemplate);
    return rvwCommentTemplate;
  }

  /**
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public java.util.Map<Long,RvwCommentTemplate> getAll() throws DataException {
    java.util.Map<Long,RvwCommentTemplate> rvwCommentTemplateMap = new java.util.HashMap<>();

    TypedQuery<TRvwCommentTemplate> tQuery =
        getEntMgr().createNamedQuery(TRvwCommentTemplate.GET_ALL, TRvwCommentTemplate.class);

    List<TRvwCommentTemplate> dbRvwCmtTmplateSets = tQuery.getResultList();

    for (TRvwCommentTemplate tRvwCommentTemplate : dbRvwCmtTmplateSets) {
      rvwCommentTemplateMap.put(tRvwCommentTemplate.getCommentId(),createDataObject(tRvwCommentTemplate));
    }
    return rvwCommentTemplateMap;
  }

}
