/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.ArtifactsSelectionDialog;
import com.bosch.caltool.icdm.ws.rest.client.a2l.VcdmPstContentServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmFileDownloadServiceClient;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author dmo5cob
 */
public class DownloadArtifactsAction extends Action {

  /**
   * separator between vcdm class and file name <VCDM_CLASS> : <FILE_NAME>
   */
  private static final String VCDM_CLASS_FILENAME_SEPARATOR = " : ";
  /**
   * Title label
   */
  private static final String FILE_SELECTION_TITLE_LABEL = "Download Artifacts";
  /**
   * Section label
   */
  private static final String DIALOG_SECTION_LABEL = "Select the file(s):";

  /**
   * A2lFile element
   */
  private final Object selectedElement;

  /**
   * @param selectedElement selected Element (PIDC file)
   */
  public DownloadArtifactsAction(final Object selectedElement) {
    super();
    this.selectedElement = selectedElement;
    setProperties();
  }


  /**
   * set the properties
   */
  private void setProperties() {
    setText("Download Artifact(s)");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_16X16);
    setImageDescriptor(imageDesc);
    setEnabled(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    PidcA2l pidcA2l = (PidcA2l) this.selectedElement;

    try {
      if (new CurrentUserBO().canDownloadArtifacts(pidcA2l.getProjectId())) {
        Set<VcdmPstContent> pstContentsSet =
            new VcdmPstContentServiceClient().getVcdmPstContentsForA2l(pidcA2l.getA2lFileId());

        Map<String, Long> artifactMap = new TreeMap<>();
        for (VcdmPstContent cont : pstContentsSet) {
          String filename = "A2L".equals(cont.getVcdmClass()) ? pidcA2l.getName() : cont.getFileName();
          artifactMap.put(cont.getVcdmClass() + VCDM_CLASS_FILENAME_SEPARATOR + filename, cont.getFileId());
        }

        ArtifactsSelectionDialog dlg = new ArtifactsSelectionDialog(Display.getCurrent().getActiveShell(),
            artifactMap.keySet(), ArrayContentProvider.getInstance(), new ColumnLabelProvider(), DIALOG_SECTION_LABEL);

        dlg.setTitle(FILE_SELECTION_TITLE_LABEL);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {
          onOKPressed(artifactMap, dlg, pidcA2l.getProjectId());
        }
      }
      else {
        CDMLogger.getInstance().errorDialog("Insufficient privilege to download artifacts", Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    super.run();

  }


  /**
   * @param a2lFile
   * @param fileNameFileIdMap
   * @param vcdmPst
   * @param dlg
   * @param pidcId
   */
  private void onOKPressed(final Map<String, Long> fileNameFileIdMap, final ArtifactsSelectionDialog dlg,
      final Long pidcId) {

    Object[] fileNames = dlg.getResult();
    if (fileNames.length == 0) {
      MessageDialogUtils.getErrorMessageDialog("Selection Empty", "Please select a file to download");
    }
    else {
      String filePath = dlg.getFilePath();
      if (CommonUtils.isEmptyString(filePath)) {
        return;
      }

      Map<String, Boolean> retMap = invokeWSToDownloadFiles(fileNames, filePath, fileNameFileIdMap, pidcId);
      showMessageAfterDownload(filePath, retMap);
    }
  }


  /**
   * @param filePath
   * @param statusMap
   */
  private void showMessageAfterDownload(final String filePath, final Map<String, Boolean> statusMap) {
    StringBuilder str = new StringBuilder();
    for (Entry<String, Boolean> entry : statusMap.entrySet()) {
      // Check download successful status
      if (Boolean.TRUE.equals(entry.getValue())) {
        // Append file name
        str.append("\t\n").append(entry.getKey());
      }
    }
    if (str.length() == 0) {
      CDMLogger.getInstance().errorDialog("The artifact(s) could not be downloaded.", Activator.PLUGIN_ID);
    }
    else {
      String message = "Artifact(s) downloaded successfully to path :" + filePath + "\nArtifacts :" + str.toString();
      CommonUiUtils.getInstance().openInfoDialogWithLink(message, filePath);
    }
  }


  /**
   * @param fileNames
   * @param filePath
   * @param returnMap
   * @param serviceHandler
   * @param fileNameFileIdMap
   * @param pidcId
   */
  private Map<String, Boolean> invokeWSToDownloadFiles(final Object[] fileNames, final String filePath,
      final Map<String, Long> fileNameFileIdMap, final Long pidcId) {

    Map<String, Boolean> fileDwnldStatusMap = new HashMap<>();

    try {
      final ProgressMonitorDialog progressDlg = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      progressDlg.run(true, true, monitor -> {
        for (Object vcdmClassfileName : fileNames) {
          Long pstFileVrsn = fileNameFileIdMap.get(vcdmClassfileName);
          String vcdmClassFileNameStr = vcdmClassfileName.toString();
          String fileName = vcdmClassFileNameStr.replace(VCDM_CLASS_FILENAME_SEPARATOR, "_");

          try {
            new VcdmFileDownloadServiceClient().get(pidcId, pstFileVrsn, fileName, filePath);
            fileDwnldStatusMap.put(vcdmClassFileNameStr, true);
          }
          catch (ApicWebServiceException exp) {
            fileDwnldStatusMap.put(vcdmClassFileNameStr, false);
            CDMLogger.getInstance().error("Error downloading file " + vcdmClassFileNameStr + " : " + exp.getMessage(),
                exp, Activator.PLUGIN_ID);
          }
        }
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return fileDwnldStatusMap;
  }
}
