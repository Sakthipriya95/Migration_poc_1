/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lCompliParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Class to upload the a2l file path into a dialog and checks for compliance parameter in it
 *
 * @author svj7cob
 */
// Task 264144
public class A2LCompliParamCheckDialog extends AbstractDialog {

  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * save button
   */
  private Button generateBtn;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * browse button image
   */
  private Image browseButtonImage;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * a2l name text
   */
  private Text a2lNameText;

  /**
   * pdf export file path
   */
  private Text pdfExportPathText;

  private String ouputFilePath;
  /**
   * selc a2l file
   */
  protected String a2lFilePath;
  /**
   * the given a2l file
   */
  private File file;

  /**
   * To get the pdf report file name from the json file.
   */
  private A2lCompliParameterServiceResponse response;

  /**
   * Flag to check the completion of dialog monitor
   */
  private boolean isMonitorJobCompleted;

  /**
   * Flag to check the completion of progress dialog monitor
   */
  private boolean isProgressMonitorJobCompl;
  /**
   * Flag to check the completion of dialog open state
   */
  private boolean isDialogAlreadyOpenFlag;
  /**
   * Flag to check if the exception dialog already open
   */
  private boolean isExceptionDialogOpen;
  /**
   * Flag to check if the Cannot export dialog already open
   */
  private boolean isCannotDialogOpen;
  private DropFileListener a2lFileDropFileListener;
  private DropFileListener pdfFileDropFileListener;

  /**
   * Width of dialog
   */
  private static final int WIDTH_OF_DIALOG = 730;
  /**
   * Height of dialog
   */
  private static final int HEIGHT_OF_DIALOG = 300;

  /**
   * Initial state of Button to generate the pdf report
   */
  private static final String GENERATE_BUTTON_CONSTANT = "Generate";

  /**
   * Working state of Button to generate the pdf report
   */
  private static final String GENERATING_BUTTON_CONSTANT = "Generating...";

