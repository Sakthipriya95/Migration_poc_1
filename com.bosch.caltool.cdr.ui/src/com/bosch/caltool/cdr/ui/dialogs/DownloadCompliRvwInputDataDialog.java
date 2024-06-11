/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CompliReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author dmr1cob
 */
public class DownloadCompliRvwInputDataDialog extends TitleAreaDialog {

  /**
   * Diallog title
   */
  private static final String DIALOG_TITLE = "Download Files";
  /**
   * mandotary field decorator
   */
  private static final String DESC_TXT_MANDATORY = "This field is mandatory.";

  /**
   * Minimum width of the dialog
   */
  private static final int DIALOG_MIN_WIDTH = 100;

  /**
   * Minimum height of the dialog
   */
  private static final int DIALOG_MIN_HEIGHT = 50;

  /**
   * Form toolkit
   */
  private FormToolkit formToolkit;

  /**
   * Execution Id text field
   */
  private Text executionId;

  /**
   * Output file directory text field
   */
  private Text destinationPath;

  /**
   * Import Input data button
   */
  private Button downloadBtn;

  /**
   * Import button Constant
   */
  public static final String DOWNLOAD_BUTTON_CONSTANT = "Download";

  /**
   * Compliance review Dialog
   */
  private final CompliReviewDialog compliRvwDialog;
  /**
   * True if files downloaded successfully
   */
  private boolean downloadSuccess;

  /**
   * @param parentShell shell
   * @param compliRvwDialog {@link CompliReviewDialog}
   */
  public DownloadCompliRvwInputDataDialog(final Shell parentShell, final CompliReviewDialog compliRvwDialog) {
    super(parentShell);
    this.compliRvwDialog = compliRvwDialog;
  }

  /**
   * Configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Enter Execution ID and Destination");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {

    final Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout());

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.minimumWidth = DIALOG_MIN_WIDTH;
    gridData.minimumHeight = DIALOG_MIN_HEIGHT;
    composite.setLayoutData(gridData);

    createMainComposite(composite);

    return composite;
  }

  /**
   * Create the main composite and its contents
   */
  private void createMainComposite(final Composite composite) {

    final Composite mainComposite = getFormToolkit().createComposite(composite);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    mainComposite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    mainComposite.setLayoutData(gridData);
    // Create text field for Execution Id
    createExecutionIdTextField(mainComposite);
    createDestinationTextField(mainComposite);
    preFillExecutionId();
  }

