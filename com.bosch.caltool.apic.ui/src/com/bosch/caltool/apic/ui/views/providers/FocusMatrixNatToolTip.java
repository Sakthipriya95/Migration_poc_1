/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.apic.ui.editors.pages.FocusMatrixPageNATTableSection;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author dmo5cob
 */
public class FocusMatrixNatToolTip extends NatTableContentTooltip {

  /**
   * Column index
   */
  private static final int STATIC_COL_INDEX = 4;

  /**
   * FocusMatrixPageNATTableSection instance
   */
  private final FocusMatrixPageNATTableSection focusMatrixSection;

  /**
   * FocusMatrixDataHandler
   */
  private final FocusMatrixDataHandler fmDataHandler;

  /**
   * @param natTable natTable
   * @param tooltipRegions tooltipRegions
   * @param focusMatrixPageNATTableSectionNew FocusMatrixPage
   */
  public FocusMatrixNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final FocusMatrixPageNATTableSection focusMatrixPageNATTableSectionNew) {
    super(natTable, tooltipRegions);
    this.focusMatrixSection = focusMatrixPageNATTableSectionNew;
    this.fmDataHandler = this.focusMatrixSection.getFocusMatrixPage().getDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  /*
   * Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);
    // Get the cell
    ILayerCell cell = this.natTable.getCellByPosition(col, row);

    int originalCol = LayerUtil.convertColumnPosition(this.natTable, this.natTable.getColumnPositionByX(event.x),
        ((CustomFilterGridLayer<FocusMatrixAttributeClientBO>) this.natTable.getLayer()).getDummyDataLayer());

    if (cell != null) {

      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());

      if (isVisibleContentPainter(painter)) {
        // Getting the cell value
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());

        DataLayer bodyDataLayer = this.focusMatrixSection.getUcFilterGridLayer().getDummyDataLayer();
        IRowDataProvider<FocusMatrixAttributeClientBO> bodyDataProvider =
            (IRowDataProvider<FocusMatrixAttributeClientBO>) bodyDataLayer.getDataProvider();
        // get the row position
        int rowIndexWithoutHeader =
            LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
                ((CustomFilterGridLayer<FocusMatrixAttributeClientBO>) this.natTable.getLayer()).getDummyDataLayer());

        if (cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
          if ((originalCol) >= STATIC_COL_INDEX) {

            FocusMatrixAttributeClientBO fmAttr = bodyDataProvider.getRowObject(rowIndexWithoutHeader);
            FocusMatrixUseCaseItem fmUcItem = fmAttr.getFocusmatrixUseCaseItem(originalCol - STATIC_COL_INDEX);
            tooltipValue = fmUcItem.getUseCaseItem().getToolTip();
            if (fmUcItem.getUseCaseItem() instanceof UseCaseSectionClientBO) {
              UseCaseSectionClientBO ucSectionBO = (UseCaseSectionClientBO) fmUcItem.getUseCaseItem();
              tooltipValue = ucSectionBO.getFMToolTip();
            }

          }
        }
        // if row index greater than/or equal to header row count
        else {
          FocusMatrixAttributeClientBO fmAttr = bodyDataProvider.getRowObject(rowIndexWithoutHeader);

          // if col index less than static col index
          Attribute attribute = fmAttr.getAttribute();
          if ((col != 0) && ((originalCol) < (STATIC_COL_INDEX - 2))) {
            // get attr group and attr super group from pidc data handler
            AttrGroup attrGroup =
                this.fmDataHandler.getPidcDataHandler().getAttributeGroupMap().get(attribute.getAttrGrpId());

            tooltipValue =
                CommonUiUtils.getMessage("USE_CASE", "ATTR_NAME", tooltipValue, this.fmDataHandler.getPidcDataHandler()
                    .getAttributeSuperGroupMap().get(attrGroup.getSuperGrpId()).getName(), attrGroup.getName());
          }
          // if col index greater than static col index
          if ((originalCol + 1) > STATIC_COL_INDEX) {
            if (fmAttr.isHiddenToUser()) {
              tooltipValue =
                  "Due to missing access rights ,you can neither see the color nor the comments. You need atleast read access on this PIDC";
            }
            else {
              AttributeClientBO attrBO = new AttributeClientBO(attribute);
              tooltipValue = fmAttr.getFocusmatrixUseCaseItem(originalCol - 4).getToolTip(attrBO);
            }
          }
//          value in the tool tip
          if ((originalCol) == (STATIC_COL_INDEX - 2)) {
            tooltipValue = fmAttr.getValueToolTip();
          }
//          remarks in the tool tip
          if (originalCol == CommonUIConstants.COLUMN_INDEX_3) {
            tooltipValue = fmAttr.getRemarksDisplay();
          }
        }
        // if there is tooltip string present
        if (!CommonUtils.isEmptyString(tooltipValue)) {
          return tooltipValue;
        }
      }
    }
    return null;
  }

}
