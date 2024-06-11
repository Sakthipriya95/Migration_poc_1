/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.CdrReportListPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * This class is to display the tooltips for review report nat table
 *
 * @author mkl2cob
 */
public class CdrReportNatToolTip extends NatTableContentTooltip {


  /**
   * DataRvwReportPage
   */
  private final CdrReportListPage dataRvwReportPage;

  /**
   * @param natTable NatTable
   * @param dataRvwReportPage DataRvwReportPage
   * @param strings tooltip regions
   */
  public CdrReportNatToolTip(final NatTable natTable, final String[] strings,
      final CdrReportListPage dataRvwReportPage) {
    super(natTable, strings);
    this.natTable = natTable;
    this.dataRvwReportPage = dataRvwReportPage;
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    // get the column position
    int col = this.natTable.getColumnPositionByX(event.x);

    int originalCol = LayerUtil.convertColumnPosition(this.natTable, this.natTable.getColumnPositionByX(event.x),
        ((CustomFilterGridLayer<A2LParameter>) this.natTable.getLayer()).getDummyDataLayer());

    // get the row position
    int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
        ((CustomFilterGridLayer<A2LParameter>) this.natTable.getLayer()).getDummyDataLayer());
    ILayerCell cell = this.natTable.getCellByPosition(col, this.natTable.getRowPositionByY(event.y));
    // get the tooltip value
    if (cell != null) {
      return getToolTipText(originalCol, row, cell);
    }
    return null;
  }

  /**
   * Get the tooltip value
   *
   * @param originalCol originalCol
   * @param row row
   * @param cell cell
   * @return
   */
  private String getToolTipText(final int originalCol, final int row, final ILayerCell cell) {
    // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
    ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
    if (isVisibleContentPainter(painter)) {
      A2LParameter a2lParam =
          this.dataRvwReportPage.getDataRprtFilterGridLayer().getBodyDataProvider().getRowObject(row);
      String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
      // ICDM-2045
      if (originalCol == CommonUIConstants.LATEST_A2L_COL_INDEX) {
        tooltipValue = "A2L Version of the latest review result";
      }
      else if (originalCol == CommonUIConstants.LATEST_FUNC_COL_INDEX) {
        tooltipValue = "Function Version of the latest review result";
      }
      // questionnaire response status
      else if (originalCol == CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX) {
        tooltipValue = getToolTipForRvwQnaireStatusColumn(cell, a2lParam);
      }

      /** Tooltip for wp_finished column */
      else if (originalCol == CommonUIConstants.WP_FINISHED_COL_INDEX) {
        tooltipValue = getTooltipForWPFinishedColumn(a2lParam, tooltipValue);
      }

      // ICDM-2487
      else if (originalCol == ApicUiConstants.COLUMN_INDEX_0) {
        tooltipValue = getTooltipForParamColumn(a2lParam);
      }
      else {
        if (cell.getConfigLabels().getLabels().contains(CDRConstants.TICK) &&
            (originalCol != CommonUIConstants.WP_FINISHED_COL_INDEX)) {
          // if the label 'TICK' is present
          tooltipValue = "Reviewed";
        }
        tooltipValue = getReviewToolTip(originalCol, row, cell, tooltipValue);
      }
      if (tooltipValue.length() > 0) {
        // display tooltip for non empty strings
        return tooltipValue;
      }
    }
    return null;

  }

  /**
   * @param cell
   * @param a2lParam
   * @return
   */
  private String getToolTipForRvwQnaireStatusColumn(final ILayerCell cell, final A2LParameter a2lParam) {
    String tooltipValue;
    if (cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
      tooltipValue = "Latest review questionnaire response status";
    }
    else {
      tooltipValue =
          this.dataRvwReportPage.getCdrReportDataHandler().getQnaireRespVersStatusToolTip(a2lParam.getParamId());
    }
    return tooltipValue;
  }

  /**
   * @param a2lParam
   * @param tooltipValueDefault
   * @return
   */
  private String getTooltipForWPFinishedColumn(final A2LParameter a2lParam, final String tooltipValueDefault) {
    String tooltipValue = tooltipValueDefault;
    String status =
        this.dataRvwReportPage.getEditorInput().getReportData().getWpFinishedRespStatus(a2lParam.getParamId());

    if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType(), status)) {
      tooltipValue = CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getUiType();
    }
    if (CommonUtils.isEqual(CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType(), status)) {
      tooltipValue = CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getUiType();
    }
    return tooltipValue;
  }

  /**
   * @param a2lParam
   * @return
   */
  private String getTooltipForParamColumn(final A2LParameter a2lParam) {
    String tooltipValue;
    // Tooltip creation based on parameter type
    // Tooltip is appended for all parameter Types, a parameter satisfies
    StringBuilder builder = new StringBuilder();
    if (a2lParam.isComplianceParam()) {
      builder.append(ApicConstants.COMPLIANCE_PARAM);
      // Appending a new line after every Parameter type for tooltip
      builder.append('\n');
    }
    if (a2lParam.getCharacteristic().isReadOnly()) {
      builder.append(ApicConstants.READ_ONLY_PARAM);
      builder.append('\n');
    }
    if (a2lParam.getCharacteristic().isDependentCharacteristic()) {
      builder.append(ApicConstants.DEPENDENT_PARAM).append('\n');
      String[] characteristicNames = a2lParam.getCharacteristic().getDependentCharacteristic().getCharacteristicName();
      builder.append(Arrays.stream(characteristicNames).collect(Collectors.joining("\n")));
      builder.append('\n');
    }
    if (a2lParam.isQssdParameter()) {
      builder.append(ApicConstants.QSSD_PARAM);
      builder.append('\n');
    }
    if (a2lParam.isBlackList()) {
      builder.append(ApicConstants.BLACK_LIST_PARAM);
      builder.append('\n');
    }
    tooltipValue = builder.toString();
    // Removing the extra new line character at the end of the string
    if (tooltipValue.length() > 0) {
      tooltipValue = tooltipValue.substring(0, tooltipValue.length() - 1);
    }
    return tooltipValue;
  }

  /**
   * Tooltip for review results
   *
   * @param originalCol originalCol
   * @param row row
   * @param cell cell
   * @param tooltipValue2
   * @return
   */
  private String getReviewToolTip(final int originalCol, final int row, final ILayerCell cell,
      final String tooltipValue2) {
    String tooltipValue = tooltipValue2;
    // Checking if the column is dynamic column
    if ((originalCol >= CdrReportListPage.STATIC_COL_INDEX) &&
        !cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {

      // display customised tooltip for dynamic columns
      A2LParameter a2lParam =
          this.dataRvwReportPage.getDataRprtFilterGridLayer().getBodyDataProvider().getRowObject(row);
      CdrReportEditorInput input = this.dataRvwReportPage.getEditorInput();
      // get the report data instance
      CdrReportDataHandler reportData = input.getReportData();
      tooltipValue =
          reportData.getReviewInfoTooltip(a2lParam.getName(), originalCol - (CdrReportListPage.STATIC_COL_INDEX));

    }
    return tooltipValue;
  }
}
