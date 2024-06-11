/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * Filter for Attr alias table.
 *
 * @author rgo7cob
 */
public class AliasValueFilter extends AbstractViewerFilter {

  /**
   * data Handler
   */
  private AliasDefEditorDataHandler dataHandler;

  /**
   * @param dataHandler data Handler
   */
  public AliasValueFilter(final AliasDefEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * Filtering the pidc results {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // get the attr value
    if (element instanceof AttributeValue) {
      AttributeValue result = (AttributeValue) element;
      // check for value
      if (matchText(result.getNameRaw())) {
        return true;
      }
      // Check for value alias
      if (matchText(this.dataHandler.getValueAliasName(result.getId()))) {
        return true;
      }

    }
    return false;
  }
}
