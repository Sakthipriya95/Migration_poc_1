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

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentCompHexRvwResultsPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * @author dja7cob
 */
public class DaCompHexNatToolTip extends NatTableContentTooltip {

  private final DataAssessmentCompHexRvwResultsPage page;

  /**
   * @param natTable
   * @param strings
   * @param compHexWithCdfNatPage
   */
  public DaCompHexNatToolTip(final CustomNATTable natTable, final String[] tooltipRegions,
      final DataAssessmentCompHexRvwResultsPage compHexWithCdfNatPage) {
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
    if (rowObj instanceof DaCompareHexParam) {
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
    DaCompareHexParam compParam = (DaCompareHexParam) rowObj;

    // Compli/Shape result column
    if (col == CommonUIConstants.COMPLI_RESULT_INDEX) {
      StringBuilder tooltipSb = getCompliTooltip(compParam);

      modifiedToolTip = tooltipSb.toString();
    }
    else if ((col == CommonUIConstants.REVIEW_SCORE_INDEX)) {
      if (this.page.getDataAssmntReportDataHandler().getCompHexWithCdfxDataHandler() != null) {
        modifiedToolTip = this.page.getDataAssmntReportDataHandler().getCompHexWithCdfxDataHandler().getCdrReportData()
            .getReviewInfoTooltip(compParam.getParamName(), col - CommonUIConstants.REVIEW_SCORE_INDEX);
      }
      else {
        modifiedToolTip = DATA_REVIEW_SCORE.getType(compParam.getReviewScore()).getScoreDisplay();
      }
    }
    else if ((col == CommonUIConstants.QNAIRE_STATUS)) {
      if (cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
        modifiedToolTip = "Latest review questionnaire response status";
      }
      else {
        modifiedToolTip = this.page.getDataAssmntReportDataHandler().getQnaireStatusTooltip(compParam);
      }
    }
    else if ((col == CommonUIConstants.WP_FINISHED_INDEX)) {
      String status = compParam.getWpFinishedStatus();

      if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), status) ||
          CommonUtils.isEqual(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue(), status)) {
        modifiedToolTip = CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getUiType();
      }
      if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType(), status)) {
        modifiedToolTip = CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getUiType();
      }
    }
    else if ((col == CommonUIConstants.EQUAL_INDEX)) {
      modifiedToolTip = this.page.getDataAssmntReportDataHandler().getEqualStatus(compParam);
    }
    else if (col == CommonUIConstants.SSD_CLASS_INDEX) {
      modifiedToolTip = getTooltipForSsdClass(modifiedToolTip, compParam);
    }
    return modifiedToolTip;
  }

  private StringBuilder getCompliTooltip(final DaCompareHexParam compParam) {
    StringBuilder tooltipSb = new StringBuilder();
    if ((null != compParam.getCompliTooltip()) && !compParam.getCompliTooltip().isEmpty()) {
      tooltipSb.append("Compli Result\t: ");
      tooltipSb.append(compParam.getCompliTooltip());
      tooltipSb.append("\n");
    }
    if ((null != compParam.getQssdTooltip()) && !compParam.getQssdTooltip().isEmpty()) {
      tooltipSb.append("Q-SSD Result\t: ");
      tooltipSb.append(compParam.getQssdTooltip());
    }
    return tooltipSb;
  }

  private String getTooltipForSsdClass(final String toolTip, final DaCompareHexParam compParam) {
    String modifiedToolTip = toolTip;
    if (compParam.isCompli()) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.COMPLIANCE_PARAM).concat("\n");
    }
    if (compParam.isReadOnly()) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.READ_ONLY_PARAM).concat("\n");
    }
    if (compParam.isDependantCharacteristic()) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.DEPENDENT_PARAM).concat("\n")
          .concat(Arrays.stream(compParam.getDepCharsName()).collect(Collectors.joining("\n"))).concat("\n");
    }
    if (compParam.isQssdParameter()) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.QSSD_PARAM).concat("\n");
    }
    if (compParam.isBlackList()) {
      modifiedToolTip = modifiedToolTip.concat(ApicConstants.BLACK_LIST_PARAM).concat("\n");
    }
    if (modifiedToolTip.length() > 0) {
      modifiedToolTip = modifiedToolTip.substring(0, modifiedToolTip.length() - 1);
    }
    return modifiedToolTip;
  }
}
