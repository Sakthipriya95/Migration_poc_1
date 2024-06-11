/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.jpa;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.CmdModApicUser;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.comppkg.jpa.bo.CPDataProvider;
import com.bosch.caltool.dmframework.bo.CommandStack;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.GenericException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.VersionValidator;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.database.DatabaseInitializer;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.JPALogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.logger.SSDLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Initialise the data model. Create the database connection and fetch the startup data from database and creates
 * necessary business objects.
 *
 * @author bne4cob
 */
final class DataModelInitializer {


  /**
   * Logged in User's NT ID
   */
  private final String username;

  /**
   * First name
   */
  private String userFirstName;

  /**
   * Last name
   */
  private String userLastName;

  /**
   * Department
   */
  private String userDepartment;

  /**
   * Version of the application in plugin.xml
   */
  private final String productVersion;


  /**
   * Constructor, to be called if database initialized from iCDM client
   *
   * @param username Logged in User's NT ID
   * @param userFirstName first name
   * @param userLastName last name
   * @param userDepartment department
   * @param productVersion Version of the application in the plugin.xml
   */
  public DataModelInitializer(final String username, final String userFirstName, final String userLastName,
      final String userDepartment, final String productVersion) {
    this.username = username;
    this.userFirstName = userFirstName;
    this.userLastName = userLastName;
    this.userDepartment = userDepartment;

    this.productVersion = productVersion;
  }

  /**
   * Constructor to initialize database from iCDM web service
   *
   * @param username Logged in User's NT ID
   * @param productVersion Version of the application in the plugin.xml
   */
  public DataModelInitializer(final String username, final String productVersion) {
    this.username = username;
    this.productVersion = productVersion;
  }

  /**
   * Initialise DB connection and load startup data
   *
   * @throws IcdmException exception while loading data
   */
  public void initializeModel() throws IcdmException {


    EntityManagerFactory emf = ObjectStore.getInstance().getEntityManagerFactory();
    if ((emf == null) || !emf.isOpen()) {
      Properties dmProps = getDmProps();
      ObjectStore.getInstance().initialise(CDMLogger.getInstance(), JPALogger.getInstance(), dmProps);
      ObjectStore.getInstance().setCnsMessageType(CnsDataProducerServiceClient.class);
      DatabaseInitializer dbConnector = new DatabaseInitializer(CDMLogger.getInstance());
      emf = dbConnector.connect();
    }

    loadData(emf);

  }

  /**
   * @param emf
   * @throws IcdmException
   * @throws DataException
   */
  private void loadData(final EntityManagerFactory emf) throws IcdmException, DataException {
    try {

      CDMLogger.getInstance().debug("Starting data model initialization...");

      // load APIC data
      loadAPICData(emf);

      // ICDM-252
      validateProductVersion();

      // updateCurrentUserDetails();

      // ICDM-2640
      // if started by the web server, new thread need not be created
      if (CommonUtils.isStartedFromWebService()) {
        InitializeModelforWebService initializeWSData = new InitializeModelforWebService();
        initializeWSData.fetchData();
      }
      else {

        fetchAllUsers();

        fetchUnits();
        // Icdm-1033
        fetchAllFeatures();

        // ICDM-959
        fetchLinkNodes();

        fetchAllAttrStructure();

        // icdm-205 - Load the a2l system constants details from db
        loadA2lData();

        // iCDM-519 - create CDR data provider and load related data, if required
        loadCDRData();

        // create Cmp Pkg data provider and load related data, if required
        loadCmpPkgData();

      }
    }
    // Icdm exceptions thrown by the application
    catch (IcdmException exp) {
      throw exp;
    }
    catch (Exception exp) {
      if (exp.getCause() instanceof DatabaseException) {
        throw new DataException(DataException.ERCD_DB_EX_DURING_INIT1, exp.getCause().getMessage(), exp);
      }
      throw new DataException(DataException.ERCD_INIT_UNEXP_ERR, exp.getCause().getMessage(), exp);
    }
  }


  /**
   * Get the data model properties, to initialise the data model
   *
   * @return properties
   */
  private Properties getDmProps() {
    Properties dmProps = new Properties();
    dmProps.setProperty(ObjectStore.P_DB_SERVER, Messages.getString(ObjectStore.P_DB_SERVER));
    dmProps.setProperty(ObjectStore.P_DB_PORT, Messages.getString(ObjectStore.P_DB_PORT));
    dmProps.setProperty(ObjectStore.P_USER_NAME, this.username);
    dmProps.setProperty(ObjectStore.P_CQN_MODE, String.valueOf(ObjectStore.CQN_MODE_COMMAND));

    // Run DBConnectionPoller only for iCDM client, not from web services.
    String conPollMode = CommonUtils.isStartedFromWebService() ? ObjectStore.CP_NO : ObjectStore.CP_YES;
    dmProps.setProperty(ObjectStore.P_CONN_POLL_MODE, conPollMode);

    dmProps.setProperty(ApicDataProvider.P_LOAD_CUR_USER_ONLY_START, ApicConstants.YES);
    dmProps.setProperty(ApicDataProvider.P_LOAD_LVL_ATTR_ONLY_START, ApicConstants.YES);
    return dmProps;
  }

