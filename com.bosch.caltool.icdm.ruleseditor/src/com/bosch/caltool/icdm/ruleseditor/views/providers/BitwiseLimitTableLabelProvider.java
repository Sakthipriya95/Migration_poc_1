/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views.providers;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.grid.GridItem;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.ruleseditor.dialogs.BitValue;


/**
 * @author bru2cob
 */
public class BitwiseLimitTableLabelProvider extends ColumnLabelProvider {

  /**
   * Col index
   */
  private final int columnIndex;

  /**
   * @param columnIndex columnIndex
   */
  public BitwiseLimitTableLabelProvider(final int columnIndex) {
    this.columnIndex = columnIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    if (element instanceof BitValue) {
      final BitValue item = (BitValue) element;
      switch (this.columnIndex) {
      // bit type is displayed in first column
        case ApicUiConstants.COLUMN_INDEX_0:
          cell.setText(item.getBitType());
          cell.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
          break;
        // checkbox for set,unset and undef checkboxes
        default:
          setCheckboxValues(cell, item);
          break;

      }
    }

  }

  /**
   * Set the checkbox of corresponding col
   * 
   * @param cell
   * @param item
   */
  private void setCheckboxValues(final ViewerCell cell, final BitValue item) {
    // get corresponding col number or bit index
    int bitIndex = item.getColNumOrBitIndex(cell.getColumnIndex());
    if (item.getBitsList().contains(bitIndex)) {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), true);
    }
    else {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), false);
    }
  }
}
