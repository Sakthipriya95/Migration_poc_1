/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;

/**
 * @author nip4cob
 */
public class PidcNameFilter extends PatternFilter {


  // Added PidcTreeViewFilter in order to filter the Pidcs with hyphen
  PidcTreeViewFilter pidcTreeViewFilter = new PidcTreeViewFilter();

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPattern(final String patternString) {
    this.pidcTreeViewFilter.setFilterText(patternString);
    super.setPattern(patternString);
  }

  @Override
  protected boolean isParentMatch(final Viewer tviewer, final Object element) {
    // restrict search on other other parent
    if ((element instanceof PidcTreeNode) &&
        ((PidcTreeNode) element).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
      return super.isParentMatch(tviewer, element);
    }
    return false;
  }

  @Override
  protected boolean isLeafMatch(final Viewer tviewer, final Object element) {
    if ((element instanceof PidcTreeNode) &&
        ((PidcTreeNode) element).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
      if (!this.pidcTreeViewFilter.getFilterText().isEmpty()) {
        return this.pidcTreeViewFilter.selectElement(element);
      }
      return true;
    }
    return false;
  }
}
