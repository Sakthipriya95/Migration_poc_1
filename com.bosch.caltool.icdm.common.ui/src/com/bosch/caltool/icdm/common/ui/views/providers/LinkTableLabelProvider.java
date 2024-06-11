/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;


/**
 * Label provider for links table viewer
 *
 * @author mkl2cob
 */
public class LinkTableLabelProvider implements ITableLabelProvider, IColorProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String result = "";
    if (element instanceof LinkData) {

      LinkData linkData = (LinkData) element;
      switch (columnIndex) {
        case 0:
          result = linkData.getNewLink();
          break;
        case 1:
          result = linkData.getNewDescEng();
          break;
        case 2:
          result = linkData.getNewDescGer();
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object element) {

    // ICDM-1502 show deleted links in red color
    if (element instanceof LinkData) {
      final LinkData link = (LinkData) element;
      if (link.getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
        return Display.getDefault().getSystemColor(SWT.COLOR_RED);
      }
    }
    return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getBackground(final Object element) {
    // Not applicable
    return null;
  }

}
