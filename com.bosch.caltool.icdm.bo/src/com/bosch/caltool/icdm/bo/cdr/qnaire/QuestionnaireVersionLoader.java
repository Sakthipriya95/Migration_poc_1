/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionResultOption;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * Loader class for Questionnaire Version
 *
 * @author bru2cob
 */
public class QuestionnaireVersionLoader extends AbstractBusinessObject<QuestionnaireVersion, TQuestionnaireVersion> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionnaireVersionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTIONNAIRE_VERSION, TQuestionnaireVersion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected QuestionnaireVersion createDataObject(final TQuestionnaireVersion entity) throws DataException {
    QuestionnaireVersion object = new QuestionnaireVersion();

    setCommonFields(object, entity);

    object.setName(new QuestionnaireLoader(getServiceData()).getDataObjectByID(entity.getTQuestionnaire().getQnaireId())
        .getName());
    object.setQnaireId(entity.getTQuestionnaire().getQnaireId());
    object.setActiveFlag(entity.getActiveFlag());
    object.setInworkFlag(entity.getInworkFlag());
    object.setResultRelevantFlag(entity.getResultRelevantFlag());
    object.setResultHiddenFlag(entity.getResultHiddenFlag());
    object.setMeasurementRelevantFlag(entity.getMeasurementRelevantFlag());
    object.setMeasurementHiddenFlag(entity.getMeasurementHiddenFlag());
    object.setSeriesRelevantFlag(entity.getSeriesRelevantFlag());
    object.setSeriesHiddenFlag(entity.getSeriesHiddenFlag());
    object.setLinkRelevantFlag(entity.getLinkRelevantFlag());
    object.setLinkHiddenFlag(entity.getLinkHiddenFlag());
    object.setOpenPointsRelevantFlag(entity.getOpenPointsRelevantFlag());
    object.setOpenPointsHiddenFlag(entity.getOpenPointsHiddenFlag());
    object.setRemarkRelevantFlag(entity.getRemarkRelevantFlag());
    object.setRemarksHiddenFlag(entity.getRemarksHiddenFlag());
    object.setMajorVersionNum(entity.getMajorVersionNum());
    object.setMinorVersionNum(entity.getMinorVersionNum());
    object.setDescEng(entity.getDescEng());
    object.setDescGer(entity.getDescGer());
    object.setMeasureRelaventFlag(entity.getMeasureRelaventFlag());
    object.setMeasureHiddenFlag(entity.getMeasureHiddenFlag());
    object.setResponsibleRelaventFlag(entity.getResponsibleRelaventFlag());
    object.setResponsibleHiddenFlag(entity.getResponsibleHiddenFlag());
    object.setCompletionDateRelaventFlag(entity.getCompletionDateRelaventFlag());
    object.setCompletionDateHiddenFlag(entity.getCompletionDateHiddenFlag());
    object.setGenQuesEquivalent(entity.getGenQuesEquivalentFlag());
    object.setNoNegativeAnsAllowedFlag(entity.getNoNegativeAnswersAllowed());