  /**
   * Create Execution Id text field
   *
   * @param comp parent composite
   */
  private void createExecutionIdTextField(final Composite comp) {
    createLabelControl(comp, "Execution ID");
    this.executionId = createTextField(comp);
    this.executionId.setEnabled(true);
    this.executionId.setEditable(true);
    this.executionId.addModifyListener(e -> {
      autoFillDestinationPath();
      enableOkButton();
    });

    ControlDecoration decorator = new ControlDecoration(this.executionId, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
    createLabelControl(comp, "");
  }

  /**
   * @param mainComposite
   */
  private void createDestinationTextField(final Composite comp) {
    createLabelControl(comp, "Destination");
    this.destinationPath = createTextField(comp);
    this.destinationPath.setEnabled(true);
    this.destinationPath.setEditable(false);
    this.destinationPath.addModifyListener(e -> enableOkButton());

    final Button browseBtn = getFormToolkit().createButton(comp, "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Save Zip Report to :");
        fileDialog.setFilterExtensions(new String[] { "*.zip" });
        fileDialog.setFilterNames(new String[] { "Zip Format (.zip)" });
        fileDialog.setOverwrite(true);
        fileDialog.setFilterIndex(0);
        if (CommonUtils.isNotEmptyString(DownloadCompliRvwInputDataDialog.this.destinationPath.getText())) {
          String fileName =
              FilenameUtils.getBaseName(DownloadCompliRvwInputDataDialog.this.destinationPath.getText()) + ".zip";
          fileDialog.setFileName(fileName);
          fileDialog.setFilterPath(
              FilenameUtils.getFullPath(DownloadCompliRvwInputDataDialog.this.destinationPath.getText()) + fileName);
        }

        String selectedFile = fileDialog.open();
        if (CommonUtils.isNotEmptyString(selectedFile) && !selectedFile.endsWith(".zip")) {
          selectedFile = selectedFile + ".zip";
        }
        DownloadCompliRvwInputDataDialog.this.destinationPath.setText(selectedFile);
      }
    });

    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    browseBtn.setToolTipText("Select zip export filepath");

    ControlDecoration decorator = new ControlDecoration(this.destinationPath, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
  }


  private void autoFillDestinationPath() {
    String execId = this.executionId.getText();
    if (CommonUtils.isNotEmptyString(execId)) {
      StringBuilder zipFileName = new StringBuilder();
      zipFileName.append(execId);
      zipFileName.append(".zip");
      String zipFilePath = CommonUtils.getSystemUserTempDirPath() + zipFileName.toString();
      if (CommonUtils.isNotEmptyString(DownloadCompliRvwInputDataDialog.this.destinationPath.getText())) {
        zipFilePath = FilenameUtils.getFullPath(DownloadCompliRvwInputDataDialog.this.destinationPath.getText()) +
            zipFileName.toString();
      }
      File zipFile = new File(zipFilePath);
      if (CommonUtils.checkIfFileOpen(zipFile)) {
        DownloadCompliRvwInputDataDialog.this.downloadBtn.setEnabled(false);
        DownloadCompliRvwInputDataDialog.this.destinationPath.setText("");
        MessageDialogUtils.getInfoMessageDialog("",
            "'The selected target file is already open in another program. Please close the file and choose again.");
        return;
      }
      DownloadCompliRvwInputDataDialog.this.destinationPath.setText(zipFilePath);
    }
    else {
      DownloadCompliRvwInputDataDialog.this.destinationPath.setText("");
    }
  }

  /**
   * Enable OK button only if execution id is provided
   */
  private void enableOkButton() {
    if (null != this.downloadBtn) {
      this.downloadBtn.setEnabled(CommonUtils.isNotEmptyString(this.executionId.getText()) &&
          CommonUtils.isNotEmptyString(this.destinationPath.getText()));
    }
  }

  /**
   * prefill execution id if available
   */
  private void preFillExecutionId() {
    String execId = null == this.compliRvwDialog.getExecutionId() ? "" : this.compliRvwDialog.getExecutionId();
    this.executionId.setText(execId);
  }

  /**
   * Creates a text field
   *
   * @param comp parent composite
   * @return Text the new field
   */
  private Text createTextField(final Composite comp) {
    final Text text = TextUtil.getInstance().createEditableText(this.formToolkit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  /**
   * Creates a label
   *
   * @param compparent composite
   * @param lblName label text
   */
  private void createLabelControl(final Composite comp, final String lblName) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);

  }

  /**
   * @return FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Allow resizing
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.downloadBtn = createButton(parent, IDialogConstants.OK_ID, DOWNLOAD_BUTTON_CONSTANT, false);
    this.downloadBtn.setEnabled(CommonUtils.isNotEmptyString(this.executionId.getText()) &&
        CommonUtils.isNotEmptyString(this.destinationPath.getText()));
    createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
  }

  @Override
  protected void okPressed() {
    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    String execId = DownloadCompliRvwInputDataDialog.this.executionId.getText();
    String destPath = DownloadCompliRvwInputDataDialog.this.destinationPath.getText();
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Downloading files", 100);
        monitor.setTaskName("Downloading Input/Output files");
        monitor.worked(10);
        try {
          new CompliReviewServiceClient().downloadAllFiles(execId, FilenameUtils.getBaseName(destPath),
              FilenameUtils.getFullPath(destPath));
          openDirectory(destPath);
          DownloadCompliRvwInputDataDialog.this.downloadSuccess = true;
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    if (this.downloadSuccess) {
      super.close();
    }
  }

  private void openDirectory(final String filePath) {
    try {
      Runtime.getRuntime().exec("explorer.exe \"" + filePath + "\"");
    }
    catch (IOException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

}
