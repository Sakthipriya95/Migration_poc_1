/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;


/**
 * @author MSP5COB
 */
public class ARCReleasedParamFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for instance
    if (element instanceof CDRResultParameter) {
      // get the parameter
      CDRResultParameter param = (CDRResultParameter) element;
      // check the param name
      if (CommonUtils.isNotNull(param.getName()) && matchText(param.getName())) {
        return true;
      }
    }

    return false;
  }

}
