/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * ICDM-2593 Label provider for value column of predefined attribute value table viewer
 *
 * @author dja7cob
 */
public class PredefinedValColLblProvider extends ColumnLabelProvider {

  /**
   * Map of selected predefined attribute and value
   */
  final Map<Attribute, AttributeValue> selPredefinedValMap;

  /**
   * @param selPredefinedValMap Map of selected predefined attribute and value
   */
  public PredefinedValColLblProvider(final Map<Attribute, AttributeValue> selPredefinedValMap) {
    this.selPredefinedValMap = selPredefinedValMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String result = "";
    if (element instanceof Attribute) {
      final Attribute attr = (Attribute) element;
      // check if the map is filled
      if ((null != this.selPredefinedValMap) && (null != this.selPredefinedValMap.get(attr))) {
        result = this.selPredefinedValMap.get(attr).getName();
      }
    }
    return result;
  }
}