  /**
   * @param parentShell shell
   */
  public A2LCompliParamCheckDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * Configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Compliance Check of an A2L File");
    super.configureShell(newShell);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle("Compliance Check of an A2L File");
    // Set the message
    setMessage("Enter A2L File Path", IMessageProvider.INFORMATION);
    return contents;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.generateBtn = createButton(parent, IDialogConstants.OPEN_ID, GENERATE_BUTTON_CONSTANT, false);
    this.generateBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.response != null) {
      CommonUiUtils.openFile(this.ouputFilePath + "\\" + this.response.getReportFileName());
      super.okPressed();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;

  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.browseButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    // a2l file detail
    createA2lControl(txtGrid);
    createPdfControl(txtGrid);
  }

  /**
   * create a2l info
   *
   * @param txtGrid gridData
   */
  private void createA2lControl(final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "Input A2L File : ");
    this.a2lNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.a2lNameText.setLayoutData(txtGrid);
    this.a2lNameText.setEditable(false);

    // Adding Drop Listener for a2l file path textbox
    this.a2lFileDropFileListener = new DropFileListener(this.a2lNameText, new String[] { "*.a2l" });
    this.a2lFileDropFileListener.addDropFileListener(false);
    this.a2lFileDropFileListener.setEditable(true);
    this.a2lNameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable();
        autoFillPdfFilePath();
      }
    });
    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Upload A2L File");
        fileDialog.setFilterExtensions(new String[] { "*.a2l" });
        A2LCompliParamCheckDialog.this.a2lFilePath = fileDialog.open();
        if (A2LCompliParamCheckDialog.this.a2lFilePath != null) {
          A2LCompliParamCheckDialog.this.a2lNameText.setText(A2LCompliParamCheckDialog.this.a2lFilePath);
        }
      }
    });
    browseBtn.setImage(this.browseButtonImage);
    browseBtn.setToolTipText("Select a2l file");
  }

  private void autoFillPdfFilePath() {
    String a2lFilePath = A2LCompliParamCheckDialog.this.a2lNameText.getText();
    if (!CommonUtils.isEmptyString(a2lFilePath)) {
      String pdfFileName = "ComplianceParameters_" + FilenameUtils.getBaseName(a2lFilePath) + "_" +
          new SimpleDateFormat(DateFormat.DATE_FORMAT_14).format(new Date()) + ".pdf";
      String pdfFilePath = FilenameUtils.getFullPath(a2lFilePath);
      String pdfFullPath = pdfFilePath + pdfFileName;
      A2LCompliParamCheckDialog.this.file = new File(CommonUtils.getCompletePdfFilePath(pdfFullPath, ".pdf"));
      if (CommonUtils.checkIfFileOpen(A2LCompliParamCheckDialog.this.file)) {
        A2LCompliParamCheckDialog.this.generateBtn.setEnabled(false);
        A2LCompliParamCheckDialog.this.pdfExportPathText.setText("");
        MessageDialogUtils.getInfoMessageDialog("",
            "'The selected target file is already open in another program. Please close the file and choose again.");
        return;
      }
      A2LCompliParamCheckDialog.this.pdfExportPathText.setText(pdfFilePath);
    }
  }

  private void createPdfControl(final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "Output Zip  File : ");
    this.pdfExportPathText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.pdfExportPathText.setLayoutData(txtGrid);
    this.pdfExportPathText.setEditable(false);
    // Adding Drop Listener for pdf export textbox
    this.pdfFileDropFileListener = new DropFileListener(this.pdfExportPathText, new String[] { "*.pdf" });
    this.pdfFileDropFileListener.addDropFileListener(false);
    this.pdfFileDropFileListener.setEditable(true);
    this.pdfExportPathText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable();
      }
    });
    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        fileDialog.setText("Save Pdf Report to :");
        fileDialog.setFilterExtensions(new String[] { "*.pdf" });
        fileDialog.setFilterNames(new String[] { "PDF Format (.pdf)" });
        fileDialog.setOverwrite(true);
        fileDialog.setFilterIndex(0);
        if (null != A2LCompliParamCheckDialog.this.a2lFilePath) {
          fileDialog.setFileName(FilenameUtils.getBaseName(A2LCompliParamCheckDialog.this.a2lFilePath));
        }
        String selectedFile = fileDialog.open();
        String fileExtn = "pdf";
        if (selectedFile != null) {
          A2LCompliParamCheckDialog.this.file = new File(CommonUtils.getCompletePdfFilePath(selectedFile, fileExtn));
          if (CommonUtils.checkIfFileOpen(A2LCompliParamCheckDialog.this.file)) {
            A2LCompliParamCheckDialog.this.pdfExportPathText.setText("");
            MessageDialogUtils.getInfoMessageDialog("",
                "The selected target file is already open in another program. Please close the file and choose again.");
            return;
          }
          A2LCompliParamCheckDialog.this.pdfExportPathText.setText(selectedFile);
        }
      }
    });
    browseBtn.setImage(this.browseButtonImage);
    browseBtn.setToolTipText("Select pdf export filepath");
  }

  /**
   * checks whether the save button can be enabled or not
   */
  private void checkGenerateBtnEnable() {
    this.generateBtn.setEnabled(validateFields());

    if (this.generateBtn.isEnabled()) {
      this.generateBtn.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          if (!A2LCompliParamCheckDialog.this.isMonitorJobCompleted) {
            changeGenerateButtonState(false);
            A2LCompliParamCheckDialog.this.isDialogAlreadyOpenFlag = false;
            A2LCompliParamCheckDialog.this.isExceptionDialogOpen = false;
            A2LCompliParamCheckDialog.this.isCannotDialogOpen = false;
            try {
              final String a2lFilePath = A2LCompliParamCheckDialog.this.a2lNameText.getText();
              final String pdfFilePath =
                  A2LCompliParamCheckDialog.this.pdfExportPathText.getText() + "A2LCompliParam__" +
                      (new SimpleDateFormat("yyyyMMdd_hhmmss")).format(Calendar.getInstance().getTime()) + ".zip";

              ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
              dialog.run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(final IProgressMonitor monitor) {
                  if (checkFlagConditions()) {

                    A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();
                    monitor.beginTask("Fetching compliance parameter...", 100);
                    monitor.setTaskName("Checking compliance parameter");
                    monitor.worked(20);

                    try {

                      servClient.getA2lCompliParamFiles(a2lFilePath, "DUMMY", pdfFilePath);
                      File pdfFile = new File(pdfFilePath);
                      File tempPath = new File(System.getenv(Messages.getString("CommonUtils.APPDATA_DIR")) +
                          File.separator + pdfFile.getName());
                      Files.copy(pdfFile.toPath(), tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                      ZipUtils.unzip(tempPath.getAbsolutePath(), null);
                      A2LCompliParamCheckDialog.this.ouputFilePath =
                          ZipUtils.getZipFolderPath(tempPath.getAbsolutePath());

                      final String unzippedFolderPath = ZipUtils.getZipFolderPath(pdfFilePath);
                      File fileObj = new File(unzippedFolderPath);
                      File[] files = fileObj.listFiles();
                      for (File fil : files) {

                        if ("A2lComplianceReport.json".equals(fil.getName())) {
                          A2LCompliParamCheckDialog.this.response =
                              JsonUtil.toModel(fil, A2lCompliParameterServiceResponse.class);
                          break;
                        }
                      }

                      deleteFile(tempPath);
                    }
                    catch (IOException | ApicWebServiceException | InvalidInputException exp) {
                      CDMLogger.getInstance().error(exp.getMessage(), exp);
                      MessageDialogUtils.getErrorMessageDialog("Error", exp.getMessage());
                      A2LCompliParamCheckDialog.this.isExceptionDialogOpen = true;
                      return;
                    }
                    monitor.worked(70);
                    monitor.setTaskName("Exporting pdf report");


                    monitor.worked(100);
                    A2LCompliParamCheckDialog.this.isMonitorJobCompleted = true;
                    A2LCompliParamCheckDialog.this.isProgressMonitorJobCompl = true;
                    Display.getDefault().asyncExec(new Runnable() {

                      @Override
                      public void run() {
                        A2LCompliParamCheckDialog.this.okPressed();
                      }
                    });
                    monitor.done();
                  }
                }

                private void deleteFile(final File file) {
                  if (file.isDirectory()) {

                    // directory is empty, then delete it
                    if (file.list().length == 0) {
                      file.delete();

                    }
                    // Directory is not empty
                    else {

                      // list all the directory contents
                      String files[] = file.list();
                      for (String temp : files) {
                        // construct the file structure
                        File fileDelete = new File(file, temp);

                        // recursive delete
                        deleteFile(fileDelete);
                      }

                      // check the directory again, if empty then delete it
                      if (file.list().length == 0) {
                        file.delete();

                      }
                    }

                  }
                  else {
                    // if file, then delete it
                    file.delete();

                  }

                }

              });
            }
            catch (InvocationTargetException exp) {
              CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
            }
            catch (InterruptedException exp) {
              CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
            }
            finally {
              changeGenerateButtonState(true);
            }
          }
        }
      });
    }
  }

  /**
   * Checking the dialog open state
   *
   * @return true if any dialog in open state
   */
  private boolean checkFlagConditions() {
    return !A2LCompliParamCheckDialog.this.isProgressMonitorJobCompl &&
        !A2LCompliParamCheckDialog.this.isDialogAlreadyOpenFlag &&
        !A2LCompliParamCheckDialog.this.isExceptionDialogOpen && !A2LCompliParamCheckDialog.this.isCannotDialogOpen;
  }

  /**
   * Gets the job change listener /**
   */
  private void changeGenerateButtonState(final boolean checkFlag) {
    if ((null != A2LCompliParamCheckDialog.this.generateBtn) &&
        !A2LCompliParamCheckDialog.this.generateBtn.isDisposed()) {
      A2LCompliParamCheckDialog.this.generateBtn
          .setText(checkFlag ? GENERATE_BUTTON_CONSTANT : GENERATING_BUTTON_CONSTANT);
      A2LCompliParamCheckDialog.this.generateBtn.setEnabled(checkFlag);
    }
  }

  /**
   * Validate the a2l file name
   *
   * @return true if file name is available
   */
  private boolean validateFields() {
    boolean isValid = false;
    if (CommonUtils.isNotEmptyString(this.a2lNameText.getText()) &&
        (CommonUtils.isNotEmptyString(this.pdfExportPathText.getText()))) {
      isValid = true;
    }
    return isValid;
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
  protected Point getInitialSize() {
    return new Point(WIDTH_OF_DIALOG, HEIGHT_OF_DIALOG);
  }
}
