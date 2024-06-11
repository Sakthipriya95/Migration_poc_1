/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author nip4cob
 */
public class QuestionUpdationCommand extends AbstractSimpleCommand {

  private final QuestionUpdationData inputData;

  /**
   * @param serviceData
   * @param inputData
   * @throws IcdmException
   */
  public QuestionUpdationCommand(final ServiceData serviceData, final QuestionUpdationData inputData)
      throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() throws IcdmException {
    if (this.inputData.isOnlyDepChange()) {
      setDepQuestionAndResp(new QuestionLoader(getServiceData()).getEntityObject(this.inputData.getQuestion().getId()));
    }
    else {
      Question newQues = updateQuestion();
      if (null != newQues) {
        createQuestionConfig(newQues);
        createAttrAndValDep(newQues);
        if (!this.inputData.getqDepValToEdit().isEmpty()) {
          editAttrDepnVals();
        }
        if (CommonUtils.isNotEmpty(this.inputData.getqDepAttrToDelete()) ||
            !this.inputData.getqDepValToDelete().isEmpty()) {
          removeAttrAndValDep();
        }

      }
    }
    // To Create Question Result Options for the questions
    if (CommonUtils.isNotEmpty(this.inputData.getQnaireResultOptionsToBeAdd())) {
      addQuestionResultOptions(this.inputData.getQuestion());
    }
    // To Edit Question Result Options for the questions
    if (CommonUtils.isNotEmpty(this.inputData.getQnaireResultOptionsToBeEdit())) {
      editQuestionResultOptions();
    }
    // To delete Question Result Options for the questions
    if (CommonUtils.isNotEmpty(this.inputData.getQnaireResultOptionsToBeDelete())) {
      deleteQuestionResultOptions();
    }
  }

  /**
   * Method to create the QuestionResultOptions for the given question
   **/
  private void addQuestionResultOptions(final Question newQues) throws IcdmException {
    for (QuestionResultOptionsModel qnaireResultsModel : this.inputData.getQnaireResultOptionsToBeAdd()) {
      QuestionResultOption questionResult = new QuestionResultOption();
      questionResult.setQResultName(qnaireResultsModel.getResult());
      questionResult.setQResultType(qnaireResultsModel.getAssesment());
      questionResult.setQId(newQues.getId());
    //setting allow to finsh WP
      questionResult.setqResultAlwFinishWP(qnaireResultsModel.isAllowFinishWP());
      QuestionResultOptionCommand questionResultCommand =
          new QuestionResultOptionCommand(getServiceData(), questionResult, false, false);
      executeChildCommand(questionResultCommand);
    }
  }

  /**
   * Method to delete the QuestionResultOptions for the given question
   **/
  private void deleteQuestionResultOptions() throws IcdmException {
    for (QuestionResultOptionsModel qnaireResultsModel : this.inputData.getQnaireResultOptionsToBeDelete()) {
      QuestionResultOption questionResult = new QuestionResultOptionLoader(getServiceData())
          .getDataObjectByID(qnaireResultsModel.getQuestionResultOptId());
      QuestionResultOptionCommand questDeleteResultCommand =
          new QuestionResultOptionCommand(getServiceData(), questionResult, false, true);
      executeChildCommand(questDeleteResultCommand);
    }
  }

  /**
   * Method to update the QuestionResultOptions for the given question
   **/
  private void editQuestionResultOptions() throws IcdmException {
    for (QuestionResultOptionsModel qnaireResultsModel : this.inputData.getQnaireResultOptionsToBeEdit()) {
      QuestionResultOption questionResult = new QuestionResultOptionLoader(getServiceData())
          .getDataObjectByID(qnaireResultsModel.getQuestionResultOptId());
      questionResult.setQResultName(qnaireResultsModel.getResult());
      questionResult.setQResultType(qnaireResultsModel.getAssesment());
    //setting allow to finsh WP
      questionResult.setqResultAlwFinishWP(qnaireResultsModel.isAllowFinishWP());
      QuestionResultOptionCommand questEditResultCommand =
          new QuestionResultOptionCommand(getServiceData(), questionResult, true, false);
      executeChildCommand(questEditResultCommand);
    }
  }


