/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentWorkPackagesPage;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * @author NDV4KOR
 */
public class DataAssessmentWorkpackageToolTip extends NatTableContentTooltip {


  private final DataAssessmentWorkPackagesPage page;

  /**
   * @param natTable
   * @param strings
   * @param dataAssessmentWorkpackages
   */
  public DataAssessmentWorkpackageToolTip(final CustomNATTable natTable, final String[] tooltipRegions,
      final DataAssessmentWorkPackagesPage dataAssessmentWorkpackages) {
    super(natTable, tooltipRegions);
    this.page = dataAssessmentWorkpackages;
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
    Object rowObj =
        this.page.getDataAssessmentWPFilterGridLayer().getBodyDataProvider().getRowObject(cell.getRowIndex());
    // set the tooltip
    if (rowObj instanceof DaWpResp) {
      modifiedToolTip = getModifiedTooltip(cell.getColumnIndex(), rowObj);
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
  private String getModifiedTooltip(final int col, final Object rowObj) {
    String modifiedToolTip = "";
    DaWpResp daWpResp = (DaWpResp) rowObj;
    if (col == DataAssessmentWorkPackagesPage.COLUMN_NUM_OVERALL_WP) {
      modifiedToolTip = getBooleanText(daWpResp.getWpReadyForProductionFlag().equals(ApicConstants.CODE_YES));
    }
    if (col == DataAssessmentWorkPackagesPage.COLUMN_NUM_WP_FINISHED) {
      modifiedToolTip = getBooleanText(daWpResp.getWpFinishedFlag().equals(ApicConstants.CODE_YES));
    }

    if (col == DataAssessmentWorkPackagesPage.COLUMN_NUM_QNAIRE_ANSWERED_BASELINED) {
      if (daWpResp.getQnairesAnsweredFlag().equals(CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.N_A.getDbType())) {
        modifiedToolTip = CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.N_A.getUiType();
      }
      else {
        modifiedToolTip = getBooleanText(daWpResp.getQnairesAnsweredFlag().equals( CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.YES.getDbType()));
      }
    }
    if (col == DataAssessmentWorkPackagesPage.COLUMN_NUM_PARAMETER_REVIEWED) {
      modifiedToolTip = getBooleanText(daWpResp.getParameterReviewedFlag().equals(ApicConstants.CODE_YES));
    }

    if (col == DataAssessmentWorkPackagesPage.COLUMN_NUM_HEX_FILE_EQUAL_TO_REVIEWS) {
      modifiedToolTip = getBooleanText(daWpResp.getHexRvwEqualFlag().equals(ApicConstants.CODE_YES));
    }
    return modifiedToolTip;
  }

  private String getBooleanText(final boolean booleanValue) {
    return booleanValue ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

}
