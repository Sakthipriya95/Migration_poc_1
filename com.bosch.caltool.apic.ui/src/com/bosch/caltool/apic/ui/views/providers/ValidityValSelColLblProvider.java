/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.SortedSet;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.grid.GridItem;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * ICDM-2593 Label provider for vadility value check box selection
 *
 * @author dja7cob
 */
public class ValidityValSelColLblProvider extends ColumnLabelProvider {

  private final SortedSet<AttributeValue> selLevAttrValues;

  /**
   * @param selLevAttrValues instamce
   */
  public ValidityValSelColLblProvider(final SortedSet<AttributeValue> selLevAttrValues) {
    this.selLevAttrValues = selLevAttrValues;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    // check for attribute value
    if (element instanceof AttributeValue) {
      final AttributeValue item = (AttributeValue) element;
      getValSelection(cell, item);
    }
  }

  /**
   * @param cell
   * @param item
   */
  private void getValSelection(final ViewerCell cell, final AttributeValue attrVal) {

    final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();

    gridItem.setChecked(cell.getVisualIndex(),
        ((null != this.selLevAttrValues) && this.selLevAttrValues.contains(attrVal)));

  }
}
