/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespGrpsResponse;
import com.bosch.caltool.icdm.model.a2l.WpImportFromFuncInput;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ui.dialogs.A2lWPDefnVersionListDialog;
import com.bosch.caltool.icdm.ui.dialogs.AddA2LDialog;
import com.bosch.caltool.icdm.ui.dialogs.AddA2lWpDefinitionVersionDialog;
import com.bosch.caltool.icdm.ui.dialogs.UpdateWpParamDetailsDialog;
import com.bosch.caltool.icdm.ui.editors.pages.WPLabelAssignNatPage;
import com.bosch.caltool.icdm.ui.jobs.ImportA2lWpRespJob;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpParamMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ImportA2lWpRespServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.InputFileParser;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * The Class ImportWpRespAction.Alternate A2l File Names
 */
public class WpRespActionSet extends Action {

  private static final int WP_RESP_MAP_SIZE = 1;
  private Action importWpRespFromFC2WP;
  private Action importWpRespFromExcel;

  private Action importWpParamFromA2lGrps;

  private Action addActiveVersAction;

  private Action par2wpAction;

  private Action resetWorkSplitAction;

  private Action copyVersToWokingSet;

  /** The sel LAB files list. */
  // List to store selected files
  private final List<String> selLABFilesList = new ArrayList<>();

  /** The labels. */
  // LAB file labels set
  private final Set<String> labelsfromLAB = new TreeSet<>();

  /**
   * WPLabelAssignNatPage instance
   */
  private WPLabelAssignNatPage wpLabelAssignNatPage;
  private Action importLABAction;
  private Action clearLABAction;
  private Action editAction;
  private Action createWpFromFuncAction;


