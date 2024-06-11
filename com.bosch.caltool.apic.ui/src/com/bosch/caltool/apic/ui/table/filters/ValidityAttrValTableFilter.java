/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * ICDM-2593 Table filter for Validty Attribute Value table in AddValidityAttrValDialog
 *
 * @author dja7cob
 */
public class ValidityAttrValTableFilter extends AbstractViewerFilter {

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
    return false;
  }
}
