/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.ruleseditor.utils.CdrUIUtils;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * NAT table input converter for Review result page
 *
 * @author adn1cob
 */
public class ResultParamNatInputToColConverter extends AbstractNatInputToColumnConverter {


  private final ReviewResultClientBO resultData;
  private final ReviewResultEditor editor;

  /**
   * @param editor ReviewResultEditor
   */
  public ResultParamNatInputToColConverter(final ReviewResultEditor editor) {
    this.resultData = editor.getEditorInput().getResultData();
    this.editor = editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object result = null;
    if (evaluateObj instanceof CDRResultParameter) {
      result = getCDRResultParameterColumnData((CDRResultParameter) evaluateObj, colIndex);
    }

    return result;

  }

  /**
   * Get CDR Result parameter
   *
   * @param cdrResParam param
   * @param colIndex index
   * @return object
   */
  public Object getCDRResultParameterColumnData(final CDRResultParameter cdrResParam, final int colIndex) {
    Object result = null;
    switch (colIndex) {
      case ReviewResultParamListPage.SSD_CLASS_COL_NUMBER:
        result = CdrUIUtils.getSSDLabelString(cdrResParam, this.resultData);
        break;
      case ReviewResultParamListPage.PARAMETER_COL_NUMBER:
        result = CommonUtils.checkNull(cdrResParam.getName());
        break;
      case ReviewResultParamListPage.LONG_NAME_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getFunctionParameter(cdrResParam).getLongName());
        break;
      // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
      case ReviewResultParamListPage.WORKPACKAGE_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getWpName(cdrResParam));
        break;
      case ReviewResultParamListPage.RESPONSIBILITY_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getRespName(cdrResParam));
        break;
      case ReviewResultParamListPage.RESP_TYPE_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getRespType(cdrResParam));
        break;
      case ReviewResultParamListPage.TYPE_COL_NUMBER:
        result =
            CommonUtils.checkNull(CommonUtils.checkNull(this.resultData.getFunctionParameter(cdrResParam).getType()));
        break;
      case ReviewResultParamListPage.CLASS_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getParameterClassStr(cdrResParam));
        break;
      case ReviewResultParamListPage.CODEWORD_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getParamIsCodeWordDisplay(cdrResParam));
        break;
      case ReviewResultParamListPage.BITWISE_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getParamIsBitWiseDisplay(cdrResParam));
        break;
      case ReviewResultParamListPage.PARAM_HINT_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getParameterHint(cdrResParam));
        break;
      case ReviewResultParamListPage.FC_NAME_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getFunctionName(cdrResParam));
        break;
      case ReviewResultParamListPage.LOWER_LIMIT_COL_NUMBER:
        // to avoid null pointer exception while grouping, empty string is returned
        result = cdrResParam.getLowerLimit() == null ? "" : cdrResParam.getLowerLimit().toString();
        break;
      case ReviewResultParamListPage.REF_VAL_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getRefValueString(cdrResParam));
        break;
      case ReviewResultParamListPage.PARENT_REF_VALUE_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getParentRefValue(cdrResParam));
        break;
      case ReviewResultParamListPage.PARENT_CHECK_VALUE_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getParentCheckedValue(cdrResParam));
        break;
      // ICDM-2151
      case ReviewResultParamListPage.REF_VAL_UNIT_COL_NUMBER:
        result = CommonUtils.checkNull(cdrResParam.getRefUnit());
        break;
      case ReviewResultParamListPage.UPPER_LIMIT_COL_NUMBER:
        result = cdrResParam.getUpperLimit() == null ? "" : cdrResParam.getUpperLimit().toString();
        break;
      case ReviewResultParamListPage.BIT_LIMIT_COL_NUMBER:
        result = CommonUtils.checkNull(cdrResParam.getBitwiseLimit());
        break;
      case ReviewResultParamListPage.CHECK_VALUE_COL_NUMBER:
        // 657694: Impl - Same format between “Reference Value” and "Checked Value" in review result editor
        result = CommonUtils.checkNull(this.resultData.getCheckedValueString(cdrResParam));
        break;
      // ICDM-2151
      case ReviewResultParamListPage.CHECK_VALUE_UNIT_COL_NUMBER:
        result = CommonUtils.checkNull(cdrResParam.getCheckUnit());
        break;
      case ReviewResultParamListPage.IMP_VALUE_COL_NUMBER:
        result = this.resultData.getParamCalDataPhy(cdrResParam);
        break;
      case ReviewResultParamListPage.RESULT_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getResult(cdrResParam));
        break;
      // Task 231287
      case ReviewResultParamListPage.SEC_RESULT_COL_NUMBER:
        result = CommonUtils.checkNull(this.resultData.getCustomSecondaryResult(cdrResParam));
        break;
      case ReviewResultParamListPage.SCORE_COL_NUMBER:
        result = this.resultData.getScoreUIType(cdrResParam);
        break;
      case ReviewResultParamListPage.SCORE_DESC_COL_NUMBER:
        result = this.resultData.getScoreDescription(cdrResParam);
        break;
      case ReviewResultParamListPage.COMMENT_COL_NUMBER:
        result = CommonUtils.checkNull(cdrResParam.getRvwComment());
        break;
      case ReviewResultParamListPage.CDFX_STATUS_COL_NUMBER:
        result = this.resultData.getHistoryState(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_USER_COL_NUMBER:
        result = this.resultData.getHistoryUser(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_DATE_COL_NUMBER:
        result = this.resultData.getHistoryDate(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_WP_COL_NUMBER:
        result = this.resultData.getHistoryContext(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_PROJECT_COL_NUMBER:
        result = this.resultData.getHistoryProject(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_TARGET_VAR_COL_NUMBER:
        result = this.resultData.getHistoryTargetVariant(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_TEST_OBJ_COL_NUMBER:
        result = this.resultData.getHistoryTestObject(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_PROGRAM_ID_COL_NUMBER:
        result = this.resultData.getHistoryProgramIdentifier(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_DATA_COL_NUMBER:
        result = this.resultData.getHistoryDataIdentifier(cdrResParam);
        break;
      case ReviewResultParamListPage.CDFX_REMARK_COL_NUMBER:
        result = this.resultData.getHistoryRemark(cdrResParam);
        break;
      case ReviewResultParamListPage.MATURITY_LEVEL_COL_NUMBER:
        result = this.resultData.getMaturityLevel(cdrResParam);
        break;
      case ReviewResultParamListPage.READY_FOR_SERIES_COL_NUMBER:
        result = this.resultData.getReadyForSeriesStr(cdrResParam);
        break;
      case ReviewResultParamListPage.EXACT_MATCH_COL_NUMBER:
        result = this.resultData.getExactMatchUiStr(cdrResParam);
        break;
      case ReviewResultParamListPage.ARC_RELEASED_COL_NUMBER:
        result = cdrResParam.getArcReleasedFlag() ? "Yes" : "No";
        break;
      default:
        result = "";
        break;
    }
    if (CommonUtils.isNotEqual(colIndex, ReviewResultParamListPage.COMMENT_COL_NUMBER)) {
      result = this.editor.sliceNumericValue(result);
    }
    return result;
  }


}
