/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.AliasDef;


/**
 * @author rgo7cob
 */
public class AliasDefinitionFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for instance
    if (element instanceof AliasDef) {
      // get the alias defintion
      AliasDef def = (AliasDef) element;
      // check the name
      if (CommonUtils.isNotNull(def.getName()) && matchText(def.getName())) {
        return true;
      }
    }

    return false;
  }

}
