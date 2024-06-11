/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * ICDM-2593 Label provider for adding predefined attribute
 *
 * @author dja7cob
 */
public class AddPredfndAttrLabelProvider extends ColumnLabelProvider {

  final int columnIndex;

  /**
   * @param columnIndex index
   */
  public AddPredfndAttrLabelProvider(final int columnIndex) {
    this.columnIndex = columnIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    // If the element if of type Attribute
    if (element instanceof Attribute) {
      final Attribute item = (Attribute) element;

      switch (this.columnIndex) {
        // Attribute name column
        case ApicUiConstants.COLUMN_INDEX_0:
          cell.setText(item.getName());
          break;
        case ApicUiConstants.COLUMN_INDEX_1:
          // Attribute description column
          // Null check
          attrDescCol(cell, item);
          break;
        default:
          break;
      }
    }
  }

  /**
   * @param cell
   * @param item
   */
  private void attrDescCol(final ViewerCell cell, final Attribute item) {
    if (!CommonUtils.isEmptyString(item.getDescription())) {
      cell.setText(item.getDescription());
    }
  }
}
