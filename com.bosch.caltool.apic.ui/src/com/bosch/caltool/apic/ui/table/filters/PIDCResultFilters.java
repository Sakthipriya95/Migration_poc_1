/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.apic.ui.editors.pages.PIDCSearchPage;
import com.bosch.caltool.icdm.client.bo.apic.PIDCScoutResult;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * Type filter for the results table in PIDC scout
 *
 * @author bru2cob
 */
public class PIDCResultFilters extends AbstractViewerFilter {

  /**
   * Instance of pidc search page
   */
  PIDCSearchPage pidcSearchPage;

  /**
   * @param pidcSearchPage pidcSearch page
   */
  public PIDCResultFilters(final PIDCSearchPage pidcSearchPage) {
    this.pidcSearchPage = pidcSearchPage;
  }

  /**
   * Filtering the pidc results {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    /**
     * Input for the table is PIDCards.
     */
    if (element instanceof PIDCScoutResult) {
      PIDCScoutResult result = (PIDCScoutResult) element;
      // match for pidc name
      if (matchText(result.getPidcVersion().getName())) {
        return true;
      }
      // match for pidc levels name
      for (PidcVersionAttribute versAttr : result.getLevelAttributesMap().values()) {
        if (matchText(versAttr.getValue())) {
          return true;
        }
      }
    }
    return false;
  }

}
