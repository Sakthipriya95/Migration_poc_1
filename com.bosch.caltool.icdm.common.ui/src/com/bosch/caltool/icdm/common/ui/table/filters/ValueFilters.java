package com.bosch.caltool.icdm.common.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * PIDCAttrFilters.java This class handles the common filters for the PID Attribute table viewer
 */
public class ValueFilters extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof AttributeValue) {
      // Icdm-590
      final AttributeValue attrVal = (AttributeValue) element;
      // match the Attr Val name or Actual Value.
      if (matchText(attrVal.getName())) {
        return true;
      }
      // match the Unit Value.
      if (matchText(attrVal.getUnit())) {
        return true;
      }
      if (matchText(attrVal.getDescriptionEng()) || matchText(attrVal.getDescriptionGer())) {
        return true;
      } // add description col filter condition

      // Icdm-831 made changes to the Filter
      if (matchText(attrVal.getClearingStatus())) {
        return true;
      }
    }
    // Default return false.
    return false;

  }

}