/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.DataAssessmentDownloadDialog;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentDownloadServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author NDV4KOR
 */
public class DataAssessmentDownloadAction extends Action {

  /**
   * Selected DataAssessment object.
   */
  private final DaDataAssessment selectedElement;
  /**
   * File Downloaded Status
   */
  private boolean fileDwnldStatus;

  /**
   * @param selectedElement selected Element (PIDC file)
   */
  public DataAssessmentDownloadAction(final DaDataAssessment selectedElement) {
    super();
    this.selectedElement = selectedElement;
    setProperties();
  }

  /**
   * set the properties
   */
  private void setProperties() {
    setEnabled(true);
    run();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    DataAssessmentDownloadDialog dlg = new DataAssessmentDownloadDialog(Display.getCurrent().getActiveShell());
    dlg.open();
    if (dlg.getReturnCode() == Window.OK) {
      onOKPressed(dlg);
    }

  }

  /**
   * @param dlg
   * @param selectedElement
   */
  private void onOKPressed(final DataAssessmentDownloadDialog dlg) {

    if (CommonUtils.isEmptyString(dlg.getFilePath())) {
      MessageDialogUtils.getErrorMessageDialog("Selection Empty", "Please select a file to download");
    }
    else {
      String filePath = dlg.getFilePath();
      invokeWSToDownloadFiles(filePath);
      showMessageAfterDownload(filePath);
    }
  }


  /**
   * @param filePath
   * @param retMap
   */
  private void showMessageAfterDownload(final String filePath) {
    // Check download status
    if (this.fileDwnldStatus) {
      String message = "Data Assessment Report downloaded to path:" + filePath;
      CommonUiUtils.getInstance().openInfoDialogWithLink(message, filePath);
    }
  }


  /**
   * @param selectedElement
   * @param filePath
   * @return
   * @return
   */
  private boolean invokeWSToDownloadFiles(final String filePath) {
    String fileName = DataAssmntReportDataHandler.getFileName(this.selectedElement);
    try {
      final ProgressMonitorDialog progressDlg = new CustomProgressDialog(Display.getDefault().getActiveShell());
      progressDlg.run(true, true, monitor -> {
        try {
          new DaDataAssessmentDownloadServiceClient().getDataAssessementReport(this.selectedElement.getId(), fileName,
              filePath);
          this.fileDwnldStatus = true;
        }
        catch (ApicWebServiceException e) {
          this.fileDwnldStatus = false;
          CDMLogger.getInstance().errorDialog(
              "Error occurred while downloading Data Assessment report : " + e.getLocalizedMessage(), e,
              Activator.PLUGIN_ID);
        }

      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return this.fileDwnldStatus;
  }


}
