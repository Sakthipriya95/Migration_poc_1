/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * @author dja7cob
 */
public class ValidityValDescColLblProvider extends ColumnLabelProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String result = "";
    if (element instanceof AttributeValue) {
      // If the element if of type AttributeValue
      final AttributeValue item = (AttributeValue) element;
      // Null check
      if (CommonUtils.isNotEmptyString(item.getDescription())) {
        // Return the desciption of the attribute value
        result = item.getDescription();
      }
    }
    return result;
  }
}