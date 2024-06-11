/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;

/**
 * @author nip4cob
 */
public class QuestionConfigCommand extends AbstractCommand<QuestionConfig, QuestionConfigLoader> {

  /**
   * @param serviceData
   * @param inputData
   * @param isUpdate
   * @throws IcdmException
   */
  public QuestionConfigCommand(final ServiceData serviceData, final QuestionConfig inputData, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, inputData, new QuestionConfigLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * @param serviceData
   * @param inputData
   * @throws IcdmException
   */
  public QuestionConfigCommand(final ServiceData serviceData, final QuestionConfig inputData) throws IcdmException {
    super(serviceData, inputData, new QuestionConfigLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    // Create entity for Question config
    TQuestionConfig dbQuestionConfig = new TQuestionConfig();
    TQuestion dbQues = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQId());
    // Associate the created question config to the question
    dbQuestionConfig.setTQuestion(dbQues);
    setNewConfig(dbQuestionConfig);
    setUserDetails(COMMAND_MODE.CREATE, dbQuestionConfig);
    dbQues.setTQuestionConfig(dbQuestionConfig);
    persistEntity(dbQuestionConfig);
  }

  /**
   * @param dbQuestionConfig
   */
  private void setNewConfig(final TQuestionConfig dbQuestionConfig) {
    if (null != getInputData().getResult()) {
      dbQuestionConfig.setResult(getInputData().getResult());
    }
    if (null != getInputData().getLink()) {
      dbQuestionConfig.setLink(getInputData().getLink());
    }
    if (null != getInputData().getMeasurement()) {
      dbQuestionConfig.setMeasurement(getInputData().getMeasurement());
    }
    if (null != getInputData().getRemark()) {
      dbQuestionConfig.setRemark(getInputData().getRemark());
    }
    if (null != getInputData().getSeries()) {
      dbQuestionConfig.setSeries(getInputData().getSeries());
    }
    if (null != getInputData().getOpenPoints()) {
      dbQuestionConfig.setOpenPoints(getInputData().getOpenPoints());
    }
    if (null != getInputData().getMeasure()) {
      dbQuestionConfig.setMeasure(getInputData().getMeasure());
    }
    if (null != getInputData().getResponsible()) {
      dbQuestionConfig.setResponsible(getInputData().getResponsible());
    }
    if (null != getInputData().getCompletionDate()) {
      dbQuestionConfig.setCompletionDate(getInputData().getCompletionDate());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TQuestionConfig dbQconfig = new QuestionConfigLoader(getServiceData()).getEntityObject(getInputData().getId());
    setNewConfig(dbQconfig);
    setUserDetails(COMMAND_MODE.UPDATE, dbQconfig);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return checkDataChange();
  }

  /**
   * @return
   */
  private boolean checkDataChange() {
    return isValidResLinkMeasChange() || isValidRemSerieOpnPtChange() || isValidUserDetChange();
  }

  /**
   * @return
   */
  private boolean isValidUserDetChange() {
    return isObjectChanged(getInputData().getMeasure(), getOldData().getMeasure()) ||
        isObjectChanged(getInputData().getResponsible(), getOldData().getResponsible()) ||
        isObjectChanged(getInputData().getCompletionDate(), getOldData().getCompletionDate());
  }

  /**
   * @return
   */
  private boolean isValidRemSerieOpnPtChange() {
    return isObjectChanged(getInputData().getRemark(), getOldData().getRemark()) ||
        isObjectChanged(getInputData().getSeries(), getOldData().getSeries()) ||
        isObjectChanged(getInputData().getOpenPoints(), getOldData().getOpenPoints());
  }

  /**
   * @return
   */
  private boolean isValidResLinkMeasChange() {
    return isObjectChanged(getInputData().getResult(), getOldData().getResult()) ||
        isObjectChanged(getInputData().getLink(), getOldData().getLink()) ||
        isObjectChanged(getInputData().getMeasurement(), getOldData().getMeasurement());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
