/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.cdr.ui.dialogs.CopyResToVarDialog;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * This is filter class for copying result to variant
 *
 * @author bru2cob
 */
public class CopyResToVarTabFilter extends AbstractViewerFilter {

  /**
   * Instance of CopyResToVarDialog
   */
  CopyResToVarDialog copyResToVarDialog;

  /**
   * @param copyResToVarDialog
   */
  public CopyResToVarTabFilter(final CopyResToVarDialog copyResToVarDialog) {
    this.copyResToVarDialog = copyResToVarDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof PidcVariant) {
      PidcVariant variant = (PidcVariant) element;
      // check for variant name
      if (matchText(variant.getName())) {
        return true;
      }
      // check for variant desc
      if (matchText(variant.getDescription())) {
        return true;
      }
      // check for pver match
      if (this.copyResToVarDialog.getVarWithoutDepMatch().contains(variant) &&
          !this.copyResToVarDialog.getVarWithDiffPver().contains(variant) &&
          matchText(CdrUIConstants.DEPENDANT_NOT_SET)) {
        return true;
      }
      // check if linked var
      if (this.copyResToVarDialog.getLinkedVars().contains(variant) && matchText(CdrUIConstants.LINKED_RESULT)) {
        return true;
      }
      if (this.copyResToVarDialog.getVarWithDiffPver().contains(variant) &&
          matchText(CdrUIConstants.DIFFERENT_SDOM_PVER)) {
        return true;
      }
    }
    return false;
  }

}
