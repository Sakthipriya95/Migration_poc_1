/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * This is label provider for comare hex with cdfx
 *
 * @author mkl2cob
 */
public class DaCompHexNatInputToColConverter extends AbstractNatInputToColumnConverter {


  private final DataAssmntReportDataHandler dataAssmntReportDataHandler;

  /**
   * @param dataAssmntReportDataHandler DaCompHexNatInputToColConverter
   */
  public DaCompHexNatInputToColConverter(final DataAssmntReportDataHandler dataAssmntReportDataHandler) {
    this.dataAssmntReportDataHandler = dataAssmntReportDataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object result = null;
    // same row as last, use its object as it's way faster than a list get row
    if (evaluateObj instanceof DaCompareHexParam) {
      // Get the a2l parameter data
      result = getCompareParamColData((DaCompareHexParam) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * @param daCompHexParam
   * @param colIndex
   * @return
   */
  private Object getCompareParamColData(final DaCompareHexParam daCompHexParam, final int colIndex) {
    Object result = "";
    switch (colIndex) {
      case CommonUIConstants.SSD_CLASS_INDEX:
        // Column for SSD class type
        result = daCompHexParam.isCompli() ? CommonUtils.concatenate("Compliance", " ") : "";
        break;
      // column for param type
      case CommonUIConstants.TYPE_INDEX:
        result = daCompHexParam.getParamType().getText();
        break;
      // column for param name
      case CommonUIConstants.PARAMTETER_INDEX:
        result = daCompHexParam.getParamName();
        break;
      // column for func name
      case CommonUIConstants.FUNC_INDEX:
        result = daCompHexParam.getFuncName();
        break;
      // column for func version
      case CommonUIConstants.FUNC_VERS_INDEX:
        result = daCompHexParam.getFuncVers();
        break;
      case CommonUIConstants.WP_INDEX:
        // RESP column
        result = daCompHexParam.getWpName();
        break;
      case CommonUIConstants.RESP_TYPE_INDEX:
        // Responsibility type column
        result = CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daCompHexParam.getRespType())) ||
            CommonUtils.isEqual(WpRespType.CUSTOMER, WpRespType.getType(daCompHexParam.getRespType())) ||
            CommonUtils.isEqual(WpRespType.OTHERS, WpRespType.getType(daCompHexParam.getRespType()))
                ? WpRespType.getType(daCompHexParam.getRespType()).getDispName() : daCompHexParam.getRespType();
        break;
      case CommonUIConstants.RESP_INDEX:
        result = daCompHexParam.getRespName();
        break;
      /** WP Finished */
      case CommonUIConstants.WP_FINISHED_INDEX:
        result = CommonUtils.isEqual(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue(),
            daCompHexParam.getWpFinishedStatus()) ? CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType()
                : daCompHexParam.getWpFinishedStatus();
        break;
      case CommonUIConstants.LATEST_A2L_VERS_INDEX:
        // Latest A2L version
        result = daCompHexParam.getLatestA2lVersion();
        break;
      case CommonUIConstants.LATEST_FUNC_VERS_INDEX:
        // Latest Function version
        result = daCompHexParam.getLatestFunctionVersion();
        break;
      case CommonUIConstants.QNAIRE_STATUS:
        result = this.dataAssmntReportDataHandler.getQnaireStatus(daCompHexParam);
        break;
      case CommonUIConstants.REVIEWED_INDEX:
        result = this.dataAssmntReportDataHandler.getReviewedStatus(daCompHexParam);
        break;
      case CommonUIConstants.EQUAL_INDEX:
        result = this.dataAssmntReportDataHandler.getEqualStatus(daCompHexParam);
        break;
      case CommonUIConstants.HEX_VALUE_INDEX:
        result = this.dataAssmntReportDataHandler.getCalDataStringFromBytes(daCompHexParam.getHexValue());
        break;
      case CommonUIConstants.REVIEWED_VALUE_INDEX:
        result = !daCompHexParam.isNeverReviewed()
            ? this.dataAssmntReportDataHandler.getCalDataStringFromBytes(daCompHexParam.getReviewedValue())
            : ApicConstants.EMPTY_STRING;
        break;
      case CommonUIConstants.COMPLI_RESULT_INDEX:
        result = this.dataAssmntReportDataHandler.getResult(daCompHexParam);
        break;
      case CommonUIConstants.REVIEW_SCORE_INDEX:
        if (CommonUtils.isNotNull(daCompHexParam.getReviewScore())) {
          result = DATA_REVIEW_SCORE.getType(daCompHexParam.getReviewScore()).getScoreDisplay();
        }
        break;
      case CommonUIConstants.LATEST_RVW_CMNT_INDEX:
        if (CommonUtils.isNotNull(daCompHexParam.getLatestReviewComments())) {
          result = daCompHexParam.getLatestReviewComments();
        }
        break;
     // Case for the review description index
      case CommonUIConstants.REVIEW_DESCRIPTION_INDEX:
        // Check if CompHexWithCdfxDataHandler is not null
        if (CommonUtils.isNotNull(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler())) {
          // Retrieve review result name from CompHexWithCdfxDataHandler
          result = this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCdrReportData()
              .getReviewResultName(daCompHexParam.getParamName());
        }
        // If CompHexWithCdfxDataHandler is null
        else {
          // Set result to review result name from daCompHexParam, or default to NO_REVIEW_RESULT_FOUND
          result = daCompHexParam.getRvwResultName() != null ? daCompHexParam.getRvwResultName()
              : CommonUIConstants.NO_REVIEW_RESULT_FOUND;
        }
        break;
      default:
        result = "";
        break;
    }
    return result;
  }
}
