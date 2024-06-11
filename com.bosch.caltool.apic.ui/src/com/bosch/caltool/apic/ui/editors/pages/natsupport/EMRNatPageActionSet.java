/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.EMRAssignVariantDialog;
import com.bosch.caltool.apic.ui.dialogs.EMRFileEditDescDialog;
import com.bosch.caltool.apic.ui.dialogs.EMRUploadStatusDialog;
import com.bosch.caltool.apic.ui.editors.pages.EMRNatPage;
import com.bosch.caltool.apic.ui.table.filters.EMRToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.wizards.EMRImportWizard;
import com.bosch.caltool.apic.ui.wizards.EMRImportWizardDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * The Class EMRNatToolBarActionSet.
 *
 * @author gge6cob
 */
public class EMRNatPageActionSet {

  /** The page. */
  private final EMRNatPage emrNatPage;

  /** The filter grid layer. */
  private final CustomFilterGridLayer<EmrFileMapping> filterGridLayer;

  /** The code is deleted. */
  private Action codeIsDeleted;

  /** The code is not deleted. */
  private Action codeIsNotDeleted;

  /** The code loaded w/o errors. */
  private Action importNewCodexWizard;

  private Action deleteCodexFile;

  /**
   * Instantiates a new EMR nat tool bar & right click menu action set.
   *
   * @param codexMeasurementPage the codex measurement page
   * @param filterGridLayer the filter grid layer
   */
  public EMRNatPageActionSet(final EMRNatPage codexMeasurementPage,
      final CustomFilterGridLayer<EmrFileMapping> filterGridLayer) {
    this.emrNatPage = codexMeasurementPage;
    this.filterGridLayer = filterGridLayer;
  }

  /**
   * Method to apply column filter for all columns.
   */
  public void applyColumnFilter() {
    // Toolbar filter for all Columns : IMP to trigger filter events in NAT table
    this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    this.filterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
  }

