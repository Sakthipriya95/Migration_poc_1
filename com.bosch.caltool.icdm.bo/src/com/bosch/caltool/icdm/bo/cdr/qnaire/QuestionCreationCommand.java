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
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author nip4cob
 */
public class QuestionCreationCommand extends AbstractSimpleCommand {

  private final QuestionCreationData inputData;

  /**
   * @param serviceData
   * @param inputData
   * @throws IcdmException
   */
  public QuestionCreationCommand(final ServiceData serviceData, final QuestionCreationData inputData)
      throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    Question newQues = createQuestion();
    this.inputData.getQuestion().setId(newQues.getId());
    if (!this.inputData.getQuestion().getHeadingFlag()) {
      createQuestionConfig(newQues);
    }
    if (CommonUtils.isNotEmpty(this.inputData.getAttributes())) {
      createAttrAndValDep(newQues);
    }
    //To Create Result Options for the questions
    if (CommonUtils.isNotEmpty(this.inputData.getQnaireResultOptionModel())) {
      createQuestionResultOptions(newQues);
    }
  }

  /**
   *Method to create the QuestionResultOptions for the given question  
   **/
  private void createQuestionResultOptions(final Question newQues) throws IcdmException {

    for (QuestionResultOptionsModel qnaireResultsModel : this.inputData.getQnaireResultOptionModel()) {
      QuestionResultOption questionResult = new QuestionResultOption();
      questionResult.setQResultName(qnaireResultsModel.getResult());
      questionResult.setQResultType(qnaireResultsModel.getAssesment());
      questionResult.setQId(newQues.getId());
      //setting the result allowed to finish WP
      questionResult.setqResultAlwFinishWP(qnaireResultsModel.isAllowFinishWP());
      QuestionResultOptionCommand questionResultCommand =
          new QuestionResultOptionCommand(getServiceData(), questionResult, false, false);
      executeChildCommand(questionResultCommand);
    }
  }

  /**
   * @param newQues
   * @throws IcdmException
   */
  private void createAttrAndValDep(final Question newQues) throws IcdmException {
    Map<String, Long> qDepAttrMap = new HashMap<>();
    for (QuestionDepenAttr qDepAttr : this.inputData.getAttributes()) {
      qDepAttr.setQId(newQues.getId());
      QuestDepAttrCommand cmd = new QuestDepAttrCommand(getServiceData(), qDepAttr);
      executeChildCommand(cmd);
      QuestionDepenAttr newAttr = cmd.getNewData();
      if (CommonUtils.isNotNull(newQues.getParentQId())) {
        qDepAttrMap.put(String.valueOf(newQues.getParentQId()) + String.valueOf(newAttr.getAttrId()), newAttr.getId());
      }
      else {
        qDepAttrMap.put(String.valueOf(newAttr.getAttrId()), newAttr.getId());
      }
    }


    for (Entry<String, QuestionDepenAttrValue> entry : this.inputData.getqDepValCombMap().entrySet()) {
      QuestionDepenAttrValue valueToCreate = entry.getValue();
      for (String key : qDepAttrMap.keySet()) {
        if ((key + String.valueOf(valueToCreate.getQCombiNum())).equals(entry.getKey())) {
          valueToCreate.setQAttrDepId(qDepAttrMap.get(key));
        }
      }
      QuestionDepenAttrValueCommand cmd = new QuestionDepenAttrValueCommand(getServiceData(), valueToCreate);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param newQues
   * @throws IcdmException
   */
  private void createQuestionConfig(final Question newQues) throws IcdmException {
    QuestionConfig newQuestionConfig = this.inputData.getQuestionConfig();
    newQuestionConfig.setQId(newQues.getId());
    QuestionConfigCommand qConfigCmd = new QuestionConfigCommand(getServiceData(), newQuestionConfig);
    executeChildCommand(qConfigCmd);
    newQues.setQuestionConfigId(qConfigCmd.getNewData().getId());
    QuestionCommand qCmd = new QuestionCommand(getServiceData(), newQues, true);
    executeChildCommand(qCmd);
  }

  /**
   * @throws IcdmException
   */
  private Question createQuestion() throws IcdmException {
    Question newQuestion = this.inputData.getQuestion();
    QuestionCommand qCmd = new QuestionCommand(getServiceData(), newQuestion);
    if (this.inputData.isReorderWithinParent()) {
      reorderQNumber();
    }
    executeChildCommand(qCmd);
    return qCmd.getNewData();
  }


  /**
   * @param dbQuestion
   * @throws DataException
   */
  private void reorderQNumber() throws IcdmException {
    QuestionLoader loader = new QuestionLoader(getServiceData());
    Set<TQuestion> childQuestions = new HashSet<>();
    Long assignedQNo;

    if (this.inputData.getQuestion().getParentQId() == null) {
      Set<Question> firstLevelQuestions = loader.getFirstLevelQuestions(this.inputData.getQuestion().getQnaireVersId());
      for (Question frstLvlQuestion : firstLevelQuestions) {
        childQuestions.add(loader.getEntityObject(frstLvlQuestion.getId()));
      }
    }
    else {
      // Get selected question's child question
      childQuestions = loader.getEntityObject(this.inputData.getQuestion().getParentQId()).getTQuestions();
    }

    assignedQNo = (long) (childQuestions.size() + 1);

    reorderWithinSameParent(assignedQNo, childQuestions);
  }


  /**
   * @param tQuestion
   * @param assignedQNo
   * @param childQuestions
   * @throws IcdmException
   */
  private void reorderWithinSameParent(final Long assignedQNo, final Set<TQuestion> childQuestions)
      throws IcdmException {
    QuestionLoader loader = new QuestionLoader(getServiceData());
    SortedSet<TQuestion> reverseChildQuestions = new TreeSet<>(getTQuestionComparator(true));
    reverseChildQuestions.addAll(childQuestions);
    for (TQuestion ques : reverseChildQuestions) {
      long qNum = ques.getQNumber();
      if ((qNum >= this.inputData.getQuestion().getQNumber()) && (qNum < assignedQNo)) {
        // from selected question number to assigned question number , increment the question numbers by 1 to make
        // selected question no. available
        Question question = loader.createDataObject(ques);
        question.setQNumber(qNum + 1);
        QuestionCommand quesCmdForChild = new QuestionCommand(getServiceData(), question, true);
        executeChildCommand(quesCmdForChild);
        getServiceData().getEntMgr().flush();
      }
    }
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
