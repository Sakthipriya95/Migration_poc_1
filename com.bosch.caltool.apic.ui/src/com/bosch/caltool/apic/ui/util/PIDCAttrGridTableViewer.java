/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.util;

import org.eclipse.swt.widgets.Composite;

import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;


/**
 * ICDM-479
 * 
 * @author bru2cob
 */
public class PIDCAttrGridTableViewer extends CustomGridTableViewer {

  /**
   * Count of attributes of PIDCard
   */
  private int totalItemCount;


  /**
   * @param totalItemCount the totalItemCount to set
   */
  public void setTotalItemCount(final int totalItemCount) {
    this.totalItemCount = totalItemCount;
  }


  /**
   * @param parent parent control
   * @param style int style bits to create the grid
   * @param colIndex Array of Column Index for which tooltip has to be set as non-null
   * @param totalItemCount total number of attributes of the PIDcard
   */
  public PIDCAttrGridTableViewer(final Composite parent, final int style, final int[] colIndex, final int totalItemCount) {
    super(parent, style, colIndex);
    this.totalItemCount = totalItemCount;
  }


  @Override
  public void setStatusBarMessage() {
    int filteredItemCount = getGrid().getItemCount();
    updateStatusBar(this.totalItemCount, filteredItemCount);
  }

}
