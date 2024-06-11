/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * ICDM-2593 Label provider for attribute column of Predefined attribute value table viewer
 *
 * @author dja7cob
 */
public class PredefinedAttrColLblProvider extends ColumnLabelProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String result = "";
    // If the element is of type Attribute
    if (element instanceof Attribute) {
      final Attribute attr = (Attribute) element;
      result = attr.getName();
    }
    return result;
  }
}

