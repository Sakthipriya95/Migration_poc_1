/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editor.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.model.user.NodeAccessInfo;

/**
 * @author say8cob
 */
public class NodeSelectionTableViewerLabelProvider implements ITableLabelProvider {

  /**
   * 
   */
  private static final int NODE_DESC_COLUMN_INDEX = 1;
  /**
   * 
   */
  private static final int NODE_NAME_COLUMN_INDEX = 0;

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener arg0) {
    // Not Implemented

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not Implemented

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object arg0, final String arg1) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener arg0) {
    // Not Implemented

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object arg0, final int arg1) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String value = null;
    if (element instanceof NodeAccessInfo) {
      NodeAccessInfo nodeAccess = (NodeAccessInfo) element;
      switch (columnIndex) {
        case NODE_NAME_COLUMN_INDEX:
          // get user full name
          value = nodeAccess.getNodeName();
          break;
        case NODE_DESC_COLUMN_INDEX:
          // get user name
          value = nodeAccess.getNodeDesc();
          break;

        default:
          value = "";
          break;
      }
    }
    return value;
  }

}
