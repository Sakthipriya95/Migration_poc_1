/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.VariantMapClientModel;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * @author rgo7cob
 */
public class MapVaraintTableFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for element instance Type
    if (element instanceof VariantMapClientModel) {
      VariantMapClientModel varMapClientModel = (VariantMapClientModel) element;
      // Filter var name
      if (matchText(varMapClientModel.getVariantName())) {
        return true;
      }
      // Filter desc
      if (matchText(varMapClientModel.getVariantDesc())) {
        return true;
      }
      // Match the other var group name
      if (matchText(varMapClientModel.getOtherVarGroupName())) {
        return true;
      }

      if (matchText(String.valueOf(varMapClientModel.isMapped()))) {
        return true;
      }
    }
    return false;
  }
}

