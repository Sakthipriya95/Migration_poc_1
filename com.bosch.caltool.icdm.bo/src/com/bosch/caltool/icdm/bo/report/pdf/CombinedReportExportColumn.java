/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;

/**
 * @author MSP5COB
 */
public enum CombinedReportExportColumn {

                                        /**
                                         * Unique Instance for Single class implementation
                                         */
                                        INSTANCE;

  /**
   * @return the unique instance of this class
   */
  public static CombinedReportExportColumn getInstance() {
    return INSTANCE;
  }

  /**
   * Header columns
   */
  protected static final String[] resultSheetHeader = new String[] {
      DataAssessmentReportConstants.COMPLIANCE_NAME,
      DataAssessmentReportConstants.PARAMETER,
      DataAssessmentReportConstants.LONG_NAME,
      DataAssessmentReportConstants.WP_NAME,
      DataAssessmentReportConstants.RESPONSIBILITY_STR,
      DataAssessmentReportConstants.RESPONSIBILITY_TYPE,
      DataAssessmentReportConstants.TYPE,
      DataAssessmentReportConstants.CLASS,
      DataAssessmentReportConstants.IS_CODE_WORD,
      DataAssessmentReportConstants.IS_BITWISE,
      DataAssessmentReportConstants.PARAMETER_HINT,
      DataAssessmentReportConstants.FC_NAME,
      DataAssessmentReportConstants.LOWER_LIMIT,
      DataAssessmentReportConstants.UPPER_LIMIT,
      DataAssessmentReportConstants.BITWISE_LIMIT,
      DataAssessmentReportConstants.READY_FOR_SERIES,
      DataAssessmentReportConstants.REFERENCE_VALUE,
      DataAssessmentReportConstants.REFERENCE_VALUE_UNIT,
      DataAssessmentReportConstants.EXACT_MATCH,
      DataAssessmentReportConstants.CHECKED_VALUE,
      DataAssessmentReportConstants.CHECKED_VALUE_UNIT,
      DataAssessmentReportConstants.IMPORTED_VALUE,
      DataAssessmentReportConstants.RESULT,

      DataAssessmentReportConstants.SECONDARY_RESULT,
      DataAssessmentReportConstants.SCORE,
      DataAssessmentReportConstants.SCORE_DESCRIPTION,
      DataAssessmentReportConstants.COMMENT,

      DataAssessmentReportConstants.CDFX_STATUS,
      DataAssessmentReportConstants.CDFX_USER,
      DataAssessmentReportConstants.CDFX_DATE,
      DataAssessmentReportConstants.CDFX_WORK_PACKAGE,
      DataAssessmentReportConstants.CDFX_PROJECT,
      DataAssessmentReportConstants.CDFX_TARGET_VARIANT,
      DataAssessmentReportConstants.CDFX_TEST_OBJECT,
      DataAssessmentReportConstants.CDFX_PROGRAM_IDENTIFIER,
      DataAssessmentReportConstants.CDFX_DATA_IDENTIFIER,
      DataAssessmentReportConstants.CDFX_REMARK,
      DataAssessmentReportConstants.REF_VAL_MATURITY_LEVEL };

  /**
   * Header columns for Review Info
   */
  protected static final String[] reviewInfoSheetHeader = new String[] {
      DataAssessmentReportConstants.PROJECT,
      DataAssessmentReportConstants.REVIEW_VARIANT,
      DataAssessmentReportConstants.A2LFILE,
      DataAssessmentReportConstants.REVIEW_CREATOR,
      DataAssessmentReportConstants.CALIBRATION_ENGINEER,
      DataAssessmentReportConstants.AUDITOR,
      DataAssessmentReportConstants.OTH_PARTICIPANTS,
      DataAssessmentReportConstants.RVW_CREATION_DATE,
      DataAssessmentReportConstants.WORKPACKAGE,
      DataAssessmentReportConstants.INTERNAL_FILES,
      DataAssessmentReportConstants.INPUT_FILES,
      DataAssessmentReportConstants.PARENT_REVIEW,
      DataAssessmentReportConstants.REVIEW_TYPE,
      DataAssessmentReportConstants.REVIEW_DESCRIPTION,
      DataAssessmentReportConstants.REVIEW_STATUS,
      DataAssessmentReportConstants.FILE_REVIEWED,
      DataAssessmentReportConstants.FILES_ATTACHED,
      DataAssessmentReportConstants.FUNCTIONS_REVIEWWED,
      DataAssessmentReportConstants.ATTRIBUTE_VALUES };

}