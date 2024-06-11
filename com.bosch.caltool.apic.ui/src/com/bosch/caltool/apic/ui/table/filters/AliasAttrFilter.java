/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * Filter for Attr alias table.
 *
 * @author rgo7cob
 */
public class AliasAttrFilter extends AbstractViewerFilter {

  /**
   * Data Handler
   */
  private final AliasDefEditorDataHandler dataHandler;

  /**
   * @param dataHandler Data Handler
   */
  public AliasAttrFilter(final AliasDefEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * Filtering the pidc results {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    /**
     * check for attr
     */
    if (element instanceof Attribute) {
      Attribute result = (Attribute) element;
      // Name
      if (matchText(result.getName())) {
        return true;
      }
      // type filter
      if (matchText(result.getValueType())) {
        return true;
      }

      if (matchText(this.dataHandler.getAttrAliasName(result.getId()))) {
        return true;
      }

    }
    return false;
  }
}
