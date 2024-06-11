/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;


/**
 * @author jvi6cob
 */
public class QuestionConfig extends AbstractCdrObject {

  /**
   * @param cdrDataProvider CDRDataProvider
   * @param qConfigID Long
   */
  protected QuestionConfig(final CDRDataProvider cdrDataProvider, final Long qConfigID) {
    super(cdrDataProvider, qConfigID);
    getDataCache().getQuestionConfigMap().put(qConfigID, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQuestionConfig(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQuestionConfig(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionConfig(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionConfig(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QUESTION_CONFIG;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getQuestion().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getQuestion().getDescription();
  }

  /**
   * Returns the link value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getLink() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getLink());
  }

  /**
   * Returns the Measurement value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getMeasurement() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getMeasurement());
  }

  /**
   * Returns the Open points' value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getOpenPoints() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getOpenPoints());
  }

  /**
   * Returns the Remark value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getRemark() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getRemark());
  }

  /**
   * Returns the Result value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getResult() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getResult());
  }

  /**
   * Returns the Series value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getSeries() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getSeries());
  }

  /**
   * @return Question
   */
  public Question getQuestion() {
    return getDataCache().getQuestion(getEntityProvider().getDbQuestionConfig(getID()).getTQuestion().getQId());
  }

  /**
   * Returns the Measure value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getMeasure() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getMeasure());
  }

  /**
   * Returns the Responsible value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getResponsible() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getResponsible());
  }

  /**
   * Returns the CompletionDate value for this QuestionConfig
   * 
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getCompletionDate() {
    return QUESTION_CONFIG_TYPE.getType(getEntityProvider().getDbQuestionConfig(getID()).getCompletionDate());
  }
}
