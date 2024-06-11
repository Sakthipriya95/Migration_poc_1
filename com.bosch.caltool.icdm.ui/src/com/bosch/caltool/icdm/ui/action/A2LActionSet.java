/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.AddA2LDialog;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2lParamComparePage;
import com.bosch.caltool.icdm.ui.jobs.A2LParamCompareExportJob;
import com.bosch.caltool.icdm.ui.jobs.WPLabelExportJob;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author bru2cob
 */
public class A2LActionSet {

  /**
   * This method adds the right click option to get comp pkg data for an a2l file // ICDM-939
   *
   * @param menuMgr MenuManager instance
   * @param cmpPkg Function that is being selected
   * @param a2lEditorInput a2l editor input
   */
  public void getCompPkgDataAction(final MenuManager menuMgr, final CompPackage cmpPkg,
      final A2LContentsEditorInput a2lEditorInput) {

    final Action getCompPkgDataAction = new Action() {

      @Override
      public void run() {
        CDMLogger.getInstance().infoDialog("To Be Implemented !", Activator.PLUGIN_ID);

        // Removed CompPkgDataWizardDialog and its associated classes for SONAR Fix task 540020
        // To get the code reference please refer to 2.4.0 code
      }
    };
    getCompPkgDataAction.setText(IMessageConstants.COMP_PKG_DATA);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    getCompPkgDataAction.setImageDescriptor(imageDesc);
    menuMgr.add(getCompPkgDataAction);

  }

