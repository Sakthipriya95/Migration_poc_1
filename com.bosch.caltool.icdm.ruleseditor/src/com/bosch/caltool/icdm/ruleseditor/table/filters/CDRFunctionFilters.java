package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.Function;

/**
 * CDRFunctionFilters.java This class handles the filters for the Function selection dialog
 */
public class CDRFunctionFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Function) {
      Function fnElement = (Function) element;

      if ((fnElement.getName() != null) && matchText(fnElement.getName())) {
        return true;
      }
    }
    return false;
  }

}