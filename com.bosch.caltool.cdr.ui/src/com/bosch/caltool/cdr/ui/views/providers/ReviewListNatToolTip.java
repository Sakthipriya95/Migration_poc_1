/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.cdr.ui.editors.pages.ReviewListPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;


/**
 * This class is used to provide tooltip to PIDC review results NAT table
 *
 * @author mkl2cob
 */
public class ReviewListNatToolTip extends NatTableContentTooltip {

  /**
   * Tooltip buffer initial size
   */
  private static final int TOOLTIP_INITIAL_SIZE = 50;

  /**
   * PIDCRvwResultsDtlsPage page
   */
  private final ReviewListPage pidcRvwResPage;

  /**
   * Constructor
   *
   * @param natTable NatTable
   * @param tooltipRegions String[]
   * @param pidcRvwResultsDtlsPage PIDCRvwResultsDtlsPage
   */
  public ReviewListNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final ReviewListPage pidcRvwResultsDtlsPage) {
    super(natTable, tooltipRegions);
    this.pidcRvwResPage = pidcRvwResultsDtlsPage;
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    // get the column and row position
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);


    ILayerCell cell = this.natTable.getCellByPosition(col, row);
    if (cell != null) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());

        if ((col == CommonUIConstants.COLUMN_INDEX_1) &&
            !cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER) &&
            !cell.getConfigLabels().hasLabel(GridRegion.FILTER_ROW)) {
          // for the first column


          Object rowObject =
              this.pidcRvwResPage.getDataRprtFilterGridLayer().getBodyDataProvider().getRowObject(cell.getRowIndex());
          if (rowObject instanceof ReviewVariantModel) {
            // display tooltips for images
            ReviewVariantModel cdrRes = (ReviewVariantModel) rowObject;
            // ICDM-2258
            tooltipValue = getToolTip(cdrRes);
          }
        }

        if (tooltipValue.length() > 0) {
          // display tooltip for non empty strings
          return tooltipValue;
        }
      }
    }
    return null;
  }

  /**
   * @param reviewVariant
   * @return
   */
  public String getToolTip(final ReviewVariantModel reviewVariant) {
    String rvwType = CDRConstants.REVIEW_TYPE
        .getType(reviewVariant.getReviewResultData().getCdrReviewResult().getReviewType()).toString();
    String rvwStatus = CDRConstants.REVIEW_STATUS
        .getType(reviewVariant.getReviewResultData().getCdrReviewResult().getRvwStatus()).toString();
    String lockedStatus =
        CommonUtils.getDisplayText(CDRConstants.REVIEW_LOCK_STATUS.YES == CDRConstants.REVIEW_LOCK_STATUS
            .getType(reviewVariant.getReviewResultData().getCdrReviewResult().getLockStatus()));
    StringBuilder toolTipReviewResult = new StringBuilder(TOOLTIP_INITIAL_SIZE);
    toolTipReviewResult.append("\nReview Type : ").append(rvwType).append("\nStatus : ").append(rvwStatus)
        .append("\nLock Status : ").append(lockedStatus);

    StringBuilder toolTipVariant = new StringBuilder(TOOLTIP_INITIAL_SIZE);
    toolTipVariant.append(toolTipReviewResult);

    toolTipVariant.append("\nReview Variant : ").append(reviewVariant.getRvwVariant().getPrimaryVariantName())
        .append("\nMapped Variant : ").append(reviewVariant.getRvwVariant().getName());


    return toolTipVariant.toString();
  }

}
