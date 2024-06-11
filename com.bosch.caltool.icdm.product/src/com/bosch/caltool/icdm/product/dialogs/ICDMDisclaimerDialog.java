package com.bosch.caltool.icdm.product.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.general.IcdmSessionHandler;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.IcdmClientStartupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author dmo5cob This class is for dispalying the disclaimer dialog on icdm tool startup.
 */
public class ICDMDisclaimerDialog extends Dialog {

  /**
   * Constant for the label against Accept radio button
   */
  private static final String ACCEPT_THE_AGREEMENT = "I accept the agreement";
  /**
   * Constant for the label against Do not Accept radio button
   */
  private static final String DO_NOT_ACCEPT = "I do not accept the agreement";
  /**
   * Flag to indicate if the user has accepted the terms and conditions
   */
  private boolean acceptFlag;
  /**
   * Logged in user details
   */
  private final String userName;

  /**
   * @param parentShell Shell
   * @param username username
   */
  public ICDMDisclaimerDialog(final Shell parentShell, final String userName) {
    super(parentShell);
    this.userName = userName;
  }


  /**
   * @return html message string
   */
  private String getMessage() {
    String htmlContentStr = "";

    IcdmClientStartupServiceClient icdmStartUpServiceClient = new IcdmClientStartupServiceClient();
    byte[] bytes;
    try {
      bytes = icdmStartUpServiceClient.getDisclaimerFile(CommonUtils.getSystemUserTempDirPath());
      Map<String, byte[]> bytesMap = ZipUtils.unzip(bytes);
      byte[] disclaimerFile = bytesMap.get(ApicConstants.ICDM_DISCLAIMER_FILE_NAME);
      if (null != disclaimerFile) {
        htmlContentStr = FileIOUtil.convertHtmlByteToString(disclaimerFile);
      }
    }
    catch (ApicWebServiceException | IOException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return htmlContentStr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);

    GridLayout layout = new GridLayout(1, false);
    composite.setLayout(layout);

    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.widthHint = 600;
    data.heightHint = 400;
    composite.setLayoutData(data);

    Browser browser = new Browser(composite, SWT.NONE);
    browser.setText(getMessage());
    browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    // accept radio button
    createAcceptOption(composite);
    // do not accept radio button
    createDoNotAcceptOption(composite);

    return composite;
  }

  /**
   * @param composite
   */
  private void createDoNotAcceptOption(final Composite composite) {

    Button noAcceptBtn = new Button(composite, SWT.RADIO);
    noAcceptBtn.setText(DO_NOT_ACCEPT);
    noAcceptBtn.addSelectionListener(new SelectionListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ICDMDisclaimerDialog.this.acceptFlag = false;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });
  }

  /**
   * @param grp
   */
  private void createAcceptOption(final Composite composite) {

    Button acceptBtn = new Button(composite, SWT.RADIO);
    acceptBtn.setText(ACCEPT_THE_AGREEMENT);
    acceptBtn.addSelectionListener(new SelectionListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ICDMDisclaimerDialog.this.acceptFlag = true;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("iCDM disclaimer");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void okPressed() {
    if (this.acceptFlag) {
      // update the date
      updateAcceptanceDate();

    }
    else {
      // close connections
      new IcdmSessionHandler().closeSessions();
      // clear temp files
      File tempFilesDir = new File(CommonUtils.getICDMTmpFileDirectoryPath() + "");
      try {
        FileUtils.cleanDirectory(tempFilesDir);
      }
      catch (IOException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    // close the dialog
    close();
  }


  /**
   * Update the date on which the user has accepted the terms and conditions. Save the date in TABV_APIC_USERS table
   */
  private void updateAcceptanceDate() {
    UserServiceClient userServiceClient = new UserServiceClient();
    try {

      User user = userServiceClient.getApicUserByUsername(this.userName);
      user.setDisclaimerAcceptedDate(ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_15));
      userServiceClient.update(user);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @return the acceptFlag
   */
  public boolean getAcceptFlag() {
    return this.acceptFlag;
  }


}

