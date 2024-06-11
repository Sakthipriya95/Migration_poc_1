/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.Comparator;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;

/**
 * @author bne4cob
 */
class CdrReportColumnComparator implements Comparator<A2LParameter> {

  private final CdrReportDataHandler cdrReportData;
  private final int columnNum;

  CdrReportColumnComparator(final CdrReportDataHandler cdrReportData, final int columnNum) {
    this.cdrReportData = cdrReportData;
    this.columnNum = columnNum;
  }

  /**
   * @param param1 A2LParameter
   * @param param2 A2LParameter
   * @return int compare result
   */
  @Override
  public int compare(final A2LParameter param1, final A2LParameter param2) {
    int ret;
    switch (this.columnNum) {
      // ICDM-2439
      case CommonUIConstants.SSD_CLASS_COL_INDEX:
        ret = param1.compareTo(param2, A2LParameter.SortColumns.SORT_PARAM_TYPE_COMPLIANCE);
        break;
      case CommonUIConstants.PARAM_TYPE_COL_INDEX:
        ret = param1.compareTo(param2, A2LParameter.SortColumns.SORT_CHAR_TYPE);
        break;
      case CommonUIConstants.PARAM_NAME_COL_INDEX:
        ret = param1.compareTo(param2, A2LParameter.SortColumns.SORT_CHAR_NAME);
        break;
      case CommonUIConstants.FUNC_COL_INDEX:
        ret = param1.compareTo(param2, A2LParameter.SortColumns.SORT_CHAR_DEFFUNC);
        break;
      case CommonUIConstants.FUNC_VERS_COL_INDEX:
        // comparing function version
        ret = funcVerCol(param1, param2);
        break;
      case CommonUIConstants.WP_COL_INDEX:
        // comparing Work package
        ret = ApicUtil.compare(this.cdrReportData.getWpName(param1.getParamId()),
            this.cdrReportData.getWpName(param2.getParamId()));
        break;
      case CommonUIConstants.RESP_TYPE_COL_INDEX:
        ret = ApicUtil.compare(this.cdrReportData.getRespType(param1.getParamId()),
            this.cdrReportData.getRespType(param2.getParamId()));
        break;
      // ICDM-2605
      case CommonUIConstants.RESPONSIBILITY_COL_INDEX:
        // comparing responsibilities
        ret = ApicUtil.compare(this.cdrReportData.getRespName(param1.getParamId()),
            this.cdrReportData.getRespName(param2.getParamId()));
        break;
      /** comparing WP finished */
      case CommonUIConstants.WP_FINISHED_COL_INDEX:
        ret = ApicUtil.compare(this.cdrReportData.getWpFinishedRespStatus(param1.getParamId()),
            this.cdrReportData.getWpFinishedRespStatus(param2.getParamId()));
        break;

      case CommonUIConstants.CW_COL_INDEX:
        ret = ApicUtil.compare(param1.getCodeWordString(), param2.getCodeWordString());
        break;
      case CommonUIConstants.LATEST_A2L_COL_INDEX:
        // comparing latest a2l version
        ret = ApicUtil.compare(this.cdrReportData.getLatestA2LVersion(param1.getName()),
            this.cdrReportData.getLatestA2LVersion(param2.getName()));
        break;
      case CommonUIConstants.LATEST_FUNC_COL_INDEX:
        // comparing whether the paramter is reviewed or not
        ret = ApicUtil.compare(this.cdrReportData.getLatestFunctionVersion(param1.getName()),
            this.cdrReportData.getLatestFunctionVersion(param2.getName()));
        break;
      case CommonUIConstants.RVWD_COL_INDEX:
        // comparing whether the paramter is reviewed or not
        ret = ApicUtil.compare(this.cdrReportData.getReviewScore(param1.getName()),
            this.cdrReportData.getReviewScore(param2.getName()));
        break;
      case CommonUIConstants.RVW_COMMENT_COL_INDEX:
        // comparing latest review comment of parameters
        ret = ApicUtil.compare(this.cdrReportData.getLatestReviewComment(param1.getName()),
            this.cdrReportData.getLatestReviewComment(param2.getName()));
        break;
      case CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX:
        // comparing latest review comment of parameters
        ret = ApicUtil.compare(this.cdrReportData.getQnaireRespVersStatus(param1.getParamId(), false),
            this.cdrReportData.getQnaireRespVersStatus(param2.getParamId(), false));
        break;
      case CommonUIConstants.RVW_DESCRIPTION_COL_INDEX:
        // comparing latest review description of parameters
        ret = ApicUtil.compare(this.cdrReportData.getReviewResultName(param1.getName()),
            this.cdrReportData.getReviewResultName(param2.getName()));
        break;
      default:
        // comparing reviewed dates
        ret = ApicUtil.compare(
            this.cdrReportData.getReviewScore(param1.getName(), this.columnNum - CdrReportListPage.STATIC_COL_INDEX),
            this.cdrReportData.getReviewScore(param2.getName(), this.columnNum - CdrReportListPage.STATIC_COL_INDEX));

    }
    return ret;
  }

  /**
   * @param dataRvwReportObj1
   * @param dataRvwReportObj2
   * @return
   */
  private int funcVerCol(final A2LParameter dataRvwReportObj1, final A2LParameter dataRvwReportObj2) {
    int ret;
    ret = ApicUtil.compare(
        null == dataRvwReportObj1.getDefFunction() ? "" : dataRvwReportObj1.getDefFunction().getFunctionVersion(),
        null == dataRvwReportObj2.getDefFunction() ? "" : dataRvwReportObj2.getDefFunction().getFunctionVersion());
    return ret;
  }

}