  /**
   * Copy RuleSet link to clipboard
   *
   * @param manager menu Manager
   * @param ruleSet RuleSet link to be copied as link
   */
  // iCDM-1249
  public void copyRuleSetLinktoClipBoard(final IMenuManager manager, final RuleSet ruleSet) {
    // Copy Link Action
    final Action copyLink = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {

        // ICDM-1649
        LinkCreator creator = new LinkCreator(ruleSet);
        try {
          creator.copyToClipBoard();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }

    };
    copyLink.setText("Copy Rule Set Link");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RULE_SET_16X16);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }

  // ICDM-1232

  /**
   * Action to Upload an A2L File
   *
   * @param manager menu manager
   * @param pidcTreeNode PidcTreeNode , to which A2L file is to be uploaded
   */
  // ICDM-773
  public void uploadA2lAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    SdomPVER sdomPver = pidcTreeNode.getSdomPver();
    Pidc pidc = pidcTreeNode.getPidc();
    final Action uploadA2lAction = new Action() {

      @Override
      public void run() {
        // Dialog to open the a2l files in the pver
        CurrentUserBO currUser = new CurrentUserBO(); // Getting Logged in User for access check
        try {
          // If the user has write or owner access for the particular PIDC, display the A2L Upload Dialog
          if (currUser.hasNodeOwnerAccess(pidc.getId()) || currUser.hasNodeWriteAccess(pidc.getId())) {
            AddA2LDialog a2lDialog = new AddA2LDialog(Display.getDefault().getActiveShell(), sdomPver);
            a2lDialog.open();
          }
          // If the user does not have write or owner access, display the warning Message
          else {
            MessageDialogUtils.getInfoMessageDialog("Upload A2L File",
                "You must have write or owner access on the PIDC to upload an A2L. Ask the PIDC owner to grant access to you !");
          }
        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
        }
      }
    };
    uploadA2lAction.setText("Upload A2L File");
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.UPLOAD_16X16);
    uploadA2lAction.setImageDescriptor(imageDesc);
    manager.add(uploadA2lAction);
  }

  /**
   * @param manager
   * @param pidcA2l
   */
  public void copytoClipBoard(final IMenuManager manager, final PidcA2l pidcA2l) {
    // Copy Link Action
    final Action copyLink = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // ICDM-1649
        LinkCreator creator = new LinkCreator(pidcA2l);
        try {
          creator.copyToClipBoard();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy A2L File Link");
    copyLink.setEnabled(true);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.A2LFILE_LINK_16X16);
    copyLink.setImageDescriptor(imageDesc);
    
    manager.add(copyLink);
  }

  /**
   * @param manager
   * @param pidcA2l
   */
  public void sendA2lFileLinkInOutlook(final IMenuManager manager, final PidcA2l pidcA2l) {
    // ICDM-1649
    // A2L file Link in outlook Action
    final Action linkAction = new SendObjectLinkAction("Send A2L File Link", pidcA2l);
    linkAction.setEnabled(true);
    manager.add(linkAction);
  }

  /**
   * @param a2LWPInfoBO a2LWPInfoBO
   */
  public void exportWPLabelPage(final A2LWPInfoBO a2LWPInfoBO) {
    A2lVariantGroup a2lVariantGroup = a2LWPInfoBO.getSelectedA2lVarGroup();
    Set<A2LWpParamInfo> excelData = a2LWPInfoBO.loadExcelData();
    // file name should be currenty selected variant group (should not be more than 40 characters)
    String fileName;
    String versionName = a2LWPInfoBO.getA2lWpDefnVersMap()
        .get(a2LWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId()).getVersionName();
    if (null != a2lVariantGroup) {
      fileName = a2LWPInfoBO.getPidcA2lBo().getA2LFileName() + "_" + a2lVariantGroup.getName() + "_" + versionName +
          "_" + "Par2WP";
    }
    else {
      fileName = a2LWPInfoBO.getPidcA2lBo().getA2LFileName() + "_" + "Default" + "_" + versionName + "_" + "Par2WP";
    }
    fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save Excel Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setOverwrite(true);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    String fileSelected = fileDialog.open();
    String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
    if (fileSelected != null) {
      final File file = new File(CommonUtils.getCompleteFilePath(fileSelected, fileExtn));
      if (CommonUtils.checkIfFileOpen(file)) {
        MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN,
            "Excel file already open, Please close the file and try export again");
        return;
      }
      WPLabelExportJob job = new WPLabelExportJob(fileSelected, fileExtn, a2LWPInfoBO, excelData);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      job.schedule();
    }
  }

  /**
   * Export A2LParamCompare
   *
   * @param a2lParamComparePage A2lParamComparePage
   */
  public void exportA2LParamCompare(final A2lParamComparePage a2lParamComparePage) {
    String fileNameStr;
    fileNameStr = "A2L_Parameter_Comparision";
    String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    String fileName = CommonUtils.concatenate(fileNameStr, "_", currentDate);
    fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save Excel Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setOverwrite(true);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    String fileSelected = fileDialog.open();
    String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
    if (fileSelected != null) {
      final File file = new File(CommonUtils.getCompleteFilePath(fileSelected, fileExtn));
      if (CommonUtils.checkIfFileOpen(file)) {
        MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN,
            "Excel file already open, Please close the file and try export again");
        return;
      }
      // sort the excel data to be exported based on parameter name
      SortedSet<A2lParamCompareRowObject> sortedExcelData =
          new TreeSet<>(A2lParamComparePage.getA2lParamCompareComparator(1));
      sortedExcelData.addAll(a2lParamComparePage.getCompareRowObjects());
      // Map of column index and header label to create excel column header
      Map<Integer, String> headerLabelMap = new HashMap<>();
      headerLabelMap.putAll(a2lParamComparePage.getPropertyToLabelMap());
      // removing icon column mapping
      headerLabelMap.remove(0);
      reorderColHeader(headerLabelMap);
      A2LParamCompareExportJob job = new A2LParamCompareExportJob(fileSelected, fileExtn, headerLabelMap,
          sortedExcelData, a2lParamComparePage.getParamCompareHandler());
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      job.schedule();
    }
  }

  /**
   * @param headerLabelMap
   */
  private void reorderColHeader(final Map<Integer, String> headerLabelMap) {
    int col = 2;
    while (col < headerLabelMap.size()) {
      headerLabelMap.put(++col, IUIConstants.WORK_PACKAGE);
      headerLabelMap.put(++col, IUIConstants.RESPONSIBILTY);
      headerLabelMap.put(++col, IUIConstants.NAME_AT_CUSTOMER);
      headerLabelMap.put(++col, IUIConstants.FUNCTION_NAME);
      headerLabelMap.put(++col, IUIConstants.FUNCTION_VERS);
      headerLabelMap.put(++col, IUIConstants.BC);
    }
  }
}
