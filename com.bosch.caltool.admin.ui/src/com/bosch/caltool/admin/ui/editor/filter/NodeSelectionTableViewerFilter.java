/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editor.filter;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * Table viewer filter for Node acess selection dialog
 * 
 * @author say8cob
 */
public class NodeSelectionTableViewerFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof NodeAccessInfo) {
      // when the element is NodeAccessInfo
      NodeAccessInfo nodeaccess = (NodeAccessInfo) element;

      return matchText(nodeaccess.getNodeDesc()) || matchText(nodeaccess.getNodeName());
    }
    return false;
  }

}
