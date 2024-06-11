/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.cdr.ui.jobs.CdrReportExcelExportJob;
import com.bosch.caltool.cdr.ui.jobs.CompareHexWithRwDataExportJob;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CdfxExportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * @author say8cob
 */
public class CalStatusListDialog extends AbstractDialog {

  private static final String REGEX = "[^a-zA-Z0-9]+";

  /** message for FILE already opened. */
  private static final String FILE_ALREADY_OPEN_MSG =
      "Excel file already open, Please close the file and try export again";

  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  private FormToolkit formToolkit;

  private Composite composite;

  private Button exportBtn;
  /**
   * Section instance
   */
  private Section section;

  private Form form;

  private final Color whiteColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

  private final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);

  private Button readinessChkBox;

  private Text excelExportPathText;

  private Button standExcelReport;

  private Button calStatusList;

  private final CdrReportEditorInput cdrReportEditorInput;

  private CdrReportDataHandler cdrData;

  private final CompHexWithCDFxDataHandler compData;

  private String fileExtn;

  private final boolean isCDRExcelExport;

  private ControlDecoration isReadinessFulfilled;

  // Hold the value of the file directory path in which the Data Review Report would be saved
  private String fileDirectoryPath;

  // Hold the value of the file name of Data Review Report
  private String fileName;

  /**
   * @param parentShell as shell
   * @param cdrReportEditorInput as editor input for Data Review Report
   * @param compData as editor input for CompareHEx
   * @param isCDRExcelExport true for data review / false for compare hex
   */
  public CalStatusListDialog(final Shell parentShell, final CdrReportEditorInput cdrReportEditorInput,
      final CompHexWithCDFxDataHandler compData, final boolean isCDRExcelExport) {
    super(parentShell);
    this.cdrReportEditorInput = cdrReportEditorInput;
    this.compData = compData;
    this.isCDRExcelExport = isCDRExcelExport;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Calibration Status List");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);
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

  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Calibration Status List");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    GridLayout gridLayout = new GridLayout();
    // create scrolled composite
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

    this.composite = new Composite(scrollComp, SWT.NONE);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);

    createSection();

    this.composite.layout(true, true);

    scrollComp.setLayout(gridLayout);
    scrollComp.setLayoutData(gridData);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setContent(this.composite);
    scrollComp.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    return parent;
  }


  /**
   * create section
   *
   * @param parent
   */
  private void createSection() {
    if (CommonUtils.isNotNull(this.cdrReportEditorInput)) {
      this.cdrData = this.cdrReportEditorInput.getReportData();
    }

    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    this.section =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), gridData, "What to export ?");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    // set the client
    this.section.setClient(this.form);
  }

  /**
   * @param parent
   */
  private void createForm() {

    this.form = getFormToolkit().createForm(this.section);

    final Composite maincomposite = new Composite(this.composite, SWT.NONE);
    GridData gridDataMain = new GridData(SWT.FILL, SWT.FILL, true, true);
    maincomposite.setLayout(new GridLayout(1, true));
    maincomposite.setLayoutData(gridDataMain);
    maincomposite.setBackground(this.whiteColor);

    // create the controls for scope selection
    createRadioControls(maincomposite);
    // create the controls for readiness selection
    createReadinessControls(maincomposite);
    // create the controls for export selection
    createExportFileControls(maincomposite);

    final GridLayout gridLayout = new GridLayout();
    // 3 columns for the layout
    gridLayout.numColumns = 3;
    gridLayout.verticalSpacing = 9;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

  }


  /**
   * @param firstRowComposite // Icdm-715 UI Changes to Wizard
   */
  private void createRadioControls(final Composite firstRowComposite) {
    // ICDM-2032
    this.standExcelReport = new Button(firstRowComposite, SWT.RADIO);
    this.standExcelReport.setText("Standard Excel Report");
    this.standExcelReport.setSelection(false);
    this.standExcelReport.setBackground(this.whiteColor);
    this.standExcelReport.setSelection(true);
    this.standExcelReport.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        CalStatusListDialog.this.readinessChkBox.setEnabled(false);
        enableDiableExportBtn();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // No Implementation Needed
      }
    });

    this.calStatusList = new Button(firstRowComposite, SWT.RADIO);
    this.calStatusList.setText("100% Calibration Status List");
    this.calStatusList.setSelection(false);
    this.calStatusList.setBackground(this.whiteColor);
    this.calStatusList.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        CalStatusListDialog.this.readinessChkBox.setEnabled(true);
        showDecForReadinessFulfilled();
        enableDiableExportBtn();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // No Implementation Needed
      }
    });

  }

  /**
   * create variant controls
   */
  private void createReadinessControls(final Composite maincomposite) {
    // composite for displaying radio buttons

    final Group readinessGroup = new Group(maincomposite, SWT.NONE);
    readinessGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(SWT.FILL, SWT.FILL, true, false);
    readinessGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    readinessGroup.setText("Readiness : ");
    readinessGroup.setBackground(this.whiteColor);

    Composite readinessComposite = getFormToolkit().createComposite(readinessGroup);
    GridLayout layout = new GridLayout(1, false);
    readinessComposite.setLayout(layout);

    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.widthHint = 400;
    data.heightHint = 200;
    readinessComposite.setLayoutData(data);

    Browser browser = new Browser(readinessComposite, SWT.NONE);
    browser.setText(getCdfxReadinessConditionStr());
    GridData gridDataBrowser = new GridData(SWT.FILL, SWT.FILL, true, true);
    browser.setLayoutData(gridDataBrowser);

    this.readinessChkBox = new Button(readinessGroup, SWT.CHECK);
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.readinessChkBox.setBackground(this.whiteColor);
    this.readinessChkBox.setLayoutData(gridData);
    this.readinessChkBox.setText("Readiness fulfilled");
    this.readinessChkBox.setEnabled(false);


    this.isReadinessFulfilled = new ControlDecoration(this.readinessChkBox, SWT.LEFT | SWT.TOP);


    // add the listener
    this.readinessChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        showDecForReadinessFulfilled();
        enableDiableExportBtn();
      }
    });
  }

  /**
   * Enable/Disable tooltip and decorator for Readiness fulfilled basd on export type
   */
  protected void showDecForReadinessFulfilled() {
    Decorators decorators = new Decorators();
    // If 100% calibration export is selected, then Readiness fulfilled should be checked
    if (!this.readinessChkBox.getSelection() && this.calStatusList.getSelection()) {
      decorators.showReqdDecoration(this.isReadinessFulfilled,
          "Readiness should be read and fulfilled for 100% Calibration status list");
    }
    else {
      decorators.showErrDecoration(this.isReadinessFulfilled, IUtilityConstants.EMPTY_STRING, false);
    }
  }

  /**
   * @return
   */
  private String getCdfxReadinessConditionStr() {
    String htmlContentStr = "";
    try {
      htmlContentStr = FileIOUtil.convertHtmlByteToString(
          new CdfxExportServiceClient().getCdfxReadinessConditionFile(CommonUtils.getSystemUserTempDirPath()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return htmlContentStr;
  }

  /**
   * create variant controls
   */
  private void createExportFileControls(final Composite maincomposite) {

//     composite for displaying radio buttons
    Composite exportComposite = getFormToolkit().createComposite(maincomposite);
    GridLayout gridLayoutForComp = new GridLayout();
    gridLayoutForComp.numColumns = 3;
    GridData exportGridData = new GridData();
    exportGridData.grabExcessHorizontalSpace = true;
    exportGridData.horizontalAlignment = GridData.FILL;
    exportComposite.setLayout(gridLayoutForComp);
    exportComposite.setLayoutData(exportGridData);
    getFormToolkit().createLabel(exportComposite, "Output File : ");

    if (CommonUtils.isEmptyString(this.preference.getString(ApicConstants.LAST_USED_FILE_PATH))) {
      this.fileDirectoryPath = CommonUtils.getUserDirPath();
    }
    else {
      /**
       * incase the previously used directory is deleted, the userdirectory path should be set to default directory path
       */
      File f = new File(this.preference.getString(ApicConstants.LAST_USED_FILE_PATH));
      if (f.exists() && f.isDirectory()) {
        this.fileDirectoryPath = this.preference.getString(ApicConstants.LAST_USED_FILE_PATH);
      }
      else {
        this.fileDirectoryPath = CommonUtils.getUserDirPath();
      }
    }

    if (CalStatusListDialog.this.isCDRExcelExport &&
        CommonUtils.isNotNull(CalStatusListDialog.this.cdrReportEditorInput)) {
      // setting file name for Data Review Report
      this.fileName = new CdrActionSet().getExcelFileName(CalStatusListDialog.this.cdrData);
    }
    else if (CommonUtils.isNotNull(CalStatusListDialog.this.compData)) {
      // Setting file name for Compare HEX Report
      this.fileName = getCompareHexFileName();
    }
    this.excelExportPathText = getFormToolkit().createText(exportComposite,
        this.fileDirectoryPath.concat("\\").concat(this.fileName), SWT.SINGLE | SWT.BORDER);
    this.excelExportPathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.excelExportPathText.setEditable(false);
    this.excelExportPathText.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        enableDiableExportBtn();
      }
    });

    ControlDecoration zipExportPathTextDec = new ControlDecoration(this.excelExportPathText, SWT.LEFT | SWT.TOP);
    new Decorators().showReqdDecoration(zipExportPathTextDec, "This field is mandatory.");

    this.excelExportPathText.addModifyListener(evnt -> Validator.getInstance().validateNDecorate(zipExportPathTextDec,
        this.excelExportPathText, null, null, true));

    final Button browseBtn = getFormToolkit().createButton(exportComposite, "", SWT.PUSH);

    browseBtn.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {

        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);

        fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
        fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
        /**
         * file already exists scenario will be handled at the file export part instead of file dialog's save part
         */
        fileDialog.setOverwrite(false);
        fileDialog.setFilterIndex(0);
        fileDialog.setFileName(CalStatusListDialog.this.fileName);


        String fileSelected = fileDialog.open();
        /**
         * Incase the file dialog is opened and the user clicks cancel button without selecting any preferance should
         * not be overriden with an empty value. FilterPath would be empty if the file dialog is opned and no file is
         * selected.
         */
        if (!CommonUtils.isEmptyString(fileDialog.getFilterPath())) {
          CalStatusListDialog.this.preference.putValue(ApicConstants.LAST_USED_FILE_PATH, fileDialog.getFilterPath());
        }

        CalStatusListDialog.this.fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
        if (fileSelected != null) {
          String completeFilePath = CommonUtils.getCompleteFilePath(fileSelected, CalStatusListDialog.this.fileExtn);
          final File file = new File(completeFilePath);
          if (CommonUtils.checkIfFileOpen(file)) {
            MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN, FILE_ALREADY_OPEN_MSG);
            return;
          }
          CalStatusListDialog.this.excelExportPathText.setText(completeFilePath);

        }
        /**
         * if the user deletes the last used directory by navigating through file dialog and does not select any other
         * directory to save the excel file, user directory would be set to file path
         */
        else if (CommonUtils.isEmptyString(fileDialog.getFilterPath())) {

          File f = new File(CalStatusListDialog.this.preference.getString(ApicConstants.LAST_USED_FILE_PATH));
          if (!(f.exists() && f.isDirectory())) {
            CalStatusListDialog.this.excelExportPathText.setText(CommonUtils.getUserDirPath().concat("\\")
                .concat(CalStatusListDialog.this.fileName).concat(".").concat(CalStatusListDialog.this.fileExtn));
            CalStatusListDialog.this.preference.putValue(ApicConstants.LAST_USED_FILE_PATH, fileDialog.getFilterPath());
          }
        }
        enableDiableExportBtn();
      }
    });
    browseBtn.setImage(this.browseImage);
    browseBtn.setToolTipText("Select zip export filepath");
  }

  private String getCompareHexFileName() {
    String varName = (this.compData.getSelctedVar()) == null ? "<NO-VARIANT>" : this.compData.getSelctedVar().getName();
    String hexFileName = "Comparison - " + this.compData.getHexFileName() + "  and \nReview Results of " +
        this.compData.getA2lFile().getFilename() + "\nVariant - " + varName;

    return hexFileName.replaceAll(REGEX, "_");
  }

  private void enableDiableExportBtn() {
    this.exportBtn.setEnabled(CommonUtils.isNotEmptyString(this.excelExportPathText.getText()) &&
        (this.readinessChkBox.getEnabled() ? this.readinessChkBox.getSelection()
            : !this.readinessChkBox.getEnabled()) &&
        (this.standExcelReport.getSelection() || this.calStatusList.getSelection()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.exportBtn = createButton(parent, IDialogConstants.OK_ID, "Export", true);
    /** export button gets enabled by default for 'standard excel report case' */
    this.exportBtn.setEnabled(true);
    CalStatusListDialog.this.excelExportPathText.setText(
        this.fileDirectoryPath.concat("\\").concat(this.fileName).concat(".").concat(ExcelConstants.FILTER_EXTNS[0]));
    // create button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected void okPressed() {
    String filePath = this.excelExportPathText.getText();
    /** set the default file extension to .xlsx */

    CalStatusListDialog.this.fileExtn =
        CalStatusListDialog.this.fileExtn == null ? ExcelConstants.FILTER_EXTNS[0] : CalStatusListDialog.this.fileExtn;

    final File file = new File(filePath);

    /** check if the file already exists */
    if (file.exists() && (!MessageDialogUtils.getConfirmMessageDialogWithYesNo(IMessageConstants.CONFIRM_SAVE_AS,
        this.fileName.concat("." + this.fileExtn).concat(IMessageConstants.CONFIRM_SAVE_AS_MSG)))) {
      return;
    }
    /** check if the file is opened */
    if (CommonUtils.checkIfFileOpen(file)) {
      MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN, FILE_ALREADY_OPEN_MSG);
      return;
    }

    CalStatusListDialog.this.excelExportPathText.setText(filePath);

    if (this.isCDRExcelExport && CommonUtils.isNotNull(this.cdrReportEditorInput) &&
        CommonUtils.isNotEmptyString(filePath)) {

      if (this.standExcelReport.getSelection()) {
        // job to export the cdr report
        final Job job =
            new CdrReportExcelExportJob(this.cdrData, filePath, this.fileExtn, this.standExcelReport.getSelection());
        CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        job.schedule();
      }
      else if (this.calStatusList.getSelection()) {
        final Job job =
            new CdrReportExcelExportJob(this.cdrData, filePath, this.fileExtn, !this.calStatusList.getSelection());
        CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        job.schedule();
      }
    }
    else if (CommonUtils.isNotNull(this.compData) && CommonUtils.isNotEmptyString(filePath)) {
      if (this.standExcelReport.getSelection()) {
        Job job = new CompareHexWithRwDataExportJob(new MutexRule(), this.compData, filePath, this.fileExtn,
            this.standExcelReport.getSelection());
        CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        job.schedule();
      }
      else if (this.calStatusList.getSelection()) {
        Job job = new CompareHexWithRwDataExportJob(new MutexRule(), this.compData, filePath, this.fileExtn,
            !this.calStatusList.getSelection());
        CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        job.schedule();
      }
    }
    super.okPressed();
  }


}
