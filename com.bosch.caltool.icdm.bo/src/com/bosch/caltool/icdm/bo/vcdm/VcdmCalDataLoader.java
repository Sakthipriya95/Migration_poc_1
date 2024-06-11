/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.vcdm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.calcomp.easee.easeetoecdm.Easee2eCdmModel;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.apic.vcdminterface.VCDMInterfaceException;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.IcdmBoConstants;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.Timer;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDFWriterLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.vcdm.VcdmCalDataInput;
import com.vector.easee.application.cdmservice.IParameterValue;

/**
 * @author dja7cob
 */
public class VcdmCalDataLoader extends AbstractSimpleBusinessObject {

  /**
   * Service directory to write cdfx/serialized file
   */
  private static final String VCDM_CAL_DATA_DIR = "//VCDM_CAL_DATA//";

  /**
   * @param serviceData instance
   */
  public VcdmCalDataLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Fetches cal data from vCDM for a given DST Id and A2L Id. Cal data map fetched from vCDM will be written to a CDFx
   * file and sent to the client as byte array
   *
   * @param input vcdm cal data input
   * @return byte array of cal data file
   * @throws IcdmException Exception in fetching cal data from vCDM
   */
  public byte[] fetchCalDataCdfx(final VcdmCalDataInput input) throws IcdmException {
    Timer timer = new Timer();
    getLogger().info("Started fetching cal data from vCDM to a CDFx file");

    Map<String, CalData> calDataMap = fetchVcdmCalDataMap(input);

    // write cal data map to cdfx file
    String cdfxFilePath = buildCdfxFilePath(input.getVcdmDstId());
    createCdfxFile(calDataMap, cdfxFilePath);

    byte[] retByteArray = FileIOUtil.toByteArray(cdfxFilePath);
    getLogger().debug("CDFx file converted to byte array. Length : {}", retByteArray.length);

    getLogger().info("Fetching cal data from vCDM to CDFx file completed in {}", timer.finish());

    return retByteArray;
  }

  /**
   * Fetches cal data from vCDM for a given DST Id and A2L id. Cal data map fetched from vCDM will be serialized and
   * written to .ser file and sent to the client as byte array.
   *
   * @param input vcdm cal data input
   * @return byte array of cal data file
   * @throws IcdmException Exception in fetching cal data from vCDM
   */
  public byte[] fetchCalDataSerialized(final VcdmCalDataInput input) throws IcdmException {
    Timer timer = new Timer();
    getLogger().info("Started fetching serialized cal data from vCDM");

    // Explicitly declared as hashmap to support serialization
    HashMap<String, CalData> calDataMap = new HashMap<>();
    calDataMap.putAll(fetchVcdmCalDataMap(input));

    getLogger().debug("Serializing cal data...");
    String serFilePath = buildSerFilePath(input.getVcdmDstId());
    serializeCalDataInfoToFile(calDataMap, serFilePath);
    getLogger().debug("Serialization of cal data completed");

    byte[] retByteArray = FileIOUtil.toByteArray(serFilePath);
    getLogger().debug("Serialized cal data object converted to byte array. Length : {}", retByteArray.length);

    getLogger().info("Fetching serialized cal data from vCDM completed in {}", timer.finish());

    return retByteArray;
  }

  /**
   * Fetches cal data map from vCDM
   *
   * @param input vcdm cal data input
   * @return cal data map
   * @throws IcdmException Exception in fetching cal data map
   */
  private Map<String, CalData> fetchVcdmCalDataMap(final VcdmCalDataInput input) throws IcdmException {
    getLogger().info("Fetching cal data from vCDM started for DST ID : {}", input.getVcdmDstId());

    Set<IParameterValue> paramValues;
    try {
      // Get parameter values for vcdm dst
      getLogger().debug("Retrieving parameter values from vCDM");

      // login to vcdm interface
      VCDMInterface vcdmInfUser = new VcdmInterfaceProvider(getServiceData()).createInterfaceUser(input.getEncPwd());

      paramValues = vcdmInfUser.getParameterValues(String.valueOf(input.getVcdmDstId()));

      getLogger().debug("Set of parameter values retrieved. Number of parameters : {}", paramValues.size());
    }
    catch (VCDMInterfaceException e) {
      throw new IcdmException(
          "Error retrieving parameter values from vCDM. Possible reasons: Access denied to DST for user / Invalid Dst ID : " +
              input.getVcdmDstId() + ". " + e.getMessage(),
          e);
    }

    // Get A2LFileInfo
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(input.getA2lFileId());
    A2LFileInfo a2lFileInfo = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(a2lFile);

    getLogger().debug("Converting param values to cal data model...");
    Map<String, CalData> calDataMap =
        new Easee2eCdmModel(EASEELogger.getInstance()).getCalData(a2lFileInfo, paramValues);
    getLogger().info("Fetching cal data from vCDM completed. params count = {}", calDataMap.size());

    return calDataMap;
  }

