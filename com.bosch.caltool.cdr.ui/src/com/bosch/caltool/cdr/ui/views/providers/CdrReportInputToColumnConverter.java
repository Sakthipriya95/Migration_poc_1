/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_STATUS_TYPE;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * ICDM-1697
 *
 * @author mkl2cob
 */
public class CdrReportInputToColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * index for static columns
   */
  private static final int STATIC_COL_INDEX = 16;
  /**
   * CdrReportData
   */

  private final CdrReportDataHandler cdrReportData;

  /**
   * @param cdrReportData CdrReportData
   */
  public CdrReportInputToColumnConverter(final CdrReportDataHandler cdrReportData) {

    this.cdrReportData = cdrReportData;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    String result = null;
    if (evaluateObj instanceof A2LParameter) { // ICDM-1700
      A2LParameter a2lParam = (A2LParameter) evaluateObj;
      switch (colIndex) {
        case CommonUIConstants.SSD_CLASS_COL_INDEX:
          result = a2lParam.isComplianceParam() ? CommonUtils.concatenate("Compliance", " ") : "";
          break;
        case CommonUIConstants.PARAM_TYPE_COL_INDEX:
          result = a2lParam.getType();
          break;
        case CommonUIConstants.PARAM_NAME_COL_INDEX:
          result = a2lParam.getName();
          break;
        case CommonUIConstants.FUNC_COL_INDEX:
          // ICDM-1901
          result = a2lParam.getDefFunction() == null ? ApicConstants.EMPTY_STRING : a2lParam.getDefFunction().getName();
          break;
        case CommonUIConstants.FUNC_VERS_COL_INDEX:
          // ICDM-1901
          result = a2lParam.getDefFunction() == null ? ApicConstants.EMPTY_STRING
              : a2lParam.getDefFunction().getFunctionVersion();
          break;
        case CommonUIConstants.WP_COL_INDEX:
          // get the responsibility from cdr report data
          result = this.cdrReportData.getWpName(a2lParam.getParamId());
          break;
        case CommonUIConstants.RESP_TYPE_COL_INDEX:

          result = this.cdrReportData.getRespType(a2lParam.getParamId());
          break;
        case CommonUIConstants.RESPONSIBILITY_COL_INDEX:
          // get the responsibility from cdr report data
          // ICDM-2605
          result = this.cdrReportData.getRespName(a2lParam.getParamId());
          break;

        case CommonUIConstants.WP_FINISHED_COL_INDEX:
          /** Fetch the WP_finished status from cdrReportData */
          result = this.cdrReportData.getWpFinishedRespStatus(a2lParam.getParamId());
          break;

        case CommonUIConstants.CW_COL_INDEX:
          result = a2lParam.getCodeWordString();
          break;
        case CommonUIConstants.LATEST_A2L_COL_INDEX:
          result = this.cdrReportData.getLatestA2LVersion(a2lParam.getName());
          break;
        // latest function version column
        case CommonUIConstants.LATEST_FUNC_COL_INDEX:
          result = this.cdrReportData.getLatestFunctionVersion(a2lParam.getName());
          break;
        case CommonUIConstants.RVWD_COL_INDEX:
          return getReviewedStatus(a2lParam);
        case CommonUIConstants.RVW_COMMENT_COL_INDEX:
          return getLatestReviewComment(a2lParam);
        case CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX:
          return getQnaireStatusDispText(a2lParam);
        case CommonUIConstants.RVW_DESCRIPTION_COL_INDEX:
          result = this.cdrReportData.getReviewResultName(a2lParam.getName());
          break;
        default:
          // get the display text from cdr report data in case of dynamic columns
          result = this.cdrReportData.getDisplayText(a2lParam.getName(), colIndex - STATIC_COL_INDEX);
      }
    }
    return result;
  }


  /**
   * @param a2lParam
   * @return qnaire status
   */
  private String getQnaireStatusDispText(final A2LParameter a2lParam) {

    String qnaireRespVersStatus = this.cdrReportData.getQnaireRespVersStatus(a2lParam.getParamId(), false);
    QS_STATUS_TYPE typeByDbCode = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus);

    // questionnaire version status
    if (CommonUtils.isNotEmptyString(qnaireRespVersStatus) && CommonUtils.isNotNull(typeByDbCode)) {
      return typeByDbCode.getUiType();
    }

    return qnaireRespVersStatus;
  }

  /**
   * Task 288502
   *
   * @param a2lParam A2LParameter
   * @return String
   */
  private String getLatestReviewComment(final A2LParameter a2lParam) {
    return this.cdrReportData.getLatestReviewComment(a2lParam.getName());
  }

  /**
   * @param a2lParam
   * @return
   */
  private Object getReviewedStatus(final A2LParameter a2lParam) {
    // ICDM-2585 (Parent Task ICDM-2412)-2
    String reviewedStr = this.cdrReportData.isReviewedStr(a2lParam.getName());
    if (ApicConstants.REVIEWED.equals(reviewedStr)) {
      return CommonUtils.concatenate(ApicConstants.REVIEWED, ApicConstants.EMPTY_SPACE);
    }
    else if (ApicConstants.NEVER_REVIEWED.equals(reviewedStr)) {
      return ApicConstants.EMPTY_STRING;
    }
    else {
      return ApicConstants.NOT_REVIEWED;
    }
  }
}
