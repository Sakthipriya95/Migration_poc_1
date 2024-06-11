/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.bo.IPIDCAttribute;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionConfig;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;


/**
 * @author jvi6cob
 */
@Deprecated
public class Question extends AbstractCdrObject implements Comparable<Question>, IAttributeMappedObject {


  /**
   *
   */
  private static final int ONE_CONST = 1;

  private static final String FLD_QUES_NAME_ENG = "QUES_NAME_ENG";

  private static final String FLD_QUES_NAME_GER = "QUES_NAME_GER";

  private static final String FLD_QUES_DESC_ENG = "QUES_DESC_ENG";

  private static final String FLD_QUES_DESC_GER = "QUES_DESC_GER";

  private static final String FLD_Q_NO = "FLD_Q_NO";
  private static final String FLD_HEADING = "HEADING";

  private static final String FLD_DELETED = "DELETED";

  private final Set<QuestionDepenAttr> questionDepenAttrs;

  private QuestionConfig questionConfig;// null when Question is heading


  private final SortedSet<Question> childQuestions;

  private final ConcurrentMap<Attribute, QuestionDepenAttr> attrDepnMap =
      new ConcurrentHashMap<Attribute, QuestionDepenAttr>();
  private static final int MAX_LEVELS_ALLOWED = 3;
  /**
   * Level 3 index
   */
  private static final int LEVEL_3 = 3;
  /**
   * Level 2 index
   */
  private static final int LEVEL_2 = 2;
  /**
   * Level 1 index
   */
  private static final int LEVEL_1 = 1;

  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Ques Name
                            */
                           SORT_QUES_NAME,
                           /**
                            * Ques Number
                            */
                           SORT_QUES_NUMBER,
                           /**
                            * Ques Hint
                            */
                           SORT_QUES_HINT,
                           /**
                            * Measurable
                            */
                           SORT_QUES_MEASURABLE,
                           /**
                            * Series
                            */
                           SORT_SERIES,
                           /**
                            * Link
                            */
                           SORT_LINK,
                           /**
                            * Result
                            */
                           SORT_RESULT,

