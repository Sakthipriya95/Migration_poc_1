package com.bosch.rcputils.nebula.gridviewer;

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

import java.util.Collection;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.widgets.Composite;


/**
 * CustomGridTableViewer overrides the refresh method to update the status bar after every refresh.
 * 
 * @author bru2cob
 */
public class CustomGridTableViewer extends GridTableViewer {

  private IStatusLineManager statusLineManager;

  private final String EMPTY_STRING = "";

  // ICDM-451
  /**
   * flag to indicate whether tooltip support is needed for the GridTableViewer
   */
  private final boolean multilineTooltipSupportNeeded;

  /**
   * Integer array to store the column indices for which tooltip has to be set as non-null. This is done to disable the
   * default tooltip
   */
  private int[] colIndexList;


  /**
   * @param parent
   * @param style
   */
  public CustomGridTableViewer(final Composite parent, final int style) {
    super(parent, style);
    this.multilineTooltipSupportNeeded = false;
  }

  /**
   * ICDM-451
   * 
   * @param parent parent control
   * @param style int style bits to create the grid
   * @param colIndex Array of Column Index for which tooltip has to be set as non-null
   */
  public CustomGridTableViewer(final Composite parent, final int style, final int[] colIndex) {
    super(parent, style);
    this.multilineTooltipSupportNeeded = true;
    this.colIndexList = colIndex;
  }

  /**
   * {@inheritDoc} Updation of status after refresh is done
   */
  @Override
  public void refresh() {
    super.refresh();
    setStatusBarMessage();
    // Setting column grid item's tooltip to empty string.This is to disable the default
    // tooltip
    if (this.multilineTooltipSupportNeeded) {
      GridItem[] items = getGrid().getItems();
      for (GridItem gridItem : items) {
        for (final int colIndex : this.colIndexList) {
          gridItem.setToolTipText(colIndex, EMPTY_STRING);
        }
      }
    }
  }

  /**
   * input for status line
   */
  protected void setStatusBarMessage() {
    Collection<?> items = null;
    int totalItemCount = 0;
    if ((null != getInput()) && !(getInput().toString().isEmpty())) {
      items = (Collection<?>) getInput();
    }
    if (items != null) {
      totalItemCount = items.size();
    }
    int filteredItemCount = getGrid().getItemCount();
    updateStatusBar(totalItemCount, filteredItemCount);
  }

  /**
   * Updating status
   * 
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  public void updateStatusBar(final int totalItemCount, final int filteredItemCount) {
    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount);
    buf.append(" out of " + totalItemCount + " records ");

    if (totalItemCount != filteredItemCount) {
      this.statusLineManager.setErrorMessage(buf.toString());
    }
    else {
      this.statusLineManager.setErrorMessage(null);
      this.statusLineManager.setMessage(buf.toString());
    }
    this.statusLineManager.update(true);

  }


  /**
   * @return the statusLineManager
   */
  public IStatusLineManager getStatusLineManager() {
    return this.statusLineManager;
  }

  /**
   * @param statusLineManager the statusLineManager to set
   */
  public void setStatusLineManager(final IStatusLineManager statusLineManager) {
    this.statusLineManager = statusLineManager;
  }

}
