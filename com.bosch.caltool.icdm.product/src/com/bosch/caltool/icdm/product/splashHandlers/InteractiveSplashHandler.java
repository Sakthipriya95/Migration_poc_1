/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

// NOTE : do not change the package name 'splashHandlers'. During sync with product file, eclipse resets the splash
// handler's path. This results in creation of a default handler and its usage
package com.bosch.caltool.icdm.product.splashHandlers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.splash.AbstractSplashHandler;

import com.bosch.calcomp.twofachecker.TWOFA_STATUS;
import com.bosch.calcomp.twofachecker.TwoFAChecker;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.client.bo.cns.CnsListener;
import com.bosch.caltool.icdm.client.bo.connectionstate.ConnectionPoller;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.general.IcdmSessionHandler;
import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.GenericException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.PropFileReader;
import com.bosch.caltool.icdm.common.util.VersionValidator;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.CnsClientLogger;
import com.bosch.caltool.icdm.logger.TwoFALogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.TWOFA_CHECK_LEVEL;
import com.bosch.caltool.icdm.model.general.AzureUserModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.product.azure.sso.AzureSSO;
import com.bosch.caltool.icdm.product.dialogs.ICDMDisclaimerDialog;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.InitializationProperties;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.IcdmClientStartupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.IcdmVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmAvailabilityCheckServiceClient;

