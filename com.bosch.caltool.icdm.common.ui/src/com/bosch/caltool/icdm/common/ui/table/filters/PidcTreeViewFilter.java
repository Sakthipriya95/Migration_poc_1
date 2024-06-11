/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * @author nip4cob
 */
public class PidcTreeViewFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean selectElement(final Object element) {
    PidcTreeNode treenode = (PidcTreeNode) element;
    // match text to pidc name
    return matchText(treenode.getPidc().getName());
  }

}
