/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.MonicaFileSelDialog;
import com.bosch.caltool.cdr.ui.dialogs.MonicaFolderSelectionDialog;
import com.bosch.caltool.cdr.ui.dialogs.MonicaReviewDialog;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.cdr.MonicaFileData;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author and4cob
 */
public class MonicaReviewActionSet {

  /**
   * Action to add MoniCa Report File
   */
  private Action actnAddFile;

  /**
   * Action to edit MoniCa Report File
   */
  private Action actnEditFile;

  /**
   * Action to remove the selected MoniCa Report file from the table viewer
   */
  private Action actnDelFile;

  /**
   * Action to add Variant
   */
  private Action actnSelectVariant;

  /**
   * Action to open Variant Compare editor
   */
  private Action actnVarCompare;


  /**
   * Action to select all the monica excel files in a folder
   */
  private Action addFolderSelection;

  /**
   * Title label
   */
  private static final String FILE_SELECTION_TITLE_LABEL = "MoniCa File Selection";

  /**
   *
   */
  private final MonicaReviewDialog monicaReviewDialog;

  /**
   * @param dialog instance of MonicaReviewDialog
   */
  public MonicaReviewActionSet(final MonicaReviewDialog dialog) {
    this.monicaReviewDialog = dialog;
  }


  /**
   * This method defines the action for adding a MoniCa Report File
   *
   * @param toolBarManager instance
   */
  public void createFileAddAction(final ToolBarManager toolBarManager) {

    this.actnAddFile = new Action("Add File") {

      @Override
      public void run() {
        final MonicaFileSelDialog editFileDialog = new MonicaFileSelDialog(Display.getDefault().getActiveShell(),
            MonicaReviewActionSet.this.monicaReviewDialog, false);
        editFileDialog.open();
        MonicaReviewActionSet.this.monicaReviewDialog.enableDisableOKBtn();
      }
    };
    // Image for add action
    this.actnAddFile.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.actnAddFile.setEnabled(true);
    toolBarManager.add(this.actnAddFile);
  }

