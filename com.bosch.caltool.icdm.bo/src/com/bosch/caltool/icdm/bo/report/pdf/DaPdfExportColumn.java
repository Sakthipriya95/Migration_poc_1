/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;


import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;

/**
 * @author HNU1COB
 */
public enum DaPdfExportColumn {

                               /**
                                * Unique Instance for Single class implementation
                                */
                               INSTANCE;


  /**
   * Column headers for Work Packages table in PDF
   */
  protected static final String[] HDR_DA_WP_TABLE = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.WORK_PACKAGE_COL,
      DataAssessmentReportConstants.RESPONSIBLE_COL,
      DataAssessmentReportConstants.WP_READY_FOR_PROD_COL,
      DataAssessmentReportConstants.WP_FINISHED_COL,
      DataAssessmentReportConstants.QNAIRE_ANS_AND_BASELINED_COL,
      DataAssessmentReportConstants.PARAM_REVIEWED_COL,
      DataAssessmentReportConstants.HEX_FILE_EQUAL_REV_VAL_COL };

  /**
   * Column headers for Questionnaires table in PDF
   */
  protected static final String[] HDR_DA_QNAIRE_TABLE = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.WORK_PACKAGE_COL,
      DataAssessmentReportConstants.RESPONSIBLE_COL,
      DataAssessmentReportConstants.QNAIRE_COL,
      DataAssessmentReportConstants.READY_FOR_PROD_COL,
      DataAssessmentReportConstants.BASELINE_EXISTING_COL,
      DataAssessmentReportConstants.POSITIVE_ANS_COL,
      DataAssessmentReportConstants.NEGATIVE_ANS_COL,
      DataAssessmentReportConstants.NEUTRAL_ANS_COL,
      DataAssessmentReportConstants.VERSION_NAME_COL,
      DataAssessmentReportConstants.REVIEWED_BY_COL,
      DataAssessmentReportConstants.REVIEWED_ON_COL,
      DataAssessmentReportConstants.QNAIRE_LINK_COL };

  /**
   * Column headers for Questionnaires table in PDF
   */
  protected static final String[] HDR_DA_BASELINES_TABLE = new String[] {
      DataAssessmentReportConstants.SERIAL_NUM,
      DataAssessmentReportConstants.BASELINE_NAME_COL,
      DataAssessmentReportConstants.REMARKS_COL,
      DataAssessmentReportConstants.CREATED_ON_COL,
      DataAssessmentReportConstants.ASSESSMENT_TYPE_COL,
      DataAssessmentReportConstants.VARIANT_NAME,
      DataAssessmentReportConstants.BASELINE_FILE_STATUS };

  /**
   * @return the unique instance of this class
   */
  public static DaPdfExportColumn getInstance() {
    return INSTANCE;
  }
}
