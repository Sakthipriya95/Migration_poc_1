/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;

/**
 * @author bru2cob
 */
public class FeaValTabFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof SSDFeatureICDMAttrModel) {
      SSDFeatureICDMAttrModel selWp = (SSDFeatureICDMAttrModel) element;
      // division name filter
      if (matchText(selWp.getFeaValModel().getFeatureText())) {
        return true;
      }
      // resource filter
      if (matchText(selWp.getFeaValModel().getValueText())) {
        return true;
      }

    }
    // Default false
    return false;
  }

}
