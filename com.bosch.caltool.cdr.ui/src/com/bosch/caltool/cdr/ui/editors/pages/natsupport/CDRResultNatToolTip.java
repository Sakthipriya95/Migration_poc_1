/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Tool tip customization for CDR Result NAT table <br>
 *
 * @author adn1cob
 */

public class CDRResultNatToolTip extends NatTableContentTooltip {

  /**
   *
   */
  private static final String ACCEPTED = "Accepted";
  /**
   * NAT Page instance
   */
  private final ReviewResultParamListPage page;
  ReviewResultClientBO resultData;

  /**
   * @param natTable natTable
   * @param tooltipRegions tooltipRegions
   * @param reviewResultNatPage natPage
   */
  public CDRResultNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final ReviewResultParamListPage reviewResultNatPage) {
    super(natTable, tooltipRegions);
    this.page = reviewResultNatPage;
    this.resultData = ((ReviewResultEditorInput) (reviewResultNatPage.getEditorInput())).getResultData();
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);


    ILayerCell cell = this.natTable.getCellByPosition(col, row);
    if (cell != null) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
        // return tooltip as cell content if cell is in header region
        if (cell.getConfigLabels().hasLabel(GridRegion.COLUMN_HEADER) &&
            !cell.getConfigLabels().hasLabel(GridRegion.FILTER_ROW) && !CommonUtils.isEmptyString(tooltipValue)) {
          return tooltipValue;
        }
        // skip tooltips for filter row
        if (!cell.getConfigLabels().hasLabel(GridRegion.FILTER_ROW)) {
          // Tool tip text based on columns
          String modifiedToolTip = modifyToolTip(cell.getColumnIndex(), cell.getRowIndex());
          if (!modifiedToolTip.isEmpty()) {
            return modifiedToolTip;
          }
          else if (!CommonUtils.isEmptyString(tooltipValue)) {
            return tooltipValue;
          }
        }
      }
    }
    return null;
  }

  /**
   * Get modified tooltip
   *
   * @param col
   * @param tooltipValue
   * @return the modified too tip
   */
  private String modifyToolTip(final int col, final int row) {
    String modifiedToolTip = "";
    Object rowObj = this.page.getCustomFilterGridLayer().getBodyDataProvider().getRowObject(row);
    // set the tooltip
    if (rowObj instanceof CDRResultParameter) {
      modifiedToolTip = getModifiedTooltip(col, rowObj);
    }
    return modifiedToolTip;
  }

  /**
   * @param col
   * @param modifiedToolTip
   * @param rowObj
   * @return
   */
  private String getModifiedTooltip(final int col, final Object rowObj) {
    String modifiedToolTip = "";
    CDRResultParameter resParam = (CDRResultParameter) rowObj;
    // Reference value tooltip
    // ICDM-2439 column position changed
    if ((col == ReviewResultParamListPage.REF_VAL_COL_NUMBER) && this.resultData.getResultBo().isDeltaReview() &&
        this.resultData.isRefValChanged(resParam)) {
      modifiedToolTip = "Parent Reference Value: " + this.resultData.getParentRefValString(resParam);
    }
    // Check value tooltip
    // ICDM-2151
    else if (col == ReviewResultParamListPage.CHECK_VALUE_COL_NUMBER) {
      modifiedToolTip = getTooltipForCheckValueColumn(resParam);
    }
    // ICDM-1940
    // lower limit tooltip
    else if ((col == ReviewResultParamListPage.LOWER_LIMIT_COL_NUMBER) &&
        this.resultData.getResultBo().isDeltaReview() && this.resultData.isLowerLimitChanged(resParam)) {
      modifiedToolTip = "Parent Lower limit: " + this.resultData.getParentLowerLimitString(resParam);
    }
    // upper limit tooltip
    // ICDM-2151
    else if ((col == ReviewResultParamListPage.UPPER_LIMIT_COL_NUMBER) &&
        this.resultData.getResultBo().isDeltaReview() && this.resultData.isUpperLimitChanged(resParam)) {
      modifiedToolTip = "Parent Upper limit: " + this.resultData.getParentUpperLimitString(resParam);
    }
    // bitwise flag tooltip
    else if ((col == ReviewResultParamListPage.BITWISE_COL_NUMBER) && this.resultData.getResultBo().isDeltaReview() &&
        this.resultData.isBitwiseFlagChanged(resParam)) {
      modifiedToolTip = "Parent Bitwise Value: " + this.resultData.getParentBitwiseValString(resParam);
    }
    // bitwise limit tooltip
    // ICDM-2151
    else if ((col == ReviewResultParamListPage.READY_FOR_SERIES_COL_NUMBER) &&
        this.resultData.getResultBo().isDeltaReview() && this.resultData.isReadyForSeriesFlagChanged(resParam)) {
      modifiedToolTip = "Parent Ready For Series: " + this.resultData.getParentReadyForSeriesString(resParam);
    }
    else if ((col == ReviewResultParamListPage.EXACT_MATCH_COL_NUMBER) &&
        this.resultData.getResultBo().isDeltaReview() && this.resultData.isExactMatchFlagChanged(resParam)) {
      modifiedToolTip = "Parent Exact Match: " + this.resultData.getParentExactMatchUiStr(resParam);
    }
    else {
      modifiedToolTip = getTooltipForOthercolumns(col, resParam);
    }
    return modifiedToolTip;
  }

  /**
   * @param col
   * @param modifiedToolTip
   * @param resParam
   * @return
   */
  private String getTooltipForOthercolumns(final int col, final CDRResultParameter resParam) {
    String modifiedToolTip = "";
    if ((col == ReviewResultParamListPage.BIT_LIMIT_COL_NUMBER) && this.resultData.getResultBo().isDeltaReview() &&
        this.resultData.isBitwiseLimitChanged(resParam)) {
      modifiedToolTip = "Parent Bitwise limit: " + this.resultData.getParentBitwiseLimitString(resParam);
    }
    // result change tooltip
    // ICDM-2151
    else if ((col == ReviewResultParamListPage.RESULT_COL_NUMBER) && this.resultData.getResultBo().isDeltaReview() &&
        this.resultData.isResultChanged(resParam)) {
      modifiedToolTip = getDelResColTooltip(resParam);
    }
    // Task 236308
    else if ((col == ReviewResultParamListPage.SEC_RESULT_COL_NUMBER) &&
        this.resultData.getResultBo().isDeltaReview() && this.resultData.isSecondaryResultChanged(resParam)) {
      modifiedToolTip = "Parent Secondary Result: " + this.resultData.getParentSecResultValueString(resParam);
    }
    else if ((col == ReviewResultParamListPage.CHECK_VALUE_UNIT_COL_NUMBER) &&
        this.resultData.isCheckValueRefValueUnitDifferent(resParam)) {
      modifiedToolTip = "Different from Reference Value's Unit :  " + resParam.getRefUnit();
    }
    else if (col == ReviewResultParamListPage.RESULT_COL_NUMBER) {
      modifiedToolTip = getResColTooltip(resParam);
    }
    else if (col == ReviewResultParamListPage.SCORE_COL_NUMBER) {
      modifiedToolTip = getScoreColTooltip(resParam);
    }
    else if ((col == ReviewResultParamListPage.COMMENT_COL_NUMBER) &&
        CommonUtils.isEmptyString(resParam.getRvwComment())) {
      modifiedToolTip = "Right click to set comment/import from another review";
    }
    // ICDM-2487 P1.27.122
    else if ((col == ReviewResultParamListPage.SSD_CLASS_COL_NUMBER)) {
      modifiedToolTip = getTooltipForSSDClassColumn(modifiedToolTip, resParam);
    }
    else if (col == ReviewResultParamListPage.PARAMETER_COL_NUMBER) {
      modifiedToolTip = resParam.getName() + "\n" + "FC Name: " + resParam.getFuncName() + "\n" + "Long Name: " +
          resParam.getDescription();
    }
    return modifiedToolTip;
  }

  /**
   * @param resParam
   * @return
   */
  private String getTooltipForCheckValueColumn(final CDRResultParameter resParam) {
    String modifiedToolTip;
    if (this.resultData.getResultBo().isDeltaReview() && this.resultData.isCheckedValueChanged(resParam)) {
      modifiedToolTip = "Parent Check Value: " + this.resultData.getParentCheckedValString(resParam);
    }
    else {
      modifiedToolTip = this.resultData.getCheckedValueString(resParam);
    }
    return modifiedToolTip;
  }

  /**
   * @param modifiedToolTip
   * @param resParam
   * @return
   */
  private String getTooltipForSSDClassColumn(String modifiedToolTip, final CDRResultParameter resParam) {
    if (this.resultData.isComplianceParameter(resParam)) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.COMPLIANCE_PARAM).concat("\n");
    }
    if (this.resultData.isReadOnly(resParam)) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.READ_ONLY_PARAM).concat("\n");
    }
    if (this.resultData.isQssdParameter(resParam)) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.QSSD_PARAM).concat("\n");
    }
    if (this.resultData.isDependentParam(resParam)) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.DEPENDENT_PARAM).concat("\n");
      StringBuilder depToolTip = new StringBuilder();
      this.resultData.getResponse().getDepParamMap().get(resParam.getParamId())
          .forEach(depParamName -> depToolTip.append("\t").append(depParamName).append("\n"));
      modifiedToolTip = modifiedToolTip.concat(depToolTip.toString()).concat("\n");
    }
    if (this.resultData.isBlackList(resParam)) {
      CommonDataBO dataBo = new CommonDataBO();
      try {
        modifiedToolTip =
            modifiedToolTip.concat(dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP).concat("\n"));
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    if (modifiedToolTip.length() > 0) {
      modifiedToolTip = modifiedToolTip.substring(0, modifiedToolTip.length() - 1);
    }
    return modifiedToolTip;
  }

  /**
   * @param resParam
   * @return
   */
  private String getScoreColTooltip(final CDRResultParameter resParam) {
    String modifiedToolTip;
    if (this.resultData.getResultBo().isDeltaReview() /* && resParam.isScoreChanged() */) {
      modifiedToolTip = this.resultData.getScoreExtDescription(resParam) + "\n" + "Parent Score\t: " +
          this.resultData.getParentScoreValueString(resParam);
    }
    else {
      modifiedToolTip = this.resultData.getScoreExtDescription(resParam);
    }
    return modifiedToolTip;
  }

  /**
   * @param resParam
   * @return
   */
  private String getResColTooltip(final CDRResultParameter resParam) {
    String modifiedToolTip;
    StringBuilder tooltip = new StringBuilder();

    // If the parameter is compliant and the compliant result is not null,
    // Show the compliance result
    if (null != resParam.getCompliResult()) {
      tooltip.append("Compliance Result : ");
      tooltip.append(CDRConstants.COMPLI_RESULT_FLAG.getType(resParam.getCompliResult()));
      appendNewLine(tooltip);
    }
    // append QSSD result to the resParam if the param is a QSSD param
    if (null != resParam.getQssdResult()) {
      tooltip.append("QSSD Result : ");
      tooltip.append(CDRConstants.QSSD_RESULT_FLAG.getType(resParam.getQssdResult()));
      appendNewLine(tooltip);
    }
    if (null != resParam.getSrResult()) {
      getTooltipForSr(resParam, tooltip);
      getTooltipForSrAccp(resParam, tooltip);
    }
    tooltip.append("Review Result\t\t : ");
    tooltip.append(this.resultData.getCommonResult(resParam));
    modifiedToolTip = tooltip.toString();
    return modifiedToolTip;
  }

  /**
   * @param resParam
   * @param tooltip
   */
  private void getTooltipForSrAccp(final CDRResultParameter resParam, final StringBuilder tooltip) {
    if (null != resParam.getSrAcceptedFlag()) {
      tooltip.append("Shape Result Accepted\t : ");
      if (CDRConstants.SR_ACCEPTED_FLAG.YES.getUiType().equals(resParam.getSrAcceptedFlag())) {
        tooltip.append(CDRConstants.SR_ACCEPTED_FLAG.YES.getUiType());
      }
      else {
        tooltip.append(CDRConstants.SR_ACCEPTED_FLAG.NO.getUiType());
      }
      appendNewLine(tooltip);
    }
  }

  /**
   * @param resParam
   * @param tooltip
   */
  private void getTooltipForSr(final CDRResultParameter resParam, final StringBuilder tooltip) {
    tooltip.append("Shape Result\t\t : ");
    if (CDRConstants.SR_RESULT.FAIL.getUiType().equalsIgnoreCase(resParam.getSrResult())) {
      tooltip.append(CDRConstants.SR_RESULT.FAIL.getUiType());
    }
    else {
      tooltip.append(CDRConstants.SR_RESULT.PASS.getUiType());
    }
    appendNewLine(tooltip);
  }

  /**
   * @param resParam
   * @return
   */
  private String getDelResColTooltip(final CDRResultParameter resParam) {
    String modifiedToolTip;
    StringBuilder tooltip = new StringBuilder();
    getDelResColToolTipForResults(resParam, tooltip);
    getResColToolTipParent(resParam, tooltip);
    modifiedToolTip = tooltip.toString();
    return modifiedToolTip;
  }

  /**
   * @param resParam
   * @param tooltip
   */
  private void getDelResColToolTipForResults(final CDRResultParameter resParam, final StringBuilder tooltip) {
    // If the parameter is compliant and the compliant result is not null,
    // Show the compliance result
    if (null != resParam.getCompliResult()) {
      tooltip.append("Compliance Result\t : ");
      tooltip.append(CDRConstants.COMPLI_RESULT_FLAG.getType(resParam.getCompliResult()));
      appendNewLine(tooltip);
    }
    // If the parameter is qssd and the qssd result is not null,
    // Show the qssd result
    if (null != resParam.getQssdResult()) {
      tooltip.append("QSSD Result : ");
      tooltip.append(CDRConstants.QSSD_RESULT_FLAG.getType(resParam.getQssdResult()).getUiType());
      appendNewLine(tooltip);
    }
    // If shape check is performed, append the shape result to tooltip
    if (null != resParam.getSrResult()) {
      tooltip.append("Shape Result\t : ");
      if (CDRConstants.SR_RESULT.FAIL.getDbType().equalsIgnoreCase(resParam.getSrResult()) &&
          (null != resParam.getSrAcceptedFlag()) &&
          CDRConstants.SR_ACCEPTED_FLAG.YES.getDbType().equalsIgnoreCase(resParam.getSrAcceptedFlag())) {
        tooltip.append(ACCEPTED);
        appendNewLine(tooltip);
      }
      else {
        tooltip.append(resParam.getSrResult());
        appendNewLine(tooltip);
      }
    }
    // Append the common result of the parameter
    tooltip.append("Review Result\t : ");
    tooltip.append(this.resultData.getCommonResult(resParam));
    appendNewLine(tooltip);
  }

  /**
   * @param tooltip
   */
  private void appendNewLine(final StringBuilder tooltip) {
    tooltip.append("\n");
  }

  /**
   * @param resParam
   * @param tooltip
   */
  private void getResColToolTipParent(final CDRResultParameter resParam, final StringBuilder tooltip) {
    // If the parent parameter is compliant and the compliant result is not null,
    // Show the compliance result of parent parameter
    if (!this.resultData.getParentCompResultValStr(resParam).isEmpty()) {
      tooltip.append("Parent Compliance Result : ");
      tooltip.append(this.resultData.getParentCompResultValStr(resParam));
      appendNewLine(tooltip);
    }
    // If the parent parameter is qssd and the compliant result is not null,
    // Show the qssd result of parent parameter
    if (!this.resultData.getParentQssdResultValStr(resParam).isEmpty()) {
      tooltip.append("Parent QSSD Result : ");
      tooltip.append(this.resultData.getParentQssdResultValStr(resParam));
      appendNewLine(tooltip);
    }
    // If shape check is performed for parent parameter, append the shape review result
    if (null != this.resultData.getParentParam(resParam).getSrResult()) {
      tooltip.append("Parent Shape Result\t : ");
      if (CDRConstants.SR_RESULT.FAIL.getDbType()
          .equalsIgnoreCase(this.resultData.getParentParam(resParam).getSrResult()) &&
          (null != this.resultData.getParentParam(resParam).getSrAcceptedFlag()) && CDRConstants.SR_ACCEPTED_FLAG.YES
              .getDbType().equalsIgnoreCase(this.resultData.getParentParam(resParam).getSrAcceptedFlag())) {
        tooltip.append(ACCEPTED);
        appendNewLine(tooltip);
      }
      else {
        tooltip.append(this.resultData.getParentParam(resParam).getSrResult());
        appendNewLine(tooltip);
      }
    }
    // Append the common result of parent parameter
    tooltip.append("Parent Review Result\t : ");
    tooltip.append(this.resultData.getCommonResult(this.resultData.getParentParam(resParam)));
  }
}
