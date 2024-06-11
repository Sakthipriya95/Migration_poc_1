package com.bosch.easee.eASEEcdm_Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.easee.eASEE_ComAPI.IApplication;
import com.bosch.easee.eASEE_ComAPI.IClientApi2;
import com.bosch.easee.eASEE_ComAPI.IRelationObj;
import com.bosch.easee.eASEE_ComAPI.IVersionCol;
import com.bosch.easee.eASEE_ComAPI.IVersionObj;
import com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmDatasetExportType;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmEReportFileType;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmProcessResult;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmReportFilter;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmReportMode;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult;
import com.bosch.easee.eASEEcdm_ComAPI.EDeliverTargetClass;
import com.bosch.easee.eASEEcdm_ComAPI.EValidationOptions;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinResult;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportResult;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportSettings;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMDomain;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioApi;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResult;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItem;
import com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareSettings;
import com.bosch.easee.eASEEcdm_Service.model.vCDMProcessResult;
import com.bosch.easee.eASEEcdm_Service.util.ConnectionException;
import com.bosch.easee.eASEEcdm_Service.util.DBPasswordRetrival;
import com.vector.easee.application.cdmdatasetservice.CDMWebDataSetService;
import com.vector.easee.application.cdmdomainservice.CDMWebDomainService;
import com.vector.easee.application.cdmprojectservice.CDMWebProjectService;
import com.vector.easee.application.cdmservice.AttributesContainer;
import com.vector.easee.application.cdmservice.CDMWebServiceException;
import com.vector.easee.application.cdmservice.CreateFileObjectItem;
import com.vector.easee.application.cdmservice.CreateObjectItem;
import com.vector.easee.application.cdmservice.CreateObjectItemStates;
import com.vector.easee.application.cdmservice.CreateObjectItemStates.CreateObjectItemStateImpl;
import com.vector.easee.application.cdmservice.DeliveryFileInfoType;
import com.vector.easee.application.cdmservice.FileObjCreationInfoType;
import com.vector.easee.application.cdmservice.FileObjReferenceInfoType;
import com.vector.easee.application.cdmservice.GetPRDItem;
import com.vector.easee.application.cdmservice.GetPVDItem;
import com.vector.easee.application.cdmservice.IParameterValue;
import com.vector.easee.application.cdmservice.InsertIntoContainerItem;
import com.vector.easee.application.cdmservice.ItemStates;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;
import com.vector.easee.application.cdmservice.ValidationMessage;
import com.vector.easee.application.cdmservice.ValidationResultType;
import com.vector.easee.application.cdmservice.ValidationStateType;
import com.vector.easee.application.cdmservice.WSActivationFlag;
import com.vector.easee.application.cdmservice.WSAttrMapList;
import com.vector.easee.application.cdmservice.WSProductAttributeValue;
import com.vector.easee.application.cdmservice.WSProgramKey;
import com.vector.easee.application.cdmservice.WSVariantKey;
import com.vector.easee.application.cdmservice.WorkpackageAndFunctionCalibrationStatus;
import com.vector.easee.application.cdmsessionservice.CDMWebSessionService;
import com.vector.easee.application.cdmversionservice.CDMWebVersionService;
import com.vector.easee.application.idm.cdmidmservice.CDMWebIDMService;
import com.vector.easee.application.idm.types.UserItem;
import com.vector.easee.application.idm.types.UserQuery;
import com.vector.easee.cdm.server.gen.XmlManager;
import com.vector.easee.cdm.server.gen.prd.FUNCLIST;
import com.vector.easee.cdm.server.gen.prd.FUNCLIST.FUNC;
import com.vector.easee.cdm.server.gen.prd.PARAMLIST;
import com.vector.easee.cdm.server.gen.prd.PARAMLIST.PARAM;
import com.vector.easee.cdm.server.gen.prd.WPLIST;
import com.vector.easee.cdm.server.gen.pvd.ObjectFactory;
import com.vector.easee.cdm.server.gen.pvd.WP;

import com4j.Com4jObject;

/**
 * @author hef2fe
 */
public class EASEEService implements EASEEServiceMethods {

  // WebService Services
  private static final String WEB_SERVICE_SESSION_SERVICE = "/services/CDMSessionService";
  private static final String WEB_SERVICE_VERSION_SERVICE = "/services/CDMVersionService";
  private static final String WEB_SERVICE_DOMAIN_SERVICE = "/services/CDMDomainService";
  private static final String WEB_SERVICE_DATASET_SERVICE = "/services/CDMDataSetService";
  private static final String WEB_SERVICE_PROJECT_SERVICE = "/services/CDMProjectService";
  private static final String WEB_SERVICE_IDM_SERVICE = "/services/CDMIDMService";

  // DGS_CDM_QUA
  private static final String WEB_SERVICE_PROTOCOL_QUA = "https://";
  private static final String WEB_SERVICE_PORT_QUA = "12943";
  private static final String WEB_SERVICE_SERVER_QUA = "rb-dgscdmquaweb.de.bosch.com";
  private static final String WEB_SERVICE_NAME_QUA = "/DGS_CDM_QUA_WEBSRV";

  private static String API_ALIAS_QUA = "";

  // DGS_CDM_PRO
  private static final String WEB_SERVICE_PROTOCOL_PRO = "https://";
  private static final String WEB_SERVICE_PORT_PRO = "12943";
  private static final String WEB_SERVICE_SERVER_PRO = "rb-dgscdmproweb.de.bosch.com";
  private static final String WEB_SERVICE_NAME_PRO = "/DGS_CDM_PRO_WEBSRV";

  private static String API_ALIAS_PRO = "";

  /**
   * connect to eASEE.cdm PRO (production) database
   */
  public static final int DGS_CDM_PRO = 1;

  /**
   * connect to eASEE.cdm QUA (test) database
   */
  public static final int DGS_CDM_QUA = 2;

  // --------------------------------------------------------------------------
  public static enum PrdListType {
                                  /** type for characteristics list **/
                                  PARAMLIST,
                                  /** type for functions list **/
                                  FUNCLIST,
                                  /** type for work packages list **/
                                  WPLIST;
  }

  /**
   * Possible Element-Configuration-States.<br>
   * <lu>
   * <li>[0] {@link #DISABLED}
   * <li>[1] {@link #ENABLED}
   * <li>[2] {@link #HIDDEN}
   * <li>[3] {@link #NODEFINE}
   * <li>[4] {@link #NOTAVAILABLE}
   * <li>[5] {@link #QBSOLETE}</lu> <br>
   * <br>
   * View ConfigStates:<br>
   * <code>
   * for (ConfigState cs : ConfigState.values())
   * System.out.print("["+cs.ordinal() +"] "+ cs.name +"\n");</code>
   *
   * @author gpe2si
   * @version 1.0 2011/08/12
   */
  public static enum ConfigState {
                                  /** config state of the element is disabled */
                                  DISABLED,
                                  /** config state of the element is enabled */
                                  ENABLED,
                                  /** config state of the element is hidden */
                                  HIDDEN,
                                  /** config state of the element is not defined */
                                  NODEFINE,
                                  /** config state of the element can not be determined */
                                  NOTAVAILABLE,
                                  /** config state of the element is obsolete. */
                                  QBSOLETE;
  }

  /**
   * config state of the element is not defined.
   *
   * @deprecated replaced by {@link CopyOfEASEEService.ConfigState#NODEFINE}
   * @author gpe2si
   */
  @Deprecated
  public static final String CONFIG_STATE_UNDEFINED = "NODEFINE";

  /**
   * config state of the element is disabled.
   *
   * @deprecated replaced by {@link CopyOfEASEEService.ConfigState#DISABLED}
   * @author gpe2si
   */
  @Deprecated
  public static final String CONFIG_STATE_DISABLED = "DISABLED";

  /**
   * config state of the element is not enabled.
   *
   * @deprecated replaced by {@link CopyOfEASEEService.ConfigState#ENABLED}
   * @author gpe2si
   */
  @Deprecated
  public static final String CONFIG_STATE_ENABLED = "ENABLED";

  /**
   * config state of the element is not hidden.
   *
   * @deprecated replaced by {@link CopyOfEASEEService.ConfigState#HIDDEN}
   * @author gpe2si
   */
  @Deprecated
  public static final String CONFIG_STATE_HIDDEN = "HIDDEN";

  /**
   * config state of the element is not obsolete.
   *
   * @deprecated replaced by {@link CopyOfEASEEService.ConfigState#QBSOLETE}
   * @author gpe2si
   */
  @Deprecated
  public static final String CONFIG_STATE_OBSOLETE = "QBSOLETE";

  /**
   * config state of the element can not be determined.
   *
   * @deprecated replaced by {@link CopyOfEASEEService.ConfigState#NOTAVAILABLE}
   * @author gpe2si
   */
  @Deprecated
  public static final String CONFIG_STATE_NOTAVAILABLE = "NOTAVAILABLE";

  // --------------------------------------------------------------------------
  // -----

  /* WebService time out for requests */
  private static final int WEB_SERVICE_TIME_OUT_DEFAULT = 300000;
  private final int webServiceTimeOut;

  /* WebService constants */
  private static final String SESSION_HANDLE_ERROR = "ERROR";
  private static final String WEB_SERVICE_LOGIN_ERROR = "FAILED! (exception)";
  private static final String WEB_SERVICE_SESSION_VALID = "Valid";

  @SuppressWarnings("unused")
  private static final String WEB_SERVICE_SESSION_INVALID = "Invalid";

  /* local handles for the WebServices */
  private static CDMWebSessionService wsSession = null;
  private static CDMWebDomainService wsDomain = null;
  private static CDMWebProjectService wsProject = null;
  private static CDMWebDataSetService wsDataSet = null;
  private static CDMWebVersionService wsVersion = null;
  private static CDMWebIDMService wsIdm = null;

  /* current WebService session, might be changed during runtime */
  private String sessionHandle;

  /* Flag if the WebService is logged in */
  private boolean isWebServiceLoggedIn = false;

  /* Flag if the API is logged in */
  private boolean isApiLoggedIn = false;

  /* database connect information, used by WebService and API */
  /* the database connection information is set via the init method */
  /* current username */
  private String username = "";

  /* curent password */
  private String password = "";

  /* current domain */
  private String domain = "";

  /* local variable for current database (PRO or QUA) */
  private int server = 0;

  /* API handle for the application */
  private IApplication app = null;

  /* API handles for the client */
  private IClientApi2 clientApi2 = null;

  /* API handle for the eASEE.cdm domain */
  private ICDMDomain cdmDomain = null;

  /* API handle for CdmStudio */
  private ICDMStudioApi cdmStudio = null;

  /* flag, if API connection has been established by the library */
  private boolean newlyConnected = false;

  /* the logger used by the library */
  private final ILoggerAdapter logger;

  /**
   * Create an instance of the eASEE service library
   *
   * @param logger logger for messages
   */
  public EASEEService(final ILoggerAdapter logger) {
    super();

    this.logger = logger;
    this.webServiceTimeOut = WEB_SERVICE_TIME_OUT_DEFAULT;
  }

  /**
   * Create an instance of the eASEE service library
   *
   * @param logger logger for messages
   * @param timeOut WebService time out in ms
   */
  public EASEEService(final ILoggerAdapter logger, final int timeOut) {
    super();

    this.logger = logger;
    this.webServiceTimeOut = timeOut;
  }

  /**
   * Get a WeService connection Returns WEB_SERVICE_LOGIN_ERROR if the WebService session cannot be established Sets the
   * isWebServiceLoggedIn flag according to the log in state
   *
   * @param username
   * @param password
   * @return the WebService session handle
   */
  private String getWebServiceConnection(final String username, final String password) {
    String sHandle = SESSION_HANDLE_ERROR;

    // WebService URLs depending on selected server
    String urlSession = "";
    String urlDomain = "";
    String urlProject = "";
    String urlVersion = "";
    String urlDataSet = "";
    String urlIdm = "";

    // WebService connect info depending on selected server
    String webServiceProtocol = "";
    String webServiceServer = "";
    String webServicePort = "";
    String webServiceName = "";

    this.logger.info("using cacerts file: " + System.getProperty("javax.net.ssl.trustStore"));

    // set the connect info depending on the server
    switch (this.server) {
      case DGS_CDM_PRO:
        this.logger.info("connect WebService for: " + username + " at DGS_CDM_PRO");

        webServiceProtocol = WEB_SERVICE_PROTOCOL_PRO;
        webServicePort = WEB_SERVICE_PORT_PRO;
        webServiceServer = WEB_SERVICE_SERVER_PRO;
        webServiceName = WEB_SERVICE_NAME_PRO;

        break;

      case DGS_CDM_QUA:
        this.logger.info("connect WebService for: " + username + " at DGS_CDM_QUA");

        webServiceProtocol = WEB_SERVICE_PROTOCOL_QUA;
        webServicePort = WEB_SERVICE_PORT_QUA;
        webServiceServer = WEB_SERVICE_SERVER_QUA;
        webServiceName = WEB_SERVICE_NAME_QUA;

        break;

      default:
        this.logger.fatal("No WebService Server defined!");

        break;
    }

    // set the URLs depending on the server
    urlSession =
        webServiceProtocol + webServiceServer + ":" + webServicePort + webServiceName + WEB_SERVICE_SESSION_SERVICE;
    urlDomain =
        webServiceProtocol + webServiceServer + ":" + webServicePort + webServiceName + WEB_SERVICE_DOMAIN_SERVICE;
    urlProject =
        webServiceProtocol + webServiceServer + ":" + webServicePort + webServiceName + WEB_SERVICE_PROJECT_SERVICE;
    urlVersion =
        webServiceProtocol + webServiceServer + ":" + webServicePort + webServiceName + WEB_SERVICE_VERSION_SERVICE;
    urlDataSet =
        webServiceProtocol + webServiceServer + ":" + webServicePort + webServiceName + WEB_SERVICE_DATASET_SERVICE;
    urlIdm = webServiceProtocol + webServiceServer + ":" + webServicePort + webServiceName + WEB_SERVICE_IDM_SERVICE;

    // set the log level temporarily to INFO to avoid too much messages
    int oldLogLevel = this.logger.getLogLevel();
    this.logger.setLogLevel(ILoggerAdapter.LEVEL_INFO);

    // connect to eASEE.cdm
    try {

      wsSession = new CDMWebSessionService(urlSession, this.webServiceTimeOut);
      wsDomain = new CDMWebDomainService(urlDomain, this.webServiceTimeOut);
      wsProject = new CDMWebProjectService(urlProject, this.webServiceTimeOut);
      wsVersion = new CDMWebVersionService(urlVersion, this.webServiceTimeOut);
      wsDataSet = new CDMWebDataSetService(urlDataSet, this.webServiceTimeOut);
      wsIdm = new CDMWebIDMService(urlIdm, this.webServiceTimeOut);

      sHandle = wsSession.login(username, password, this.domain);

      if (sHandle.equalsIgnoreCase(WEB_SERVICE_LOGIN_ERROR)) {
        this.logger.error("WebService connection can not be established!");
        this.isWebServiceLoggedIn = false;
      }
      else {
        this.isWebServiceLoggedIn = true;
        wsSession.setExtendedLogging(sHandle, true);
        this.logger.info("WebService login successful!");
        this.logger.debug("Session handle: " + sHandle);
      }
    }
    catch (CDMWebServiceException | NoClassDefFoundError e) {
      this.isWebServiceLoggedIn = false;
      this.logger.fatal("unexpected Error while Login: " + e.getLocalizedMessage());
    }
    finally {
      // reset the log level
      this.logger.setLogLevel(oldLogLevel);
    }

    return sHandle;
  }

  /**
   * Get the eASEE.cdm domain handle
   *
   * @return the eASEE.cdm domain handle or NULL if the API is not logged in
   * @throws EASEEServiceException
   */
  public ICDMDomain getCdmDomain() throws EASEEServiceException {
    if (isApiLoggedIn()) {
      return this.cdmDomain;
    }
    else {
      return null;
    }
  }

  /**
   * Check, if the WebService is logged in
   *
   * @return Flag, if the Web Service is logged in
   */
  public boolean isWebServiceLoggedIn() {
    this.logger.debug("check WebService connection");

    if (this.isWebServiceLoggedIn) {
      // check connection
      if (!isConnectionValid()) {
        this.sessionHandle = getWebServiceConnection(this.username, this.password);
      }
    }
    else {
      this.sessionHandle = getWebServiceConnection(this.username, this.password);
    }

    return this.isWebServiceLoggedIn;
  }

  /**
   * Check if the WebService is logged in and if the session is still valid.
   *
   * @return flag if the session is still valid
   */
  private boolean isConnectionValid() {
    boolean isValid = false;
    String sessionState;

    try {
      sessionState = wsSession.getSessionState(this.sessionHandle);

      this.logger.debug("sessionState: " + sessionState);

      isValid = sessionState.equals(WEB_SERVICE_SESSION_VALID);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error when checking if the WebService session is valid");
      isValid = false;
    }

    return isValid;
  }

  /**
   * Check, if the API is logged in. If not, log in via API.
   *
   * @return API is logged in.
   * @throws EASEEServiceException
   */
  public boolean isApiLoggedIn() throws EASEEServiceException {
    this.logger.debug("check API connection");

    if (this.isApiLoggedIn) {
      // TODO: check connection
    }
    else {
      switch (this.server) {
        case DGS_CDM_PRO:

          getApiConnection(this.username, this.password, this.domain, API_ALIAS_PRO);

          break;

        case DGS_CDM_QUA:
          getApiConnection(this.username, this.password, this.domain, API_ALIAS_QUA);

          break;

        default:
          break;
      }
    }

    return this.isApiLoggedIn;
  }

  /**
   * Get details about an eASEE.cdm API version object. Returns a String with following components: <br>
   * (class of the object) : (element name) / (variant) ; (revision) <br>
   * If parameter withVersionNumber is TRUE: string starts with <br>
   * (version number) => <br>
   *
   * @param versObj the eASEE.cdm version object to be analyzed
   * @param withVersNumber TRUE : display the version number <br>
   *          FALSE: don't display the version number
   * @return the version details as a String
   */
  public static String getVersionInfo(final IVersionObj versObj, final boolean withVersNumber) {
    String versionInfo;

    if (withVersNumber) {
      versionInfo = versObj.number() + " => " + versObj._class() + " : " + versObj.elementName() + " / " +
          versObj.variant() + " ; " + versObj.revision();
    }
    else {
      versionInfo =
          versObj._class() + " : " + versObj.elementName() + " / " + versObj.variant() + " ; " + versObj.revision();
    }

    return versionInfo;
  }

