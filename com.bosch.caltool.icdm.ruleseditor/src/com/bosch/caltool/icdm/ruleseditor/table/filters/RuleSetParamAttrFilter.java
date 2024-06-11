/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;

/**
 * @author UKT1COB
 */
public class RuleSetParamAttrFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for instance
    if (element instanceof RuleSetParameterAttr) {
      // get the parameter
      RuleSetParameterAttr paramAttr = (RuleSetParameterAttr) element;
      // check the param name
      if (CommonUtils.isNotNull(paramAttr.getName()) && matchText(paramAttr.getName())) {
        return true;
      }
    }

    return false;
  }

}
