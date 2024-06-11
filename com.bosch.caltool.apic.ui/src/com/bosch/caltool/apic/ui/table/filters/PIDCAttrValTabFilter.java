/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * @author mga1cob
 */
public class PIDCAttrValTabFilter extends AbstractViewerFilter {

  /**
   * Call back Method for Filter
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for element instance Type
    if (element instanceof AttributeValue) {
      AttributeValue attrValue = (AttributeValue) element;
      // Filter attribute value
      if (matchText(attrValue.getName())) {
        return true;
      }
      // Filter attribute value description
      if (matchText(attrValue.getDescription())) {
        return true;
      }
      // Match the Value class text
      if (matchText(attrValue.getCharStr())) {
        return true;
      }
    }
    return false;
  }
}
