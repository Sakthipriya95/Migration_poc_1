/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * Type filter for data object table viewer
 *
 * @param <D> IBasicObject
 * @author bne4cob
 */
public class BasicObjectViewerTypeFilter<D extends IBasicObject> extends AbstractViewerFilter {

  /**
   * Match with name and description of IDataObject
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof IBasicObject) {
      @SuppressWarnings("unchecked")
      D basObj = (D) element;
      // check for name or description
      if (matchText(basObj.getName()) || matchText(basObj.getDescription())) {
        return true;
      }
    }
    return false;
  }

}
