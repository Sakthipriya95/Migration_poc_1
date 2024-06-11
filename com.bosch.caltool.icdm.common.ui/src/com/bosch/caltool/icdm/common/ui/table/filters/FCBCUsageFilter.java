/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;


/**
 * Icdm-521 Moved the class to common UI
 *
 * @author jvi6cob
 */
public class FCBCUsageFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean returnType = false;
    if (element instanceof FCBCUsage) {
      // when the element is FCBCUsage
      final FCBCUsage fcBcUsage = (FCBCUsage) element;
      // match text with name , version , customer name or created user
      returnType = matchText(fcBcUsage.getName()) || matchText(fcBcUsage.getFuncVersion()) ||
          matchText(fcBcUsage.getCustomerName()) || matchText(fcBcUsage.getVcdmAprj()) ||
          matchText(fcBcUsage.getCreatedUser());

    }
    return returnType;
  }

}
