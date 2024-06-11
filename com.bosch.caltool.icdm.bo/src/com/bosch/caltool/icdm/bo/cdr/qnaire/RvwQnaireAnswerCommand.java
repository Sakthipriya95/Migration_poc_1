/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;

/**
 * @author apj4cob
 */
public class RvwQnaireAnswerCommand extends AbstractCommand<RvwQnaireAnswer, RvwQnaireAnswerLoader> {

  /**
   * Instantiates a new rvw qnaire answer command.
   *
   * @param serviceData ServiceData
   * @param inputData RvwQnaireAnswer
   * @param cmdMode the cmd mode
   * @throws IcdmException error when initializing
   */
  public RvwQnaireAnswerCommand(final ServiceData serviceData, final RvwQnaireAnswer inputData,
      final COMMAND_MODE cmdMode) throws IcdmException {
    super(serviceData, inputData, new RvwQnaireAnswerLoader(serviceData), cmdMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwQnaireAnswer dbRvwQuesAns = new TRvwQnaireAnswer();
    dbRvwQuesAns.setTQuestion(new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQuestionId()));
    new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQuestionId()).getTRvwQnaireAnswers()
        .add(dbRvwQuesAns);

    TRvwQnaireRespVersion dbRepVersion =
        new RvwQnaireRespVersionLoader(getServiceData()).getEntityObject(getInputData().getQnaireRespVersId());
    if (null != dbRepVersion) {
      dbRepVersion.addTRvwQnaireAnswer(dbRvwQuesAns);
    }

    // child commands for link RvwQnaireAnswerOpl
    dbRvwQuesAns.setMeasurement(getInputData().getMeasurement());
    dbRvwQuesAns.setRemark(getInputData().getRemark());
    dbRvwQuesAns.setResult(getInputData().getResult());
    dbRvwQuesAns.setSeries(getInputData().getSeries());
    dbRvwQuesAns.setTQuestionResultOption(
        new QuestionResultOptionLoader(getServiceData()).getEntityObject(getInputData().getSelQnaireResultOptID()));
    setUserDetails(COMMAND_MODE.CREATE, dbRvwQuesAns);
    persistEntity(dbRvwQuesAns);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RvwQnaireAnswerLoader ldr = new RvwQnaireAnswerLoader(getServiceData());
    TRvwQnaireAnswer entity = ldr.getEntityObject(getInputData().getId());
    if (isObjectChanged(getInputData().getMeasurement(), getOldData().getMeasurement())) {
      entity.setMeasurement(getInputData().getMeasurement());
    }
    if (isObjectChanged(getInputData().getRemark(), getOldData().getRemark())) {
      entity.setRemark(getInputData().getRemark());
    }
    if (isObjectChanged(getInputData().getResult(), getOldData().getResult())) {
      entity.setResult(getInputData().getResult());
    }
    if (isObjectChanged(getInputData().getSeries(), getOldData().getSeries())) {
      entity.setSeries(getInputData().getSeries());
    }
    if (isObjectChanged(getInputData().getSelQnaireResultOptID(), getOldData().getSelQnaireResultOptID())) {
      entity.setTQuestionResultOption(
          new QuestionResultOptionLoader(getServiceData()).getEntityObject(getInputData().getSelQnaireResultOptID()));
    }
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwQnaireAnswerLoader ldr = new RvwQnaireAnswerLoader(getServiceData());
    TRvwQnaireAnswer entity = ldr.getEntityObject(getInputData().getId());
    entity.getTQuestion().getTRvwQnaireAnswers().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return dataChangedRemarkResultMeas() || isObjectChanged(getInputData().getSeries(), getOldData().getSeries()) ||
        isObjectChanged(getInputData().getSelQnaireResultOptID(), getOldData().getSelQnaireResultOptID());
  }

  /**
   * @return
   */
  private boolean dataChangedRemarkResultMeas() {
    return isObjectChanged(getInputData().getMeasurement(), getOldData().getMeasurement()) ||
        isObjectChanged(getInputData().getRemark(), getOldData().getRemark()) ||
        isObjectChanged(getInputData().getResult(), getOldData().getResult());
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
