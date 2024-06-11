/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FileUtils;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.A2LLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author dja7cob
 */
public abstract class AbstractA2LFileInfoProvider {

  /**
   * Wait time for a thread in parallel download
   */
  private static final int WAIT_TIME_FOR_DOWNLOAD = 3000;

  /**
   * Key - File ID, serialized file path
   */
  private static final ConcurrentMap<Long, String> serializationStatusMap = new ConcurrentHashMap<>();

  /**
   * Key - File ID, lock status
   */
  private static final ConcurrentMap<Long, String> lockStatusMap = new ConcurrentHashMap<>();

  /**
   * Flag to track deserialization errors
   */
  private boolean isDeserializationError = false;


  /**
   * Get A2L file info model object for the A2L file
   *
   * @param a2lFile a2l file object
   * @return A2L file info object
   * @throws IcdmException Exception in getting A2L file info object
   */
  public abstract A2LFileInfo fetchA2LFileInfo(final A2LFile a2lFile) throws IcdmException;

  /**
   * Download the A2L file if not available already and return the file path
   *
   * @param a2lFile a2l file object
   * @return a2l file path
   * @throws IcdmException exception in dowloading a2l file
   */
  public String getA2lFilePath(final A2LFile a2lFile) throws IcdmException {
    if (!isSerializedA2LInFileSystem(a2lFile.getId())) {
      fetchA2LFileInfo(a2lFile);
    }
    return getDownloadDir(a2lFile.getId()) + File.separator + a2lFile.getFilename();
  }

  /**
   * Download A2L file if not available in the system / if the deserialization of existing file failed
   *
   * @param a2lFile a2l file object
   * @return A2L download path
   * @throws IcdmException exception while downloading A2L file
   */
  protected abstract String downloadA2LFile(final A2LFile a2lFile) throws IcdmException;

  /**
   * @return the logger
   */
  protected abstract ILoggerAdapter getLogger();

  /**
   * @return parent folder path for file download
   */
  protected abstract String getA2lFolderPath();

  /**
   * @param a2lFileId a2l file id
   * @return folder path to download a2l/serialized a2l file
   */
  protected String getDownloadDir(final Long a2lFileId) {
    String folderPath = getA2lFolderPath() + File.separator + a2lFileId;
    if (!new File(folderPath).exists()) {
      new File(folderPath).mkdir();
    }
    return folderPath;
  }


  /**
   * @param a2lFileId a2l file id
   * @return a2l file info
   * @throws IcdmException Exception in retrieving serialized A2L file
   */
  protected A2LFileInfo retrieveSerializedA2L(final Long a2lFileId) throws IcdmException {
    A2LFileInfo a2lFileInfo = null;

    if (isSerializedA2LInCache(a2lFileId)) {
      a2lFileInfo = deserializeIfFileUnlocked(a2lFileId);
    }
    else if (isSerializedA2LInFileSystem(a2lFileId)) {
      a2lFileInfo = deserializeIfFileUnlocked(a2lFileId);
      addFileIdToCache(a2lFileId, buildSerializedFilePath(a2lFileId));
    }

    return a2lFileInfo;
  }

  /**
   * @param a2lFile a2l file
   * @return a2l file info
   * @throws IcdmException exception in downloading fresh A2L file
   */
  protected A2LFileInfo deleteSerFileAndDownloadA2L(final A2LFile a2lFile) throws IcdmException {

    A2LFileInfo a2lFileInfo = null;

    Long a2lFileId = a2lFile.getId();

    getLogger().debug("Checking if A2L file is already available. A2L file ID : {}", a2lFileId);

    if (!this.isDeserializationError && checkSerFileAvailabilityAndWait(a2lFileId)) {
      a2lFileInfo = retrieveSerializedA2L(a2lFileId);
    }
    else {
      if (!isFileLocked(a2lFileId)) {
        a2lFileInfo = downloadAndGetA2LFileInfo(a2lFile);
      }
      else {
        throw new IcdmException(
            "Error downloading A2L file from vCDM due to timeout. Waited for : " + WAIT_TIME_FOR_DOWNLOAD + "seconds");
      }
    }
    return a2lFileInfo;
  }

  /**
   * @param fileId a2l file id / vcdm file id
   * @return path to the serialized file available in cache
   */
  protected String getSerializedA2LPathFromCache(final Long fileId) {
    return serializationStatusMap.get(fileId);
  }

