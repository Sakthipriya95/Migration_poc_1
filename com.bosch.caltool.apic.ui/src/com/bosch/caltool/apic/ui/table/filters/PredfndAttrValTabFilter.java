/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * ICDM-2593 Filter text filter for predefined attribute , value
 * tables(AddNewPredefinedAttrDialog,AddNewPredefinedValDialog)
 *
 * @author dja7cob
 */
public class PredfndAttrValTabFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for element instance Type
    if (element instanceof Attribute) {
      Attribute attr = (Attribute) element;
      // Filter attribute
      if (matchText(attr.getName()) || matchText(attr.getDescription())) {
        return true;
      }
    }
    if (element instanceof AttributeValue) {
      AttributeValue attrVal = (AttributeValue) element;
      // Filter attribute value
      if (matchText(attrVal.getName()) || matchText(attrVal.getDescription())) {
        return true;
      }
    }
    return false;
  }
}
