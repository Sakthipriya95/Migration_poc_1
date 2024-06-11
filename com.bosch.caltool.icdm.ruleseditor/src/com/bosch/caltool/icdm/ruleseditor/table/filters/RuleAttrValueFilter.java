/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author mkl2cob
 */
public class RuleAttrValueFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // check if element is an attr value
    if (element instanceof AttributeValue) {
      AttributeValue attrValue = (AttributeValue) element;
      // return true if attr value matches the search text
      return ((attrValue.getName() != null) && matchText(attrValue.getName()));
    }
    return false;
  }

}
