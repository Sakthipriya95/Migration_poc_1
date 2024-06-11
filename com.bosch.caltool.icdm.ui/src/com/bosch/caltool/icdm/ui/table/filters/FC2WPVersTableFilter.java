/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.dialogs.FC2WPVersionSelDialog;

/**
 * @author bru2cob
 */
public class FC2WPVersTableFilter extends AbstractViewerFilter {

  /**
   * instance of FC2WPVersionSelDialog
   */
  private final FC2WPVersionSelDialog fc2wpVersionSelDialog;

  /**
   * @param fc2wpVersionSelDialog
   */
  public FC2WPVersTableFilter(final FC2WPVersionSelDialog fc2wpVersionSelDialog) {
    this.fc2wpVersionSelDialog = fc2wpVersionSelDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof FC2WPVersion) {
      FC2WPVersion selFc2wp = (FC2WPVersion) element;
      // name filter
      if (matchText(selFc2wp.getName().substring(0, selFc2wp.getName().indexOf('(')))) {
        return true;
      }
      // version name filter
      if (matchText(selFc2wp.getVersionName())) {
        return true;
      }
      // division filter
      if (matchText(
          FC2WPVersTableFilter.this.fc2wpVersionSelDialog.getAllVersionsMap().get(selFc2wp).getDivisionName())) {
        return true;
      }
    }
    return false;
  }

}
