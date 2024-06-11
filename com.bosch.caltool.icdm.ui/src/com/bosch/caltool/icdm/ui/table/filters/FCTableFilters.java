package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * FCTableFilters.java This class handles the common filters for the FC page table viewer
 */
public class FCTableFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Function) {
      // If selected element is a FC
      // Filetr the FC table
      Function fnElement = (Function) element;

      // If FC name matches
      if ((fnElement.getName() != null) && matchText(fnElement.getName())) {
        return true;
      }
      // If FC version matches
      if ((fnElement.getFunctionVersion() != null) && matchText(fnElement.getFunctionVersion())) {
        return true;
      }
      // If FC ID matches
      if ((fnElement.getLongIdentifier() != null) && matchText(fnElement.getLongIdentifier())) {
        return true;
      }

    }
    return false;
  }

}