/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author dja7cob
 */
public class PredefinedAttrValTabFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for element instance Type
    if (element instanceof AttributeValue) {
      AttributeValue attrValue = (AttributeValue) element;
      // Filter attribute value
      return matchText(attrValue.getName());
    }
    if (element instanceof Attribute) {
      Attribute attr = (Attribute) element;
      // Filter attribute name
      return matchText(attr.getName());
    }
    return false;
  }

}
