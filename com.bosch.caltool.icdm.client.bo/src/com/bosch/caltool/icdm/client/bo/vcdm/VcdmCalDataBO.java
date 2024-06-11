/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.vcdm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VcdmCalDataInput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmCalDataServiceClient;

/**
 * @author dja7cob
 */
public class VcdmCalDataBO {

  /**
   * Pref instance for storing cdfx download pref
   */
  private final IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
  /**
   * Flag to check deserialization error
   */
  private boolean isDeserializationError = false;
  /**
   * Pref name - cdfx local download -icdm version
   */
  private static final String PREF_KEY_LOCAL_CDFX_ICDM_VERSION = "download.vcdm.dst.caldata.locally.icdm.version";
  /**
   * Pref name - cdfx local download
   */
  private static final String PREF_KEY_LOCAL_CDFX = "download.vcdm.dst.caldata.locally";

  /**
   * iCDM current version
   */
  private static String icdmVers = null;


  /**
   * @param vcdmdstRevision selected vCDM DST revision
   * @param a2lFileId A2L File id
   * @param a2lFileInfo A2L file info
   * @param selectedDstTreePath DST path
   * @return Cal data map from vCDM
   * @throws IcdmException Exception in retrieving cal data map from vCDM
   */
  public Map<String, CalData> fetchCalDataFromVcdm(final VCDMDSTRevision vcdmdstRevision, final A2LFileInfo a2lFileInfo,
      final Long a2lFileId, final String selectedDstTreePath)
      throws IcdmException {

    Map<String, CalData> vcdmCalDataMap = new HashMap<>();

    try {
      if (isCdfxDownloadPref()) {
        getLoggerCDM().info("CDFx preference is true for current iCDM version. Download CDFx file and parse locally.");
        vcdmCalDataMap = downloadAndParseCdfx(vcdmdstRevision, a2lFileInfo, a2lFileId);
      }
      else {
        vcdmCalDataMap = downloadSerCaldata(vcdmdstRevision, a2lFileInfo, a2lFileId);
      }
    }
    catch (ApicWebServiceException e) {
      getLoggerCDM().info("Exception in retrieving current iCDM version", e);
    }

    if (vcdmCalDataMap.isEmpty()) {
      throw new IcdmException("Caldata not available in vCDM for the selected DST : " + selectedDstTreePath);
    }

    return vcdmCalDataMap;
  }

  /**
   * @param vcdmdstRevision vcdm dst revision
   * @param a2lFileInfo A2LFileInfo
   * @param a2lFileId A2L File Id
   * @param vcdmCalDataMap
   * @return vcdm caldata map
   * @throws ApicWebServiceException
   * @throws IcdmException
   */
  private Map<String, CalData> downloadSerCaldata(final VCDMDSTRevision vcdmdstRevision, final A2LFileInfo a2lFileInfo,
      final Long a2lFileId)
      throws ApicWebServiceException, IcdmException {

    Map<String, CalData> vcdmCalDataMap = new HashMap<>();
    getLoggerCDM()
        .info("CDFx preference is false for current iCDM version. Fetch serialized cal data objects from vCDM.");

    try {
      String serlzdFilePath = fetchSerializedCalData(vcdmdstRevision, a2lFileId);
      vcdmCalDataMap = deserializeVcdmCalData(serlzdFilePath);
    }
    catch (IcdmException e) {
      getLoggerCDM().debug(e.getMessage(), e);
    }
    catch (ApicWebServiceException e) {
      getLoggerCDM().info("Exception in retrieving serialized caldata from vCDM", e);
    }

    if (this.isDeserializationError) {
      getLoggerCDM().info("Exception in deserialization of cal data objects. Download CDFx file and parse locally.");
      setCdfxLocalDownloadPref(CommonUtilConstants.CODE_YES);
      vcdmCalDataMap = downloadAndParseCdfx(vcdmdstRevision, a2lFileInfo, a2lFileId);
    }
    return vcdmCalDataMap;
  }

  /**
   * @return
   */
  private CDMLogger getLoggerCDM() {
    return CDMLogger.getInstance();
  }

  /**
   * Checks whether cdfx download preference is available to download locally
   *
   * @return is preference available
   * @throws IcdmException Exception in retrieving current iCDM version
   * @throws ApicWebServiceException
   */
  private boolean isCdfxDownloadPref() throws ApicWebServiceException {

    getLoggerCDM().info("Checking A2L download preference...");

    // iCDM version in which the preference was set. Defaulted to current version
    String icdmVersion = fetchiCDMVersion();
    String prefIcdmVersion = this.pref.get(PREF_KEY_LOCAL_CDFX_ICDM_VERSION, icdmVersion);
    if (prefIcdmVersion.equals(icdmVersion)) {
      // Is local download required? Defaulted to NO
      String prefLocalDownload = this.pref.get(PREF_KEY_LOCAL_CDFX, CommonUtilConstants.CODE_NO);
      getLoggerCDM().debug("Pref {} = {}", PREF_KEY_LOCAL_CDFX, prefLocalDownload);
      return prefLocalDownload.equals(CommonUtilConstants.CODE_YES);
    }
    setCdfxLocalDownloadPref(CommonUtilConstants.CODE_NO);

    return false;
  }