  /**
   * Get details about an eASEE.cdm API version object. Returns a String with following components: <br>
   * (class of the object) : (element name) / (variant) ; (revision) <br>
   *
   * @param versObj the eASEE.cdm version object to be analyzed
   * @return the version details as a String
   */
  public static String getVersionInfo(final IVersionObj versObj) {
    return getVersionInfo(versObj, true);
  }

  /**
   * Get details about an eASEE.cdm WebService version object. Returns a String with following components: <br>
   * (class of the object) : (element name) / (variant) ; (revision) <br>
   * If parameter withVersionNumber is TRUE: string starts with <br>
   * (version number) => <br>
   *
   * @param versObj the eASEE.cdm version object to be analyzed
   * @param withVersNumber TRUE : display the version number <br>
   *          FALSE: don't display the version number
   * @return the version details as a String
   */
  public static String getVersionInfo(final ObjInfoEntryType versObj, final boolean withVersNumber) {
    String versionInfo;

    if (withVersNumber) {
      versionInfo = versObj.getVersionNo() + " => " + versObj.getObjClass() + " : " + versObj.getObjName() + " / " +
          versObj.getObjVariant() + " ; " + versObj.getObjRevision();
    }
    else {
      versionInfo = versObj.getObjClass() + " : " + versObj.getObjName() + " / " + versObj.getObjVariant() + " ; " +
          versObj.getObjRevision();
    }

    return versionInfo;
  }

  /**
   * Get details about an eASEE.cdm WebService version object. Returns a String with following components: <br>
   * (class of the object) : (element name) / (variant) ; (revision) <br>
   *
   * @param versObj the eASEE.cdm version object to be analyzed
   * @return the version details as a String
   */
  public static String getVersionInfo(final ObjInfoEntryType versObj) {
    return getVersionInfo(versObj, true);
  }

  /**
   * eASEE.cdm does not allow all characters in element names This method modifies names to be valid for eASEE.cdm
   *
   * @param name the name to be checked and modified
   * @return adapted name to be used in eASEE.cdm
   */
  public static String adaptName4eASEE(final String name) {
    String newName = name;

    newName = newName.replaceAll("ï¿½", "ae");
    newName = newName.replaceAll("ï¿½", "oe");
    newName = newName.replaceAll("ï¿½", "ue");
    newName = newName.replaceAll("ï¿½", "Ae");
    newName = newName.replaceAll("ï¿½", "Oe");
    newName = newName.replaceAll("ï¿½", "Ue");
    newName = newName.replaceAll("ï¿½", "ss");
    newName = newName.replaceAll("'", "");

    return newName;
  }

  /**
   * Initialize the eASEE Service with username, password, domain and Server This method must be called once
   *
   * @param user
   * @param password
   * @param domain
   * @param server
   */
  public void init(final String user, final String password, final String domain, final int server) {
    this.username = user;
    this.password = password;
    this.domain = domain;
    this.server = server;
    try {
      if (DGS_CDM_PRO == server) {
        API_ALIAS_PRO = DBPasswordRetrival.getInstance().getAPIAlias("VCDM_API_ALIAS_PRO");
        if ((API_ALIAS_PRO == null) || API_ALIAS_PRO.isEmpty()) {
          useDefaultAPIAlias();
        }
        this.logger.info("API Alias:" + API_ALIAS_PRO);
      }
      else if (DGS_CDM_QUA == server) {
        API_ALIAS_QUA = DBPasswordRetrival.getInstance().getAPIAlias("VCDM_API_ALIAS_QUA");
        if ((API_ALIAS_QUA == null) || API_ALIAS_QUA.isEmpty()) {
          useDefaultAPIAlias();
        }
        this.logger.info("API Alias:" + API_ALIAS_QUA);
      }
    }
    catch (ConnectionException e) {
      this.logger.error(e.getLocalizedMessage());
      useDefaultAPIAlias();
    }
  }

  /**
   * To use the default Alias if the password webservice is down
   */
  public void useDefaultAPIAlias() {
    API_ALIAS_PRO = "CDM_PS_PRO";
    API_ALIAS_QUA = "CDM_PS_QUA";
  }

  /**
   * Get the API handles and connect if not yet connected. If the client is still running, it might be connected.
   *
   * @param user
   * @param password
   * @param domain
   * @param server
   * @throws EASEEServiceException
   */
  private void getApiConnection(final String user, final String password, final String domain, final String server)
      throws EASEEServiceException {
    try {
      // get eASEE.cdm object
      this.app = com.bosch.easee.eASEE_ComAPI.ClassFactory.createApplication();

      // connect to eASEE.cdm
      if (!this.app.isConnected()) {
        this.logger.info("connecting vCDM API for: " + this.username + " at " + server);

        try {
          this.app.connect(user, password, domain, server);
          this.newlyConnected = true;

          if (!this.app.isConnected()) {
            this.isApiLoggedIn = false;
            throw new EASEEServiceException("could not log in to vCDM API.");
          }
        }
        catch (Exception e) {
          throw new EASEEServiceException("could not log in to vCDM API." + e.getLocalizedMessage());
        }
      }

      this.clientApi2 = this.app.clientApi2();

      // get the CDM domain object
      Com4jObject cdmDomainObject = this.app.getPlugin("NEAdh.Application");
      this.cdmDomain = cdmDomainObject.queryInterface(ICDMDomain.class);

      // get the CDM Studio API
      this.cdmStudio = this.cdmDomain.getCdmStudio();

      this.isApiLoggedIn = true;
    }
    catch (EASEEServiceException e) {
      this.isApiLoggedIn = false;
      throw e;
    }
    catch (Exception e) {
      this.isApiLoggedIn = false;
      throw new EASEEServiceException("unknow exception when getting API connection! " + e);
    }
  }

  /**
   * Get the correct DeliverTargetClass for an eASEE class. This method is to be used when using the API.
   *
   * @param easeeClass
   * @return the API DeliverTargetClass
   */
  public EDeliverTargetClass getDeliverTargetClass(final String easeeClass) {
    EDeliverTargetClass targetClass = null;

    if (easeeClass.equals("PAR")) {
      targetClass = EDeliverTargetClass.DTCPAR;
    }
    else if (easeeClass.equals("FPAR")) {
      targetClass = EDeliverTargetClass.DTCFPAR;
    }
    else if (easeeClass.equals("EPAR")) {
      targetClass = EDeliverTargetClass.DTCEPAR;
    }
    else if (easeeClass.equals("ZFPAR")) {
      targetClass = EDeliverTargetClass.DTCZFPAR;
    }
    else if (easeeClass.equals("EPAR")) {
      targetClass = EDeliverTargetClass.DTCEPAR;
    }
    else if (easeeClass.equals("HEX")) {
      targetClass = EDeliverTargetClass.DTCHEX;
    }
    else if (easeeClass.equals("EXD")) {
      targetClass = EDeliverTargetClass.DTCEXD;
    }
    else {
      targetClass = EDeliverTargetClass.DTCAUTO;
    }

    return targetClass;
  }

