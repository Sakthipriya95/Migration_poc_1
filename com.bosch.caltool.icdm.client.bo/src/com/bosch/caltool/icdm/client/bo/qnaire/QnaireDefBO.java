/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.qnaire;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QUESTION_CONFIG_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestDepenValCombination;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QnaireVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class QnaireEditorDataHandler.
 *
 * @author nip4cob
 */
public class QnaireDefBO {

  /** The qnaire def model. */
  private QnaireVersionModel qnaireDefModel;

  /** The all qn attr val dep model. */
  private QuestAttrAndValDepModel allQnAttrValDepModel;

  /**
   * Instantiates a new qnaire editor data handler.
   *
   * @param questionnaireVersion the questionnaire version
   */
  public QnaireDefBO(final QuestionnaireVersion questionnaireVersion) {
    initializeData(questionnaireVersion.getId());
  }

  /**
   * Instantiates a new qnaire editor data handler.
   *
   * @param qnaireVersId the questionnaire version
   */
  public QnaireDefBO(final long qnaireVersId) {
    initializeData(qnaireVersId);
  }

  /**
   * Instantiates a new qnaire editor data handler.
   *
   * @param qnaireVersId the questionnaire version
   */
  public QnaireDefBO(final long qnaireVersId, final boolean loadAttrDepnModel) {
    initializeData(qnaireVersId);
    if (loadAttrDepnModel) {
      getAllQnDepnAttrValModel(qnaireVersId);
    }
  }
  
  /**
   * Instantiates a new qnaire editor data handler for combined excel export 
   * @param qnaireVersionModel as QnaireVersionModel
   * @param attrAndValDepModel as QuestAttrAndValDepModel
   */
  public QnaireDefBO(QnaireVersionModel qnaireVersionModel ,QuestAttrAndValDepModel attrAndValDepModel) {
    this.qnaireDefModel = qnaireVersionModel;
    this.allQnAttrValDepModel = attrAndValDepModel;
  }

