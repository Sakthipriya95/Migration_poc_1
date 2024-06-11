package com.bosch.easee.eASEEcdm_Service.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.easee.eASEEcdm_Service.EASEEObjClass;
import com.bosch.easee.eASEEcdm_Service.EASEEService;
import com.bosch.easee.eASEEcdm_Service.EASEEServiceException;
import com.vector.easee.application.cdmservice.CDMWebServiceException;
import com.vector.easee.application.cdmservice.CreateObjectItemStates.CreateObjectItemStateImpl;
import com.vector.easee.application.cdmservice.IParameterValue;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;


public class SimpleTestWebServiceInterface {

  private static final String LOG_FILE = "SimpleTestWebServiceInterface.log";

  private static final String EASEE_DOMAIN = "CDM";
  // private static final int EASEE_SERVER = EASEEService.DGS_CDM_PRO;
  private static final int EASEE_SERVER = EASEEService.DGS_CDM_QUA;

  private static final int WEB_SERVICE_TIME_OUT = 120000;

  private static EASEEService eASEEService = null;

  private ILoggerAdapter logger;

  private static final String LOGGER_PATTERN = "[%-5p] - %d{dd.MM.yyyy, HH:mm:ss} - %m%n";

  public SimpleTestWebServiceInterface() {

  }

  /**
   * Gets the user's temp directory from the environmental variable.
   *
   * @return the temp directory
   */
  public String getUserTempDirectory() {
    String userTempDir = System.getenv("TEMP");
    if ((userTempDir == null) || (userTempDir.trim().length() == 0)) {
      this.logger.warn("User temp directory is not set. Hence setting it to C:\temp");
      userTempDir = "C:\\temp";
    }
    return userTempDir + "\\";
//		return "C:/LocalData/hef2fe/Data/A2L_Analyzer/eASEE/";
  }

  private void init(final String userName, final String password) throws SecurityException, IOException {
    String logFile = getUserTempDirectory() + LOG_FILE;

    this.logger = new Log4JLoggerAdapterImpl(logFile, LOGGER_PATTERN);

    this.logger.setLogLevel(ILoggerAdapter.LEVEL_INFO);

    eASEEService = new EASEEService(this.logger, WEB_SERVICE_TIME_OUT);
    eASEEService.init(userName, password, EASEE_DOMAIN, EASEE_SERVER);
  }

  private void startWebserviceTest() throws SecurityException, IOException {


    List<ObjInfoEntryType> dstList = null;

    try {

      // get objects from eASEE
      this.logger.info("searching for objects ...");

//			dstList = eASEEService.getObjects(" (Bereich='CDM') "
//							+ " AND (Klasse='A2L') "
////							+ " AND (Version_Name like 'M1754VDAC866_M6K04_071210_internal_GROUPS%') "
//							+ " AND (CREATE_DATE > TO_DATE ('2010-01-01','YYYY-MM-DD'))"
//							+ "");

      dstList = eASEEService.getObjects(EASEEObjClass.A2L, "003%", null, null, null);

      this.logger.info(dstList.size() + " objects found");

      // test attribute renaming
//			if (eASEEService.renameProductAttribute("TRANSMISSION", "Transmission")) {
//				logger.info("Product attribute renamed!");
//			} else {
//				logger.warn("Rename Product attribute FAILED!");
//			}

      // test attributeValue renaming
//			Map<String, String> valueMaping = new HashMap<String, String>();
//
//			valueMaping.put("_no", "no");
//
//			if (eASEEService.renameProductAttributeValues("ABS", valueMaping)) {
//				logger.info("Product attribute renamed!");
//			} else {
//				logger.warn("Rename Product attribute FAILED!");
//			}

      // test attribute merge
      Map<String, String> valueMaping = new HashMap<String, String>();

//			valueMaping.put("_no", "no");

      List<String> valuesList = new Vector<String>();

      valuesList.add("-");

//			eASEEService.createProductAttributeAndValue("TRANSMISSION_temp", valuesList);
//
//			if (eASEEService.mergeProductAttributes("TRANSMISSION", "TRANSMISSION_temp")) {
//				logger.info("Product attribute renamed!");
//			} else {
//				logger.warn("Rename Product attribute FAILED!");
//			}

    }
    catch (Throwable e) {
      e.printStackTrace();

    }
    finally {

      //
    }

  }

