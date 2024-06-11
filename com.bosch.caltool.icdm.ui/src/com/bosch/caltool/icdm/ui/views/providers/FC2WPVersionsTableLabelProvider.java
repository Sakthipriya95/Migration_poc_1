/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import java.text.ParseException;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.grid.GridItem;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.report.Activator;

/**
 * @author dmo5cob
 */
public class FC2WPVersionsTableLabelProvider extends ColumnLabelProvider {

  /**
   * column index
   */
  private final int columnIndex;


  /**
   * Constructor
   *
   * @param columnIndex column index
   */
  public FC2WPVersionsTableLabelProvider(final int columnIndex) {
    this.columnIndex = columnIndex;
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
   * @param cell ViewerCell
   * @param version FC2WPVersion
   */
  private void getIsActiveValue(final ViewerCell cell, final FC2WPVersion version) {
    if (version.isActive()) {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), true);
      gridItem.setCheckable(cell.getVisualIndex(), version.isActive());
    }
    else {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), false);
      gridItem.setCheckable(cell.getVisualIndex(), version.isActive());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    if (element instanceof FC2WPVersion) {
      FC2WPVersion version = (FC2WPVersion) element;
      switch (this.columnIndex) {
        case 0:
          cell.setText(version.getVersionName());
          break;

        case 1:
          cell.setText(version.getDescription());
          break;

        case 2:
          getIsActiveValue(cell, version);
          break;

        case 3:
          setFormattedDate(cell, version);
          break;

        case 4:
          cell.setText(version.getCreatedUser());
          break;

        default:
          break;
      }
      cell.setForeground(getForeground(element));
    }

  }


  /**
   * @param cell
   * @param version
   */
  private void setFormattedDate(final ViewerCell cell, FC2WPVersion version) {
    String formattedDate = null;
    try {
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, version.getCreatedDate(),
          DateFormat.DATE_FORMAT_04);
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    cell.setText(formattedDate);
  }
}
