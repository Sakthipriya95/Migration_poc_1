package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;

/**
 * Icdm-1215 This class handles the common filters for the Review Attr Values in the Review Info page
 */
public class RvwAttrValFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // check if the element is instance of Review Attr Value
    if (element instanceof RvwAttrValue) {
      final RvwAttrValue rvwAttrVal = (RvwAttrValue) element;
      // Filter by attr name or Attr Val Name
      return matchText(rvwAttrVal.getName()) || matchText(rvwAttrVal.getValueDisplay());
    }

    return false;

  }

}