  /**
   * Fetches the current iCDM version from common params
   *
   * @return
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private static String fetchiCDMVersion() throws ApicWebServiceException {
    if (CommonUtils.isEmptyString(VcdmCalDataBO.icdmVers)) {
      icdmVers = new CommonDataBO().getIcdmVersion();
    }
    return VcdmCalDataBO.icdmVers;
  }

  /**
   * Saves the value to the prefernce file
   *
   * @param downloadCdfxPref preference yes/no
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private void setCdfxLocalDownloadPref(final String downloadCdfxPref) throws ApicWebServiceException {
    this.pref.put(PREF_KEY_LOCAL_CDFX_ICDM_VERSION, fetchiCDMVersion());
    this.pref.put(PREF_KEY_LOCAL_CDFX, downloadCdfxPref);

    try {
      this.pref.flush();
    }
    catch (BackingStoreException e) {
      getLoggerCDM().debug("Exception in adding download preferences to preference store : " + e.getMessage(), e);
    }
  }

  /**
   * Downloads cal data objetcs in a CDFx file
   *
   * @param vcdmdstRevision vcdm dst revision
   * @param a2lFileId
   * @return File path where CDFx file is downloaded
   * @throws IcdmException Exception in downloading CDFx file
   */
  private String downloadCdfxFile(final VCDMDSTRevision vcdmdstRevision, final Long a2lFileId) throws IcdmException {

    String cdfxFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + vcdmdstRevision.getDstID() + "_" +
        System.currentTimeMillis() + ".cdfx";

    try {
      getLoggerCDM().debug("Downloading cal data CDFx file");
      new VcdmCalDataServiceClient().fetchVcdmCalDataCdfx(createVcdmCalDataInput(vcdmdstRevision.getDstID(), a2lFileId),
          cdfxFilePath);
      getLoggerCDM().debug("Cal data CDFx file downloaded successfully to path : {}", cdfxFilePath);
    }
    catch (ApicWebServiceException e) {
      throw new IcdmException("Exception in retrieving serialized cal data from vCDM. " + e.getMessage(), e);
    }

    return cdfxFilePath;
  }

  /**
   * Creates vcdm cal data input object for the service
   *
   * @param vCDMDstId
   * @param a2lFileId
   * @return vcdm cal data input object
   * @throws PasswordNotFoundException
   */
  private VcdmCalDataInput createVcdmCalDataInput(final Long vCDMDstId, final Long a2lFileId) {
    VcdmCalDataInput vcdmCalDataInput = new VcdmCalDataInput();
    vcdmCalDataInput.setA2lFileId(a2lFileId);
    vcdmCalDataInput.setVcdmDstId(vCDMDstId);

    try {
      vcdmCalDataInput.setEncPwd(new CurrentUserBO().getEncPassword());
    }
    catch (ApicWebServiceException e) {
      getLoggerCDM().debug(e.getMessage(), e);
    }

    return vcdmCalDataInput;
  }

  /**
   * Deserializes the serialized file of cal data objects
   *
   * @param serializedFilePath File path of the serialized cal data
   * @return Vcdm cal data (deserialized)
   * @throws IcdmException Exception in deserialization
   */
  private Map<String, CalData> deserializeVcdmCalData(final String serializedFilePath) throws IcdmException {
    getLoggerCDM().debug("Deserializing cal data objects from vCDM");

    byte[] serByteArr = FileIOUtil.toByteArray(serializedFilePath);
    try (ByteArrayInputStream byteInpStream = new ByteArrayInputStream(serByteArr);
        ObjectInputStream objInpStream = new ObjectInputStream(byteInpStream);) {

      @SuppressWarnings("unchecked")
      Map<String, CalData> vcdmCalDataMap = (HashMap<String, CalData>) objInpStream.readObject();
      getLoggerCDM().debug("Deserialization of caldata objects from vCDM completed");
      return vcdmCalDataMap;
    }
    catch (ClassNotFoundException | InvalidClassException exp) {
      this.isDeserializationError = true;
      throw new IcdmException("Exception in deserialization of caldata objects from vCDM. " + exp.getMessage(), exp);
    }
    catch (IOException exp) {
      throw new IcdmException("Exception in accessing serialized caldata file. " + exp.getMessage(), exp);
    }
  }

  /**
   * Downloads and parse the cdfx file
   *
   * @param vcdmdstRevision vcdm DST revision
   * @param a2lFileId
   * @param a2lFileInfo
   * @return Map of cal data objetcs
   * @throws IcdmException Exception in CDFX file handling
   */
  private Map<String, CalData> downloadAndParseCdfx(final VCDMDSTRevision vcdmdstRevision,
      final A2LFileInfo a2lFileInfo, final Long a2lFileId)
      throws IcdmException {

    String cdfxFilePath = downloadCdfxFile(vcdmdstRevision, a2lFileId);

    getLoggerCDM().debug("Parsing CDFx file...");
    CaldataFileParserHandler calDataParser = new CaldataFileParserHandler(ParserLogger.getInstance(), a2lFileInfo);
    Map<String, CalData> calDataObjects = calDataParser.getCalDataObjects(cdfxFilePath);
    getLoggerCDM().debug("Parsing CDFx file completed");

    return calDataObjects;
  }

  /**
   * Retrieves the cal data objects in a serialized file
   *
   * @param vcdmdstRevision vcdm DST revision
   * @return Serialized file path
   * @throws IcdmException Exception in retrieving serialized file of cal data
   * @throws ApicWebServiceException
   */
  private String fetchSerializedCalData(final VCDMDSTRevision vcdmdstRevision, final Long a2lFileId)
      throws ApicWebServiceException {

    String serlzdFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + vcdmdstRevision.getDstID() + "_" +
        System.currentTimeMillis() + ".ser";

    new VcdmCalDataServiceClient()
        .fetchVcdmCalDataSerialized(createVcdmCalDataInput(vcdmdstRevision.getDstID(), a2lFileId), serlzdFilePath);
    getLoggerCDM().debug("Downloading cal data as serialized file completed to path : {}", serlzdFilePath);

    return serlzdFilePath;
  }
}