  /**
   * Creates the import wp resp from FC 2 WP action.
   *
   * @param pidcVersionId the pidc version id
   * @param a2lFileId the a 2 l file id
   * @param selectedWpDefnVersionId the selected wp defn version id
   * @param createParamMapping the create param mapping
   * @param a2lWPInfoBO the a 2 l WP info BO
   * @return the action
   */
  public Action createImportWpRespFromFC2WPAction(final Long pidcVersionId, final Long a2lFileId,
      final Long selectedWpDefnVersionId, final boolean createParamMapping, final A2LWPInfoBO a2lWPInfoBO) {

    this.importWpRespFromFC2WP = new Action() {

      @Override
      public void run() {
        Job job = new ImportA2lWpRespJob(pidcVersionId, a2lFileId, selectedWpDefnVersionId, createParamMapping,
            a2lWPInfoBO, true);
        job.schedule();
      }
    };

    this.importWpRespFromFC2WP.setText("Import Work Package-Responsibility from FC2WP");
    this.importWpRespFromFC2WP.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC2WP_DEFN_16X16));
    this.importWpRespFromFC2WP.setEnabled(a2lWPInfoBO.isEditable());
    return this.importWpRespFromFC2WP;
  }

  /**
   * Creates the import from A2L Groups action.
   *
   * @param a2lWPInfoBO the A2L WP info BO
   * @return the action
   */
  public Action addActiveVersionAction(final A2LWPInfoBO a2lWPInfoBO) {
    this.addActiveVersAction = new Action() {

      @Override
      public void run() {
        AddA2lWpDefinitionVersionDialog addA2lWPDefnVersDialog =
            new AddA2lWpDefinitionVersionDialog(Display.getCurrent().getActiveShell(), a2lWPInfoBO, null);
        addA2lWPDefnVersDialog.open();
      }
    };
    this.addActiveVersAction.setText("Create active version for the WP definitions with the latest changes");
    this.addActiveVersAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_CIRCLE_16X16));
    this.addActiveVersAction
        .setEnabled(a2lWPInfoBO.isEditable() && a2lWPInfoBO.getPidcA2lBo().getPidcA2l().isWorkingSetModified());
    return this.addActiveVersAction;
  }

  /**
   * Creates the import from A2L Groups action.
   *
   * @param a2lWPInfoBO the A2L WP info BO
   * @param pidcVersion
   * @return the action
   */
  public Action par2WPAssignmentAction(final A2LWPInfoBO a2lWPInfoBO, final PidcVersion pidcVersion) {
    this.par2wpAction = new Action() {

      @Override
      public void run() {
        AddA2LDialog a2lDialog =
            new AddA2LDialog(Display.getCurrent().getActiveShell(), pidcVersion, a2lWPInfoBO, false);
        a2lDialog.open();
      }
    };
    this.par2wpAction.setText("Par2WP Assignment");
    this.par2wpAction.setEnabled(a2lWPInfoBO.isEditable());
    this.par2wpAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TRANSFER_16X16));
    return this.par2wpAction;
  }

  /**
   * Reset complete worksplit and create empty version.
   *
   * @param a2lWPInfoBO the A2L WP info BO
   * @return the action
   */
  public Action restWorkSplitAction(final A2LWPInfoBO a2lWPInfoBO) {
    this.resetWorkSplitAction = new Action() {

      @Override
      public void run() {
        try {
          boolean confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Warning",
              "All the wp params in working set will be reset to _DEFAULT_WP, Do you nevertheless want to continue?");

          if (confirm) {
            ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
            dialog.run(true, true, (final IProgressMonitor monitor) -> {

              monitor.beginTask("Resetting complete work split to _DEFAULT_WP...", 100);
              monitor.worked(20);

              try {

                new A2lWpResponsibilityServiceClient().resetWorkSplit(a2lWPInfoBO.getPidcA2lBo().getPidcA2lId());
              }

              catch (ApicWebServiceException exp) {
                CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
              }
              monitor.worked(100);
              monitor.done();
            });
          }
        }

        catch (InvocationTargetException exp) {
          CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
        catch (InterruptedException exp) {
          Thread.currentThread().interrupt();
          CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    this.resetWorkSplitAction.setText("Reset Work Split");
    this.resetWorkSplitAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RESET_16X16));
    this.resetWorkSplitAction.setEnabled(a2lWPInfoBO.isEditable());
    return this.resetWorkSplitAction;
  }

  /**
   * Creates the action to copy another version to working set
   *
   * @param a2lWPInfoBO a2lWPInfoBO
   * @return action
   */
  public Action copyVersToWorkingSetAction(final A2LWPInfoBO a2lWPInfoBO) {
    setCopyVersToWokingSet(new Action() {

      @Override
      public void run() {
        A2lWPDefnVersionListDialog addA2lWPDefnVersDialog =
            new A2lWPDefnVersionListDialog(Display.getCurrent().getActiveShell(), a2lWPInfoBO, null, true);
        addA2lWPDefnVersDialog.open();
        if (addA2lWPDefnVersDialog.isOkPressed && MessageDialogUtils.getConfirmMessageDialogWithYesNo(
            "Copy WP Version to Working Set",
            "By copying the selected version \'" + addA2lWPDefnVersDialog.getSelectedA2lWpDefnVersion().getName() +
                "\' to \'Working Set\',\nthe Work Package-Responsibility and Parameter mappings in 'Working Set' will be overwritten.\n" +
                "Do you want to proceed? ")) {
          copyWithProgressBar(addA2lWPDefnVersDialog.getSelectedA2lWpDefnVersion(), a2lWPInfoBO.getWorkingSetId());
        }
      }
    });
    getCopyVersToWokingSet().setText("Copy Work Package-Responsibility and Parameter mappings from another version");
    getCopyVersToWokingSet().setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.COPY_16X16));
    getCopyVersToWokingSet().setEnabled(a2lWPInfoBO.isEditable());
    return getCopyVersToWokingSet();
  }

  /**
   * @param a2lWpDefnVersion
   * @param WorkingSetId
   */
  private void copyWithProgressBar(final A2lWpDefnVersion a2lWpDefnVersion, final Long WorkingSetId) {

    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Copying version \'" + a2lWpDefnVersion.getName() + "\' to 'Working Set'...", 100);
        monitor.worked(50);
        List<CopyPar2WpFromA2lInput> inputDataList = new ArrayList<>();
        CopyPar2WpFromA2lInput inputData = new CopyPar2WpFromA2lInput();
        inputData.setDescPidcA2lId(a2lWpDefnVersion.getPidcA2lId());
        inputData.setSourceWpDefVersId(a2lWpDefnVersion.getId());
        inputData.setDestWpDefVersId(WorkingSetId);
        inputData.setOverWriteAssigments(true);
        inputData.setCopyToWS(true);
        inputDataList.add(inputData);
        try {
          new A2lWpDefinitionVersionServiceClient().copyA2lWpResp(inputDataList);
        }
        catch (ApicWebServiceException excep) {
          CDMLogger.getInstance().errorDialog(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance()
          .error("Error in invoking thread to open progress bar for copying version to workingset  !", e);
      Thread.currentThread().interrupt();
    }
  }


  /**
   * Creates the import from A2L Groups action.
   *
   * @param a2lWPInfoBO the A2L WP info BO
   * @return the action
   */
  public Action createImportFromA2lGroupsAction(final A2LWPInfoBO a2lWPInfoBO) {
    this.importWpParamFromA2lGrps = new Action() {

      @Override
      public void run() {
        if (a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().size() == WP_RESP_MAP_SIZE) {
          importWithProgressBar(a2lWPInfoBO);
        }
        else {
          CDMLogger.getInstance().infoDialog(
              "Import not possible since there are work packages and responsibilities already defined.",
              Activator.PLUGIN_ID);
        }
      }
    };
    this.importWpParamFromA2lGrps.setText("Import WP-Responsibility and mappings from A2L Groups");
    this.importWpParamFromA2lGrps.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WP_GROUP_28X30));
    this.importWpParamFromA2lGrps.setEnabled(a2lWPInfoBO.canImportGroups());
    return this.importWpParamFromA2lGrps;
  }

  /**
   * @param a2lWPInfoBO
   */
  private void importWithProgressBar(final A2LWPInfoBO a2lWPInfoBO) {
    String dialogMessage = getDisplayMessage("A2L_IMPORT", "A2L_GROUP_IMPORT_INFO_MSG");
    if (MessageDialogUtils.getConfirmMessageDialogWithYesNo(
        "Import Workpackage-Responsibility and mappings from A2L Groups", dialogMessage)) {
      ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      try {
        dialog.run(true, true, monitor -> {
          monitor.beginTask("Importing Work Package-Responsibility and mappings from A2L Groups ...", 100);
          monitor.worked(50);
          importWpRespGrps(a2lWPInfoBO);
          monitor.worked(100);
          monitor.done();
        });
      }
      catch (InvocationTargetException | InterruptedException e) {
        CDMLogger.getInstance().error(
            "Error in invoking thread to open progress bar for A2L Work package Responsibility Groups import !", e);
        Thread.currentThread().interrupt();
      }
    }
  }


  /**
   * @param groupName
   * @param name
   * @return
   */
  private String getDisplayMessage(final String groupName, final String name) {
    try {
      return (new CommonDataBO()).getMessage(groupName, name);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    return "";
  }


  /**
   * @param a2lWPInfoBO
   */
  private void importWpRespGrps(final A2LWPInfoBO a2lWPInfoBO) {
    try {
      ImportA2lWpRespGrpsResponse response = new ImportA2lWpRespServiceClient().a2lWpRespGrpsImport(
          a2lWPInfoBO.getPidcA2lBo().getPidcA2l(), a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId());
      Set<A2lWpResponsibility> wpRespPalSet = response.getWpRespPalSet();
      A2lWpResponsibility a2lWpResp = (A2lWpResponsibility) wpRespPalSet.toArray()[0];
      if ((wpRespPalSet.size() == WP_RESP_MAP_SIZE) &&
          (ApicConstants.DEFAULT_A2L_WP_NAME.equalsIgnoreCase(a2lWpResp.getName()))) {
        CDMLogger.getInstance().infoDialog(
            "Work Package-Responsibility and mappings could not be imported since the A2L does not contain groups.",
            Activator.PLUGIN_ID);
      }
      else {
        CDMLogger.getInstance().infoDialog(getDisplayMessage("A2L_IMPORT", "A2L_GROUP_IMPORT_SUCCESS_MSG"),
            Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }


  /**
   * Creates the import wp resp from excel action.
   *
   * @param pidcVersionId the pidc version id
   * @param a2lFileId the a 2 l file id
   * @param selectedWpDefnVersionId the selected wp defn version id
   * @param createParamMapping the create param mapping
   * @param a2lWPInfoBO the a 2 l WP info BO
   * @return the action
   */
  public Action createImportWpRespFromExcelAction(final Long pidcVersionId, final Long a2lFileId,
      final Long selectedWpDefnVersionId, final A2LWPInfoBO a2lWPInfoBO) {
    this.importWpRespFromExcel = new Action() {

      @Override
      public void run() {
        Job job = new ImportA2lWpRespJob(pidcVersionId, a2lFileId, selectedWpDefnVersionId, true, a2lWPInfoBO, false);
        job.schedule();
      }
    };
    this.importWpRespFromExcel.setText("Import Work Package-Responsibility from Excel File");
    this.importWpRespFromExcel.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.IMPORT_PID_28X30));
    this.importWpRespFromExcel.setEnabled(a2lWPInfoBO.isEditable());
    return this.importWpRespFromExcel;
  }

  /**
   * @param wpLabelAssignNatPage
   * @return
   */
  public Action createLabAction(final WPLabelAssignNatPage wpLabelAssignNatPage) {
    this.wpLabelAssignNatPage = wpLabelAssignNatPage;
    this.importLABAction = new Action() {

      @Override
      public void run() {
        importLABFile();
        setFilterStatus();
      }
    };
    this.importLABAction.setText("Load LAB File");
    final ImageDescriptor importLAB = ImageManager.getImageDescriptor(ImageKeys.UPLOAD_LAB_16X16);
    this.importLABAction.setImageDescriptor(importLAB);
    this.importLABAction.setEnabled(this.wpLabelAssignNatPage.getA2lWPInfoBO().isEditable());
    return this.importLABAction;
  }

  /**
   * Import LAB file.
   */
  protected void importLABFile() {
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
    fileDialog.setText("Import LAB file");
    fileDialog.setFilterExtensions(new String[] { "*.lab" });
    fileDialog.setFilterNames(new String[] { "LAB File(*.lab)" });
    final String selectedFile = fileDialog.open();
    if (selectedFile != null) {
      runImportLABFile(fileDialog);
    }
  }

  /**
   * Run import LAB file.
   *
   * @param fileDialog the file dialog
   */
  private void runImportLABFile(final FileDialog fileDialog) {
    Runnable busyRunnable = () -> {
      try {
        InputFileParser fileParser;
        String filterPath = fileDialog.getFilterPath();
        String[] filesNames = fileDialog.getFileNames();
        File selFile;
        for (String fileName : filesNames) {
          selFile = new File(filterPath, fileName);
          String filePath = selFile.getAbsolutePath();
          fileParser = new InputFileParser(ParserLogger.getInstance(), filePath);
          fileParser.parse();
          List<String> fileLabels = fileParser.getLabels();

          // get labels from functions
          List<String> funcList = fileParser.getFunctions();
          if (CommonUtils.isNotEmpty(funcList)) {
            funcList.stream().forEach(func -> fileLabels
                .addAll(this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lFileInfoBo().getParamListfromFunction(func)));
          }

          // get labels from groups
          List<String> grpList = fileParser.getGroups();
          if (CommonUtils.isNotEmpty(grpList)) {
            fileLabels
                .addAll(this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lFileInfoBo().getParamListFromGroups(grpList));
          }

          this.selLABFilesList.add(fileName);
          this.labelsfromLAB.addAll(fileLabels);
        }
        if (!this.labelsfromLAB.isEmpty()) {
          for (String paramName : this.labelsfromLAB) {
            A2LParameter a2lParam =
                this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lFileInfoBo().getA2lParamByName(paramName);
            if ((a2lParam != null) &&
                this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lWParamInfoMap().containsKey(a2lParam.getParamId())) {
              this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lWParamInfoMap().get(a2lParam.getParamId())
                  .setLABParam(true);
            }
          }
        }
        else {
          CDMLogger.getInstance().info("No valid parameters found in LAB file. Please verify the LAB file!",
              Activator.PLUGIN_ID);
        }
        // ICDM-1140
        this.wpLabelAssignNatPage.getToolBarActionSet().getWithoutLABParamAction().setChecked(false);
        this.wpLabelAssignNatPage.getToolBarActionSet().getWithoutLABParamAction().run();
      }
      catch (ParserException e) {
        CDMLogger.getInstance().error("Error while importing Lab File", e, Activator.PLUGIN_ID);
      }
    };
    BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
  }

  private void setFilterStatus() {
    StringBuilder toolTip = new StringBuilder();
    toolTip.append("Clear data");
    if (!this.selLABFilesList.isEmpty()) {
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithLABParamAction().setEnabled(true);
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithoutLABParamAction().setEnabled(true);
      toolTip.append(" from : \n");
      for (String fileName : this.selLABFilesList) {
        toolTip.append(fileName + "\n");
      }
      this.clearLABAction.setToolTipText(toolTip.toString());
      this.clearLABAction.setEnabled(true);
    }
    else {
      this.wpLabelAssignNatPage.getToolBarFilters().setWithLabParam(true);
      this.wpLabelAssignNatPage.getToolBarFilters().setWithoutLabParam(true);
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithLABParamAction().setEnabled(false);
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithoutLABParamAction().setEnabled(false);
      this.clearLABAction.setToolTipText("Clear LAB file");
      this.clearLABAction.setEnabled(false);
    }
  }

  /**
   * @param wpLabelAssignNatPage2
   * @return
   */
  public Action createClearLabAction(final WPLabelAssignNatPage wpLabelAssignNatPage2) {
    this.clearLABAction = new Action() {

      @Override
      public void run() {
        clearLABFiles();
        setFilterStatus();
      }
    };
    this.clearLABAction.setText("Clear LAB File");
    final ImageDescriptor clearLAB = ImageManager.getImageDescriptor(ImageKeys.CLEAR_LAB_16X16);
    this.clearLABAction.setImageDescriptor(clearLAB);
    this.clearLABAction.setEnabled(false);
    return this.clearLABAction;
  }

  /**
   * Clear LAB files.
   */
  protected void clearLABFiles() {
    Runnable busyRunnable = () -> {
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithLABParamAction().setChecked(true);
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithoutLABParamAction().setChecked(true);
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithLABParamAction().run();
      this.wpLabelAssignNatPage.getToolBarActionSet().getWithoutLABParamAction().run();

      for (String paramName : this.labelsfromLAB) {
        A2LParameter a2lParam =
            this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lFileInfoBo().getA2lParamByName(paramName);
        if ((a2lParam != null) &&
            this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lWParamInfoMap().containsKey(a2lParam.getParamId())) {
          this.wpLabelAssignNatPage.getA2lWPInfoBO().getA2lWParamInfoMap().get(a2lParam.getParamId())
              .setLABParam(false);
        }
      }
      this.selLABFilesList.clear();
      this.labelsfromLAB.clear();
      this.wpLabelAssignNatPage.refreshNatTable();
    };
    BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);

  }


  /**
   * @return the importWpRespFromFC2WP
   */
  public Action getImportWpRespFromFC2WP() {
    return this.importWpRespFromFC2WP;
  }


  /**
   * @param importWpRespFromFC2WP the importWpRespFromFC2WP to set
   */
  public void setImportWpRespFromFC2WP(final Action importWpRespFromFC2WP) {
    this.importWpRespFromFC2WP = importWpRespFromFC2WP;
  }


  /**
   * @return the importWpRespFromExcel
   */
  public Action getImportWpRespFromExcel() {
    return this.importWpRespFromExcel;
  }


  /**
   * @param importWpRespFromExcel the importWpRespFromExcel to set
   */
  public void setImportWpRespFromExcel(final Action importWpRespFromExcel) {
    this.importWpRespFromExcel = importWpRespFromExcel;
  }

  /**
   * @return the importWpParamFromA2lGrps
   */
  public Action getImportWpParamFromA2lGrps() {
    return this.importWpParamFromA2lGrps;
  }


  /**
   * @param importWpParamFromA2lGrps the importWpParamFromA2lGrps to set
   */
  public void setImportWpParamFromA2lGrps(final Action importWpParamFromA2lGrps) {
    this.importWpParamFromA2lGrps = importWpParamFromA2lGrps;
  }


  /**
   * @return the addActiveVersAction
   */
  public Action getAddActiveVersAction() {
    return this.addActiveVersAction;
  }


  /**
   * @param addActiveVersAction the addActiveVersAction to set
   */
  public void setAddActiveVersAction(final Action addActiveVersAction) {
    this.addActiveVersAction = addActiveVersAction;
  }

  /**
   * @return the importLABAction
   */
  public Action getImportLABAction() {
    return this.importLABAction;
  }


  /**
   * @param importLABAction the importLABAction to set
   */
  public void setImportLABAction(final Action importLABAction) {
    this.importLABAction = importLABAction;
  }


  /**
   * @return the editAction
   */
  public Action getEditAction() {
    return this.editAction;
  }


  /**
   * @param editAction the editAction to set
   */
  public void setEditAction(final Action editAction) {
    this.editAction = editAction;
  }

  /**
   * @return the createWpFromFuncsAction
   */
  public Action getCreateWpFromFuncsAction() {
    return this.createWpFromFuncAction;
  }


  /**
   * @param wpLabelAssignNatPage WPLabelAssignNatPage instance
   * @return
   */
  public IAction createEditAction(final Composite composite, final WPLabelAssignNatPage wpLabelAssignNatPage) {
    this.wpLabelAssignNatPage = wpLabelAssignNatPage;
    this.editAction = new Action("Edit") {

      @Override
      public void run() {
        A2LWPInfoBO a2lWPInfoBO = wpLabelAssignNatPage.getA2lWPInfoBO();
        A2lVariantGroup selectedA2lVarGroup = a2lWPInfoBO.getSelectedA2lVarGroup();
        A2LWpParamInfo paramInfo = (A2LWpParamInfo) wpLabelAssignNatPage.getSelectedObj();
        try {

          paramInfo = createWpRespAndMapping(selectedA2lVarGroup, paramInfo, a2lWPInfoBO);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp);
        }
        UpdateWpParamDetailsDialog editPar2WPDialog = new UpdateWpParamDetailsDialog(composite.getShell(), paramInfo,
            a2lWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId(), a2lWPInfoBO);
        editPar2WPDialog.open();
      }
    };
    // set icon for button
    this.editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editAction.setEnabled(false);

    return this.editAction;
  }

  /**
   * @return the copyVersToWokingSet
   */
  public Action getCopyVersToWokingSet() {
    return this.copyVersToWokingSet;
  }

  /**
   * @param copyVersToWokingSet the copyVersToWokingSet to set
   */
  public void setCopyVersToWokingSet(final Action copyVersToWokingSet) {
    this.copyVersToWokingSet = copyVersToWokingSet;
  }

  /**
   * @param wpLabelAssignNatPage
   * @param selectedA2lVarGroup
   * @param paramInfo
   * @param a2lWPInfoBO
   * @return
   * @throws ApicWebServiceException
   */
  public A2LWpParamInfo createWpRespAndMapping(final A2lVariantGroup selectedA2lVarGroup,
      final A2LWpParamInfo paramInfo, final A2LWPInfoBO a2lWPInfoBO)
      throws ApicWebServiceException {
    if (((paramInfo.getVariantGroupId() == null) || (paramInfo.getA2lWpParamMappingId() == null)) &&
        (selectedA2lVarGroup != null)) {
      A2lWpResponsibility respTobecreated = null;
      A2lWpResponsibility wpResp = a2lWPInfoBO.getWpRespPalForA2lWpMapping(paramInfo);
      Map<Long, A2lWpResponsibility> wpRespMap = a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap();

      for (A2lWpResponsibility a2lWpResp : wpRespMap.values()) {
        Long variantGrpId = a2lWpResp.getVariantGrpId();
        if ((variantGrpId != null) && variantGrpId.equals(selectedA2lVarGroup.getId()) &&
            wpResp.getA2lWpId().equals(a2lWpResp.getA2lWpId())) {
          respTobecreated = a2lWpResp;
          break;
        }
      }
      String wpName = a2lWPInfoBO.getWPName(paramInfo);
      respTobecreated = createWpResp(selectedA2lVarGroup, paramInfo, a2lWPInfoBO, respTobecreated, wpResp, wpName);
      if (respTobecreated != null) {
        return createNewWpMapping(selectedA2lVarGroup, paramInfo, respTobecreated, a2lWPInfoBO);
      }

    }

    return paramInfo;
  }

  /**
   * @param selectedA2lVarGroup
   * @param paramInfo
   * @param a2lWPInfoBO
   * @param respTobecreated
   * @param wpResp
   * @param wpName
   * @return
   * @throws ApicWebServiceException
   */
  private A2lWpResponsibility createWpResp(final A2lVariantGroup selectedA2lVarGroup, final A2LWpParamInfo paramInfo,
      final A2LWPInfoBO a2lWPInfoBO, A2lWpResponsibility respTobecreated, final A2lWpResponsibility wpResp,
      final String wpName)
      throws ApicWebServiceException {
    if ((respTobecreated == null) && !ApicConstants.DEFAULT_A2L_WP_NAME.equals(wpName)) {
      respTobecreated = new A2lWpResponsibility();
      respTobecreated.setA2lRespId(paramInfo.getA2lRespId());
      respTobecreated.setA2lWpId(wpResp.getA2lWpId());
      respTobecreated.setWpDefnVersId(selectedA2lVarGroup.getWpDefnVersId());
      respTobecreated.setVariantGrpId(selectedA2lVarGroup.getId());

      List<A2lWpResponsibility> wpRespList = new ArrayList<>();
      wpRespList.add(respTobecreated);
      A2lWpResponsibilityServiceClient wpRespClient = new A2lWpResponsibilityServiceClient();
      Set<A2lWpResponsibility> wpRespCreated = wpRespClient.create(wpRespList, a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
      for (A2lWpResponsibility a2lWpResponsibility : wpRespCreated) {
        respTobecreated = a2lWpResponsibility;

      }

    }
    return respTobecreated;
  }

  /**
   * @param selectedA2lVarGroup
   * @param paramInfo
   * @param respTobecreated
   * @param wpRespId
   * @param a2lWPInfoBO
   * @return
   * @throws ApicWebServiceException
   */
  private A2LWpParamInfo createNewWpMapping(final A2lVariantGroup selectedA2lVarGroup, final A2LWpParamInfo paramInfo,
      final A2lWpResponsibility respTobecreated, final A2LWPInfoBO a2lWPInfoBO)
      throws ApicWebServiceException {
    A2lWpParamMapping paramInfotoCreate = new A2lWpParamMapping();
    paramInfotoCreate.setParA2lRespId(respTobecreated.getA2lRespId());
    paramInfotoCreate.setParamId(paramInfo.getParamId());
    paramInfotoCreate.setWpDefnVersionId(selectedA2lVarGroup.getWpDefnVersId());

    paramInfotoCreate.setWpNameCust(paramInfo.getWpNameCust());
    paramInfotoCreate.setWpNameCustInherited(paramInfo.isWpNameInherited());
    paramInfotoCreate.setWpRespId(respTobecreated.getId());

    A2lWpParamMappingUpdateModel model = new A2lWpParamMappingUpdateModel();
    model.getA2lWpParamMappingToBeCreated().add(paramInfotoCreate);
    A2lWpParamMappingServiceClient client = new A2lWpParamMappingServiceClient();
    A2lWpParamMappingUpdateModel updatedModel =
        client.updateA2lWpParamMapping(model, a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
    A2LWpParamInfo paramInfoCreated = null;
    Map<Long, A2lWpParamMapping> createdA2lWpParamMapping = updatedModel.getCreatedA2lWpParamMapping();

    for (A2lWpParamMapping mapping : createdA2lWpParamMapping.values()) {
      paramInfoCreated = a2lWPInfoBO.setDetails(mapping);

    }
    return paramInfoCreated;
  }

  /**
   * @param a2lWPInfoBO A2LWPInfoBO
   * @return Action
   */
  public Action createWpsFromFunctionsAction(final A2LWPInfoBO a2lWPInfoBO) {

    this.createWpFromFuncAction = new Action() {

      @Override
      public void run() {
        if (!a2lWPInfoBO.getDetailsStrucModel().getA2lVariantGrpMap().isEmpty()) {
          CDMLogger.getInstance().warnDialog(
              "Variant Groups other than '<DEFAULT>' is available. In this case, 'Creation of  Work Packages from Functions' is not supported for now",
              Activator.PLUGIN_ID);
        }
        else {
          boolean confirmImport = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
              "Create WP from Functions - Confirmation",
              "For each function in the A2L, one work package is created.\nParameters are assigned to the new work package.\n\nClick 'OK' to proceed.");
          if (confirmImport) {
            boolean confirmDeleteUnusedWp = false;
            boolean confirmKeepExistingResp = false;
            if (a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().size() != WP_RESP_MAP_SIZE) {
              MessageDialogUtils.getInstance();
              confirmDeleteUnusedWp =
                  MessageDialogUtils.getConfirmMessageDialogWithYesNo("Delete existing WP - Confirmation",
                      "Do you want to delete existing WPs that are not needed any longer?");
            }
            if (a2lWPInfoBO.isRespAvailableInParamLevel()) {
              confirmKeepExistingResp =
                  MessageDialogUtils.getConfirmMessageDialogWithYesNo("Keep existing Responsibilities - Confirmation",
                      "Do you want to keep the existing responsibilities on parameter level?");
            }
            WpImportFromFuncInput importWpFromFuncInput = new WpImportFromFuncInput();
            importWpFromFuncInput.setWpDefVersId(a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId());
            importWpFromFuncInput.setDeleteUnusedWPs(confirmDeleteUnusedWp);
            importWpFromFuncInput.setKeepExistingResp(confirmKeepExistingResp);
            createWpFromFuncWithProgressBar(importWpFromFuncInput);
          }
        }

      }
    };
    this.createWpFromFuncAction.setText("Create Work Packages from Functions");
    this.createWpFromFuncAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WP_28X30));
    this.createWpFromFuncAction.setEnabled(a2lWPInfoBO.isEditable());
    return this.createWpFromFuncAction;

  }

  /**
   * @param importformFuncInput WpImportFromFuncInput
   */
  protected void createWpFromFuncWithProgressBar(final WpImportFromFuncInput importformFuncInput) {

    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Creating Work Package from Functions ...", 100);
        monitor.worked(50);
        try {
          new A2lWpDefinitionVersionServiceClient().importWpFromFunctions(importformFuncInput);
          CDMLogger.getInstance().infoDialog("Work Packages created from Functions successfully", Activator.PLUGIN_ID);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance()
          .error("Error in invoking thread to open progress bar for Creation of Work Packages from Functions !", e);
      Thread.currentThread().interrupt();
    }

  }

  /**
   * @return the par2wpAction
   */
  public Action getPar2wpAction() {
    return this.par2wpAction;
  }


  /**
   * @param par2wpAction the par2wpAction to set
   */
  public void setPar2wpAction(final Action par2wpAction) {
    this.par2wpAction = par2wpAction;
  }

  /**
   * @return resetWorkSplitAction
   */
  public Action getResetWorkSplitAction() {
    return this.resetWorkSplitAction;
  }


}

