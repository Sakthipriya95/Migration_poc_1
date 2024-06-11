package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;

/**
 * This class handles the common filters for the AttributesDependency table viewer in AttributesPage
 */
public class AttributesDependencyFilters extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof AttrNValueDependency) {

      // Icdm-590
      final AttrNValueDependency attrDep = (AttrNValueDependency) element;
      // match dep or value column
      return matchText(attrDep.getName()) || matchText(attrDep.getValue());

    }

    return false;
  }
}