/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.ui.dialogs.WPDivDetailsDialog;


/**
 * @author apj4cob
 */
public class WPDivCdlTableLabelProvider implements ITableLabelProvider, IColorProvider {

  private final WPDivDetailsDialog wpDivDetailsDialog;

  /**
   * @param wpDivDetailsDialog WPDivDetailsDialog
   */
  public WPDivCdlTableLabelProvider(final WPDivDetailsDialog wpDivDetailsDialog) {
    this.wpDivDetailsDialog = wpDivDetailsDialog;
  }

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
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String result = "";
    if (element instanceof WorkpackageDivisionCdl) {

      WorkpackageDivisionCdl cdlData = (WorkpackageDivisionCdl) element;
      switch (columnIndex) {
        case 0:
          result = this.wpDivDetailsDialog.getRegionMap().get(cdlData.getRegionId()).getRegionName();
          break;
        case 1:
          result = this.wpDivDetailsDialog.getUserMap().get(cdlData.getUserId()).getDescription();
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
    WorkpackageDivisionCdl obj = (WorkpackageDivisionCdl) element;
    for (WorkpackageDivisionCdl cdl : this.wpDivDetailsDialog.getDelCdlSet()) {
      boolean flag = cdl.getRegionId().equals(obj.getRegionId()) && cdl.getUserId().equals(obj.getUserId());
      if (flag) {
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
