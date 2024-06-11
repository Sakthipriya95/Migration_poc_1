/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.OSSDocumentDownloadServiceClient;


/**
 * @author NDV4KOR
 */
public class OSSFileOpenAction extends Action implements IWorkbenchAction {

  private static final String ACTION_TEXT = "OSS Attribution Document";

  /**
   * Action ID
   */
  private static final String ACTION_ID = "com.bosch.caltool.icdm.product.actions.OSSFileOpenAction";

  /**
   * constructor
   */
  public OSSFileOpenAction() {
    super(ACTION_TEXT, ImageManager.getImageDescriptor(ImageKeys.ICDM_OSS_16X16));
    setId(ACTION_ID);
  }

  @Override
  public void run() {
    // Downlod OSS Documet.Zip to temp folder of user.
    downloadOSSDocument();
    // Open created PDF report if available
    String zipDir = CommonUtils.concatenate(CommonUtils.getSystemUserTempDirPath(), File.separator,
        ApicConstants.OSS_DOCUMENT_PATH);
    File pdfFile = getOSSPdfFile(new File(zipDir));
    if ((pdfFile != null) && CommonUtils.isFileAvailable(pdfFile.getPath())) {
      if (!CommonUtils.checkIfFileOpen(pdfFile)) {
        CommonUiUtils.openFile(pdfFile.getPath());
      }
    }
    else {
      CDMLogger.getInstance().error("Failed to open OSS Attribution Document", Activator.PLUGIN_ID);
    }
  }


  /**
   * Method to Download OSS Document
   */
  public void downloadOSSDocument() {
    try {

      String dirPath = CommonUtils.concatenate(CommonUtils.getSystemUserTempDirPath(), File.separator,
          ApicConstants.OSS_DOCUMENT_PATH);
      CommonUtils.deleteFile(dirPath);

      dirPath = CommonUtils.concatenate(dirPath, File.separator);

      byte[] files = new OSSDocumentDownloadServiceClient().getOSSDocument(CommonUtils.getSystemUserTempDirPath());
      Map<String, byte[]> filesUnZipped = ZipUtils.unzip(files);
      createOSSDocument(dirPath, filesUnZipped);
    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().warnDialog("The Selected File is already open", fnfe, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException | IOException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }


  }

  /**
   * Method to create OSS Document in temp
   */
  private void createOSSDocument(final String dirPath, final Map<String, byte[]> files) throws IOException {
    Set<String> keySet = files.keySet();
    for (String key : keySet) {
      if (!CommonUtils.isEmptyString(FilenameUtils.getExtension(key))) {// check if path has file
        byte[] fileBytes = files.get(key);
        CommonUtils.createFile(dirPath.concat(key), fileBytes); // creating file in temp path
        // (C:\..\AppData\Local\Temp\OSS_Document\)
      }
    }
  }

  /**
   * Method to get OSS Pdf document
   *
   * @param directory file directory
   * @return OSS document pdf file
   */
  public File getOSSPdfFile(final File directory) {
    Iterator fileIt = null;
    String[] extensions = { "pdf" };
    fileIt = FileUtils.iterateFiles(directory, extensions, false);

    if ((fileIt == null) || !fileIt.hasNext()) {
      return null;
    }
    while (fileIt.hasNext()) {
      return (File) fileIt.next();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub
  }

}
