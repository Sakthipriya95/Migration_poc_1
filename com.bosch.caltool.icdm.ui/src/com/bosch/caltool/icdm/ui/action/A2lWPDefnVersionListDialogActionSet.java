/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.ui.dialogs.A2lWPDefnVersionListDialog;
import com.bosch.caltool.icdm.ui.dialogs.AddA2lWpDefinitionVersionDialog;
import com.bosch.caltool.icdm.ui.sorters.A2lWpDefVersionSorter;
import com.bosch.caltool.icdm.ui.views.A2LDetailsPage;

/**
 * @author pdh2cob
 */
public class A2lWPDefnVersionListDialogActionSet {


  private final A2LWPInfoBO a2lWpInfoBo;

  private Action addAction;

  private Action editAction;

  private final A2lWPDefnVersionListDialog a2lWPDefnVersionListDialog;

  /**
   * @param a2lWPDefnVersionListDialog - A2lWPDefnVersionListDialog instance
   */
  public A2lWPDefnVersionListDialogActionSet(final A2lWPDefnVersionListDialog a2lWPDefnVersionListDialog) {
    this.a2lWPDefnVersionListDialog = a2lWPDefnVersionListDialog;
    this.a2lWpInfoBo = this.a2lWPDefnVersionListDialog.getA2lWPInfoBO();
  }


  /**
   * @param toolBarManager instance of toolbar manager
   * @param shell parent shell
   * @param a2lDetailsPage
   */
  public void addA2lWpDefnVersionAction(final ToolBarManager toolBarManager, final Shell shell,
      final A2LDetailsPage a2lDetailsPage) {

    this.addAction = new Action() {

      @Override
      public void run() {
        AddA2lWpDefinitionVersionDialog dialog =
            new AddA2lWpDefinitionVersionDialog(shell, A2lWPDefnVersionListDialogActionSet.this.a2lWpInfoBo, null);
        dialog.open();

        A2lWPDefnVersionListDialogActionSet.this.a2lWPDefnVersionListDialog.refreshTable();
        if (a2lDetailsPage != null) {
          ComboViewer wpDefnVersComboViewer = a2lDetailsPage.getWpDefnVersComboViewer();
          A2lWpDefVersionSorter sorter = new A2lWpDefVersionSorter();
          List<A2lWpDefnVersion> values =
              new ArrayList<>(A2lWPDefnVersionListDialogActionSet.this.a2lWpInfoBo.getA2lWpDefnVersMap().values());
          Collections.sort(values, sorter);
          wpDefnVersComboViewer.setInput(values);
          final ISelection selection = new StructuredSelection(a2lDetailsPage.getSelectedA2lWpDefVers());
          wpDefnVersComboViewer.setSelection(selection);
        }
      }
    };
    this.addAction.setText("Add Workpackage Definition Version");
    this.addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addAction.setEnabled(this.a2lWpInfoBo.isWPInfoModifiable());
    toolBarManager.add(this.addAction);

  }

  /**
   * @param toolBarManager instance of toolbar manager
   * @param shell parent shell
   * @param a2lDetailsPage
   * @param selectedA2lWPDefnVers - selected A2lWpDefinitionVersion
   */
  public void editA2lWpDefnVersionAction(final ToolBarManager toolBarManager, final Shell shell,
      final A2LDetailsPage a2lDetailsPage) {

    this.editAction = new Action() {

      @Override
      public void run() {
        A2lWpDefnVersion selectedA2lWpDefnVersion =
            A2lWPDefnVersionListDialogActionSet.this.a2lWPDefnVersionListDialog.getSelectedA2lWpDefnVersion();
        if (selectedA2lWpDefnVersion != null) {
          AddA2lWpDefinitionVersionDialog dialog = new AddA2lWpDefinitionVersionDialog(shell,
              A2lWPDefnVersionListDialogActionSet.this.a2lWpInfoBo, selectedA2lWpDefnVersion);
          dialog.open();
          A2lWPDefnVersionListDialogActionSet.this.a2lWPDefnVersionListDialog.setSelectedA2lWpDefnVersion(null);
          A2lWPDefnVersionListDialogActionSet.this.a2lWPDefnVersionListDialog.refreshTable();
          if (a2lDetailsPage != null) {
            ComboViewer wpDefnVersComboViewer = a2lDetailsPage.getWpDefnVersComboViewer();
            A2lWpDefVersionSorter sorter = new A2lWpDefVersionSorter();
            List<A2lWpDefnVersion> values =
                new ArrayList<>(A2lWPDefnVersionListDialogActionSet.this.a2lWpInfoBo.getA2lWpDefnVersMap().values());
            Collections.sort(values, sorter);
            wpDefnVersComboViewer.setInput(values);
            final ISelection selection = new StructuredSelection(a2lDetailsPage.getSelectedA2lWpDefVers());
            wpDefnVersComboViewer.setSelection(selection);
            A2lWPDefnVersionListDialogActionSet.this.a2lWPDefnVersionListDialog
                .setSelectedA2lWpDefnVersion(selectedA2lWpDefnVersion);

          }
        }
      }
    };
    this.editAction.setText("Edit Workpackage Definition Version");
    this.editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editAction.setEnabled(false);
    toolBarManager.add(this.editAction);

  }


  /**
   * @return the addAction
   */
  public Action getAddAction() {
    return this.addAction;
  }


  /**
   * @return the editAction
   */
  public Action getEditAction() {
    return this.editAction;
  }

}
