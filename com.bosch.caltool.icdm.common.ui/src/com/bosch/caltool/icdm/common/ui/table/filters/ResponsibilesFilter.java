/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;

/**
 * @author NIP4COB
 */
public class ResponsibilesFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof A2lResponsibility) {
      A2lResponsibility resp = (A2lResponsibility) element;
      // Firstname filter
      if (matchText(resp.getLFirstName())) {
        return true;
      }
      // Last name filter
      if (matchText(resp.getLLastName())) {
        return true;
      }
      // Department filter
      if (matchText(resp.getLDepartment())) {
        return true;
      }
      // Alias Name filter
      if (matchText(resp.getAliasName())) {
        return true;
      }
    }
    return false;
  }

}
