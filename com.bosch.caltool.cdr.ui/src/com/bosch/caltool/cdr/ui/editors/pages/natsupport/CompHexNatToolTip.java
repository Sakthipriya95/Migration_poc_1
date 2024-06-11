/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.cdr.ui.editors.pages.CompHexWithCdfNatPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_STATUS;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * @author dja7cob
 */
public class CompHexNatToolTip extends NatTableContentTooltip {

  private final CompHexWithCdfNatPage page;

  /**
   * @param natTable
   * @param strings
   * @param compHexWithCdfNatPage
   */
  public CompHexNatToolTip(final CustomNATTable natTable, final String[] tooltipRegions,
      final CompHexWithCdfNatPage compHexWithCdfNatPage) {
    super(natTable, tooltipRegions);
    this.page = compHexWithCdfNatPage;
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
        // tooltip for header in comphex nattable
        if (cell.getConfigLabels().hasLabel(GridRegion.COLUMN_HEADER) && !CommonUtils.isEmptyString(tooltipValue)) {
          return tooltipValue;
        }
        // skip tooltips for filter row
        if (!cell.getConfigLabels().hasLabel(GridRegion.FILTER_ROW)) {
          // Tool tip text based on columns
          String modifiedToolTip = modifyToolTip(cell);
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
  private String modifyToolTip(final ILayerCell cell) {
    String modifiedToolTip = "";
    Object rowObj = this.page.getCompRprtFilterGridLayer().getBodyDataProvider().getRowObject(cell.getRowIndex());
    // set the tooltip
    if (rowObj instanceof CompHexWithCDFParam) {
      modifiedToolTip = getModifiedTooltip(cell, cell.getColumnIndex(), rowObj);
    }
    return modifiedToolTip;
  }

  /**
   * @param col
   * @param rowObj
   * @return
   */
  /**
   * @param col
   * @param modifiedToolTip
   * @param rowObj
   * @return
   */
  private String getModifiedTooltip(final ILayerCell cell, final int col, final Object rowObj) {
    String modifiedToolTip = "";
    CompHexWithCDFParam compParam = (CompHexWithCDFParam) rowObj;

    if (col == CommonUIConstants.COMPLI_RESULT_INDEX) {
      modifiedToolTip = getCompliResultTooltip(compParam);
    }
    else if (col == CommonUIConstants.REVIEW_SCORE_INDEX) {
      modifiedToolTip = getReviewScoreTooltip(compParam, col);
    }
    else if (col == CommonUIConstants.QNAIRE_STATUS) {
      modifiedToolTip = getQnaireStatusTooltip(cell, compParam);
    }
    else if (col == CommonUIConstants.WP_FINISHED_INDEX) {
      modifiedToolTip = getWpFinishedTooltip(compParam);
    }
    else if (col == CommonUIConstants.EQUAL_INDEX) {
      modifiedToolTip = getEqualTooltip(compParam);
    }
    else if (col == CommonUIConstants.SSD_CLASS_INDEX) {
      modifiedToolTip = getSsdClassTooltip(compParam);
    }

    return modifiedToolTip;
  }

  private String getCompliResultTooltip(final CompHexWithCDFParam compParam) {
    StringBuilder tooltipSb = new StringBuilder();
    if ((compParam.getCompliTooltip() != null) && !compParam.getCompliTooltip().isEmpty()) {
      tooltipSb.append("Compli Result\t: ").append(compParam.getCompliTooltip()).append("\n");
    }
    if ((compParam.getQssdTooltip() != null) && !compParam.getQssdTooltip().isEmpty()) {
      tooltipSb.append("Q-SSD Result\t: ").append(compParam.getQssdTooltip());
    }
    return tooltipSb.toString();
  }

  private String getReviewScoreTooltip(final CompHexWithCDFParam compParam, final int col) {
    return this.page.getCompHexDataHdlr().getCdrReportData().getReviewInfoTooltip(compParam.getParamName(),
        col - CommonUIConstants.REVIEW_SCORE_INDEX);
  }

  private String getQnaireStatusTooltip(final ILayerCell cell, final CompHexWithCDFParam compParam) {
    if (cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
      return "Latest review questionnaire response status";
    }
    else {
      return this.page.getCompHexDataHdlr().getCdrReportData().getQnaireRespVersStatusToolTip(compParam.getParamName());
    }
  }

  private String getWpFinishedTooltip(final CompHexWithCDFParam compParam) {
    String status =
        this.page.getCompHexDataHdlr().getCdrReportData().getWpFinishedRespStatuswithName(compParam.getParamName());
    if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), status)) {
      return CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getUiType();
    }
    if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType(), status)) {
      return CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getUiType();
    }
    return "";
  }

  private String getEqualTooltip(final CompHexWithCDFParam compParam) {
    return compParam.isEqual() ? CommonUtils.concatenate(RESULT_STATUS.EQUAL.getText(), "  ")
        : RESULT_STATUS.NOT_EQUAL.getText();
  }

  private String getSsdClassTooltip(final CompHexWithCDFParam compParam) {
    StringBuilder tooltipSb = new StringBuilder();
    if (compParam.isCompli()) {
      tooltipSb.append(ApicConstants.COMPLIANCE_PARAM).append("\n");
    }
    if (compParam.isReadOnly()) {
      tooltipSb.append(ApicConstants.READ_ONLY_PARAM).append("\n");
    }
    if (compParam.isDependantCharacteristic()) {
      tooltipSb.append(ApicConstants.DEPENDENT_PARAM).append("\n")
          .append(Arrays.stream(compParam.getDepCharsName()).collect(Collectors.joining("\n"))).append("\n");
    }
    if (compParam.isQssdParameter()) {
      tooltipSb.append(ApicConstants.QSSD_PARAM).append("\n");
    }
    if (compParam.isBlackList()) {
      tooltipSb.append(ApicConstants.BLACK_LIST_PARAM).append("\n");
    }
    return tooltipSb.toString().trim();
  }
}
