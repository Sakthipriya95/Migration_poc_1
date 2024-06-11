/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * @author bru2cob ICDM-1158
 */
public class AliasAttrToolBarFilter extends AbstractViewerFilter {

  /**
   * Defines pidc attribute mandatory filter is selected or not
   */
  private boolean attrWithAlias = true;


  /**
   * Defines pidc attribute non-mandatory filter is selected or not
   */
  private boolean attrWithoutAlias = true;
  private final AliasDefEditorDataHandler dataHandler;


  /**
   * @param dataHandler dataHandler
   */
  public AliasAttrToolBarFilter(final AliasDefEditorDataHandler dataHandler) {
    super();
    this.dataHandler = dataHandler;
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * @param attrWithAlias attrWithAlias
   */
  public void setAttrWithAlias(final boolean attrWithAlias) {
    this.attrWithAlias = attrWithAlias;

  }


  /**
   * @param attrWithoutAlias attrWithoutAlias
   */
  public void setAttrWithoutAlias(final boolean attrWithoutAlias) {
    this.attrWithoutAlias = attrWithoutAlias;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Attribute) {
      final Attribute attr = (Attribute) element;
      // Filter for alias name
      if (!filterAttrWithAlias(attr)) {
        return false;
      }
      // Filter for no alias name
      if (!filterAttrWithoutAlias(attr)) {
        return false;
      }
    }
    return true;
  }


  /**
   * @param attr
   * @return
   */
  private boolean filterAttrWithoutAlias(final Attribute attr) {
    if (!this.attrWithAlias) {
      return this.dataHandler.getAttrAlias(attr.getId()) == null;
    }
    return true;
  }

  /**
   * @param attr
   * @return
   */
  private boolean filterAttrWithAlias(final Attribute attr) {
    if (!this.attrWithoutAlias) {
      return this.dataHandler.getAttrAlias(attr.getId()) != null;
    }
    return true;
  }


}