  private void startAPITest() {
    try {
      if (eASEEService.isApiLoggedIn()) {
        this.logger.info("API Connection Successful");
      }
      else {
        this.logger.info("API Connection Failed");
      }
    }
    catch (EASEEServiceException e) {
      // TODO Auto-generated catch block
      this.logger.error("Error while connecting to API" + e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  private void setTypedRelation() {
    String[] endVersions = new String[1];
    String[] result = new String[10];
    endVersions[0] = "21036167";
    if (eASEEService.isWebServiceLoggedIn()) {
      try {
        result = eASEEService.setTypedRelation("18594946", "compliance check", endVersions);
        System.out.println(result);
      }
      catch (EASEEServiceException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private void getTypedRelation() {
    String[] result = new String[10];
    if (eASEEService.isWebServiceLoggedIn()) {
      try {
        result = eASEEService.getTypedRelation("21036167", "assigned documentation");
        System.out.println(result);
      }
      catch (EASEEServiceException e) {
        e.printStackTrace();
      }
    }
  }

  private void getDSTValues(final String versionNo) {
    try {
      if (eASEEService.isWebServiceLoggedIn()) {
        Set<IParameterValue> values = eASEEService.getDataSetValues(versionNo, null);
        if ((values != null) && !values.isEmpty()) {
          this.logger.info("Number of values found in DST : " + values.size());
        }
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting DST Values" + e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  private void createProductKey(final String aprjVersionNo, final String variantName) {
    try {
      if (eASEEService.isWebServiceLoggedIn()) {
        Map<String, String> t = new HashMap<>();
        t.put("1. SOP", "2014. 12");
        eASEEService.createProductKey(aprjVersionNo, variantName, t);

      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while creating product keys " + e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  private String createFileObject() {
    String newVersion = "";
    try {
      if (eASEEService.isWebServiceLoggedIn()) {
        Path p = Paths.get("D:/users/vau3cob/Test_Upload_file.txt");
        CreateObjectItemStateImpl impl =
            eASEEService.createFileObject(p, "TestObject", "Test Var", EASEEObjClass.DOCU, true);
        newVersion = impl.getNewVersionNo();
        System.out.println(newVersion);
      }
    }
    catch (EASEEServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return newVersion;
  }

  /**
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {
    // System.setProperty("javax.net.ssl.trustStore","resources\\cacerts");
    System.setProperty("java.library.path", "resources\\");
    SimpleTestWebServiceInterface testClass = new SimpleTestWebServiceInterface();
    testClass.init("TAB1JA", "11etcME009@#$");

    testClass.getParameterSet("C598G3V8_131107.hex");
    testClass.getParameterSet("PAR", "PT_Grip.DCM", 2210);
    testClass.getVersion("19925048");
    // testClass.createFileObject();
    // testClass.setTypedRelation();
    // testClass.getTypedRelation();
    // @SuppressWarnings("unused")
    // List<ObjInfoEntryType> list = eASEEService.getObjects(EASEEObjClass.DST, "",
    // "CURSOR_OFFROAD_370KW_STAGEV_FORAGE_HARVESTER.P1603V32", 0, null);

    testClass.startWebserviceTest();
    testClass.getDSTValues("20576599");
    testClass.startAPITest();
    // testClass.createProductKey("18760674", "var1");

  }

  /**
   * @param string
   */
  @SuppressWarnings("restriction")
  private void getVersion(final String versionNo) {
    if (eASEEService.isWebServiceLoggedIn()) {
      ObjInfoEntryType values = eASEEService.getVersion(versionNo);
      if ((values != null)) {
        this.logger.info("Object found with name : " + values.getDisplayName());
      }
    }
  }

  /**
   * @param string
   */
  @SuppressWarnings("restriction")
  private void getParameterSet(final String origFileName) {
    if (eASEEService.isWebServiceLoggedIn()) {
      List<ObjInfoEntryType> values = eASEEService.getParameterSet(origFileName);
      if ((values != null) && !values.isEmpty()) {
        this.logger.info("Number of objects found for the Original File : " + values.size());
      }
    }
  }

  @SuppressWarnings("restriction")
  private void getParameterSet(final String fileClass, final String origFileName, final long fileSize) {
    if (eASEEService.isWebServiceLoggedIn()) {
      List<ObjInfoEntryType> values = eASEEService.getParameterSet(fileClass, origFileName, fileSize);
      if ((values != null) && !values.isEmpty()) {
        this.logger.info("Number of objects found for the Original File : " + values.size());
      }
    }
  }

}
