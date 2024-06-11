package com.bosch.caltool.apic.vcdminterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.security.Decryptor;
import com.bosch.easee.eASEEcdm_Service.EASEEObjClass;
import com.bosch.easee.eASEEcdm_Service.EASEEService;
import com.bosch.easee.eASEEcdm_Service.EASEEServiceException;
import com.vector.easee.application.cdmservice.CDMWebServiceException;
import com.vector.easee.application.cdmservice.IParameterValue;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;
import com.vector.easee.application.cdmservice.WSAttrMapList;

/**
 * This class handles the datatransfer from/to vCDM.
 *
 * @author hef2fe
 */
public class VCDMInterface {

  /**
   * The A2L sync lock.
   */
  public static final Object A2LSYNC_LOCK = new Object();

  /**
   * The ILoggerAdapter instance for logging EASEE messages.
   */
  private final ILoggerAdapter easeeLogger;

  /**
   * The ILoggerAdapter instance for logging A2L messages.
   */
  private ILoggerAdapter a2lLogger;

  /**
   * the vCDM service handle for the logged in User
   */
  private EASEEService easeeLoggedInUserService;

  /**
   * the vCDM service handle with super user credentials
   */
  private EASEEService easeeSuperService;

  /**
   * @param username the username to be used for the WebService interface
   * @param password the password to be used for the WebService interface
   * @param easeeLogger the logger to be used for EaseeService
   * @throws VCDMInterfaceException thrown if the WebService can not be initialized
   */
  public VCDMInterface(final String username, final String password, final ILoggerAdapter easeeLogger)
      throws VCDMInterfaceException {
    this.easeeLogger = easeeLogger;

    String wsDomain = Messages.getString("EASEEService.DOMAAIN_NAME");

    int serverType = EASEEService.DGS_CDM_PRO;
    if ("DGS_CDM_QUA".equals(Messages.getString("EASEEService.WS_SERVER"))) {
      serverType = EASEEService.DGS_CDM_QUA;
    }

    this.easeeLoggedInUserService = new EASEEService(easeeLogger);

    this.easeeLoggedInUserService.init(username, password, wsDomain, serverType);

    if (!this.easeeLoggedInUserService.isWebServiceLoggedIn()) {
      throw new VCDMInterfaceException("Web service login failed");
    }

  }

