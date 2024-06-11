/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_STATUS;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * This is label provider for comare hex with cdfx
 *
 * @author mkl2cob
 */
public class CompHexWithCdfNatInputToColConvertr extends AbstractNatInputToColumnConverter {

  /**
   * Report Data instance
   */
  private final CdrReportDataHandler reportData;

  /**
   * Instantiates a new comp hex with cdf nat input to col convertr.
   *
   * @param reportData the report data
   */
  public CompHexWithCdfNatInputToColConvertr(final CdrReportDataHandler reportData) {
    this.reportData = reportData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object result = null;
    // same row as last, use its object as it's way faster than a list get row
    if (evaluateObj instanceof CompHexWithCDFParam) {
      // Get the a2l parameter data
      result = getCompareParamColData((CompHexWithCDFParam) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * @param comparedObj
   * @param colIndex
   * @return
   */
  private Object getCompareParamColData(final CompHexWithCDFParam comparedObj, final int colIndex) {
    Object result = "";
    switch (colIndex) {
      case CommonUIConstants.SSD_CLASS_INDEX:
        // Column for SSD class type
        result = comparedObj.isCompli() ? CommonUtils.concatenate("Compliance", " ") : "";
        break;
      // column for param type
      case CommonUIConstants.TYPE_INDEX:
        result = comparedObj.getParamType().getText();
        break;
      // column for param name
      case CommonUIConstants.PARAMTETER_INDEX:
        result = comparedObj.getParamName();
        break;
      // column for func name
      case CommonUIConstants.FUNC_INDEX:
        result = comparedObj.getFuncName();
        break;
      // column for func version
      case CommonUIConstants.FUNC_VERS_INDEX:
        result = comparedObj.getFuncVers();
        break;
      case CommonUIConstants.WP_INDEX:
        // RESP column
        result = this.reportData.getWpName(comparedObj.getParamName());
        break;
      case CommonUIConstants.RESP_TYPE_INDEX:
        // Responsibility type column
        result = this.reportData.getRespType(comparedObj.getParamName());
        break;
      case CommonUIConstants.RESP_INDEX:
        result = this.reportData.getRespName(comparedObj.getParamName());
        break;
      /** WP Finished */
      case CommonUIConstants.WP_FINISHED_INDEX:
        result = this.reportData.getWpFinishedRespStatuswithName(comparedObj.getParamName());
        break;
      case CommonUIConstants.LATEST_A2L_VERS_INDEX:
        // Latest A2L version
        result = comparedObj.getLatestA2lVersion();
        break;
      case CommonUIConstants.LATEST_FUNC_VERS_INDEX:
        // Latest Function version
        result = comparedObj.getLatestFunctionVersion();
        break;
      case CommonUIConstants.QNAIRE_STATUS:
        // questionnaire version status
        String qnaireRespVersStatus = this.reportData.getQnaireRespVersStatus(comparedObj.getParamName(), false);
        // questionnaire version status
        if (CommonUtils.isNotEmptyString(qnaireRespVersStatus) &&
            CommonUtils.isNotNull(CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus))) {
          result = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus).getUiType();
        }
        else {
          result = qnaireRespVersStatus;
        }
        break;
      case CommonUIConstants.REVIEWED_INDEX:
        result = getReviewedStatus(comparedObj);
        break;
      case CommonUIConstants.EQUAL_INDEX:
        result = getEqualStatus(comparedObj);
        break;
      case CommonUIConstants.HEX_VALUE_INDEX:
        if (CommonUtils.isNotNull(comparedObj.getHexCalDataPhySimpleDispValue())) {
          return comparedObj.getHexCalDataPhySimpleDispValue();
        }
        break;
      case CommonUIConstants.REVIEWED_VALUE_INDEX:
        if (CommonUtils.isNotNull(comparedObj.getCdfxCalDataPhySimpleDispValue())) {
          result = !comparedObj.isNeverReviewed() ? comparedObj.getCdfxCalDataPhySimpleDispValue()
              : ApicConstants.EMPTY_STRING;
        }
        break;
      case CommonUIConstants.COMPLI_RESULT_INDEX:
        result = getResultUIVal(comparedObj);
        break;
      case CommonUIConstants.REVIEW_SCORE_INDEX:
        if (CommonUtils.isNotNull(comparedObj.getReviewScore())) {
          result = comparedObj.getReviewScore();
        }
        break;
      case CommonUIConstants.LATEST_RVW_CMNT_INDEX:
        if (CommonUtils.isNotNull(comparedObj.getLatestReviewComments())) {
          result = comparedObj.getLatestReviewComments();
        }
        break;
      case CommonUIConstants.REVIEW_DESCRIPTION_INDEX:
        result = this.reportData.getReviewResultName(comparedObj.getParamName());
        break;
      default:
        result = "";
        break;
    }
    return result;
  }


  /**
   * @param comparedObj
   * @return
   */
  private Object getResultUIVal(final CompHexWithCDFParam comparedObj) {
    return this.reportData.getResult(comparedObj);
  }


  /**
   * Gets the equal status.
   *
   * @param comparedObj the compared obj
   * @return the equal status
   */
  private Object getEqualStatus(final CompHexWithCDFParam comparedObj) {
    if (comparedObj.isNeverReviewed()) {
      return ApicConstants.EMPTY_STRING;
    }
    return comparedObj.isEqual() ? CommonUtils.concatenate(RESULT_STATUS.EQUAL.getText(), "  ")
        : RESULT_STATUS.NOT_EQUAL.getText();
  }

  /**
   * Gets the reviewed status.
   *
   * @param comparedObj the compared obj
   * @return the reviewed status
   */
  private Object getReviewedStatus(final CompHexWithCDFParam comparedObj) {
    if (comparedObj.isNeverReviewed()) {
      return ApicConstants.NEVER_REVIEWED;
    }
    if (comparedObj.isReviewed()) {
      // Space is appended to differentiate the filter text 'Reviewed' from other similar texts like 'Never Reviewed'
      return CommonUtils.concatenate(ApicConstants.REVIEWED, "  ");
    }
    return ApicConstants.NOT_FINALLY_REVIEWED;
  }
}