  /**
   * Checks if is deleted.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isDeleted(final ToolBarManager toolBarManager, final EMRToolBarFilters toolBarFilters) {

    this.codeIsDeleted = new Action(CommonUIConstants.FILTER_IS_DELETED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDeletedFlag(isChecked());
        applyColumnFilter();
        EMRNatPageActionSet.this.emrNatPage.getToolBarFilterCurrStateMap()
            .put(EMRNatPageActionSet.this.codeIsDeleted.getText(), EMRNatPageActionSet.this.codeIsDeleted.isChecked());
      }
    };
    this.codeIsDeleted.setChecked(false);
    this.codeIsDeleted.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_DELETED_16X16));
    toolBarManager.add(this.codeIsDeleted);
    // Adding the default state of toolbar filter
    this.emrNatPage.addToToolBarFilterMap(this.codeIsDeleted, this.codeIsDeleted.isChecked());
    this.codeIsDeleted.runWithEvent(new Event());
  }

  /**
   * Checks if is not deleted.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isNotDeleted(final ToolBarManager toolBarManager, final EMRToolBarFilters toolBarFilters) {

    this.codeIsNotDeleted = new Action(CommonUIConstants.FILTER_IS_DELETED_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotDeletedFlag(isChecked());
        applyColumnFilter();
        EMRNatPageActionSet.this.emrNatPage.getToolBarFilterCurrStateMap().put(
            EMRNatPageActionSet.this.codeIsNotDeleted.getText(), EMRNatPageActionSet.this.codeIsNotDeleted.isChecked());
      }
    };
    this.codeIsNotDeleted.setChecked(true);
    this.codeIsNotDeleted.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_NOT_DELETED_16X16));
    toolBarManager.add(this.codeIsNotDeleted);
    this.emrNatPage.addToToolBarFilterMap(this.codeIsNotDeleted, this.codeIsNotDeleted.isChecked());
  }

  /**
   * Checks if is loaded without errors.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isLoadedWithoutErrors(final ToolBarManager toolBarManager, final EMRToolBarFilters toolBarFilters) {

    /** The code loaded w/o errors. */
    Action codeIsLoadedWithoutErrors = new Action(CommonUIConstants.FILTER_IS_LOADED_WTO_ERROR, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setIsLoadedWithoutErrors(isChecked());
        applyColumnFilter();
      }
    };
    codeIsLoadedWithoutErrors.setChecked(true);
    codeIsLoadedWithoutErrors.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_IS_FOUND_16X16));
    toolBarManager.add(codeIsLoadedWithoutErrors);
    this.emrNatPage.addToToolBarFilterMap(codeIsLoadedWithoutErrors, codeIsLoadedWithoutErrors.isChecked());
  }

  /**
   * Checks if is loaded with errors.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isLoadedWithErrors(final ToolBarManager toolBarManager, final EMRToolBarFilters toolBarFilters) {

    /** The code loaded with errors. */
    Action codeIsLoadedWithErrors = new Action(CommonUIConstants.FILTER_IS_LOADED_WITH_ERROR, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setIsLoadedWithErrors(isChecked());
        applyColumnFilter();
      }
    };
    codeIsLoadedWithErrors.setChecked(true);
    codeIsLoadedWithErrors.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_NOT_FOUND_16X16));
    toolBarManager.add(codeIsLoadedWithErrors);
    this.emrNatPage.addToToolBarFilterMap(codeIsLoadedWithErrors, codeIsLoadedWithErrors.isChecked());
  }

  /**
   * Show only items of current variant.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   * @return action
   */
  public Action showOnlyItemsOfCurrentVariant(final ToolBarManager toolBarManager,
      final EMRToolBarFilters toolBarFilters) {

    /** The code Show only entries with assignment to current variant - is not Shown on PIDC level. */
    Action codeShowItemsOfCurrentVariant = new Action(CommonUIConstants.FILTER_SHOW_VARIANT_ONLY, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setShowItemsOfCurrentVariant(isChecked());
        applyColumnFilter();
      }
    };
    codeShowItemsOfCurrentVariant.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16));
    toolBarManager.add(codeShowItemsOfCurrentVariant);
    this.emrNatPage.addToToolBarFilterMap(codeShowItemsOfCurrentVariant, codeShowItemsOfCurrentVariant.isChecked());
    return codeShowItemsOfCurrentVariant;
  }

  /**
   * Adds the new codex file.
   *
   * @param toolBarManager the tool bar manager
   */
  public void addNewCodexFile(final ToolBarManager toolBarManager) {

    this.importNewCodexWizard = new Action(CommonUIConstants.FILTER_IMPORT_CODEX) {

      @Override
      public void run() {
        IEditorInput pidcEditorInput = EMRNatPageActionSet.this.emrNatPage.getEditorInput();
        EMRImportWizard codexWizard = new EMRImportWizard(pidcEditorInput);
        EMRImportWizardDialog codexWizardDialog =
            new EMRImportWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), codexWizard);

        codexWizardDialog.create();
        codexWizardDialog.open();
        setChecked(false);
      }
    };
    this.importNewCodexWizard.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.importNewCodexWizard);
    this.emrNatPage.addToToolBarFilterMap(this.importNewCodexWizard, false);
  }

  /**
   * Gets the adds the new codex file action.
   *
   * @return the adds the new codex file action
   */
  public Action getAddNewCodexFileAction() {
    return this.importNewCodexWizard;
  }

  /**
   * @param menuManager menu Manager
   * @param hasAccess access rights
   * @return action object
   */
  public Action assignVariantMenuAction(final MenuManager menuManager, final boolean hasAccess) {

    Action assignProcToVariant = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        EmrFileMapping selectedObj = EMRNatPageActionSet.this.emrNatPage.getCurrentSelection();
        if ((selectedObj == null) || !hasAccess) {
          return;
        }
        Set<Long> emrFileIds = new HashSet<>();
        emrFileIds.add(selectedObj.getEmrFile().getId());
        EMRAssignVariantDialog dialog =
            new EMRAssignVariantDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), emrFileIds);
        dialog.open();
      }
    };
    assignProcToVariant.setText(ApicUiConstants.EMR_ASSIGN_VARIANT);
    assignProcToVariant.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
    assignProcToVariant.setEnabled(hasAccess);
    menuManager.add(assignProcToVariant);
    return assignProcToVariant;
  }

  /**
   * @param menuManager menu Manager
   * @param hasAccess access rights
   * @return action object
   */
  public Action modifyDescMenuAction(final MenuManager menuManager, final boolean hasAccess) {
    Action modifyFileDescription = new Action() {

      @Override
      public void run() {
        EmrFileMapping selectedObj = EMRNatPageActionSet.this.emrNatPage.getCurrentSelection();
        if ((selectedObj == null) || !hasAccess) {
          return;
        }
        EMRFileEditDescDialog dialog = new EMRFileEditDescDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), selectedObj.getEmrFile());
        dialog.open();
      }
    };
    modifyFileDescription.setText(ApicUiConstants.EMR_MODIFY_DESC);
    modifyFileDescription.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    modifyFileDescription.setEnabled(hasAccess);
    menuManager.add(modifyFileDescription);
    return modifyFileDescription;
  }

  /**
   * @param menuManager menu Manager
   * @param hasAccess access rights
   * @return action object
   */
  public Action openExcelMenuAction(final MenuManager menuManager, final boolean hasAccess) {
    Action openExcelFile = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        EmrFileServiceClient fileClient = new EmrFileServiceClient();
        try {
          EmrFileMapping selectedObj = EMRNatPageActionSet.this.emrNatPage.getCurrentSelection();
          if ((selectedObj == null) || !hasAccess) {
            return;
          }
          String fileName = selectedObj.getEmrFile().getName();

          if (selectedObj.isFileUpload()) {

            DirectoryDialog dlg = new DirectoryDialog(Display.getCurrent().getActiveShell());
            dlg.setMessage("Select a directory to store the EMR file : " + fileName);
            String filePath = dlg.open();
            if (CommonUtils.isEmptyString(filePath)) {
              return;
            }
            String outputFilePath = fileClient.downloadEmrFile(selectedObj.getEmrFile().getId(), fileName, filePath);
            if (CommonUtils.isFileAvailable(outputFilePath)) {
              CommonUiUtils.openFile(outputFilePath);
            }
          }
          else {
            MessageDialogUtils.getErrorMessageDialog("Excel file does not exist",
                "Excel file was not uploaded for this EMR data. Hence it cannot be downloaded.");
          }
        }
        catch (Exception e) {
          CDMLogger.getInstance().error("Emission Robustness CODEX file not found : " + e.getMessage(), e,
              Activator.PLUGIN_ID);
        }
      }
    };
    openExcelFile.setText(ApicUiConstants.EMR_OPEN_EXCEL);
    openExcelFile.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16));
    openExcelFile.setEnabled(hasAccess);
    menuManager.add(openExcelFile);
    return openExcelFile;

  }

  /**
   * @param menuManager menu Manager
   * @param hasAccess access rights
   * @return action object
   */
  public Action checkUploadProtocolMenuAction(final MenuManager menuManager, final boolean hasAccess) {
    Action checkUploadProtocol = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        EmrFileMapping selectedObj = EMRNatPageActionSet.this.emrNatPage.getCurrentSelection();
        if ((selectedObj == null) || !hasAccess) {
          return;
        }
        // dialog with result of protocol
        EMRUploadStatusDialog uploadStatusDialog =
            new EMRUploadStatusDialog(Display.getCurrent().getActiveShell(), selectedObj);
        uploadStatusDialog.open();
      }
    };
    checkUploadProtocol.setText(ApicUiConstants.EMR_CHK_UPLOAD_PROTOCOL);
    checkUploadProtocol.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HISTORY_VIEW_16X16));
    checkUploadProtocol.setEnabled(hasAccess);
    menuManager.add(checkUploadProtocol);
    return checkUploadProtocol;
  }

  /**
   * @return delete Action
   */
  public Action getDeleteCodexFileAction() {
    return this.deleteCodexFile;
  }

  /**
   * @param mainToolBarManager toolbar mgr
   */
  public void deleteCodexFile(final ToolBarManager mainToolBarManager) {

    this.deleteCodexFile = new Action("Delete CODEX File") {

      @Override
      public void run() {
        String fileName = EMRNatPageActionSet.this.emrNatPage.getCurrentSelection().getEmrFile().getName();
        if (EMRNatPageActionSet.this.emrNatPage.getCurrentSelection().getEmrFile().getDeletedFlag()) {
          // already deleted
          MessageDialogUtils.getInfoMessageDialog("Delete CODEX File : " + fileName,
              "The selected CODEX file had been deleted already.");
        }
        else {
          boolean confirm =
              MessageDialogUtils.getConfirmMessageDialogWithYesNo("Confirm deleting the CODEX file : " + fileName,
                  "The selected CODEX file will be deleted. Do you still want to delete it?");
          if (confirm) {
            EmrFileServiceClient client = new EmrFileServiceClient();
            try {
              // Cloned to ensure current rowObject remains unchanged incase of error in updating the data
              EmrFile toBeDeleted = EMRNatPageActionSet.this.emrNatPage.getCurrentSelection().getEmrFile().clone();
              toBeDeleted.setDeletedFlag(true);
              EmrFile updatedFile = client.updateEmrFileDetails(toBeDeleted);
              if (updatedFile != null) {
                EMRNatPageActionSet.this.emrNatPage.getCurrentSelection().setEmrFile(updatedFile);
              }
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
            }
          }
        }
      }
    };
    this.deleteCodexFile.setEnabled(false);
    this.deleteCodexFile.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETED_ITEMS_ICON_16X16));
    mainToolBarManager.add(this.deleteCodexFile);
    this.emrNatPage.addToToolBarFilterMap(this.deleteCodexFile, false);
  }

  /**
   * @param currentSelection
   * @param hasAccess
   */
  public void enableDeleteAction(final EmrFileMapping currentSelection, final boolean hasAccess) {
    if ((currentSelection != null) && !currentSelection.getEmrFile().getDeletedFlag()) {
      this.deleteCodexFile.setEnabled(hasAccess);
    }
    else {
      this.deleteCodexFile.setEnabled(false);
    }
  }
}
