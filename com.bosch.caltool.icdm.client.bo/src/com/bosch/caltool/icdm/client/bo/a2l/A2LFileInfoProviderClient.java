/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.io.File;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.bo.a2l.AbstractA2LFileInfoProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Timer;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileDownloadServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.IcdmVersionServiceClient;

/**
 * @author dja7cob
 */
public class A2LFileInfoProviderClient extends AbstractA2LFileInfoProvider {

  /**
   * Pref name - iCDM version during which the local download flag was set
   */
  private static final String PREF_KEY_LOCAL_A2L_ICDM_VERSION = "download.a2l.locally.icdm.version";

  /**
   * Pref name - local download
   */
  private static final String PREF_KEY_LOCAL_A2L = "download.a2l.locally";

  private static final Object SYNC = new Object();

  private static String icdmVersion = null;

  private final IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);


  /**
   * {@inheritDoc}
   * <p>
   * Get A2L file info object for an A2L file in client
   *
   * @throws IcdmException Exception in getting A2L file info object for a2lfile
   */
  @Override
  public A2LFileInfo fetchA2LFileInfo(final A2LFile a2lFile) throws IcdmException {
    Timer timer = new Timer();

    Long a2lFileId = a2lFile.getId();
    getLogger().info("Fetching A2LFileInfo model started. A2L File ID : {}", a2lFileId);

    A2LFileInfo retA2lInfo = null;

    try {
      retA2lInfo = retrieveSerializedA2L(a2lFileId);

      if (null == retA2lInfo) {
        getLogger().debug("Serialized A2L file not in file system. Downloading from server...");
        retA2lInfo = checkPrefAndFetchA2LFileInfo(a2lFile);
      }
    }
    catch (ApicWebServiceException e) {
      throw new IcdmException(e.getMessage(), e);
    }
    catch (IcdmException e) {
      getLogger().info("Deserialization failed for A2L, To download A2L file and serialize locally.");
      getLogger().warn(e.getMessage(), e);
    }

    if (null == retA2lInfo) {
      // If there is an error in deserialization, fetch serialized a2l based on download preferences
      if (isDeserializationError()) {
        getLogger().debug("Serialized file not-exists/corrupted/not-matched. Deleting(if exists) and starting again");
        setA2LFileLocalDownloadPref(CommonUtilConstants.CODE_YES);
        retA2lInfo = deleteSerFileAndDownloadA2L(a2lFile);
      }
      else {
        // If the a2l file info object is not retrieved due to other issues (network failures/IO excpetion),
        // attempt to download serialized a2l/ a2l file based on preferences
        try {
          retA2lInfo = checkPrefAndFetchA2LFileInfo(a2lFile);
        }
        catch (ApicWebServiceException e) {
          throw new IcdmException(e.getMessage(), e);
        }
      }
    }

    getLogger().info("Fetching A2LFileInfo model completed. Time taken = {}", timer.finish());

    return retA2lInfo;
  }

  /**
   * {@inheritDoc}
   *
   * @return File path of the a2l file downloaded
   * @throws IcdmException Exception in downloading A2L file
   */
  @Override
  protected String downloadA2LFile(final A2LFile a2lFile) throws IcdmException {
    getLogger().info("Downloading A2L file. A2L file ID : {}", a2lFile.getId());

    String downloadFolderPath = getDownloadDir(a2lFile.getId());

    try {
      new A2LFileDownloadServiceClient().getA2lFileById(a2lFile.getVcdmA2lfileId(), a2lFile.getFilename(),
          downloadFolderPath);
    }
    catch (ApicWebServiceException e) {
      getLogger().error("Exception in downloading A2L file. A2L file ID : " + a2lFile.getId(), e);
      throw new IcdmException(e.getMessage(), e);
    }

    String a2lFilePath = downloadFolderPath + File.separator + a2lFile.getFilename();
    getLogger().info("A2L file downloaded successfully to : {}", a2lFilePath);

    return a2lFilePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }

  /**
   * @return parent folder path to download A2L/serialized A2L file
   */
  @Override
  protected String getA2lFolderPath() {
    return CommonUtils.getICDMTmpFileDirectoryPath();
  }

  /**
   * @param a2lFile
   * @param a2lFileId
   * @param icdmVers
   * @return
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private A2LFileInfo checkPrefAndFetchA2LFileInfo(final A2LFile a2lFile)
      throws IcdmException, ApicWebServiceException {

    boolean downloadA2lLocally = isA2LFileLocalDownloadPrefSet();
    getLogger().info("Download A2L locally : {}", downloadA2lLocally);

    final Long a2lFileId = a2lFile.getId();

    clearDownloadDir(a2lFileId);

    A2LFileInfo retA2lInfo;
    if (downloadA2lLocally) {
      getLogger().info("Checking a2l download preference...");
      retA2lInfo = deleteSerFileAndDownloadA2L(a2lFile);
    }
    else {
      downloadSerializedA2L(a2lFileId);
      retA2lInfo = retrieveSerializedA2L(a2lFileId);
    }

    return retA2lInfo;
  }

  /**
   * @param icdmVersValue
   * @param downloadA2lLocallyValue
   * @throws BackingStoreException
   */
  private void setA2LFileLocalDownloadPref(final String downloadA2lLocallyValue) throws IcdmException {
    String icdmVers = fetchIcdmVersion();

    this.pref.put(PREF_KEY_LOCAL_A2L_ICDM_VERSION, icdmVers);
    this.pref.put(PREF_KEY_LOCAL_A2L, downloadA2lLocallyValue);

    try {
      this.pref.flush();
    }
    catch (BackingStoreException e) {
      throw new IcdmException("Exception in adding download preferences to preference store : " + e.getMessage(), e);
    }
  }

  /**
   * @param icdmVers
   * @return
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private boolean isA2LFileLocalDownloadPrefSet() throws IcdmException {
    getLogger().info("Checking A2L download preference...");

    String icdmVers = fetchIcdmVersion();

    // iCDM version in which the preference was set. Defaulted to current version
    String prefIcdmVersion = this.pref.get(PREF_KEY_LOCAL_A2L_ICDM_VERSION, icdmVers);
    if (prefIcdmVersion.equals(icdmVers)) {
      // Is local download required? Defaulted to NO
      String prefLocalDownload = this.pref.get(PREF_KEY_LOCAL_A2L, CommonUtilConstants.CODE_NO);
      getLogger().debug("Pref {} = {}", PREF_KEY_LOCAL_A2L, prefLocalDownload);
      return prefLocalDownload.equals(CommonUtilConstants.CODE_YES);
    }

    setA2LFileLocalDownloadPref(CommonUtilConstants.CODE_NO);

    return false;
  }


  /**
   * @param a2lFileId
   * @throws ApicWebServiceException
   * @throws IcdmException
   */
  private void downloadSerializedA2L(final Long a2lFileId) throws ApicWebServiceException, IcdmException {
    getLogger().info("Downloading serialized A2L file. A2L file ID : {} ", a2lFileId);

    String serializedFileName = a2lFileId.toString() + ".ser";
    new A2LFileInfoServiceClient().getA2LFileInfoSerialized(a2lFileId, serializedFileName, getDownloadDir(a2lFileId));

    String serFilePath = getDownloadDir(a2lFileId) + File.separator + serializedFileName;
    createDonefile(a2lFileId);

    getLogger().info("Serialized A2L file downloaded successfully to : {}", serFilePath);
  }

  /**
   * Method to fetch current icdm version
   */
  private static String fetchIcdmVersion() throws IcdmException {
    if (icdmVersion == null) {
      synchronized (SYNC) {
        try {
          icdmVersion = new IcdmVersionServiceClient().getIcdmVersion();
        }
        catch (ApicWebServiceException e1) {
          throw new IcdmException("Exception while retrieving iCDM version : " + e1.getMessage(), e1);
        }
      }
    }
    return icdmVersion;
  }
}