  /**
   * Save the user details to the ICDM system. This is done when the user logins for the first time, or if the user
   * details are not updated.
   *
   * @param userInfo the user details obtained from LDAP authentication
   */
  @Deprecated
  private void updateCurrentUserDetails() {

    if (CommonUtils.isStartedFromWebService()) {
      return;
    }

    final ApicDataProvider dataProvider = CDMDataProvider.getInstance().getApicDataProvider();
    final ApicUser curUser = dataProvider.getApicUser(this.username);

    CommandStack cmdStk = new CommandStack();

    CmdModApicUser usrCmd = new CmdModApicUser(dataProvider, curUser);
    usrCmd.setFirstName(this.userFirstName);
    usrCmd.setLastName(this.userLastName);
    usrCmd.setDepartment(this.userDepartment);
    usrCmd.setUpdateTimestamp(true);

    cmdStk.addCommand(usrCmd);

  }

  /**
   * Fetch all users as a thread
   */
  private void fetchAllUsers() {
    // Load all users in the background as a thread
    final Thread objThread = new Thread(new Runnable() {

      /**
       * Load link node IDs
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDMLogger.getInstance().debug("AllAPICUsers loading thread started");

        CDMDataProvider.getInstance().getApicDataProvider().loadAllApicUsers();

        CDMLogger.getInstance().debug("AllAPICUsers loading thread completed");
      }
    });

    objThread.start();

  }


  /**
   * Load the use case items' attribute mappings during startup as a separate thread
   */
  // ICDM-1123
  private void loadUseCaseItemAttributeMapping() {
    // Load all users in the background as a thread
    final Thread objThread = new Thread(new Runnable() {

      /**
       * Load link node IDs
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDMLogger.getInstance().debug("AllUseCaseItemAttributeMapping loading thread started");

        // Mapping between use case items and attributes
        CDMDataProvider.getInstance().getApicDataProvider().loadUseCaseItemAttributeMapping();

        CDMLogger.getInstance().debug("AllUseCaseItemAttributeMapping loading thread completed");
      }
    });

    objThread.start();

  }

  // ICDM-252
  /**
   * Validate's application version, to ensure that the latest version of the iCDM tool is being opened.
   *
   * @throws GenericException if tool's version is not the latest
   */
  private void validateProductVersion() throws GenericException {
    // if started by the web server, validation is not necessary
    if (CommonUtils.isStartedFromWebService()) {
      return;
    }

    // Validation is done by comparing the version number mentioned in product plugin against the version number
    // configured in the database table TABV_COMMON_PARAMS

    final String dbVersion =
        CDMDataProvider.getInstance().getApicDataProvider().getParameterValue(ApicConstants.ICDM_CLIENT_VERSION);
    // ICDM-2263
    VersionValidator validator = new VersionValidator(dbVersion, false);
    if (!validator.validateVersions(this.productVersion)) {
      throw new GenericException(
          "A newer version of iCDM client v(" + dbVersion + ") is available. Please upgrade to the latest.");
    }
  }

  /**
   * Method to load APIC data
   *
   * @param username user name
   */
  private void loadAPICData(final EntityManagerFactory emf) {

    CDMLogger.getInstance().debug("Loading APIC configurations...");

    /* --- Language Preference settings -- */
    Language language;
    if (CommonUtils.isStartedFromWebService()) {
      language = Language.ENGLISH;
    }
    else {
      final String selectedLang = PlatformUI.getPreferenceStore().getString(IcdmJpaConstants.LANGUAGE);
      language = Language.getLanguage(selectedLang);
    }

    Map<String, ILoggerAdapter> loggerMap = new ConcurrentHashMap<>();
    loggerMap.put(ApicDataProvider.LOGGR_PARSER_NAME, ParserLogger.getInstance());
    loggerMap.put(ApicDataProvider.LOGGR_SSDLOGGR_NAME, SSDLogger.getInstance());

    /* --- Load APIC data ---- */
    final ApicDataProvider apicDataProvider =
        new ApicDataProvider(loggerMap, emf, this.username.toUpperCase(Locale.ENGLISH), language);
    // Set loaded data to CDMDataProvider
    CDMDataProvider.getInstance().setApicDataProvider(apicDataProvider);
  }


  /**
   * Fetch units in the background as a thread
   */
  private void fetchUnits() {
    // Load units of parameters in the background as a thread
    final Thread objThread = new Thread(new Runnable() {

      /**
       * Load units
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDMLogger.getInstance().debug("AllUnits loading thread started");

        // Invoking this method, for the first time, fetches the units from DB
        CDMDataProvider.getInstance().getApicDataProvider().getUnits();

        CDMLogger.getInstance().debug("AllUnits loading thread completed");
      }
    });

    objThread.start();
  }


  /**
   * Icdm-1033 Fetch units in the background as a thread
   */
  private void fetchAllFeatures() {
    // Load units of parameters in the background as a thread
    final Thread objThread = new Thread(new Runnable() {

      /**
       * Load features
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDMLogger.getInstance().debug("AllSSDFeatures loading thread started");

        // Invoking this method, for the first time, fetches the units from DB
        CDMDataProvider.getInstance().getApicDataProvider().getAllFeatures();

        CDMLogger.getInstance().debug("AllSSDFeatures loading thread completed");
      }
    });

    objThread.start();
  }

  /**
   * ICDM-959 Fetch nodetype and Id's which has links
   */
  private void fetchLinkNodes() {
    // Load units of parameters in the background as a thread
    final Thread objThread = new Thread(new Runnable() {

      /**
       * Load link node IDs
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDMLogger.getInstance().debug("AlllinkNodes loading thread started");

        // Invoking this method, for the first time, fetches the nodeid's which has links from DB
        CDMDataProvider.getInstance().getApicDataProvider().getLinkNodeIds();

        CDMLogger.getInstance().debug("AlllinkNodes loading thread started");
      }
    });

    objThread.start();
  }

  /**
   *
   */
  private void fetchAllAttrStructure() {
    // Load attributes, super groups, groups, values in the background as a thread
    final Thread objThread = new Thread(new Runnable() {

      /**
       * Load link node IDs
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDMLogger.getInstance().debug("AllAttributes loading thread started");

        CDMDataProvider.getInstance().getApicDataProvider().loadAllAttrStructure();

        CDMLogger.getInstance().debug("AllAttributes loading thread completed");

        // After loading attributes, load use case item attribute mappings
        // The cactivity is triggerend in a separate thread.
        // ICDM-1123
        loadUseCaseItemAttributeMapping();
      }
    });

    objThread.start();

  }

  /**
   * Load the A2L related data. This is executed as a thread. Set the data provider to CDMDataProvider
   */
  private void loadA2lData() {

    final Thread objThread = new Thread(new Runnable() {

      /**
       * Initialize A2L data provider
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // Synchronization is enabled to load the a2l data, before allowing the information to be used by UI
        synchronized (IcdmJpaConstants.A2L_DATA_SYNC_LOCK) {
          CDMLogger.getInstance().debug("LoadingA2LConfigurations thread started");

          A2LDataProvider a2lDataProvider = new A2LDataProvider(CDMDataProvider.getInstance().getApicDataProvider());
          CDMDataProvider.getInstance().setA2lDataProvider(a2lDataProvider);

          CDMLogger.getInstance().debug("LoadingA2LConfigurations thread completed");
        }

      }
    });

    objThread.start();
  }


  /**
   * Load the data for CDR.
   */
  private void loadCDRData() {

    CDMLogger.getInstance().debug("Loading CDR configurations thread started");

    // create CDR data provider
    final CDRDataProvider cdrDataProvider = new CDRDataProvider(CDMDataProvider.getInstance().getApicDataProvider());
    // Set the data
    CDMDataProvider.getInstance().setCdrDataProvider(cdrDataProvider);

    CDMLogger.getInstance().debug("Loading CDR configurations thread completed");
  }

  /**
   * Load the data for Cmp Pkg.
   */
  private void loadCmpPkgData() {


    final Thread objThread = new Thread(new Runnable() {

      /**
       * Initialize A2L data provider
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {

        // Synchronization is enabled to load the a2l data, before allowing the information to be used by UI
        synchronized (IcdmJpaConstants.A2L_DATA_SYNC_LOCK) {
          CDMLogger.getInstance().debug("LoadingComponentPackageConfigurations thread started");

          // create cmp pkg data provider
          final CPDataProvider cpDataProvider = new CPDataProvider(CDMDataProvider.getInstance().getApicDataProvider(),
              CDMDataProvider.getInstance().getCdrDataProvider());
          // Set the data
          CDMDataProvider.getInstance().setCpDataProvider(cpDataProvider);

          CDMLogger.getInstance().debug("LoadingComponentPackageConfigurations thread completed");
        }
      }

    });

    objThread.start();
  }


}
