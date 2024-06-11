/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpRespImportExcelParser;
import com.bosch.caltool.icdm.client.bo.a2l.A2lRespImportCSVParser;
import com.bosch.caltool.icdm.client.bo.a2l.ImportA2lWpRespInputProfileData;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lImportProfileDetails;
import com.bosch.caltool.icdm.model.a2l.A2lImportProfileFileType;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpImportProfile;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.report.excel.ExcelClientConstants;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpImportProfileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.CommonParamServiceClient;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * @author bru2cob
 */
public class ImportA2lWpRespFromInputFileDialog extends AbstractDialog {


  /**
   * enum for Upload Profile-Import Mode
   */
  private enum ImportMode {
                           WORKPACKAGE("W"),
                           PARAMETER("P"),
                           FUNCTION("F");

    private final String dbType;

    ImportMode(final String dbType) {
      this.dbType = dbType;
    }


    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

  }

  /**
   *
   */
  private static final String[] FILTER_CSV_EXTN = new String[] { "csv" };

  /**
   *
   */
  private static final String[] FILTER_EXCEL_EXTN = new String[] { "xlsx", "xls", "xlsm" };

  /**
  *
  */
  private static final String SEPARATOR = "->";

  /**
   *
   */
  private static final String FUNCTION_COLUMN = "Function Column : ";

  /**
   *
   */
  private static final String CSV = "csv";

  /**
   *
   */
  private static final String EXCEL = "Excel";


  /** Top composite. */
  private Composite top;

  /** Composite instance for the dialog. */
  private Composite composite;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Section instance. */
  private Section section;

  /** Form instance. */
  private Form form;
  /** Number of Columns of the grid layout. */
  private static final int GRID_NUM_COLS = 3;
  /**
   * Save button
   */
  protected Button importBtn;
  /**
   * Cancel button
   */
  protected Button cancelBtn;
  private String fileSelected;
  A2LWPInfoBO a2lWpInfoBo;
  A2LWpRespImportExcelParser parserObj;
  A2lRespImportCSVParser csvParserObj;
  private Combo fktOrLabelCombo;

  private Combo wpColCombo;

  private Combo respCombo;

  private Combo varGrpCombo;

  private String encoding;

  private Map<String, Integer> colHeadingMap;
  private boolean isFunctionColSelected;
  private boolean isLabelColSelected;
  private Button funcRadio;

  private Button labelRadio;

  private SortedSet<A2lVariantGroup> a2lVarGrps;

  private ImportA2lWpRespData parsedInputFileData;


  private SortedSet<String> headingSet;

  private Combo respTypeCombo;

  private ComboViewer sourceForUploadComboViewer;
  private ComboViewer fileEncodingComboViewer;
  private Combo sheetCombo;

  private A2lWpImportProfile selectedProfile;

  private Label funcOrParamLabel;

  private Text filePathText;
  private Button excelBrowse;

  private ControlDecoration fktOrLabelComboDec;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  private ControlDecoration wpColComboDec;

  private ControlDecoration respComboDec;

  private ControlDecoration sheetComboDec;

  private A2lImportProfileDetails importProfileDetails;

  private ControlDecoration varGrpDec;

  private int selIndex;

  private String selVarGrpName = null;

  private Label encodeLabel;

  private Button uploadOnlyWps;

  private Combo headingRowCombo;

  /**
   * key - row number, value - first 15 characters of the column 1 of that particular row number
   */
  private Map<Integer, String> rowNumWithContentMap;

  private Button wpPrefixChkBtn;

  private Label wpPrefixLabel;

  private Label respPrefixLabel;

  private Button respPrefixCheckBtn;

  private ControlDecoration headRowNumComboDec;

  private String[] charSets;

  private String selFileType;

