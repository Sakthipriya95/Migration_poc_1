/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVersDataResolver;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_ASSESMENT_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;

/**
 * @author MSP5COB
 */
public class QnaireResponseBO extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String DELIMITER_STRING = ", ";
  /**
   * not applicable constant.
   */
  private static final String NOT_APPL = "<NOT APPLICABLE>";
  /**
  *
  */
  private static final String SUFFIX_RESULT_COL_VAL = " answer";
  /**
  *
  */
  private static final String PREFIX_RESULT_COL_VAL = " with ";

  /**
   * The qnaire def model.
   */
  private RvwQnaireResponseModel qnaireRespModel = new RvwQnaireResponseModel();

  private final QnaireRespVersDataResolver quesRespdataResolver;

  private final QnaireVersionModel qnaireDefModel;

  /**
   * @param serviceData ServiceData
   * @param qnaireResponseCombinedModel QnaireResponseCombinedModel
   * @param quesRespdataResolver QnaireRespVersDataResolver
   * @param qnaireDefModel QnaireVersionModel
   */
  public QnaireResponseBO(final ServiceData serviceData, final QnaireResponseCombinedModel qnaireResponseCombinedModel,
      final QnaireRespVersDataResolver quesRespdataResolver, final QnaireVersionModel qnaireDefModel) {
    super(serviceData);
    this.qnaireRespModel = qnaireResponseCombinedModel.getRvwQnaireResponseModel();
    this.quesRespdataResolver = quesRespdataResolver;
    this.quesRespdataResolver.loadMainQuestions(this.qnaireRespModel);
    this.qnaireDefModel = qnaireDefModel;
  }

  /**
   * Gets the all question map.
   *
   * @return the all question map
   */
  public Map<Long, RvwQnaireAnswer> getAllQuestionMap() {
    return this.quesRespdataResolver.getAllQAMap();
  }

  /**
   * Gets the all question answers.
   *
   * @return the all question answers
   */
  public SortedSet<RvwQnaireAnswer> getAllQuestionAnswers() {
    return getAllQuestionMap().values().stream().collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * Checks if is question visible.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if the question is visible
   */
  public boolean isQuestionVisible(final RvwQnaireAnswer rvwQnaireAns) {
    return this.quesRespdataResolver.isQuestionVisible(rvwQnaireAns, true);
  }

  /**
   * Gets the ques response.
   *
   * @return the quesResponse
   */
  public RvwQnaireResponse getQuesResponse() {
    return this.qnaireRespModel.getRvwQnrResponse();
  }

  /**
   * @return the qnaireRespModel
   */
  public RvwQnaireResponseModel getQnaireRespModel() {
    return this.qnaireRespModel;
  }


  /**
   * Gets the question.
   *
   * @param questionId the question id
   * @return the question
   */
  public Question getQuestion(final Long questionId) {
    if ((questionId == null) || !this.qnaireDefModel.getQuestionMap().containsKey(questionId)) {
      return null;
    }
    return this.qnaireDefModel.getQuestionMap().get(questionId);
  }

  /**
   * Checks for question.
   *
   * @param questionId the question id
   * @return true, if successful
   */
  public boolean hasQuestion(final Long questionId) {
    return getQuestion(questionId) != null;
  }

  /**
   * Find the padded question number for the given question.
   *
   * @param questionId the question id
   * @return the question number with '0' padded at each level to the width of 9
   */
  public String getPaddedQuestionNumber(final long questionId) {
    String padQn = "M" + String.format("%09d", getQNum(questionId));

    Question currQstn = getQuestion(questionId);
    if (currQstn != null) {
      Question parentQstn = getQuestion(currQstn.getParentQId());
      if (null == parentQstn) {
        return padQn;
      }
      return getPaddedQuestionNumber(parentQstn.getId()) + "." + padQn;
    }
    return padQn;
  }

  /**
   * Gets the q num.
   *
   * @param questionId the question id
   * @return Question Number of this question without the parent's number
   */
  protected long getQNum(final long questionId) {
    return getQuestion(questionId) != null ? getQuestion(questionId).getQNumber() : 0l;
  }

  /**
   * Get the question responses at the immediate child level.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return sorted set of children as RvwQnaireAnswer
   */
  public SortedSet<RvwQnaireAnswer> getChildQuestions(final RvwQnaireAnswer rvwQnaireAns) {
    SortedSet<RvwQnaireAnswer> allChildrenSet = new TreeSet<>();
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      Map<Long, List<Long>> childQuestions = this.qnaireDefModel.getChildQuestionIdMap();
      for (Long childQuesId : childQuestions.get(rvwQnaireAns.getQuestionId())) {
        // Get child questions
        if (getAllQuestionMap().containsKey(childQuesId)) {
          RvwQnaireAnswer childRvwQuesAns = getAllQuestionMap().get(childQuesId);
          allChildrenSet.add(childRvwQuesAns);
        }
        for (Long innerChildQuesId : childQuestions.get(childQuesId)) {
          if (getAllQuestionMap().containsKey(innerChildQuesId)) {
            allChildrenSet.add(getAllQuestionMap().get(innerChildQuesId));
          }
        }
      }
    }

    return allChildrenSet;
  }

  /**
   * Gets the questionnaire.
   *
   * @return the questionnaire
   */
  public Questionnaire getQuestionnaire() {
    return this.qnaireDefModel.getQuestionnaire();
  }

  /**
   * Get the ID configured for general Questionnaire
   *
   * @return long
   */
  public long getGeneralQnaireId() {
    long genQnaireId = 0l;
    String genQnairePropVal = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.GENERAL_QNAIRE_ID);

    if (CommonUtils.isNotEmptyString(genQnairePropVal)) {
      genQnaireId = Long.valueOf(genQnairePropVal);
    }
    else {
      CDMLogger.getInstance().warn("General Questionnaire defined in system is not VALID! ");
    }
    return genQnaireId;
  }

  /**
   * Checks whether the questionnaire is 'General' type.
   *
   * @return true, if this is a general questionnaire's version
   */
  public boolean isGeneralType() {
    return CommonUtils.isEqual(getQuestionnaire().getId(), getGeneralQnaireId());
  }


  /**
   * Gets the question number.
   *
   * @param questionId the question id
   * @return the question number
   */
  public String getQuestionNumber(final Long questionId) {
    String qNo = String.valueOf(this.qnaireDefModel.getQuestionMap().get(questionId).getQNumber());
    Question parent =
        this.qnaireDefModel.getQuestionMap().get(this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId());
    if (null == parent) {
      return qNo;
    }
    return getQuestionNumber(this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId()) + "." + qNo;
  }

  /**
   * Gets the question number.
   *
   * @param questionId the question id
   * @return question number of this question
   */
  public String getQuestionNumberWithPrefix(final long questionId) {
    String prefix = "";
    if (getQuestion(questionId) != null) {
      prefix = isGeneralType() ? "G" : "";
      return prefix + getQuestionNumber(questionId);
    }
    return prefix;
  }

  /**
   * Checks if is heading.
   *
   * @param questionId the question id
   * @return true, if is heading
   */
  public boolean isHeading(final Long questionId) {
    if ((this.qnaireDefModel.getQuestionMap() != null) &&
        this.qnaireDefModel.getQuestionMap().containsKey(questionId)) {
      return this.qnaireDefModel.getQuestionMap().get(questionId).getHeadingFlag();
    }
    return false;
  }

  /**
   * Checks if is heading.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return true, if is heading
   */
  public boolean checkHeading(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      long questionId = rvwQnaireAns.getQuestionId();
      return isHeading(questionId);
    }
    return true;
  }

  /**
   * Gets the name.
   *
   * @param questionId the question id
   * @return the name
   */
  public String getName(final long questionId) {
    if (getQuestion(questionId) != null) {
      return getQuestion(questionId).getName();
    }
    return getDisplayVersionName();
  }


  /**
   * Gets the display version name.
   *
   * @return the display version name
   */
  public String getDisplayVersionName() {
    return getQuestionnaire().getName() + " (" + getVersionName(this.qnaireDefModel.getQuestionnaireVersion()) + ")";
  }


  /**
   * @param version QuestionnaireVersion
   * @return Version name
   */
  public String getVersionName(final QuestionnaireVersion version) {
    Long majorVersionNum = version.getMajorVersionNum();
    Long minorVersionNum = version.getMinorVersionNum();

    if (CommonUtils.isEqual(majorVersionNum, 0L) &&
        (CommonUtils.isEqual(minorVersionNum, 0L) || CommonUtils.isEqual(minorVersionNum, null))) {
      // if major version num and minor version num is 0
      return "Working Set";
    }
    StringBuilder versionName = new StringBuilder("Version ");
    versionName.append(majorVersionNum);
    if (minorVersionNum != null) {
      versionName.append("." + minorVersionNum);
    }
    else {
      versionName.append("." + 0);
    }
    return versionName.toString();
  }

  /**
   * Gets the description.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return the description
   */
  public String getDescription(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    // If this is dummy answer (i.e. heading)
    if (isHeading(questionId)) {
      return "";
    }
    if (getQuestion(questionId) != null) {
      return getQuestion(questionId).getDescription();
    }
    return getQnaireVersion().getDescription();
  }

  /**
   * @param questionId
   * @return
   */
  private boolean isQnaireHeading(final long questionId) {
    return (questionId == getQnaireVersion().getId());
  }

  /**
   * @return
   */
  private QuestionnaireVersion getQnaireVersion() {
    return this.qnaireDefModel.getQuestionnaireVersion();
  }

  /**
   * Gets the checks if is measurement UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return if measurement display string
   */
  public String getMeasurementUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showMeasurement(questionId)) {
      return QUESTION_RESP_SERIES_MEASURE.getType(getMeasurement(rvwQnaireAns)).getUiType();
    }
    return NOT_APPL;
  }

  /**
   * Gets the measurement.
   *
   * @param questionId the question id
   * @return the measurement
   */
  public QUESTION_CONFIG_TYPE getMeasurement(final Long questionId) {
    if (!isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().get(questionId) != null)) {
      return QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getMeasurement());
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * Gets the measurement.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public String getMeasurement(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId)) {
      return NOT_APPL;
    }
    return rvwQnaireAns.getMeasurement();
  }

  /**
   * Show measurement.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showMeasurement(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getMeasurementRelevantFlag()) &&
        ((getMeasurement(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getMeasurement(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the series.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Series
   */
  public String getSeries(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId)) {
      return "";
    }
    return CommonUtils.isNotNull(rvwQnaireAns.getSeries()) ? rvwQnaireAns.getSeries() : "";
  }

  /**
   * Show series maturity.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showSeriesMaturity(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getSeriesRelevantFlag()) &&
        ((getSeries(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getSeries(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the series.
   *
   * @param questionId the question id
   * @return the series
   */
  public QUESTION_CONFIG_TYPE getSeries(final Long questionId) {
    if (!isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().get(questionId) != null)) {
      return QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getSeries());
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * Gets the checks if is series UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return is series display string
   */
  public String getSeriesUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showSeriesMaturity(rvwQnaireAns.getQuestionId())) {
      return QUESTION_RESP_SERIES_MEASURE.getType(getSeries(rvwQnaireAns)).getUiType();
    }
    return NOT_APPL;
  }

  /**
   * Gets the link.
   *
   * @param questionId the question id
   * @return the link
   */
  public QUESTION_CONFIG_TYPE getLink(final Long questionId) {
    return !isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().containsKey(questionId))
        ? QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getLink())
        : QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * Show links.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showLinks(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getLinkRelevantFlag()) &&
        ((getLink(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getLink(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the links.
   *
   * @param answer rvw qnaire ans object
   * @return the links
   */
  public Set<Link> getLinks(final RvwQnaireAnswer answer) {
    Map<Long, Link> links =
        this.qnaireRespModel.getLinksMap().computeIfAbsent(answer.getId(), k -> getLinksFromServer(answer));

    return null == links ? new HashSet<>() : links.values().stream().collect(Collectors.toCollection(HashSet::new));
  }

  /**
   * Get links from server via service
   *
   * @param answer RvwQnaireAnswer
   * @return map of links with key as link ID
   */
  private Map<Long, Link> getLinksFromServer(final RvwQnaireAnswer answer) {
    Map<Long, Link> links = null;
    try {
      links = new LinkLoader(getServiceData()).getLinksByNode(answer.getId(), MODEL_TYPE.RVW_QNAIRE_ANS.getTypeCode());
    }
    catch (DataException e) {
      CDMLogger.getInstance().warn(e.getMessage(), e);
    }

    return links;
  }


  /**
   * Gets the link UI string
   *
   * @param answer the rvw qnaire ans
   * @return links display string
   */
  public String getLinkUIString(final RvwQnaireAnswer answer) {
    long questionId = answer.getQuestionId();

    String uiLinkStr;

    // Empty if heading
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      uiLinkStr = "";
    }
    else if (showLinks(questionId)) {
      // Sorted URLs, one per line
      uiLinkStr =
          getLinks(answer).stream().map(Link::getLinkUrl).sorted().collect(Collectors.joining(DELIMITER_STRING));
    }
    else {
      uiLinkStr = NOT_APPL;
    }

    return uiLinkStr;
  }


  /**
   * Generates the open point data.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return open points display string
   */
  public String getOpenPointsUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    // If this is dummy answer (i.e. heading)
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showOpenPoints(questionId)) {
      StringBuilder opPoints = new StringBuilder();
      if (!getOpenPointsList(rvwQnaireAns).isEmpty()) {
        generateOPString(rvwQnaireAns, opPoints);
      }
      return opPoints.toString();
    }
    return NOT_APPL;
  }


  /**
   * Show open points.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showOpenPoints(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getOpenPointsRelevantFlag()) &&
        ((getOpenPoints(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getOpenPoints(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the open points.
   *
   * @param questionId the question id
   * @return the open points
   */
  public QUESTION_CONFIG_TYPE getOpenPoints(final Long questionId) {
    if (!isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().containsKey(questionId))) {
      return QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getOpenPoints());
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * Get the open points against this question response.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return the openPointsList
   */
  public Map<Long, RvwQnaireAnswerOpl> getOpenPointsList(final RvwQnaireAnswer rvwQnaireAns) {
    Map<Long, RvwQnaireAnswerOpl> openPointsList = new HashMap<>();
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      long questionId = rvwQnaireAns.getQuestionId();
      if (isHeading(questionId)) {
        return openPointsList;
      }
      return this.qnaireRespModel.getOpenPointsMap().containsKey(rvwQnaireAns.getId())
          ? this.qnaireRespModel.getOpenPointsMap().get(rvwQnaireAns.getId()) : openPointsList;
    }
    return openPointsList;
  }

  private String getMessageValueForKey(final String grpName, final String key) {
    return new MessageLoader(getServiceData()).getMessage(grpName, key);
  }

  /**
   * String representation of all open points of this question response.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @param opPoints the op points
   * @return the int
   * @throws ApicWebServiceException the apic web service exception
   */
  private int generateOPString(final RvwQnaireAnswer rvwQnaireAns, final StringBuilder opPoints) {
    int count = 1;
    for (RvwQnaireAnswerOpl openPoint : this.qnaireRespModel.getOpenPointsMap().get(rvwQnaireAns.getId()).values()) {
      Long questionId = rvwQnaireAns.getQuestionId();
      if (count > 1) {
        opPoints.append(DELIMITER_STRING);
      }
      getOpenPoints(rvwQnaireAns, openPoint, opPoints, questionId);
      count++;
    }
    return count;
  }

  private void getOpenPoints(final RvwQnaireAnswer rvwQnaireAns, final RvwQnaireAnswerOpl openPoint,
      final StringBuilder opPoints, final Long questionId) {
    String openPoints =
        getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS);

    String oplMeasures = getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE);

    String oplResponsible =
        getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_RESPONSIBLE);
    String oplDate = getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE);

    String oplStatus = getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_STATUS);

    opPoints.append(openPoints).append(" : ").append(getOpenPointsUIString(rvwQnaireAns, openPoint))
        .append(DELIMITER_STRING);

    // Measures(optional)
    if (showMeasures(questionId)) {
      opPoints.append(oplMeasures).append(" : ").append(openPoint.getMeasure()).append(DELIMITER_STRING);
    }

    // Responsible (optional)
    if (showResponsible(questionId)) {
      opPoints.append(oplResponsible).append(" : ").append(getOplResponsible(openPoint, questionId))
          .append(DELIMITER_STRING);
    }

    // Completion date(optional)
    if (showCompletionDate(questionId)) {
      opPoints.append(oplDate).append(" : ").append(getOplCompletionDate(openPoint, questionId))
          .append(DELIMITER_STRING);
    }

    // Status
    opPoints.append(oplStatus).append(" : ").append(getOplResult(openPoint, questionId));
  }

  /**
   * Gets the open points UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @param openPoint the open point
   * @return open points display string
   */
  public String getOpenPointsUIString(final RvwQnaireAnswer rvwQnaireAns, final RvwQnaireAnswerOpl openPoint) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showOpenPoints(rvwQnaireAns.getQuestionId())) {
      return CommonUtils.isNotNull(openPoint) ? openPoint.getOpenPoints() : "";
    }
    return NOT_APPL;
  }

  /**
   * Show measures.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showMeasures(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getMeasureRelaventFlag()) &&
        ((getMeasure(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getMeasure(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the measure.
   *
   * @param questionId the question id
   * @return the measure
   */
  public QUESTION_CONFIG_TYPE getMeasure(final Long questionId) {
    if (!isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().containsKey(questionId))) {
      return QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getMeasure());
    }
    return QUESTION_CONFIG_TYPE.getType("");

  }

  /**
   * Show responsible.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showResponsible(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getResponsibleRelaventFlag()) &&
        ((getResponsible(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getResponsible(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the responsible.
   *
   * @param questionId the question id
   * @return the responsible
   */
  public QUESTION_CONFIG_TYPE getResponsible(final Long questionId) {
    if (!isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().containsKey(questionId))) {
      return QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getResponsible());
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }


  /**
   * Gets the opl responsible.
   *
   * @param openPoint the open point
   * @param questionId the question id
   * @return the opl responsible
   */
  private String getOplResponsible(final RvwQnaireAnswerOpl openPoint, final long questionId) {
    String responsible = NOT_APPL;
    if (isHeading(questionId)) {
      responsible = "";
    }
    if (showResponsible(questionId)) {
      responsible = (openPoint.getResponsible() == null) && (openPoint.getResponsibleName() == null) ? ""
          : openPoint.getResponsibleName();
    }
    return responsible;
  }

  /**
   * Show completion date.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showCompletionDate(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getCompletionDateRelaventFlag()) &&
        ((getCompletionDate(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getCompletionDate(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the completion date.
   *
   * @param questionId the question id
   * @return the completion date
   */
  public QUESTION_CONFIG_TYPE getCompletionDate(final Long questionId) {
    if (!isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().containsKey(questionId))) {
      return QUESTION_CONFIG_TYPE
          .getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getCompletionDate());
    }
    return QUESTION_CONFIG_TYPE.getType("");

  }

  /**
   * Gets the opl completion date.
   *
   * @param openPoint the open point
   * @param questionId the question id
   * @return the opl completion date
   */
  private String getOplCompletionDate(final RvwQnaireAnswerOpl openPoint, final long questionId) {
    String completionDate = NOT_APPL;
    if (isHeading(questionId)) {
      completionDate = "";
    }
    if (showCompletionDate(questionId)) {
      try {
        completionDate = openPoint.getCompletionDate() == null ? ""
            : ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, openPoint.getCompletionDate());
      }
      catch (ParseException e) {
        CDMLogger.getInstance().error(
            "Error in converting Open point completion date to format:" + DateFormat.DATE_FORMAT_09, e,
            Activator.PLUGIN_ID);
        completionDate = openPoint.getCompletionDate();
      }
    }
    return completionDate;
  }

  /**
   * Gets the opl result.
   *
   * @param openPoint the open point
   * @param questionId the question id
   * @return the opl result
   */
  private String getOplResult(final RvwQnaireAnswerOpl openPoint, final long questionId) {
    String result = CommonUtils.getBooleanType(openPoint.getResult()) ? CommonUtilConstants.DISPLAY_YES
        : CommonUtilConstants.DISPLAY_NO;
    if (isHeading(questionId)) {
      result = "";
    }
    return result;
  }

  /**
   * Gets the remarks UI string.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Remarks display string
   */
  public String getRemarksUIString(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      return "";
    }
    if (showRemarks(rvwQnaireAns.getQuestionId())) {
      return getRemark(rvwQnaireAns);
    }
    return NOT_APPL;
  }

  /**
   * Show remarks.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showRemarks(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getRemarkRelevantFlag()) &&
        ((getRemark(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getRemark(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));

  }

  /**
   * Gets the remark.
   *
   * @param questionId the question id
   * @return the remark
   */
  public QUESTION_CONFIG_TYPE getRemark(final Long questionId) {
    return !isHeading(questionId) && (this.qnaireDefModel.getQuestionConfigMap().containsKey(questionId))
        ? QUESTION_CONFIG_TYPE.getType(this.qnaireDefModel.getQuestionConfigMap().get(questionId).getRemark())
        : QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * Gets the remark.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Remarks answer
   */
  public String getRemark(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (isHeading(questionId)) {
      return "";
    }

    return CommonUtils.isNotNull(rvwQnaireAns.getRemark()) ? rvwQnaireAns.getRemark() : "";
  }

  /**
   * @param questionId as input
   * @param questResultOptId as input
   * @return resultOptionString
   */
  public String getQuestionResultOptionUIString(final Long questionId, final Long questResultOptId) {
    String resultOptionString = ApicConstants.EMPTY_STRING;
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      return resultOptionString;
    }
    if (null == questResultOptId) {
      return resultOptionString;
    }
    if (showResult(questionId)) {
      Map<Long, QuestionResultOption> questionResultOptionsMap = getQuestionResultOptionsMap(questionId);

      QuestionResultOption questionResultOption = questionResultOptionsMap.get(questResultOptId);
      if (null != questionResultOption) {
        resultOptionString = questionResultOption.getQResultName();
      }
    }
    return resultOptionString;
  }

  /**
   * Show result.
   *
   * @param questionId the question id
   * @return true if relevancy in questionnaire and mandatory or optional from question level
   */
  public boolean showResult(final long questionId) {
    return !isHeading(questionId) && CommonUtils.getBooleanType(getQnaireVersion().getResultRelevantFlag()) &&
        ((getResult(questionId) == QUESTION_CONFIG_TYPE.MANDATORY) ||
            (getResult(questionId) == QUESTION_CONFIG_TYPE.OPTIONAL));
  }

  /**
   * Gets the result.
   *
   * @param questionId the question id
   * @return the result
   */
  public QUESTION_CONFIG_TYPE getResult(final Long questionId) {
    QuestionConfig questionConfig = this.qnaireDefModel.getQuestionConfigMap().get(questionId);
    if ((questionConfig != null) && !isHeading(questionId)) {
      return QUESTION_CONFIG_TYPE.getType(questionConfig.getResult());
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }

  /**
   * @param questionId as input
   * @return Set<QuestionResultOption>
   */
  public Map<Long, QuestionResultOption> getQuestionResultOptionsMap(final Long questionId) {
    if (!hasQuestion(questionId)) {
      return new HashMap<>();
    }
    return this.qnaireDefModel.getQuesWithQuestionResultOptionsMap().get(questionId);
  }

  /**
   * @param answer as input
   * @return calculated answers status finished / not finished
   */
  public String getCalculatedResults(final RvwQnaireAnswer answer) {
    long questionId = answer.getQuestionId();

    String calculatedResultTxt;

    // Empty if heading
    if (isHeading(questionId) || isQnaireHeading(questionId)) {
      calculatedResultTxt = "";
    }
    else if (checkMandatoryItemsFilled(answer)) {

      // Sorted URLs, one per line
      calculatedResultTxt = answer.getSelQnaireResultAssement() == null ? ""
          : PREFIX_RESULT_COL_VAL + QS_ASSESMENT_TYPE.getTypeByDbCode(answer.getSelQnaireResultAssement()).getUiType() +
              QnaireResponseBO.SUFFIX_RESULT_COL_VAL;
      calculatedResultTxt = CDRConstants.FINISHED_MARKER + calculatedResultTxt;
    }
    else {
      calculatedResultTxt = CDRConstants.NOT_FINISHED_MARKER;
    }
    return calculatedResultTxt;
  }


  /**
   * to check whether all the mandaroty item for the answers is filled
   *
   * @param answer as input
   * @return true/false
   */
  public boolean checkMandatoryItemsFilled(final RvwQnaireAnswer answer) {
    boolean seriesNotValid = isSeriesMandatory(answer) && CommonUtils.isEmptyString(answer.getSeries());
    boolean measurementNotValid = isMeasurementMandatory(answer) && CommonUtils.isEmptyString(answer.getMeasurement());
    boolean linkNotValid = isLinkMandatory(answer) && CommonUtils.isNullOrEmpty(getLinks(answer));
    boolean remarksNotValid = isRemarksMandatory(answer) && CommonUtils.isEmptyString(answer.getRemark());
    boolean resultNotValid =
        isResultMandatory(answer) && CommonUtils.isEmptyString(answer.getSelQnaireResultAssement());
    boolean allItemsFilled = seriesNotValid || measurementNotValid || linkNotValid || remarksNotValid || resultNotValid;

    return !allItemsFilled;
  }

  /**
   * Checks if is result mandatory.
   *
   * @param rvwQnaireAns the rvw qnaire ans
   * @return true, if is result mandatory
   */
  public boolean isResultMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getResultRelevantFlag()) &&
          (getResult(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is link mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is link mandatory
   */
  public boolean isLinkMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getLinkRelevantFlag()) &&
          (getLink(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is measurement mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is measurement mandatory
   */
  public boolean isMeasurementMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getMeasurementRelevantFlag()) &&
          (getMeasurement(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }


  /**
   * Checks if is remarks mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is remarks mandatory
   */
  public boolean isRemarksMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getRemarkRelevantFlag()) &&
          (getRemark(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is series mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is series mandatory
   */
  public boolean isSeriesMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getSeriesRelevantFlag()) &&
          (getSeries(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is measures mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is measures mandatory
   */
  public boolean isMeasuresMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getMeasureRelaventFlag()) &&
          (getMeasure(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }


  /**
   * Checks if is responsible mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is responsible mandatory
   */
  public boolean isResponsibleMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getResponsibleRelaventFlag()) &&
          (getResponsible(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }

  /**
   * Checks if is date mandatory.
   *
   * @param rvwQnaireAns the rvw ans obj
   * @return true, if is date mandatory
   */
  public boolean isDateMandatory(final RvwQnaireAnswer rvwQnaireAns) {
    if (getQuestion(rvwQnaireAns.getQuestionId()) != null) {
      return CommonUtils.getBooleanType(getQnaireVersion().getCompletionDateRelaventFlag()) &&
          (getCompletionDate(rvwQnaireAns.getQuestionId()) == QUESTION_CONFIG_TYPE.MANDATORY);
    }
    return false;
  }


}
