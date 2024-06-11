/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.table.filters.RvwWrkPkgSelToolBarFilter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author hnu1cob
 */
public class RvwWPSelToolBarActionSet {

  Action boschRespAction;

  Action customerRespAction;

  Action otherRespAction;


  /**
   * Action for bosch responsibility
   *
   * @param toolBarManager ToolBarManager
   * @param filters filter
   * @param wrkPkgTableViewer table viewer
   */
  public void showBoschRespAction(final ToolBarManager toolBarManager, final RvwWrkPkgSelToolBarFilter filters,
      final GridTableViewer wrkPkgTableViewer) {

    this.boschRespAction = new Action("Bosch Responsibility", SWT.TOGGLE) {

      @Override
      public void run() {

        filters.setBoschRespType(isChecked());


        wrkPkgTableViewer.refresh();
      }
    };

    this.boschRespAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BOSCH_RESPONSIBLE_ICON_16X16));

    this.boschRespAction.setChecked(true);
    toolBarManager.add(this.boschRespAction);
  }


  /**
   * Action for Cust responsibility
   *
   * @param toolBarManager ToolBarManager
   * @param filters filter
   * @param wrkPkgTableViewer table viewer
   */
  public void showCustomerRespAction(final ToolBarManager toolBarManager, final RvwWrkPkgSelToolBarFilter filters,
      final GridTableViewer wrkPkgTableViewer) {

    this.customerRespAction = new Action("Customer Responsibility", SWT.TOGGLE) {

      @Override
      public void run() {

        filters.setCustRespType(isChecked());

        wrkPkgTableViewer.refresh();
      }
    };

    this.customerRespAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUST_RESPONSIBLE_ICON_16X16));

    this.customerRespAction.setChecked(true);
    toolBarManager.add(this.customerRespAction);
  }

  /**
   * Action for others responsibility
   *
   * @param toolBarManager ToolBarManager
   * @param filters filter
   * @param wrkPkgTableViewer table viewer
   */
  public void showOthersRespAction(final ToolBarManager toolBarManager, final RvwWrkPkgSelToolBarFilter filters,
      final GridTableViewer wrkPkgTableViewer) {

    this.otherRespAction = new Action("Others Responsibility", SWT.TOGGLE) {

      @Override
      public void run() {

        filters.setOthersRespType(isChecked());

        wrkPkgTableViewer.refresh();
      }
    };

    this.otherRespAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.OTHERS_RESPONSIBLE_ICON_16X16));
    this.otherRespAction.setChecked(true);
    toolBarManager.add(this.otherRespAction);
  }

}