    object.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));

    return object;
  }


  /**
   * @param questionnaireVersionId
   * @return
   * @throws DataException
   */
  public QnaireVersionModel getQnaireVersionWithDetails(final Long questionnaireVersionId) throws DataException {

    QnaireVersionModel ret = new QnaireVersionModel();
    Map<Long, Map<Long, QuestionResultOption>> questionWithResultOptionsMap = new HashMap<>();

    Set<TQuestion> questionEntities = getEntityObject(questionnaireVersionId).getTQuestions();

    QuestionConfigLoader questionConfigLoader = new QuestionConfigLoader(getServiceData());
    QuestionLoader questionLoader = new QuestionLoader(getServiceData());

    QuestionnaireVersion qVersion = getDataObjectByID(questionnaireVersionId);
    ret.setQuestionnaireVersion(qVersion);

    QuestionnaireLoader qnaireLoader = new QuestionnaireLoader(getServiceData());
    ret.setQuestionnaire(qnaireLoader.getDataObjectByID(qVersion.getQnaireId()));
    if (null != questionEntities) {
      for (TQuestion tQuestion : questionEntities) {
        // to load the question result options in QuestionVersionModel
        Map<Long, QuestionResultOption> questionResultOptMap = new HashMap<>();
        for (TQuestionResultOption tQuestionResult : new QuestionResultOptionLoader(getServiceData())
            .getAllQuestionResultOptionsByQID(tQuestion.getQId())) {
          QuestionResultOption questionResultOption =
              new QuestionResultOptionLoader(getServiceData()).createDataObject(tQuestionResult);
          questionResultOptMap.put(questionResultOption.getId(), questionResultOption);
        }
        questionWithResultOptionsMap.put(tQuestion.getQId(), questionResultOptMap);
        ret.setQuesWithQuestionResultOptionsMap(questionWithResultOptionsMap);

        ret.getQuestionMap().put(tQuestion.getQId(), questionLoader.getDataObjectByID(tQuestion.getQId()));

        if (tQuestion.getTQuestionConfig() != null) {
          ret.getQuestionConfigMap().put(tQuestion.getQId(),
              questionConfigLoader.getDataObjectByID(tQuestion.getTQuestionConfig().getQconfigId()));
        }

        // set child questions
        List<Long> childQuestions = new ArrayList<>();

        for (TQuestion childQuestion : tQuestion.getTQuestions()) {
          childQuestions.add(childQuestion.getQId());
        }
        ret.getChildQuestionIdMap().put(tQuestion.getQId(), childQuestions);
      }
    }
    return ret;
  }

  /**
   * @param questionId question id
   * @return QnaireVersionModel
   * @throws DataException Exception
   */
  public QnaireVersionModel getQnaireVModelByQuesId(final Long questionId) throws DataException {
    QuestionLoader questionLoader = new QuestionLoader(getServiceData());
    QnaireVersionModel ret = new QnaireVersionModel();
    // Current Question
    TQuestion tQuestion = new QuestionLoader(getServiceData()).getEntityObject(questionId);

    setQueResultsOptionsMap(ret, tQuestion);

    ret.getQuestionMap().put(tQuestion.getQId(), questionLoader.getDataObjectByID(tQuestion.getQId()));

    if (tQuestion.getTQuestionConfig() != null) {
      ret.getQuestionConfigMap().put(tQuestion.getQId(),
          new QuestionConfigLoader(getServiceData()).getDataObjectByID(tQuestion.getTQuestionConfig().getQconfigId()));
    }

    // set child questions
    List<Long> childQuestionsSet = new ArrayList<>();
    for (TQuestion childQuestion : tQuestion.getTQuestions()) {
      ret.getQuestionMap().put(childQuestion.getQId(), questionLoader.getDataObjectByID(childQuestion.getQId()));
      if (childQuestion.getTQuestionConfig() != null) {
        ret.getQuestionConfigMap().put(childQuestion.getQId(), new QuestionConfigLoader(getServiceData())
            .getDataObjectByID(childQuestion.getTQuestionConfig().getQconfigId()));
        setQueResultsOptionsMap(ret, childQuestion);
      }
      childQuestionsSet.add(childQuestion.getQId());
      List<Long> innerChildQuestionsSet = new ArrayList<>();
      for (TQuestion innerChildQues : childQuestion.getTQuestions()) {
        ret.getQuestionMap().put(innerChildQues.getQId(), questionLoader.getDataObjectByID(innerChildQues.getQId()));
        if (innerChildQues.getTQuestionConfig() != null) {
          ret.getQuestionConfigMap().put(innerChildQues.getQId(), new QuestionConfigLoader(getServiceData())
              .getDataObjectByID(innerChildQues.getTQuestionConfig().getQconfigId()));
          setQueResultsOptionsMap(ret, innerChildQues);
        }
        innerChildQuestionsSet.add(innerChildQues.getQId());
      }
      ret.getChildQuestionIdMap().put(childQuestion.getQId(), innerChildQuestionsSet);

    }
    ret.getChildQuestionIdMap().put(tQuestion.getQId(), childQuestionsSet);
    return ret;

  }

  private void setQueResultsOptionsMap(final QnaireVersionModel ret, final TQuestion tQuestion) throws DataException {
    Map<Long, Map<Long, QuestionResultOption>> quesWithQuestionResultOptionsMap =
        ret.getQuesWithQuestionResultOptionsMap();
    if (quesWithQuestionResultOptionsMap == null) {
      quesWithQuestionResultOptionsMap = new HashMap<>();
    }
    // to load the question result options in QuestionVersionModel
    Map<Long, QuestionResultOption> questionResultOptMap = new HashMap<>();
    for (TQuestionResultOption tQuestionResult : new QuestionResultOptionLoader(getServiceData())
        .getAllQuestionResultOptionsByQID(tQuestion.getQId())) {
      QuestionResultOption questionResultOption =
          new QuestionResultOptionLoader(getServiceData()).createDataObject(tQuestionResult);
      questionResultOptMap.put(questionResultOption.getId(), questionResultOption);
    }
    quesWithQuestionResultOptionsMap.put(tQuestion.getQId(), questionResultOptMap);

    ret.setQuesWithQuestionResultOptionsMap(quesWithQuestionResultOptionsMap);
  }

  /**
   * @param wpDivsWithQs
   * @param qsWithoutActiveVersions
   * @return
   * @throws IcdmException
   */
  public SortedSet<QuestionnaireVersion> getQuestionnaireVersions(final List<Long> wpDivsWithQs,
      final SortedSet<com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire> qsWithoutActiveVersions)
      throws IcdmException {
    final TypedQuery<TQuestionnaireVersion> typeQuery =
        getEntMgr().createNamedQuery(TQuestionnaireVersion.NQ_GET_QNVERS_FOR_WPDIV, TQuestionnaireVersion.class);

    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    typeQuery.setParameter("wpDivIds", wpDivsWithQs);

    final List<TQuestionnaireVersion> qNaireVersions = typeQuery.getResultList();

    SortedSet<QuestionnaireVersion> existingQuestVersions = new TreeSet<>();
    for (TQuestionnaireVersion qNaireVersion : qNaireVersions) {
      QuestionnaireVersion questionnaireVersion = getDataObjectByID(qNaireVersion.getQnaireVersId());
      com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire questionnaire =
          new QuestionnaireLoader(getServiceData()).getDataObjectByID(questionnaireVersion.getQnaireId());

      if (getAllVersions(questionnaireVersion.getQnaireId()).size() == 1) {
        if (!CommonUtils.isEqualIgnoreCase(questionnaireVersion.getActiveFlag(), CommonUtilConstants.CODE_YES)) {
          qsWithoutActiveVersions.add(questionnaire);
        }
      }
      else if (CommonUtils.isEqualIgnoreCase(questionnaireVersion.getActiveFlag(), CommonUtilConstants.CODE_YES)) {
        existingQuestVersions.add(questionnaireVersion);
      }

    }
    return existingQuestVersions;
  }


  /**
   * @param questionnaireId questionnaireId
   * @return sorted set of QuestionnaireVersion
   * @throws DataException error during fetching data
   */
  public SortedSet<QuestionnaireVersion> getAllVersions(final Long questionnaireId) throws DataException {
    SortedSet<QuestionnaireVersion> retSet = new TreeSet<>();
    Set<TQuestionnaireVersion> tQuestionnaireVersions =
        new QuestionnaireLoader(getServiceData()).getEntityObject(questionnaireId).getTQuestionnaireVersions();
    QuestionnaireVersionLoader qversionLoader = new QuestionnaireVersionLoader(getServiceData());
    if (null != tQuestionnaireVersions) {
      for (TQuestionnaireVersion tQuestionnaireVersion : tQuestionnaireVersions) {
        retSet.add(qversionLoader.createDataObject(tQuestionnaireVersion));
      }
    }
    return retSet;
  }


  /**
   * @param questionnaireId questionnaireId
   * @return active QuestionnaireVersion
   * @throws DataException error during fetching data
   */
  public QuestionnaireVersion getActiveQnnaireVersion(final Long questionnaireId) throws DataException {
    SortedSet<QuestionnaireVersion> questionnaireVersionSet = getAllVersions(questionnaireId);
    if (null != questionnaireVersionSet) {
      for (QuestionnaireVersion questionnaireVersion : questionnaireVersionSet) {
        if (ApicConstants.CODE_YES.equals(questionnaireVersion.getActiveFlag())) {
          return questionnaireVersion;
        }
      }
    }
    return null;
  }


  /**
   * @param questionnaireId questionnaireId
   * @return QuestionnaireVersion
   * @throws DataException error while retrieving data
   */
  public QuestionnaireVersion getWorkingSet(final Long questionnaireId) throws DataException {
    SortedSet<QuestionnaireVersion> questionnaireVersionSet = getAllVersions(questionnaireId);
    if (null != questionnaireVersionSet) {
      for (QuestionnaireVersion questionnaireVersion : questionnaireVersionSet) {
        if ((questionnaireVersion.getMajorVersionNum() == 0L) && (questionnaireVersion.getMinorVersionNum() == null)) {
          // if major version is zero and minor version is null
          return questionnaireVersion;
        }
      }
    }
    return null;
  }

  /**
   * @return active general questionniare versions id
   * @throws DataException exception
   */
  public Long getActiveGenrlQnaireVers() throws DataException {
    return getActiveVersForGivQnaireId(CommonParamKey.GENERAL_QNAIRE_ID);
  }

  /**
   * @return active general questionniare versions id
   * @throws DataException exception
   */
  public Long getActiveOBDGenrlQnaireVers() throws DataException {
    return getActiveVersForGivQnaireId(CommonParamKey.OBD_GENERAL_QNAIRE_ID);
  }

  /**
   * @param QnaireId
   * @return
   * @throws DataException
   * @throws DataNotFoundException
   */
  private Long getActiveVersForGivQnaireId(final CommonParamKey qnaireId) throws DataException, DataNotFoundException {
    String genQnairePropVal = new CommonParamLoader(getServiceData()).getValue(qnaireId);
    if (CommonUtils.isNotEmptyString(genQnairePropVal)) {
      Long genQnaireId = Long.valueOf(genQnairePropVal);
      return new QuestionnaireVersionLoader(getServiceData()).getActiveQnnaireVersion(genQnaireId).getId();
    }
    throw new DataNotFoundException(
        "No active general questionnaire version found for questionnaire id " + genQnairePropVal);
  }

}
