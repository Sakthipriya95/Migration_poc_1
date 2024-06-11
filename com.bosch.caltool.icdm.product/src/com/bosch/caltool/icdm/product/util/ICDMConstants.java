/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.util;

/**
 * ICDMConstants.java This class maintains all CONSTANTS applicable to the main perspective
 *
 * @author adn1cob
 */
public final class ICDMConstants {

  /**
   * Constant defining FILE on Menu
   */
  public static final String FILE = "File";

  /**
   * Constant defining ADMINISTRATION on Menu
   */
  public static final String ADMIN = "Admin";
  /**
   * Constant defining HELP on Menu
   */
  public static final String HELP = "Help";
  /**
   * Constant defining WINDOW on Menu
   */
  public static final String WINDOW = "Window";
  /**
   * Constant defining MAIN
   */
  public static final String MAIN = "Main";
  /**
   * Constant defining DEBUG option id in preference page
   */
  public static final String PREF_ID_DEBUG_OPTION = "org.eclipse.debug.ui.DebugPreferencePage";
  /**
   * Constant defining JAVA option id in preference page
   */
  public static final String PREF_ID_JAVA_OPTION = "org.eclipse.jdt.ui.preferences.JavaBasePreferencePage";
  /**
   * Constant defining TEAM option id in preference page
   */
  public static final String PREF_ID_TEAM_OPTION = "org.eclipse.team.ui.TeamPreferences";
  /**
   * Constant defining GENERAL option id in preference page
   */
  public static final String PREF_ID_WORKBENCH_OPTION = "org.eclipse.ui.preferencePages.Workbench";

  /**
   * Constant defining ANT option id in preference page
   */
  public static final String PREF_ID_ANT_PREF_OPTION = "org.eclipse.ant.ui.AntPreferencePage";

  /**
   * Constant defining PLUG-IN option id in preference page
   */
  public static final String PREF_ID_PLUG_IN_OPTION = "org.eclipse.pde.ui.MainPreferencePage";

  /**
   * Constant defining activity ID for Navigations
   */
  public static final String ACTIV_ID_NAVIGATION = "org.eclipse.ui.edit.text.actionSet.navigation";
  /**
   * Constant defining activity ID for ANNOTATION Navigations
   */
  public static final String ACTIV_ID_ANNOT_NAVIGATION = "org.eclipse.ui.edit.text.actionSet.annotationNavigation";
  /**
   * Constant defining activity ID for Convert Line Delimeter ID
   */
  public static final String ACTIV_ID_CONVERT_LINE_DELIM = "org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo";

  /**
   * ICDM Startup message in iCDM log
   */
  public static final String STARTUP_LOG_MESSAGE = "****************** Starting iCDM *********************";

  /**
   * ICDM Startup message in iCDM LOG-View
   */
  public static final String STARTUP_LOG_VIEW_MESSAGE = "Welcome to iCDM !";
  /**
   * Label defined for Preference Logging - specify log file location
   */
  public static final String ENTR_LOG_FILE_LOC = "Log file location has to be entered";
  /**
   * Label define for Preference Logging - default location
   */
  public static final String USE_DEFAULT_LOG_LOCATION = "Use default log location";
  /**
   * Base title for iCDM application
   */
  public static final String ICDM_TITLE_BASE = "iCDM - Intelligent Calibration Data Management";

  // ICDM-105
  /**
   * The perspective id for the iCDM
   */
  public static final String ID_PERSP_ICDM = "com.bosch.caltool.icdm.product.perspectives.CDMPerspective";
  /**
   * The perspective id for the Project-ID-Card
   */
  public static final String ID_PERSP_PIDC = "com.bosch.caltool.icdm.product.perspectives.PIDCPerspective";
  /**
   * The perspective id for the Use case
   */
  public static final String ID_PERSP_USECASE = "com.bosch.caltool.icdm.product.perspectives.UseCasePerspective";
  /**
   * The perspective id for the Calibration Data Review
   */
  public static final String ID_PERSP_CDR = "com.bosch.caltool.icdm.product.perspectives.CalibrationDataReview";
  /**
   * The perspective id for the Component Packages
   */
  public static final String ID_PERSP_CMPKG = "com.bosch.caltool.icdm.product.perspectives.ComponentPackages";

  /**
   * Workbench part reference interface
   */
  public static final String WORKBENCH_PART_REFERENCE = "org.eclipse.ui.IWorkbenchPartReference";
  /**
   *
   */
  public static final String ICDM_STARTUP = "ICDM_STARTUP";
  /**
   *
   */
  public static final String NO_ACCESS_TEXT = "NO_ACCESS_TEXT";

  /**
   * 2FA validation group
   */
  public static final String TWOFA = "2FA";

  /**
   * Warning message
   */
  public static final String WARNING_MSG = "WARNING_MSG";

  /**
   * Error message
   */
  public static final String ERROR_MSG = "ERROR_MSG";
  
  /**
   * View id of citibot plugin
   */
  public static final String CITI_VIEW_ID = "com.bosch.citi.lite.client.citiview";
  

  /**
   * 
   */
  public static final String RIGHT_FOLDER = "RightFolder";


  /**
   * Private constructor
   */
  private ICDMConstants() {
    // Private constructor to prevent instance creation
  }
}