/**
 * @since 3.3
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {

  private static final String MSG_AUTH_FAILED = "Authentication Failed";
  // iCDM-438
  /**
   * Command line argument value for ssoYN
   */
  private static final String SSO_VAL_N = CommonUtilConstants.CODE_NO;
  /**
   * Command line argument for sso
   */
  private static final String CMD_ARG_SSO = "-ssoYN";

  /**
   * Progress completed
   */
  private static final int PGRS_COMPLETED = 100;

  /**
   * Progress after user validation
   */
  private static final int PGRS_AFTR_VLDTION = 40;

  /**
   * Progress before user validation
   */
  private static final int PGRSS_BFR_VLDTION = 20;

  /**
   * Progress bar horizontal span
   */
  private static final int PGRS_BAR_HOR_SPAN = 3;

  /**
   * progress bar height hint
   */
  private static final int PGRS_BAR_HT_HINT = 12;

  /**
   * Cancel button's vertical indent
   */
  private static final int CNCL_VRT_INDENT = 10;

  /**
   * OK button's vertical indent
   */
  private static final int OK_BTN_VER_INDENT = 10;

  /**
   * Label's horizontal indent
   */
  private static final int F_LABEL_H_INDENT = 175;

  /**
   * Button's width
   */
  private static final int F_BTN_WIDTH_HINT = 80;

  /**
   * Text fields' width
   */
  private static final int F_TEXT_WIDTH_HINT = 175;

  /**
   * No. of columns in the grid
   */
  private static final int F_COLUMN_COUNT = 3;


  /**
   * Composite
   */
  private Composite fCompositeLogin;
  /**
   * User name text
   */
  private Text fTextUsername;
  /**
   * Passoword text field
   */
  private Text fTextPassword;
  /**
   * Ok button
   */
  private Button fButtonOK;
  /**
   * Cancel button
   */
  private Button fButtonCancel;
  /**
   * Authenticated flag
   */
  private boolean fAuthenticated = false;
  /**
   * Progress Bar for loading application and data model
   */
  private ProgressBar fBar;
  private boolean sso;

  /**
   * Open splash. if SSO is enabled, authenticate using SSO
   * <p>
   *
   * @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets .Shell)
   */
  @Override
  public final void init(final Shell splash) {
    // Store the shell
    super.init(splash);
    // Configure the shell layout
    configureUISplash();

    this.sso = isSingleSignOn();
    // iCDM-438
    // Prepare minimal layout if SSO is enabled
    if (this.sso) {
      createSsoUI(splash);
      azureAdSSOLogin(false);
    }
    else {
      // Create UI with user name , password fields..
      createUI();
      // Create UI listeners
      createUIListeners();
      // Force the splash screen to layout
      splash.layout(true);
    }
    // Keep the splash screen visible and prevent the RCP application from
    // loading until the close button is clicked.
    doEventLoop();


  }


  /**
   * Handle events.
   */
  private void doEventLoop() {
    final Shell splash = getSplash();
    while (!this.fAuthenticated) {
      if (!splash.getDisplay().readAndDispatch()) {
        splash.getDisplay().sleep();
      }
    }
  }

  /**
   * Creates UI Listeners.
   */
  private void createUIListeners() {
    // Create enter key is pressed listener
    enterKeyListenerForOkButton();
    // Create the OK button listeners
    this.fButtonOK.addSelectionListener(new SelectionAdapter() {

      /**
       * Authenticate the user
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        handleButtonOKWidgetSelected();
      }
    });
    // Create the cancel button listeners
    this.fButtonCancel.addSelectionListener(new SelectionAdapter() {

      /**
       * Close splash and application
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Abort the loading of the RCP application
        exitSplashAndAppln();
      }
    });
  }

  /**
   * This method invokes on on enter key press to perform ok button selection.
   */
  private void enterKeyListenerForOkButton() {
    this.fTextPassword.addKeyListener(new KeyAdapter() {

      /**
       * Redirect to button listener
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.character == SWT.CR) {
          InteractiveSplashHandler.this.fButtonOK.notifyListeners(SWT.Selection, new Event());
        }
      }
    });
    this.fTextUsername.addKeyListener(new KeyAdapter() {

      /**
       * Redirect to button listener
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.character == SWT.CR) {
          InteractiveSplashHandler.this.fButtonOK.notifyListeners(SWT.Selection, new Event());
        }
      }
    });
  }

  /**
   * Method hanldes the action logic on OK widget selected.
   */
  private void handleButtonOKWidgetSelected() {

    // Get username and password
    final String username = this.fTextUsername.getText();
    final String password = this.fTextPassword.getText();
    // Get properties file instance
    final Properties iCDMProperties = PropFileReader.getInstance().getiCDMProperties();
    // Validate if properties file is available
    if (iCDMProperties == null) {
      // add all errors to loggers
      CDMLogger.getInstance().info("iCDM.properties file missing!!");
      MessageDialog.openError(getSplash(), "Properties file missing!", //$NON-NLS-1$
          "iCDM.Properties file should be available in user's APPDATA's iCDM sub-directory!" +
              " Please check log for more details.");
      // fatal error, exit application
      exitSplashAndAppln();
    }
    else {
      // Authorization is provided using LDAP username and password
      if ((username.length() > 0) && (password.length() > 0)) {
        this.fBar.setVisible(true);
        this.fBar.setSelection(PGRSS_BFR_VLDTION);
        // Validate credentials
        validateUser(username, password);
      }
      else {
        // add all errors to loggers
        CDMLogger.getInstance().info(MSG_AUTH_FAILED);
        MessageDialog.openError(getSplash(), MSG_AUTH_FAILED, "A username and password must be specified to login."); //$NON-NLS-1$
        this.fTextPassword.setText("");
        this.fTextUsername.setFocus();
      }
    }
  }

  /**
   * Creates UI components.
   */
  private void createUI() {

    createComposite();

    createFields();

    createButtons();

    createProgressBar();

  }


  /**
   * Prepares UI for SSO login
   *
   * @param splash Shell
   */
  // iCDM-438
  private void createSsoUI(final Shell splash) {

    createComposite();
    createProgressBar();
    this.fBar.setVisible(true);
    this.fBar.setSelection(PGRSS_BFR_VLDTION);
    splash.layout(true);


  }

  /**
   * Create buttons.
   */
  private void createButtons() {
    // Create the OK button
    this.fButtonOK = new Button(this.fCompositeLogin, SWT.PUSH);
    this.fButtonOK.setText("OK"); //$NON-NLS-1$
    // Configure layout data
    final GridData gdOkBtn = new GridData(SWT.NONE, SWT.NONE, false, false);
    gdOkBtn.widthHint = F_BTN_WIDTH_HINT;
    gdOkBtn.verticalIndent = OK_BTN_VER_INDENT;
    this.fButtonOK.setLayoutData(gdOkBtn);

    // Create the Cancel button
    this.fButtonCancel = new Button(this.fCompositeLogin, SWT.PUSH);
    this.fButtonCancel.setText("Cancel"); //$NON-NLS-1$
    // Configure layout data
    final GridData gdCancelBtn = new GridData(SWT.NONE, SWT.NONE, false, false);
    gdCancelBtn.widthHint = F_BTN_WIDTH_HINT;
    gdCancelBtn.verticalIndent = CNCL_VRT_INDENT;
    this.fButtonCancel.setLayoutData(gdCancelBtn);

  }

  /**
   * Create text fields and their labels.
   */
  private void createFields() {
    // Create the Username label
    final Label lblUsername = new Label(this.fCompositeLogin, SWT.NONE);
    lblUsername.setText("&User Name:"); //$NON-NLS-1$
    // Configure layout data
    final GridData gdUserLbl = new GridData();
    gdUserLbl.horizontalIndent = F_LABEL_H_INDENT;
    lblUsername.setLayoutData(gdUserLbl);

    // Create the Username text widget
    this.fTextUsername = new Text(this.fCompositeLogin, SWT.BORDER);
    // Configure layout data
    final GridData gdUserFld = new GridData(SWT.NONE, SWT.NONE, false, false);
    gdUserFld.widthHint = F_TEXT_WIDTH_HINT;
    gdUserFld.horizontalSpan = 2;
    this.fTextUsername.setLayoutData(gdUserFld);
    this.fTextUsername.addModifyListener(event -> setEnableOKBtn());

    // Create the password label
    final Label lblPassword = new Label(this.fCompositeLogin, SWT.NONE);
    lblPassword.setText("&Password:"); //$NON-NLS-1$
    // Configure layout data
    final GridData gdPasswordLbl = new GridData();
    gdPasswordLbl.horizontalIndent = F_LABEL_H_INDENT;
    lblPassword.setLayoutData(gdPasswordLbl);

    // Create the password text box
    final int style = SWT.PASSWORD | SWT.BORDER;
    this.fTextPassword = new Text(this.fCompositeLogin, style);
    // Configure layout data
    final GridData gdPsswdFld = new GridData(SWT.NONE, SWT.NONE, false, false);
    gdPsswdFld.widthHint = F_TEXT_WIDTH_HINT;
    gdPsswdFld.horizontalSpan = 2;
    this.fTextPassword.setLayoutData(gdPsswdFld);
    this.fTextUsername.setFocus();
    this.fTextPassword.addModifyListener(event -> setEnableOKBtn());

    // Blank label
    final Label lblBlank = new Label(this.fCompositeLogin, SWT.NONE);
    lblBlank.setVisible(false);


  }

  /**
   * Create composite.
   */
  private void createComposite() {
    // Create the composite
    this.fCompositeLogin = new Composite(getSplash(), SWT.BORDER);
    final GridLayout layout = new GridLayout(F_COLUMN_COUNT, false);
    this.fCompositeLogin.setLayout(layout);

    final Composite spanner = new Composite(this.fCompositeLogin, SWT.NONE);
    final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.horizontalSpan = F_COLUMN_COUNT;
    spanner.setLayoutData(data);


  }

  /**
   * Creates the UI Progress bar.
   */
  private void createProgressBar() {
    this.fBar = new ProgressBar(this.fCompositeLogin, SWT.HORIZONTAL);
    this.fBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    ((GridData) this.fBar.getLayoutData()).heightHint = PGRS_BAR_HT_HINT;
    ((GridData) this.fBar.getLayoutData()).horizontalSpan = PGRS_BAR_HOR_SPAN;
    this.fBar.setMaximum(IProgressMonitor.UNKNOWN);
    this.fBar.setVisible(false);
  }

  /**
   * Creates UI Splash.
   */
  private void configureUISplash() {
    // Configure layout
    final FillLayout layout = new FillLayout();
    getSplash().setLayout(layout);
    // Force shell to inherit the splash background
    getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
  }

  /**
   * This method enables Ok button based on user name and password text field validations.
   */
  private void setEnableOKBtn() {
    this.fButtonOK.setEnabled(validateTextFields());
  }

  /**
   * This method validates user name and password text fields.
   *
   * @return boolean
   */
  private boolean validateTextFields() {
    final String userId = this.fTextUsername.getText();
    final String password = this.fTextPassword.getText();
    return (((userId != null) && (!"".equals(userId.trim()))) && ((password != null) && (!"".equals(password.trim()))));
  }

  /**
   * The method to authorize via LDAP authorization..
   *
   * @param username user name
   * @param password password
   */
  private void validateUser(final String username, final String password) {
    // Use LDAP user authorization
    CDMLogger.getInstance().debug("Login mode : Normal; Logging in as ->" + username);
    // Validate USER entered from splash screen
    try {
      UserInfo userInfo = new LdapAuthenticationWrapper().validate(username, password);
      // Display appprpriate message
      doPostUserAuthentication(userInfo.getUserName(), userInfo.getGivenName(), userInfo.getSurName(),
          userInfo.getDepartment());

      new CurrentUserBO().setPassword(password);
    }
    catch (LdapException ldapExp) {
      // Display appropriate message if failed
      displayMsgIfInvalid(MSG_AUTH_FAILED, ldapExp);
    }
    catch (ApicWebServiceException e) {
      displayMsgIfInvalid("iCDM Error", e);
    }
  }

  /**
   * Verifies SSO login
   */
  // iCDM-438
  private void validateSSO() {
    // Use LDAP user authorization
    CDMLogger.getInstance().debug("Login mode : SSO");
    // Validate USER
    try {
      UserInfo userInfo = new LdapAuthenticationWrapper().validateSSO();
      // inserting entry into the Login Info Table to indicate User has Used LDAP login
      AzureSSO.insertUserLoginInfo(userInfo.getUserName().toUpperCase(), 0L, 1L);
      // Display appprpriate message
      doPostUserAuthentication(userInfo.getUserName(), userInfo.getGivenName(), userInfo.getSurName(),
          userInfo.getDepartment());
    }
    catch (LdapException ldapExp) {
      // Display appropriate message if failed
      displayMsgIfInvalid(MSG_AUTH_FAILED, ldapExp);
    }
  }


  private void azureAdSSOLogin(final boolean retryFlag) {
    String password = (new PasswordServiceWrapper(CDMLogger.getInstance()))
        .getPassword(Messages.getString(Messages.ICDM_WS_TOKEN_KEY));
    String username = Messages.getString(Messages.ICDM_STARTUP_LOGIN_USER);
    AzureUserModel azureUserModel = null;
    try {
      initializeIcdmWsClient(username, password);
      azureUserModel = new AzureSSO().azureAdSSOLogin();
      CDMLogger.getInstance().debug("Azure SSO login Success");
      doPostUserAuthentication(azureUserModel.getUserName(), azureUserModel.getFirstName(),
          azureUserModel.getLastName(), azureUserModel.getDepartment());
    }
    catch (PartInitException | MalformedURLException | ApicWebServiceException | UnsupportedEncodingException e) {
      azureAuthFailureAction(retryFlag);
    }
  }

  private void azureAuthFailureAction(final boolean retryFlag) {
    if (retryFlag) {
      // User has already retried Azure SSO
      // Proceed with LDAP Auth
      CDMLogger.getInstance().debug("Retry of Azure SSO login Failed, proceeding with windows Login");
      validateSSO();
    }
    else {
      boolean confirm = MessageDialog.openConfirm(getSplash(), "Azure SSO Failed",
          "Single Sign On using Azure Failed, click Ok to reattempt Authentication via Browser");
      if (confirm) {
        CDMLogger.getInstance().debug("Azure SSO login Failed, retrying...");
        azureAdSSOLogin(true);
      }
      else {
        CDMLogger.getInstance()
            .debug("User clicked cancel for Retry Azure SSO confirmation, proceeding with windows Login");
        validateSSO();
      }
    }

  }


  /**
   * Display appropriate message based on the validation
   */
  private void doPostUserAuthentication(final String userName, final String firstName, final String lastName,
      final String department) {
    this.fAuthenticated = true;
    this.fBar.setSelection(PGRS_AFTR_VLDTION);
    // iCDMLogger.info -> with two params - logs in log view and log file

    CDMLogger.getInstance().info(firstName + " " + lastName + ", " + ICDMConstants.STARTUP_LOG_VIEW_MESSAGE,
        Activator.PLUGIN_ID);
    // LDAP Login successful

    // Initialize iCDM web service, CNS service clients
    try {

      // Get web service token from password service
      String password = (new PasswordServiceWrapper(CDMLogger.getInstance()))
          .getPassword(Messages.getString(Messages.ICDM_WS_TOKEN_KEY));

      ClientConfiguration.getDefault().setUserName(userName);
      // validates the iCDM version and displays error message if the user is using an older version of iCDM
      // version validation should be done before fetching common params to avoid mismatch in commonparams between
      // server and client
      validateIcdmVersion();

      validate2FALogin();

      initializeCnsClient(userName, password);

      ConnectionPoller.INSTANCE.start();

      validateUserAccess();

      validateVcdmServicesAvailability();

      // update the logged in user details and the timestamp
      updateCurrentUserDetails(firstName, lastName, department);

      // download welcome page
      downloadWelcomePage();
    }
    catch (ApicWebServiceException | GenericException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      exitSplashAndAppln();
    }
    catch (DataException exp) {
      CDMLogger.getInstance().warnDialog("vCDM Web service login failed. Opening A2L Files is not possible",
          Activator.PLUGIN_ID);
      CDMLogger.getInstance().error("vCDM Web service login failed. Opening A2L Files is not possible", exp);
    }
    catch (Exception exp) {
      CDMLogger.getInstance().errorDialog("Unexpected error occured during application startup. " + exp.getMessage(),
          exp, Activator.PLUGIN_ID);
      exitSplashAndAppln();
    }

    try {
      if (new CurrentUserBO().isDisclaimerAcceptanceExpired()) {
        ICDMDisclaimerDialog aboutDialog = new ICDMDisclaimerDialog(Display.getDefault().getActiveShell(), userName);
        aboutDialog.open();
        if (!aboutDialog.getAcceptFlag()) {
          exitSplashAndAppln();
        }
      }
    }
    catch (ApicWebServiceException | IcdmException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    if (!this.fBar.isDisposed()) {
      this.fBar.setSelection(PGRS_COMPLETED);
    }
  }


  private void displayMsgIfInvalid(final String title, final Exception exp) {

    MessageDialog.openError(getSplash(), title, exp.getMessage());

    CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);

    this.fBar.setVisible(false);
    // In case of sigle sign on, close the tool
    if (this.sso) {
      exitSplashAndAppln();
    }
  }

  /**
   * Checks from cmd line args if sso is NOT enabled
   *
   * @param args
   * @return
   */
  // iCDM-438
  private boolean isSingleSignOn() {

    String[] args = Platform.getCommandLineArgs();

    boolean ret = true;
    if (args != null) {
      int index = 0;
      while (index < args.length) {
        if (CMD_ARG_SSO.equals(args[index]) && ((index + 1) < args.length) && SSO_VAL_N.equals(args[index + 1])) {
          ret = false;
          break;
        }
        index++;
      }
    }
    return ret;
  }


  /**
   * @throws DataException
   */
  private void validateVcdmServicesAvailability() throws DataException {
    try {
      new VcdmAvailabilityCheckServiceClient().isVcdmAvailable();
    }
    catch (ApicWebServiceException exp) {
      throw new DataException(
          "vCDM services not available. Opening A2L Files etc. are not possible. " + exp.getMessage(), exp);
    }
  }

  /**
   * @throws ApicWebServiceException
   * @throws GenericException
   */
  private void validateIcdmVersion() throws ApicWebServiceException, GenericException {
    // Validation is done by comparing the version number mentioned in product plugin against the version number
    // configured in the database table TABV_COMMON_PARAMS

    final String dbVersion = new IcdmVersionServiceClient().getIcdmVersion();
    // ICDM-2263
    VersionValidator validator = new VersionValidator(dbVersion, false);
    if (!validator.validateVersions(Activator.getDefault().getPluginVersion())) {
      throw new GenericException(
          "A newer version of iCDM client v(" + dbVersion + ") is available. Please upgrade to the latest.");
    }
  }

  private void validate2FALogin() throws GenericException, ApicWebServiceException {

    // Check level of 2FA
    String paramValue = new CommonDataBO().getParameterValue(CommonParamKey.TWOFA_CHECK_LEVEL);
    CDMLogger.getInstance().debug("2FA check level is {}", paramValue);
    TWOFA_CHECK_LEVEL twofaCheckLevel = TWOFA_CHECK_LEVEL.get2FACheckLevel(paramValue);

    // If the level is none then 2FA validation is not required
    if (twofaCheckLevel != TWOFA_CHECK_LEVEL.NONE) {

      TWOFA_STATUS state = new TwoFAChecker(TwoFALogger.getInstance()).check();

      // If the user is not logged in via 2FA or WTS server then throw error or warning based on 2FA check level
      if ((state != TWOFA_STATUS.AUTH_2FA) && (state != TWOFA_STATUS.AUTH_NT_REMOTE)) {

        // Check whether to block the user by throwing error
        if (twofaCheckLevel == TWOFA_CHECK_LEVEL.BLOCK) {
          throw new GenericException(new CommonDataBO().getMessage(ICDMConstants.TWOFA, ICDMConstants.ERROR_MSG));
        }

        // Show only a warning
        CDMLogger.getInstance().warnDialog(
            new CommonDataBO().getMessage(ICDMConstants.TWOFA, ICDMConstants.WARNING_MSG), Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private void validateUserAccess() throws ApicWebServiceException, IcdmException {
    if (!new CurrentUserBO().hasMinimumAccess()) {
      throw new IcdmException(new CommonDataBO().getMessage(ICDMConstants.ICDM_STARTUP, ICDMConstants.NO_ACCESS_TEXT));
    }
  }


  private void initializeIcdmWsClient(final String username, final String password) throws ApicWebServiceException {
    InitializationProperties wsClientProps = new InitializationProperties();
    wsClientProps.setLogger(CDMLogger.getInstance());

    // Server from messages.properties
    wsClientProps.setServer(Messages.getString(Messages.ICDM_WS_SERVER_KEY));

    // User name from LDAP authentication
    wsClientProps.setUserName(username);

    wsClientProps.setPassword(password);

    wsClientProps.setIcdmTempDirectory(CommonUtils.getICDMTmpFileDirectoryPath());

    // Get language from Language Preference settings
    final String selectedLang = PlatformUI.getPreferenceStore().getString(ApicConstants.LANGUAGE);
    Language language = Language.getLanguage(selectedLang);
    wsClientProps.setLanguage(language.getText());

    wsClientProps.setCnsEnabled(true);

    ClientConfiguration.getDefault().initialize(wsClientProps);

  }


  /**
   * download the welcome page
   */
  public void downloadWelcomePage() {
    try {
      // fetch welcome page

      String dirPath =
          CommonUtils.concatenate(CommonUtils.getSystemUserTempDirPath(), File.separator, ApicConstants.WELCOME_PAGE);
      CommonUtils.deleteFile(dirPath);

      dirPath = CommonUtils.concatenate(dirPath, File.separator);

      byte[] files = new IcdmClientStartupServiceClient().getWelcomePageFiles(CommonUtils.getSystemUserTempDirPath());
      Map<String, byte[]> filesUnZipped = ZipUtils.unzip(files);
      createWelcomeIcdmFiles(dirPath, filesUnZipped);
    }
    catch (ApicWebServiceException | IOException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  // ICDM-1566
  private void createWelcomeIcdmFiles(final String dirPath, final Map<String, byte[]> files) throws IOException {
    Set<String> keySet = files.keySet();
    for (String key : keySet) {
      if (!CommonUtils.isEmptyString(FilenameUtils.getExtension(key))) {// check if path has file
        byte[] fileBytes = files.get(key);
        CommonUtils.createFile(dirPath.concat(key), fileBytes); // creating file in temp path
        // (C:\..\AppData\Local\Temp\welcome_page\)
      }
    }
  }

  /**
   * @param password
   * @param user
   * @throws ApicWebServiceException error while retrieving CNS URL
   * @throws CnsServiceClientException error while connecting to CNS Server
   */
  private void initializeCnsClient(final String user, final String password)
      throws CnsServiceClientException, ApicWebServiceException {
    int listeneingPort = getAvailableCnsListeningPort();

    CnsClientConfiguration config = new CnsClientConfiguration();

    String cnsUrl = new CommonDataBO().getParameterValue(CommonParamKey.CNS_SERVER_URL);
    config.setBaseUrl(cnsUrl);
    config.setLogger(CnsClientLogger.getInstance());
    config.setUser(user);
    config.setPassword(password);

    // Find timezone offset from client timezone in minutes
    int offset = TimeZone.getDefault().getOffset(new Date().getTime()) / 1000 / 60;
    config.setTimezoneOffset(offset);

    CnsClientConfiguration.initialize(config);

    String sessionId = new CnsDataConsumerServiceClient().createSession(listeneingPort);
    CnsClientConfiguration.getDefaultConfig().setSessionId(sessionId);

    ClientConfiguration.getDefault().setCnsSessionId(sessionId);


    CnsListener.INSTANCE.start();

  }


  /**
   * @return port number
   */
  private int getAvailableCnsListeningPort() {
    // TODO initialize listener port
    return 0;
  }


  /**
   * Exits the splash and the application
   */
  private void exitSplashAndAppln() {
    if ((getSplash() != null) && (getSplash().getDisplay() != null) && !getSplash().getDisplay().isDisposed()) {
      getSplash().getDisplay().close();
    }
    new IcdmSessionHandler().closeSessions();
    System.exit(0);
  }

  /**
   * Updates the current user details
   */
  private void updateCurrentUserDetails(final String firstName, final String lastName, final String department)
      throws ApicWebServiceException {
    UserServiceClient userServiceClient = new UserServiceClient();
    User user = userServiceClient.getCurrentApicUser();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setDepartment(department);
    userServiceClient.update(user);
  }


}
