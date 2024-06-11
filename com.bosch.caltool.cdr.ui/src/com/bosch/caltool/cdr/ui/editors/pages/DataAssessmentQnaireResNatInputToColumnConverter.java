/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * @author ajk2cob
 */
public class DataAssessmentQnaireResNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

  private static final String EMPTY_STRING = "";

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object returnObj = null;
    // same row as last, use its object as it's way faster than a list_get(row)
    // If object is instance of data assessment questionnaire result details value
    if (evaluateObj instanceof DataAssessmentQuestionnaires) {
      final DataAssessmentQuestionnaires qnaireResultDetails = (DataAssessmentQuestionnaires) evaluateObj;
      // Get the column index
      if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_WORKPACKAGE) {
        // workpackage name
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getA2lWpName())
            ? qnaireResultDetails.getA2lWpName() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_NAME) {
        // responsibility name
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getA2lRespName())
            ? qnaireResultDetails.getA2lRespName() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_RESP_TYEP) {
        // questionnaire name
        returnObj = CommonUtils.isNotEmptyString(WpRespType.getType(qnaireResultDetails.getA2lRespType()).getDispName())
            ? WpRespType.getType(qnaireResultDetails.getA2lRespType()).getDispName() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_QUES_NAME) {
        // questionnaire name
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getQnaireRespName())
            ? qnaireResultDetails.getQnaireRespName() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_ASSESSMENT_PROD_READY) {
        // is ready for production
        returnObj = CommonUtils.isNotNull(qnaireResultDetails.isQnaireReadyForProd())
            ? CommonUtils.getBooleanCode(qnaireResultDetails.isQnaireReadyForProd()) : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING) {
        // basline existing
        returnObj = CommonUtils.isNotNull(qnaireResultDetails.isQnaireBaselineExisting())
            ? CommonUtils.getBooleanCode(qnaireResultDetails.isQnaireBaselineExisting()) : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_POSITIVE) {
        // positive flag
        returnObj = CommonUtils.isNotNull(qnaireResultDetails.getQnairePositiveAnsCount())
            ? qnaireResultDetails.getQnairePositiveAnsCount() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEGATIVE) {
        // negative flag
        returnObj = CommonUtils.isNotNull(qnaireResultDetails.getQnaireNegativeAnsCount())
            ? qnaireResultDetails.getQnaireNegativeAnsCount() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_NEUTRAL) {
        // neutral flag
        returnObj = CommonUtils.isNotNull(qnaireResultDetails.getQnaireNeutralAnsCount())
            ? qnaireResultDetails.getQnaireNeutralAnsCount() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_VERSION_NAME) {
        // questionnaire version name
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getQnaireRespVersName())
            ? qnaireResultDetails.getQnaireRespVersName() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_BY) {
        // questionnaire reviewed by
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getQnaireRvdUserDisplayName())
            ? qnaireResultDetails.getQnaireRvdUserDisplayName() : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_REVIEWED_ON) {
        // questionnaire reviewed on
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getQnaireReviewedDate())
            ? CommonUtils.setDateFormat(qnaireResultDetails.getQnaireReviewedDate()) : EMPTY_STRING;
      }
      else if (colIndex == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_LINK) {
        // questionnaire link
        returnObj = CommonUtils.isNotEmptyString(qnaireResultDetails.getQnaireBaselineLinkDisplayText())
            ? qnaireResultDetails.getQnaireBaselineLinkDisplayText() : EMPTY_STRING;
      }
    }
    return returnObj;
  }

}
