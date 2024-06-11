package com.bosch.caltool.icdm.product.dialogs;

import java.io.IOException;
import java.util.Map;

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

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.ws.rest.client.cda.CalDataAnalyzerServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob This class is for dispalying the disclaimer dialog on icdm tool startup.
 */
public class CaldataAnalyzerDisclaimerDialog extends Dialog {

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
  private Boolean acceptFlag;
  /**
   * Proceed button for disclaimer dialog
   */
  private Button proceedButton;


  /**
   * @param parentShell Shell
   */
  public CaldataAnalyzerDisclaimerDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * @return html string
   */
  public String getMessage() {
    String htmlContentStr = "";

    CalDataAnalyzerServiceClient cdaServiceClient = new CalDataAnalyzerServiceClient();
    byte[] bytes;
    try {
      bytes = cdaServiceClient.getDisclaimerFile(CommonUtils.getSystemUserTempDirPath());
      Map<String, byte[]> bytesMap = ZipUtils.unzip(bytes);
      byte[] disclaimerFile = bytesMap.get(ApicConstants.CDA_DISCLAIMER_FILE_NAME);
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
    data.heightHint = 200;
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
    noAcceptBtn.setSelection(true);
    noAcceptBtn.addSelectionListener(new SelectionListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        CaldataAnalyzerDisclaimerDialog.this.acceptFlag = false;
        CaldataAnalyzerDisclaimerDialog.this.proceedButton.setEnabled(false);
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
        CaldataAnalyzerDisclaimerDialog.this.acceptFlag = true;
        CaldataAnalyzerDisclaimerDialog.this.proceedButton.setEnabled(true);
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
    this.proceedButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.PROCEED_LABEL, true);
    this.proceedButton.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Calibration Data Analyzer Disclaimer");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void okPressed() {
    setReturnCode(this.acceptFlag ? OK : CANCEL);
    close();
  }

}

