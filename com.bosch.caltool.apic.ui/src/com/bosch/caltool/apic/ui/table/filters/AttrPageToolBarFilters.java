/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Icdm-832 tool bar filter in Attributes Page
 *
 * @author rgo7cob
 */
public class AttrPageToolBarFilters extends AbstractViewerFilter {


  /**
   * Defines pidc attribute not used flag filter is selected or not
   */
  // By default this flag will switched on
  private boolean attrwithNotClr = true;

  /**
   * Defines pidc attribute not used flag filter is selected or not
   */
  // By default this flag will switched on
  private boolean attrWithoutNotClr = true;

  private final List<Long> unClearedAttrIds = new ArrayList<>();

  /**
   * Constructor for the attribute tool bar filter
   */
  public AttrPageToolBarFilters() {
    super();
    fetchUnClearedAttrs();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);

  }


  /**
   * @return the attrwithNotClr
   */
  public boolean isAttrwithNotClr() {
    return this.attrwithNotClr;
  }


  /**
   * @return the attrWithoutNotClr
   */
  public boolean isAttrWithoutNotClr() {
    return this.attrWithoutNotClr;
  }

  /**
   * set the Attr with Not cleared Value flag
   *
   * @param attrwithNotClr attrwithNotClr
   */
  public void setAttrWithNotClr(final boolean attrwithNotClr) {
    this.attrwithNotClr = attrwithNotClr;
  }

  /**
   * set the Attr without Not cleared Value flag
   *
   * @param attrWithoutNotClr attrWithoutNotClr
   */
  public void setAttrWithoutNotClr(final boolean attrWithoutNotClr) {
    this.attrWithoutNotClr = attrWithoutNotClr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    final Attribute attr = (Attribute) element;
    if (!isAttrwithNotClr() && this.unClearedAttrIds.contains(attr.getId())) {
      return false;
    }
    return !(!isAttrWithoutNotClr() && !this.unClearedAttrIds.contains(attr.getId()));
  }


  /**
   *
   */
  private void fetchUnClearedAttrs() {
    AttributeServiceClient client = new AttributeServiceClient();

    try {
      List<Long> unClearedAttributeIds = client.getUnClearedAttrIds();
      if (null != unClearedAttributeIds) {
        this.unClearedAttrIds.addAll(unClearedAttributeIds);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    // ICDM-278
    setFilterText(filterText, false);
  }


}