                           /**
                            * Remark
                            */
                           SORT_REMARK,
                           /**
                            * Open points
                            */
                           SORT_OP,
                           /**
                            * Measures
                            */
                           SORT_MEASURES,
                           /**
                            * Responsible
                            */
                           SORT_RESPONSIBLE,
                           /**
                            * Date
                            */
                           SORT_DATE;
  }

  /**
   * ICDM-2054 key- combination id, value - QuesDepnValCombination
   */
  private final ConcurrentMap<Long, QuesDepnValCombination> quesAttrValComb =
      new ConcurrentHashMap<Long, QuesDepnValCombination>();


  /**
   * @param cdrDataProvider CDRDataProvider
   * @param qID Long
   */
  public Question(final CDRDataProvider cdrDataProvider, final Long qID) {
    super(cdrDataProvider, qID);
    getDataCache().getQuestionMap().put(qID, this);
    this.childQuestions = new TreeSet<Question>();
    this.questionDepenAttrs = new HashSet<QuestionDepenAttr>();

    if (!isHeading()) {
      TQuestionConfig tQuestionConfig = getEntityProvider().getDbQuestion(getID()).getTQuestionConfig();
      Long qconfigId = tQuestionConfig.getQconfigId();
      this.questionConfig = new QuestionConfig(cdrDataProvider, qconfigId);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQuestion(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQuestion(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestion(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestion(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QUESTION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getNameEng(), getNameGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * Returns the Question Name in ENGLISH
   *
   * @return Question English Name in String
   */
  public String getNameEng() {
    String returnValue = getEntityProvider().getDbQuestion(getID()).getQNameEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Question Name in GERMAN
   *
   * @return Question German Name in String
   */
  public String getNameGer() {
    String returnValue = getEntityProvider().getDbQuestion(getID()).getQNameGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * {@inheritDoc} Returns the Question HINT(Description) in ENGLISH/GERMAN
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getDescEng(), getDescGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * Returns the Question HINT(Description) in ENGLISH
   *
   * @return Question English HINT(Description) in String
   */
  public String getDescEng() {
    String returnValue = getEntityProvider().getDbQuestion(getID()).getQHintEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Question HINT(Description) in ENGLISH
   *
   * @return Question German HINT(Description) in String
   */
  public String getDescGer() {
    String returnValue = getEntityProvider().getDbQuestion(getID()).getQHintGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Question questionToCmp) {
    int compResult = 0;
    if (null != questionToCmp) {
      compResult = ApicUtil.compare(getPaddedQuestionNumber(), questionToCmp.getPaddedQuestionNumber());
      if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        return ApicUtil.compare(getID(), questionToCmp.getID());
      }
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return true if this is a heading
   */
  public boolean isHeading() {
    return getEntityProvider().getDbQuestion(getID()).getHeadingFlag().equals(ApicConstants.YES);
  }

  /**
   * @return question result type
   */
  public String getPositiveResult() {
    return getEntityProvider().getDbQuestion(getID()).getPositiveResult();
  }

  /**
   * @return question result ui type
   */
  public String getPositiveResultUIString() {
    return CDRConstants.QS_RESULT_TYPE.getTypeByDbCode(getPositiveResult()).getUiType();
  }

  /**
   * Returns the link value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getLink() {
    if (!isHeading()) {
      return this.questionConfig.getLink();
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }


  /**
   * @return link display string
   */
  public String getLinkUIString() {
    if (isHeading()) {
      return null;
    }
    if (showLink()) {
      return CommonUtils.checkNull(getLink().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showLink() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getLinkRelevantFlag());
  }

  /**
   * Returns the link value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getMeasurement() {
    if (!isHeading()) {
      return this.questionConfig.getMeasurement();
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * @return measurement display string
   */
  public String getMeasurementUIString() {
    if (isHeading()) {
      return null;
    }
    if (showMeasurement()) {
      return CommonUtils.checkNull(getMeasurement().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showMeasurement() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getMeasurementRelevantFlag());
  }

  /**
   * Returns the Open points value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getOpenPoints() {
    if (!isHeading()) {
      return this.questionConfig.getOpenPoints();
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * @return open points display string
   */
  public String getOpenPointsUIString() {
    if (isHeading()) {
      return null;
    }
    if (showOpenPoints()) {
      return CommonUtils.checkNull(getOpenPoints().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showOpenPoints() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getOpenPointsRelevantFlag());
  }

  /**
   * Returns the Remark value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getRemark() {
    if (!isHeading()) {
      return this.questionConfig.getRemark();
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * @return remark display string
   */
  public String getRemarkUIString() {
    if (isHeading()) {
      return null;
    }
    if (showRemark()) {
      return CommonUtils.checkNull(getRemark().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showRemark() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getRemarkRelevantFlag());
  }

  /**
   * Returns the Result value for this Question
   *
   * @return String
   */
  public QUESTION_CONFIG_TYPE getResult() {
    if (!isHeading()) {
      return this.questionConfig.getResult();
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * @return Result display string
   */
  public String getResultUIString() {
    if (isHeading()) {
      return null;
    }
    if (showResult()) {
      return CommonUtils.checkNull(getResult().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showResult() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getResultRelevantFlag());
  }

  /**
   * Returns the series value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getSeries() {
    if (!isHeading()) {
      return this.questionConfig.getSeries();
    }
    return QUESTION_CONFIG_TYPE.getType("");

  }

  /**
   * @return series display string
   */
  public String getSeriesUIString() {
    if (isHeading()) {
      return null;
    }
    if (showSeries()) {
      return CommonUtils.checkNull(getSeries().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showSeries() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getSeriesRelevantFlag());
  }

  // ICDM-2191
  /**
   * Returns the series value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getMeasure() {
    if (!isHeading()) {
      return this.questionConfig.getMeasure();
    }
    return QUESTION_CONFIG_TYPE.getType("");

  }

  /**
   * @return measure display string
   */
  public String getMeasureUIString() {
    if (isHeading()) {
      return null;
    }
    if (showMeasure()) {
      return CommonUtils.checkNull(getMeasure().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showMeasure() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getMeasureRelaventFlag());
  }

  /**
   * Returns the series value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getResponsible() {
    if (!isHeading()) {
      return this.questionConfig.getResponsible();
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * @return responsible display string
   */
  public String getResponsibleUIString() {
    if (isHeading()) {
      return null;
    }
    if (showResponsible()) {
      return CommonUtils.checkNull(getResponsible().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showResponsible() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getResponsibleRelaventFlag());
  }

  /**
   * Returns the series value for this Question
   *
   * @return QUESTION_CONFIG_TYPE
   */
  public QUESTION_CONFIG_TYPE getCompletionDate() {
    if (!isHeading()) {
      return this.questionConfig.getCompletionDate();
    }
    return QUESTION_CONFIG_TYPE.getType("");

  }

  /**
   * @return CompletionDate display string
   */
  public String getCompletionDateUIString() {
    if (isHeading()) {
      return null;
    }
    if (showCompletionDate()) {
      return CommonUtils.checkNull(getCompletionDate().getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * @return
   */
  public boolean showCompletionDate() {
    return CommonUtils.getBooleanType(getQuestionnaireVersion().getCompletionDateRelaventFlag());
  }

  /**
   * @return the parent question/heading
   */
  public Question getParentQuestion() {
    Question parent = null;
    // For the first version the parent version would be null
    if (null != getEntityProvider().getDbQuestion(getID()).getTQuestion()) {
      parent = getDataCache().getQuestion(getEntityProvider().getDbQuestion(getID()).getTQuestion().getQId());
    }
    return parent;
  }

  /**
   * @return Question Number of this question without the parent's number
   */
  public long getQNum() {
    return getEntityProvider().getDbQuestion(getID()).getQNumber();
  }

  /**
   * @return question number of this question including the parent's question number
   */
  public String getQuestionNumber() {
    return getQuestionNumber(this);
  }

  /**
   * @return the question number, including the parent's number, with '0' padded at each level to the width of 9
   */
  private String getPaddedQuestionNumber() {
    return getPaddedQuestionNumber(this);
  }

  /**
   * Find the padded question number for the given question
   *
   * @param question for which the padding to be done
   * @return the question number with '0' padded at each level to the width of 9
   */
  private String getPaddedQuestionNumber(final Question question) {
    String padQn = String.format("%09d", question.getQNum());

    Question parent = question.getParentQuestion();
    if (null == parent) {
      return padQn;
    }

    return getPaddedQuestionNumber(parent) + "." + padQn;

  }

  /**
   * @return all child questions corresponding to a heading/parent question
   */
  public SortedSet<Question> getChildQuestions() {
    synchronized (this) {
      this.childQuestions.clear();
      getLogger().debug("Loading all child questions of parent question : {}", getID());
      Set<TQuestion> childQues = getEntityProvider().getDbQuestion(getID()).getTQuestions();
      Question quesBO;
      if (null != childQues) {
        for (TQuestion ques : childQues) {
          quesBO = getDataCache().getQuestion(ques.getQId());
          if (quesBO == null) {
            quesBO = new Question(getDataProvider(), ques.getQId());
          }
          this.childQuestions.add(quesBO);
        }
      }

      getLogger().debug("Total child questions count = {}", this.childQuestions.size());

    }
    return this.childQuestions;

  }

  /**
   * Build and find the question number for the given question, considering the parent question's number as well
   *
   * @param question question
   * @return question number as string
   */
  private String getQuestionNumber(final Question question) {
    String qNo = String.valueOf(question.getQNum());

    Question parent = question.getParentQuestion();
    if (null == parent) {
      return qNo;
    }

    return getQuestionNumber(parent) + "." + qNo;
  }

  /**
   * @return Attribute Dependencies for this question
   */
  public Set<QuestionDepenAttr> getDepenAttributes() {
    if (this.questionDepenAttrs.isEmpty()) {
      getLogger().debug("Loading Attribute dependency value combinations of Question : {}", getID());
      Set<TQuestionDepenAttribute> qDepenAttributes =
          getEntityProvider().getDbQuestion(getID()).getTQuestionDepenAttributes();
      QuestionDepenAttr qDepenAttrBO;
      for (TQuestionDepenAttribute tQuestionDepenAttr : qDepenAttributes) {
        qDepenAttrBO = getDataCache().getQuestionDepenAttribute(tQuestionDepenAttr.getQattrDepenId());
        if (qDepenAttrBO == null) {
          qDepenAttrBO = new QuestionDepenAttr(getDataProvider(), tQuestionDepenAttr.getQattrDepenId());
        }
        this.questionDepenAttrs.add(qDepenAttrBO);
      }
      getLogger().debug("Total Attribute dependencies count = {}", this.questionDepenAttrs.size());

    }

    return this.questionDepenAttrs;
  }

  /**
   * @return QuestionConfig
   */
  public QuestionConfig getQuesConfig() {
    return this.questionConfig;
  }

  /**
   * {@inheritDoc} ICDM-2054
   */
  @Override
  public Set<Attribute> getAttributes() {
    Set<Attribute> mappedAttributes = new TreeSet<>();
    if (!getDepenAttributes().isEmpty()) {
      for (QuestionDepenAttr depenAttr : getDepenAttributes()) {
        mappedAttributes.add(depenAttr.getAttribute());
        this.attrDepnMap.put(depenAttr.getAttribute(), depenAttr);
      }
    }
    return mappedAttributes;
  }

  /**
   * @return the attrDepnMap
   */
  public Map<Attribute, QuestionDepenAttr> getAttrDepnMap() {
    return this.attrDepnMap;
  }

  /**
   * @return the quesAttrValComb
   */
  public Map<Long, QuesDepnValCombination> getQuesAttrValComb() {
    loadAttrValCombinations();
    return this.quesAttrValComb;
  }

  /**
   * ICDM-2054 load attribute value combinations into data cache
   */
  private void loadAttrValCombinations() {

    if (this.quesAttrValComb.isEmpty()) {
      getLogger().debug("Loading Attribute dependency value combinations of Question : {}", getID());
      Set<TQuestionDepenAttribute> qDepenAttributes =
          getEntityProvider().getDbQuestion(getID()).getTQuestionDepenAttributes();
      QuestionDepenAttr qDepenAttrBO;
      for (TQuestionDepenAttribute tQuestionDepenAttr : qDepenAttributes) {
        qDepenAttrBO = getDataCache().getQuestionDepenAttribute(tQuestionDepenAttr.getQattrDepenId());
        if (qDepenAttrBO == null) {
          qDepenAttrBO = new QuestionDepenAttr(getDataProvider(), tQuestionDepenAttr.getQattrDepenId());
        }
        this.questionDepenAttrs.add(qDepenAttrBO);

        Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues = tQuestionDepenAttr.getTQuestionDepenAttrValues();
        createQuesDepnCombinations(qDepenAttrBO, tQuestionDepenAttrValues);
      }
      getLogger().debug("Total Attribute dependencies combination count = {}", this.quesAttrValComb.size());

    }


  }

  /**
   * @param qDepenAttrBO QuestionDepenAttr
   * @param tQuestionDepenAttrValues Set<TQuestionDepenAttrValue>
   */
  private void createQuesDepnCombinations(final QuestionDepenAttr qDepenAttrBO,
      final Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues) {
    QuestionDepenAttrValue depnAttrValue;
    for (TQuestionDepenAttrValue tdepnAttrVal : tQuestionDepenAttrValues) {
      // iterate through the attribute values for a TQuestionDepenAttribute
      depnAttrValue = getDataCache().getQuestionDepenAttrValue(tdepnAttrVal.getDepenAttrValId());
      if (depnAttrValue == null) {
        // create QuestionDepenAttrValue
        depnAttrValue = new QuestionDepenAttrValue(getDataProvider(), tdepnAttrVal.getDepenAttrValId());
      }
      QuesDepnValCombination quesDepnValComb = this.quesAttrValComb.get(tdepnAttrVal.getQCombiNum());
      if (CommonUtils.isNull(quesDepnValComb)) {
        // add depnAttrValue as new combination
        // create attr val combination bo
        quesDepnValComb = new QuesDepnValCombination(this, false);
        quesDepnValComb.setCombinationId(tdepnAttrVal.getQCombiNum());
        this.quesAttrValComb.put(tdepnAttrVal.getQCombiNum(), quesDepnValComb);
      }
      quesDepnValComb.getQuesAttrValMap().put(qDepenAttrBO, depnAttrValue);
      quesDepnValComb.getAttrValMap().put(qDepenAttrBO.getAttribute(), depnAttrValue.getAttributeValue());
    }
  }


  /**
   * Check, if the Question has been marked as deleted
   *
   * @return TRUE, if the Question has been marked as deleted
   */
  public boolean isDeleted() {
    return ApicConstants.YES.equals(getEntityProvider().getDbQuestion(getID()).getDeletedFlag());

  }

  /**
   * Checks whether the question is deleted by verifiying its's deleted flag or one of its parent's in the hierarchy.
   *
   * @return true, if question is deleted
   */
  public boolean isDeletedExt() {
    return isDeletedExt(this);
  }

  /**
   * Checks whether the given question is deleted by verifiying its's deleted flag or one of its parent's in the
   * hierarchy.
   *
   * @param question to check
   * @return true, if question is deleted
   */
  private boolean isDeletedExt(final Question question) {
    if (question.isDeleted()) {
      return true;
    }
    if (question.getParentQuestion() != null) {
      return isDeletedExt(question.getParentQuestion());
    }
    return false;
  }


  /**
   * Checks whether the question is valid for a questionnaire response identified by the project attribute map
   *
   * @param projAttrMap effective project attributes for the question response
   * @return true if the question is valid
   */
  /*
   * project Attr Map obtained by QuestionnaireResponse.getEffectiveProjectAttrs()
   */
  public boolean isValid(final Map<Long, IPIDCAttribute> projAttrMap) {
    // Question is not valid if it is deleted
    if (isDeletedExt()) {
      return false;
    }

    // Check validity based on the attribute dependency
//    QnaireRespDepnEvaluator depEval = new QnaireRespDepnEvaluator(getDataProvider());
    return false;//depEval.isQuestionApplicable(this, projAttrMap);
  }

  /**
   * default method to find maximum of combination id
   *
   * @return Maximum of combination id
   */
  public Long getMaxCombNumber() {
    // in case of deletion , we do not reorder the combination numbers
    // hence getting the size of the map will not work for maximum combination number
    Long maxCombNo = 1L;
    if (this.quesAttrValComb != null) {
      for (Long combNum : this.quesAttrValComb.keySet()) {
        if (combNum > maxCombNo) {
          maxCombNo = combNum;
        }
      }
    }
    return maxCombNo;
  }

  /**
   * @return QuestionnaireVersion
   */
  public QuestionnaireVersion getQuestionnaireVersion() {
    return getDataCache().getQuestionnaireVersion(
        getEntityProvider().getDbQuestion(getID()).getTQuestionnaireVersion().getQnaireVersId());
  }

  /**
   * @param questionToCmp parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final Question questionToCmp, final SortColumns sortColumn) { // NOPMD

    int compareResult;

    switch (sortColumn) {
      case SORT_QUES_NAME:
        // comparing the question names
        compareResult = ApicUtil.compare(getName(), questionToCmp.getName());
        break;
      case SORT_QUES_NUMBER:
        // comparing the ques number
        compareResult = ApicUtil.compare(getPaddedQuestionNumber(), questionToCmp.getPaddedQuestionNumber());
        break;
      case SORT_QUES_HINT:
        // comparing the question hint
        compareResult = ApicUtil.compare(getDescription(), questionToCmp.getDescription());
        break;
      case SORT_QUES_MEASURABLE:
        compareResult = compareMeasurable(questionToCmp);
        break;
      case SORT_SERIES:
        compareResult = compareSeries(questionToCmp);
        break;
      case SORT_RESULT:
        compareResult = compareResult(questionToCmp);
        break;
      case SORT_LINK:
        compareResult = compareLink(questionToCmp);
        break;
      case SORT_REMARK:
        compareResult = compareRemark(questionToCmp);
        break;
      case SORT_OP:
        compareResult = compareOpenPoints(questionToCmp);
        break;
      case SORT_MEASURES:
        compareResult = compareMeasures(questionToCmp);

        break;
      case SORT_RESPONSIBLE:
        compareResult = compareResponsible(questionToCmp);

        break;
      case SORT_DATE:
        compareResult = compareDate(questionToCmp);

        break;
      default:
        // Compare q number
        compareResult = compareTo(questionToCmp);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the q number
      compareResult = compareTo(questionToCmp);
    }

    return compareResult;
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareDate(final Question questionToCmp) {
    // compare completion date
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getCompletionDate().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getCompletionDate().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareResponsible(final Question questionToCmp) {
    // compare responsible
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getResponsible().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getResponsible().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareMeasures(final Question questionToCmp) {
    // compare the measures
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getMeasure().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getMeasure().getUiType());

  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareOpenPoints(final Question questionToCmp) {
    // compare the open points
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getOpenPoints().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getOpenPoints().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareRemark(final Question questionToCmp) {
    // compare the remark
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getRemark().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getRemark().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareLink(final Question questionToCmp) {
    // comparing the link
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getLink().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getLink().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareResult(final Question questionToCmp) {
    // comparing the result
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getResult().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getResult().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareSeries(final Question questionToCmp) {
    // comparing the series
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getSeries().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getSeries().getUiType());
  }

  /**
   * @param questionToCmp
   * @return
   */
  private int compareMeasurable(final Question questionToCmp) {
    // comparing the measurable
    return ApicUtil.compare(
        getQuesConfig() == null ? ApicConstants.EMPTY_STRING : getQuesConfig().getMeasurement().getUiType(),
        questionToCmp.getQuesConfig() == null ? ApicConstants.EMPTY_STRING
            : questionToCmp.getQuesConfig().getMeasurement().getUiType());

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    objDetails.put(FLD_QUES_NAME_ENG, getNameEng());
    objDetails.put(FLD_QUES_NAME_GER, getNameGer());
    objDetails.put(FLD_QUES_DESC_ENG, getDescEng());
    objDetails.put(FLD_QUES_DESC_GER, getDescGer());
    objDetails.put(FLD_HEADING, String.valueOf(isHeading()));
    objDetails.put(FLD_DELETED, String.valueOf(isDeleted()));
    objDetails.put(FLD_Q_NO, getQuestionNumber());

    return objDetails;
  }

  /**
   * @return question number of this question
   */
  public String getQuestionNumberWithName() {
    return getQuestionNumber() + "  " + getName();
  }

  /**
   * @return the level of the question
   */
  public int getQuestionLevel() {
    if (null == getParentQuestion()) {
      return LEVEL_1;
    }
    else if ((null != getParentQuestion()) && (null == getParentQuestion().getParentQuestion())) {
      return LEVEL_2;
    }
    else {
      return LEVEL_3;
    }
  }

  /**
   * @return the previous qs in the first level or the second level
   */
  public Question getPreviousQuestion() {
    Question previousQs = null;
    SortedSet<Question> firstLevelQs = null;
    if (getQuestionLevel() == LEVEL_1) {
      firstLevelQs = getQuestionnaireVersion().getFirstLevelQuestions();
    }
    else if (getQuestionLevel() == LEVEL_2) {
      firstLevelQs = getParentQuestion().getChildQuestions();
    }
    if (null != firstLevelQs) {
      List<Question> qsList = new ArrayList<>(firstLevelQs);
      int qsIndex = qsList.indexOf(this);
      if ((qsIndex - ONE_CONST) >= 0) {
        previousQs = qsList.get(qsIndex - ONE_CONST);
      }
    }
    return previousQs;
  }

  /**
   * @return the maxLevelsAllowed
   */
  public int getMaxLevelsAllowed() {
    return Question.MAX_LEVELS_ALLOWED;
  }

  /**
   * @return true if question has third level qs
   */
  public boolean hasThirdLevel() {
    boolean isThirdLevel = false;
    if (getParentQuestion() == null) {
      for (Question qs : getChildQuestions()) {
        if (!qs.getChildQuestions().isEmpty()) {
          isThirdLevel = true;
          break;
        }
      }
    }

    return isThirdLevel;
  }

  /**
   * @param questionConfig the questionConfig to set
   */
  public void setQuestionConfig(final QuestionConfig questionConfig) {
    this.questionConfig = questionConfig;
  }

  /**
   * Checks whether the questionnaire is 'General' type
   *
   * @return true, if this is a general questionnaire's version
   */
  public boolean isGeneralType() {
    return getQuestionnaireVersion().isGeneralType();
  }

  /**
   * @return the dependent question
   */
  public Question getDependentQuestion() {
    Question parent = null;
    // For the first version the parent version would be null
    if (null != getEntityProvider().getDbQuestion(getID()).getDepQuestion()) {
      parent = getDataCache().getQuestion(getEntityProvider().getDbQuestion(getID()).getDepQuestion().getQId());
    }
    return parent;
  }

  /**
   * @return the dependent question response positive/negative
   */
  public String getDepQuesResponse() {
    return getEntityProvider().getDbQuestion(getID()).getDepQuesResponse();
  }

  /**
   * @return the questions removing the cyclic dep
   */
  public SortedSet<Question> getQuestionWithoutCyclic() {
    SortedSet<Question> retQues = new TreeSet<>();
    for (Question qsn : getQuestionnaireVersion().getAllQuestions(false)) {
      Question depQues = qsn.getDependentQuestion();
      if ((depQues == null) || !hasCyclicDep(qsn)) {
        retQues.add(qsn);
      }
    }
    return retQues;
  }

  /**
   * @param ques
   * @return
   */
  private boolean hasCyclicDep(final Question qsn) {
    Question depQues = qsn.getDependentQuestion();
    while (depQues != null) {
      if (CommonUtils.isEqual(depQues, this)) {
        return true;
      }
      depQues = depQues.getDependentQuestion();
    }
    return false;
  }

}
