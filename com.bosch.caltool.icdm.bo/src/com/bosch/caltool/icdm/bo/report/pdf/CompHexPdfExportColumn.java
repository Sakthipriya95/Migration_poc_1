/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;

/**
 * @author TRL1COB
 */
public enum CompHexPdfExportColumn {

                                    /**
                                     * Unique Instance for Single class implementation
                                     */
                                    INSTANCE;


  /**
   * @return the unique instance of this class
   */
  public static CompHexPdfExportColumn getInstance() {
    return INSTANCE;
  }

  /**
   * Columns for compare report
   */
  protected static final String[] compareHexResultsHeader = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.COMPLIANCE_NAME,
      DataAssessmentReportConstants.TYPE,
      DataAssessmentReportConstants.PARAMETER,
      DataAssessmentReportConstants.FUNCTION,
      DataAssessmentReportConstants.FC_VERSION,
      DataAssessmentReportConstants.WP,
      DataAssessmentReportConstants.RESP_TYPE,
      DataAssessmentReportConstants.WP_FINISHED,
      DataAssessmentReportConstants.RESP,
      DataAssessmentReportConstants.LATEST_A2L_VER,
      DataAssessmentReportConstants.LATEST_FUNC_VER,
      DataAssessmentReportConstants.QUESTIONNAIRE_STATUS,
      DataAssessmentReportConstants.IS_REVIEWED,
      DataAssessmentReportConstants.EQUAL,
      DataAssessmentReportConstants.HEX_VALUE,
      DataAssessmentReportConstants.REVIEWED_VALUE,
      DataAssessmentReportConstants.COMPLI_RESULT,
      DataAssessmentReportConstants.REVIEW_SCORE,
      DataAssessmentReportConstants.REVIEW_COMMENTS };


  /**
   * Columns for compare result info headers
   */
  protected static final String[] compareResultsInfoHeaders = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.COMPARE_RESULTS,
      DataAssessmentReportConstants.COUNT };


  /**
   * Columns for compare result info
   */
  protected static final String[] compareResultsInfo = new String[] {
      DataAssessmentReportConstants.HEX_FILE,
      DataAssessmentReportConstants.A2LFILE,
      DataAssessmentReportConstants.COMP_VARIANT,
      DataAssessmentReportConstants.TOTAL_PARAMS,
      DataAssessmentReportConstants.EQUAL_REVIEWED,
      DataAssessmentReportConstants.COMPLI_PARAMS,
      DataAssessmentReportConstants.COMPLI_PARAMS_PASSED,
      DataAssessmentReportConstants.NUM_PARAM_BSH_RESP,
      DataAssessmentReportConstants.NUM_PARAM_BSH_RESP_RVW,
      DataAssessmentReportConstants.PARAM_BSH_RESP_RVW,
      DataAssessmentReportConstants.PARAM_BSH_RESP_QNAIRE,
      DataAssessmentReportConstants.QNAIRE_NEGATIVE_ANSWER,
      DataAssessmentReportConstants.CONSIDERED_PREVIOUS_REVIEWS };

  /**
   * Columns for Questionnaire Status Legend
   */
  protected static final String[] compareHexQnaireResultLegend =
      new String[] { DataAssessmentReportConstants.QNAIRE_STATUS, DataAssessmentReportConstants.QNAIRE_STATUS_DETAILS };

  /**
   * Columns for Work Package Status Legend
   */
  protected static final String[] compareHexWrkPkgLegend =
      new String[] { DataAssessmentReportConstants.WRKPKG_STATUS, DataAssessmentReportConstants.WRKPKG_STATUS_DETAILS };


}