  /**
   * Discard all objects used by the eASEE Service library. If the API has been connected by the library, disconnect.
   * Disconnect from the WebService. ATTENTION: Currently, the API handles are not disposed, since this forces errors.
   */
  public void discard() {

    // disconnect from vCDM if connection established by this program
    if (this.newlyConnected) {
      if (this.cdmDomain != null) {
        this.cdmDomain.dispose();
      }
      if (this.clientApi2 != null) {
        this.clientApi2.dispose();
      }
      if (this.app != null) {
        this.app.exit();
        this.app.dispose();
      }
      this.logger.info("vCDM API logged Out successfully");
    }

    if ((this.sessionHandle != null) && (this.sessionHandle.length() > 0)) {
      try {
        boolean isLoggedOut = wsSession.logout(this.sessionHandle);

        if (isLoggedOut) {
          this.logger.info("Logout successful!");
        }
        else {
          this.logger.error("Error while logout!");
        }
      }
      catch (CDMWebServiceException e) {
        // TODO Auto-generated catch block
        this.logger.error("Error during logout : " + e.getLocalizedMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * Get an eASEE version object via the API.
   *
   * @param versionNumber of the object
   * @return the IVersionObj object
   * @throws EASEEServiceException
   */
  public IVersionObj getVersion(final int versionNumber) throws EASEEServiceException {
    isApiLoggedIn();

    this.logger.debug("getting version number: " + versionNumber);

    return this.clientApi2.getVersionObject(versionNumber);
  }

  /**
   * Get a eASEE Version object by versionNumber via WebService If the version could not be found, a warning will be
   * logged and the method will return NULL.
   *
   * @param versionNumber the versionNumber to be searched
   * @return the version object information
   */
  public ObjInfoEntryType getVersion(final String versionNumber) {
    ObjInfoEntryType objInfo = null;

    this.logger.debug("searching version: " + versionNumber);

    isWebServiceLoggedIn();

    // get the version via WebService

    String xmlString = "<Filter><AND><Version VersionNumber=\"" + versionNumber + "\"/></AND></Filter>";
    try {
      List<ObjInfoEntryType> objectList = wsDomain.searchObjectsByXml(this.sessionHandle, xmlString);
      this.logger.debug(objectList.size() + " objects found");

      int numObjectsFound = objectList.size();

      if (numObjectsFound == 1) {
        objInfo = objectList.get(0);
      }
      else if (numObjectsFound == 0) {
        this.logger.warn("Version with ID " + versionNumber + " not found!");
      }
      else {
        this.logger.fatal("getVersion with version number returns " + numObjectsFound + " versions");
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.fatal("unexpected error in getVersion(" + versionNumber + ")");
      this.logger.fatal(e.getMessage());
    }

    return objInfo;
  }

  /**
   * Check, if a version is content of a container using the WebService
   *
   * @param containerVersionNumber
   * @param contentVersionNumber
   * @return TRUE if the version is contained in the container
   */
  public boolean isContent(final String containerVersionNumber, final String contentVersionNumber) {
    List<ObjInfoEntryType> objInfoList = null;

    isWebServiceLoggedIn();

    try {
      objInfoList = wsVersion.searchContent(this.sessionHandle, null, containerVersionNumber);

      for (ObjInfoEntryType objInfo : objInfoList) {
        if (objInfo.getVersionNo().equals(contentVersionNumber)) {
          return true;
        }
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Deliver a HEX file to an eASEE.cdm DST using the WebService. The HEX file is delivered as the HEX object and not as
   * an EXD or anything else.
   *
   * @param hexFilePath
   * @param dstVersion
   * @param activate
   * @return version number of the delivered HEX file or NULL in case of erors
   */
  public String deliverHexFile(final String hexFilePath, final String dstVersion, final boolean activate) {
    String newHexVersion = null;

    isWebServiceLoggedIn();

    DeliveryFileInfoType deliveryFileInfoType = DeliveryFileInfoType.builder(hexFilePath, activate);

    try {
      newHexVersion = wsDataSet.doHexDelivery(this.sessionHandle, dstVersion, deliveryFileInfoType);
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
      // CDMSL-58
      // e.printStackTrace();
    }

    return newHexVersion;
  }

  /**
   * Deliver a DOCU file into a DST. The name of the DOCU object will be the file name. The variant of the DOCU object
   * will be "-". If the File is still existing with the same name (ORIGINAL FILE attribute), it will be re-used. If the
   * file is existing but different, it will not be assigned to the DST. In this case an error message will be logged
   * and NULL will be returned.
   *
   * @param docuFilePath The Path of the DOCU file to be delivered
   * @param dstVersion The DST into which the DOCU file should be delivered
   * @return The eASEE VersionID of the DOCU object
   */
  public String deliverDocuFile(final String docuFilePath, final String dstVersion) {
    isWebServiceLoggedIn();

    // the version ID of the assigned DOCU object
    String newDocuVersion = null;

    // list of DOCU objects when checking if the object is still existing
    List<ObjInfoEntryType> docuObjList;

    List<ObjInfoEntryType> newDocuVersions;
    FileObjCreationInfoType[] docuInfoTypes = new FileObjCreationInfoType[1];
    FileObjReferenceInfoType[] docuReferenceInfoTypes = new FileObjReferenceInfoType[1];

    Map<String, String> emptyMap = new HashMap<String, String>();
    Map<String, String> versionAttr = new HashMap<String, String>();

    // the DOCU file to be delivered
    File docuFile = new File(docuFilePath);

    // create legal eASEE object name
    String docuObjName = adaptName4eASEE(docuFile.getName());

    // variant of DOCU object is always "-"
    String docuObjVariant = "-";

    // Extension of the file is always the File Type. If the file is not supported then error will be thrown!
    String docuObjType = FilenameUtils.getExtension(docuFilePath);

    // check if the object is still existing in eASEE.cdm
    docuObjList = getObjects(EASEEObjClass.DOCU, docuObjName, docuObjVariant, null, null);

    if (docuObjList.size() > 0) {
      // check, if one of the objects is equal
      for (ObjInfoEntryType objInfoEntryType : docuObjList) {
        this.logger.debug("check if DOCU is equal to an existing one: " + getVersionInfo(objInfoEntryType));

        if (isEqualEaseeFile(objInfoEntryType, docuFilePath)) {
          // file is equal, check if still existing in DST
          if (isContent(dstVersion, objInfoEntryType.getVersionNo())) {
            this.logger.info("DOCU file is still content of DST");

            return objInfoEntryType.getVersionNo();
          }
          else {
            this.logger.info("DOCU file still existing in eASEE.cdm, will be re-used");
          }

          // create
          FileObjReferenceInfoType docuReferenceInfoType =
              FileObjReferenceInfoType.builder(objInfoEntryType.getVersionNo(), objInfoEntryType.getObjClass());
          // content attributes must be set to an empty map as a
          // work-around (bug in WebService)
          docuReferenceInfoType.setContentAttrs(emptyMap);
          docuReferenceInfoTypes[0] = docuReferenceInfoType;

          break;
        }
      }

      if (docuReferenceInfoTypes[0] == null) {
        this.logger.error("DOCU file found in eASEE but not identical!");
        this.logger.error("DOCU file will not be delivered");

        return null;
      }
    }

    if (docuReferenceInfoTypes[0] == null) {
      this.logger.info("creating new DOCU object ...");

      Date origDate = new Date(docuFile.lastModified());

      FileObjCreationInfoType fileObjCreationInfoType = FileObjCreationInfoType.builder(docuFilePath, docuObjName,
          docuObjVariant, EASEEObjClass.DOCU.toString(), docuObjType);
      fileObjCreationInfoType.setElemAttrs(emptyMap);
      fileObjCreationInfoType.setVersionAttrs(versionAttr);
      fileObjCreationInfoType.setContentAttrs(emptyMap);

      versionAttr.put("ORIGINAL DATE", getOrigDateString(origDate));
      versionAttr.put("ORIGINAL FILE", docuFile.getName());

      docuInfoTypes[0] = fileObjCreationInfoType;
    }

    try {
      if (docuInfoTypes[0] == null) {
        newDocuVersions = wsDataSet.doDocuDelivery(this.sessionHandle, dstVersion, null, docuReferenceInfoTypes);
      }
      else {
        newDocuVersions =
            wsDataSet.doDocuDelivery(this.sessionHandle, dstVersion, docuInfoTypes, docuReferenceInfoTypes);
      }

      newDocuVersion = newDocuVersions.get(0).getVersionNo();

      if (newDocuVersion != null) {
        this.logger.info("DOCU file assigned to DST, VersionNumber: " + newDocuVersion);
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
      e.printStackTrace();
    }

    return newDocuVersion;
  }

  public boolean checkInPureHexDst(final String dstVersion) {
    boolean checkInState = false;
    String[] srcDstVersions = new String[1];

    isWebServiceLoggedIn();

    srcDstVersions[0] = dstVersion;

    try {
      checkInState = wsDataSet.checkInPureHexDST(this.sessionHandle, srcDstVersions);
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
      e.printStackTrace();
    }

    return checkInState;
  }

  public List<ObjInfoEntryType> getContent(final String objClass, final String containerVersion) {
    List<ObjInfoEntryType> contents = null;

    isWebServiceLoggedIn();

    try {
      contents = wsVersion.searchContent(this.sessionHandle, objClass, containerVersion);
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
      e.printStackTrace();
    }

    return contents;
  }

  public void removeFromContainer(final String containerVersion, final List<ObjInfoEntryType> contentList) {
    isWebServiceLoggedIn();

    HashMap<String, Set<String>> objectsToRemove = new HashMap<String, Set<String>>();

    HashSet<String> contents = new HashSet<String>();

    for (ObjInfoEntryType content : contentList) {
      contents.add(content.getVersionNo());
    }

    if (contents.size() <= 0) {
      return;
    }
    else {
      objectsToRemove.put(containerVersion, contents);
    }

    try {
      wsVersion.removeFromContainerRelationship(this.sessionHandle, objectsToRemove);
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * @param origDate
   * @return
   */
  private String getOrigDateString(final Date origDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    dateFormat.applyPattern("yyyy'-'MM'-'dd HH':'mm':'ss");

    return dateFormat.format(origDate);
  }

  /**
   * @param aprjFilter
   * @param isCheckedIn
   * @return
   */
  public List<ObjInfoEntryType> getAprj(final String aprjFilter, final boolean isCheckedIn) {
    return getObjects(EASEEObjClass.APRJ, aprjFilter, null, null, isCheckedIn);
  }

  public List<ObjInfoEntryType> getAprj(final String aprjFilter) {
    return getObjects(EASEEObjClass.APRJ, aprjFilter, null, null, null);
  }

  /**
   * @param aprj
   * @return
   */
  public ICDMCalibrationProject getAprj(final ObjInfoEntryType aprj) {
    ICDMCalibrationProject apiAprj;

    // get APRJ by search criteria
    ICDMCalibrationProjectCol calProjCollection = this.cdmDomain.searchCalibrationProjects(aprj.getObjName(),
        aprj.getObjVariant(), Integer.parseInt(aprj.getObjRevision()));
    this.logger.info("APRJ found: " + calProjCollection.count());

    // get the first APRJ from result list
    apiAprj = ((Com4jObject) calProjCollection.item(1)).queryInterface(ICDMCalibrationProject.class);

    return apiAprj;
  }

  /**
   * @deprecated please use {@link #getObjects(EASEEObjClass, String, String, Integer, Boolean)} instead. gp2si.
   * @param objClass
   * @param objName
   * @param objVariant
   * @param objRevision
   * @param checkedOut
   * @return
   */
  @Deprecated
  public List<ObjInfoEntryType> getObjects(final String objClass, final String objName, final String objVariant,
      final Integer objRevision, final Boolean checkedOut) {

    // TODO [gpe2si]: delete this method.

    isWebServiceLoggedIn();

    List<ObjInfoEntryType> objectList = null;
    int numberOfObjects = 0;

    String objRevisionStr;

    if ((objRevision == null) || (objRevision.intValue() == -1)) {
      objRevisionStr = null;
    }
    else {
      objRevisionStr = objRevision.toString();
    }

    try {
      objectList = wsDomain.searchObjects(this.sessionHandle, objClass, objName, objVariant, objRevisionStr, null, null,
          checkedOut);

      numberOfObjects = objectList.size();

      this.logger.debug(numberOfObjects + " objects found (" + objClass + " : " + objName + " / " + objVariant + " ; " +
          objRevisionStr + ")");

      return objectList;
    }
    catch (CDMWebServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @author gpe2si
   */
  @Override
  public List<ObjInfoEntryType> getObjects(final EASEEObjClass objClass, final String objName, final String objVariant,
      final Integer objRevision, final Boolean objChkInState) {

    // TODO

    assert (objClass != null) : "ObjClass is mandatory!";

    // List of results.
    List<ObjInfoEntryType> objList = null;

    // ObjRevision as String.
    String objRevisionStr = null;

    if ((objRevision != null) && (objRevision.intValue() != -1)) {
      objRevisionStr = objRevision.toString();
    }

    // Search for objects. Returns null if no Obj was found.
    objList = searchObjects(objClass, objName, objVariant, objRevisionStr, null, null, objChkInState);

    int numberOfObjects = 0;

    if (objList != null) {
      numberOfObjects = objList.size();
    }

    this.logger.debug(numberOfObjects + " objects found (" + objClass.toString() + " : " + objName + " / " +
        objVariant + " ; " + objRevisionStr + ")");

    return objList;
  }

  /**
   * @param xmlString -> "<Filter><AND><Version Class=\"" + fileClass + "\" Domain=\"CDM\"
   *          useVersionName=\"true\"/><Attribute DataType=\"STR\" Domain=\"CDM\" Name=\"ORIGINAL FILE\"
   *          Operator=\"equal\" Type=\"VER\" Value=\"" + eASEEFileName + "\"/><Attribute DataType=\"INT\"
   *          Domain=\"CDM\" Name=\"File size\" Operator=\"equal\" Type=\"SYS\" Value=\"" + fileSize +
   *          "\"/></AND></Filter>"
   * @return
   */
  public List<ObjInfoEntryType> getObjects(final String xmlString) {
    isWebServiceLoggedIn();

    List<ObjInfoEntryType> objectList = null;
    int numberOfObjects = 0;

    try {
      objectList = wsDomain.searchObjectsByXml(this.sessionHandle, xmlString);

      numberOfObjects = objectList.size();

      this.logger.debug(numberOfObjects + " objects found (" + xmlString + ")");

      return objectList;
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Unexpected error while getting objects : " + e.getMessage());
    }

    return null;
  }

  /**
   * @param versionNumber
   * @return
   * @throws EASEEServiceException
   */
  public ICDMDataset getDataset(final int versionNumber) throws EASEEServiceException {
    isApiLoggedIn();

    return this.cdmDomain.getDataset(versionNumber);
  }

  /**
   * @param objInfoList
   * @return
   */
  public ObjInfoEntryType getLatest(final List<ObjInfoEntryType> objInfoList) {
    ObjInfoEntryType latestObjInfo = null;
    ObjInfoEntryType objInfo;

    for (Iterator<ObjInfoEntryType> iObjInfo = objInfoList.iterator(); iObjInfo.hasNext();) {
      objInfo = iObjInfo.next();

      if (!objInfo.getObjChkInState()) {
        return objInfo;
      }
      else if (latestObjInfo == null) {
        latestObjInfo = objInfo;
      }
      else if (Integer.parseInt(latestObjInfo.getVersionNo()) < Integer.parseInt(objInfo.getVersionNo())) {
        latestObjInfo = objInfo;
      }
    }

    return latestObjInfo;
  }

  /**
   * @param origFileName
   * @return
   */
  public List<ObjInfoEntryType> getParameterSet(final String origFileName) {
    isWebServiceLoggedIn();

    List<ObjInfoEntryType> objectList = null;

    String eASEEFileName = EASEEService.adaptName4eASEE(origFileName);

    String xmlString =
        "<Filter><AND><Version Domain=\"CDM\" useVersionName=\"true\"/><Attribute DataType=\"STR\" Domain=\"CDM\" Name=\"ORIGINAL FILE\" Operator=\"equal\" Type=\"VER\" Value=\"" +
            eASEEFileName + "\"/></AND></Filter>";
    try {
      objectList = wsDomain.searchObjectsByXml(this.sessionHandle, xmlString);

      this.logger.debug(objectList.size() + " objects found (" + origFileName + ")");
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Unexpected error while getting objects with originalfilename : " + e.getMessage());
    }

    return objectList;
  }

  /**
   * @param fileClass
   * @param origFileName
   * @param fileSize
   * @return
   */
  public List<ObjInfoEntryType> getParameterSet(final String fileClass, final String origFileName,
      final long fileSize) {
    isWebServiceLoggedIn();

    List<ObjInfoEntryType> objectList = null;

    String eASEEFileName = EASEEService.adaptName4eASEE(origFileName);


    String xmlString = "<Filter><AND><Version Class=\"" + fileClass +
        "\" Domain=\"CDM\" useVersionName=\"true\"/><Attribute DataType=\"STR\" Domain=\"CDM\" Name=\"ORIGINAL FILE\" Operator=\"equal\" Type=\"VER\" Value=\"" +
        eASEEFileName +
        "\"/><Attribute DataType=\"INT\" Domain=\"CDM\" Name=\"File size\" Operator=\"equal\" Type=\"SYS\" Value=\"" +
        fileSize + "\"/></AND></Filter>";

    try {
      objectList = wsDomain.searchObjectsByXml(this.sessionHandle, xmlString);

      this.logger.info(objectList.size() + " objects found (" + origFileName + ")");
    }
    catch (CDMWebServiceException e) {
      this.logger
          .error("unexpected error while getting parameterset with Originial file name and size : " + e.getMessage());
    }

    return objectList;
  }

  /**
   * Create a new revision of a DST. Returns NULL in case of problems.
   *
   * @param objInfo
   * @return Info of the newly created DST revision
   */
  public ObjInfoEntryType checkOutDst(final ObjInfoEntryType objInfo) {
    isWebServiceLoggedIn();

    ObjInfoEntryType newObjInfo = null;

    try {
      // TODO: check, if version is a DST
      // TODO: check, if version is checked in
      // TODO: check, if version is latest revision
      this.logger.info("creating new revision of: " + getVersionInfo(objInfo, false));

      String result = wsDataSet.createDSTRevision(this.sessionHandle, objInfo.getVersionNo());
      this.logger.debug("CreateDSTRevision result: " + result);

      newObjInfo = getVersion(result);
      this.logger.info("new revision created: " + getVersionInfo(newObjInfo, false));

    }
    catch (CDMWebServiceException e) {
      this.logger.warn("Can't create DST revision: " + getVersionInfo(objInfo, false));
      this.logger.warn(e.getLocalizedMessage());
    }

    return newObjInfo;
  }

  /**
   * @deprecated because of:
   *             <ul>
   *             <li>missing Parameter in Method-Header
   *             <li>missing CDMWebServiceException-Handle
   *             </ul>
   *             better use {@link #getProgramKeys(String, List)}
   * @author gpe2si
   * @param aprjVersionNumber
   * @return
   */
  @Deprecated
  public List<WSProgramKey> getProgramKeys(final String aprjVersionNumber) {

    // TODO [gpe2si]: delete this method.

    isWebServiceLoggedIn();

    List<WSProgramKey> programKeys = null;

    try {
      programKeys = wsProject.getProgramKeys(this.sessionHandle, aprjVersionNumber, null);
    }
    catch (CDMWebServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return programKeys;
  }

  /**
   * @param aprjVersionNumber
   * @return
   */
  public List<WSVariantKey> getProductKeys(final String aprjVersionNumber) {
    isWebServiceLoggedIn();

    List<WSVariantKey> productKeys = null;

    String productKeyFilter = null;

    try {
      productKeys = wsProject.getProductKeys(this.sessionHandle, aprjVersionNumber, productKeyFilter);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error in getting the product keys for APRJ " + e.getLocalizedMessage(), e);
    }

    return productKeys;
  }

  /**
   * @param versionNumber
   * @return
   */
  public WSAttrMapList getVersionAttributes(final String versionNumber) {
    isWebServiceLoggedIn();

    WSAttrMapList versionAttributes = null;

    try {
      versionAttributes = wsVersion.getVersionAttributes(this.sessionHandle, versionNumber);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error in getting the version attributes " + e.getLocalizedMessage(), e);
    }

    return versionAttributes;
  }


  /**
   * Set Version Attributes for a particular version object
   *
   * @param versionNumber VersionNumber of the object to be modified
   * @param attrMap Structure with attributes and values to be set
   * @return true if o.k., false if not o.k.
   */
  public boolean setVersionAttributes(final String versionNumber, final WSAttrMapList attrMap) {
    isWebServiceLoggedIn();

    try {
      wsVersion.setVersionAttributes(this.sessionHandle, versionNumber, attrMap);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error in setting the version attributes " + e.getLocalizedMessage(), e);
      return false;
    }

    return true;
  }

  /**
   * @param versionNumber
   * @return
   */
  public WSAttrMapList getElementAttributes(final String versionNumber) {
    isWebServiceLoggedIn();

    WSAttrMapList elementAttributes = null;

    try {
      elementAttributes = wsVersion.getElementAttributes(this.sessionHandle, versionNumber);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error in getting the element attributes " + e.getLocalizedMessage(), e);
    }

    return elementAttributes;
  }

  /**
   * @author pdb1kor
   * @param versionNumber
   * @return
   */
  public WSAttrMapList getSystemAttributes(final String versionNumber) {
    isWebServiceLoggedIn();

    WSAttrMapList systemAttributes = null;

    try {
      systemAttributes = wsVersion.getSystemAttributes(this.sessionHandle, versionNumber);

    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error in getting the system attribute " + e.getLocalizedMessage(), e);
    }

    return systemAttributes;
  }

  /**
   * Get the content attribute for specified container and content version number.
   *
   * @param containerVersionNo
   * @param contentVersionNo
   * @return
   */
  public WSAttrMapList getContentAttributes(final String containerVersionNo, final String contentVersionNo) {
    isWebServiceLoggedIn();

    WSAttrMapList contentAttributes = null;

    try {
      contentAttributes = wsVersion.getContentAttributes(this.sessionHandle, containerVersionNo, contentVersionNo);

    }
    catch (CDMWebServiceException e) {
      this.logger.fatal("Error while getting content attributes: " + e.getLocalizedMessage());
    }

    return contentAttributes;
  }

  /**
   * Set the content attribute for specified container and content version number.
   *
   * @param containerVersionNo
   * @param contentVersionNo
   * @param contentAttributes
   * @return
   */
  public void setContentAttributes(final String containerVersionNo, final String contentVersionNo,
      final WSAttrMapList contentAttributes) {
    isWebServiceLoggedIn();

    try {
      wsVersion.setContentAttributes(this.sessionHandle, containerVersionNo, contentVersionNo, contentAttributes);

    }
    catch (CDMWebServiceException e) {
      this.logger.fatal("Error while setting content attributes: " + e.getLocalizedMessage());
    }

  }


  /**
   * Get the configuration states of a list of objects in containers
   *
   * @param contentRelations
   * @return
   */
  public Map<String, Map<String, String>> getConfigState(final Map<String, Set<String>> contentRelations) {
    isWebServiceLoggedIn();

    Map<String, Map<String, String>> contentStates = null;

    try {
      contentStates = wsVersion.getConfigState(this.sessionHandle, contentRelations);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting configuration states  " + e.getLocalizedMessage(), e);
    }

    return contentStates;
  }

  /**
   * @param one
   * @param two
   * @return
   * @throws IOException
   */
  public boolean compareExactly(final File one, final File two) throws IOException {
    if (one.length() == two.length()) {
      FileInputStream fis1 = null;
      FileInputStream fis2 = null;
      try {
        fis1 = new FileInputStream(one);
        fis2 = new FileInputStream(two);
        int temp = 0;
        byte[] buffer1 = new byte[102400];
        byte[] buffer2 = new byte[102400];

        while ((temp = fis1.read(buffer1)) != -1) {
          if (temp != fis2.read(buffer2)) {
            // files do not have the same size
            return false;
          }
          else {
            // compare the buffers
            for (int offset = 0; offset < temp; offset++) {
              if (buffer1[offset] != buffer2[offset]) {
                return false;
              }
            }
          }
        }

        return true;
      }
      catch (FileNotFoundException e) {
        this.logger.fatal("File not found in compare exactly!", e);
      }

      finally {
        if (fis1 != null) {
          fis1.close();
        }
        if (fis2 != null) {
          fis2.close();
        }
      }
    }
    return false;
  }

  /**
   * @param objEntry
   * @param referenceFile
   * @return
   */
  public boolean isEqualEaseeFile(final ObjInfoEntryType objEntry, final String referenceFile) {
    boolean result = false;

    String tempFile = System.getenv("TEMP") + "\\eASEE_compare_temp.tmp";

    try {
      getEaseeFile(objEntry, tempFile);

      File fileTemp = new File(tempFile);

      if (getFileExtension(referenceFile).equalsIgnoreCase("ZIP")) {
        result = compareZipFile(fileTemp, new File(referenceFile));
      }
      else {
        result = compareExactly(fileTemp, new File(referenceFile));
      }
      fileTemp.delete();
    }
    catch (EASEEServiceException | IOException e) {
      this.logger.fatal(e.getMessage());
    }

    return result;
  }

  /**
   * Get the version object based on the query specified
   *
   * @author pdb1kor
   * @param xmlString "<Filter><AND><Version Class=\"" + fileClass + "\" Domain=\"CDM\"
   *          useVersionName=\"true\"/><Attribute DataType=\"STR\" Domain=\"CDM\" Name=\"ORIGINAL FILE\"
   *          Operator=\"equal\" Type=\"VER\" Value=\"" + eASEEFileName + "\"/><Attribute DataType=\"INT\"
   *          Domain=\"CDM\" Name=\"File size\" Operator=\"equal\" Type=\"SYS\" Value=\"" + fileSize +
   *          "\"/></AND></Filter>"
   * @return
   */
  public IVersionCol getVersions(final String xmlString) {

    IVersionCol vcol = null;

    try {

      if (isApiLoggedIn()) {

        vcol = this.clientApi2.searchVersionsByXml(xmlString);

      }
      else {

        return null;
      }

    }
    catch (Exception e) {
      this.logger.fatal(e.getMessage());
    }
    return vcol;
  }

  /**
   * @param objEntry
   * @param destFile
   * @return
   * @throws EASEEServiceException
   */
  public boolean getEaseeFile(final ObjInfoEntryType objEntry, final String destFile) throws EASEEServiceException {
    try (FileOutputStream fos = new FileOutputStream(destFile)) {
      return getEaseeResourceStream(objEntry, fos);
    }
    catch (FileNotFoundException e) {
      this.logger.fatal("Error: File not found! " + e);
    }
    catch (IOException e) {
      this.logger.fatal("Error: " + e);
    }
    return false;
  }

  /**
   * @param objEntry
   * @param destOutputStream
   * @return
   * @throws EASEEServiceException
   */
  public boolean getEaseeResourceStream(final ObjInfoEntryType objEntry, final OutputStream destOutputStream)
      throws EASEEServiceException {
    return getEaseeResourceStream(objEntry.getVersionNo(), destOutputStream);
  }

  /**
   * @param objEntry
   * @param destOutputStream
   * @return
   * @throws EASEEServiceException
   */
  public boolean getEaseeResourceStream(final String objVersionNo, final OutputStream destOutputStream)
      throws EASEEServiceException {
    if (!isWebServiceLoggedIn()) {
      this.logger.debug("Login to Webservice failed ...");
      return false;
    }
    // execute the request
    this.logger.debug("Getting file from eASEE ...");
    try (InputStream in = wsVersion.fetchArtifact(this.sessionHandle, objVersionNo)) {
      this.logger.debug("Reading file ...");
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) >= 0) {
        destOutputStream.write(buffer, 0, len);
      }
      this.logger.debug("File saved: " + destOutputStream);
      return true;
    }
    catch (CDMWebServiceException e) {
      this.logger.fatal("Error getting File after re-connect");
      this.logger.fatal("  Message: " + e.getMessage());
      throw new EASEEServiceException("Error while getting vCDM File " + e.getLocalizedMessage());
    }
    catch (IOException e) {
      this.logger.fatal("Error: " + e);
      throw new EASEEServiceException("IO Exception while getting vCDM File " + e.getLocalizedMessage());
    }

  }


  /**
   * @deprecated because method-name not like the the delegated.<br>
   *             replaced by {@link #getActivatedProductAttributeValues(String projectVersionNo, String name)} gpe2si.
   * @param aprjVersNumber
   * @param attrName
   * @return
   */
  @Deprecated
  public WSAttrMapList getAprjProductAttributeValues(final String aprjVersNumber, final String attrName) {

    // TODO [gpe2si]: delete this method.

    WSAttrMapList attrMapList = null;

    isWebServiceLoggedIn();

    try {
      attrMapList = wsProject.getActivatedProductAttributeValues(this.sessionHandle, aprjVersNumber, attrName);
    }
    catch (CDMWebServiceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return attrMapList;
  }

  // -----------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public final WSAttrMapList getActivatedProductAttributeValues(final String projectVersionNo, final String name) {

    WSAttrMapList attrMapList = null;

    isWebServiceLoggedIn();

    try {
      attrMapList = wsProject.getActivatedProductAttributeValues(this.sessionHandle, projectVersionNo, name);
    }
    catch (CDMWebServiceException e) {
      e.printStackTrace();
    }

    return attrMapList;
  }

  /**
   * {@inheritDoc}
   *
   * @author gpe2si
   */
  @Override
  public final WSAttrMapList getProductAttributeValues(final String name) {

    WSAttrMapList attrMapList = null;

    isWebServiceLoggedIn();

    try {
      attrMapList = wsDomain.getProductAttributeValues(this.sessionHandle, name);
    }
    catch (CDMWebServiceException e) {
      e.printStackTrace();
    }

    return attrMapList;
  }

  /**
   * Create Product Attributes and Values in the Domain. Since the attribute name in vCDM might not match the given
   * attribute name (different upper/lower case), the correct name from vCDM will be get
   *
   * @param attr the name of the attribute
   * @param prdAttrVal the list of values of the attribute
   * @author sdh1cob
   * @throws CDMWebServiceException
   */
  public final void createProductAttributeAndValue(final String attr, final List<String> prdAttrVal)
      throws CDMWebServiceException {
    isWebServiceLoggedIn();

    try {
      // get the attribute name as defined in vCDM
      String vCDMAttributeName = validateAttributeName(attr);

      this.logger.debug(
          "creating product attr in domain - ATTR: " + vCDMAttributeName + " :: VALUES: " + prdAttrVal.toString());


      wsDomain.createProductAttributeAndValues(this.sessionHandle, vCDMAttributeName, prdAttrVal);

    }
    catch (CDMWebServiceException e) {
      this.logger
          .error("ERROR creating product attr in domain - ATTR: " + attr + " :: VALUES: " + prdAttrVal.toString());
      this.logger.error(e.getLocalizedMessage());

      throw e;
    }
  }


  /**
   * Create a product key in an APRJ and assign attribute values.
   *
   * @param verNum the version number of the APRJ
   * @param prodKey the product key
   * @param productAttrMap the attributes and values
   * @author sdh1cob
   * @throws CDMWebServiceException
   */
  public final void createProductKey(final String verNum, final String prodKey,
      final Map<String, String> productAttrMap)
      throws CDMWebServiceException {

    // a single attribute and value
    WSProductAttributeValue prodAttrVal;

    // a list of values for a single attribute
    List<String> valuesList;

    // the list of all attributes and values
    List<WSProductAttributeValue> prodAttrValList = new ArrayList<WSProductAttributeValue>();

    // iterate over all attributes and values
    for (Entry<String, String> entry : productAttrMap.entrySet()) {

      // check if a value is defined for the attribute
      if (entry.getValue() != null) {
        // create a new values list
        valuesList = new ArrayList<String>();
        // create a new attribute and value structure
        prodAttrVal = new WSProductAttributeValue();

        // add the value to the values list
        valuesList.add(entry.getValue());
        // define the attribute name
        prodAttrVal.setName(entry.getKey());
        // define the values list
        prodAttrVal.setValuesList(valuesList);
        // add the attribute to the list of all attributes
        prodAttrValList.add(prodAttrVal);
      }
    }

    isWebServiceLoggedIn();

    try {
      this.logger.debug("creating product key - VERSION: " + verNum + " :: PROD_KEY: " + prodKey);

      if (!isVariantExistsInAPRJ(verNum, prodKey)) {
        // create and activate the product key
        wsProject.createProductKey(this.sessionHandle, verNum, prodKey, prodAttrValList, WSActivationFlag.ACTIVATE);
      }
      else {

        String errorMsg = "Variant name: " + prodKey +
            " is already existing in another case. A variant name must be case-insensitive unique in a vCDM APRJ.";

        throw new CDMWebServiceException(errorMsg);
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error("error creating product key - VERSION: " + verNum + " :: PROD_KEY: " + prodKey);
      this.logger.error(e.getLocalizedMessage());
      throw e;
    }
  }

  /**
   * To check if the variant to be created is already existing in the APRJ in a different case. This method is inlcuded
   * to avoid the duplicate variant creation in the APRJ due to the different in case.
   *
   * @param verNum
   * @param prodKey
   * @return variantExists
   */
  public final boolean isVariantExistsInAPRJ(final String verNum, final String prodKey) {

    boolean exists = false;

    List<WSVariantKey> existingVariants = getProductKeys(verNum);

    if ((existingVariants != null) && !existingVariants.isEmpty()) {

      for (WSVariantKey variant : existingVariants) {

        if (variant.getName().equalsIgnoreCase(prodKey) && !variant.getName().equals(prodKey)) {
          exists = true;
          break;
        }

      }
    }

    return exists;
  }

  /**
   * Activate Attribute and Values in APRJ Supports only one attribute with a list of values. Since the attribute name
   * in vCDM might not match the given attribute name (different upper/lower case), the correct name from vCDM will be
   * get If the list of values is empty, nothing will happen.
   *
   * @param attr the attribute to be activated
   * @param attrValList the list of values to be activated
   * @param aprjVerNo the APRJ for which the values should be activated
   * @author sdh1cob
   * @throws CDMWebServiceException
   */
  public final void setProductAttributeValue(final String attr, final List<String> attrValList, final String aprjVerNo)
      throws CDMWebServiceException {

    // check, if values are in the list
    if (((attrValList) == null) || (attrValList.size() == 0)
    // even, if the list has no values, the size is 1!!
        || (attrValList.get(0) == null)) {
      // no values in the list, nothing can be defined

      this.logger.warn("attribute with empty value list will not be activated in APRJ: " + attr);

      return;
    }

    isWebServiceLoggedIn();

    try {
      // the list of all attributes and values to be activated
      List<WSProductAttributeValue> prAttrValList = new ArrayList<WSProductAttributeValue>();
      // one attribute with the list of values to be activated
      WSProductAttributeValue prdAttributeValue = new WSProductAttributeValue();

      // get the attribute name as defined in vCDM
      String vCDMAttributeName = validateAttributeName(attr);

      // define the attribute and values to be activated
      prdAttributeValue.setName(vCDMAttributeName);
      prdAttributeValue.setValuesList(attrValList);

      // add the attribute and values to the list
      prAttrValList.add(prdAttributeValue);

      this.logger.debug("activating attribute values in APRJ - " + " APRJ: " + aprjVerNo + " :: ATTR: " + attr +
          " :: VALUES: " + attrValList.toString());

      // activate
      wsProject.setProductAttributeValuesState(this.sessionHandle, aprjVerNo, prAttrValList, WSActivationFlag.ACTIVATE);

    }
    catch (CDMWebServiceException e) {
      this.logger.error("attribute values can not be activating in APRJ - " + " APRJ: " + aprjVerNo + " :: ATTR: " +
          attr + " :: VALUES: " + attrValList.toString());
      this.logger.error(e.getLocalizedMessage());
      throw e;
    }
  }

  /**
   * Activate Attribute and Values in APRJ If the list of values is empty, nothing will happen.
   *
   * @param aprjVerNo the version number of the APRJ
   * @param attributesAndValues the list of attributes and values to be activated
   * @throws CDMWebServiceException
   */
  public final void setProductAttributeValues(final String aprjVerNo,
      final List<WSProductAttributeValue> attributesAndValues)
      throws CDMWebServiceException {

    // check, if values are in the list
    if (((attributesAndValues) == null) || (attributesAndValues.size() == 0)) {
      // no values in the list, nothing can be defined

      return;
    }

    isWebServiceLoggedIn();

    try {

      for (WSProductAttributeValue attrAndValues : attributesAndValues) {
        this.logger.debug("activating attribute values in APRJ - " + " APRJ: " + aprjVerNo + " :: ATTR: " +
            attrAndValues.getName() + " :: VALUES: " + attrAndValues.getValuesList());
      }

      // activate
      wsProject.setProductAttributeValuesState(this.sessionHandle, aprjVerNo, attributesAndValues,
          WSActivationFlag.ACTIVATE);

    }
    catch (CDMWebServiceException e) {
      this.logger.error("ERROR when activation attributes and values for APRJ - " + " APRJ: " + aprjVerNo);
      this.logger.error(e.getLocalizedMessage());

      throw e;
    }
  }

  /**
   * Set the project context (many) for objects
   *
   * @param projContext Key: object to be set, Value: projects list
   */
  public final void setProjectContext(final Map<String, Set<String>> projContext) {

    if (isWebServiceLoggedIn()) {
      try {

        // set context
        ItemStates<String> itemStates = wsVersion.setProjectContext(this.sessionHandle, projContext);
        this.logger.info("Project Context set for items: " + itemStates.size());

      }
      catch (CDMWebServiceException e) {
        this.logger.error("ERROR when setting project context!");
        this.logger.error(e.getLocalizedMessage());

      }
    }

  }

  /**
   * Check if an attribute with the given name exists in vCDM with a different upper/lower case name. If so, the vCDM
   * name will be returned. If the attribute exists with the same name or if the attribute does not exist, the given
   * name will be returned.
   *
   * @param origAttrName the attribute name to be checked
   * @return the vCDM name or the original name if not existing
   */
  private String validateAttributeName(final String origAttrName) {
    // the return value
    String vCDMAttributeName = origAttrName;

    // get all product attributes defined in the domain
    WSAttrMapList attrValues = getProductAttributeValues(null);

    if ((attrValues != null) && (attrValues.size() > 0)) {
      // some attributes are existing in the domain

      // iterate over all attributes of the domain
      for (String vcdmAttribute : attrValues.keySet()) {
        // check if the vCDM attribute is equal to the attribute to be checked
        if (vcdmAttribute.toUpperCase().equals(origAttrName.toUpperCase())) {
          // return the vCDM attribute name
          vCDMAttributeName = vcdmAttribute;

          if (!vcdmAttribute.equals(origAttrName)) {
            this.logger.warn("attribute exists in vCDM with different style: " + origAttrName + " => " + vcdmAttribute);
          }

          break;
        }
      }

    }
    else {
      // no attributes in the domain
      vCDMAttributeName = origAttrName;
    }

    return vCDMAttributeName;
  }

  /**
   * {@inheritDoc}
   *
   * @author gpe2si
   */
  @Override
  public final WSAttrMapList getProductKeyAttributeValues(final String projectVersionNo, final String name) {

    WSAttrMapList attrMapList = null;

    isWebServiceLoggedIn();

    try {
      attrMapList = wsProject.getProductKeyAttributeValues(this.sessionHandle, projectVersionNo, name);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting product key attribute values  " + e.getLocalizedMessage(), e);
    }

    return attrMapList;
  }

  /**
   * {@inheritDoc}
   *
   * @author gpe2si
   */
  @Override
  public final List<WSProgramKey> getProgramKeys(final String projectVersionNo, final List<String> names) {

    List<WSProgramKey> programKeys = null;

    isWebServiceLoggedIn();

    try {
      programKeys = wsProject.getProgramKeys(this.sessionHandle, projectVersionNo, names);
    }
    catch (CDMWebServiceException e) {
      // CDMSL-58
      this.logger.error("Getting Program Keys failed " + e.getMessage());
    }

    return programKeys;
  }

  /**
   * {@inheritDoc}
   *
   * @author gpe2si
   */
  @Override
  public final List<ObjInfoEntryType> searchObjects(final EASEEObjClass objClass, final String objName,
      final String objVariant, final String objRevision, final String objType, final String objDomain,
      final Boolean objChkInState) {

    assert (objClass != null) : "ObjClass is mandatory!";

    List<ObjInfoEntryType> foundObjs = null;

    isWebServiceLoggedIn();

    try {
      foundObjs = EASEEService.wsDomain.searchObjects(this.sessionHandle, objClass.name(), objName, objVariant,
          objRevision, objType, objDomain, objChkInState);
    }
    catch (CDMWebServiceException e) {
      e.printStackTrace();
    }

    return foundObjs;
  }

  /**
   * {@inheritDoc}
   *
   * @author gpe2si
   * @throws CDMWebServiceException
   */
  @Override
  public Set<IParameterValue> getDataSetValues(final String versionId, final String[] parameterNames)
      throws CDMWebServiceException {

    Set<IParameterValue> paramSet = null;

    isWebServiceLoggedIn();

    try {
      paramSet = wsDataSet.getDataSetValues(this.sessionHandle, versionId, parameterNames);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting dataset values " + e.getLocalizedMessage(), e);
      throw e;
    }

    return paramSet;

  }

  /**
   * Deletes a version object.
   *
   * @param vers_nr -version number of version to delete
   * @param comment -delete comment
   * @author pdb1kor
   */
  public void deleteVersionObject(final int vers_nr, final String comment) {

    try {

      if (isApiLoggedIn()) {
        this.clientApi2.deleteVersionObject(vers_nr, comment);
      }
    }
    catch (Exception e) {
      this.logger.fatal("Delete version failed. " + e.getMessage());
    }
  }

  /**
   * A Checkout without reservation is executed and the file is placed in the given directory.
   *
   * @param version
   * @param filePath
   * @author pdb1kor
   */
  public void fetchVersionObject(final IVersionObj version, final String filePath) {

    try {

      if (isApiLoggedIn()) {
        this.clientApi2.fetchFile(version, filePath);
      }

    }
    catch (Exception e) {
      this.logger.fatal("Fetch failed " + e.getMessage());
    }

  }

  /**
   * Creates a new PVD Object inclusive Workpackages with Functions and Labels. Uses default values for version comment
   * and version description
   *
   * @param pvdName name of the PVD to be created
   * @param pvdVariant variant of the PVD to be created
   * @param wpFuncMap function mapping
   * @param wpLabelMap label mapping
   * @return true if PVD created, false if not
   */
  public boolean createPvdByObjects(final String pvdName, final String pvdVariant,
      final Map<String, List<String>> wpFuncMap, final Map<String, List<String>> wpLabelMap) {

    ObjInfoEntryType pvd;

    String versionComment = "";
    String versionDescription = "This PVD was created via Webservice";

    pvd = createPvdByObjects(pvdName, pvdVariant, wpFuncMap, wpLabelMap, versionComment, versionDescription);

    return pvd != null;
  }

  /**
   * Creates a new PVD Object inclusive Workpackages with Functions and Labels.
   *
   * @param pvdName name of the PVD to be created
   * @param pvdVariant variant of the PVD to be created
   * @param wpFuncMap function mapping
   * @param wpLabelMap label mapping
   * @param versionComment version comment
   * @param versionDescription version description
   * @return newly created PVD object
   */
  public ObjInfoEntryType createPvdByObjects(final String pvdName, final String pvdVariant,
      final Map<String, List<String>> wpFuncMap, final Map<String, List<String>> wpLabelMap,
      final String versionComment, final String versionDescription) {
    boolean retVal = true;

    // object factory for PVD business objects
    ObjectFactory factory = new ObjectFactory();

    ObjInfoEntryType version = null;

    // create new PVD
    com.vector.easee.cdm.server.gen.pvd.PVD pvd = factory.createPVD();

    if (wpFuncMap != null) { // compute Workpackage - Functions - Map

      for (Map.Entry<String, List<String>> e : wpFuncMap.entrySet()) {
        // create new work package and assign functions to it
        WP wp = factory.createWP();
        wp.setNAME(e.getKey());
        this.logger.info("Create WP: " + wp.getNAME());
        WP.FUNCLIST funcList = factory.createWPFUNCLIST();
        // Adding functions
        List<String> wpFuncList = e.getValue();
        for (int x = 0; x < wpFuncList.size(); x++) { // assign
          // functions to
          // WP
          WP.FUNCLIST.FUNC func = factory.createWPFUNCLISTFUNC();
          func.setNAME(wpFuncList.get(x));
          funcList.getFUNC().add(func);
        }

        wp.setFUNCLIST(funcList);

        if ((wpLabelMap != null) && (wpLabelMap.containsKey(e.getKey()))) { // check if there are also Label for this WP

          WP.PARAMLIST paramList = factory.createWPPARAMLIST();
          // Adding functions
          List<String> wpLabelList = wpLabelMap.get(e.getKey()); // e.getValue();
          for (int x = 0; x < wpLabelList.size(); x++) { // assign Label to same WP
            WP.PARAMLIST.PARAM param = factory.createWPPARAMLISTPARAM();
            param.setNAME(wpLabelList.get(x));
            paramList.getPARAM().add(param);
          }

          wp.setPARAMLIST(paramList);
        }

        // add new Work package
        pvd.getWP().add(wp);
      } // end for
    }

    if (wpLabelMap != null) { // compute Workpackage - Label - Map

      for (Map.Entry<String, List<String>> e : wpLabelMap.entrySet()) {
        // create new work package and assign labels to it
        if ((wpFuncMap != null) && (wpFuncMap.containsKey(e.getKey()))) {
          // check if there are also Functions for this WP which
          // already assigned to WP in WP-Func-Map handling before!

        }
        else {

          WP wp = factory.createWP();
          wp.setNAME(e.getKey());
          this.logger.info("Create WP: " + wp.getNAME());
          WP.PARAMLIST paramList = factory.createWPPARAMLIST();
          // Adding Label
          List<String> wpLabelList = e.getValue();
          for (int x = 0; x < wpLabelList.size(); x++) { // assign Label to WP
            WP.PARAMLIST.PARAM param = factory.createWPPARAMLISTPARAM();
            param.setNAME(wpLabelList.get(x));
            paramList.getPARAM().add(param);
          }

          wp.setPARAMLIST(paramList);
          // add new Work package
          pvd.getWP().add(wp);
        }
      } // end for
    }

    if ((wpFuncMap == null) && (wpLabelMap == null)) { // end if wpFuncMap
      retVal = false;
      this.logger.error("No Functions/Label defined for PVD creation!");
    } // end if wpFuncMap

    if (retVal) {
      retVal = false;
      // create XML from PVD
      String xmlString = XmlManager.readStreamFromPVD(pvd);
      // this.logger.info(xmlString);
      try {
        ValidationResultType validationResult = new ValidationResultType();

        isWebServiceLoggedIn();

        version = wsDomain.createPVD(this.sessionHandle, pvdName, pvdVariant, versionComment, xmlString, null, null,
            validationResult);

        // Check the overall validation Result
        if (validationResult.getValidationState() != ValidationStateType.ERROR) {
          this.logger.debug("Validation State: " + validationResult.getValidationState());
          this.logger.info("PVD created successfully: " + version.getObjClass() + ":" + version.getObjName() + "/" +
              version.getObjVariant() + ";" + version.getObjRevision());
          retVal = true;
        }

        // get all the infos from the validation result
        ValidationMessage[] infos = validationResult.getValidationMessages();

        if (infos != null) {
          for (ValidationMessage info : infos) {
            this.logger.info(info.getMessageType().toString() + " - " + info.getMessage());
          }
        }
      }
      catch (Exception ex) {
        this.logger.fatal(ex.getMessage());
      }
    }
    return version;
  }


  /**
   * Creates a new PRD Object with Workpackages, Functions and/or Labels. Implementation used e.g. by P.A.L. tool.
   *
   * @param PRD -Name
   * @param PRD -Variant
   * @param Group -Name_Subgroup-Name_Type_Map
   * @param true -WP_Resp_Type_Map/false -Resp_WP_Type_Map
   * @author nde2si
   */
  public boolean createPrdByObjects(final String prdName, final String prdVariant,
      final HashMap<String, HashMap<String, HashMap<EASEEService.PrdListType, List<String>>>> groupSubgroupTypeMap,
      final boolean b_wp_resp_order, final String ntSysAdminName, final String sysAdminPW) {
    // Group Subgroup Type Label or Fkt or WP
    // true = WP - Resp - Type - Label/ false = Resp - WP - Type - Label
    boolean retVal = true;
    List<String> usedGroupNames = new ArrayList<String>();
    List<String> usedSubGroupNames = new ArrayList<String>();

    // object factory for PRD business objects
    com.vector.easee.cdm.server.gen.prd.ObjectFactory factory = new com.vector.easee.cdm.server.gen.prd.ObjectFactory();

    // create new PVD
    com.vector.easee.cdm.server.gen.prd.PRD prd = factory.createPRD();

    if (groupSubgroupTypeMap != null) { // compute Group - Subgroup - Type - Label/Fkt/WP Map

      HashMap<String, UserItem> allUsersMap = getAllUser(ntSysAdminName, sysAdminPW);

      // Set<UserItem> users = searchUser(null, "HEIKO", null, ntSysAdminName, sysAdminPW);
      // for (UserItem i : users) {
      // for(String id : i.getAssignedAccountIDs()){
      // System.out.println("Assigned account id: " + id);
      // }
      // System.out.println("UserID: " + i.getID());
      // System.out.println("Default location: " + i.getDefaultLocationName());
      // System.out.println("Department: " + i.getDepartment());
      // System.out.println("E-mail: " + i.getEMail());
      // System.out.println("First name: " + i.getFirstName());
      // System.out.println("Last name: " + i.getLastName());
      // System.out.println("Phone: " + i.getPhone());
      // System.out.println("Short name: " + i.getShortName());
      // }

      for (Map.Entry<String, HashMap<String, HashMap<EASEEService.PrdListType, List<String>>>> e_Group : groupSubgroupTypeMap
          .entrySet()) {
        // Group level
        HashMap<String, HashMap<EASEEService.PrdListType, List<String>>> subGroupTypeMap = e_Group.getValue();
        // create new Group and assign Subgroups to it
        com.vector.easee.cdm.server.gen.prd.USERGROUP group = factory.createUSERGROUP();
        String groupName = e_Group.getKey();
        if (!b_wp_resp_order) { // User names (responsibilities with NTUser) in root groups
          group.setNAME("_" + groupName); // group name with prefix "_" because the user and the group needs unequal
          // names
          if (!usedGroupNames.contains("_" + groupName)) {
            usedGroupNames.add("_" + groupName);
          }
          this.logger.info("Create group: _" + groupName);
          if (usedSubGroupNames.contains("_" + groupName)) {
            retVal = false;
            this.logger.error("Same name found for group and subgroup: " + "_" + groupName + "! That's not allowed!");
          }
          if (allUsersMap.containsKey(groupName.toUpperCase())) {
            this.logger.debug("Add user " + groupName + " to PRD.");
            com.vector.easee.cdm.server.gen.prd.USER user = factory.createUSER();
            user.setNAME(groupName.toUpperCase());
            if (!group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().contains(user)) {
              group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().add(user);
            }
          }
        }
        else {
          group.setNAME(groupName);
          if (!usedGroupNames.contains(groupName)) {
            usedGroupNames.add(groupName);
          }
          this.logger.info("Create group: " + groupName);
          if (usedSubGroupNames.contains(groupName)) {
            retVal = false;
            this.logger.error("Same name found for group and subgroup: " + groupName + "! That's not allowed!");
          }
        }

        for (Map.Entry<String, HashMap<EASEEService.PrdListType, List<String>>> e_SubGroup : subGroupTypeMap
            .entrySet()) {
          // SubGroup level
          HashMap<EASEEService.PrdListType, List<String>> typeMap = e_SubGroup.getValue();
          // create new Subgroup and assign Label/Functions/Workpackages to it (depends on type)
          com.vector.easee.cdm.server.gen.prd.USERGROUP subGroup = factory.createUSERGROUP();
          String subGroupName = e_SubGroup.getKey();
          if (usedGroupNames.contains(subGroupName)) {
            retVal = false;
            this.logger.error("Same name found for group and subgroup: " + subGroupName + "! That's not allowed!");
          }
          if (!usedSubGroupNames.contains(subGroupName)) {
            usedSubGroupNames.add(subGroupName);
          }
          subGroup.setNAME(subGroupName);
          this.logger.debug("Create subgroup: " + subGroupName);
          if (b_wp_resp_order) { // User names (responsibilities with NTUser) in sub groups
            if (allUsersMap.containsKey(subGroupName.toUpperCase())) {
              this.logger.debug("Add user " + subGroupName + " to PRD.");
              com.vector.easee.cdm.server.gen.prd.USER user = factory.createUSER();
              user.setNAME(subGroupName.toUpperCase());
              if (!group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().contains(user)) {
                group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().add(user);
              }
            }
            else { // user name not found; add as sub group instead
              group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().add(subGroup);
            }
          }
          else { // no user names in sub groups
            group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().add(subGroup);
          }

          for (Map.Entry<EASEEService.PrdListType, List<String>> e_Type : typeMap.entrySet()) {
            // Type level
            List<String> charFktWpList = e_Type.getValue();
            // add Characteristics/Functions/Workpackages depends on type
            EASEEService.PrdListType type = e_Type.getKey();
            this.logger.debug("Add list of type: " + type);
            boolean newList = false;

            switch (type) {

              case PARAMLIST:
                PARAMLIST paramListGroup = null;

                if (group.getPARAMLIST() == null) {
                  paramListGroup = factory.createPARAMLIST();
                  newList = true;
                }
                else {
                  paramListGroup = group.getPARAMLIST();
                }
                for (int x = 0; x < charFktWpList.size(); x++) {
                  PARAM param = factory.createPARAMLISTPARAM();
                  param.setNAME(charFktWpList.get(x));
                  paramListGroup.getPARAM().add(param);
                }
                if (newList) {
                  group.setPARAMLIST(paramListGroup);
                }
                break;
              case FUNCLIST:
                FUNCLIST funcListGroup = null;

                if (group.getFUNCLIST() == null) {
                  funcListGroup = factory.createFUNCLIST();
                  newList = true;
                }
                else {
                  funcListGroup = group.getFUNCLIST();
                }
                for (int x = 0; x < charFktWpList.size(); x++) {
                  FUNC func = factory.createFUNCLISTFUNC();
                  func.setNAME(charFktWpList.get(x));
                  funcListGroup.getFUNC().add(func);
                }
                if (newList) {
                  group.setFUNCLIST(funcListGroup);
                }
                break;
              case WPLIST:

                WPLIST wpListGroup = null;

                if (group.getWPLIST() == null) {
                  wpListGroup = factory.createWPLIST();
                  newList = true;
                }
                else {
                  wpListGroup = group.getWPLIST();
                }
                for (int x = 0; x < charFktWpList.size(); x++) {
                  com.vector.easee.cdm.server.gen.prd.WPLIST.WP wp = factory.createWPLISTWP();
                  wp.setNAME(charFktWpList.get(x));
                  wpListGroup.getWP().add(wp);
                }
                if (newList) {
                  group.setWPLIST(wpListGroup);
                }
                break;
            }

          } // end for Type

        } // end for SubGroups

        prd.getUSERGROUP().add(group);
      } // end for Groups
    } // end if (groupSubgroupTypeMap != null)


    if (groupSubgroupTypeMap == null) {
      retVal = false;
      this.logger.error("No Functions/Label defined for PRD creation!");
    }

    if (retVal) {
      retVal = false;
      // create XML from PRD
      String xmlString = XmlManager.readStreamFromPRD(prd);
      xmlString = xmlString.replace("PPD>", "PRD>"); // workaround for 4.6.0.15 webservice bug
      // *****
      // try {
      // File file = new File("C:\\temp\\PRD_xmlString.xml");
      // FileOutputStream fos;
      //
      // fos = new FileOutputStream(file);
      //
      // if (!file.exists()) {
      // file.createNewFile();
      // }
      //
      // // get the content in bytes
      // byte[] contentInBytes = xmlString.getBytes();
      //
      // fos.write(contentInBytes);
      // fos.flush();
      // fos.close();
      // fos = null;
      // }
      // catch (IOException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      // *****
      ObjInfoEntryType version = null;
      try {
        ValidationResultType validationResult = new ValidationResultType();

        version = wsDomain.createPRD(this.sessionHandle, prdName, prdVariant, "comment", xmlString, validationResult);

        // Check the overall validation Result
        if (validationResult.getValidationState() != ValidationStateType.ERROR) {
          this.logger.debug("Validation State: " + validationResult.getValidationState());
          this.logger.info("PRD created successfully: " + version.getObjClass() + ":" + version.getObjName() + "/" +
              version.getObjVariant() + ";" + version.getObjRevision());

          retVal = true;
        }

        // get all the infos from the validation result
        ValidationMessage[] infos = validationResult.getValidationMessages();

        if (infos != null) {
          for (ValidationMessage info : infos) {
            this.logger.info(info.getMessageType().toString() + " - " + info.getMessage());
          }
        }
      }
      catch (Exception ex) {
        this.logger.fatal(ex.getMessage());
      }
    }
    return retVal;
  }

  /**
   * Creates a new PRD Object with Workpackages, Functions and/or Labels. Used by iCDM
   *
   * @param PRD -Name
   * @param PRD -Variant
   * @param Group -Name_Subgroup-Name_Type_Map
   * @param true -WP_Resp_Type_Map/false -Resp_WP_Type_Map
   * @author hef2fe
   */
  public ObjInfoEntryType createPrdByObjects2(final String prdName, final String prdVariant,
      final HashMap<String, HashMap<String, HashMap<PrdListType, List<String>>>> prdMap,
      final Map<String, List<String>> aliasNtUserMap, final String ntSysAdminName, final String sysAdminPW,
      final String versionComment, final String versionDescription) {
    // Group Subgroup Type Label or Fkt or WP
    // true = WP - Resp - Type - Label/ false = Resp - WP - Type - Label

    boolean retVal = true;

    ObjInfoEntryType version = null;

    int groupNumber = 0;

    List<String> usedGroupNames = new ArrayList<String>();
    List<String> usedSubGroupNames = new ArrayList<String>();

    List<String> ntUserList;

    // list for group level parameter
    PARAMLIST paramListGroup = null;
    // list for subgroup level parameter
    PARAMLIST paramListSubGroup = null;
    // a single parameter
    PARAM param;

    // parameter validation
    if (prdMap == null) {
      this.logger.error("No Functions/Label defined for PRD creation!");

      return null;
    }

    // object factory for PRD business objects
    com.vector.easee.cdm.server.gen.prd.ObjectFactory factory = new com.vector.easee.cdm.server.gen.prd.ObjectFactory();

    // create new PRD
    com.vector.easee.cdm.server.gen.prd.PRD prd = factory.createPRD();

    // get all vCDM Users
    HashMap<String, UserItem> allUsersMap = getAllUser(ntSysAdminName, sysAdminPW);

    // main loop for RESP groups
    for (Map.Entry<String, HashMap<String, HashMap<EASEEService.PrdListType, List<String>>>> respGroup : prdMap
        .entrySet()) {

      // increment the group number (used to male sub groups with same WP unique
      groupNumber++;

      // create new Group for RESP
      String groupName = respGroup.getKey();

      com.vector.easee.cdm.server.gen.prd.USERGROUP group = factory.createUSERGROUP();
      group.setNAME(groupName);

      // check uniquenss of group names
      if (!usedGroupNames.contains(groupName)) {
        usedGroupNames.add(groupName);
      }
      else {
        this.logger.error("Duplicate group name: " + groupName + "! That's not allowed!");
        retVal = false;
        break;
      }

      // check uniquenss of sub group names
      if (usedSubGroupNames.contains(groupName)) {
        this.logger.error("Same name found for group and subgroup: " + groupName + "! That's not allowed!");
        retVal = false;
        break;
      }

      this.logger.info("Create main group: " + groupName);

      // add the group to the PRD
      prd.getUSERGROUP().add(group);

      // get the NT-Users for RESP group
      ntUserList = aliasNtUserMap.get(groupName);
      if ((ntUserList != null) && !ntUserList.isEmpty()) {
        // NT-Users defined, add all user to Group
        for (String ntUser : ntUserList) {
          // check if NT-user name exists as a user in vCDM
          if (allUsersMap.containsKey(ntUser)) {
            this.logger.debug("Add user " + ntUser + " to Group " + groupName);
            com.vector.easee.cdm.server.gen.prd.USER user = factory.createUSER();
            user.setNAME(ntUser);
            if (!group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().contains(user)) {
              group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().add(user);
            }
          }

        }
      }

      // get subGroups (WP level)
      Map<String, HashMap<EASEEService.PrdListType, List<String>>> wpParameterMap = respGroup.getValue();

      // loop for WP level
      for (Map.Entry<String, HashMap<EASEEService.PrdListType, List<String>>> wpGroup : wpParameterMap.entrySet()) {
        // SubGroup level
        Map<EASEEService.PrdListType, List<String>> parameterMap = wpGroup.getValue();

        // create new Subgroup and assign Label/Functions/Workpackages to it (depends on type)
        com.vector.easee.cdm.server.gen.prd.USERGROUP subGroup = factory.createUSERGROUP();

        // name of subgroup is <WP_NAME>_<RESP> to make it unique
        String subGroupName =
            wpGroup.getKey().substring(0, Math.min(wpGroup.getKey().length(), 70)) + "_[" + groupNumber + "]";

        // check, if subgroup name is used as group name
        if (usedGroupNames.contains(subGroupName)) {
          this.logger.error("Same name found for group and subgroup: " + subGroupName + "! That's not allowed!");
          retVal = false;
          break;
        }

        if (!usedSubGroupNames.contains(subGroupName)) {
          usedSubGroupNames.add(subGroupName);
        }
        else {
          this.logger.error("Duplicate subgroup name: " + subGroupName + "! That's not allowed!");
          retVal = false;
          break;
        }

        this.logger.debug("Create subgroup: " + subGroupName);

        subGroup.setNAME(subGroupName);

        group.getUSERGROUPOrUSEROrSYSTEMUSERGROUP().add(subGroup);

        for (Map.Entry<EASEEService.PrdListType, List<String>> e_Type : parameterMap.entrySet()) {
          // Type level
          List<String> charFktWpList = e_Type.getValue();
          // add Characteristics/Functions/Workpackages depends on type
          EASEEService.PrdListType type = e_Type.getKey();
          this.logger.debug("Add list of type: " + type);

          switch (type) {

            case PARAMLIST:
              // get the group level parameter list
              if (group.getPARAMLIST() == null) {
                paramListGroup = factory.createPARAMLIST();
                group.setPARAMLIST(paramListGroup);
              }
              else {
                paramListGroup = group.getPARAMLIST();
              }

              // get the subgroup level parameter list
              if (subGroup.getPARAMLIST() == null) {
                paramListSubGroup = factory.createPARAMLIST();
                subGroup.setPARAMLIST(paramListSubGroup);
              }
              else {
                paramListSubGroup = subGroup.getPARAMLIST();
              }

              // add all WP parameter to group level and subgroup level parameter lists
              for (int x = 0; x < charFktWpList.size(); x++) {
                param = factory.createPARAMLISTPARAM();
                param.setNAME(charFktWpList.get(x));
                paramListGroup.getPARAM().add(param);

                param = factory.createPARAMLISTPARAM();
                param.setNAME(charFktWpList.get(x));
                paramListSubGroup.getPARAM().add(param);
              }
              break;
            case FUNCLIST:

              this.logger.error("Functions not supported!");
              break;
            case WPLIST:

              this.logger.error("WP not supported!");
              break;
          }

        } // end for Type

      } // end for SubGroups

    } // end for Groups


    if (retVal) {
      retVal = false;
      // create XML from PRD
      String xmlString = XmlManager.readStreamFromPRD(prd);

      try {
        ValidationResultType validationResult = new ValidationResultType();

        isWebServiceLoggedIn();

        version =
            wsDomain.createPRD(this.sessionHandle, prdName, prdVariant, versionComment, xmlString, validationResult);

        // Check the overall validation Result
        if (validationResult.getValidationState() != ValidationStateType.ERROR) {
          this.logger.debug("Validation State: " + validationResult.getValidationState());
          this.logger.info("PRD created successfully: " + version.getObjClass() + ":" + version.getObjName() + "/" +
              version.getObjVariant() + ";" + version.getObjRevision());

          retVal = true;
        }

        // get all the infos from the validation result
        ValidationMessage[] infos = validationResult.getValidationMessages();

        if (infos != null) {
          for (ValidationMessage info : infos) {
            this.logger.info(info.getMessageType().toString() + " - " + info.getMessage());
          }
        }
      }
      catch (Exception ex) {
        this.logger.fatal(ex.getMessage());
      }
    }
    return version;
  }

  /**
   * Get all the Users from vCDM.
   *
   * @return HashMap (userAccountId(uppercase) , UserItem)
   */
  public HashMap<String, UserItem> getAllUser(final String sysAdminName, final String sysAdminPw) {

    Set<UserItem> allUser = null;
    HashMap<String, UserItem> allUserMap = null;

    try {
      allUser = wsIdm.getUsers(sysAdminName, sysAdminPw);
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
    }

    allUserMap = new HashMap<String, UserItem>();

    if (allUser != null) {
      for (UserItem user : allUser) {
        String[] ids = user.getAssignedAccountIDs();
        if (ids != null) {
          for (String id : ids) {
            allUserMap.put(id.toUpperCase(), user);
          }
        }
      }
    }

    return allUserMap;
  }


  /**
   * Search one User in vCDM with NTUser, FirstName, LastName. String = null -> Wildcard
   *
   * @return UserItem
   */
  public Set<UserItem> searchUser(final String ntUser, final String firstName, final String lastName,
      final String sysAdminName, final String sysAdminPw) {

    UserQuery query = new UserQuery();

    query.setAccountID(ntUser);
    query.setLastName(lastName);
    query.setFirstName(firstName);

    Set<UserItem> itemData = null;
    try {
      itemData = wsIdm.getUsersByQuery(sysAdminName, sysAdminPw, query);
    }
    catch (CDMWebServiceException e) {
      this.logger.error(e.getMessage());
    }

    return itemData;
  }

  /**
   * Get the eASEE.cdm Studio version which is used by the current eASEE.cdm version.
   *
   * @return The CDM Studio version
   * @throws EASEEServiceException
   */
  public String getCdmStudioVersion() throws EASEEServiceException {
    String cdmStudioVersion = "not defined";

    isApiLoggedIn();

    cdmStudioVersion = this.cdmStudio.cdmStudioVersion();

    return cdmStudioVersion;
  }

  /**
   * Compare two parameter sets. The parameter sets are compared physically including the units
   *
   * @param a2lFile the A2L file used for both parameter sets
   * @param masterFile the first parameter set
   * @param compareFile the second parameter set
   * @return the compare results (equal, unequal, missing parameters)
   * @throws EASEEServiceException
   */
  public CdmStudioCompareResult cdmStudioCompare(final String a2lFile, final String masterFile,
      final String compareFile)
      throws EASEEServiceException {

    CdmStudioCompareResult compareResult = new CdmStudioCompareResult();

    isApiLoggedIn();

    ICDMStudioCompareSettings compSettings = this.cdmStudio.createCompareSettings();

    // set target, source compared against this file
    ICDMStudioCallObject target = compSettings.createCDMStudioCallObject();
    target.a2lFileName(a2lFile);
    target.fileName(masterFile);
    compSettings.target(target);

    // source 1
    ICDMStudioCallObject source = compSettings.createCDMStudioCallObject();
    source.a2lFileName(a2lFile);
    source.fileName(compareFile);
    compSettings.addSource(source);

    compSettings.mode(ECdmReportMode.CDM_ReportModePhysicalWithUnits);
    compSettings.filter(ECdmReportFilter.CDM_ReportFilterAll);
    // don't create an output file!
    // it is either possible to create an output file or to
    // fill the result lists
    compSettings.reportFileType(ECdmEReportFileType.CDM_ReportFileTypeNone);

    ICDMStudioCompareResult compResult = this.cdmStudio.compare(compSettings);

    if (compResult == null) {
      return compareResult;
    }

    ICDMStudioCompareResultItem resultItem = compResult.resultFromSourceFilename(compareFile);

    compareResult.setEqualParCount(resultItem.equalParameters().count());

    getCdmStudioCompareResult(compareResult.getMissingPar(), resultItem.missingParameters());
    getCdmStudioCompareResult(compareResult.getUnequalPar(), resultItem.unequalParameters());

    return compareResult;
  }


  /**
   * Compare two parameter sets. The parameter sets are compared physically including the units
   *
   * @param masterA2lFile
   * @param masterDataFile
   * @param compareA2LFile
   * @param compareFile
   * @return
   * @throws EASEEServiceException
   */
  public CdmStudioCompareResult cdmStudioCompare(final String masterA2lFile, final String masterDataFile,
      final String compareA2LFile, final String compareFile)
      throws EASEEServiceException {

    CdmStudioCompareResult compareResult = new CdmStudioCompareResult();

    isApiLoggedIn();

    ICDMStudioCompareSettings compSettings = this.cdmStudio.createCompareSettings();
    // set target, source compared against this file
    ICDMStudioCallObject target = compSettings.createCDMStudioCallObject();
    target.a2lFileName(masterA2lFile);
    target.fileName(masterDataFile);
    compSettings.target(target);

    // source 1
    ICDMStudioCallObject source = compSettings.createCDMStudioCallObject();
    source.a2lFileName(compareA2LFile);
    source.fileName(compareFile);
    compSettings.addSource(source);

    compSettings.mode(ECdmReportMode.CDM_ReportModePhysicalWithUnits);
    compSettings.filter(ECdmReportFilter.CDM_ReportFilterAll);
    // don't create an output file!
    // it is either possible to create an output file or to
    // fill the result lists
    compSettings.reportFileType(ECdmEReportFileType.CDM_ReportFileTypeNone);

    ICDMStudioCompareResult compResult = this.cdmStudio.compare(compSettings);

    if (compResult == null) {
      return compareResult;
    }

    ICDMStudioCompareResultItem resultItem = compResult.resultFromSourceFilename(compareFile);

    compareResult.setEqualParCount(resultItem.equalParameters().count());

    getCdmStudioCompareResult(compareResult.getMissingPar(), resultItem.missingParameters());
    getCdmStudioCompareResult(compareResult.getUnequalPar(), resultItem.unequalParameters());

    return compareResult;
  }

  /**
   * Compare two parameter sets. The parameter sets are compared physically including the units. This will also create
   * an output report file in the result file location
   *
   * @param masterA2lFile
   * @param masterDataFile
   * @param compareA2LFile
   * @param compareFile
   * @param resultFile
   * @param fileType
   * @return
   * @throws EASEEServiceException
   */
  public CdmStudioCompareResult cdmStudioCompare(final String masterA2lFile, final String masterDataFile,
      final String compareA2LFile, final String compareFile, final String resultFile,
      final ECdmEReportFileType fileType)
      throws EASEEServiceException {

    CdmStudioCompareResult compareResult = new CdmStudioCompareResult();

    isApiLoggedIn();

    ICDMStudioCompareSettings compSettings = this.cdmStudio.createCompareSettings();
    // set target, source compared against this file
    ICDMStudioCallObject target = compSettings.createCDMStudioCallObject();
    target.a2lFileName(masterA2lFile);
    target.fileName(masterDataFile);
    compSettings.target(target);

    // source 1
    ICDMStudioCallObject source = compSettings.createCDMStudioCallObject();
    source.a2lFileName(compareA2LFile);
    source.fileName(compareFile);
    compSettings.addSource(source);

    compSettings.mode(ECdmReportMode.CDM_ReportModePhysicalWithUnits);
    compSettings.filter(ECdmReportFilter.CDM_ReportFilterAll);

    // Create an output file in the result file location
    compSettings.reportFileType(fileType);
    compSettings.reportFileName(resultFile);
    compSettings.autoOpen(false);

    ICDMStudioCompareResult compResult = this.cdmStudio.compare(compSettings);

    if (compResult == null) {
      return compareResult;
    }

    ICDMStudioCompareResultItem resultItem = compResult.resultFromSourceFilename(compareFile);

    if (resultItem != null) {
      compareResult.setEqualParCount(resultItem.equalParameters().count());

      getCdmStudioCompareResult(compareResult.getMissingPar(), resultItem.missingParameters());
      getCdmStudioCompareResult(compareResult.getUnequalPar(), resultItem.unequalParameters());
    }
    return compareResult;
  }

  /**
   * @param compareResult
   * @param parameters
   */
  private void getCdmStudioCompareResult(final List<String> resultList, final ICDMCollection parameters) {
    for (int i = 1; i <= parameters.count(); i++) {
      String parameter = (String) parameters.item(i);
      resultList.add(parameter);
    }
  }

  /**
   * Get the eASEE version name and original file name of copy/merge protocol files from a DST version
   *
   * @param versionId DST version id
   * @return HashMap(version name, original name)
   */
  public HashMap<String, String> getRelatedLogFiles(final int versionId) {
    HashMap<String, String> retVal = null;
    try {
      if (isApiLoggedIn()) {
        try {

          IVersionObj versObj = getVersion(versionId);
          int anzRel = versObj.getRelationCollection().count();
          this.logger.info("Relations found #: " + anzRel);

          for (Com4jObject comO : versObj.getRelationCollection()) {
            IRelationObj relO = comO.queryInterface(IRelationObj.class); // !!!!! a
            // simple
            // cast
            // is
            // not
            // enough

            IVersionObj sourceVer = relO.getSourceVersion();
            this.logger.info("Relation Source Name: " + sourceVer.versionName());
            this.logger.info(relO.name());
            if (relO.name().equalsIgnoreCase("protocol")) {
              int anz = relO.getTargetVersionCollection().count();
              this.logger.info("Number of Copy/Merge protocol files: " + anz);
              if (anz > 0) {
                retVal = new HashMap<String, String>();
                for (Com4jObject com2O : relO.getTargetVersionCollection()) {
                  IVersionObj protVer = com2O.queryInterface(IVersionObj.class);
                  this.logger.info(protVer.versionName());
                  retVal.put(protVer.versionName(), protVer.fileName());
                }
              }
            }
          }

        }
        catch (NumberFormatException e) {
          this.logger.error(e.getLocalizedMessage());
        }
        catch (EASEEServiceException e) {
          this.logger.error(e.getLocalizedMessage());
        }
      }
    }
    catch (EASEEServiceException e) {
      this.logger.error("ERROR while connecting to eASEE.cdm!");
      this.logger.error(e.getLocalizedMessage());
    }
    return retVal;

  }

  /**
   * Get the A2L version id from the predecessor PST
   *
   * @param versionId DST version id as integer
   * @return String A2L version id (null if not available)
   */
  public String getA2LfromPredecessorPst(final int versionId) {
    String retVal = null;
    try {
      if (isApiLoggedIn()) {
        try {
          IVersionObj versObj = getVersion(versionId); // get DST
          // version
          // object
          String actualPstName = versObj.getVersionAttribute("PST"); // actual
          // PST
          // name
          while (true) {
            int preDst = versObj.getPredecessor().number(); // get
            // version
            // id from
            // predecessor
            // PST
            if (preDst == 0) {
              break; // no predecessor PST available
            }
            else {
              versObj = getVersion(preDst); // get
              // DST
              // version
              // object
              // from
              // pre-DST
              if (versObj != null) {
                String prePstName = versObj.getVersionAttribute("PST"); // pre-PST
                // name
                if (prePstName.equalsIgnoreCase(actualPstName)) {
                  // it's the same PST (--> only a Revision in
                  // eASEE.cdm)
                }
                else {
                  List<ObjInfoEntryType> dstList = searchObjects(EASEEObjClass.DST, versObj.name(), versObj.variant(),
                      Integer.toString(versObj.revision()), null, "CDM", null);
                  if (dstList.size() > 0) {
                    List<ObjInfoEntryType> a2lList = getContent("A2L", dstList.get(0).getVersionNo());
                    if (a2lList.size() > 0) {
                      retVal = a2lList.get(0).getVersionNo(); // get
                      // version
                      // id
                      // from
                      // A2L
                      // object
                      // System.out.println("Actual A2L: "
                      // + actualPstName);
                      // System.out.println("Pre A2L : "
                      // + prePstName);

                    }
                  }
                }
              }
              else {
                break;
              }
            }
          } // end while
        }
        catch (EASEEServiceException e) {
          this.logger.error("ERROR while searching for predecessor PST!");
          this.logger.error(e.getLocalizedMessage());
        }
      }
    }
    catch (EASEEServiceException e) {
      this.logger.error("ERROR while connecting to eASEE.cdm!");
      this.logger.error(e.getLocalizedMessage());
    }
    return retVal; // A2L version number or null
  }

  public void setExtendedLogging(final boolean extLogON) {
    try {
      wsSession.setExtendedLogging(this.sessionHandle, extLogON);
      if (extLogON) {
        this.logger.info("Switch extended Logging ON");
      }
      else {
        this.logger.info("Switch extended Logging OFF");
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error("ERROR while setting Extended Logging!");
      this.logger.error(e.getLocalizedMessage());
    }

  }

  public boolean renameProductAttribute(final String oldName, final String newName) {

    isWebServiceLoggedIn();

    try {
      wsDomain.renameProductAttribute(this.sessionHandle, oldName, newName);

      return true;
    }
    catch (CDMWebServiceException e) {
      this.logger.error("ERROR while renaming a product attribute!");
      this.logger.error(e.getLocalizedMessage());

      return false;
    }

  }


  public boolean renameProductAttributeValues(final String attributeName, final Map<String, String> valueMapping) {

    isWebServiceLoggedIn();

    try {
      wsDomain.renameProductAttributeValues(this.sessionHandle, attributeName, valueMapping);

      return true;
    }
    catch (CDMWebServiceException e) {
      this.logger.error("ERROR while renaming a product attribute values!");
      this.logger.error(e.getLocalizedMessage());

      return false;
    }

  }

  public boolean mergeProductAttributes(final String sourceAttributeName, final String targetAttributeName,
      final Map<String, String> valueMapping) {

    isWebServiceLoggedIn();

    try {
      wsDomain.mergeProductAttributes(this.sessionHandle, sourceAttributeName, targetAttributeName, valueMapping);

      return true;
    }
    catch (CDMWebServiceException e) {
      this.logger.error("ERROR while merging a product attributes!");
      this.logger.error(e.getLocalizedMessage());

      return false;
    }

  }

  public boolean mergeProductAttributes(final String sourceAttributeName, final String targetAttributeName) {

    Map<String, String> valueMaping = new HashMap<String, String>();

    WSAttrMapList allAttrValues = getProductAttributeValues(null);

    List<String> valuesList = allAttrValues.get(sourceAttributeName);

    for (Object element : valuesList) {
      String attrValue = (String) element;

      valueMaping.put(attrValue, attrValue);
    }

    return mergeProductAttributes(sourceAttributeName, targetAttributeName, valueMaping);

  }

  /**
   * Rename a calibration variant CDMSL-91
   *
   * @param versionId The versionID of the APRJ
   * @param oldName the old name of the CalibrationVariant
   * @param newName the new name of the CalibrationVariant
   * @return true if successful, otherwise false
   */
  public boolean renameCalibrationVariant(final String versionId, final String oldName, final String newName) {

    // check if the WebService session is still valid
    isWebServiceLoggedIn();

    try {
      // try to rename the calibration variant
      // the last two boolean parameters are enabeling pre and post validation
      wsProject.renameCalibrationVariant(this.sessionHandle, versionId, oldName, newName, true, true);

      return true;
    }
    catch (CDMWebServiceException e) {
      this.logger.error("ERROR while renaming a Calibration Variant!");
      this.logger.error(e.getLocalizedMessage());

      return false;
    }

  }


  /**
   * @param pstName
   * @param objComments
   * @param hexFile
   * @param a2lFile
   * @param additionalFiles
   * @param additionalObjects
   * @param elemAttrMap
   * @param versAttrMap
   * @return List<ObjInfoEntryType> resultList
   * @author vau3cob
   */
  public List<ObjInfoEntryType> createPST(final String pstName, final String objComments,
      final FileObjCreationInfoType hexFile, final FileObjCreationInfoType a2lFile,
      final FileObjCreationInfoType[] additionalFiles, final FileObjReferenceInfoType additionalObjects[],
      final Map<String, String> elemAttrMap, final Map<String, String> versAttrMap) {
    /* List that stores the created Objects */
    List<ObjInfoEntryType> resultList = null;
    try {
      /* Create the PST in vCDM client */

      // Added by MRF5COB on 16-APR-2014
      isWebServiceLoggedIn();
      // ///////////////////////////


      resultList = wsDomain.createPST(this.sessionHandle, pstName, "SW", objComments, hexFile, a2lFile, additionalFiles,
          additionalObjects, elemAttrMap, versAttrMap);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Exception while creating PST " + e.getLocalizedMessage());
    }
    return resultList;

  }

  /**
   * Create an new A2L file object in vCDM Before creating the file object, the method checks, if the file is still
   * existing in vCDM. This check is done based on the ORIGINAL FILE attribute. If a file is available with the same
   * name, the file will be compared. The method returns the vCDM VERSION_NUMBER of the A2L file (if it has been created
   * or if it was still existing) The A2L object created by this method has the name of the A2L file. The variant will
   * be '-' by default. If an A2L object is existing with the same name and variant, the method tries to create an
   * object with a number starting with 1 as the variant.
   *
   * @param filePath The path where the file to be imported is located
   * @return The VERSION_NUMBER of the A2L file in vCDM
   * @throws EASEEServiceException
   */
  public String createA2LFileObject(final String filePath) throws EASEEServiceException {
    /*  */

    this.logger.info("creating A2L file in vCDM, filepath: " + filePath);

    File a2lFile = new File(filePath);

    String a2lFileName = a2lFile.getName();

    String objClass = "A2L";
    String objType = "A2L";

    String objName = adaptName4eASEE(a2lFileName.substring(0, a2lFileName.lastIndexOf('.')));
    String objVariant = "-";

    String objComment = "A2L File created via vCDM ServiceLibrary";

    // Check, if A2L File is still available in vCDM

    this.logger.info("checking if identical A2L file with same name is still available in vCDM ...");

    List<ObjInfoEntryType> a2lFileList = getParameterSet(a2lFileName);

    if (a2lFileList == null) {
      // timeout while getting A2L files with the same name
      String errorMessage = "ERROR when getting existing A2L Files (timeout)";
      this.logger.error(errorMessage);

      throw new EASEEServiceException(errorMessage);

    }

    boolean equalFileFound = false;

    for (ObjInfoEntryType element : a2lFileList) {
      ObjInfoEntryType objInfo = element;

      equalFileFound = isEqualEaseeFile(objInfo, filePath);

      if (equalFileFound) {
        this.logger.info("re-using existing A2L file (" + objInfo.getVersionNo() + " :: " + objInfo.getObjName() +
            " / " + objInfo.getObjVariant() + " ; " + objInfo.getObjRevision() + ")");
        return objInfo.getVersionNo();
      }

    }

    this.logger.info("identical A2L file with same name is NOT available in vCDM");

    // check, if a new object can be created with the standard name
    // CDMSL-90
    List<ObjInfoEntryType> existingObjects;
    int variantNumber = 0;

    do {

      existingObjects = getObjects(EASEEObjClass.A2L, objName, objVariant, null, null);

      if (!existingObjects.isEmpty()) {
        // object with the same identification is still existing

        // increment the variantNumber and use it as the variant name
        variantNumber++;
        objVariant = Integer.toString(variantNumber);

      }
      else {
        // no objects found with same identification
        break;
      }
    }
    // to avoid an endless loop which should normally not happen
    while (variantNumber < 100);


    // insert the file
    CreateObjectItemStates resultList = null;

    SimpleDateFormat sdfmt = new SimpleDateFormat();
    sdfmt.applyPattern("yyyy-MM-dd HH:mm:ss");
    String a2lFileDate = sdfmt.format(new Date(a2lFile.lastModified()));

    List<CreateFileObjectItem> fileObjects = new Vector<CreateFileObjectItem>();

    CreateFileObjectItem fileObject = new CreateFileObjectItem(objName, objVariant, objClass, objType);

    fileObject.setFilePath(filePath);

    fileObject.setKeepCheckedOut(false);
    // fileObject.setElementComment(objComment);
    fileObject.setVersionComment(objComment);

    Map<String, String> versionAttributes = new HashMap<String, String>();

    versionAttributes.put("ORIGINAL FILE", a2lFileName);
    versionAttributes.put("ORIGINAL DATE", a2lFileDate);

    fileObject.setVersionAttrs(versionAttributes);

    fileObjects.add(fileObject);

    try {
      // check if WebService connection is still valid
      isWebServiceLoggedIn();

      /* Create the file object in vCDM client */
      resultList = wsVersion.createFileObject(this.sessionHandle, fileObjects);
    }
    catch (CDMWebServiceException e) {
      String errorMessage = "ERROR creating A2L File Object: " + filePath + " :: ERROR: " + e.getLocalizedMessage();
      this.logger.error(errorMessage);

      throw new EASEEServiceException(errorMessage);
    }

    CreateObjectItemStateImpl createFileObjectResult = resultList.iterator().next();

    if (createFileObjectResult.getNewVersionNo() == null) {
      // error
      String errorMessage;

      if (createFileObjectResult.getErrorMessage() != null) {
        errorMessage = createFileObjectResult.getErrorMessage();
      }
      else {
        errorMessage = "NO error message available";
      }

      errorMessage = "ERROR creating A2L File Object: " + filePath + " :: ERROR: " + errorMessage;

      this.logger.error(errorMessage);

      throw new EASEEServiceException(errorMessage);
    }
    else {
      CreateObjectItem createdObject = createFileObjectResult.getItem();
      this.logger.info("A2L file created (" + createFileObjectResult.getNewVersionNo() + " :: " +
          createdObject.getObjName() + " / " + createdObject.getObjVariant() + ")");
    }

    return createFileObjectResult.getNewVersionNo();

  }

  // CDMSL - 52
  /**
   * This Method creates the program Key in vCDM Client in the specified APRJ.
   *
   * @param projectVersionNo - Version number of APRJ where the Program Key has to be created
   * @param name - Name for Program Key
   * @param pstVersionNo - Version number of the PST to be used for Creating Program Key
   * @return WSProgramKey
   * @author vau3cob
   */
  public WSProgramKey createProgramKey(final String projectVersionNo, final String name, final String pstVersionNo) {
    WSProgramKey pgmKey = null;
    try {
      isWebServiceLoggedIn();
      /* Create Program Key in vCDM Client */
      pgmKey = wsProject.createProgramKey(this.sessionHandle, projectVersionNo, name, pstVersionNo);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Exception while creating Program Key" + e.getLocalizedMessage());
    }
    return pgmKey;
  }


  // CDMSL-47
  /**
   * This method compare the contents of the Zip files
   *
   * @param zipFileOne
   * @param zipFileTwo
   * @return true if
   * @author vau3cob
   */
  public boolean compareZipFile(final File one, final File two) {
    boolean result = false;
    try {
      TreeMap<String, ByteArrayOutputStream> file1 = decompressor(one);
      TreeMap<String, ByteArrayOutputStream> file2 = decompressor(two);
      result = compareZipContents(file1, file2);
    }
    catch (IOException e) {
      this.logger.error("Exception while opening Zip file for comparing" + e.getLocalizedMessage());
    }
    return result;

  }

  /**
   * This method is to file decompressing inside zip and returns a map with ByteArrayOutStream
   *
   * @param zipfName
   * @return HashMap<String, ByteArrayOutputStream> of zip fileContents Key- FileName Value - ByteArrayOutputStream of
   *         files inside zip
   * @throws IOException
   * @author vau3cob
   */
  public TreeMap<String, ByteArrayOutputStream> decompressor(final File zipfName) throws IOException {
    int BUFF_SIZE = 2048;
    /* Entries processed for zip files */
    ZipEntry entryData;

    int counter;

    // Holding in Memory
    TreeMap<String, ByteArrayOutputStream> zipCont = new TreeMap<String, ByteArrayOutputStream>();

    byte byteData[] = new byte[BUFF_SIZE];
    /* File in Inputstream for decompress */
    FileInputStream file_decompress = new FileInputStream(zipfName);

    /* New buffer for decompressed files */
    BufferedInputStream bufferInput = new BufferedInputStream(file_decompress);

    /* File opened with buffer */
    ZipInputStream zipInputStream = new ZipInputStream(bufferInput);


    while ((entryData = zipInputStream.getNextEntry()) != null) {

      /* array creation for current data */
      ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
      zipCont.put(entryData.getName(), byteOutputStream);

      /* copy of memory */
      while ((counter = zipInputStream.read(byteData, 0, BUFF_SIZE)) != -1) {
        byteOutputStream.write(byteData, 0, counter);
      }
      /* buffer flushed */
      byteOutputStream.flush();
      byteOutputStream.close();
    }
    zipInputStream.close();
    return zipCont;
  }

  /**
   * This method compare the contents of the two zip files
   *
   * @param file1
   * @param file2
   * @return true if contents of zip file are true, false otherwise
   * @author vau3cob
   */
  public boolean compareZipContents(final TreeMap<String, ByteArrayOutputStream> file1,
      final TreeMap<String, ByteArrayOutputStream> file2) {
    Set listFile1 = file1.keySet();
    if (listFile1.size() == file2.keySet().size()) {
      for (Iterator iter = listFile1.iterator(); iter.hasNext();) {
        String fileName = (String) iter.next();
        // extract the contents for both
        ByteArrayOutputStream content2 = file2.get(fileName);
        ByteArrayOutputStream content1 = file1.get(fileName);

        if (content2 == null) {
          // file not found in Zip File 2
          return false;
        }
        if (content2.size() != content1.size()) {
          // not the same size, so files not equal, return false
          return false;
        }
        byte array1[] = content1.toByteArray();
        byte array2[] = content2.toByteArray();
        for (int i = 0; i < array1.length; i++) {
          if (array1[i] != array2[i]) {
            /* Different index */
            return false;
          }
        }

      }
    }
    else {
      /* Number of files in both the zip files are not equal. hence return false */
      return false;
    }
    return true;
  }

  /**
   * This method returns the extension of the file being passed
   *
   * @param fileName
   * @return fileExtension
   * @author vau3cob
   */
  public String getFileExtension(final String fileName) {
    int extensionPos = fileName.lastIndexOf(".");
    return fileName.substring(extensionPos + 1);
  }


  // CDMSL-54
  /**
   * This methodd is used to set the element attriibutes
   *
   * @param versionNo
   * @param elementAttributes
   * @throws CDMWebServiceException
   * @author MRF5COB
   */
  public void setElementAttributes(final String versionNo, final WSAttrMapList elementAttributes)
      throws CDMWebServiceException {
    isWebServiceLoggedIn();
    wsVersion.setElementAttributes(this.sessionHandle, versionNo, elementAttributes);
  }


  // CDMSL-59
  /**
   * This method inserts an easee object to a container
   *
   * @param versionNumber
   * @return
   * @author mid2si
   */
  public ItemStates insertIntoContainer(final Collection<InsertIntoContainerItem> versionNumber) {
    isWebServiceLoggedIn();

    ItemStates insertIntoContainer = null;

    try {
      isWebServiceLoggedIn();
      insertIntoContainer = wsVersion.insertIntoContainer(this.sessionHandle, versionNumber);

    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while inserting into a container " + e.getLocalizedMessage(), e);
    }

    return insertIntoContainer;
  }

  // CDMSL-59
  /**
   * This method creates a container object in vCDM client
   *
   * @param createObjectItems
   * @return
   * @author mid2si
   */
  public CreateObjectItemStates createContainerObject(final Collection createObjectItems) {
    isWebServiceLoggedIn();

    CreateObjectItemStates createContainerObject = null;

    try {
      isWebServiceLoggedIn();
      createContainerObject = wsVersion.createContainerObject(this.sessionHandle, createObjectItems);

    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while creating a container " + e.getLocalizedMessage(), e);
    }

    return createContainerObject;
  }

  /**
   * This method returns the workpackages and functions calibration status of the dataset
   *
   * @see JiraID-CDMSL-68
   * @param dstVersionNo - Version number of dataset that work package is of interest.
   * @param workpackageNames - Name of work package (based on PVD) that are of interest. If array is empty, return
   *          status of all work packages.
   * @param functionNames - Name of functions (based on A2L) that are of interest. If array is empty, return status of
   *          all functions.
   * @return WorkpackageAndFunctionCalibrationStatus
   * @author vau3cob
   */
  public WorkpackageAndFunctionCalibrationStatus readWorkpackageStatus(final String dstVersionNo,
      final String[] workpackageNames, final String[] functionNames) {
    WorkpackageAndFunctionCalibrationStatus workPackageCalState = null;
    try {
      isWebServiceLoggedIn();
      workPackageCalState =
          wsDataSet.readWorkpackageStatus(this.sessionHandle, dstVersionNo, workpackageNames, functionNames);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting workpackage/function and calstate information " + e.getLocalizedMessage());
    }
    return workPackageCalState;
  }

  /**
   * This methods get details of the bulk attributes in one webservice call. it is required to pass the attribute name
   * in the correct attribute category (version, element, configuration attribute). This is necessary, because some
   * attributes exists in different categories. E.g. CalibrationState may exist as version attribute (PAR) as well as
   * configuration attribute (on DST/PAR container relation).
   *
   * @see JiraID-CDMSL-68
   * @param containerVersion - Optional: Version of container that surrounds the versions of interest. This parameter is
   *          only necessary, if configuration attributes should be read (e.g. of PARs in DSTs). Leave this empty, if no
   *          configration attributes are required. Do not use this parameter, if you interested in the attributes of
   *          the container!
   * @param versionNumbers - Version number of object that attributes are of interest. This could be also container
   *          version, e.g. a dataset. So if you are interested in the version numbers of the dataset, pass the dataset
   *          version here not in the ï¿½containerVersionï¿½ attribute. If you are interested in the configuration
   *          attributes between DST and PAR, pass the DST version in containerVersion, the PAR versions in
   *          versionNumbers.
   * @param versionAttributes - Name of version attributes of interest.
   * @param elementAttributes - Name of element attributes of interest.
   * @param usageAttributes - Name of configuration attributes of interest.
   * @return AttributesContainer
   * @author vau3cob
   */
  public AttributesContainer getBulkAttributes(final String containerVersion, final String[] versionNumbers,
      final String[] versionAttributes, final String[] elementAttributes, final String[] usageAttributes) {
    AttributesContainer attributesContainer = null;
    try {
      isWebServiceLoggedIn();
      attributesContainer = wsVersion.getAttributes(this.sessionHandle, containerVersion, versionNumbers,
          versionAttributes, elementAttributes, usageAttributes);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting information of the attributes in bulk " + e.getLocalizedMessage());
    }
    return attributesContainer;
  }

  /**
   * Method to get the version No of the predecessor object
   *
   * @param versionNo - Version no to which predecessor needs to be fetched.
   * @return
   */
  public String getPredecessorObj(final int versionNo) {
    int predecessorVersionNo = 0;
    try {
      if (isApiLoggedIn()) {
        try {
          IVersionObj versObj = getVersion(versionNo);
          predecessorVersionNo = versObj.getPredecessor().number();
        }
        catch (EASEEServiceException e) {
          this.logger.error("Error while getting Predecessor Object ");
        }
      }
    }
    catch (EASEEServiceException e) {
      this.logger.error("ERROR while connecting to vCDM!");
      this.logger.error(e.getLocalizedMessage());
    }
    return String.valueOf(predecessorVersionNo);
  }

  /**
   * Method to get the version No of all the predecessors available
   *
   * @param versionNo - Version no to which predecessor needs to be fetched.
   * @return list of Predecessor object version numbers
   */
  public List<String> getPredecessorCollection(final int versionNo) {
    List<String> predecessorList = new ArrayList<>();
    int preDSTVersionNo = 0;
    IVersionObj versObj = null;
    try {
      if (isApiLoggedIn()) {
        try {
          versObj = getVersion(versionNo);
          if (versObj.hasPredecessor()) {
            preDSTVersionNo = versObj.getPredecessor().number();
            predecessorList.add(String.valueOf(preDSTVersionNo));
          }
          while ((preDSTVersionNo != 0)) {
            versObj = getVersion((preDSTVersionNo));
            if (versObj.hasPredecessor()) {
              preDSTVersionNo = versObj.getPredecessor().number();
              predecessorList.add(String.valueOf(preDSTVersionNo));
            }
            else {
              preDSTVersionNo = 0;
            }
          }

        }
        catch (EASEEServiceException e) {
          this.logger.error("Error while getting Predecessor Object ");
        }
      }
    }
    catch (EASEEServiceException e) {
      this.logger.error("ERROR while connecting to vCDM!");
      this.logger.error(e.getLocalizedMessage());
    }
    return predecessorList;
  }

  // CDMSL-77
  /**
   * This method will return the PVD information as GetPVDItem Object. In this object pvd information is available as an
   * XML String. If Unmarshalled Java object of the PVD information XML is required then getUnmarshalledPVDInfo() method
   * can be used directly by passing the PVD version Number
   *
   * @param pvdVersionNo
   * @return GetPVDItem
   */
  public GetPVDItem getPVDInfo(final String pvdVersionNo) {

    GetPVDItem pvdItem = null;
    try {
      isWebServiceLoggedIn();
      pvdItem = wsVersion.getPVD(this.sessionHandle, pvdVersionNo);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting information PVDInfo " + e.getLocalizedMessage());
    }
    return pvdItem;
  }

  // CDMSL-77
  /**
   * This method will return the unmarshalled PVD information as a java object
   *
   * @param pvdVersionNo
   * @return PVD
   */
  public com.bosch.easee.eASEEcdm_Service.model.pvd.PVD getUnmarshalledPVDInfo(final String pvdVersionNo) {

    GetPVDItem item = null;
    JAXBContext jaxbContext = null;
    Unmarshaller unmarshaller = null;
    com.bosch.easee.eASEEcdm_Service.model.pvd.PVD pvdInfo = null;
    try {
      isWebServiceLoggedIn();
      // Get pvd info from vCDM
      item = wsVersion.getPVD(this.sessionHandle, pvdVersionNo);
      StringReader reader = new StringReader(item.getAssignmentXML());

      // Unmarshal the XML content to java object
      jaxbContext = JAXBContext.newInstance(com.bosch.easee.eASEEcdm_Service.model.pvd.PVD.class,
          com.bosch.easee.eASEEcdm_Service.model.pvd.WP.class);
      unmarshaller = jaxbContext.createUnmarshaller();
      pvdInfo = (com.bosch.easee.eASEEcdm_Service.model.pvd.PVD) unmarshaller.unmarshal(reader);

    }
    catch (CDMWebServiceException | JAXBException e) {
      this.logger.error("Error while getting information PVDInfo " + e.getLocalizedMessage());
    }

    return pvdInfo;
  }

  // CDMSL-95
  /**
   * This method will return the PRD information as GetPRDItem Object. In this object pvd information is available as an
   * XML String. If Unmarshalled Java object of the PRD information XML is required then getUnmarshalledPRDInfo() method
   * can be used directly by passing the PRD version Number
   *
   * @param prdVersionNo
   * @return GetPRVDItem
   */
  public GetPRDItem getPRDInfo(final String prdVersionNo) {

    GetPRDItem pvdItem = null;
    try {
      isWebServiceLoggedIn();
      pvdItem = wsVersion.getPRD(this.sessionHandle, prdVersionNo);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting information PRDInfo " + e.getLocalizedMessage());
    }
    return pvdItem;
  }

  // CDMSL-95
  /**
   * This method will return the unmarshalled PRD information as a java object
   *
   * @param prdVersionNo
   * @return PRD
   */
  public com.bosch.easee.eASEEcdm_Service.model.prd.PRD getUnmarshalledPRDInfo(final String prdVersionNo) {

    GetPRDItem item = null;
    JAXBContext jaxbContext = null;
    Unmarshaller unmarshaller = null;
    com.bosch.easee.eASEEcdm_Service.model.prd.PRD prdInfo = null;
    try {
      isWebServiceLoggedIn();
      // Get prd info from vCDM
      item = wsVersion.getPRD(this.sessionHandle, prdVersionNo);
      StringReader reader = new StringReader(item.getAssignmentXML());

      // Unmarshal the XML content to java object
      jaxbContext = JAXBContext.newInstance(com.bosch.easee.eASEEcdm_Service.model.prd.PRD.class,
          com.bosch.easee.eASEEcdm_Service.model.prd.USER.class);
      unmarshaller = jaxbContext.createUnmarshaller();
      prdInfo = (com.bosch.easee.eASEEcdm_Service.model.prd.PRD) unmarshaller.unmarshal(reader);

    }
    catch (CDMWebServiceException | JAXBException e) {
      this.logger.error("Error while getting information PRDInfo " + e.getLocalizedMessage());
    }

    return prdInfo;
  }

  /**
   * @param aprjName
   * @param aprjRevision
   * @param dstName
   * @param dstRevision
   * @param checkInComment
   */
  public boolean checkInDST(final String aprjName, final int aprjRevision, final String dstName, final int dstRevision,
      String checkInComment) {

    boolean isCheckedSuccess = false;
    try {
      isApiLoggedIn();
      this.cdmDomain = getCdmDomain();
      ICDMCalibrationProjectCol calProjects = this.cdmDomain.searchCalibrationProjects(aprjName, null, aprjRevision);
      if (calProjects.count() < 1) {
        this.logger.fatal("No APRJ found for the specified condition");
        return false;
      }

      Com4jObject calProjectObj = (Com4jObject) calProjects.item(1);
      ICDMCalibrationProject calProject = calProjectObj.queryInterface(ICDMCalibrationProject.class);

      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinSettings dstCheckinSetting = calProject.getCheckinSettings();

      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol datasets =
          this.cdmDomain.searchDatasets(null, dstName, dstRevision);
      if (datasets.count() < 1) {
        this.logger.error("DST not found to check in : " + dstName + " ; " + dstRevision + "(DST)");
      }
      Com4jObject datasetObj = (Com4jObject) datasets.item(1);
      ICDMDataset dataset = datasetObj.queryInterface(ICDMDataset.class);

      ICDMDatasetCol dstCollection = this.cdmDomain.getDatasetCollection();

      // Validation option-Integration dataset (Check completeness)
      dstCheckinSetting.getOption(ECDMOptionCategoryId.CDMOptCat_Validation,
          EValidationOptions.PCHECK_COMPLETENESS_IDST.comEnumValue());
      // Validation option-Integration dataset (Overlapping)
      dstCheckinSetting.getOption(ECDMOptionCategoryId.CDMOptCat_Validation,
          EValidationOptions.PCHECK_OVERLAP_IDST.comEnumValue());
      // Validation option-Integration dataset (parameter completeness)
      dstCheckinSetting.getOption(ECDMOptionCategoryId.CDMOptCat_Validation,
          EValidationOptions.COMPLETENESS_OF_PARSET_PO.comEnumValue());
      // Checkin Comment
      if ((checkInComment == null) || checkInComment.isEmpty()) {
        checkInComment = "Checked-In via API";
      }
      dstCheckinSetting.comment(checkInComment);

      dstCollection.add(dataset);
      ICDMDatasetCheckinDatasetSettings dstCheckinDatasetSettings = dstCheckinSetting.getDatasetSettings(dataset);

      dstCheckinSetting.setDatasetSettings(dataset, dstCheckinDatasetSettings);

      // dstCheckinSetting.ignoreValidationWarnings(true);
      // dstCheckinSetting.ignoreIntegrationResult(ECdmProcessSettings.CDMProcessIgnoreWarnings);
      dstCheckinSetting.requiredDataQuality(ECdmRequiredDataQuality.CDMDataQualityIgnoreWarnings);


      this.logger.info("Checkin operation started");

      ICDMDatasetCheckinResult dstCheckinResult = dstCollection.checkIn(dstCheckinSetting);

      this.logger.info("Validation result  : " + dstCheckinResult.getDatasetResult(dataset).validationResult());
      this.logger.info("Integration result  : " + dstCheckinResult.getDatasetResult(dataset).integrationResult());

      if (ECdmProcessResult.CDMResultSuccess.equals(dstCheckinResult.getDatasetResult(dataset).integrationResult())) {
        isCheckedSuccess = true;
      }


      this.logger.info("Checkin operation finished");
    }
    catch (Exception e) {
      this.logger.fatal("Exception while chekin dst.." + e.getLocalizedMessage());
    }
    return isCheckedSuccess;
  }


  /**
   * @param aprjName
   * @param aprjRevision
   * @param dstName
   * @param dstRevision
   * @param checkInComment
   */
  public boolean checkInDST(final String aprjName, final int aprjRevision, final String dstName, final int dstRevision,
      String checkInComment, final ECdmRequiredDataQuality checkInSetting) {

    boolean isCheckedSuccess = false;
    try {
      isApiLoggedIn();
      this.cdmDomain = getCdmDomain();
      ICDMCalibrationProjectCol calProjects = this.cdmDomain.searchCalibrationProjects(aprjName, null, aprjRevision);
      if (calProjects.count() < 1) {
        this.logger.fatal("No APRJ found for the specified condition");
        return false;
      }

      Com4jObject calProjectObj = (Com4jObject) calProjects.item(1);
      ICDMCalibrationProject calProject = calProjectObj.queryInterface(ICDMCalibrationProject.class);

      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinSettings dstCheckinSetting = calProject.getCheckinSettings();

      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol datasets =
          this.cdmDomain.searchDatasets(null, dstName, dstRevision);
      if (datasets.count() < 1) {
        this.logger.error("DST not found to check in : " + dstName + " ; " + dstRevision + "(DST)");
      }
      Com4jObject datasetObj = (Com4jObject) datasets.item(1);
      ICDMDataset dataset = datasetObj.queryInterface(ICDMDataset.class);

      ICDMDatasetCol dstCollection = this.cdmDomain.getDatasetCollection();

      // Validation option-Integration dataset (Check completeness)
      dstCheckinSetting.getOption(ECDMOptionCategoryId.CDMOptCat_Validation,
          EValidationOptions.PCHECK_COMPLETENESS_IDST.comEnumValue());
      // Validation option-Integration dataset (Overlapping)
      dstCheckinSetting.getOption(ECDMOptionCategoryId.CDMOptCat_Validation,
          EValidationOptions.PCHECK_OVERLAP_IDST.comEnumValue());
      // Validation option-Integration dataset (parameter completeness)
      dstCheckinSetting.getOption(ECDMOptionCategoryId.CDMOptCat_Validation,
          EValidationOptions.COMPLETENESS_OF_PARSET_PO.comEnumValue());
      // Checkin Comment
      if ((checkInComment == null) || checkInComment.isEmpty()) {
        checkInComment = "Checked-In via API";
      }
      dstCheckinSetting.comment(checkInComment);

      dstCollection.add(dataset);
      ICDMDatasetCheckinDatasetSettings dstCheckinDatasetSettings = dstCheckinSetting.getDatasetSettings(dataset);

      dstCheckinSetting.setDatasetSettings(dataset, dstCheckinDatasetSettings);

      // dstCheckinSetting.ignoreValidationWarnings(true);
      // dstCheckinSetting.ignoreIntegrationResult(checkInSetting);
      dstCheckinSetting.requiredDataQuality(checkInSetting);


      this.logger.info("Checkin operation started");

      ICDMDatasetCheckinResult dstCheckinResult = dstCollection.checkIn(dstCheckinSetting);

      this.logger.info("Validation result  : " + dstCheckinResult.getDatasetResult(dataset).validationResult());
      this.logger.info("Integration result  : " + dstCheckinResult.getDatasetResult(dataset).integrationResult());
      if (ECdmProcessResult.CDMResultSuccess.equals(dstCheckinResult.getDatasetResult(dataset).integrationResult())) {
        isCheckedSuccess = true;
      }


      this.logger.info("Checkin operation finished");
    }
    catch (Exception e) {
      if (e.getLocalizedMessage() != null) {
        this.logger.fatal("Exception while chekin dst.." + e.getLocalizedMessage());

      }
      else {
        this.logger.fatal("Exception while chekin dst..");
      }

    }
    return isCheckedSuccess;
  }

  /**
   * This method is used to create an object in vCDM via Webservice.
   *
   * @param objClass
   * @param type
   * @param name
   * @param variant
   * @param repository
   * @param description
   * @param comment
   * @param filePath
   * @param keepCheckedOut
   * @param transfer
   * @return
   * @throws EASEEServiceException
   * @deprecated USE createFileObjectViaAPI Instead.
   */
  @Deprecated
  public IVersionObj createAPIObject(final EASEEObjClass objClass, final String type, final String name,
      final String variant, final String repository, final String description, final String comment,
      final String filePath, final boolean keepCheckedOut, final boolean transfer)
      throws EASEEServiceException {
    return createFileObjectViaAPI(objClass, type, name, variant, repository, description, comment, filePath,
        keepCheckedOut, transfer);
  }

  public String exportDST(final String aprjName, final int aprjRevision, final String dstName, final int dstRevision,
      final File masterHexpath) {

    String exportedHexPath = null;

    try {

      isApiLoggedIn();
      this.cdmDomain = getCdmDomain();
      ICDMCalibrationProjectCol calProjects = this.cdmDomain.searchCalibrationProjects(aprjName, null, aprjRevision);
      if (calProjects.count() < 1) {
        this.logger.fatal("No APRJ found for the specified condition");
        return null;
      }
      Com4jObject calProjectObj = (Com4jObject) calProjects.item(1);
      ICDMCalibrationProject calProject = calProjectObj.queryInterface(ICDMCalibrationProject.class);

      ICDMDatasetExportSettings icdmDatasetExportSettings =
          calProject.getExportSettings(ECdmDatasetExportType.CDM_EXPORT_OBJECTFILE);

      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol datasets =
          this.cdmDomain.searchDatasets(null, dstName, dstRevision);
      if (datasets.count() < 1) {
        this.logger.error("DST not found to check in : " + dstName + " ; " + dstRevision + "(DST)");
      }
      Com4jObject datasetObj = (Com4jObject) datasets.item(1);
      ICDMDataset dataset = datasetObj.queryInterface(ICDMDataset.class);
      ICDMDatasetExportDatasetSettings icdmDatasetExportDatasetSettings =
          icdmDatasetExportSettings.getDatasetSettings(dataset);
      // icdmDatasetExportSettings.ignoreIntegrationResult(ECdmProcessSettings.CDMProcessIgnoreErrors);
      // icdmDatasetExportSettings.ignoreIntegrationResult(ECdmProcessSettings.CDMProcessIgnoreWarnings);
      icdmDatasetExportSettings.requiredValidationDataQuality(ECdmRequiredDataQuality.CDMDataQualityIgnoreWarnings);
      icdmDatasetExportSettings.requiredIntegrationDataQuality(ECdmRequiredDataQuality.CDMDataQualityIgnoreWarnings);

      icdmDatasetExportSettings.fileType(EASEEObjClass.HEX.toString());
      icdmDatasetExportSettings.path(masterHexpath.getAbsolutePath());
      icdmDatasetExportSettings.setDatasetSettings(dataset, icdmDatasetExportDatasetSettings);

      ICDMDatasetCol dstCollection = this.cdmDomain.getDatasetCollection();

      dstCollection.add(dataset);


      ICDMDatasetExportResult dstExportResult = dstCollection.export(icdmDatasetExportSettings);
      this.logger.info("Validation result  : " + dstExportResult.getDatasetResult(dataset).validationResult());
      this.logger.info("Integration result  : " + dstExportResult.getDatasetResult(dataset).integrationResult());
      // ICDMMessage message = ((ICDMMessage) dstExportResult.getDatasetResult(dataset).integrationMessages().item(0));

      // this.logger.info("Integration result : " + message.text());
      // this.logger.info(dstExportResult.getDatasetResult(dataset));
      // Iterator<Com4jObject> iterator = dstExportResult.getDatasetResult(dataset).integrationMessages().iterator();
      // while (iterator.hasNext()) {
      // this.logger.info(((ICDMMessageCol) iterator.next()).);
      // }

      // if (ECdmProcessResult.CDMResultSuccess.equals(dstExportResult.getDatasetResult(dataset).integrationResult())) {
      exportedHexPath = dstExportResult.getDatasetResult(dataset).fileName();
      // }

    }
    catch (EASEEServiceException e) {
      this.logger.fatal("Exception while Exporting dst.." + e.getLocalizedMessage());
    }

    return exportedHexPath;

  }

  /**
   * Method to Export a hex file of the Checked out DST. All the Calibration values of the DST will be integrated to the
   * hex file thats being exported
   *
   * @param aprjName
   * @param aprjRevision
   * @param dstName
   * @param dstRevision
   * @param masterHexpath
   * @param validationProcessSettings
   * @param integrationProcessSettings
   * @return
   */
  public vCDMProcessResult exportDST(final String aprjName, final int aprjRevision, final String dstName,
      final int dstRevision, final String masterHexpath) {
    // final ECdmProcessSettings integrationProcessSettings)

    String exportedHexPath = null;
    vCDMProcessResult result = null;
    try {

      isApiLoggedIn();
      this.cdmDomain = getCdmDomain();
      ICDMCalibrationProjectCol calProjects = this.cdmDomain.searchCalibrationProjects(aprjName, null, aprjRevision);
      if (calProjects.count() < 1) {
        this.logger.fatal("No APRJ found for the specified condition");
        return null;
      }
      Com4jObject calProjectObj = (Com4jObject) calProjects.item(1);
      ICDMCalibrationProject calProject = calProjectObj.queryInterface(ICDMCalibrationProject.class);

      ICDMDatasetExportSettings icdmDatasetExportSettings =
          calProject.getExportSettings(ECdmDatasetExportType.CDM_EXPORT_OBJECTFILE);

      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol datasets =
          this.cdmDomain.searchDatasets(null, dstName, dstRevision);
      if (datasets.count() < 1) {
        this.logger.error("DST not found to check in : " + dstName + " ; " + dstRevision + "(DST)");
      }
      Com4jObject datasetObj = (Com4jObject) datasets.item(1);
      ICDMDataset dataset = datasetObj.queryInterface(ICDMDataset.class);
      ICDMDatasetExportDatasetSettings icdmDatasetExportDatasetSettings =
          icdmDatasetExportSettings.getDatasetSettings(dataset);
      // icdmDatasetExportSettings.ignoreIntegrationResult(ECdmProcessSettings.CDMProcessIgnoreErrors);
      // icdmDatasetExportSettings.ignoreIntegrationResult(integrationProcessSettings);
      // icdmDatasetExportSettings.ignoreValidationResult(validationProcessSettings);
      icdmDatasetExportSettings.requiredValidationDataQuality(ECdmRequiredDataQuality.CDMDataQualityIgnoreWarnings);
      icdmDatasetExportSettings.requiredIntegrationDataQuality(ECdmRequiredDataQuality.CDMDataQualityIgnoreWarnings);
      icdmDatasetExportSettings.fileType(EASEEObjClass.HEX.toString());
      icdmDatasetExportSettings.path(masterHexpath);
      icdmDatasetExportSettings.setDatasetSettings(dataset, icdmDatasetExportDatasetSettings);
      ICDMDatasetCol dstCollection = this.cdmDomain.getDatasetCollection();

      dstCollection.add(dataset);


      ICDMDatasetExportResult dstExportResult = dstCollection.export(icdmDatasetExportSettings);

      ECdmValidationResult validationResult = dstExportResult.getDatasetResult(dataset).validationResult();
      ECdmProcessResult processResult = dstExportResult.getDatasetResult(dataset).integrationResult();

      this.logger.info("Validation result  : " + validationResult);
      this.logger.info("Integration result  : " + processResult);

      exportedHexPath = dstExportResult.getDatasetResult(dataset).fileName();

      result = new vCDMProcessResult(validationResult, processResult, exportedHexPath, null);

    }
    catch (EASEEServiceException e) {
      this.logger.fatal("Exception while Exporting dst.." + e.getLocalizedMessage());
      result = new vCDMProcessResult(null, null, null, "Process Failed due to " + e.getLocalizedMessage());
    }

    return result;

  }

  /**
   * Use this method to Connect a single start objects with one or many end objects. The connection is named.
   *
   * @param startVersion - Starting point of relation, e.g. a PAR object.
   * @param typedRelationName - Name of typed relation. Please note that typed relation also have a backward path name.
   *          Here, the forward path name has to be given. e.g. "assigned documentation"
   * @param endVersions - Version numbers of objects that should be connected with start object (e.g. DOCs that should
   *          point to a PAR).
   * @return - string with status. Null if everything went fine.
   * @throws EASEEServiceException
   */
  public String[] setTypedRelation(final String startVersion, final String typedRelationName,
      final String[] endVersions)
      throws EASEEServiceException {
    String[] result = null;
    try {
      result = wsVersion.setTypedRelation(this.sessionHandle, startVersion, typedRelationName, endVersions);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while setting Typed Relation - " + e.getLocalizedMessage());
      throw new EASEEServiceException("Error while setting Typed Relation - " + e.getLocalizedMessage());
    }
    return result;
  }

  /**
   * Use this method to Resolve the named connection between a single start objects and one or many end objects.
   *
   * @param startVersion - Starting point of relation, e.g. a PAR object.
   * @param typedRelationName - Name of typed relation. Please note that typed relation also have a backward path name.
   *          Here, the forward path name has to be given. e.g. "assigned documentation"
   * @param endVersions - Version numbers of objects that connection should be resolved (resolve connection of 2 out of
   *          4 DOCs that are connected with a PAR).
   * @return - string[] with status of each connection resolved. Null if everything went fine.
   * @throws EASEEServiceException
   */
  public String[] deleteTypedRelation(final String startVersion, final String typedRelationName,
      final String[] endVersions)
      throws EASEEServiceException {
    String[] result = null;
    try {
      result = wsVersion.deleteTypedRelation(this.sessionHandle, startVersion, typedRelationName, endVersions);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while deleting Typed Relation - " + e.getLocalizedMessage());
      throw new EASEEServiceException("Error while deleting Typed Relation - " + e.getLocalizedMessage());
    }
    return result;
  }

  /**
   * Use this method to Get the end point objects of a typed relation.
   *
   * @param startVersion - Starting point of relation, e.g. a PAR object.
   * @param typedRelationName - Name of typed relation. Please note that typed relation also have a backward path name.
   *          Here, the forward path name has to be given.
   * @return String array of Version numbers of objects that are connected to start objects (e.g DOCs that are connected
   *         with a PAR).
   * @throws EASEEServiceException
   */
  public String[] getTypedRelation(final String startVersion, final String typedRelationName)
      throws EASEEServiceException {
    String[] result = null;
    try {
      result = wsVersion.getTypedRelation(this.sessionHandle, startVersion, typedRelationName);
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting Typed Relation - " + e.getLocalizedMessage());
      throw new EASEEServiceException("Error while getting Typed Relation - " + e.getLocalizedMessage());
    }
    return result;
  }

  /**
   * This function creates a new non-calibration object like documentation files or checksum configuration files.
   *
   * @param filePath - Path of the file
   * @param objName - Object Name
   * @param objVariant - Object Variant
   * @param objClass - EASEEObjClass
   * @param keepCheckedOut - True if the file object has to be checked out state
   * @return CreateObjectItemStateImpl
   * @throws EASEEServiceException
   */
  public CreateObjectItemStateImpl createFileObject(final Path filePath, final String objName, final String objVariant,
      final EASEEObjClass objClass, final boolean keepCheckedOut)
      throws EASEEServiceException {

    CreateObjectItemStateImpl createdObjectState = null;

    Set<CreateFileObjectItem> createItems = new HashSet<>();
    String extension = FilenameUtils.getExtension(filePath.getFileName().toString());

    try {

      CreateFileObjectItem createItem = new CreateFileObjectItem(objName, objVariant, objClass.toString(), extension);
      createItem.setFilePath(filePath.toString());
      createItem.setKeepCheckedOut(keepCheckedOut);

      createItems.add(createItem);

      CreateObjectItemStates states = wsVersion.createFileObject(this.sessionHandle, createItems);

      if ((states != null) && !states.isEmpty()) {
        createdObjectState = states.iterator().next();
      }
    }
    catch (CDMWebServiceException e) {
      this.logger.error("Error while getting creating file Object - " + e.getLocalizedMessage());
      throw new EASEEServiceException("Error while getting creating file Object- " + e.getLocalizedMessage());
    }

    return createdObjectState;
  }

  /**
   * This method is used to create an object in vCDM using vCDM API.
   *
   * @param objClass
   * @param type - (XLSX,DOC,etc)
   * @param name
   * @param variant
   * @param repository - CDM
   * @param description
   * @param comment
   * @param filePath
   * @param keepCheckedOut
   * @param transfer
   * @return new created IVersionObj
   * @throws EASEEServiceException
   */
  public IVersionObj createFileObjectViaAPI(final EASEEObjClass objClass, final String type, final String name,
      final String variant, final String repository, final String description, final String comment,
      final String filePath, final boolean keepCheckedOut, final boolean transfer)
      throws EASEEServiceException {
    IVersionObj result = null;
    try {
      if (isApiLoggedIn()) {
        result = this.clientApi2.createFileObject(objClass.toString(), type, name, variant, comment, filePath,
            keepCheckedOut);
      }
    }
    catch (EASEEServiceException e) {
      this.logger.error("Error while getting creating file Object via API - " + e.getLocalizedMessage());
      throw new EASEEServiceException("Error while getting creating file Object via API " + e.getLocalizedMessage());
    }
    return result;
  }

  public void checkin(final String version, final String comment, final String filePath,
      final boolean takeOverRequiredAttributes, final boolean freezeAutoLatestRelations, final boolean transfer) {

    try {
      if (isApiLoggedIn()) {
        IVersionObj pVersion;
        pVersion = this.getVersion(Integer.parseInt(version));
        this.clientApi2.checkin(pVersion, comment, filePath);
      }
    }
    catch (NumberFormatException | EASEEServiceException e) {
      this.logger.error("Cannot checkin the container " + e.getLocalizedMessage());
    }
  }

  public void checkout(final String version, final String variant, final String comment, final String filePath,
      final boolean takeOverRequiredAttributes, final boolean transfer) {

    try {
      if (isApiLoggedIn()) {
        int ver = Integer.parseInt(version);
        IVersionObj pVersion = this.clientApi2.getVersionObject(ver);
        this.clientApi2.checkout(pVersion, variant, comment);
      }
    }
    catch (NumberFormatException | EASEEServiceException e) {
      this.logger.error("Cannot checkout the container " + e.getLocalizedMessage());
    }
  }
}