  /**
   * This constructor user VCDM super user credentials to log into VCDM This constructor is used for loading the
   * a2lFiles
   *
   * @param easeeLogger the logger to be used for EaseeService
   * @param a2lLogger the logger to be used for A2lParser
   * @throws VCDMInterfaceException exception thrown if the WebService can not be initialized
   */
  public VCDMInterface(final ILoggerAdapter easeeLogger, final ILoggerAdapter a2lLogger) throws VCDMInterfaceException {

    this.easeeLogger = easeeLogger;
    this.a2lLogger = a2lLogger;
    final String wsUser = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_NAME);
    // Get the passord from the Web service
    String passwordKey = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_PASS);

    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(this.easeeLogger);
    final String wsPassword =
        Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), this.easeeLogger);
    String wsDomain = Messages.getString(CommonUtilConstants.EASEE_SERVICE_DOMAIN_NAME);

    int serverType = EASEEService.DGS_CDM_PRO;
    if ("DGS_CDM_QUA".equals(Messages.getString(CommonUtilConstants.EASEE_SERVICE_WS_SERVER))) {
      serverType = EASEEService.DGS_CDM_QUA;
    }

    this.easeeSuperService = new EASEEService(easeeLogger);

    this.easeeSuperService.init(wsUser, wsPassword, wsDomain, serverType);

    if (!this.easeeSuperService.isWebServiceLoggedIn()) {
      throw new VCDMInterfaceException("Web service login failed");
    }
  }


  /**
   * Retrieves the A2L file contents using the EASEE CDM Web service.
   *
   * @param fileName name of the A2L file.
   * @param filePath the file path
   * @param serializedFileName the serialized file name
   * @param versionNumber version ID of the A2L file.
   * @return the file contents as an instance of A2LFileInfo class.
   * @throws IOException exception error during serialization
   * @throws EASEEServiceException error from vCDM service
   */
  public final File getSerializedA2LFileInfo(final String fileName, final String filePath,
      final String serializedFileName, final String versionNumber)
      throws IOException, EASEEServiceException {

    String tempFilePath = filePath + File.separator + fileName;
    final File tempFile = new File(tempFilePath);

    String serialFilePath = filePath + File.separator + serializedFileName + ".ser";
    final File serialFile = new File(serialFilePath);
    this.easeeLogger.info("A2L file path : {}", tempFilePath);

    A2LFileInfo a2lFileInfo;
    byte[] objectByteArray = null;

    if (serialFile.exists()) {
      this.easeeLogger.info("Serialized file already exists. path : {}", serialFilePath);
    }
    else {
      // Download, Parse & save serialized object
      this.easeeLogger.info("Downloading A2L file from vCDM : {}", fileName);
      final ObjInfoEntryType objEntry = new ObjInfoEntryType();
      objEntry.setVersionNo(versionNumber);

      this.easeeSuperService.getEaseeFile(objEntry, tempFilePath);

      if (!tempFile.exists()) {
        this.easeeLogger.warn("A2L file not downloaded");
        return null;
      }
      this.easeeLogger.info("Parsing a2l file info : {}", fileName);
      a2lFileInfo = parseFile(tempFilePath);

      this.easeeLogger.info("Serializing to cache a2l file info : {}", fileName);
      objectByteArray = serializeObject(a2lFileInfo);
      try (FileOutputStream outputStream = new FileOutputStream(serialFile);) {
        outputStream.write(objectByteArray);
      }
      this.easeeLogger.info("Serialized file created. path : {}", serialFilePath);
    }

    this.easeeLogger.info("A2l File Info retrieved successfully for the file : {}", fileName);

    return serialFile;
  }

  private byte[] serializeObject(final A2LFileInfo a2lFileInfo) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(a2lFileInfo);
      return out.toByteArray();
    }
  }

  /**
   * @param fileName Name of the file
   * @param versionNumber file version number
   * @param monitor Progress monitor
   * @param tempFilePath file path where the file is downloaded
   * @return success/failure
   */
  public final boolean getvCDMPSTArtifacts(final String fileName, final String versionNumber,
      final IProgressMonitor monitor, final String tempFilePath) {

    this.easeeLogger.info("Get vCDM PST file : {}, Version number : {}", fileName, versionNumber);
    final ObjInfoEntryType objEntry = new ObjInfoEntryType();

    objEntry.setVersionNo(versionNumber);

    String filePath = tempFilePath + File.separator + fileName;

    this.easeeLogger.info("  File path : {}", filePath);

    if (CommonUtils.isNotNull(monitor)) {
      monitor.worked(10);
      monitor.subTask("Retrieving artifacts  . . .");
    }
    // Download the file(s) using the web service.
    boolean flag = false;
    try {
      flag = this.easeeSuperService.getEaseeFile(objEntry, filePath);
    }
    catch (EASEEServiceException exp) {
      this.easeeLogger.error("Error in fetching VCDM PST Atrifacts", exp);
    }

    if (CommonUtils.isNotNull(monitor)) {
      monitor.worked(100);
    }

    return flag;


  }

  /**
   * @param fileName Name of the file
   * @param versionNumber file version number
   * @param tempFilePath file path where the file is downloaded
   * @return success/failure
   * @throws EASEEServiceException error from vCDM service
   */
  public final boolean getvCDMPSTArtifacts(final String fileName, final String versionNumber, final String tempFilePath)
      throws EASEEServiceException {

    this.easeeLogger.info("Get vCDM PST file : {}", fileName);
    final ObjInfoEntryType objEntry = new ObjInfoEntryType();

    objEntry.setVersionNo(versionNumber);

    String filePath = tempFilePath + File.separator + fileName;

    this.easeeLogger.info("File  path : {}", filePath);

    // Download the file(s) using the web service.
    return this.easeeSuperService.getEaseeFile(objEntry, filePath);
  }

  /**
   * Parse the downloaded A2L file using A2L parser and create the A2LFileInfo object
   *
   * @param filePath
   * @return A2LFileInfo object
   */
  private A2LFileInfo parseFile(final String filePath) {
    synchronized (A2LSYNC_LOCK) {
      // Initialise the A2L parser
      this.a2lLogger.info("Initializing A2L Parser ...");

      A2LParser a2lParser = new A2LParser(this.a2lLogger);
      a2lParser.setFileName(filePath);

      this.a2lLogger.info("Parsing A2L file {}", filePath);
      // Parse the A2L file and create the A2LFileInfo object.
      a2lParser.parse();

      this.a2lLogger.info("Parsing A2L file completed");

      return a2lParser.getA2LFileInfo();
    }
  }

  /**
   * ICDM 533
   *
   * @param vCDMAprj vCDM APRJ name
   * @return APRJ ID
   */
  public String getAPRJID(final String vCDMAprj) {
    final List<ObjInfoEntryType> aprjList =
        this.easeeSuperService.getObjects(EASEEObjClass.APRJ, vCDMAprj, null, null, null);
    String aprjID = "";
    long aprjIDLong = 0;
    long temp;
    // Finding out the APRJ with highest version number
    for (ObjInfoEntryType obj : aprjList) {
      temp = Long.parseLong(obj.getVersionNo());
      if (temp > aprjIDLong) {
        aprjIDLong = temp;
        aprjID = obj.getVersionNo();
      }
    }
    CDMLogger.getInstance().info("APRJ ID: " + aprjID);
    return aprjID;
  }

  /**
   * iCDM-775 <br>
   * Upload a a2l file to vCDM. The method calls eASEEService and returns the vCDM VERSION_NUMBER of the A2L file <br>
   * (if it has been created or if it was still existing) Also, Refer docuementation of
   * EASEEService.createA2LFileObject(..) method
   *
   * @param a2lPath path of the a2l file
   * @return returns the version number of a2l file after uploading, if already exists returns the existing version
   *         number
   * @throws EASEEServiceException exception
   */
  public String loadA2LFile(final String a2lPath) throws EASEEServiceException {
    this.easeeLogger.debug("Uploading A2L file to vCDM : {}", a2lPath);

    String a2lVersion = "";
    try {
      a2lVersion = this.easeeSuperService.createA2LFileObject(a2lPath);
      this.easeeLogger.info("A2L file version number in vCDM: {}", a2lVersion);
    }
    catch (EASEEServiceException e) {
      this.easeeLogger.error("Error uploading a2l file : " + a2lPath, e);
      throw e;
    }
    return a2lVersion;
  }


  /**
   * The hex file is fetched using versNumber and stored in temp folder
   *
   * @param versNumber version number of the delivered HEX file or NULL in case of erors
   * @return hexfile path
   * @throws VCDMInterfaceException if interface is not initialized
   */
  public String loadHexFile(final int versNumber) throws VCDMInterfaceException {

    this.easeeLogger.info("Get Hex file ");

    assertInitialization(this.easeeSuperService);

    List<ObjInfoEntryType> listOfHex =
        this.easeeSuperService.getContent(EASEEObjClass.HEX.name(), String.valueOf(versNumber));

    ObjInfoEntryType hexFileObj = listOfHex.get(0);

    WSAttrMapList attr = this.easeeSuperService.getVersionAttributes(hexFileObj.getVersionNo());
    String hexFileName = "";
    if (null != attr) {
      List<String> hexFileList = attr.get("ORIGINAL FILE");
      if ((null != hexFileList) && (null != hexFileList.get(0))) {
        hexFileName = hexFileList.get(0);
      }
    }
    hexFileName = hexFileName.substring(0, hexFileName.length() - 4);
    String hexFilePath =
        CommonUtils.getICDMTmpFileDirectoryPath() + File.separator + hexFileName + "_" + versNumber + ".hex";

    this.easeeLogger.info("Hex file path : {}", hexFilePath);

    // Download the A2L file using the web service.
    try {
      this.easeeSuperService.getEaseeFile(hexFileObj, hexFilePath);
    }
    catch (EASEEServiceException e) {
      this.easeeLogger.error("Error loading the  hex file file : " + e.getMessage(), e);
    }


    return hexFilePath;
  }


  /**
   * ICDM-868 <br>
   * The method calls eASEEService and returns a set of IParameterValue's for the specific DST ID
   *
   * @param dstID DST-ID in VCDM
   * @return IParameterValue
   * @throws VCDMInterfaceException Catched CDMWebServiceException and rethrown ICDM-1687
   */
  public Set<IParameterValue> getParameterValues(final String dstID) throws VCDMInterfaceException {
    assertInitialization(this.easeeLoggedInUserService);

    try {
      return this.easeeLoggedInUserService.getDataSetValues(dstID, null);
    }
    catch (CDMWebServiceException e) {
      this.easeeLogger.error("Error while retrieving Parameter Values for DST with id : " + dstID, e);
      throw new VCDMInterfaceException(e.getMessage(), e);
    }
  }


  /**
   * ICDM-1841 This method fetched the additional VCDM details of an A2lFile based on the CalDataFileID(Version number)
   *
   * @param versionNumber String
   * @return Map
   * @throws VCDMInterfaceException error from interface
   */
  public Map<String, List<String>> getVersionAttributes(final String versionNumber) throws VCDMInterfaceException {
    assertInitialization(this.easeeSuperService);

    Map<String, List<String>> a2lFileVersionAttrMap = new HashMap<>();
    WSAttrMapList versionAttributes = this.easeeSuperService.getVersionAttributes(versionNumber);
    for (Entry<String, List<String>> verAttrEntry : versionAttributes.entrySet()) {
      a2lFileVersionAttrMap.put(verAttrEntry.getKey(), verAttrEntry.getValue());
    }
    return versionAttributes;
  }


  private void assertInitialization(final EASEEService service) throws VCDMInterfaceException {
    if (service == null) {
      throw new VCDMInterfaceException("vCDM interface not initialized");
    }
  }

}
