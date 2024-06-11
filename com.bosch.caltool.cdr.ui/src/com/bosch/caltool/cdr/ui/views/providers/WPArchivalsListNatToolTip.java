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

import com.bosch.caltool.cdr.ui.editors.pages.WPArchivalsListPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.WpArchival;


/**
 * This class is used to provide tooltip to WP archivals NAT table
 *
 * @author ukt1cob
 */
public class WPArchivalsListNatToolTip extends NatTableContentTooltip {

  /**
   * Tooltip buffer initial size
   */
  private static final int TOOLTIP_INITIAL_SIZE = 50;

  /**
   * WPArchivalListPage page
   */
  private final WPArchivalsListPage wpArchivalPage;

  /**
   * Constructor
   *
   * @param natTable NatTable
   * @param tooltipRegions String[]
   * @param wpArchivalPage wpArchivalPage
   */
  public WPArchivalsListNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final WPArchivalsListPage wpArchivalPage) {
    super(natTable, tooltipRegions);
    this.wpArchivalPage = wpArchivalPage;
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
              this.wpArchivalPage.getWpArchivalFilterGridLayer().getBodyDataProvider().getRowObject(cell.getRowIndex());
          if (rowObject instanceof WpArchival) {
            WpArchival wpArchival = (WpArchival) rowObject;
            tooltipValue = getToolTip(wpArchival);
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
   * @param wpArchival wpArchival
   * @return tooltip
   */
  public String getToolTip(final WpArchival wpArchival) {
    StringBuilder toolTipWPArchival = new StringBuilder(TOOLTIP_INITIAL_SIZE);

    toolTipWPArchival.append("Baseline Name : ").append(wpArchival.getBaselineName()).append("\nPIDC Version : ")
        .append(wpArchival.getPidcVersFullname()).append("\nA2L File : ").append(wpArchival.getA2lFilename())
        .append("\nVariant : ").append(wpArchival.getVariantName()).append("\nWorkpackage : ")
        .append(wpArchival.getWpName()).append("\nResponsible : ").append(wpArchival.getRespName());


    return toolTipWPArchival.toString();
  }

}