  /**
   * @param parentShell
   */
  public ImportA2lWpRespFromInputFileDialog(final Shell parentShell, final A2LWPInfoBO a2lWpInfoBo) {
    super(parentShell);
    this.a2lWpInfoBo = a2lWpInfoBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Excel Import");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Load assignments from Excel file");
    return contents;
  }

  /**
   * Sets the Dialog Resizable
   *
   * @param newShellStyle
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX);
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite.
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section.
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Select additional list with WP and Resp. Information");
    this.section.setExpanded(true);
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes formToolkit.
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
   * This method initializes form.
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = GRID_NUM_COLS;

    this.form = this.formToolkit.createForm(this.section);
    gridLayout.numColumns = 3;

    createProfileUploadLabel();

    createInputListFileLabel();

    createFileEncodingLabel();

    createSheetNameLabel();

    createHeadingRowLabel();

    createUploadOnlyWpsLabel();

    createImportModeRadioBtn();

    createFuncOrParamLabel();

    createWpColLabel();

    createWpPrefixLabel();

    createRespColLabel();

    createRespPrefixLabel();

    // RESP_TYPE column
    createRespTypeLabel();

    createVariantGrpLabel();

    sheetComboSelectionListener();

    excelBrowseSelectionListener();

    uploadOnlyWPSelectionListener();

    this.form.getBody().setLayout(gridLayout);
  }

  /**
   *
   */
  private void createRespPrefixLabel() {
    this.respPrefixLabel = new Label(this.form.getBody(), SWT.NONE);
    this.respPrefixLabel.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.respPrefixLabel.setVisible(false);
    this.respPrefixCheckBtn = new Button(this.form.getBody(), SWT.CHECK);
    this.respPrefixCheckBtn.setSelection(false);
    this.respPrefixCheckBtn.setVisible(false);

    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void createWpPrefixLabel() {
    this.wpPrefixLabel = new Label(this.form.getBody(), SWT.NONE);
    this.wpPrefixLabel.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.wpPrefixLabel.setVisible(false);
    this.wpPrefixChkBtn = new Button(this.form.getBody(), SWT.CHECK);
    this.wpPrefixChkBtn.setSelection(false);
    this.wpPrefixChkBtn.setVisible(false);

    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void createHeadingRowLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Heading row : ");
    this.headingRowCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.headingRowCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.headingRowCombo.setEnabled(false);
    this.headRowNumComboDec = new ControlDecoration(this.headingRowCombo, SWT.LEFT | SWT.TOP);
    addHeadingRowSelectionListener();
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void addHeadingRowSelectionListener() {
    this.headingRowCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.headRowNumComboDec,
            ImportA2lWpRespFromInputFileDialog.this.headingRowCombo, true);
        validateUploadOnlyWps();
        try {
          setInputForCombos();
        }
        catch (InvalidInputException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        addExcelVarGrps();
        validateRadioBtns();
        setDefaultHeading(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getWpColumn(),
            ImportA2lWpRespFromInputFileDialog.this.wpColCombo);
        setDefaultHeading(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getRespColumn(),
            ImportA2lWpRespFromInputFileDialog.this.respCombo);
        setDefaultHeading(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getRespTypeColumn(),
            ImportA2lWpRespFromInputFileDialog.this.respTypeCombo);
        validateCombos();
        validateInputs();
      }
    });
  }

  /**
   *
   */
  private void uploadOnlyWPSelectionListener() {
    this.uploadOnlyWps.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        boolean uploadParams = !ImportA2lWpRespFromInputFileDialog.this.uploadOnlyWps.getSelection();
        ImportA2lWpRespFromInputFileDialog.this.funcRadio.setEnabled(uploadParams);
        ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setEnabled(uploadParams);
        ImportA2lWpRespFromInputFileDialog.this.labelRadio.setEnabled(uploadParams);
        ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.setEnabled(uploadParams);

        if (!uploadParams) {
          ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.removeAll();
          ImportA2lWpRespFromInputFileDialog.this.decorators.showErrDecoration(
              ImportA2lWpRespFromInputFileDialog.this.fktOrLabelComboDec, IUtilityConstants.EMPTY_STRING, false);
        }
        else {
          ImportA2lWpRespFromInputFileDialog.this.headingSet.forEach(heading -> {
            if (!heading.isEmpty()) {
              ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.add(heading);
            }
          });
        }

        validateRadioBtns();
        if (!ImportA2lWpRespFromInputFileDialog.this.funcRadio.getSelection() &&
            !ImportA2lWpRespFromInputFileDialog.this.labelRadio.getSelection()) {
          ImportA2lWpRespFromInputFileDialog.this.funcRadio.setSelection(true);
          ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setText(FUNCTION_COLUMN);
        }

        checkForEmptyCombos();
        validateCombos();
        validateInputs();
      }
    });

  }

  /**
   *
   */
  private void createUploadOnlyWpsLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Upload only WPs : ");
    this.uploadOnlyWps = new Button(this.form.getBody(), SWT.CHECK);
    this.uploadOnlyWps.setSelection(false);
    this.uploadOnlyWps.setEnabled(false);

    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void excelBrowseSelectionListener() {
    this.excelBrowse.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Import PID Card Excel/CSV");
        setFilterExt(fileDialog);

        ImportA2lWpRespFromInputFileDialog.this.fileSelected = fileDialog.open();
        if (ImportA2lWpRespFromInputFileDialog.this.fileSelected == null) {
          return;
        }
        ImportA2lWpRespFromInputFileDialog.this.importBtn.setEnabled(false);

        ImportA2lWpRespFromInputFileDialog.this.filePathText
            .setText(ImportA2lWpRespFromInputFileDialog.this.fileSelected);
        ImportA2lWpRespFromInputFileDialog.this.sheetCombo.removeAll();
        ImportA2lWpRespFromInputFileDialog.this.labelRadio.setSelection(false);
        ImportA2lWpRespFromInputFileDialog.this.funcRadio.setSelection(false);
        ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setText("");
        ImportA2lWpRespFromInputFileDialog.this.fileEncodingComboViewer.getCombo().removeAll();
        ImportA2lWpRespFromInputFileDialog.this.headingRowCombo.removeAll();
        clearAllCombos();
        enableDisableCombos(false);
        File selectedFile = new File(ImportA2lWpRespFromInputFileDialog.this.filePathText.getText());

        ImportA2lWpRespFromInputFileDialog.this.selFileType =
            ImportA2lWpRespFromInputFileDialog.this.selectedProfile.getProfileDetails().getFileType().getType();

        if (selectedFile.exists()) {
          checkFileExtnNParse();
        }
        try {
          setParsedData();
        }
        catch (InvalidInputException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          enableDisableCombos(false);
        }
      }


    });
  }

  /**
  *
  */
  private void checkFileExtnNParse() {
    String[] excelFileTypes = FILTER_EXCEL_EXTN;
    String[] splittedFilePathArr = ImportA2lWpRespFromInputFileDialog.this.filePathText.getText().split("\\.");
    String fileType = splittedFilePathArr[splittedFilePathArr.length - 1];
    if (Arrays.asList(excelFileTypes).contains(fileType)) {
      this.selFileType = EXCEL;
      parseExcel();
    }
    else if (CSV.equalsIgnoreCase(fileType)) {
      this.selFileType = CSV;
      parseCsv();
    }
  }

  /**
   * @param fileDialog
   */
  private void setFilterExt(final FileDialog fileDialog) {
    ImportA2lWpRespFromInputFileDialog.this.importProfileDetails =
        ImportA2lWpRespFromInputFileDialog.this.selectedProfile.getProfileDetails();
    A2lImportProfileFileType fileType = ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getFileType();
    String[] fileFormat;
    if (CommonUtils.isEmptyString(fileType.getFormat())) {
      String[] fileTypes = fileType.getType().split(",");
      fileFormat = addSupportedFormats(fileTypes);
    }
    else {
      fileFormat = fileType.getFormat().split(",");
    }
    setFilterExtForDiffFormats(fileDialog, fileFormat);
  }

  /**
   * @param fileFormat
   * @param fileTypes
   */
  private String[] addSupportedFormats(final String[] fileTypes) {
    List<String> fileFormatExtn = new ArrayList<>();
    for (String fType : fileTypes) {
      if (EXCEL.equalsIgnoreCase(fType)) {
        fileFormatExtn.addAll(Arrays.asList(FILTER_EXCEL_EXTN));
      }
      else if (CSV.equalsIgnoreCase(fType)) {
        fileFormatExtn.addAll(Arrays.asList(FILTER_CSV_EXTN));
      }
    }
    if (fileFormatExtn.isEmpty()) {
      fileFormatExtn.add("*.*");
    }

    return Arrays.copyOf(fileFormatExtn.toArray(), fileFormatExtn.toArray().length, String[].class);
  }

  /**
   * @param fileDialog
   * @param fileFormat
   */
  private void setFilterExtForDiffFormats(final FileDialog fileDialog, final String[] fileFormat) {
    for (int i = 0; i < fileFormat.length; i++) {
      fileFormat[i] = "*." + fileFormat[i];
    }
    fileDialog.setFilterExtensions(fileFormat);
  }

  /**
   *
   */
  private void sheetComboSelectionListener() {
    this.sheetCombo.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.sheetComboDec,
            ImportA2lWpRespFromInputFileDialog.this.sheetCombo, true);
        ImportA2lWpRespFromInputFileDialog.this.headingRowCombo.setEnabled(true);

        clearAllCombos();
        ImportA2lWpRespFromInputFileDialog.this.headingRowCombo.removeAll();
        try {
          getFirstColumnContent();
        }
        catch (InvalidInputException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          enableDisableCombos(false);
        }
        fillHeadingRowNumberCombo();
        ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.setEnabled(false);
        ImportA2lWpRespFromInputFileDialog.this.wpColCombo.setEnabled(false);
        ImportA2lWpRespFromInputFileDialog.this.respCombo.setEnabled(false);
        ImportA2lWpRespFromInputFileDialog.this.respTypeCombo.setEnabled(false);
        ImportA2lWpRespFromInputFileDialog.this.varGrpCombo.setEnabled(false);
        checkForEmptyCombos();
        validateCombos();
      }


    });
  }

  /**
   *
   */
  private void createVariantGrpLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Variant Group : ");
    this.varGrpCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.varGrpCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.varGrpCombo.setEnabled(false);
    this.varGrpDec = new ControlDecoration(this.varGrpCombo, SWT.LEFT | SWT.TOP);
    this.a2lVarGrps = new TreeSet<>(this.a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap().values());
    varGrpComboSelectionListener();
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void varGrpComboSelectionListener() {
    this.varGrpCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.varGrpDec,
            ImportA2lWpRespFromInputFileDialog.this.varGrpCombo, true);
        validateInputs();
      }
    });
  }

  /**
   *
   */
  private void createRespTypeLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Resp Type Column : ");
    this.respTypeCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.respTypeCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.respTypeCombo.setEnabled(false);
    respTypeSelectionListener();
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void respTypeSelectionListener() {
    this.respTypeCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        validateInputs();
      }
    });
  }

  /**
   *
   */
  private void createRespColLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Resp Column : ");
    this.respCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.respCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.respCombo.setEnabled(false);
    this.respComboDec = new ControlDecoration(this.respCombo, SWT.LEFT | SWT.TOP);
    respComboSelectionListener();
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void respComboSelectionListener() {
    this.respCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.respComboDec,
            ImportA2lWpRespFromInputFileDialog.this.respCombo, true);
        validateInputs();
      }
    });
  }

  /**
   *
   */
  private void createWpColLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("WP Column : ");
    this.wpColCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.wpColCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.wpColCombo.setEnabled(false);
    this.wpColComboDec = new ControlDecoration(this.wpColCombo, SWT.LEFT | SWT.TOP);
    wpColComboSelectionListener();
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void wpColComboSelectionListener() {
    this.wpColCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.wpColComboDec,
            ImportA2lWpRespFromInputFileDialog.this.wpColCombo, true);
        validateInputs();
      }
    });
  }

  /**
   *
   */
  private void createFuncOrParamLabel() {
    this.funcOrParamLabel = new Label(this.form.getBody(), SWT.NONE);
    this.funcOrParamLabel.setLayoutData(GridDataUtil.getInstance().getGridData());
    funcRadioBtnSelectionListener();
    labelRadioBtnSelectionListener();
    this.fktOrLabelCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.fktOrLabelCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.fktOrLabelCombo.setEnabled(false);
    this.fktOrLabelComboDec = new ControlDecoration(this.fktOrLabelCombo, SWT.LEFT | SWT.TOP);
    this.fktOrLabelCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.fktOrLabelComboDec,
            ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo, true);
        validateInputs();
      }
    });
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void labelRadioBtnSelectionListener() {
    this.labelRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ImportA2lWpRespFromInputFileDialog.this.labelRadio.getSelection()) {
          ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.deselectAll();
          ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setText("Parameter Column : ");
          if (!CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.sheetCombo.getText()) &&
              "P".equals(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getImportMode())) {
            setDefaultHeading(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getLabelColumn(),
                ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo);
          }
          if (CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.getText())) {
            ImportA2lWpRespFromInputFileDialog.this.decorators.showReqdDecoration(
                ImportA2lWpRespFromInputFileDialog.this.fktOrLabelComboDec, IUtilityConstants.MANDATORY_MSG);
          }
          else {
            Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.fktOrLabelComboDec,
                ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo, true);
          }
          validateInputs();
        }
      }
    });
  }

  /**
   *
   */
  private void funcRadioBtnSelectionListener() {
    this.funcRadio.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ImportA2lWpRespFromInputFileDialog.this.funcRadio.getSelection()) {
          ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.deselectAll();
          ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setText(FUNCTION_COLUMN);
          if (CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.getText())) {
            ImportA2lWpRespFromInputFileDialog.this.decorators.showReqdDecoration(
                ImportA2lWpRespFromInputFileDialog.this.fktOrLabelComboDec, IUtilityConstants.MANDATORY_MSG);
          }
          validateInputs();
        }
      }
    });
  }

  /**
   *
   */
  private void createImportModeRadioBtn() {
    new Label(this.form.getBody(), SWT.NONE).setText("Import mode : ");
    Composite radioComp = new Composite(this.form.getBody(), SWT.NONE);
    GridLayout radioGridLayout = new GridLayout();
    radioGridLayout.numColumns = 2;
    radioComp.setLayout(radioGridLayout);
    radioComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.funcRadio = new Button(radioComp, SWT.RADIO);
    this.funcRadio.setText("Function based  ");
    this.funcRadio.setEnabled(false);
    this.labelRadio = new Button(radioComp, SWT.RADIO);
    this.labelRadio.setText("Parameter based  ");
    this.labelRadio.setEnabled(false);
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void createSheetNameLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Sheet name : ");
    this.sheetCombo = new Combo(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.sheetCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sheetCombo.setEnabled(false);
    this.sheetComboDec = new ControlDecoration(this.sheetCombo, SWT.LEFT | SWT.TOP);
    this.sheetCombo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   *
   */
  private void createFileEncodingLabel() {
    this.encodeLabel = new Label(this.form.getBody(), SWT.NONE);
    this.encodeLabel.setText("File Encoding :");
    this.encodeLabel.setVisible(false);
    this.fileEncodingComboViewer = new ComboViewer(this.form.getBody(), SWT.READ_ONLY | SWT.BORDER);
    this.fileEncodingComboViewer.getCombo().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.fileEncodingComboViewer.getCombo().setEnabled(false);
    this.fileEncodingComboViewer.getCombo().setVisible(false);
    this.fileEncodingComboViewer.getCombo().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    new Label(this.form.getBody(), SWT.NONE);

    fileEncodingSelectionListener();
  }

  /**
   *
   */
  private void createInputListFileLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Input List File Path :");
    this.filePathText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.filePathText.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.filePathText.setEnabled(false);
    this.filePathText.setEditable(false);
    this.excelBrowse = new Button(this.form.getBody(), SWT.PUSH);
    this.excelBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    this.excelBrowse.setEnabled(false);
  }

  /**
   *
   */
  private void createProfileUploadLabel() {
    new Label(this.form.getBody(), SWT.NONE).setText("Source for Upload :");
    this.sourceForUploadComboViewer = new ComboViewer(this.form.getBody(), SWT.READ_ONLY);
    this.sourceForUploadComboViewer.getCombo().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sourceForUploadComboViewer.getCombo().setEnabled(true);
    this.sourceForUploadComboViewer.getCombo().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    new Label(this.form.getBody(), SWT.NONE);
    fillSouceForUploadCombo();
    addSourceForUploadComboSelectionListener();
  }

  /**
   *
   */
  private void fileEncodingSelectionListener() {
    this.fileEncodingComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      this.encoding = this.fileEncodingComboViewer.getCombo().getText();
      String[] encodeStr = this.encoding.split(":");
      this.csvParserObj.setEncoding(encodeStr[1].trim());
      fillSheetNHeadingRowCombo();
    });

    try {
      this.charSets = new CommonParamServiceClient().getParameterValue("FILE_ENCODING_CHARSET").split(";");
      fillFileEncodingComboViewer();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void fillSheetNHeadingRowCombo() {
    String profileName = ImportA2lWpRespFromInputFileDialog.this.sourceForUploadComboViewer.getCombo().getText();
    this.sheetCombo.add(FilenameUtils.getBaseName(ImportA2lWpRespFromInputFileDialog.this.filePathText.getText()));
    this.sheetCombo.setEnabled(false);
    if (CSV.equalsIgnoreCase(this.selFileType) && !CommonUtils.isEmptyString(profileName) &&
        (null != ImportA2lWpRespFromInputFileDialog.this.encoding)) {
      this.sheetCombo
          .setText(FilenameUtils.getBaseName(ImportA2lWpRespFromInputFileDialog.this.filePathText.getText()));
      this.headingRowCombo.setEnabled(true);
      try {
        getFirstColumnContent();
      }
      catch (InvalidInputException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        enableDisableCombos(false);
      }
      fillHeadingRowNumberCombo();
    }
  }

  /**
   *
   */
  private void fillFileEncodingComboViewer() {
    for (String charSet : this.charSets) {
      this.fileEncodingComboViewer.getCombo().add(charSet);
    }
  }

  private void validateUploadOnlyWps() {
    ImportA2lWpRespFromInputFileDialog.this.uploadOnlyWps.setEnabled(true);
    boolean importWpOnlyMode = ImportMode.WORKPACKAGE.getDbType()
        .equals(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getImportMode());
    if (importWpOnlyMode) {
      ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.removeAll();
    }

    ImportA2lWpRespFromInputFileDialog.this.uploadOnlyWps.setSelection(importWpOnlyMode);
    ImportA2lWpRespFromInputFileDialog.this.funcRadio.setEnabled(!importWpOnlyMode);
    ImportA2lWpRespFromInputFileDialog.this.labelRadio.setEnabled(!importWpOnlyMode);
    ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.setEnabled(!importWpOnlyMode);
    ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setEnabled(!importWpOnlyMode);

  }

  /**
   *
   */
  private void validateRadioBtns() {
    if (ImportMode.PARAMETER.getDbType()
        .equals(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getImportMode())) {
      ImportA2lWpRespFromInputFileDialog.this.labelRadio.setSelection(true);
      ImportA2lWpRespFromInputFileDialog.this.funcRadio.setSelection(false);
      ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setText("Parameter Column : ");
      setDefaultHeading(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getLabelColumn(),
          ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo);
      validateInputs();
    }
    else if (ImportMode.FUNCTION.getDbType()
        .equals(ImportA2lWpRespFromInputFileDialog.this.importProfileDetails.getImportMode())) {
      ImportA2lWpRespFromInputFileDialog.this.funcRadio.setSelection(true);
      ImportA2lWpRespFromInputFileDialog.this.labelRadio.setSelection(false);
      ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setText(FUNCTION_COLUMN);
      setDefaultHeading(ExcelClientConstants.FUNCTION_NAME, ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo);
      validateInputs();
    }
  }

  /**
   *
   */
  private void validateCombos() {
    if (!CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.sheetCombo.getText())) {
      Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.sheetComboDec,
          ImportA2lWpRespFromInputFileDialog.this.sheetCombo, true);
    }
    if (!this.uploadOnlyWps.getSelection() &&
        !CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.getText())) {
      Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.fktOrLabelComboDec,
          ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo, true);
    }
    if (!CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.wpColCombo.getText())) {
      Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.wpColComboDec,
          ImportA2lWpRespFromInputFileDialog.this.wpColCombo, true);
    }
    if (!CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.respCombo.getText())) {
      Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.respComboDec,
          ImportA2lWpRespFromInputFileDialog.this.respCombo, true);
    }
    if (!CommonUtils.isEmptyString(ImportA2lWpRespFromInputFileDialog.this.varGrpCombo.getText())) {
      Validator.getInstance().validateNDecorate(ImportA2lWpRespFromInputFileDialog.this.varGrpDec,
          ImportA2lWpRespFromInputFileDialog.this.varGrpCombo, true);
    }
  }

  /**
  *
  */
  private void addSourceForUploadComboSelectionListener() {
    this.sourceForUploadComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      if (this.selIndex != this.sourceForUploadComboViewer.getCombo().getSelectionIndex()) {
        this.funcOrParamLabel.setText("");
        this.sheetCombo.removeAll();
        this.sheetCombo.setEnabled(false);
        clearAllCombos();
        enableDisableCombos(false);
        this.headingRowCombo.removeAll();
        this.headingRowCombo.setEnabled(false);
        this.labelRadio.setSelection(false);
        this.funcRadio.setSelection(false);
        this.filePathText.setText("");
        this.encodeLabel.setVisible(false);
        this.fileEncodingComboViewer.getCombo().setEnabled(false);
        this.fileEncodingComboViewer.getCombo().setVisible(false);
      }
      this.filePathText.setEnabled(true);
      this.excelBrowse.setEnabled(true);
      this.selIndex = this.sourceForUploadComboViewer.getCombo().getSelectionIndex();
      this.selectedProfile =
          (A2lWpImportProfile) this.sourceForUploadComboViewer.getCombo().getData(Integer.toString(this.selIndex));
      resetWpRespPrefixLabel();
      showPrefixForWpResp();
      String fileType =
          ImportA2lWpRespFromInputFileDialog.this.selectedProfile.getProfileDetails().getFileType().getType();
      if (CommonUtils.isNotEmptyString(fileType) && !CommonUtils.isEmptyString(this.filePathText.getText()) &&
          (null != ImportA2lWpRespFromInputFileDialog.this.encoding)) {
        checkForEmptyCombos();
        validateCombos();
      }
      validateInputs();
    });
  }

  /**
   *
   */
  private void resetWpRespPrefixLabel() {
    this.wpPrefixLabel.setText("");
    this.wpPrefixLabel.setVisible(false);
    this.wpPrefixChkBtn.setVisible(false);
    this.wpPrefixChkBtn.setSelection(false);

    this.respPrefixLabel.setText("");
    this.respPrefixLabel.setVisible(false);
    this.respPrefixCheckBtn.setVisible(false);
    this.respPrefixCheckBtn.setSelection(false);
  }

  /**
   *
   */
  private void showPrefixForWpResp() {
    String prefixForWp = this.selectedProfile.getProfileDetails().getPrefixForWp();
    if (!CommonUtils.isEmptyString(prefixForWp)) {
      this.wpPrefixLabel.setText("Add \"" + prefixForWp + "\" as prefix : ");
      this.wpPrefixLabel.setVisible(true);
      this.wpPrefixChkBtn.setVisible(true);
      this.wpPrefixChkBtn.setSelection(true);
    }
    String prefixForResp = this.selectedProfile.getProfileDetails().getPrefixForResp();
    if (!CommonUtils.isEmptyString(prefixForResp)) {
      this.respPrefixLabel.setText("Add \"" + prefixForResp + "\" as prefix : ");
      this.respPrefixLabel.setVisible(true);
      this.respPrefixCheckBtn.setVisible(true);
      this.respPrefixCheckBtn.setSelection(true);
    }
  }


  /**
   *
   */
  private void checkForEmptyCombos() {
    if (!CommonUtils.isEmptyString(this.sheetCombo.getText())) {
      if (!this.uploadOnlyWps.getSelection() && CommonUtils.isEmptyString(this.fktOrLabelCombo.getText())) {
        this.decorators.showReqdDecoration(this.fktOrLabelComboDec, IUtilityConstants.MANDATORY_MSG);
      }
      if (CommonUtils.isEmptyString(this.headingRowCombo.getText())) {
        this.decorators.showReqdDecoration(this.headRowNumComboDec, IUtilityConstants.MANDATORY_MSG);
      }
      if (CommonUtils.isEmptyString(this.wpColCombo.getText())) {
        this.decorators.showReqdDecoration(this.wpColComboDec, IUtilityConstants.MANDATORY_MSG);
      }
      if (CommonUtils.isEmptyString(this.respCombo.getText())) {
        this.decorators.showReqdDecoration(this.respComboDec, IUtilityConstants.MANDATORY_MSG);
      }
      if (CommonUtils.isEmptyString(this.varGrpCombo.getText())) {
        this.decorators.showReqdDecoration(this.varGrpDec, IUtilityConstants.MANDATORY_MSG);
      }
    }
    else {
      if (!this.uploadOnlyWps.getSelection()) {
        this.decorators.showReqdDecoration(this.fktOrLabelComboDec, IUtilityConstants.MANDATORY_MSG);
      }
      this.decorators.showReqdDecoration(this.sheetComboDec, IUtilityConstants.MANDATORY_MSG);
      this.decorators.showReqdDecoration(this.wpColComboDec, IUtilityConstants.MANDATORY_MSG);
      this.decorators.showReqdDecoration(this.respComboDec, IUtilityConstants.MANDATORY_MSG);
      this.decorators.showReqdDecoration(this.varGrpDec, IUtilityConstants.MANDATORY_MSG);
      if (!this.uploadOnlyWps.getSelection()) {
        this.fktOrLabelCombo.setEnabled(false);
        this.funcRadio.setEnabled(false);
        this.labelRadio.setEnabled(false);
      }
      this.wpColCombo.setEnabled(false);
      this.respCombo.setEnabled(false);
      this.varGrpCombo.setEnabled(false);
    }


  }

  /**
   * @param importProfileDetails
   * @throws InvalidInputException
   */
  private void setInputForCombos() throws InvalidInputException {

    getFirstColumnContent();
    getColumnNames();

    ImportA2lWpRespFromInputFileDialog.this.headingSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    ImportA2lWpRespFromInputFileDialog.this.headingSet
        .addAll(ImportA2lWpRespFromInputFileDialog.this.colHeadingMap.keySet());
    enableDisableCombos(true);
    clearAllCombos();
    for (String heading : ImportA2lWpRespFromInputFileDialog.this.headingSet) {
      if (!heading.isEmpty()) {
        if (!this.uploadOnlyWps.getSelection()) {
          ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.add(heading);
        }
        ImportA2lWpRespFromInputFileDialog.this.wpColCombo.add(heading);
        ImportA2lWpRespFromInputFileDialog.this.respCombo.add(heading);
        ImportA2lWpRespFromInputFileDialog.this.respTypeCombo.add(heading);
        if (!this.a2lVarGrps.isEmpty()) {
          if (EXCEL.equalsIgnoreCase(this.selFileType)) {
            ImportA2lWpRespFromInputFileDialog.this.varGrpCombo.add("Excel Column => " + heading);
          }
          else if (CSV.equalsIgnoreCase(this.selFileType)) {
            ImportA2lWpRespFromInputFileDialog.this.varGrpCombo.add("CSV Column => " + heading);
          }
        }
      }
    }
  }

  /**
   *
   */
  private void clearAllCombos() {
    this.fktOrLabelCombo.removeAll();
    this.wpColCombo.removeAll();
    this.respCombo.removeAll();
    this.respTypeCombo.removeAll();
    this.varGrpCombo.removeAll();
  }

  /**
   * @throws InvalidInputException
   */
  private void getFirstColumnContent() throws InvalidInputException {
    if (EXCEL.equalsIgnoreCase(this.selFileType)) {
      this.rowNumWithContentMap =
          ImportA2lWpRespFromInputFileDialog.this.parserObj.getFirstColumnContent(this.sheetCombo.getText());
    }
    else if (CSV.equalsIgnoreCase(this.selFileType)) {
      this.rowNumWithContentMap = ImportA2lWpRespFromInputFileDialog.this.csvParserObj.getRowContentMap();
    }
  }

  /**
   *
   */
  private void fillHeadingRowNumberCombo() {
    this.headingRowCombo.setEnabled(true);
    int index = 0;
    for (Entry<Integer, String> columnOneMapEntry : this.rowNumWithContentMap.entrySet()) {
      String item = columnOneMapEntry.getKey() + " -> " + columnOneMapEntry.getValue();
      this.headingRowCombo.add(item, index);
      this.headingRowCombo.setData(Integer.toString(index), item);
      index++;
    }
  }

  /**
   * @throws InvalidInputException
   */
  private void getColumnNames() throws InvalidInputException {
    if (EXCEL.equalsIgnoreCase(this.selFileType)) {
      this.colHeadingMap = ImportA2lWpRespFromInputFileDialog.this.parserObj.getSheetColNames(this.sheetCombo.getText(),
          getHeadingRowNumOfSelectedFile());
    }
    else if (CSV.equalsIgnoreCase(this.selFileType)) {
      this.colHeadingMap = this.csvParserObj.getHeaders(getHeadingRowNumOfSelectedFile());
    }
  }

  /**
   * @return
   */
  private Long getHeadingRowNumOfSelectedFile() {
    String[] hRowNumComboContent = this.headingRowCombo.getText().split(SEPARATOR);
    return Long.valueOf(hRowNumComboContent[0].trim());
  }

  /**
   *
   */
  private void enableDisableCombos(final boolean flag) {
    boolean uploadParams = !this.uploadOnlyWps.getSelection();
    ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.setEnabled(uploadParams);
    ImportA2lWpRespFromInputFileDialog.this.funcRadio.setEnabled(uploadParams);
    ImportA2lWpRespFromInputFileDialog.this.labelRadio.setEnabled(uploadParams);
    ImportA2lWpRespFromInputFileDialog.this.funcOrParamLabel.setEnabled(uploadParams);

    ImportA2lWpRespFromInputFileDialog.this.fktOrLabelCombo.setEnabled(flag);
    ImportA2lWpRespFromInputFileDialog.this.wpColCombo.setEnabled(flag);
    ImportA2lWpRespFromInputFileDialog.this.respCombo.setEnabled(flag);
    ImportA2lWpRespFromInputFileDialog.this.respTypeCombo.setEnabled(flag);
    ImportA2lWpRespFromInputFileDialog.this.varGrpCombo.setEnabled(flag);
  }

  /**
   *
   */
  private void fillSouceForUploadCombo() {
    this.sourceForUploadComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.sourceForUploadComboViewer.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        String profileName = "";
        if (element instanceof A2lWpImportProfile) {
          A2lWpImportProfile a2lWpImportProfile = (A2lWpImportProfile) element;
          profileName += a2lWpImportProfile.getProfileName();
        }
        return profileName;
      }

    });

    try {
      Map<Long, A2lWpImportProfile> importProfileMap = new A2lWpImportProfileServiceClient().getAll();
      SortedSet<A2lWpImportProfile> a2lWpImportProfileSet = new TreeSet<>();
      a2lWpImportProfileSet.addAll(importProfileMap.values());
      int index = 0;
      for (A2lWpImportProfile a2lWpImportProfile : a2lWpImportProfileSet) {
        this.sourceForUploadComboViewer.getCombo().add(a2lWpImportProfile.getProfileName(), index);
        this.sourceForUploadComboViewer.getCombo().setData(Integer.toString(index), a2lWpImportProfile);
        index++;
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param functionName
   * @param wpColCombo2
   */
  protected void setDefaultHeading(final String functionName, final Combo wpColCombo) {
    for (String heading : ImportA2lWpRespFromInputFileDialog.this.headingSet) {
      if (heading.equalsIgnoreCase(functionName)) {
        wpColCombo.setText(heading);
      }
    }
  }

  /**
   * Add excel files var group to the combo
   */
  private void addExcelVarGrps() {
    this.varGrpCombo.add("A2L File => <DEFAULT>");
    if (this.a2lVarGrps.isEmpty()) {
      this.varGrpCombo.setText("A2L File => <DEFAULT>");
      validateInputs();
    }
    for (A2lVariantGroup vargrp : this.a2lVarGrps) {
      this.varGrpCombo.add("A2L File => " + vargrp.getName());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean isInputFileVarGrp = false;
    int varGrpColNum = -1;
    Long a2lVarGrpId = null;
    String varGrp = this.varGrpCombo.getText();
    if (varGrp.contains("Excel Column")) {
      isInputFileVarGrp = true;
      varGrp = varGrp.replace("Excel Column => ", "").trim();
      varGrpColNum = this.colHeadingMap.get(varGrp);
    }
    else if (varGrp.contains("CSV Column")) {
      isInputFileVarGrp = true;
      varGrp = varGrp.replace("CSV Column => ", "").trim();
      varGrpColNum = this.colHeadingMap.get(varGrp);
    }
    else {
      varGrp = varGrp.replace("A2L File => ", "").trim();
      Collection<A2lVariantGroup> a2lVarGrpCollection =
          this.a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap().values();
      for (A2lVariantGroup selVarGrp : a2lVarGrpCollection) {
        if (selVarGrp.getName().equalsIgnoreCase(varGrp)) {
          this.selVarGrpName = selVarGrp.getName();
          a2lVarGrpId = selVarGrp.getId();
        }
      }
    }
    int funcOrLabelColNum = 0;
    String selSheetName = ImportA2lWpRespFromInputFileDialog.this.sheetCombo.getText();
    if (!this.uploadOnlyWps.getSelection()) {
      this.isFunctionColSelected = this.funcRadio.getSelection();
      this.isLabelColSelected = this.labelRadio.getSelection();
      funcOrLabelColNum = this.colHeadingMap.get(this.fktOrLabelCombo.getText());
    }
    try {
      Integer respTypeColumn;
      respTypeColumn =
          this.respTypeCombo.getText() == null ? null : this.colHeadingMap.get(this.respTypeCombo.getText());
      ImportA2lWpRespInputProfileData profileData = new ImportA2lWpRespInputProfileData();
      profileData.setFuncCol(this.isFunctionColSelected);
      profileData.setLabelCol(this.isLabelColSelected);
      profileData.setWpDefImp(this.uploadOnlyWps.getSelection());
      profileData.setFuncOrLabelCol(funcOrLabelColNum);
      profileData.setWpColNum(this.colHeadingMap.get(this.wpColCombo.getText()));
      profileData.setRespColNum(this.colHeadingMap.get(this.respCombo.getText()));
      profileData.setRespTypeColNum(respTypeColumn);
      profileData.setInputFileVarGrp(isInputFileVarGrp);
      profileData.setVarGrpColNum(varGrpColNum);
      profileData.setA2lVarGrpId(a2lVarGrpId);

      String prefixForWp =
          this.wpPrefixChkBtn.getSelection() ? this.selectedProfile.getProfileDetails().getPrefixForWp() : "";
      String prefixForResp =
          this.respPrefixCheckBtn.getSelection() ? this.selectedProfile.getProfileDetails().getPrefixForResp() : "";
      if (EXCEL.equalsIgnoreCase(this.selFileType)) {
        this.parsedInputFileData =
            this.parserObj.parseExcelSheet(selSheetName, profileData, prefixForResp, getHeadingRowNumOfSelectedFile());
      }
      else if (CSV.equalsIgnoreCase(this.selFileType)) {
        this.parsedInputFileData =
            this.csvParserObj.getImportA2lWpRespData(profileData, prefixForResp, getHeadingRowNumOfSelectedFile());
      }
      this.parsedInputFileData.getA2lWpImportProfileData().setPrefixForWp(prefixForWp);
      this.parsedInputFileData.getA2lWpImportProfileData().setPrefixForResp(prefixForResp);
      this.parserObj.closeWrkBook();
    }
    catch (InvalidInputException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    super.okPressed();
  }

  /**
   * @return
   */
  private void validateInputs() {
    boolean isValid =
        (this.uploadOnlyWps.getSelection() || (!CommonUtils.isEmptyString(this.fktOrLabelCombo.getText()))) &&
            (!CommonUtils.isEmptyString(this.wpColCombo.getText())) &&
            (!CommonUtils.isEmptyString(this.respCombo.getText())) &&
            (!CommonUtils.isEmptyString(this.varGrpCombo.getText())) &&
            (!CommonUtils.isEmptyString(this.filePathText.getText())) &&
            (!CommonUtils.isEmptyString(this.headingRowCombo.getText()));
    this.importBtn.setEnabled(isValid);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.importBtn = createButton(parent, IDialogConstants.OK_ID, "Import", true);
    this.importBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * @return the parsedExcelData
   */
  public ImportA2lWpRespData getParsedExcelData() {
    return this.parsedInputFileData;
  }


  /**
   * @param parsedExcelData the parsedExcelData to set
   */
  public void setParsedExcelData(final ImportA2lWpRespData parsedExcelData) {
    this.parsedInputFileData = parsedExcelData;
  }


  /**
   * @return the selVarGrpName
   */
  public String getSelVarGrpName() {
    return this.selVarGrpName;
  }


  /**
   * @param selVarGrpName the selVarGrpName to set
   */
  public void setSelVarGrpName(final String selVarGrpName) {
    this.selVarGrpName = selVarGrpName;
  }

  /**
   *
   */
  private void parseExcel() {
    ImportA2lWpRespFromInputFileDialog.this.parserObj = new A2LWpRespImportExcelParser(
        ImportA2lWpRespFromInputFileDialog.this.a2lWpInfoBo, ImportA2lWpRespFromInputFileDialog.this.fileSelected);
    try {
      List<String> sheetNameList = ImportA2lWpRespFromInputFileDialog.this.parserObj.getExcelSheetNames();
      for (String sheetName : sheetNameList) {
        ImportA2lWpRespFromInputFileDialog.this.sheetCombo.add(sheetName);
      }
      ImportA2lWpRespFromInputFileDialog.this.sheetCombo.setEnabled(true);
    }
    catch (InvalidInputException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void parseCsv() {
    ImportA2lWpRespFromInputFileDialog.this.csvParserObj =
        new A2lRespImportCSVParser(ImportA2lWpRespFromInputFileDialog.this.a2lWpInfoBo,
            ImportA2lWpRespFromInputFileDialog.this.filePathText.getText(),
            ImportA2lWpRespFromInputFileDialog.this.selectedProfile.getProfileDetails().getFileType().getSeperator());
    try {
      ImportA2lWpRespFromInputFileDialog.this.encoding =
          ImportA2lWpRespFromInputFileDialog.this.csvParserObj.getEncoding();
      boolean isEncodingAvailable = null != ImportA2lWpRespFromInputFileDialog.this.encoding;
      ImportA2lWpRespFromInputFileDialog.this.encodeLabel
          .setVisible(!isEncodingAvailable);
      ImportA2lWpRespFromInputFileDialog.this.fileEncodingComboViewer.getCombo()
          .setVisible(!isEncodingAvailable);
      ImportA2lWpRespFromInputFileDialog.this.fileEncodingComboViewer.getCombo()
          .setEnabled(!isEncodingAvailable);

      if (!isEncodingAvailable) {
        // fill file encoding combo only when encoding is not available
        fillFileEncodingComboViewer();
        CDMLogger.getInstance().warnDialog("Unable to detect file encoding. Please select proper encoding to proceed",
            Activator.PLUGIN_ID);
      }
      else {
        // if encodiing is available, fill sheet name and heading row combo
        fillSheetNHeadingRowCombo();
      }
    }
    catch (InvalidInputException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    ImportA2lWpRespFromInputFileDialog.this.sheetCombo
        .add(FilenameUtils.getBaseName(ImportA2lWpRespFromInputFileDialog.this.filePathText.getText()));
    ImportA2lWpRespFromInputFileDialog.this.sheetCombo.setEnabled(false);
  }

  /**
   * @throws InvalidInputException
   */
  private void setParsedData() throws InvalidInputException {
    String profileName = ImportA2lWpRespFromInputFileDialog.this.sourceForUploadComboViewer.getCombo().getText();
    if ((EXCEL.equalsIgnoreCase(this.selFileType) && !CommonUtils.isEmptyString(profileName)) ||
        (CSV.equalsIgnoreCase(this.selFileType) && !CommonUtils.isEmptyString(profileName) &&
            (null != ImportA2lWpRespFromInputFileDialog.this.encoding))) {
      if (EXCEL.equalsIgnoreCase(this.selFileType)) {
        List<String> sheetNameList = ImportA2lWpRespFromInputFileDialog.this.parserObj.getExcelSheetNames();
        this.importProfileDetails = this.selectedProfile.getProfileDetails();
        if (sheetNameList.contains(this.importProfileDetails.getSheetName())) {
          this.sheetCombo.setText(this.importProfileDetails.getSheetName());
          this.headingRowCombo.setEnabled(true);
          getFirstColumnContent();
          fillHeadingRowNumberCombo();
        }
      }
      checkForEmptyCombos();
      validateCombos();
    }
  }

}
