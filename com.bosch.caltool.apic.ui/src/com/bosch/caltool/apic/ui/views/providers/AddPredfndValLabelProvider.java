/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * @author dja7cob
 */
public class AddPredfndValLabelProvider extends CellLabelProvider {

  private final int columnIndex;

  /**
   * @param columnIndex index
   */
  public AddPredfndValLabelProvider(final int columnIndex) {
    this.columnIndex = columnIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {

    final Object element = cell.getElement();
    // If the element is of type Attributevalue
    if (element instanceof AttributeValue) {
      final AttributeValue attrVal = (AttributeValue) element;
      // Switch column index
      switch (this.columnIndex) {
        case ApicUiConstants.COLUMN_INDEX_0:
          // Attribute value name column
          cell.setText(attrVal.getName());
          break;
        case ApicUiConstants.COLUMN_INDEX_1:
          // attribute value description column
          // Check null
          attrValDescCol(cell, attrVal);
          break;
        default:
          break;
      }
    }
  }

  /**
   * @param cell
   * @param attrVal
   */
  private void attrValDescCol(final ViewerCell cell, final AttributeValue attrVal) {
    if (CommonUtils.isNotEmptyString(attrVal.getDescription())) {
      cell.setText(attrVal.getDescription());
    }
  }
}