  /**
   * Builds a file path to write the serialized cal data object
   *
   * @param vCDMDstId
   * @return
   */
  private String buildSerFilePath(final long vCDMDstId) {
    String serverTempDir = getCalDataServiceWrkDir();

    String currentTime = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_18);
    String dirName = "vcdmdatacdfx_" + currentTime;

    String cdfxFolderPath = getParentFolderPath(serverTempDir, dirName);

    return cdfxFolderPath + File.separator + vCDMDstId + ".ser";
  }

  /**
   * Gives the directory path to write the cdfx/ser file
   *
   * @param serverTempDir
   * @param dirName
   * @return File path
   */
  private String getParentFolderPath(final String serverTempDir, final String dirName) {
    String parentFolderPath = serverTempDir + File.separator + dirName;

    if (!new File(parentFolderPath).exists()) {
      new File(parentFolderPath).mkdir();
    }
    return parentFolderPath;
  }


  /**
   * Serializes the cal data object and writes to a .ser file
   *
   * @param vcdmCalData
   * @param serFilePath
   * @throws IcdmException
   */
  private void serializeCalDataInfoToFile(final HashMap<String, CalData> vcdmCalDataMap, final String serFilePath)
      throws IcdmException {

    byte[] objectByteArray = serializeCalData(vcdmCalDataMap);

    try (FileOutputStream outputStream = new FileOutputStream(new File(serFilePath))) {
      outputStream.write(objectByteArray);
    }
    catch (IOException e) {
      getLogger().error("Exception in serialization of Vcdm cal data");
      throw new IcdmException("Error in serialization of vCDM Cal data. " + e.getMessage(), e);
    }
  }

  /**
   * Serializes the input cal data object
   *
   * @return
   * @throws IcdmException
   */
  private byte[] serializeCalData(final HashMap<String, CalData> vcdmCalDataMap) throws IcdmException {
    getLogger().debug("Started serialization of vCDM cal data to byte array.");

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(vcdmCalDataMap);
      byte[] serByteArr = out.toByteArray();
      getLogger().debug("vCDM cal data serialized to byte array. Size : {}", serByteArr.length);

      return serByteArr;
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage(), e);
    }
  }

  /**
   * Builds a file path to write the CDFx file
   *
   * @param vCDMDstId
   * @return
   */
  private String buildCdfxFilePath(final long vCDMDstId) {
    String serverTempDir = getCalDataServiceWrkDir();

    String currentTime = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_18);
    String dirName = "vcdmdatacdfx_" + currentTime;

    String cdfxFolderPath = getParentFolderPath(serverTempDir, dirName);

    return cdfxFolderPath + File.separator + vCDMDstId + ".cdfx";
  }

  /**
   * Gives the service work directory path
   *
   * @return
   */
  private String getCalDataServiceWrkDir() {
    String serverTempDir = Messages.getString(IcdmBoConstants.PROPKEY_SERVICE_WORK_DIR) + VCDM_CAL_DATA_DIR;

    if (!new File(serverTempDir).exists()) {
      new File(serverTempDir).mkdir();
    }
    return serverTempDir;
  }

  /**
   * Creates a cdfx file for the cal data objects
   *
   * @param cdfCalDataObjects
   * @param cdfxFilePath
   * @throws IcdmException
   */
  private void createCdfxFile(final Map<String, CalData> cdfCalDataObjects, final String cdfxFilePath)
      throws IcdmException {

    if (CommonUtils.isNotEmpty(cdfCalDataObjects)) {
      getLogger().debug("Writing cal data values to a CDFx file...");
      try {
        new CDFWriter(cdfCalDataObjects, cdfxFilePath, CDFWriterLogger.getInstance()).writeCalDataToXML();
        getLogger().debug("Writing cal data values to CDFx file completed. File path : {}", cdfxFilePath);
      }
      catch (CDFWriterException e) {
        throw new IcdmException("Error writing CDFx file. " + e.getMessage(), e);
      }
    }
  }
}
