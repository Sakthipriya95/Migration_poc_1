/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.io.FilenameUtils;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFileExportServiceInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.util.ZipUtils;

/**
 * @author say8cob
 */
public class A2LFileExportServiceClient extends AbstractA2lWpRestServiceClient {


  /**
   * Constructor
   */
  public A2LFileExportServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.A2L_FILE_EXP);
  }

  /**
   * @param a2lExportObj as input to service
   * @param exportFileDirectory as input
   * @param a2lFileName as Input
   * @return path of the downloaded a2l file
   * @throws ApicWebServiceException as exceotion
   */
  public String exportA2lFile(final A2LFileExportServiceInput a2lExportObj, final String exportFileDirectory,
      final String a2lFileName)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    File destDir = new File(exportFileDirectory);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    // destination file path
    File destFile = new File(destDir.getPath() + File.separator + a2lFileName);

    // Creating zip File name
    StringBuilder zipFileName = new StringBuilder();
    zipFileName.append(FilenameUtils.removeExtension(destFile.getName()));
    zipFileName.append(".zip");
    // iCDM Temp Directory
    String tempDownloadToDir = getClientConfiguration().getIcdmTempDirectory();

    String tempZipFile = downloadFilePost(wsTarget, a2lExportObj, zipFileName.toString(), tempDownloadToDir);

    // unzip the downloaded file
    try {
      ZipUtils.unzip(tempZipFile, tempDownloadToDir);
      // unzipped file location
      File unZippedFolder =
          new File(tempDownloadToDir + File.separator + FilenameUtils.removeExtension(zipFileName.toString()));
      for (File file : unZippedFolder.listFiles()) {
        // copy the unzipped file to the user provided location
        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if (file.exists()) {
          Files.delete(file.toPath());
          CDMLogger.getInstance().info("File deleted successfully: " + file);
        }
      }
      if (unZippedFolder.exists()) {
        Files.delete(unZippedFolder.toPath());
        CDMLogger.getInstance().info("Un-zipped folder deleted successfully: " + unZippedFolder);
      }
      File tempZip = new File(tempZipFile);
      if (tempZip.exists()) {
        Files.delete(tempZip.toPath());
        CDMLogger.getInstance().info("Zip file deleted successfully: " + tempZip);
      }
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR,
          "Error unzipping file " + destFile.getName() + " : " + exp.getMessage(), exp);
    }
    return destFile.getPath();
  }
}
