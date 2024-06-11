/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;


import java.io.File;
import java.io.IOException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.IcdmBoConstants;
import com.bosch.caltool.icdm.bo.vcdm.VcdmFileDownload;
import com.bosch.caltool.icdm.common.bo.a2l.AbstractA2LFileInfoProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.Timer;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.a2l.A2LFile;

/**
 * @author dja7cob
 */
public class A2LFileInfoProvider extends AbstractA2LFileInfoProvider {

  /**
   * A2L file directory name
   */
  private static final String A2L_FILE_DIR = "A2L_FILE";

  private final ServiceData serviceData;

  /**
   * @param serviceData instance
   */
  public A2LFileInfoProvider(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }

  /**
   * Get serialized <code>A2LFileInfo</code> model as byte array
   *
   * @param a2lFileId A2L file ID
   * @return byte array of serialized A2L file model
   * @throws IcdmException Exception in getting byte array of serialized A2L file
   */
  public byte[] fetchA2LFileInfoSerialized(final Long a2lFileId) throws IcdmException {
    Timer timer = new Timer();
    getLogger().info("Fetching serialized A2LFileInfo started. A2L File ID : {}", a2lFileId);

    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lFileId);

    fetchA2LFileInfo(a2lFile);
    byte[] retArr = createSerializdA2lByteArr(getSerializedA2LPathFromCache(a2lFileId));

    getLogger().info("Fetching serialized A2LFileInfo completed. Time taken = {}", timer.finish());

    return retArr;
  }

  /**
   * Fetch <code>A2LFileInfo</code> model of the given A2L file
   *
   * @return A2L file info model of A2L file
   * @throws IcdmException Exception in getting a2l file info object
   */
  @Override
  public A2LFileInfo fetchA2LFileInfo(final A2LFile a2lFile) throws IcdmException {
    Timer timer = new Timer();

    Long a2lFileId = a2lFile.getId();
    getLogger().info("Fetching A2LFileInfo model started. A2L File ID : {}", a2lFileId);

    new A2LFileDownload(getServiceData()).authenticateUser();

    A2LFileInfo retA2lInfo = null;

    try {
      getLogger().info("Retrieve serialized A2L file from cache/system. A2L file ID : {}", a2lFileId);
      retA2lInfo = retrieveSerializedA2L(a2lFileId);
    }
    catch (IcdmException e) {
      getLogger().warn("Exception while retrieving serialized A2L file. ID : " + a2lFileId + ". " + e.getMessage(), e);
    }

    if (null == retA2lInfo) {
      getLogger().info("Serialized file not-exists/corrupted/not-matched. Deleting(if exists) and starting again...");
      retA2lInfo = deleteSerFileAndDownloadA2L(a2lFile);
    }

    getLogger().info("Fetching A2LFileInfo model completed. Time taken = {}", timer.finish());

    return retA2lInfo;
  }


  /**
   * Method to read the a2l from file path
   * 
   * @param filePath as A2l File Path
   * @return A2LFileInfo
   */
  public A2LFileInfo fetchA2LFileInfoFromFilePath(final String filePath) {
    return parseA2L(filePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String downloadA2LFile(final A2LFile a2lFile) throws IcdmException {
    getLogger().info("Downloading vCDM file for A2L file ID : {}, vCDM File ID : {}...", a2lFile.getId(),
        a2lFile.getVcdmA2lfileId());

    if (null == a2lFile.getVcdmA2lfileId()) {
      unLockFile(a2lFile.getId());
      throw new IcdmException("A2L.INCOMPLETE_VCDM_FILE_INFO", a2lFile.getId());
    }

    String vcdmFilePath = new VcdmFileDownload(getServiceData()).getVcdmFile(a2lFile.getVcdmA2lfileId());
    getLogger().debug("vCDM file downloaded successfully for A2L");

    String a2lFilePath = getDownloadDir(a2lFile.getId()) + File.separator + a2lFile.getFilename();

    getLogger().info("Copying downloaded vCDM file as A2L file to A2L directory. Path: {}", a2lFilePath);

    try {
      FileIOUtil.copy(vcdmFilePath, a2lFilePath);
      FileIOUtil.touch(a2lFilePath);
    }
    catch (IOException e) {
      String message = "Exception in copying vCDM file as A2L file, vCDM File ID : " + a2lFile.getVcdmA2lfileId();
      throw new IcdmException(message, e);
    }

    return a2lFilePath;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * @return parent folder path to download vcdm file/ A2L / serialized A2L file
   */
  @Override
  protected String getA2lFolderPath() {
    // create the file only for the first time
    File file = new File(Messages.getString(IcdmBoConstants.PROPKEY_SERVICE_WORK_DIR) + File.separator + A2L_FILE_DIR);
    if (!file.exists()) {
      file.mkdir();
    }
    return file.getAbsolutePath();
  }


  /**
   * @return the serviceData
   */
  private ServiceData getServiceData() {
    return this.serviceData;
  }
}
