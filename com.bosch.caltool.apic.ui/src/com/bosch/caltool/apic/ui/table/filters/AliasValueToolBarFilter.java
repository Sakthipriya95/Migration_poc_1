/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * @author bru2cob ICDM-1158
 */
public class AliasValueToolBarFilter extends AbstractViewerFilter {


  private final AliasDefEditorDataHandler dataHandler;
  private boolean valWithAlias = true;
  private boolean valWithoutAlias = true;


  /**
   * @param aliasDefEditorDataHandler aliasDef
   */
  public AliasValueToolBarFilter(final AliasDefEditorDataHandler aliasDefEditorDataHandler) {
    super();
    this.dataHandler = aliasDefEditorDataHandler;
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
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
    // check for instance
    if (element instanceof AttributeValue) {
      // get the attr value
      final AttributeValue attrVal = (AttributeValue) element;
      // Filter for value with alias
      if (!filterValWithAlias(attrVal)) {
        return false;
      }
      // Filter for value without alias
      if (!filterValWithoutAlias(attrVal)) {
        return false;
      }
    }
    return true;
  }


  /**
   * @param attr
   * @return
   */
  private boolean filterValWithoutAlias(final AttributeValue attrVal) {
    if (!this.valWithAlias) {
      return this.dataHandler.getValueAlias(attrVal.getId()) == null;
    }
    return true;
  }

  /**
   * @param attr
   * @return
   */
  private boolean filterValWithAlias(final AttributeValue attrVal) {
    if (!this.valWithoutAlias) {
      return this.dataHandler.getValueAlias(attrVal.getId()) != null;
    }
    return true;
  }

  /**
   * @param valWithAlias valWithAlias
   */
  public void setValWithAlias(final boolean valWithAlias) {
    this.valWithAlias = valWithAlias;

  }

  /**
   * @param valWithoutAlias valWithoutAlias
   */
  public void setValWithoutAlias(final boolean valWithoutAlias) {
    this.valWithoutAlias = valWithoutAlias;

  }


}
