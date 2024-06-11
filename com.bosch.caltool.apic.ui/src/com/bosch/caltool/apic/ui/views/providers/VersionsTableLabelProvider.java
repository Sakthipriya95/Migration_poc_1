/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.apic.PIDCVersionsDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author dmo5cob
 */
// Changes for icdm-244 implementing IColorProvider for setting the color for pid version
public class VersionsTableLabelProvider extends ColumnLabelProvider {

  /**
   * column index
   */
  private final int columnIndex;

  /**
   * PidcVersion instance
   */
  private final PidcVersion pidcVersion;

  private final PIDCVersionsDataHandler pidcVersionsDataHandler;

  /**
   * Constructor
   *
   * @param columnIndex column index
   * @param pidcVersion PidcVersion
   * @param pidcVersionsDataHandler
   */
  public VersionsTableLabelProvider(final int columnIndex, final PidcVersion pidcVersion,
      final PIDCVersionsDataHandler pidcVersionsDataHandler) {
    this.columnIndex = columnIndex;
    this.pidcVersion = pidcVersion;
    this.pidcVersionsDataHandler = pidcVersionsDataHandler;
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
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    if (element instanceof PidcVersion) {
      PidcVersion version = (PidcVersion) element;
      switch (this.columnIndex) {
        case 0:
          cell.setText(version.getVersionName());
          break;

        case 1:
          cell.setText(version.getDescription());
          break;

        case 2:
          cell.setText(PidcVersionStatus.getStatus(version.getPidStatus()).getUiStatus());
          break;

        case 3:
          getIsActiveValue();
          break;

        case 4:
          cell.setText(this.pidcVersionsDataHandler.getCreatedDateFormatted(version.getCreatedDate()));
          break;

        case 5:
          cell.setText(version.getCreatedUser());
          break;

        default:
          break;
      }
      cell.setForeground(getForeground(element));
    }

  }

  /**
   * @param cell ViewerCell
   * @param pidcVersion PidcVersion
   */
  private void getIsActiveValue() {
    // TODO
  }


  // icdm-244
  /**
   * override the method for setting the color of the text
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object element) {

    PidcVersion versionObj = (PidcVersion) element;

    if (CommonUtils.isEqual(versionObj, this.pidcVersion)) {
      // default implemantation for display in black color
      return null;
    }
    // return gray for non selected values
    return new Color(Display.getCurrent(), 100, 100, 100);

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