  /**
   * Gets the all qn depn attr val model.
   *
   * @param qnaireVersId the qnaire vers id
   */
  private void getAllQnDepnAttrValModel(final long qnaireVersId) {
    try {
      this.allQnAttrValDepModel = new QuestionServiceClient().getAllQnDepnAttrValModelByVersion(qnaireVersId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Initialize data.
   *
   * @param qnaireVersId the qnaire vers id
   */
  private void initializeData(final long qnaireVersId) {
    try {
      this.qnaireDefModel = new QnaireVersionServiceClient().getQnaireVersionWithDetails(qnaireVersId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * General qnaireversion details.
   *
   * @return the general qnaire version
   */
  public QuestionnaireVersion getGeneralQnaireVersion() {
    try {
      return new QuestionnaireServiceClient().getWorkingSet(getGeneralQnaireId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
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
   * Gets the measurement UI string.
   *
   * @param questionId the question id
   * @return the measurement UI string
   */
  public String getMeasurementUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showMeasurement()) {
      return CommonUtils.checkNull(getMeasurement(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
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
   * Show measurement.
   *
   * @return true, if successful
   */
  public boolean showMeasurement() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getMeasurementRelevantFlag());
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
   * Gets the link UI string.
   *
   * @param questionId the question id
   * @return link display string
   */
  public String getLinkUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showLink()) {
      return CommonUtils.checkNull(getLink(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Gets the series UI string.
   *
   * @param questionId the question id
   * @return the series UI string
   */
  public String getSeriesUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showSeries()) {
      return CommonUtils.checkNull(getSeries(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show series.
   *
   * @return true, if successful
   */
  public boolean showSeries() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getSeriesRelevantFlag());
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
   * Show link.
   *
   * @return true, if successful
   */
  public boolean showLink() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getLinkRelevantFlag());
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
   * Gets the remark UI string.
   *
   * @param questionId the question id
   * @return remark display string
   */
  public String getRemarkUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showRemark()) {
      return CommonUtils.checkNull(getRemark(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show remark.
   *
   * @return true, if successful
   */
  public boolean showRemark() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getRemarkRelevantFlag());
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
   * Gets the open points UI string.
   *
   * @param questionId the question id
   * @return open points display string
   */
  public String getOpenPointsUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showOpenPoints()) {
      return CommonUtils.checkNull(getOpenPoints(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show open points.
   *
   * @return true, if successful
   */
  public boolean showOpenPoints() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getOpenPointsRelevantFlag());
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
   * Gets the measure UI string.
   *
   * @param questionId the question id
   * @return measure display string
   */
  public String getMeasureUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showMeasure()) {
      return CommonUtils.checkNull(getMeasure(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show measure.
   *
   * @return true, if successful
   */
  public boolean showMeasure() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getMeasureRelaventFlag());
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
   * Gets the responsible UI string.
   *
   * @param questionId the question id
   * @return responsible display string
   */
  public String getResponsibleUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showResponsible()) {
      return CommonUtils.checkNull(getResponsible(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show responsible.
   *
   * @return true, if successful
   */
  public boolean showResponsible() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getResponsibleRelaventFlag());
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
   * Gets the completion date UI string.
   *
   * @param questionId the question id
   * @return CompletionDate display string
   */
  public String getCompletionDateUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showCompletionDate()) {
      return CommonUtils.checkNull(getCompletionDate(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show completion date.
   *
   * @return true, if successful
   */
  public boolean showCompletionDate() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getCompletionDateRelaventFlag());
  }

  /**
   * Gets the result.
   *
   * @param questionId the question id
   * @return the result
   */
  public QUESTION_CONFIG_TYPE getResult(final Long questionId) {
    QuestionConfig questionConfig = this.qnaireDefModel.getQuestionConfigMap().get(questionId);
    if (questionConfig!=null && !isHeading(questionId)) {
        return QUESTION_CONFIG_TYPE.getType(questionConfig.getResult());
    }
    return QUESTION_CONFIG_TYPE.getType("");
  }


  /**
   * Gets the qnaire def model.
   *
   * @return the qnaireDefModel
   */
  public QnaireVersionModel getQnaireDefModel() {
    return this.qnaireDefModel;
  }


  /**
   * Sets the qnaire def model.
   *
   * @param qnaireDefModel the qnaireDefModel to set
   */
  public void setQnaireDefModel(final QnaireVersionModel qnaireDefModel) {
    this.qnaireDefModel = qnaireDefModel;
  }


  /**
   * Gets the result UI string.
   *
   * @param questionId the question id
   * @return Result display string
   */
  public String getResultUIString(final Long questionId) {
    if (isHeading(questionId)) {
      return null;
    }
    if (showResult()) {
      return CommonUtils.checkNull(getResult(questionId).getUiType());
    }
    return CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType();
  }

  /**
   * Show result.
   *
   * @return true, if successful
   */
  public boolean showResult() {
    return CommonUtils.getBooleanType(this.qnaireDefModel.getQuestionnaireVersion().getResultRelevantFlag());
  }


  /**
   * The Enum SortColumns.
   */
  public enum SortColumns {

                           /** Ques Name. */
                           SORT_QUES_NAME,

                           /** Ques Number. */
                           SORT_QUES_NUMBER,

                           /** Ques Hint. */
                           SORT_QUES_HINT,

                           /** Measurable. */
                           SORT_QUES_MEASURABLE,

                           /** Series. */
                           SORT_SERIES,

                           /** Link. */
                           SORT_LINK,

                           /** Result. */
                           SORT_RESULT,

                           /** Remark. */
                           SORT_REMARK,

                           /** Open points. */
                           SORT_OP,

                           /** Measures. */
                           SORT_MEASURES,

                           /** Responsible. */
                           SORT_RESPONSIBLE,

                           /** Date. */
                           SORT_DATE;
  }


  /**
   * Compare to.
   *
   * @param questionId1 the question id 1
   * @param qestionId2 the qestion id 2
   * @param sortColumn the sort column
   * @return the int
   */
  public int compareTo(final Long questionId1, final Long qestionId2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {

      case SORT_QUES_NAME:
        // comparing the question names
        compareResult = ApicUtil.compare(this.qnaireDefModel.getQuestionMap().get(questionId1).getName(),
            this.qnaireDefModel.getQuestionMap().get(qestionId2).getName());
        break;
      case SORT_QUES_NUMBER:
        // comparing the ques number
        compareResult = ApicUtil.compare(getPaddedQuestionNumber(questionId1), getPaddedQuestionNumber(qestionId2));
        break;
      case SORT_QUES_HINT:
        // comparing the question hint
        compareResult = ApicUtil.compare(this.qnaireDefModel.getQuestionMap().get(questionId1).getDescription(),
            this.qnaireDefModel.getQuestionMap().get(qestionId2).getDescription());
        break;
      case SORT_QUES_MEASURABLE:
        compareResult = compareMeasurable(questionId1, qestionId2);
        break;
      case SORT_SERIES:
        compareResult = compareSeries(questionId1, qestionId2);
        break;
      case SORT_RESULT:
        compareResult = compareResult(questionId1, qestionId2);
        break;
      case SORT_LINK:
        compareResult = compareLink(questionId1, qestionId2);
        break;
      case SORT_REMARK:
        compareResult = compareRemark(questionId1, qestionId2);
        break;
      case SORT_OP:
        compareResult = compareOpenPoints(questionId1, qestionId2);
        break;
      case SORT_MEASURES:
        compareResult = compareMeasures(questionId1, qestionId2);

        break;
      case SORT_RESPONSIBLE:
        compareResult = compareResponsible(questionId1, qestionId2);

        break;
      case SORT_DATE:
        compareResult = compareDate(questionId1, qestionId2);

        break;
      default:
        // Compare q number
        compareResult = compareTo(questionId1, qestionId2);
        break;

    }
    return compareResult;
  }


  /**
   * Gets the padded question number.
   *
   * @param questionId the question id
   * @return the padded question number
   */
  public String getPaddedQuestionNumber(final Long questionId) {
    String padQn = String.format("%09d", this.qnaireDefModel.getQuestionMap().get(questionId).getQNumber());

    Question parent =
        this.qnaireDefModel.getQuestionMap().get(this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId());
    if (null == parent) {
      return padQn;
    }

    return getPaddedQuestionNumber(parent.getId()) + "." + padQn;
  }


  /**
   * @param questionId
   * @return
   */
  private int compareTo(final Long questionId1, final Long questionId2) {
    int compResult = 0;
    if (null != questionId2) {
      compResult = ApicUtil.compare(getPaddedQuestionNumber(questionId1), getPaddedQuestionNumber(questionId2));

      if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        return ApicUtil.compare(questionId1, questionId2);
      }
    }
    return compResult;
  }


  /**
   * Compare date.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareDate(final Long questionId1, final Long questionId2) {
    // compare completion date
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getCompletionDate(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getCompletionDate());
  }


  /**
   * Compare responsible.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareResponsible(final Long questionId1, final Long questionId2) {
    // compare responsible
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getResponsible(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getResponsible());
  }


  /**
   * Compare measures.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareMeasures(final Long questionId1, final Long questionId2) {
    // compare the measures
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getMeasure(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getMeasure());
  }


  /**
   * Compare open points.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareOpenPoints(final Long questionId1, final Long questionId2) {
    // compare the open points
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getOpenPoints(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getOpenPoints());
  }


  /**
   * Compare remark.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareRemark(final Long questionId1, final Long questionId2) {
    // compare the remark
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getRemark(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getRemark());
  }


  /**
   * Compare link.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareLink(final Long questionId1, final Long questionId2) {
    // comparing the link
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getLink(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getLink());
  }


  /**
   * Compare result.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareResult(final Long questionId1, final Long questionId2) {
    // comparing the result
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getResult(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getResult());
  }


  /**
   * Compare series.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareSeries(final Long questionId1, final Long questionId2) {
    // comparing the series
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getSeries(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getSeries());
  }


  /**
   * Compare measurable.
   *
   * @param questionId1 the question id 1
   * @param questionId2 the question id 2
   * @return the int
   */
  private int compareMeasurable(final Long questionId1, final Long questionId2) {
    return ApicUtil.compare(
        this.qnaireDefModel.getQuestionConfigMap().get(questionId1) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId1).getMeasurement(),
        this.qnaireDefModel.getQuestionConfigMap().get(questionId2) == null ? ApicConstants.EMPTY_STRING
            : this.qnaireDefModel.getQuestionConfigMap().get(questionId2).getMeasurement());
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
   * Checks if is working set.
   *
   * @return true, if is working set
   */
  public boolean isWorkingSet() {
    Long majorVersionNum = this.qnaireDefModel.getQuestionnaireVersion().getMajorVersionNum();
    Long minorVersionNum = this.qnaireDefModel.getQuestionnaireVersion().getMinorVersionNum();
    // if major version num and minor version num is 0 return true
    return CommonUtils.isEqual(majorVersionNum, 0L) && (CommonUtils.isEqual(minorVersionNum, null));
  }

  /**
   * Checks if is active version.
   *
   * @return true if the questionnaire version is active
   */
  public boolean isActiveVersion() {

    boolean isActiveVersion = false;
    if (this.qnaireDefModel.getQuestionnaireVersion().getActiveFlag() != null) {

      isActiveVersion = this.qnaireDefModel.getQuestionnaireVersion().getActiveFlag().equals(ApicConstants.CODE_YES);
    }
    return isActiveVersion;
  }

  /**
   * @return
   */
  public String getNameForPart() {
    return getQnaireDefModel().getQuestionnaire().getName() + " (" + getVersionName() + ")";
  }

  /**
   * Gets the version name.
   *
   * @return the version name
   */
  public String getVersionName() {
    return getVersionName(this.qnaireDefModel.getQuestionnaireVersion());
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
      versionName.append("." + 0);// TODO: Check Is 0 the only major version ending?
    }
    return versionName.toString();
  }

  /**
   * Gets the display version name.
   *
   * @return the display version name
   */
  public String getDisplayVersionName() {
    return getQuestionnaire().getName() + " (" + getVersionName() + ")";
  }

  /**
   * @return the English diaplay name
   */
  public String getDisplayVersionNameEnglish() {
    String qnaireName =
        getQuestionnaire().getNameEng() != null ? getQuestionnaire().getNameEng() : getQuestionnaire().getNameGer();
    return qnaireName + " (" + getVersionName() + ")";
  }

  /**
   * @return the german display name
   */
  public String getDisplayVersionNameGerman() {
    String qnaireName =
        getQuestionnaire().getNameGer() != null ? getQuestionnaire().getNameGer() : getQuestionnaire().getNameEng();
    return qnaireName + " (" + getVersionName() + ")";
  }

  /**
   * Checks if is modifiable.
   *
   * @return true if the current user has write access for questionnaire
   */
  public boolean isModifiable() {
    NodeAccess currentUserAccess;
    try {
      currentUserAccess =
          new CurrentUserBO().getNodeAccessRight(this.qnaireDefModel.getQuestionnaireVersion().getQnaireId());
      if ((currentUserAccess != null) && currentUserAccess.isWrite()) {
        return true;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * Gets the question level.
   *
   * @param questionId the question id
   * @return the question level
   */
  public int getQuestionLevel(final Long questionId) {
    if (null == this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId()) {
      return ApicConstants.LEVEL_1;
    }
    else if ((null != this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId()) &&
        (null == this.qnaireDefModel.getQuestionMap().get(this.qnaireDefModel.getQuestionMap()
            .get(this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId()).getParentQId()))) {
      return ApicConstants.LEVEL_2;
    }
    else {
      return ApicConstants.LEVEL_3;
    }
  }

  /**
   * Gets the max levels allowed.
   *
   * @return the maxLevelsAllowed
   */
  public int getMaxLevelsAllowed() {
    return ApicConstants.MAX_LEVELS_ALLOWED;
  }

  /**
   * Gets the previous question.
   *
   * @param questionId the question id
   * @return the previous question
   */
  public Question getPreviousQuestion(final Long questionId) {
    Question previousQs = null;
    SortedSet<Question> firstLevelQs = new TreeSet<>();
    if (getQuestionLevel(questionId) == ApicConstants.LEVEL_1) {
      firstLevelQs = getFirstLevelQuestionsWithoutDeletedQuestions();
    }
    else if (getQuestionLevel(questionId) == ApicConstants.LEVEL_2) {
      List<Long> childIds = this.qnaireDefModel.getChildQuestionIdMap().get(this.qnaireDefModel.getQuestionMap()
          .get(this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId()).getId());
      for (Long childQId : childIds) {
        firstLevelQs.add(this.qnaireDefModel.getQuestionMap().get(childQId));
      }
    }
    if (null != firstLevelQs) {
      List<Question> qsList = new ArrayList<>(firstLevelQs);
      qsList.sort(getQuestionComparator());
      int qsIndex = qsList.indexOf(this.qnaireDefModel.getQuestionMap().get(questionId));
      if ((qsIndex - ApicConstants.ONE_CONST) >= 0) {
        previousQs = qsList.get(qsIndex - ApicConstants.ONE_CONST);
      }
    }
    return previousQs;
  }

  /**
   * Get param comparator.
   *
   * @return Comparator
   */
  public Comparator<Question> getQuestionComparator() {
    return (final Question o1, final Question o2) -> compareTo(o1.getId(), o2.getId());
  }

  /**
   * Gets the result options
   *
   * @param questionId the question id
   * @return set of Question Result Options
   */
  public Map<Long, QuestionResultOption> getQnaireResultOptions(final Long questionId) {
    return this.qnaireDefModel.getQuesWithQuestionResultOptionsMap().get(questionId);
  }


  /**
   * Gets the first level questions.
   *
   * @return the first level questions
   */
  public SortedSet<Question> getFirstLevelQuestions() {
    SortedSet<Question> firstLevelQuestions = new TreeSet<>();
    for (Question question : this.qnaireDefModel.getQuestionMap().values()) {
      if (question.getParentQId() == null) {
        firstLevelQuestions.add(question);
      }
    }
    return firstLevelQuestions;
  }

  /**
   * Gets the first level questions.
   *
   * @return the first level questions
   */
  public SortedSet<Question> getFirstLevelQuestionsWithoutDeletedQuestions() {
    SortedSet<Question> firstLevelQuestions = new TreeSet<>();
    for (Question question : this.qnaireDefModel.getQuestionMap().values()) {
      if ((question.getParentQId() == null) && !question.getDeletedFlag()) {
        firstLevelQuestions.add(question);
      }
    }
    return firstLevelQuestions;
  }

  /**
   * Gets the first level questions.
   *
   * @return the first level questions
   */
  public SortedSet<Question> getFirstLevelQuestionsWithHeading() {
    SortedSet<Question> firstLevelQuestions = new TreeSet<>();
    for (Question question : this.qnaireDefModel.getQuestionMap().values()) {
      if ((question.getParentQId() == null) && isHeading(question.getId()) && !question.getDeletedFlag()) {
        firstLevelQuestions.add(question);
      }
    }
    return firstLevelQuestions;
  }

  /**
   * Checks for third level.
   *
   * @param questionId the question id
   * @return true, if successful
   */
  public boolean hasThirdLevel(final Long questionId) {
    boolean isThirdLevel = false;
    if (this.qnaireDefModel.getQuestionMap()
        .get(this.qnaireDefModel.getQuestionMap().get(questionId).getParentQId()) == null) {
      for (Long childId : this.qnaireDefModel.getChildQuestionIdMap().get(questionId)) {
        if (!this.qnaireDefModel.getChildQuestionIdMap().get(childId).isEmpty()) {
          isThirdLevel = true;
          break;
        }
      }
    }
    return isThirdLevel;
  }

  /**
   * Gets the question number with name.
   *
   * @param questionId the question id
   * @return question number of this question
   */
  public String getQuestionNumberWithName(final Long questionId) {
    return getQuestionNumber(questionId) + "  " + this.qnaireDefModel.getQuestionMap().get(questionId).getName();
  }

  /**
   * Gets the question without cyclic.
   *
   * @param selectedQues Question
   * @return the question without cyclic
   */
  public SortedSet<Question> getQuestionWithoutCyclic(final Question selectedQues) {
    SortedSet<Question> retQues = new TreeSet<>();
    for (Question qsn : getAllQuestions(false, true)) {
      Long depQuesid = qsn.getDepQuesId();
      // check whether the qsn has dependency on selected question
      if ((depQuesid == null) || !hasCyclicDep(qsn, selectedQues)) {
        retQues.add(qsn);
      }
    }
    return retQues;
  }

  /**
   * Checks for cyclic dep.
   *
   * @param depQuesid the dep quesid
   * @return true, if successful
   */
  private boolean hasCyclicDep(final Question qsn, final Question selQues) {
    Question depQues = this.qnaireDefModel.getQuestionMap().get(qsn.getDepQuesId());
    while (depQues != null) {
      if (CommonUtils.isEqual(depQues, selQues)) {
        return true;
      }
      depQues = this.qnaireDefModel.getQuestionMap().get(depQues.getDepQuesId());
    }
    return false;
  }

  /**
   * Gets the all questions.
   *
   * @param includeHeadings the include headings
   * @param includeDeleted
   * @return the all questions
   */
  public SortedSet<Question> getAllQuestions(final boolean includeHeadings, final boolean includeDeleted) {
    SortedSet<Question> retSet = new TreeSet<>(this.qnaireDefModel.getQuestionMap().values());
    if (!includeHeadings) {
      for (Question qsn : this.qnaireDefModel.getQuestionMap().values()) {
        if (qsn.getHeadingFlag()) {
          retSet.remove(qsn);
        }
      }
    }
    if (!includeDeleted) {
      this.qnaireDefModel.getQuestionMap().values().forEach(question -> {
        if (question.getDeletedFlag()) {
          retSet.remove(question);
        }
      });
    }

    return retSet;
  }

  /**
   * @return the Heading questions
   */
  public SortedSet<Question> getAllHeadingQuestions() {
    SortedSet<Question> retSet = new TreeSet<>(this.qnaireDefModel.getQuestionMap().values());
    for (Question qsn : this.qnaireDefModel.getQuestionMap().values()) {
      if (!qsn.getHeadingFlag()) {
        retSet.remove(qsn);
      }
    }
    return retSet;
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
   * Gets the qnaire version.
   *
   * @return the qnaire version
   */
  public QuestionnaireVersion getQnaireVersion() {
    return this.qnaireDefModel.getQuestionnaireVersion();
  }

  /**
   * @return the name of active questionnaire version
   */
  public String getActiveQnaireVersionName() {
    for (QuestionnaireVersion qnaireVersion : getAllVersions()) {
      if (CommonUtils.getBooleanType(qnaireVersion.getActiveFlag())) {
        return getVersionName(qnaireVersion);
      }
    }
    return getVersionName();
  }


  /**
   * Gets the name.
   *
   * @param questionId the question id
   * @return the name
   */
  public String getName(final long questionId) {
    return this.qnaireDefModel.getQuestionMap().get(questionId).getName();
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
   * Checks whether the questionnaire is 'General' type.
   *
   * @return true, if this is a general questionnaire
   */
  // ICDM-2155
  public long getGeneralQnaireId() {
    // ICDM-2404
    long genQnaireId = 0l;
    String genQnairePropVal = null;
    try {
      genQnairePropVal = new CommonDataBO().getParameterValue(CommonParamKey.GENERAL_QNAIRE_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().warn("General Questionnaire not defined in system : " + e.getMessage(), e);
    }
    if (CommonUtils.isNotEmptyString(genQnairePropVal)) {
      genQnaireId = Long.valueOf(genQnairePropVal);
    }
    else {
      CDMLogger.getInstance().warn("General Questionnaire defined in system is not VALID! ");
    }
    return genQnaireId;
  }


  /**
   * Gets the child questions.
   *
   * @return the chil questions
   */
  public Map<Long, List<Long>> getChildQuestions() {
    return this.qnaireDefModel.getChildQuestionIdMap();
  }

  /**
   * Checks for child question.
   *
   * @param parentQnId the parent qn id
   * @return true, if successful
   */
  public boolean hasChildQuestion(final Long parentQnId) {
    return (this.qnaireDefModel.getChildQuestionIdMap() != null) &&
        this.qnaireDefModel.getChildQuestionIdMap().containsKey(parentQnId);
  }

  /**
   * Gets the attributes.
   *
   * @param questionId the question id
   * @return the attributes
   */
  public Set<Attribute> getAttributes(final Long questionId) {
    Set<Attribute> questionDependentAttributes = new HashSet<>();
    try {
      questionDependentAttributes = new QuestionServiceClient().getQuestionDependentAttributes(questionId);

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return questionDependentAttributes;
  }


  /**
   * @param questionId
   * @return
   */
  public Map<Long, QuestionDepenAttr> getQuestionDepenAttr(final Long questionId) {
    Map<Long, QuestionDepenAttr> questionDependentAttributes = new HashMap<>();
    try {
      questionDependentAttributes = new QuestionServiceClient().getQuestionDepenAttrMap(questionId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return questionDependentAttributes;
  }


  /**
   * @param questionId
   * @return
   */
  public QuestAttrAndValDepModel getQuestDependentAttrAndValModel(final Long questionId) {
    QuestAttrAndValDepModel ret = null;
    try {
      ret = new QuestionServiceClient().getQuestDependentAttrAndValModel(questionId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return ret;
  }

  /**
   * Gets the all versions.
   *
   * @return the all versions
   */
  public SortedSet<QuestionnaireVersion> getAllVersions() {
    SortedSet<QuestionnaireVersion> allVersions = new TreeSet<>();
    try {
      allVersions = new QuestionnaireServiceClient().getAllVersions(this.qnaireDefModel.getQuestionnaire().getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return allVersions;
  }

  /**
   * Gets the working set.
   *
   * @param allVersions the all versions
   * @return the working set
   */
  public QuestionnaireVersion getWorkingSet(final SortedSet<QuestionnaireVersion> allVersions) {
    for (QuestionnaireVersion version : allVersions) {
      if ((version.getMajorVersionNum() == 0L) && (version.getMinorVersionNum() == null)) {
        // if major version is zero and minor version is null
        return version;
      }
    }
    return null;
  }

  /**
   * @param comb
   * @return display string
   */
  public String getDisplayText(final QuestDepenValCombination comb) {
    StringBuilder displayStr = new StringBuilder(ApicConstants.STR_BUILDER_SIZE);
    for (Attribute attr : new TreeSet<>(comb.getAttrValMap().keySet())) {
      displayStr.append(attr.getName()).append("->").append(comb.getAttrValMap().get(attr).getName()).append(", ");
    }
    // trailing comma is removed
    if (displayStr.length() > 0) {
      displayStr.delete(displayStr.length() - ApicConstants.TRAIL_COMMA_SIZE, displayStr.length() - 1);
    }
    return displayStr.toString();
  }

  /**
   * Gets the latest version.
   *
   * @return the latest version
   */
  public QuestionnaireVersion getLatestVersion() {
    SortedSet<QuestionnaireVersion> allVersions = getAllVersions();
    QuestionnaireVersion latestVersion = getWorkingSet(allVersions);

    int numOfVers = allVersions.size();
    Long higheshMajor = 0L;
    Long highestMinor = 0L;
    // iterate and find out the highest major number
    for (QuestionnaireVersion qNaireVersion : allVersions) {
      if (ApicUtil.compareLong(qNaireVersion.getMajorVersionNum(), higheshMajor) > 0) {
        higheshMajor = qNaireVersion.getMajorVersionNum();
      }
    }
    // iterate and find out the highest major for the identified highest major number
    for (QuestionnaireVersion qNaireVersion : allVersions) {
      if (numOfVers == ApicConstants.VERSION_SIZE_1) {
        latestVersion = qNaireVersion;
        break;
      }
      if (CommonUtils.isNull(qNaireVersion.getMinorVersionNum())) {
        continue;
      }
      else if (CommonUtils.isEqual(qNaireVersion.getMajorVersionNum(), higheshMajor) &&
          (ApicUtil.compareLong(qNaireVersion.getMinorVersionNum(), highestMinor) >= 0)) {
        highestMinor = qNaireVersion.getMinorVersionNum();
        latestVersion = qNaireVersion;
      }
    }

    return latestVersion;
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
   * @param questionId
   * @return
   */
  public boolean isDeletedExt(final Long questionId) {
    Question question = this.qnaireDefModel.getQuestionMap().get(questionId);
    if (question.getDeletedFlag()) {
      return true;
    }
    if (question.getParentQId() != null) {
      return isDeletedExt(question.getParentQId());
    }
    return false;
  }


  /**
   * @author nip4cob
   */
  public enum SortColumnsForVersion {
                                     /**
                                      * Version name
                                      */
                                     SORT_VERSION_NAME,
                                     /**
                                      * description
                                      */
                                     SORT_DESC,
                                     /**
                                      * version status
                                      */
                                     SORT_VERSION_STATUS,
                                     /**
                                      * active
                                      */
                                     SORT_ACTIVE,
                                     /**
                                      * created date
                                      */
                                     SORT_CREATED_DATE,
                                     /**
                                      * created user
                                      */
                                     SORT_CREATED_USER
  }


  /**
   * @param quesVers1 QuestionnaireVersion
   * @param quesVers2 QuestionnaireVersion
   * @param sortColumn column to be compared
   * @return comparison result
   */
  public int compareTo(final QuestionnaireVersion quesVers1, final QuestionnaireVersion quesVers2,
      final SortColumnsForVersion sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_VERSION_NAME:
        compareResult = ApicUtil.compare(getVersionName(quesVers1), getVersionName(quesVers2));
        break;

      case SORT_DESC:
        compareResult = ApicUtil.compare(quesVers1.getDescription(), quesVers2.getDescription());
        break;

      case SORT_ACTIVE:
        compareResult = ApicUtil.compareBoolean(quesVers1.getActiveFlag().equals(ApicConstants.CODE_YES),
            quesVers2.getActiveFlag().equals(ApicConstants.CODE_YES));
        break;

      case SORT_VERSION_STATUS:
        compareResult = ApicUtil.compareBoolean(quesVers1.getInworkFlag().equals(ApicConstants.CODE_YES),
            quesVers2.getInworkFlag().equals(ApicConstants.CODE_YES));
        break;

      case SORT_CREATED_DATE:
        compareResult = ApicUtil.compare(quesVers1.getCreatedDate(), quesVers2.getCreatedDate());
        break;

      case SORT_CREATED_USER:
        compareResult = ApicUtil.compare(quesVers1.getCreatedUser(), quesVers2.getCreatedUser());
        break;

      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the version name
      compareResult = ApicUtil.compare(getVersionName(quesVers1), getVersionName(quesVers2));

    }
    return compareResult;
  }

  /**
   * @param selectedVersion
   * @return
   */
  public boolean isWorkingSet(final QuestionnaireVersion selectedVersion) {
    Long majorVersionNum = selectedVersion.getMajorVersionNum();
    Long minorVersionNum = selectedVersion.getMinorVersionNum();
    // if major version num and minor version num is 0 return true
    return CommonUtils.isEqual(majorVersionNum, 0L) && (CommonUtils.isEqual(minorVersionNum, null));
  }

  /**
   * @param comboList
   * @return
   */
  public Long getMaxCombNumber(final List<QuestDepenValCombination> comboList) {
    // in case of deletion , we do not reorder the combination numbers
    // hence getting the size of the map will not work for maximum combination number

    Long maxCombNo = 0L;
    if (comboList != null) {
      for (QuestDepenValCombination comb : comboList) {
        Long combNum = comb.getCombinationId();
        if (combNum > maxCombNo) {
          maxCombNo = combNum;
        }
      }
    }
    return maxCombNo;
  }


  /**
   * @return the allQnAttrValDepModel
   */
  public QuestAttrAndValDepModel getAllQnAttrValDepModel() {
    return this.allQnAttrValDepModel;
  }


  /**
   * @param allQnAttrValDepModel the allQnAttrValDepModel to set
   */
  public void setAllQnAttrValDepModel(final QuestAttrAndValDepModel allQnAttrValDepModel) {
    this.allQnAttrValDepModel = allQnAttrValDepModel;
  }

  /**
   * Gets the working package details.
   *
   * @param wpDivId the wp div id
   * @return the working package details
   */
  public WorkPackageDivision getWorkingPackageDetails(final Long wpDivId) {
    try {
      WorkPackageDivisionServiceClient client = new WorkPackageDivisionServiceClient();
      return client.get(wpDivId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error fetching Workpackage Division details!", e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Update question.
   *
   * @param updateModel the update model
   */
  public void updateQuestion(final QuestionUpdationData updateModel) {
    try {
      new QuestionServiceClient().update(updateModel);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param newData QuestionCreationData
   */
  public void createQuestion(final QuestionCreationData newData) {
    try {
      new QuestionServiceClient().create(newData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param verToCreate
   */
  public void createQnaireVersion(final QuestionnaireVersion verToCreate) {
    try {
      new QnaireVersionServiceClient().create(verToCreate);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param verToUpdate
   */
  public void updateQnaireVersion(final QuestionnaireVersion verToUpdate) {
    try {
      new QnaireVersionServiceClient().update(verToUpdate);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param chData
   * @return
   */
  boolean isQuestionChanged(final ChangeData<?> chData) {
    Question quest = (Question) CnsUtils.getModel(chData);
    return CommonUtils.isEqual(quest.getQnaireVersId(), getQnaireVersion().getId());
  }

  /**
   * @param chData
   * @return
   */
  boolean isQnaireVersionChanged(final ChangeData<?> chData) {
    QuestionnaireVersion questVersion = (QuestionnaireVersion) CnsUtils.getModel(chData);
    return CommonUtils.isEqual(questVersion.getQnaireId(), getQnaireVersion().getQnaireId());
  }
}