  /**
   * @throws IcdmException
   */
  private void removeAttrAndValDep() throws IcdmException {
    QuestionDepenAttrLoader qDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());
    if (null != this.inputData.getqDepAttrToDelete()) {
      for (Long qDepAttrId : this.inputData.getqDepAttrToDelete()) {
        QuestDepAttrCommand cmd =
            new QuestDepAttrCommand(getServiceData(), qDepAttrLoader.getDataObjectByID(qDepAttrId), true);
        executeChildCommand(cmd);
      }
    }
    for (QuestionDepenAttrValue qDepValToDelete : this.inputData.getqDepValToDelete().values()) {
      QuestionDepenAttrValueCommand cmd = new QuestionDepenAttrValueCommand(getServiceData(), qDepValToDelete, false);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param newAttrValDepMap
   * @throws IcdmException
   */
  private void editAttrDepnVals() throws IcdmException {

    if (this.inputData.getqDepValToAdd().isEmpty() && !this.inputData.getqDepValToEdit().isEmpty()) {
      editQDepAttrVal();
    }
  }

  /**
   * @throws IcdmException
   */
  private void editQDepAttrVal() throws IcdmException {
    for (QuestionDepenAttrValue valueToUpdate : this.inputData.getqDepValToEdit().values()) {
      QuestionDepenAttrValueCommand cmd = new QuestionDepenAttrValueCommand(getServiceData(), valueToUpdate, true);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param newQues
   * @return
   * @throws IcdmException
   */

  private void createAttrAndValDep(final Question newQues) throws IcdmException {
    Map<String, Long> qDepAttrMap = new HashMap<>();
    if (CommonUtils.isNotEmpty(this.inputData.getqDepAttrToAdd())) {
      for (QuestionDepenAttr qDepAttr : this.inputData.getqDepAttrToAdd()) {
        qDepAttr.setQId(newQues.getId());
        QuestDepAttrCommand cmd = new QuestDepAttrCommand(getServiceData(), qDepAttr);
        executeChildCommand(cmd);
        QuestionDepenAttr newQdepAttr = cmd.getNewData();
        qDepAttrMap.put(String.valueOf(newQues.getId()) + String.valueOf(newQdepAttr.getAttrId()), newQdepAttr.getId());
      }
    }
    for (TQuestionDepenAttribute qDepAttr : new QuestionLoader(getServiceData()).getEntityObject(newQues.getId())
        .getTQuestionDepenAttributes()) {
      qDepAttrMap.put(String.valueOf(newQues.getId()) + String.valueOf(qDepAttr.getTabvAttribute().getAttrId()),
          qDepAttr.getQattrDepenId());
    }

    if (CommonUtils.isNotEmpty(this.inputData.getqDepValToAdd())) {
      for (Entry<String, QuestionDepenAttrValue> entry : this.inputData.getqDepValToAdd().entrySet()) {
        QuestionDepenAttrValue valueToCreate = entry.getValue();
        for (String key : qDepAttrMap.keySet()) {
          if ((key + String.valueOf(valueToCreate.getQCombiNum())).equals(entry.getKey())) {
            valueToCreate.setQAttrDepId(qDepAttrMap.get(key));
            QuestionDepenAttrValueCommand cmd = new QuestionDepenAttrValueCommand(getServiceData(), valueToCreate);
            executeChildCommand(cmd);
          }
        }
      }
    }
  }


  /**
   * @param newQues
   * @throws IcdmException
   */
  private void createQuestionConfig(final Question newQues) throws IcdmException {
    QuestionConfig newQuestionConfig;
    QuestionConfigCommand qConfigCmd;
    if ((null != this.inputData.getQuestionConfig()) && (null != this.inputData.getQuestionConfig().getId())) {
      newQuestionConfig = this.inputData.getQuestionConfig();
      qConfigCmd = new QuestionConfigCommand(getServiceData(), newQuestionConfig, true);
    }
    else {
      newQuestionConfig = this.inputData.getQuestionConfig();
      qConfigCmd = new QuestionConfigCommand(getServiceData(), newQuestionConfig);
      newQuestionConfig.setQId(newQues.getId());
    }
    executeChildCommand(qConfigCmd);
  }


  /**
   * Set the dependent Question and Response
   *
   * @param dbQues dbQuestion
   * @throws IcdmException
   */
  private void setDepQuestionAndResp(final TQuestion dbQues) throws IcdmException {
    QuestionLoader questionLoader = new QuestionLoader(getServiceData());
    Question quesToUpdate = questionLoader.getDataObjectByID(dbQues.getQId());
    if (null != this.inputData.getQuestion().getDepQuesId()) {
      quesToUpdate.setDepQuesId(this.inputData.getQuestion().getDepQuesId());

      if (this.inputData.getQuestion().getDepQResultOptId() != null) {
        quesToUpdate.setDepQResultOptId(this.inputData.getQuestion().getDepQResultOptId());
        quesToUpdate.setDepQuesResp(null);
      }
      else if (this.inputData.getQuestion().getDepQuesResp() != null) {
        quesToUpdate.setDepQuesResp(this.inputData.getQuestion().getDepQuesResp());
        quesToUpdate.setDepQResultOptId(null);
      }
    }
    else {
      quesToUpdate.setDepQuesId(null);
      quesToUpdate.setDepQuesResp(null);
      quesToUpdate.setDepQResultOptId(null);
    }
    QuestionCommand qCmd = new QuestionCommand(getServiceData(), quesToUpdate, true);
    executeChildCommand(qCmd);
  }

  /**
   * @throws IcdmException
   */
  private Question updateQuestion() throws IcdmException {
    Question question = this.inputData.getQuestion();
    // Level + Details update
    if (this.inputData.isLevelChange() || this.inputData.isReorderWithinParent()) {
      return reorderQNumber(question);
    }
    QuestionCommand qCmd = new QuestionCommand(getServiceData(), question, true);
    // Details update
    executeChildCommand(qCmd);
    return qCmd.getNewData();
  }


  /**
   * @param question
   * @throws IcdmException
   */
  private Question reorderQNumber(final Question question) throws IcdmException {
    Question updatedQues = null;
    QuestionLoader loader = new QuestionLoader(getServiceData());
    TQuestion tQuestion = loader.getEntityObject(question.getId());
    Set<TQuestion> childQuestions = new HashSet<>();
    if (this.inputData.getNewParent() == null) {
      Set<Question> firstLevelQuestions = loader.getFirstLevelQuestions(this.inputData.getQuestion().getQnaireVersId());
      for (Question frstLvlQuestion : firstLevelQuestions) {
        childQuestions.add(loader.getEntityObject(frstLvlQuestion.getId()));
      }
    }
    else {
      // Get the child questions of selected question's parent
      childQuestions = loader.getEntityObject(this.inputData.getNewParent().getId()).getTQuestions();
    }
    if (this.inputData.isLevelChange()) {
      updatedQues = levelChangeReOrder(tQuestion, childQuestions);
    }
    if (this.inputData.isReorderWithinParent()) {
      updatedQues = reorderWithinSameParent(tQuestion, childQuestions);
    }
    return updatedQues;
  }

  /**
   * @param tQuestion
   * @param oldQNo
   * @param childQuestions
   * @throws IcdmException
   */
  private Question reorderWithinSameParent(final TQuestion dbQuestion, final Set<TQuestion> childQuestions)
      throws IcdmException {
    Long oldQNo = dbQuestion.getQNumber();
    QuestionLoader loader = new QuestionLoader(getServiceData());
    // set 0 as quetion number to avoid unique constraint exception
    dbQuestion.setQNumber(0L);
    getServiceData().getEntMgr().flush();
    if (oldQNo > this.inputData.getNewQuesNo()) {
      // if the old question number is greater than new question number
      // make a sorted set with reverse order
      SortedSet<TQuestion> reverseChildQuestions = new TreeSet<>(getTQuestionComparator(true));
      reverseChildQuestions.addAll(childQuestions);
      for (TQuestion ques : reverseChildQuestions) {
        long qNum = ques.getQNumber();
        if ((qNum >= this.inputData.getNewQuesNo()) && (qNum < oldQNo)) {
          // from new question number to old question number , increment the question numbers by 1
          Question question = loader.createDataObject(ques);
          question.setQNumber(qNum + 1);
          QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), question, true);
          executeChildCommand(quesCmdForChild);
          getServiceData().getEntMgr().flush();
        }
      }
    }
    else {
      SortedSet<TQuestion> childQuestionsInAscOrder = new TreeSet<>(getTQuestionComparator(false));
      childQuestionsInAscOrder.addAll(childQuestions);
      for (TQuestion child : childQuestionsInAscOrder) {
        long qNum = child.getQNumber();
        if ((qNum <= this.inputData.getNewQuesNo()) && (qNum > oldQNo)) {
          // from new question number to old question number , decrement the question numbers by 1
          Question question = loader.createDataObject(child);
          question.setQNumber(qNum - 1);
          QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), question, true);
          executeChildCommand(quesCmdForChild);
          getServiceData().getEntMgr().flush();
        }
      }
    }
    dbQuestion.setQNumber(this.inputData.getNewQuesNo());
    QuestionCommand quesCmdForCurrQuesAfterEdit = new QuestionCommand(getServiceData(),
        setEditedFields(loader.createDataObject(dbQuestion), this.inputData.getQuestion()), true);
    executeChildCommand(quesCmdForCurrQuesAfterEdit);
    getServiceData().getEntMgr().flush();
    return quesCmdForCurrQuesAfterEdit.getNewData();
  }

  /**
   * @param updateQues
   * @param inpQues
   */
  private Question setEditedFields(final Question updateQues, final Question inpQues) {

    updateQues.setPositiveResult(inpQues.getPositiveResult());
    updateQues.setResultRelevantFlag(inpQues.getResultRelevantFlag());
    updateQues.setQHintEng(inpQues.getQHintEng());
    updateQues.setQHintGer(inpQues.getQHintGer());
    updateQues.setQNameEng(inpQues.getQNameEng());
    updateQues.setQNameGer(inpQues.getQNameGer());
    updateQues.setHeadingFlag(inpQues.getHeadingFlag());
    if (null != inpQues.getDepQuesId()) {
      updateQues.setDepQuesId(inpQues.getDepQuesId());
      updateQues.setDepQuesResp(inpQues.getDepQuesResp());
      updateQues.setDepQResultOptId(inpQues.getDepQResultOptId());
    }
    else {
      updateQues.setDepQuesId(null);
      updateQues.setDepQuesResp(null);
      updateQues.setDepQResultOptId(null);
    }
    return updateQues;
  }

  private Question levelChangeReOrder(final TQuestion dbQuestion, final Set<TQuestion> childQuestions)
      throws IcdmException {
    Question updatedQues = null;
    QuestionLoader loader = new QuestionLoader(getServiceData());
    // Get question number and level of the question before question level was modified
    int oldQuesLevel = this.inputData.getOldQuesLevel();
    Long oldQNo = dbQuestion.getQNumber();
    TQuestion newParent = null;
    if (CommonUtils.isNotNull(this.inputData.getNewParent())) {
      newParent = loader.getEntityObject(this.inputData.getNewParent().getId());
    }
    TQuestion oldParent = loader.getEntityObject(this.inputData.getQuestion().getParentQId());

    // set 0 as quetion number to avoid unique constraint exception
    dbQuestion.setQNumber(0L);
    dbQuestion.setTQuestion(null);
    getServiceData().getEntMgr().flush();
    if (oldQuesLevel == ApicConstants.ONE_CONST) {
      // moving the qs up , then the below qs numbers have to be decremented
      // first level questions are questions at level 1 i.e. all heading questions
      SortedSet<Question> firstLevelQuestions = new TreeSet<>(getQuestionComparator());
      firstLevelQuestions.addAll(loader.getFirstLevelQuestions(this.inputData.getQuestion().getQnaireVersId()));
      for (Question frstLvlQuestion : firstLevelQuestions) {
        long qNum = frstLvlQuestion.getQNumber();
        if (qNum > oldQNo) {
          frstLvlQuestion.setQNumber(qNum - ApicConstants.ONE_CONST);
          QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), frstLvlQuestion, true);
          executeChildCommand(quesCmdForChild);
          getServiceData().getEntMgr().flush();
        }
      }
      SortedSet<TQuestion> reverseChildQuestions = new TreeSet<>(getTQuestionComparator(true));
      reverseChildQuestions.addAll(childQuestions);
      for (TQuestion ques : reverseChildQuestions) {
        long qNum = ques.getQNumber();
        if (qNum >= this.inputData.getNewQuesNo()) {
          Question newParentChildQues = loader.createDataObject(ques);
          newParentChildQues.setQNumber(qNum + ApicConstants.ONE_CONST);
          QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), newParentChildQues, true);
          executeChildCommand(quesCmdForChild);
          getServiceData().getEntMgr().flush();
        }
      }
      dbQuestion.setTQuestion(newParent);
      dbQuestion.setQNumber(this.inputData.getNewQuesNo());
      QuestionCommand quesCmdForCurrQuesAfterEdit = new QuestionCommand(getServiceData(),
          setEditedFields(loader.createDataObject(dbQuestion), this.inputData.getQuestion()), true);
      executeChildCommand(quesCmdForCurrQuesAfterEdit);
      getServiceData().getEntMgr().flush();
      if (CommonUtils.isNotNull(newParent)) {
        newParent.getTQuestions().add(dbQuestion);
      }
      updatedQues = quesCmdForCurrQuesAfterEdit.getNewData();
    }
    else if ((oldQuesLevel == ApicConstants.LEVEL_TWO_CONST) || (oldQuesLevel == ApicConstants.LEVEL_THREE_CONST)) {
      // old parent's child questions
      SortedSet<TQuestion> oldParentChildQuestions = new TreeSet<>(getTQuestionComparator(false));
      oldParentChildQuestions.addAll(oldParent.getTQuestions());
      // selected qs parent's other second level childrens number has to be decremented
      for (TQuestion child : oldParentChildQuestions) {
        long qNum = child.getQNumber();
        if (qNum > oldQNo) {
          Question oldParentChildQues = loader.createDataObject(child);
          oldParentChildQues.setQNumber(qNum - ApicConstants.ONE_CONST);
          QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), oldParentChildQues, true);
          executeChildCommand(quesCmdForChild);
          getServiceData().getEntMgr().flush();
        }
      }
      // New Parent's child questions
      SortedSet<TQuestion> reverseChildQuestions = new TreeSet<>(getTQuestionComparator(true));
      reverseChildQuestions.addAll(childQuestions);
      for (TQuestion ques : reverseChildQuestions) {
        long qNum = ques.getQNumber();
        if (qNum >= this.inputData.getNewQuesNo()) {
          Question newParentChildQues = loader.createDataObject(ques);
          newParentChildQues.setQNumber(qNum + ApicConstants.ONE_CONST);
          QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), newParentChildQues, true);
          executeChildCommand(quesCmdForChild);
          getServiceData().getEntMgr().flush();
        }
      }
      // Update ques no. of current ques
      if (((oldQuesLevel == ApicConstants.LEVEL_THREE_CONST) &&
          (loader.getEntityObject(this.inputData.getNewParent().getId()).getTQuestion() == null)) ||
          ((oldQuesLevel == ApicConstants.LEVEL_TWO_CONST) &&
              (this.inputData.getNewLevel() == ApicConstants.LEVEL_THREE_CONST))) {
        dbQuestion.setTQuestion(newParent);
        dbQuestion.setQNumber(this.inputData.getNewQuesNo());
        QuestionCommand quesCmdForCurrQuesAfterEdit = new QuestionCommand(getServiceData(),
            setEditedFields(loader.createDataObject(dbQuestion), this.inputData.getQuestion()), true);

        executeChildCommand(quesCmdForCurrQuesAfterEdit);
        getServiceData().getEntMgr().flush();
        loader.getEntityObject(oldParent.getQId()).getTQuestions().remove(dbQuestion);
        if (CommonUtils.isNotNull(newParent)) {
          newParent.getTQuestions().add(dbQuestion);
        }
        updatedQues = quesCmdForCurrQuesAfterEdit.getNewData();
      }
      else {
        // Update ques no. of current ques ,being moved to level 1
        dbQuestion.setQNumber(this.inputData.getNewQuesNo());
        QuestionCommand quesCmdForCurrQuesAfterEdit = new QuestionCommand(getServiceData(),
            setEditedFields(loader.createDataObject(dbQuestion), this.inputData.getQuestion()), true);
        executeChildCommand(quesCmdForCurrQuesAfterEdit);
        getServiceData().getEntMgr().flush();
        oldParent.getTQuestions().remove(dbQuestion);
        updatedQues = quesCmdForCurrQuesAfterEdit.getNewData();
      }
    }
    return updatedQues;
  }

  /**
   * Get param comparator.
   *
   * @return Comparator
   */
  public Comparator<Question> getQuestionComparator() {
    return (final Question o1, final Question o2) -> o1.getQNumber().compareTo(o2.getQNumber());
  }

  /**
   * @param isReverseOrder boolean
   * @return Comparator
   */
  public Comparator<TQuestion> getTQuestionComparator(final boolean isReverseOrder) {
    if (isReverseOrder) {
      return (final TQuestion o2, final TQuestion o1) -> {
        int compare;
        compare = ModelUtil.compare(o2.getQNumber(), o1.getQNumber());
        return -compare;
      };
    }
    return (final TQuestion o1, final TQuestion o2) -> ModelUtil.compare(o1.getQNumber(), o2.getQNumber());
  }

  /**
   * Mo {@inheritDoc}
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
