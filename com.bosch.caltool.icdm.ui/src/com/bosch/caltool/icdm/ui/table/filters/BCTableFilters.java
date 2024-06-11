package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;

/**
 * BCTableFilters.java This class handles the common filters for the BC Page table viewer
 */
public class BCTableFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean result = false;
    if (element instanceof A2LBaseComponents) {
      // If selected element is a BC
      final A2LBaseComponents bcElement = (A2LBaseComponents) element;

      // Filter the table
      result = matchText(bcElement.getBcName()) || matchText(bcElement.getBcVersion()) ||
          matchText(bcElement.getRevision()) || matchText(bcElement.getState()) || matchText(bcElement.getLongName());
    }
    return result;
  }
}