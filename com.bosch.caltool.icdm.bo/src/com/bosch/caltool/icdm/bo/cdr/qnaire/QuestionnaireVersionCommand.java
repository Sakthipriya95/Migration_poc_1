package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionResultOption;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

/**
 * @author nip4cob
 */
public class QuestionnaireVersionCommand extends AbstractCommand<QuestionnaireVersion, QuestionnaireVersionLoader> {

  /**
   * @param serviceData serviceData
   * @param inputData inputData
   * @throws IcdmException Error during creating a QuestionnaireVersion
   */
  public QuestionnaireVersionCommand(final ServiceData serviceData, final QuestionnaireVersion inputData)
      throws IcdmException {
    super(serviceData, inputData, new QuestionnaireVersionLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData serviceData
   * @param inputData inputData
   * @param isUpdate update or delete flag
   * @throws IcdmException Error during updating or deleting a QuestionnaireVersion
   */
  public QuestionnaireVersionCommand(final ServiceData serviceData, final QuestionnaireVersion inputData,
      final boolean isUpdate) throws IcdmException {
    super(serviceData, inputData, new QuestionnaireVersionLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    QuestionnaireVersion createdVer = createQuesVer();
    updateQuestionDep(createdVer);
  }

  /**
   * @throws IcdmException
   */
  private QuestionnaireVersion createQuesVer() throws IcdmException {
    TQuestionnaireVersion dbQuesVer = new TQuestionnaireVersion();
    QuestionnaireLoader questionnaireLoader = new QuestionnaireLoader(getServiceData());
    QuestionnaireVersionLoader questionnaireVersionLoader = new QuestionnaireVersionLoader(getServiceData());

    TQuestionnaire dbQuestionnaire = questionnaireLoader.getEntityObject(getInputData().getQnaireId());
    TQuestionnaireVersion dbParentVersion = null;
    if (questionnaireVersionLoader.getWorkingSet(getInputData().getQnaireId()) != null) {
      dbParentVersion = questionnaireVersionLoader
          .getEntityObject(questionnaireVersionLoader.getWorkingSet(getInputData().getQnaireId()).getId());
    }

    if (null != (dbParentVersion)) {
      initalizeFieldsUsingParent(dbQuesVer, dbParentVersion);
    }
    // check for no versions. If it is Working set (set the inwork flag to Y)
    else {
      initalizeFields(dbQuesVer);
      dbQuesVer.setInworkFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
      dbQuesVer.setGenQuesEquivalentFlag(getInputData().getGenQuesEquivalent());
      dbQuesVer.setNoNegativeAnswersAllowed(getInputData().getNoNegativeAnsAllowedFlag());
    }
    // initialize the fields

    // set the questionnaire for the version
    dbQuesVer.setTQuestionnaire(dbQuestionnaire);
    setUserDetails(COMMAND_MODE.CREATE, dbQuesVer);
    persistEntity(dbQuesVer);


    QuestionnaireVersion createdVersion = questionnaireVersionLoader.getDataObjectByID(dbQuesVer.getQnaireVersId());

    // add the newly created questionnaire version to the questionnaire
    if (dbQuestionnaire.getTQuestionnaireVersions() == null) {
      dbQuestionnaire.setTQuestionnaireVersions(new HashSet<TQuestionnaireVersion>());
    }
    dbQuestionnaire.getTQuestionnaireVersions().add(dbQuesVer);

    if ((null != getInputData().getActiveFlag()) && getInputData().getActiveFlag().equals(ApicConstants.CODE_YES)) {
      resetQuesVer(dbQuestionnaire.getTQuestionnaireVersions(), dbQuesVer);
      dbQuesVer.setActiveFlag(ApicConstants.CODE_YES);
    }

    if (CommonUtils.isNotNull(dbParentVersion)) {
      setQuestions(dbParentVersion, createdVersion);
    }
    return createdVersion;
  }

  private void resetQuesVer(final Set<TQuestionnaireVersion> quesVersion, final TQuestionnaireVersion dbQuesVer) {
    TQuestionnaireVersion selectedQuesVer = null;
    for (TQuestionnaireVersion tQuestionnaireVersion : quesVersion) {
      if (ApicConstants.CODE_YES.equals(tQuestionnaireVersion.getActiveFlag()) &&
          (tQuestionnaireVersion.getQnaireVersId() != dbQuesVer.getQnaireVersId())) {
        selectedQuesVer = tQuestionnaireVersion;
      }
    }
    if (null != selectedQuesVer) {
      selectedQuesVer.setActiveFlag(ApicConstants.CODE_NO);
    }
  }


  private void setQuestions(final TQuestionnaireVersion dbParentVersion, final QuestionnaireVersion newVersion)
      throws IcdmException {
    QuestionnaireVersionLoader qnaireVerLoader = new QuestionnaireVersionLoader(getServiceData());
    QnaireVersionModel qnaireVersionWithDetails =
        qnaireVerLoader.getQnaireVersionWithDetails(dbParentVersion.getQnaireVersId());
    SortedSet<Question> firstLevelQuestions =
        new QuestionLoader(getServiceData()).getFirstLevelQuestions(dbParentVersion.getQnaireVersId());
    copyQuestions(firstLevelQuestions, null, newVersion, qnaireVersionWithDetails);
  }


  private void copyQuestions(final SortedSet<Question> questionSet, final Question parent,
      final QuestionnaireVersion newVersion, final QnaireVersionModel qnaireVersionWithDetails)
      throws IcdmException {
    for (Question question : questionSet) {
      if (question.getParentQId() == null) {
        QuestionCreationData newData = new QuestionCreationData();
        setFieldsForQuestion(question, newData, newVersion, null);
        // copy the question result options
        setFieldsForQuestionResultOptions(question, newData);
        setFieldsForQDepAttr(question, newData);
        setFieldsForQDepAttrVal(question, newData, null);
        QuestionCreationCommand cmd = new QuestionCreationCommand(getServiceData(), newData);
        executeChildCommand(cmd);
        SortedSet<Question> childQuesSet = new TreeSet<>();
        for (Long childId : qnaireVersionWithDetails.getChildQuestionIdMap().get(question.getId())) {
          if (qnaireVersionWithDetails.getQuestionMap().containsKey(childId)) {
            childQuesSet.add(qnaireVersionWithDetails.getQuestionMap().get(childId));
          }
        }
        copyQuestions(childQuesSet, newData.getQuestion(), newVersion, qnaireVersionWithDetails);
      }
      else {
        setFieldsForQuestionFromParent(parent, newVersion, qnaireVersionWithDetails, question);
      }
    }


  }

  /**
   * @param parent
   * @param newVersion
   * @param qnaireVersionWithDetails
   * @param question
   * @throws DataException
   * @throws IcdmException
   */
  private void setFieldsForQuestionFromParent(final Question parent, final QuestionnaireVersion newVersion,
      final QnaireVersionModel qnaireVersionWithDetails, final Question question)
      throws IcdmException {
    QuestionCreationData newData = new QuestionCreationData();
    setFieldsForQuestion(question, newData, newVersion, parent);
    if (null != qnaireVersionWithDetails.getQuestionConfigMap().get(question.getId())) {
      setFieldsForQuestionConfig(qnaireVersionWithDetails.getQuestionConfigMap().get(question.getId()), newData);
    }
    // copy the question result options
    setFieldsForQuestionResultOptions(question, newData);
    setFieldsForQDepAttr(question, newData);
    setFieldsForQDepAttrVal(question, newData, parent);
    QuestionCreationCommand cmd = new QuestionCreationCommand(getServiceData(), newData);
    executeChildCommand(cmd);

    // To find the child question
    SortedSet<Question> childQuesSet = new TreeSet<>();
    for (Long childId : qnaireVersionWithDetails.getChildQuestionIdMap().get(question.getId())) {
      if (qnaireVersionWithDetails.getQuestionMap().containsKey(childId)) {
        childQuesSet.add(qnaireVersionWithDetails.getQuestionMap().get(childId));
      }
    }
    if (!childQuesSet.isEmpty()) {
      copyQuestions(childQuesSet, newData.getQuestion(), newVersion, qnaireVersionWithDetails);
    }
  }

  /**
   * @param question as input
   * @param newData as input
   * @throws DataException as expection
   */
  private void setFieldsForQuestionResultOptions(final Question question, final QuestionCreationData newData)
      throws DataException {
    for (TQuestionResultOption tQuestionResultOption : new QuestionResultOptionLoader(getServiceData())
        .getAllQuestionResultOptionsByQID(question.getId())) {
      QuestionResultOptionsModel quetionResultOptionsModel = new QuestionResultOptionsModel();
      quetionResultOptionsModel.setResult(tQuestionResultOption.getQResultName());
      quetionResultOptionsModel.setAssesment(tQuestionResultOption.getQResultType());
      // setting allowing finish WP
      quetionResultOptionsModel
          .setAllowFinishWP(CommonUtils.getBooleanType(tQuestionResultOption.getqResultAlwFinishWP()));
      newData.getQnaireResultOptionModel().add(quetionResultOptionsModel);
    }
  }

  /**
   * @param question
   * @param newData
   * @param parent
   * @throws DataException
   */
  private void setFieldsForQDepAttrVal(final Question question, final QuestionCreationData newData,
      final Question parent) {
    QuestionLoader questLoader = new QuestionLoader(getServiceData());
    Set<TQuestionDepenAttribute> tQuestionDepenAttributes =
        questLoader.getEntityObject(question.getId()).getTQuestionDepenAttributes();
    for (TQuestionDepenAttribute tQuestionDepenAttribute : tQuestionDepenAttributes) {
      Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues = tQuestionDepenAttribute.getTQuestionDepenAttrValues();
      for (TQuestionDepenAttrValue tQuestionDepenAttrValue : tQuestionDepenAttrValues) {
        QuestionDepenAttrValue valToCreate = new QuestionDepenAttrValue();
        valToCreate.setQAttrDepId(tQuestionDepenAttribute.getQattrDepenId());
        valToCreate.setQCombiNum(tQuestionDepenAttrValue.getQCombiNum());
        valToCreate.setValueId(tQuestionDepenAttrValue.getTabvAttrValue().getValueId());
        String key = "";

        if (CommonUtils.isNotNull(parent)) {
          key =
              String.valueOf(parent.getId()) + String.valueOf(tQuestionDepenAttribute.getTabvAttribute().getAttrId()) +
                  String.valueOf(tQuestionDepenAttrValue.getQCombiNum());
        }
        else {
          key = String.valueOf(tQuestionDepenAttribute.getTabvAttribute().getAttrId()) +
              String.valueOf(tQuestionDepenAttrValue.getQCombiNum());
        }
        newData.getqDepValCombMap().put(key, valToCreate);
      }
    }
  }

  /**
   * @param question
   * @param newData
   * @throws DataException
   */
  private void setFieldsForQDepAttr(final Question question, final QuestionCreationData newData) throws DataException {
    QuestionDepenAttrLoader qDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());
    List<QuestionDepenAttr> qDepattrToCreate = new ArrayList<>();
    for (QuestionDepenAttr qDepAttr : qDepAttrLoader.getDepenAttrMap(question.getId()).values()) {
      QuestionDepenAttr newAttr = new QuestionDepenAttr();
      newAttr.setAttrId(qDepAttr.getAttrId());
      qDepattrToCreate.add(newAttr);
    }
    newData.setAttributes(qDepattrToCreate);
  }

  /**
   * @param questionConfig
   * @param newData
   */
  private void setFieldsForQuestionConfig(final QuestionConfig questionConfig, final QuestionCreationData newData) {
    QuestionConfig newConfig = new QuestionConfig();
    newConfig.setResult(questionConfig.getResult());
    newConfig.setOpenPoints(questionConfig.getOpenPoints());
    newConfig.setMeasurement(questionConfig.getMeasurement());
    newConfig.setRemark(questionConfig.getRemark());
    newConfig.setSeries(questionConfig.getSeries());
    newConfig.setLink(questionConfig.getLink());
    newConfig.setMeasure(questionConfig.getMeasure());
    newConfig.setResponsible(questionConfig.getResponsible());
    newConfig.setCompletionDate(questionConfig.getCompletionDate());
    newData.setQuestionConfig(newConfig);
  }

  /**
   * @param ques
   * @param newData
   * @param newVersion
   * @param parent
   */
  private void setFieldsForQuestion(final Question ques, final QuestionCreationData newData,
      final QuestionnaireVersion newVersion, final Question parent) {
    Question quesToAdd = setQuestionFields(ques, newVersion, parent);
    newData.setQuestion(quesToAdd);
  }

  /**
   * @param ques
   * @param newVersion
   * @param parent
   * @return
   */
  private Question setQuestionFields(final Question ques, final QuestionnaireVersion newVersion,
      final Question parent) {
    Question quesToAdd = new Question();
    if (parent != null) {
      quesToAdd.setParentQId(parent.getId());
    }
    quesToAdd.setQnaireVersId(newVersion.getId());
    quesToAdd.setHeadingFlag(ques.getHeadingFlag());
    quesToAdd.setDepQuesId(ques.getDepQuesId());
    quesToAdd.setDepQuesResp(ques.getDepQuesResp());
    quesToAdd.setDepQResultOptId(ques.getDepQResultOptId());
    quesToAdd.setPositiveResult(ques.getPositiveResult());
    quesToAdd.setDeletedFlag(ques.getDeletedFlag());
    quesToAdd.setResultRelevantFlag(ques.getResultRelevantFlag());
    quesToAdd.setQNumber(ques.getQNumber());
    quesToAdd.setQHintEng(ques.getQHintEng());
    quesToAdd.setQHintGer(ques.getQHintGer());
    quesToAdd.setName(ques.getName());
    quesToAdd.setQNameEng(ques.getQNameEng());
    quesToAdd.setQNameGer(ques.getQNameGer());
    return quesToAdd;
  }

  /**
   * @param dbQuesVer
   */
  private void initalizeFields(final TQuestionnaireVersion dbQuesVer) {
    // Set the default values for all.
    dbQuesVer.setActiveFlag(getInputData().getActiveFlag());
    dbQuesVer.setDescEng(getInputData().getDescEng());
    dbQuesVer.setDescGer(getInputData().getDescGer());
    dbQuesVer.setInworkFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setLinkHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setLinkRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setMajorVersionNum(getInputData().getMajorVersionNum());
    dbQuesVer.setMeasurementHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setMeasurementRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setMinorVersionNum(getInputData().getMinorVersionNum());
    dbQuesVer.setOpenPointsHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setOpenPointsRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setRemarkRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setRemarksHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setResultHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setResultRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setSeriesHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setSeriesRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setMeasureHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setMeasureRelaventFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setResponsibleHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setResponsibleRelaventFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setCompletionDateHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setCompletionDateRelaventFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
  }

  /**
   * @param dbQuesVer
   * @param workingSet
   */
  private void initalizeFieldsUsingParent(final TQuestionnaireVersion dbQuesVer,
      final TQuestionnaireVersion dbParentVer) {
    dbQuesVer.setActiveFlag(getInputData().getActiveFlag());
    dbQuesVer.setDescEng(getInputData().getDescEng());
    dbQuesVer.setDescGer(getInputData().getDescGer());
    dbQuesVer.setInworkFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setLinkHiddenFlag(dbParentVer.getLinkHiddenFlag());
    dbQuesVer.setLinkRelevantFlag(dbParentVer.getLinkRelevantFlag());
    dbQuesVer.setMajorVersionNum(getInputData().getMajorVersionNum());
    dbQuesVer.setMeasurementHiddenFlag(dbParentVer.getMeasurementHiddenFlag());
    dbQuesVer.setMeasurementRelevantFlag(dbParentVer.getMeasurementRelevantFlag());
    dbQuesVer.setMinorVersionNum(getInputData().getMinorVersionNum());
    dbQuesVer.setOpenPointsHiddenFlag(dbParentVer.getOpenPointsHiddenFlag());
    dbQuesVer.setOpenPointsRelevantFlag(dbParentVer.getOpenPointsRelevantFlag());
    dbQuesVer.setRemarkRelevantFlag(dbParentVer.getRemarkRelevantFlag());
    dbQuesVer.setRemarksHiddenFlag(dbParentVer.getRemarksHiddenFlag());
    dbQuesVer.setResultHiddenFlag(dbParentVer.getResultHiddenFlag());
    dbQuesVer.setResultRelevantFlag(dbParentVer.getResultRelevantFlag());
    dbQuesVer.setSeriesHiddenFlag(dbParentVer.getSeriesHiddenFlag());
    dbQuesVer.setSeriesRelevantFlag(dbParentVer.getSeriesRelevantFlag());
    dbQuesVer.setMeasureHiddenFlag(dbParentVer.getMeasureHiddenFlag());
    dbQuesVer.setMeasureRelaventFlag(dbParentVer.getMeasureRelaventFlag());
    dbQuesVer.setResponsibleHiddenFlag(dbParentVer.getResponsibleHiddenFlag());
    dbQuesVer.setResponsibleRelaventFlag(dbParentVer.getResponsibleRelaventFlag());
    dbQuesVer.setCompletionDateHiddenFlag(dbParentVer.getCompletionDateHiddenFlag());
    dbQuesVer.setCompletionDateRelaventFlag(dbParentVer.getCompletionDateRelaventFlag());
    dbQuesVer.setGenQuesEquivalentFlag(dbParentVer.getGenQuesEquivalentFlag());
    dbQuesVer.setNoNegativeAnswersAllowed(dbParentVer.getNoNegativeAnswersAllowed());

  }

  private void updateQuestionDep(final QuestionnaireVersion createdVer) throws IcdmException {

    QuestionLoader quesloader = new QuestionLoader(getServiceData());
    SortedSet<Question> workSetQuestionSet = quesloader.getAllQuestions(
        new QuestionnaireVersionLoader(getServiceData()).getWorkingSet(getInputData().getQnaireId()).getId());

    if (null != workSetQuestionSet) {
      for (Question workSetQuestion : workSetQuestionSet) {
        Question depTobeAdded = null;
        Question mainQuestion = null;
        // get the dep question in Working set.
        Question workSetDepQues = fetchWorkSetDepQuestions(quesloader, workSetQuestion);
        if (workSetDepQues != null) {
          // Iterate the new version Questions
          for (Question newVersionQues : quesloader.getAllQuestions(createdVer.getId())) {
            // get the dep Id to be added
            if (CommonUtils.isEqual(getQuestionNum(newVersionQues.getId(), createdVer.getId()),
                getQuestionNum(workSetDepQues.getId(), null))) {
              depTobeAdded = newVersionQues;
            }
            // get the question to which it has to be added
            if (CommonUtils.isEqual(getQuestionNum(newVersionQues.getId(), createdVer.getId()),
                getQuestionNum(workSetQuestion.getId(), null))) {
              mainQuestion = newVersionQues;
            }
          }

          // Create a command to update the Dep Id's
          prepareQuestionUpdationData(depTobeAdded, mainQuestion);
        }
      }
    }
  }

  /**
   * Retrieves the question number for a given question ID and questionnaire version ID.
   *
   * @param questionId The ID of the question.
   * @param createdVerId The ID of the questionnaire version.
   * @return The question number as a string.
   * @throws IcdmException If an error occurs during fetching the question number.
   */
  private String getQuestionNum(final Long questionId, final Long createdVerId) throws IcdmException {
    QuestionLoader quesloader = new QuestionLoader(getServiceData());

    // Get all questions as a map for the working set
    Map<Long, Question> workSetQuestionMap = quesloader.getAllQuestionsAsMap(
        new QuestionnaireVersionLoader(getServiceData()).getWorkingSet(getInputData().getQnaireId()).getId(), true);
    Map<Long, Question> newVersionQuestionMap = new HashMap<>();

    if (createdVerId != null) {
      newVersionQuestionMap = quesloader.getAllQuestionsAsMap(createdVerId, true);
    }
    // Check if the questionId belongs to the newVersionQuestionMap
    if (newVersionQuestionMap.containsKey(questionId)) {
      // Return question number
      String qNo = String.valueOf(newVersionQuestionMap.get(questionId).getQNumber());
      Question parent = newVersionQuestionMap.get(newVersionQuestionMap.get(questionId).getParentQId());
      if (null == parent) {
        return qNo;
      }
      return getQuestionNum(newVersionQuestionMap.get(questionId).getParentQId(), createdVerId) + "." + qNo;
    }
    // Check if the questionId belongs to the workSetQuestionMap
    else if (workSetQuestionMap.containsKey(questionId)) {
      // Return question number
      String qNo = String.valueOf(workSetQuestionMap.get(questionId).getQNumber());
      Question parent = workSetQuestionMap.get(workSetQuestionMap.get(questionId).getParentQId());
      if (null == parent) {
        return qNo;
      }
      return getQuestionNum(workSetQuestionMap.get(questionId).getParentQId(), createdVerId) + "." + qNo;
    }
    // If the questionId does not belong to either map, return an empty string or throw an exception
    throw new IllegalArgumentException("Question ID not found in any map.");
  }

  /**
   * @param depTobeAdded
   * @param mainQuestion
   * @throws DataException
   * @throws IcdmException
   */
  private void prepareQuestionUpdationData(final Question depTobeAdded, final Question mainQuestion)
      throws IcdmException {
    if ((depTobeAdded != null) && (mainQuestion != null)) {
      QuestionUpdationData updateModel = new QuestionUpdationData();
      updateModel.setQuestion(mainQuestion);
      updateModel.setOnlyDepChange(true);
      updateModel.getQuestion().setDepQuesId(depTobeAdded.getId());
      if (mainQuestion.getDepQuesResp() != null) {
        updateModel.getQuestion().setDepQuesResp(mainQuestion.getDepQuesResp());
        updateModel.getQuestion().setDepQResultOptId(null);
      }
      else if (mainQuestion.getDepQResultOptId() != null) {
        TQuestionResultOption depTQuesResultOpt =
            new QuestionResultOptionLoader(getServiceData()).getEntityObject(mainQuestion.getDepQResultOptId());

        for (TQuestionResultOption tQuestionResultOption : new QuestionResultOptionLoader(getServiceData())
            .getAllQuestionResultOptionsByQID(depTobeAdded.getId())) {
          if (CommonUtils.isEqual(tQuestionResultOption.getQResultName(), depTQuesResultOpt.getQResultName()) &&
              CommonUtils.isEqual(tQuestionResultOption.getQResultType(), depTQuesResultOpt.getQResultType())) {
            updateModel.getQuestion().setDepQResultOptId(tQuestionResultOption.getQResultOptionId());
            break;
          }
        }
        updateModel.getQuestion().setDepQuesResp(null);
      }
      updateModel.getQuestion().setDeletedFlag(mainQuestion.getDeletedFlag());
      updateModel.getQuestion().setResultRelevantFlag(mainQuestion.getResultRelevantFlag());
      updateQuestion(updateModel);
    }
  }

  /**
   * @param quesloader
   * @param workSetQuestion
   * @param workSetDepQues
   * @return
   * @throws DataException
   */
  private Question fetchWorkSetDepQuestions(final QuestionLoader quesloader, final Question workSetQuestion)
      throws DataException {
    Question workSetDepQues = null;
    if (null != workSetQuestion.getDepQuesId()) {
      workSetDepQues = quesloader.getDataObjectByID(workSetQuestion.getDepQuesId());
    }
    return workSetDepQues;
  }

  /**
   * @param newData
   * @param mainQuestion
   * @throws IcdmException
   */
  private void updateQuestion(final QuestionUpdationData newData) throws IcdmException {
    QuestionUpdationCommand cmd = new QuestionUpdationCommand(getServiceData(), newData);
    executeChildCommand(cmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    QuestionnaireVersionLoader qVerLoader = new QuestionnaireVersionLoader(getServiceData());

    final TQuestionnaireVersion updateQuesVer = qVerLoader.getEntityObject(getInputData().getId());
    TQuestionnaire dbQuestionnaire =
        new QuestionnaireLoader(getServiceData()).getEntityObject(getInputData().getQnaireId());

    // if active flag yes then reset all.
    if (ApicConstants.CODE_YES.equals(getInputData().getActiveFlag())) {
      resetQuesVer(dbQuestionnaire.getTQuestionnaireVersions(), updateQuesVer);
    }
    setFieldsForQnaireVersion(updateQuesVer);
    setUserDetails(COMMAND_MODE.UPDATE, updateQuesVer);
  }

  /**
   * @param updateQuesVer
   */
  private void setFieldsForQnaireVersion(final TQuestionnaireVersion updateQuesVer) {
    updateQuesVer.setActiveFlag(getInputData().getActiveFlag());

    updateQuesVer.setDescEng(getInputData().getDescEng());
    updateQuesVer.setDescGer(getInputData().getDescGer());
    updateQuesVer.setInworkFlag(getInputData().getInworkFlag());
    updateQuesVer.setLinkHiddenFlag(getInputData().getLinkHiddenFlag());
    updateQuesVer.setLinkRelevantFlag(getInputData().getLinkRelevantFlag());
    updateQuesVer.setMeasurementHiddenFlag(getInputData().getMeasurementHiddenFlag());
    updateQuesVer.setMeasurementRelevantFlag(getInputData().getMeasurementRelevantFlag());
    updateQuesVer.setOpenPointsHiddenFlag(getInputData().getOpenPointsHiddenFlag());
    updateQuesVer.setOpenPointsRelevantFlag(getInputData().getOpenPointsRelevantFlag());
    updateQuesVer.setRemarkRelevantFlag(getInputData().getRemarkRelevantFlag());
    updateQuesVer.setRemarksHiddenFlag(getInputData().getRemarksHiddenFlag());
    updateQuesVer.setResultHiddenFlag(getInputData().getResultHiddenFlag());
    updateQuesVer.setResultRelevantFlag(getInputData().getResultRelevantFlag());
    updateQuesVer.setSeriesHiddenFlag(getInputData().getSeriesHiddenFlag());
    updateQuesVer.setSeriesRelevantFlag(getInputData().getSeriesRelevantFlag());
    // ICDM-2191
    updateQuesVer.setMeasureHiddenFlag(getInputData().getMeasureHiddenFlag());
    updateQuesVer.setMeasureRelaventFlag(getInputData().getMeasureRelaventFlag());
    updateQuesVer.setResponsibleHiddenFlag(getInputData().getResponsibleHiddenFlag());
    updateQuesVer.setResponsibleRelaventFlag(getInputData().getResponsibleRelaventFlag());
    updateQuesVer.setCompletionDateHiddenFlag(getInputData().getCompletionDateHiddenFlag());
    updateQuesVer.setCompletionDateRelaventFlag(getInputData().getCompletionDateRelaventFlag());
    updateQuesVer.setGenQuesEquivalentFlag(getInputData().getGenQuesEquivalent());
    updateQuesVer.setNoNegativeAnswersAllowed(getInputData().getNoNegativeAnsAllowedFlag());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    QuestionnaireVersionLoader qVerLoader = new QuestionnaireVersionLoader(getServiceData());
    QuestionnaireLoader qnaireLoader = new QuestionnaireLoader(getServiceData());
    final TQuestionnaire dbQuestionnaire = qnaireLoader.getEntityObject(getInputData().getQnaireId());
    final TQuestionnaireVersion deletedQuesVer = qVerLoader.getEntityObject(getInputData().getId());
    // remove ques from version
    dbQuestionnaire.getTQuestionnaireVersions().remove(deletedQuesVer);
    getEm().remove(deletedQuesVer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isDesActMeasFlagValid() || isOpnRemResFlagValid() || isSerMeasDateFlagValid() || isRespFlagValid() ||
        isEquiGenQues() || isNoNegativeAnswersAllowed();
  }

  /**
   * @return true if General questionnaire equivalent flag is changed
   */
  private boolean isEquiGenQues() {
    return isObjectChanged(getInputData().getGenQuesEquivalent(), getOldData().getGenQuesEquivalent());
  }

  private boolean isNoNegativeAnswersAllowed() {
    return isObjectChanged(getInputData().getNoNegativeAnsAllowedFlag(), getOldData().getNoNegativeAnsAllowedFlag());
  }

  /**
   *
   */
  private boolean isSerMeasDateFlagValid() {
    return isSeriesFlagValid() || isMeasurementFlagValid() || isDateFlagValid();
  }

  /**
   *
   */
  private boolean isOpnRemResFlagValid() {
    return isOpnPtsFlagValid() || isRemarksFlagValid() || isResultFlagValid();
  }

  /**
   *
   */
  private boolean isDesActMeasFlagValid() {
    return isDescValid() || isActiveFlagValid() || isMeasureFlagValid();
  }

  /**
   *
   */
  private boolean isRespFlagValid() {
    return isObjectChanged(getInputData().getResponsibleHiddenFlag(), getOldData().getResponsibleHiddenFlag()) ||
        isObjectChanged(getInputData().getResponsibleRelaventFlag(), getOldData().getResponsibleRelaventFlag());
  }

  /**
   *
   */
  private boolean isDateFlagValid() {
    return isObjectChanged(getInputData().getCompletionDateHiddenFlag(), getOldData().getCompletionDateHiddenFlag()) ||
        isObjectChanged(getInputData().getCompletionDateRelaventFlag(), getOldData().getCompletionDateRelaventFlag());
  }

  /**
   *
   */
  private boolean isMeasurementFlagValid() {
    return isObjectChanged(getInputData().getMeasurementHiddenFlag(), getOldData().getMeasurementHiddenFlag()) ||
        isObjectChanged(getInputData().getMeasurementRelevantFlag(), getOldData().getMeasurementRelevantFlag());
  }

  /**
   *
   */
  private boolean isSeriesFlagValid() {
    return isObjectChanged(getInputData().getSeriesHiddenFlag(), getOldData().getSeriesHiddenFlag()) ||
        isObjectChanged(getInputData().getSeriesRelevantFlag(), getOldData().getSeriesRelevantFlag());
  }

  /**
   *
   */
  private boolean isResultFlagValid() {
    return isObjectChanged(getInputData().getResultHiddenFlag(), getOldData().getResultHiddenFlag()) ||
        isObjectChanged(getInputData().getResultRelevantFlag(), getOldData().getResultRelevantFlag());
  }

  /**
   *
   */
  private boolean isRemarksFlagValid() {
    return isObjectChanged(getInputData().getRemarkRelevantFlag(), getOldData().getRemarkRelevantFlag()) ||
        isObjectChanged(getInputData().getRemarksHiddenFlag(), getOldData().getRemarksHiddenFlag());
  }

  /**
   *
   */
  private boolean isOpnPtsFlagValid() {
    return isObjectChanged(getInputData().getOpenPointsHiddenFlag(), getOldData().getOpenPointsHiddenFlag()) ||
        isObjectChanged(getInputData().getOpenPointsRelevantFlag(), getOldData().getOpenPointsRelevantFlag());
  }

  /**
   *
   */
  private boolean isMeasureFlagValid() {
    return isObjectChanged(getInputData().getMeasureHiddenFlag(), getOldData().getMeasureHiddenFlag()) ||
        isObjectChanged(getInputData().getMeasureRelaventFlag(), getOldData().getMeasureRelaventFlag());
  }

  /**
   *
   */
  private boolean isActiveFlagValid() {
    return isObjectChanged(getInputData().getActiveFlag(), getOldData().getActiveFlag()) ||
        isObjectChanged(getInputData().getInworkFlag(), getOldData().getInworkFlag()) ||
        isObjectChanged(getInputData().getLinkHiddenFlag(), getOldData().getLinkHiddenFlag());
  }

  /**
   *
   */
  private boolean isDescValid() {
    return isObjectChanged(getInputData().getDescEng(), getOldData().getDescEng()) ||
        isObjectChanged(getInputData().getDescGer(), getOldData().getDescGer());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    /* Empty Method */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    /* Empty Method */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