  /**
   * @param a2lFileId A2L File ID
   * @throws IcdmException error during clearning directory
   */
  protected void clearDownloadDir(final Long a2lFileId) throws IcdmException {
    for (File file : new File(getDownloadDir(a2lFileId)).listFiles()) {
      try {
        Files.delete(Paths.get(file.getPath()));
      }
      catch (IOException e) {
        throw new IcdmException("Error while clearing directory contents for fresh A2L download. " + e.getMessage(), e);
      }
    }
    serializationStatusMap.remove(a2lFileId);
  }

  /**
   * @param a2lFileId a2l file id
   * @return true if done file creation is success
   * @throws IcdmException error while creating done file
   */
  protected final boolean createDonefile(final Long a2lFileId) throws IcdmException {
    try {
      getLogger().debug("Creating DONE file for A2L download... A2L file ID : {}", a2lFileId);
      return new File(getDoneFilePath(a2lFileId)).createNewFile();
    }
    catch (IOException e) {
      throw new IcdmException("Error while creating DONE file", e);
    }
  }

  /**
   * @return the isDeserializationError
   */
  protected boolean isDeserializationError() {
    return this.isDeserializationError;
  }


  /**
   * @param filePath serialized file path
   * @return byte array of serialized file
   * @throws IcdmException exception while converting file to byte array
   */
  protected byte[] createSerializdA2lByteArr(final String filePath) throws IcdmException {
    getLogger().debug("Converting serialized A2L file to byte array");

    try {
      byte[] a2lByteArr = FileUtils.readFileToByteArray(new File(filePath));
      getLogger().debug("Convertion of serialized A2L file to byte array successful. Byte array size = {}",
          a2lByteArr.length);

      return a2lByteArr;
    }
    catch (IOException e) {
      getLogger().error("Exception in converting serialized A2L file to byte array", e);
      throw new IcdmException(e.getMessage(), e);
    }
  }


  /**
   * @param a2lDownloadPath a2l file path
   * @param a2lFileId a2l file id
   * @return byte array of serialized A2L file
   * @throws IcdmException Exception while getting serialized file after fresh download
   */
  private A2LFileInfo getSerializedA2LFileInfo(final String a2lDownloadPath, final Long a2lFileId)
      throws IcdmException {

    String serializedFilePath = buildSerializedFilePath(a2lFileId);
    A2LFileInfo parsedA2L = parseA2L(a2lDownloadPath);
    serializeA2lFileInfoToFile(parsedA2L, a2lFileId, serializedFilePath);
    A2LFileInfo a2lFileInfo = deserializeA2L(a2lFileId, serializedFilePath);

    // Add file details to cache, once the serialization is completed
    addFileIdToCache(a2lFileId, serializedFilePath);

    return a2lFileInfo;
  }

  /**
   * @param a2lFileId a2l file id
   * @param serializedFilePath path to the serialized file
   * @return A2L file info object
   * @throws IcdmException Exception while getting A2L file info object for a2l
   */
  private A2LFileInfo deserializeA2L(final Long a2lFileId, final String serializedFilePath) throws IcdmException {
    getLogger().info("Deserializing A2L file... A2L file ID : {}", a2lFileId);

    byte[] serByteArr = createSerializdA2lByteArr(serializedFilePath);
    try (ByteArrayInputStream byteInpStream = new ByteArrayInputStream(serByteArr);
        ObjectInputStream objInpStream = new ObjectInputStream(byteInpStream);) {

      A2LFileInfo retObj = (A2LFileInfo) objInpStream.readObject();
      getLogger().info("Deserialization of A2L file successful. A2L file ID : {}", a2lFileId);
      this.isDeserializationError = false;
      return retObj;
    }
    catch (ClassNotFoundException | IOException exp) {
      this.isDeserializationError = true;
      getLogger().error("Exception in deserialization of A2L file. A2L file ID : " + a2lFileId);
      throw new IcdmException(exp.getMessage(), exp);
    }
  }


