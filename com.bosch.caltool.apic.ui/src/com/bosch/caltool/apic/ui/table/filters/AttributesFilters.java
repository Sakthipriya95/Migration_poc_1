/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * This class handles the common filters for the Attributes table viewer
 */
public class AttributesFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean flag = false;
    if (element instanceof Attribute) {
      // Icdm-590
      final Attribute attribute = (Attribute) element;
      // ICDM-860
      flag = checkText(attribute);
    } // ICDM-1135 To display the child elements
    else if (element instanceof AttributeValue) {
      return true;
    }
    return flag;

  }

  /**
   * @param attribute attribute
   * @return True if the properties are matching.
   */
  private boolean checkText(final Attribute attribute) {
    boolean flag = false;
    // Match the Text with the Attribute properties. If any one matches return true.
    if (checkNameDesMatch(attribute) || checkValUnitMatch(attribute) || matchText(attribute.getFormat()) ||
        (matchText(attribute.getCharStr()))) {
      flag = true;
    }
    return flag;
  }

  /*
   * @param attribute
   * @return
   */
 private boolean checkValUnitMatch(Attribute attribute) {
    return matchText(attribute.getValueType()) || /* matchText(attribute.getNormalized()) || */// TODO - Deepthi
        /* matchText(attribute.getMandatory()) || */ matchText(attribute.getUnit());
  }

  /**
   * @param attribute
   * @return
   */
  private boolean checkNameDesMatch(Attribute attribute) {
    return matchText(attribute.getNameEng()) || matchText(attribute.getNameGer()) ||
        matchText(attribute.getDescriptionEng()) || matchText(attribute.getDescriptionGer());
  }
}