  /**
   * This method defines the action for adding from folder option
   *
   * @param toolBarManager
   */
  public void createAddFolderSelectionOption(final ToolBarManager toolBarManager) {

    this.addFolderSelection = new Action("Add From Folder") {

      @Override
      public void run() {
        MonicaFolderSelectionDialog dlg = new MonicaFolderSelectionDialog(Display.getCurrent().getActiveShell(),
            MonicaReviewActionSet.this.monicaReviewDialog);

        dlg.setTitle(FILE_SELECTION_TITLE_LABEL);
        dlg.open();
        MonicaReviewActionSet.this.monicaReviewDialog.enableDisableOKBtn();

      }
    };
    // Image for add action
    this.addFolderSelection.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BROWSE_BUTTON_ICON));
    this.addFolderSelection.setEnabled(true);
    toolBarManager.add(this.addFolderSelection);

  }

  /**
   * This method defines the action for editing the fields
   *
   * @param toolBarManager instance
   */
  public void createFileEditAction(final ToolBarManager toolBarManager) {
    this.actnEditFile = new Action("Edit File") {

      @Override
      public void run() {
        final MonicaFileSelDialog editFileDialog = new MonicaFileSelDialog(Display.getDefault().getActiveShell(),
            MonicaReviewActionSet.this.monicaReviewDialog, true);
        editFileDialog.open();
        MonicaReviewActionSet.this.monicaReviewDialog.enableDisableOKBtn();
      }
    };
    // Image for edit action
    this.actnEditFile.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.actnEditFile.setEnabled(false);
    toolBarManager.add(this.actnEditFile);
  }

  /**
   * @param toolBarManager This method defines the action for removing an entry from the table viewer
   */
  public void deleteFileAction(final ToolBarManager toolBarManager) {


    this.actnDelFile = new Action("Remove File") {

      @Override
      public void run() {

        IStructuredSelection selection =
            (IStructuredSelection) MonicaReviewActionSet.this.monicaReviewDialog.getTabViewer().getSelection();
        if (!selection.isEmpty()) {
          for (Object monicaFileData : selection.toArray()) {
            MonicaFileData dataProvider = (MonicaFileData) monicaFileData;
            Set<MonicaFileData> monicaFileDataSet =
                MonicaReviewActionSet.this.monicaReviewDialog.getMonicaFileDataSet();

            monicaFileDataSet.remove(dataProvider);
            Iterator<MonicaFileData> iterator = monicaFileDataSet.iterator();
            int i = 1;
            while (iterator.hasNext()) {
              MonicaFileData provider = iterator.next();
              provider.setIndex(i);
              i++;
            }
            if (monicaFileDataSet.isEmpty()) {
              MonicaReviewActionSet.this.disableActions();
            }
          }
          MonicaReviewActionSet.this.monicaReviewDialog.getTabViewer().refresh();
          MonicaReviewActionSet.this.monicaReviewDialog.enableDisableOKBtn();
        }

        disableActions();

        return;
      }
    };
    // Image for delete action
    this.actnDelFile.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.actnDelFile.setEnabled(false);
    toolBarManager.add(this.actnDelFile);
  }

  /**
   *
   */
  private void disableActions() {
    this.actnEditFile.setEnabled(false);
    this.actnDelFile.setEnabled(false);
    this.actnSelectVariant.setEnabled(false);
  }


  /**
   * This method defines the action for editing the fields
   *
   * @param toolBarManager instance
   */
  public void selectVariantAction(final ToolBarManager toolBarManager) {
    toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    this.actnSelectVariant = new Action("Select Variant") {

      @Override
      public void run() {
        IStructuredSelection selection =
            (IStructuredSelection) MonicaReviewActionSet.this.monicaReviewDialog.getTabViewer().getSelection();
        MonicaReviewActionSet.this.monicaReviewDialog.fillVariantName(selection);
      }
    };
    // Image for select variant action
    this.actnSelectVariant.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
    this.actnSelectVariant.setEnabled(false);
    toolBarManager.add(this.actnSelectVariant);
  }


  /**
   * This method defines the action for editing the fields
   *
   * @param toolBarManager instance
   */
  public void variantCompareAction(final ToolBarManager toolBarManager) {
    toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    this.actnVarCompare = new Action("Open Variant Compare") {


      @Override
      public void run() {
        PidcDataHandler pidcDataHandler = new PidcDataHandler();
        PidcDetailsLoader loader = new PidcDetailsLoader(pidcDataHandler);
        Long pidcVersId = MonicaReviewActionSet.this.monicaReviewDialog.getPidcA2l().getPidcVersId();
        pidcDataHandler = loader.loadDataModel(pidcVersId);

        try {
          // fetch all the variants
          Map<Long, PidcVariant> pidcVarMap = (new PidcVariantServiceClient())
              .getA2lMappedVariants(MonicaReviewActionSet.this.monicaReviewDialog.getPidcA2l().getId());
          List<IProjectObject> pidcVarList = new ArrayList<>(pidcVarMap.values());

          PidcVersion pidcVersion = (new PidcVersionServiceClient()).getById(pidcVersId);
          PidcVersionBO pidcVersionBO = new PidcVersionBO(pidcVersion, pidcDataHandler);

          MonicaReviewActionSet.this.monicaReviewDialog.getMinimizedShell();
          (new PIDCActionSet()).openCompareEditor(pidcVarList, pidcVersionBO);
        }

        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };

    // Image for variant compare action
    this.actnVarCompare.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_16X16));
    this.actnVarCompare.setEnabled(this.monicaReviewDialog.varSelectionNeeded());
    toolBarManager.add(this.actnVarCompare);
  }


  /**
   * @return the addFileAction
   */
  public Action getAddFileAction() {
    return this.actnAddFile;
  }

  /**
   * @return the editFileAction
   */
  public Action getEditFileAction() {
    return this.actnEditFile;
  }

  /**
   * @return the deleteFileAction
   */
  public Action getDeleteFileAction() {
    return this.actnDelFile;
  }


  /**
   * @return the actnAddVariant
   */
  public Action getActnSelectVariant() {
    return this.actnSelectVariant;
  }


  /**
   * @param actnSelectVariant the actnSelectVariant to set
   */
  public void setActnSelectVariant(final Action actnSelectVariant) {
    this.actnSelectVariant = actnSelectVariant;
  }


  /**
   * @return the actnVarCompare
   */
  public Action getActnVarCompare() {
    return this.actnVarCompare;
  }


  /**
   * @param actnVarCompare the actnVarCompare to set
   */
  public void setActnVarCompare(final Action actnVarCompare) {
    this.actnVarCompare = actnVarCompare;
  }


}
