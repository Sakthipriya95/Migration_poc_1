/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.WPArchivalListEditorInput;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.FILE_ARCHIVAL_STATUS;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.ws.rest.client.cdr.WpFilesDownloadServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author ukt1cob
 */
public class WPArchivalsDownloadAction extends Action {


  private final WPArchivalListEditorInput wpArchivalListEditorInput;

  /**
   * @param wpArchivalListEditorInput iStructuredSelection
   */
  public WPArchivalsDownloadAction(final WPArchivalListEditorInput wpArchivalListEditorInput) {
    super("Downloading Workpackage Archival file...");
    this.wpArchivalListEditorInput = wpArchivalListEditorInput;
    setProperties();
  }


  @Override
  public void run() {

    Set<WpArchival> wpArchivals = new HashSet<>();

    IStructuredSelection selectedWPArchivals = this.wpArchivalListEditorInput.getSelectedWPArchivals();
    if (CommonUtils.isNotNull(selectedWPArchivals) && (selectedWPArchivals.size() > 0)) {
      getSelectedWpArchivals(wpArchivals, selectedWPArchivals);
      for (WpArchival wpArchival : wpArchivals) {
        if (CommonUtils.isEqual(wpArchival.getFileArchivalStatus(), FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType())) {
          CDMLogger.getInstance().infoDialog(
              "Cannot Download Work Package Archival File.\n File Archival is still in Progress.", Activator.PLUGIN_ID);
          return;
        }
        else if (CommonUtils.isEqual(wpArchival.getFileArchivalStatus(), FILE_ARCHIVAL_STATUS.FAILED.getDbType()) ||
            CommonUtils.isEqual(wpArchival.getFileArchivalStatus(), null) ||
            CommonUtils.isEqual(wpArchival.getFileArchivalStatus(), FILE_ARCHIVAL_STATUS.NOT_AVAILABLE.getDbType())) {
          CDMLogger.getInstance().infoDialog(
              "Cannot Download Work Package Archival File Due to File Archival Process Failure", Activator.PLUGIN_ID);
          return;
        }

        String fileName = getArchileFileDownloadName(wpArchival);
        String userDirPath = CommonUtils.getUserDirPath();

        // show progress monitor dilaog
        ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
        try {
          dialog.run(true, true, (final IProgressMonitor monitor) -> {
            monitor.beginTask("Downloading " + fileName, 100);
            monitor.worked(30);
            try {
              // call the download service
              downloadWpArchivalFile(wpArchival, fileName, userDirPath);
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().errorDialog(
                  "Exception occured while trying to download Workpackage archival file", e.getLocalizedMessage());
            }
            monitor.done();
          });
        }
        catch (InvocationTargetException | InterruptedException exp) {
          Thread.currentThread().interrupt();
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

        File file = new File(userDirPath + File.separator + fileName);

        // Check if the file exists
        if (file.exists()) {
          // open dialog to show link of downloaded directory location
          CommonUiUtils.getInstance().openInfoDialogWithLink(
              "Workpackage archival file downloaded successfully to path :" + userDirPath, userDirPath);
        }
      }
    }
    else {
      CDMLogger.getInstance().infoDialog("Please select some Workpackage archival files to download",
          Activator.PLUGIN_ID);
    }
  }


  /**
   * @param wpArchival
   * @return Archile File Download Name
   */
  private String getArchileFileDownloadName(final WpArchival wpArchival) {
    StringBuilder wpArchivalfileName = new StringBuilder();
    wpArchivalfileName.append("WP_Archival_File_").append(wpArchival.getWpName()).append("_")
        .append(wpArchival.getRespName()).append(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_19))
        .append(".zip");

    return (wpArchivalfileName.toString()).replace(" ", "_").replaceAll("[<>:\"/\\|?*]", "-").trim();
  }

  /**
   * @param wpArchival
   * @param fileName
   * @param userDirPath
   * @throws ApicWebServiceException
   */
  private void downloadWpArchivalFile(final WpArchival wpArchival, final String fileName, final String userDirPath)
      throws ApicWebServiceException {

    WpFilesDownloadServiceClient wpFilesDownloadService = new WpFilesDownloadServiceClient();
    wpFilesDownloadService.getWpArchivalFile(wpArchival.getId(), fileName, userDirPath);
  }


  /**
   * @param wpArchivals
   * @param selectedWPArchivals
   */
  private void getSelectedWpArchivals(final Set<WpArchival> wpArchivals,
      final IStructuredSelection selectedWPArchivals) {

    Iterator<?> iterator = selectedWPArchivals.iterator();
    while (iterator.hasNext()) {
      WpArchival wpArchival = (WpArchival) iterator.next();
      if (CommonUtils.isNotNull(wpArchival)) {
        wpArchivals.add(wpArchival);
      }
    }
  }

  /**
   * set the properties
   */
  private void setProperties() {
    setText("Download Workpackage Archival file");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DOWNLOAD_16X16));
  }
}