  /**
   * @param a2lFile
   * @param fileId
   * @return
   * @throws IcdmException
   */
  private A2LFileInfo downloadAndGetA2LFileInfo(final A2LFile a2lFile) throws IcdmException {
    Long a2lFileId = a2lFile.getId();

    lockFile(a2lFileId);

    File startFile = new File(getStartFilePath(a2lFileId));

    boolean startFileCreated = createStartFile(a2lFile, startFile);

    A2LFileInfo a2lFileInfo = null;
    if (startFileCreated) {
      clearDownloadDir(a2lFileId);
      String a2lDownloadPath = downloadA2LFile(a2lFile);
      a2lFileInfo = getSerializedA2LFileInfo(a2lDownloadPath, a2lFileId);

      // Delete the start file after successful download and serialization
      deleteStartFile(startFile);

      boolean doneFileCreated = createDonefile(a2lFileId);
      if (doneFileCreated) {
        unLockFile(a2lFileId);
      }
    }

    return a2lFileInfo;
  }


  /**
   * @param a2lFile
   * @param startFile
   * @return
   * @throws IcdmException
   */
  private boolean createStartFile(final A2LFile a2lFile, final File startFile) throws IcdmException {
    try {
      getLogger().debug("Creating START file for fresh A2L download... A2L file ID : {}", a2lFile.getId());
      return startFile.createNewFile();
    }
    catch (IOException e) {
      throw new IcdmException("Error while creating START file", e);
    }
  }

  /**
   * @param startFile
   * @throws IcdmException
   */
  private void deleteStartFile(final File startFile) throws IcdmException {
    try {
      if (startFile.exists()) {
        Files.delete(startFile.toPath());
        getLogger().debug("Existing START file deleted");
      }
    }
    catch (IOException e) {
      throw new IcdmException("Exception while deleting START file", e);
    }
  }


  /**
   * @param fileId a2l file id (client) / vcdm file id(server)
   * @param a2lFileId a2l file id
   * @return a2l file info
   * @throws DataException
   * @throws IcdmException
   */
  private A2LFileInfo deserializeIfFileUnlocked(final Long a2lFileId) throws IcdmException {
    A2LFileInfo a2lFileInfo = null;

    if (checkSerFileAvailabilityAndWait(a2lFileId)) {
      getLogger().debug("Serialized A2L file is available for deserialization. A2L file ID : {}", a2lFileId);
      a2lFileInfo = deserializeA2L(a2lFileId, buildSerializedFilePath(a2lFileId));
    }
    return a2lFileInfo;
  }

  /**
   * Checks if the serialized file is available in cache/file system. Also checks if the file is locked and waits till
   * it is unlocked
   *
   * @param a2lFileId a2l File Id
   * @return is file accessible
   */
  private boolean checkSerFileAvailabilityAndWait(final Long a2lFileId) {
    boolean isFileAvailable = isSerializedA2LInCache(a2lFileId) || isSerializedA2LInFileSystem(a2lFileId);

    if (isFileLocked(a2lFileId)) {
      getLogger().debug("A2L file is locked by another thread, wait for unlock. File ID : {}", a2lFileId);
      isFileAvailable = waitForFileUnlock(a2lFileId);
    }

    return isFileAvailable;
  }

  /**
   * @param fileId
   * @return
   */
  private boolean waitForFileUnlock(final Long a2lFileId) {
    boolean isWaitSuccess = isSerializedA2LInCache(a2lFileId);

    if (!isWaitSuccess) {
      getLogger().debug("Wait for unlock of file. File ID : {}", a2lFileId);

      int counter = 0;
      while (!isWaitSuccess && (counter < WAIT_TIME_FOR_DOWNLOAD)) {
        try {
          Thread.sleep(100);
          counter++;
        }
        catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        isWaitSuccess = isSerializedA2LInCache(a2lFileId);
      }

      return isWaitSuccess;
    }
    return isWaitSuccess;
  }

  /**
   * Invoke A2L parser to parse the downloaded A2L file to create the A2LFileInfo model
   *
   * @param filePath
   * @return A2LFileInfo object
   */
  public A2LFileInfo parseA2L(final String filePath) {

    // Multi threading is not supported by A2L parser
    synchronized (ApicConstants.A2L_PARSER_SYNC_LOCK) {

      getLogger().info("Parsing A2L file : {}", filePath);

      // Initialise the A2L parser
      A2LParser a2lParser = new A2LParser(A2LLogger.getInstance());
      a2lParser.setFileName(filePath);

      // Parse the A2L file and create the A2LFileInfo object.
      a2lParser.parse();

      getLogger().info(" A2L file parsing completed successfully");

      return a2lParser.getA2LFileInfo();
    }
  }


