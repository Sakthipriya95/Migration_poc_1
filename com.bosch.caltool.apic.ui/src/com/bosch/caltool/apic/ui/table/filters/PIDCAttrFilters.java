/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

/**
 * This class handles the common filters for the PID Attribute table viewer
 */
public class PIDCAttrFilters extends AbstractViewerFilter {

  private final PidcDataHandler pidcDataHandler;

  /**
   * @param pidcDataHandler
   */
  public PIDCAttrFilters(final PidcDataHandler pidcDataHandler) {
    this.pidcDataHandler = pidcDataHandler;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof IProjectAttribute) {
      final IProjectAttribute pidcAttr = (IProjectAttribute) element;

      // Check for Attribute properties
      if (checkAttrProps(pidcAttr)) {
        return true;
      }
      // Icdm-956 Check for Attr char Value column name
      if (checkAttrValProps(pidcAttr)) {
        return true;
      }
      // Check for Project Attr properties
      return checkProjAttrProps(pidcAttr);

    }
    return false;
  }


  /**
   * @param pidcAttr
   * @return
   */
  private boolean checkAttrValProps(final IProjectAttribute pidcAttr) {
    AttributeValue attributeValue = this.pidcDataHandler.getAttributeValueMap().get(pidcAttr.getValueId());
    if (attributeValue != null) {
      return matchText(attributeValue.getCharStr());
    }
    return false;
  }


  /**
   * Checks the properties of project attribute
   *
   * @param pidcAttr Project attribute
   * @return true/false
   */
  private boolean checkProjAttrProps(final IProjectAttribute pidcAttr) {
    if (matchText(pidcAttr.getUsedFlag())) {
      return true;
    }
    if (matchText(pidcAttr.getPartNumber())) {
      return true;
    }
    if (matchText(pidcAttr.getSpecLink())) {
      return true;
    }
    if (matchText(pidcAttr.getAdditionalInfoDesc())) {
      return true;
    }

    return matchText(pidcAttr.getValue());
  }

  /**
   * Checks the properties of attribute
   *
   * @param pidcAttr attribute
   * @return true/false
   */
  private boolean checkAttrProps(final IProjectAttribute pidcAttr) {
    Attribute attribute = this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId());

    // Icdm-590
    if (matchText(attribute.getNameEng()) || matchText(attribute.getNameGer())) {
      return true;
    }
    if (matchText(attribute.getCharStr())) {
      return true;
    }
    // ICDM-2227
    if (matchText(attribute.getCreatedDate())) {
      return true;
    }
    return matchText(attribute.getDescriptionEng()) || matchText(attribute.getDescriptionGer());
  }
}