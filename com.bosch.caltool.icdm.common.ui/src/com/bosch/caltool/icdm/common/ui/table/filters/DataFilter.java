/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author bru2cob
 */
public class DataFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof PidcVariant) {
      // if the element is PIDCVariant
      PidcVariant variant = (PidcVariant) element;
      if (matchText(variant.getName())) {
        // if the text matches with a2l param name
        return true;
      }
      if (matchText(variant.getDescription())) {
        // if the text matches with a2l param names
        return true;
      }
    }
    return false;
  }

}
