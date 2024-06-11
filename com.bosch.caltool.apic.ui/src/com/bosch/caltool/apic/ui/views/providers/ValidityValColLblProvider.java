/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * ICDM-2593 Label provider for the value name column in addValidityAttrValDialog
 *
 * @author dja7cob
 */
public class ValidityValColLblProvider extends ColumnLabelProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String result = "";
    if (element instanceof AttributeValue) {
      // Return the attribute name
      final AttributeValue item = (AttributeValue) element;
      result = item.getName();
    }
    return result;
  }
}
