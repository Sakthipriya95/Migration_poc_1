/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * ICDM-2363 This class handles the filter for usecase items in project UC selection page of PIDC CREATION wizard
 *
 * @author mkl2cob
 */
public class UseCaseItemsFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof IDataObject) {
      final IDataObject ucItem = (IDataObject) element;
      // check if ucItem name matches the filter text
      if (matchText(ucItem.getName())) {
        return true;
      }
    }
    return false;

  }

}
