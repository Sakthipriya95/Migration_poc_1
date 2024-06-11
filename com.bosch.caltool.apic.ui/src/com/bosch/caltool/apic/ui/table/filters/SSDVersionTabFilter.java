/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.SSDProjectVersion;


/**
 * @author rgo7cob
 */
public class SSDVersionTabFilter extends AbstractViewerFilter {

  /**
   * Call back Method for Filter
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for element instance Type
    if (element instanceof SSDProjectVersion) {
      SSDProjectVersion attrValue = (SSDProjectVersion) element;
      // Filter version name
      if (matchText(attrValue.getVersionName())) {
        return true;
      }
      // Filter version number
      if (matchText(attrValue.getVersionNumber())) {
        return true;
      }
      // Match the version desc
      if (matchText(attrValue.getVersionDesc())) {
        return true;
      }
    }
    return false;
  }
}