  /**
   * Serialize A2LFileInfo and write to the given file path
   *
   * @param a2lFileInfo
   * @param a2lFileId
   * @param serFilePath path to which serialized data to be written to
   * @throws IcdmException
   * @throws IOException
   */
  private void serializeA2lFileInfoToFile(final A2LFileInfo a2lFileInfo, final Long a2lFileId, final String serFilePath)
      throws IcdmException {
    getLogger().info("Serializing A2LFileInfo and writing to file... A2L file ID : {}", a2lFileId);

    byte[] objectByteArray = doSerializeA2LFileInfo(a2lFileInfo);

    getLogger().debug("Storing serialized A2L byte array to file system. File path : {}", serFilePath);
    try (FileOutputStream outputStream = new FileOutputStream(new File(serFilePath))) {
      outputStream.write(objectByteArray);
      getLogger().debug("Serialization of A2LFileInfo completed successfully.");
    }
    catch (IOException e) {
      getLogger().error("Exception in serialization of A2LFileInfo. A2L file ID : " + a2lFileId);
      throw new IcdmException(e.getMessage(), e);
    }
  }

  /**
   * @param a2lFileInfo
   * @return
   * @throws IcdmException
   * @throws IOException
   */
  private byte[] doSerializeA2LFileInfo(final A2LFileInfo a2lFileInfo) throws IcdmException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(a2lFileInfo);
      byte[] serByteArr = out.toByteArray();
      getLogger().debug("A2LFileInfo serialized to byte array. Size : {}", serByteArr.length);

      return serByteArr;
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage(), e);
    }
  }


  /**
   * @param fileId vcdm a2l file id/ a2l file id
   * @return whthere file is locked in session
   */
  private boolean isFileLocked(final Long fileId) {
    boolean isLocked = lockStatusMap.containsKey(fileId);
    getLogger().debug("Is file {} locked : {}", fileId, isLocked);

    return isLocked;
  }


  /**
   * @param fileId vcdm a2l file id/a2l file id
   */
  private void lockFile(final Long fileId) {
    getLogger().debug("Locking vCDM A2L file for serialization. vCDM A2L file ID : {}", fileId);
    lockStatusMap.put(fileId, "L");

    getLogger().debug("vCDM A2L file locked for serialization");
  }

  /**
   * @param fileId vcdm a2l file id/ a2l file id
   */
  protected void unLockFile(final Long fileId) {
    lockStatusMap.remove(fileId);
    getLogger().debug("File {} unlocked", fileId);
  }


  /**
   * @param fileID
   * @return
   */
  private String buildSerializedFilePath(final Long a2lFileId) {
    return getDownloadDir(a2lFileId) + File.separator + a2lFileId.toString() + ".ser";
  }

  /**
   * @param serializedFilePath
   * @param a2lFileId
   */
  private void addFileIdToCache(final Long fileId, final String serializedFilePath) {
    serializationStatusMap.put(fileId, serializedFilePath);
    getLogger().debug("Serialized A2L file details added to cache. File ID : {}", fileId);
  }

  /**
   * @param a2lFileId
   * @return
   */
  private boolean isSerializedA2LInFileSystem(final Long a2lFileId) {
    boolean ret =
        new File(buildSerializedFilePath(a2lFileId)).exists() && new File(getDoneFilePath(a2lFileId)).exists();
    getLogger().debug("Serialized A2L file, File ID : {}, available in file system = {}", a2lFileId, ret);

    return ret;
  }

  /**
   * @param fileId
   * @return
   */
  private boolean isSerializedA2LInCache(final Long a2lFileId) {
    boolean ret = serializationStatusMap.containsKey(a2lFileId) && new File(getDoneFilePath(a2lFileId)).exists();
    getLogger().debug("Serialized A2L file, File ID : {}, available in cache = {}", a2lFileId, ret);

    return ret;
  }

  /**
   * @param a2lFileId
   * @return
   */
  private String getDoneFilePath(final Long a2lFileId) {
    return getDownloadDir(a2lFileId) + File.separator + a2lFileId.toString() + ".done";
  }

  /**
   * @param a2lFileId
   * @return
   */
  private String getStartFilePath(final Long a2lFileId) {
    return getDownloadDir(a2lFileId) + File.separator + a2lFileId.toString() + ".start";
  }
}
