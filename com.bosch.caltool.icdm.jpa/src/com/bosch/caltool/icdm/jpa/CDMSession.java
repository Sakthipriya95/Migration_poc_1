/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.jpa;

/**
 * This class maintains the JPA connection and session
 */
import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.WindowOpenMode;
import com.bosch.caltool.icdm.logger.A2LLogger;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;

/**
 * This class keeps all application level objects like data providers, EASEE CDM web service etc.
 *
 * @author ADN1COB
 */
public class CDMSession {

  /**
   * Product version
   */
  // ICDM-252
  private String productVersion;

  /**
   * Super User's vCDM interface
   */
  private VCDMInterface vCdmSuperUser;

  /**
   * Current User's vCDM interface
   */
  private VCDMInterface vCdmCurrentUser;

  // iCDM-438
  /**
   * Defines single sign on, by default SSO is enabled
   */
  private boolean ssoEnabled = true;

  /**
   * Defines if the application window should be opened in Minimised/Maximised mode
   */
  private WindowOpenMode wMode;

  private CDMSession() {
    // Private constructor for singleton instance
  }

  /**
   * Helper class for singleton initialisation
   *
   * @author bne4cob
   */
  private static class SingletonHelper {

    /**
     * Unique instance of CDMSession
     */
    private static final CDMSession INSTANCE = new CDMSession();

    /**
     * Private constructor
     */
    private SingletonHelper() {
      // Nothing to do
    }
  }

  /**
   * This method returns CDMSession instance
   *
   * @return CDMSession
   */
  public static CDMSession getInstance() {
    return SingletonHelper.INSTANCE;
  }

  /**
   * Initialize the CDM session, from iCDM client
   *
   * @param username the user name for creating the connection.
   * @param userFirstName user First Name
   * @param userLastName user Last Name
   * @param userDepartment user Department
   * @throws IcdmException Exception during initialization
   */
  public void initialize(final String username, final String userFirstName, final String userLastName,
      final String userDepartment)
      throws IcdmException {

    CDMLogger.getInstance().info("Initialising iCDM session... ");

    DataModelInitializer dmIniter;
    if (CommonUtils.isStartedFromWebService()) {
      dmIniter = new DataModelInitializer(username, getProductVersion());
    }
    else {
      dmIniter = new DataModelInitializer(username, userFirstName, userLastName, userDepartment, getProductVersion());
    }
    dmIniter.initializeModel();

    try {
      this.vCdmSuperUser = new VCDMInterface(EASEELogger.getInstance(), A2LLogger.getInstance());
    }
    catch (Exception exp) {
      // ICDM-1111
      // Dialog and error to be logged separately to avoid minimization of welcome page
      // If CDMLogger.getInstance().warnDialog is used, due to activation of error view the above problem occurss
      CDMLogger.getInstance().warnDialog("VCDM Webservice login failed. Opening A2L Files is not possible",
          JPAActivator.PLUGIN_ID);
      CDMLogger.getInstance().error("VCDM Webservice login failed. Opening A2lFiles is not possible", exp);
    }

    CDMLogger.getInstance().info("ICDM session initialised");

  }

  /**
   * Initialize the CDM session, from iCDM web service
   *
   * @param username the user name for creating the connection.
   * @throws IcdmException Exception during initialization
   */
  public void initialize(final String username) throws IcdmException {
    initialize(username, null, null, null);
  }

  /**
   * @return the productVersion
   */
  public String getProductVersion() {
    return this.productVersion;
  }


  /**
   * @param productVersion the productVersion to set
   */
  public void setProductVersion(final String productVersion) {
    this.productVersion = productVersion;
  }

  /**
   * @return the vcdmInterfaceForSuperUser
   */
  public VCDMInterface getVcdmInterfaceForSuperUser() {
    return this.vCdmSuperUser;
  }

  /**
   * @return the vcdmInterfaceForLoggedInUser
   */
  public VCDMInterface getVcdmInterfaceForLoggedInUser() {
    return this.vCdmCurrentUser;
  }

  /**
   * Set single sign on info
   *
   * @param singleSignOn sso flag
   */
  public void setSSO(final boolean singleSignOn) {
    this.ssoEnabled = singleSignOn;
  }

  /**
   * Get if the NT user logged in as single sign-on
   *
   * @return true if sso
   */
  public boolean isSSO() {
    return this.ssoEnabled;
  }

  // /**
  // * iCDM-1241 Checks if the tool is started from URL (in such cases welcome pages needs to be skipped)
  // *
  // * @return true if trom url
  // */
  // public boolean isLinkFromURL() {
  // return this.objURL != null;
  // }
  //
  // /**
  // * Set the URL object
  // *
  // * @param urlObj URLObject
  // */
  // public void setURLObject(final URLObject urlObj) {
  // this.objURL = urlObj;
  // }
  //
  // /**
  // * @return URLObject
  // */
  // public URLObject getURLObject() {
  // return this.objURL;
  // }

  /**
   * Close the database connections
   */
  public void closeDatabaseConnection() {
    // Close the database objects stored in ObjectStore
    ObjectStore.getInstance().closeDatabaseResources();
  }


  /**
   * @return the wMode
   */
  public WindowOpenMode getwMode() {
    return this.wMode;
  }


  /**
   * @param wMode the wMode to set
   */
  public void setwMode(final WindowOpenMode wMode) {
    this.wMode = wMode;
  }


}
