/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;

/**
 * @author say8cob
 */
public class ReviewResultParameterFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof CDRResultParameter) {
      final CDRResultParameter parameter = (CDRResultParameter) element;
      if (matchText(parameter.getName())) {
        return true;
      }
    }
    return false;
  }


}
