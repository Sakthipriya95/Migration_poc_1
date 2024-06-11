/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;

/**
 * @author nip4cob
 */
public class QuestionCommand extends AbstractCommand<Question, QuestionLoader> {


  /**
   * @param serviceData serviceData
   * @param inputData Question
   * @param isUpdate udpate flag
   * @throws IcdmException error during updating or deleting a question
   */
  public QuestionCommand(final ServiceData serviceData, final Question inputData, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, inputData, new QuestionLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * @param serviceData serviceData
   * @param inputData Question
   * @throws IcdmException Error duting creating a question
   */
  public QuestionCommand(final ServiceData serviceData, final Question inputData) throws IcdmException {
    super(serviceData, inputData, new QuestionLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TQuestion dbQuestion = new TQuestion();
    dbQuestion.setHeadingFlag(booleanToYorN(getInputData().getHeadingFlag()));
    dbQuestion.setPositiveResult(getInputData().getPositiveResult());
    setNameDesc(dbQuestion);

    TQuestionnaireVersion dbQVers =
        new QuestionnaireVersionLoader(getServiceData()).getEntityObject(getInputData().getQnaireVersId());
    dbQuestion.setTQuestionnaireVersion(dbQVers);
    dbQuestion.setQNumber(getInputData().getQNumber());
    setUserDetails(COMMAND_MODE.CREATE, dbQuestion);

    // add questions to the questionnaireversion
    Set<TQuestion> tQuestions = dbQVers.getTQuestions();
    if (tQuestions == null) {
      tQuestions = new HashSet<>();
      dbQVers.setTQuestions(tQuestions);
    }
    tQuestions.add(dbQuestion);
    dbQuestion.setDeletedFlag(booleanToYorN(getInputData().getDeletedFlag()));
    dbQuestion.setResultRelevantFlag(booleanToYorN(getInputData().getResultRelevantFlag()));
    if (getInputData().getParentQId() != null) {
      TQuestion parent = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getParentQId());
      dbQuestion.setTQuestion(parent);
      // add this question as a child to its parent
      Set<TQuestion> childQuestions = parent.getTQuestions();
      if (childQuestions == null) {
        childQuestions = new HashSet<>();
        parent.setTQuestions(childQuestions);
      }
      parent.getTQuestions().add(dbQuestion);
    }
    setDepQuestionAndResp(dbQuestion);
    persistEntity(dbQuestion);
  }


  /**
   * @param dbQuestion
   */
  private void setDepQuestionAndResp(final TQuestion dbQuestion) {
    if (null != getInputData().getDepQuesId()) {
      dbQuestion.setDepQuestion(new QuestionLoader(getServiceData()).getEntityObject(getInputData().getDepQuesId()));
      if (getInputData().getDepQuesResp() != null) {
        dbQuestion.setDepQuesResponse(getInputData().getDepQuesResp());
        dbQuestion.setDepQResultOption(null);
      }
      else if (getInputData().getDepQResultOptId() != null) {
        dbQuestion.setDepQResultOption(
            new QuestionResultOptionLoader(getServiceData()).getEntityObject(getInputData().getDepQResultOptId()));
        dbQuestion.setDepQuesResponse(null);
      }
    }
    else {
      dbQuestion.setDepQuestion(null);
      dbQuestion.setDepQuesResponse(null);
      dbQuestion.setDepQResultOption(null);
    }
  }


  private void setNameDesc(final TQuestion dbQuestion) {
    dbQuestion.setQHintEng(getInputData().getQHintEng());
    dbQuestion.setQHintGer(getInputData().getQHintGer());
    dbQuestion.setQNameEng(getInputData().getQNameEng());
    dbQuestion.setQNameGer(getInputData().getQNameGer());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TQuestion dbQuestion = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getId());
    setNameDesc(dbQuestion);
    dbQuestion.setHeadingFlag(booleanToYorN(getInputData().getHeadingFlag()));
    if (isObjectChanged(getInputData().getQNumber(), getOldData().getQNumber())) {
      dbQuestion.setQNumber(getInputData().getQNumber());
    }
    if (isObjectChanged(getInputData().getPositiveResult(), getOldData().getPositiveResult())) {
      dbQuestion.setPositiveResult(getInputData().getPositiveResult());
    }
    if (getInputData().getQuestionConfigId() != null) {
      dbQuestion.setTQuestionConfig(
          new QuestionConfigLoader(getServiceData()).getEntityObject(getInputData().getQuestionConfigId()));
    }
    dbQuestion.setDeletedFlag(booleanToYorN(getInputData().getDeletedFlag()));
    dbQuestion.setResultRelevantFlag(booleanToYorN(getInputData().getResultRelevantFlag()));
    // Save Dependency question
    setDepQuestionAndResp(dbQuestion);
    setUserDetails(COMMAND_MODE.UPDATE, dbQuestion);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return dataChangedInput();
  }

  /**
   * @return
   */
  private boolean dataChangedInput() {
    return dataChangedNameHint() || dataChangedConfig() || dataChangedDelAndRelevantFlag();
  }

  /**
   * @return
   */
  private boolean dataChangedNameHint() {
    return dataChangedHint() || dataChangedName();
  }

  /**
   * @return
   */
  private boolean dataChangedDelAndRelevantFlag() {
    return dataChangedDeletedFlag() || dataChangedResultRelevantFlag();
  }

  /**
   * @return
   */
  private boolean dataChangedHint() {
    return isObjectChanged(getInputData().getQHintEng(), getOldData().getQHintEng()) ||
        isObjectChanged(getInputData().getQHintGer(), getOldData().getQHintGer());
  }

  /**
   * @return
   */
  private boolean dataChangedName() {
    return isObjectChanged(getInputData().getQNumber(), getOldData().getQNumber()) ||
        isObjectChanged(getInputData().getQNameEng(), getOldData().getQNameEng()) ||
        isObjectChanged(getInputData().getQNameGer(), getOldData().getQNameGer());
  }

  /**
   * @return
   */
  private boolean dataChangedConfig() {
    return isObjectChanged(getInputData().getDepQuesId(), getOldData().getDepQuesId()) ||
        isObjectChanged(getInputData().getDepQuesResp(), getOldData().getDepQuesResp()) ||
        isObjectChanged(getInputData().getDepQResultOptId(), getOldData().getDepQResultOptId()) ||
        isObjectChanged(getInputData().getQuestionConfigId(), getOldData().getQuestionConfigId());
  }

  private boolean dataChangedDeletedFlag() {
    return isObjectChanged(getInputData().getDeletedFlag(), getOldData().getDeletedFlag());
  }

  private boolean dataChangedResultRelevantFlag() {
    return isObjectChanged(getInputData().getResultRelevantFlag(), getOldData().getResultRelevantFlag());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
