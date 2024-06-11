/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.model.bc.SdomFc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class BCFCTableFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof CompPkgBc) {
      final CompPkgBc cmpPkgBC = (CompPkgBc) element;
      if (matchText(cmpPkgBC.getBcSeqNo().toString())) {
        return true;
      }
      if (matchText(cmpPkgBC.getBcName())) {
        return true;
      }
    }
    else if (element instanceof CompPkgFc) {
      final CompPkgFc cmpPkgFC = (CompPkgFc) element;
      if (matchText(cmpPkgFC.getFcName())) {
        return true;
      }
      // TODO long name
      if (matchText(cmpPkgFC.getFcName())) {
        return true;
      }

    }
    // For SDoM BC
    else if (element instanceof SdomBc) {
      return filterBCElement(element);
    }
    // For SDoM FC
    else if (element instanceof SdomFc) {
      return filterFCElement(element);
    }
    return false;
  }

  /**
   * @param element
   * @return whether the element is available
   */
  private boolean filterFCElement(final Object element) {

    final SdomFc sdomFc = (SdomFc) element;
    // check if FC name matches
    if (matchText(sdomFc.getName())) {
      return true;
    }
    // check if FC description matches
    return matchText(sdomFc.getDescription());

  }

  /**
   * @param element
   * @return whether the element is available
   */
  private boolean filterBCElement(final Object element) {
    final SdomBc sdomBc = (SdomBc) element;
    // check if BC name matches
    if (matchText(sdomBc.getName())) {
      return true;
    }
    // check if BC description matches
    return matchText(sdomBc.getDescription());
  }


}
