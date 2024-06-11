/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.vcdm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FileUtils;

import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.apic.vcdminterface.VCDMInterfaceException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.A2LLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.easee.eASEEcdm_Service.EASEEServiceException;

/**
 * @author dja7cob
 */
public class VcdmFileDownload extends AbstractSimpleBusinessObject {

  private static final String DIR_VCDM_DOWNLOAD_NAME = "VCDM_FILE_DOWNLOAD";

  private static final int WAITING_THREAD_TOTAL_WAIT_TIME = 120000;// 2 Minutes

  private static final int WAITING_THREAD_SLEEP_TIME = 100;// milliseconds

  /**
   * Key - vCDM File ID, download status : S/F
   */
  private static final ConcurrentMap<Long, String> downloadedItemsMap = new ConcurrentHashMap<>();

  /**
   * @param serviceData service data
   */
  public VcdmFileDownload(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Fetch vCDM file using vCDM interface
   *
   * @param pidcId pidc ID
   * @param vcdmFileId vcdm file id
   * @return file stream as byte array
   * @throws IcdmException exception
   */
  public byte[] fetchVcdmFile(final Long pidcId, final Long vcdmFileId) throws IcdmException {

    validateInputs(pidcId);

    return downloadVcdmFile(vcdmFileId);
  }

  /**
   * @param vcdmFileId vcdm file id
   * @return byte array of downloaded vcdm file
   * @throws IcdmException exception in downloading vcdm file
   */
  public byte[] downloadVcdmFile(final Long vcdmFileId) throws IcdmException {
    String vcdmFilePath = getVcdmFile(vcdmFileId);

    return getByteArrayForFile(vcdmFilePath);
  }

  /**
   * @param vcdmFileId vcdm file id
   * @return file path of the downloaded vcdm file
   * @throws IcdmException exception in downloading vcdm file
   */
  public String getVcdmFile(final Long vcdmFileId) throws IcdmException {
    // Prepare file paths
    String vcdmWorkDir = getVcdmWorkDir();

    String downloadDir = getVcdmFileDownloadDir(vcdmFileId);

    String fileName = vcdmFileId + ".file";
    String vcdmFilePath = downloadDir + File.separator + fileName;
    getLogger().debug("vCDM File download directory : {}, file name : {}", downloadDir, fileName);

    File startFile = new File(downloadDir + File.separator + vcdmFileId + ".start");
    getLogger().debug("  START file : {}", startFile);

    File doneFile = new File(downloadDir + File.separator + vcdmFileId + ".done");
    getLogger().debug("  DONE file : {}", doneFile);

    // Get parallel download check flags
    boolean curSessionDownload = downloadedItemsMap.containsKey(vcdmFileId);
    if (!curSessionDownload) {
      downloadedItemsMap.put(vcdmFileId, "S");
    }

    boolean downloadStarted = startFile.exists();
    boolean downloadFinished = doneFile.exists();

    getLogger().debug("Check Parallel downloads : Current session - {}, start file - {}, done file - {}",
        curSessionDownload, downloadStarted, downloadFinished);

    if (curSessionDownload || downloadStarted || downloadFinished) {
      getLogger().debug("Download started/finished already. Starting wait...");

      boolean wait = waitForDownload(doneFile);
      getLogger().debug("Wait completed. status : {}", wait);

      if (!wait) {
        // If wait was not successful, attempt file download
        // Directory creation not required here as it already exists
        getLogger().debug("Wait finished as failure. Starting download again...");
        download(vcdmFileId, vcdmWorkDir, downloadDir, fileName, startFile, doneFile);
      }
    }
    else {
      getLogger().debug("Starting fresh download...");

      // Download the file
      download(vcdmFileId, vcdmWorkDir, downloadDir, fileName, startFile, doneFile);
    }
    return vcdmFilePath;
  }

  /**
   * @param vcdmFileId vcdm file id
   * @return directory to download the vcdm file
   */
  public String getVcdmFileDownloadDir(final Long vcdmFileId) {
    return getVcdmWorkDir() + File.separator + vcdmFileId;
  }

  /**
   * @return
   */
  private String getVcdmWorkDir() {
    return Messages.getString("SERVICE_WORK_DIR") + File.separator + DIR_VCDM_DOWNLOAD_NAME;
  }

  /**
   * @param doneFile
   * @return
   */
  private boolean waitForDownload(final File doneFile) {
    boolean done = doneFile.exists();
    if (done) {
      getLogger().debug("DONE file exists. Download already finished");
    }
    else {
      done = doWaitForDownload(doneFile);
    }

    return done;
  }

  /**
   * @param doneFile
   * @return
   */
  private boolean doWaitForDownload(final File doneFile) {
    getLogger().debug("Wait started ...");

    int counter = 0;
    boolean done = doneFile.exists();

    long startTime = System.currentTimeMillis();

    while (!done && (counter < (WAITING_THREAD_TOTAL_WAIT_TIME / WAITING_THREAD_SLEEP_TIME))) {
      try {
        Thread.sleep(WAITING_THREAD_SLEEP_TIME);
        counter++;
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        getLogger().error("Error in vCDM file download", e);
      }
      done = doneFile.exists();
    }

    getLogger().debug("Wait completed. Waited for : {} ms. Download finished status : {}",
        (System.currentTimeMillis() - startTime), done);

    return done;
  }


  /**
   * Download vcdm file to server directory
   *
   * @param vcdmFileId file ID
   * @param vcdmWorkDir parent directory of download directory
   * @param downloadDir directory path to download file
   * @param fileName file name
   * @param startFile start file
   * @param doneFile done file
   * @throws IcdmException
   */
  private void download(final Long vcdmFileId, final String vcdmWorkDir, final String downloadDir,
      final String fileName, final File startFile, final File doneFile)
      throws IcdmException {

    getLogger().debug("Starting download from vCDM. File ID : {}", vcdmFileId);

    // Create directory for all vcdm file downloads
    FileIOUtil.createDir(vcdmWorkDir);

    // Create directory for downloading file
    FileIOUtil.createDir(downloadDir);

    try {
      boolean startFileStatus = startFile.createNewFile();
      getLogger().debug("Start file creation : {}", startFileStatus);

      boolean downloadStatus = new VCDMInterface(EASEELogger.getInstance(), A2LLogger.getInstance())
          .getvCDMPSTArtifacts(fileName, vcdmFileId.toString(), downloadDir);

      if (!downloadStatus) {
        throw new IcdmException("File download from vCDM failed");
      }

      getLogger().debug("File with ID {} downloaded from vCDM, to directory : {}", vcdmFileId, downloadDir);

      boolean doneFileStatus = doneFile.createNewFile();
      if (doneFileStatus) {
        getLogger().debug("File download finished succesfully");
      }
      if (startFile.exists()) {
        Files.delete(startFile.toPath());
      }
      downloadedItemsMap.put(vcdmFileId, "F");
    }
    catch (EASEEServiceException | VCDMInterfaceException e) {
      throw new IcdmException("Error while downloading file from vCDM : " + e.getMessage(), e);
    }
    catch (IOException e) {
      throw new IcdmException("Error occured during file download : " + e.getMessage(), e);
    }
  }

  /**
   * @param vcdmFilePath
   * @return
   * @throws IcdmException
   * @throws IOException
   */
  private byte[] getByteArrayForFile(final String vcdmFilePath) throws IcdmException {
    try {
      byte[] retByteArray = FileUtils.readFileToByteArray(new File(vcdmFilePath));

      getLogger().debug("vCDM file converted to byte array. Length : {}", retByteArray.length);

      return retByteArray;
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage(), e);
    }
  }

  /**
   * Check whether the user has access rights to download vCDM file
   *
   * @param pidcId
   * @throws IcdmException
   */
  private void validateInputs(final Long pidcId) throws IcdmException {

    // Validate the PIDC ID
    new PidcLoader(getServiceData()).validateId(pidcId);

    boolean hasAccess = new ApicAccessRightLoader(getServiceData()).isCurrentUserApicWrite() ||
        new NodeAccessLoader(getServiceData()).isCurrentUserRead(pidcId);

    getLogger().debug("Access to download files : {}", hasAccess);

    if (!hasAccess) {
      throw new UnAuthorizedAccessException("Insufficient privileges to download file from vCDM");
    }

  }
}
