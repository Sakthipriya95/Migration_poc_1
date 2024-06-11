package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.cdr.MonicaFileData;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.monicareportparser.MonitoringToolParser;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * This class provides UI to add value
 */
public class MonicaFileSelDialog extends AbstractDialog {


  /**
   *
   */
  private static final String DUPLICATE_ENTRY_MSG =
      "Duplicate entry not allowed in MoniCa Review, So it is not added for MoniCa Review.";
  /**
   * Button instance for save
   */
  protected Button saveBtn;
  /**
   * Button instance for cancel
   */
  Button cancelBtn;
  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * top comp
   */
  private Composite top;

  /**
   * Text for adding comment when a value is edited
   */
  protected Text gerName;
  /**
   * eng name txt
   */
  private Text excelFile;

  private Text dcmText;

  private Section section;

  /**
   * constant for create questionnarie
   */
  public static final String CREATE_QUESTIONARE = "Select MoniCa Report File";


  /**
   * constant for create new questionnarie
   */
  private static final String NEW_QUESTIONARE = "Select MoniCa Report File";


  /**
   * constant for file extensions
   */
  private static final String[] filExtensions = new String[] { "*.xlsx", "*.xls", };

  /**
   * constant for file names
   */
  private static final String[] fileNames = new String[] { "xlsx(*.xlsx)", "xls(*.xls)", };

  /**
   * Form instance
   */
  private Form excelForm;
  /**
   * browse button
   */
  private Button excelBrowse;
  /**
   * sheet List
   */
  private List sheetList;

  /**
   * selected sheet
   */
  private String[] selectedSheet;
  /**
   * selected file name
   */
  private String monicaFilePath;

  private boolean isDeltaReview;

  private String selSheetName;
  private Section dcmFileSection;
  private Form dcmFileForm;
  protected String dcmFilePath;
  private final MonicaReviewDialog monicaReviewDialog;
  private final boolean editModeFlag;
  private final java.util.List<String> allMonicaSheets = new ArrayList<>();

  private DropFileListener dcmFileDropFileListener;

  private DropFileListener moniCaFileDropFileListener;

  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.monicaFilePath;
  }


  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param dialog instance of MonicaReviewDialog
   * @param editModeFlag editModeFlag
   */
  // ICDM-108
  public MonicaFileSelDialog(final Shell parentShell, final MonicaReviewDialog dialog, final boolean editModeFlag) {
    super(parentShell);
    this.monicaReviewDialog = dialog;
    this.editModeFlag = editModeFlag;
  }

  /**
   * @param parentShell
   * @param isDeltaReview
   * @param monicaFileName
   */
  public MonicaFileSelDialog(final Shell parentShell, final boolean isDeltaReview, final String monicaFileName,
      final String selSheetName, final MonicaReviewDialog dialog) {
    super(parentShell);
    this.isDeltaReview = isDeltaReview;
    this.monicaFilePath = monicaFileName;
    this.selSheetName = selSheetName;
    this.monicaReviewDialog = dialog;
    this.editModeFlag = false;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);

    // Set the title
    setTitle(CREATE_QUESTIONARE);

    // Set the message
    setMessage(CREATE_QUESTIONARE);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    // set the text
    newShell.setText(NEW_QUESTIONARE);
    // ICDM-153
    super.setHelpAvailable(true);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    // ICDM-112
    this.saveBtn.setEnabled(false);
    if (this.isDeltaReview && ((this.monicaFilePath != null) && !this.monicaFilePath.isEmpty())) {
      this.excelFile.setText(this.monicaFilePath);
      fillSheetsList();
    }
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
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
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create dcm file selection section
    createDCMFileSelSection();
    // create Ques section
    createQuesSection();
    this.composite.setLayoutData(gridData);
    loadDetails();
  }


  /**
   *
   */
  private void loadDetails() {
    IStructuredSelection selection = (IStructuredSelection) this.monicaReviewDialog.getTabViewer().getSelection();
    if (!selection.isEmpty() && this.editModeFlag) {
      MonicaFileData dataProvider = (MonicaFileData) selection.getFirstElement();
      if (null != dataProvider) {
        this.dcmText.setText(dataProvider.getDcmFileName() != null ? dataProvider.getDcmFileName() : "");
        this.excelFile.setText(dataProvider.getMonicaFileName());
        int count = 0;
        int selIndex = 0;
        for (String monicaSheet : dataProvider.getAllMonicaSheets()) {
          this.sheetList.add(monicaSheet);
          if (monicaSheet.equalsIgnoreCase(dataProvider.getSheetName())) {
            selIndex = count;
          }
          count++;
        }
        this.sheetList.setSelection(selIndex);
      }
    }
  }


  /**
   *
   */
  private void createDCMFileSelSection() {
    this.dcmFileSection = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Select the DCM file");
    createDcmFileForm();
    this.dcmFileSection.setClient(this.dcmFileForm);
    this.dcmFileSection.getDescriptionControl().setEnabled(false);
  }

  /**
   *
   */
  private void createDcmFileForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;

    this.dcmFileForm = getFormToolkit().createForm(this.dcmFileSection);
    this.dcmFileForm.getBody().setLayout(gridLayout);
    getFormToolkit().createLabel(this.dcmFileForm.getBody(), "DCM File ");
    this.dcmText = getFormToolkit().createText(this.dcmFileForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    GridData commentGridData = getTextFieldGridData();
    this.dcmText.setLayoutData(commentGridData);
    this.dcmText.setEditable(false);
    this.dcmText.addModifyListener(createdcmTextModifyListener());
    // Adding Drop Listener for DCM textbox
    this.dcmFileDropFileListener = new DropFileListener(this.dcmText, new String[] { "*.dcm" });
    this.dcmFileDropFileListener.addDropFileListener(true);
    this.dcmFileDropFileListener.setEditable(true);

    Button dcmBrowse = new Button(this.dcmFileForm.getBody(), SWT.PUSH);
    final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    dcmBrowse.setImage(browseImage);
    dcmBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    dcmBrowse.addSelectionListener(createdcmFileSelListener());
    dcmBrowse.setEnabled(true);
    dcmBrowse.setToolTipText("Select DCM file");
  }

  private ModifyListener createdcmTextModifyListener() {
    return new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent arg0) {
        if ((MonicaFileSelDialog.this.dcmFileDropFileListener.getDropFilePath() != null) &&
            !MonicaFileSelDialog.this.dcmText.getText().isEmpty()) {
          MonicaFileSelDialog.this.dcmFilePath = MonicaFileSelDialog.this.dcmFileDropFileListener.getDropFilePath();
          enableDisableSave();
        }
      }
    };
  }


  /**
   * @return
   */
  private SelectionListener createdcmFileSelListener() {

    return new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        MonicaFileSelDialog.this.dcmFileDropFileListener.setDropFilePath(null);
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Upload DCM File");
        fileDialog.setFilterExtensions(new String[] { "*.dcm" });
        MonicaFileSelDialog.this.dcmFilePath = fileDialog.open();
        String dcmPath = MonicaFileSelDialog.this.dcmFilePath;
        if (dcmPath != null) {
          MonicaFileSelDialog.this.dcmText.setText(FilenameUtils.getName(dcmPath));
          if (!MonicaFileSelDialog.this.dcmText.getText().isEmpty()) {
            enableDisableSave();
          }
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {}
    };
  }

  /**
   *
   */
  private void enableDisableSave() {
    IStructuredSelection selection = (IStructuredSelection) this.monicaReviewDialog.getTabViewer().getSelection();
    if (!selection.isEmpty() && this.editModeFlag) {
      MonicaFileData dataProvider = (MonicaFileData) selection.getFirstElement();
      if (((dataProvider.getDcmFileName() != null) && dataProvider.getDcmFileName().equals(this.dcmText.getText())) &&
          dataProvider.getMonicaFileName().equals(this.excelFile.getText()) &&
          dataProvider.getSheetName().equals(this.sheetList.getSelection()[0])) {
        this.saveBtn.setEnabled(false);
      }
      else {
        this.saveBtn.setEnabled(true);
      }
    }
    else if (!this.dcmText.getText().isEmpty() && !this.excelFile.getText().isEmpty() &&
        (this.sheetList.getSelection().length != 0)) {
      this.saveBtn.setEnabled(true);
    }
    else {
      this.saveBtn.setEnabled(false);
    }
  }


  /**
   * This method initializes section
   */
  private void createQuesSection() {
    // section for Questuionnare detail
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Select the excel sheet for the review");
    createForm();
    this.section.setClient(this.excelForm);
    // iCDM-183
    this.section.getDescriptionControl().setEnabled(false);
  }


  /**
   * This method initializes form
   */
  private void createForm() {

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.excelForm = getFormToolkit().createForm(this.section);
    this.excelForm.getBody().setLayout(gridLayout);
    getFormToolkit().createLabel(this.excelForm.getBody(), "File name");
    this.excelFile = getFormToolkit().createText(this.excelForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    GridData commentGridData = getTextFieldGridData();
    this.excelFile.setLayoutData(commentGridData);
    this.excelFile.setEditable(false);
    // Adding Drop Listener for MoniCa textbox
    this.moniCaFileDropFileListener = new DropFileListener(this.excelFile, filExtensions);
    this.moniCaFileDropFileListener.addDropFileListener(true);
    this.moniCaFileDropFileListener.setEditable(true);

    // create browse button
    this.excelBrowse = new Button(this.excelForm.getBody(), SWT.PUSH);
    final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    this.excelBrowse.setImage(browseImage);
    this.excelBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    this.excelBrowse.setToolTipText("Select MoniCa File");
    getFormToolkit().createLabel(this.excelForm.getBody(), "Excel sheet Names");
    // create sheet list
    if (this.editModeFlag) {
      this.sheetList = new List(this.excelForm.getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
    }
    else {
      this.sheetList = new List(this.excelForm.getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    }
    this.sheetList.setLayoutData(GridDataUtil.getInstance().getGridData());
    addListeners();

  }

  /**
   * add the listeners for excel
   */
  private void addListeners() {

    this.excelBrowse.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        fillSheetNames();
      }

    });

    this.sheetList.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        MonicaFileSelDialog.this.setSelectedSheet(MonicaFileSelDialog.this.sheetList.getSelection());
        if (!"".equals(MonicaFileSelDialog.this.dcmText.getText().trim())) {
          enableDisableSave();
        }
      }


    });


    this.sheetList.addMouseListener(new MouseAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void mouseDoubleClick(final MouseEvent mouseevent) {
        okPressed();
      }
    });

    this.excelFile.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {

        if (CommonUtils.isEmptyString(MonicaFileSelDialog.this.excelFile.getText())) {
          MonicaFileSelDialog.this.saveBtn.setEnabled(false);
        }
        else if (MonicaFileSelDialog.this.moniCaFileDropFileListener.getDropFilePath() != null) {
          MonicaFileSelDialog.this.monicaFilePath =
              MonicaFileSelDialog.this.moniCaFileDropFileListener.getDropFilePath();
          MonicaFileSelDialog.this.sheetList.removeAll();
          MonicaFileSelDialog.this.allMonicaSheets.removeAll(MonicaFileSelDialog.this.allMonicaSheets);
          final File selectedFile = new File(MonicaFileSelDialog.this.monicaFilePath);
          if (selectedFile.exists()) {
            fillSheetsList();
          }
        }
      }
    });
  }

  /**
   * @return the text field grid data
   */
  private GridData getTextFieldGridData() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessVerticalSpace = true;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean duplicateDataFound = false;

    if (!this.editModeFlag) {
      this.selectedSheet = this.sheetList.getSelection();
      String varName = "";
      if (this.sheetList.getSelectionCount() > 0) {
        int[] selectionIndices = this.sheetList.getSelectionIndices();
        for (int selectedIndex : selectionIndices) {
          MonicaFileData dataProvider = new MonicaFileData(this.dcmFilePath, FilenameUtils.getName(this.dcmFilePath),
              this.monicaFilePath, FilenameUtils.getName(this.monicaFilePath), this.sheetList.getItem(selectedIndex),
              varName, this.allMonicaSheets);
          dataProvider.setIndex(this.monicaReviewDialog.getMonicaFileDataSet().size() + 1);
          if (!this.monicaReviewDialog.getMonicaFileDataSet().add(dataProvider)) {
            duplicateDataFound = true;
          }
          this.monicaReviewDialog.getTabViewer().setInput(this.monicaReviewDialog.getMonicaFileDataSet());
          this.monicaReviewDialog.getSaveBtn().setEnabled(false);
        }
      }
    }
    else {
      IStructuredSelection selection = (IStructuredSelection) this.monicaReviewDialog.getTabViewer().getSelection();
      if (!selection.isEmpty()) {
        MonicaFileData dataProvider = new MonicaFileData((MonicaFileData) selection.getFirstElement());
        dataProvider.setDcmFileName(this.dcmText.getText());
        if (this.dcmFilePath != null) {
          dataProvider.setDcmFIlePath(this.dcmFilePath);
        }
        if (this.monicaFilePath != null) {
          dataProvider.setMonicaFilePath(this.monicaFilePath);
        }
        dataProvider.setMonicaFileName(this.excelFile.getText());
        dataProvider.setSheetName(this.sheetList.getSelection()[0]);
        if (this.monicaReviewDialog.checkForDuplicateEntry(dataProvider)) {
          CDMLogger.getInstance().warnDialog(DUPLICATE_ENTRY_MSG, Activator.PLUGIN_ID);
          return;
        }
        if (this.monicaReviewDialog.getMonicaFileDataSet().contains(selection.getFirstElement())) {
          this.monicaReviewDialog.getMonicaFileDataSet().remove(selection.getFirstElement());
        }
        this.monicaReviewDialog.getMonicaFileDataSet().add(dataProvider);
      }
    }
    if (duplicateDataFound) {
      CDMLogger.getInstance().warnDialog(DUPLICATE_ENTRY_MSG, Activator.PLUGIN_ID);
      return;
    }
    this.monicaReviewDialog.getTabViewer().refresh();
    super.okPressed();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * fill the sheet names
   */
  private void fillSheetNames() {
    MonicaFileSelDialog.this.moniCaFileDropFileListener.setDropFilePath(null);
    this.sheetList.removeAll();
    this.allMonicaSheets.removeAll(this.allMonicaSheets);
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    fileDialog.setText("Select the MoniCa report file");
    fileDialog.setFilterExtensions(MonicaFileSelDialog.filExtensions);
    fileDialog.setFilterNames(MonicaFileSelDialog.fileNames);
    final String fileSelected = fileDialog.open();
    // store preferences
    if (fileSelected == null) {
      return;
    }
    // set the text
    MonicaFileSelDialog.this.excelFile.setText(FilenameUtils.getName(fileSelected));
    this.monicaFilePath = fileSelected;
    final File selectedFile = new File(this.monicaFilePath);
    if (selectedFile.exists()) {
      fillSheetsList();
    }
  }

  /**
   * fill the sheet lists
   */
  private void fillSheetsList() {
    MonitoringToolParser parser = new MonitoringToolParser(ParserLogger.getInstance());
    java.util.List<String> allSheetNames = parser.getAllSheetNames(this.monicaFilePath);
    // check for the correct sheet.
    for (String sheetName : allSheetNames) {
      if (sheetName.endsWith("iCDM_Check")) {
        this.sheetList.add(sheetName);
        this.allMonicaSheets.add(sheetName);
      }
      this.saveBtn.setEnabled(false);
    }
    // no sheets available
    if (this.sheetList.getItemCount() == 0) {
      CDMLogger.getInstance().errorDialog("The Excel sheet is not valid for review.Please select a valid file",
          Activator.PLUGIN_ID);
    }
    // Default Selection if only one sheet is available
    else if (this.sheetList.getItemCount() == 1) {
      this.sheetList.setSelection(0);
      if (!"".equals(this.dcmText.getText().trim())) {
        enableDisableSave();
      }
    }
    else if (this.isDeltaReview && (this.selSheetName != null) &&
        this.selSheetName.endsWith(CDRConstants.MONICA_SHEETNAME_POSTFIX_VAL)) {
      String[] sheetName = { this.selSheetName };
      this.sheetList.setSelection(sheetName);
      MonicaFileSelDialog.this.saveBtn.setEnabled(true);
    }
  }


  /**
   * @return the sheetList
   */
  public List getSheetList() {
    return this.sheetList;
  }


  /**
   * @param sheetList the sheetList to set
   */
  public void setSheetList(final List sheetList) {
    this.sheetList = sheetList;
  }


  /**
   * @return the filenames
   */
  public static String[] getFilenames() {
    return fileNames;
  }


  /**
   * @return the selectedSheet
   */
  public String[] getSelectedSheet() {
    return this.selectedSheet;
  }

  /**
   * @param selectedSheet the selectedSheet to set
   */
  public void setSelectedSheet(final String[] selectedSheet) {
    this.selectedSheet = selectedSheet;
  }


  /**
   * @return the selSheetName
   */
  public String getSelSheetName() {
    return this.selSheetName;
  }


  /**
   * @param selSheetName the selSheetName to set
   */
  public void setSelSheetName(final String selSheetName) {
    this.selSheetName = selSheetName;
  }


  /**
   * @return the editModeFlag
   */
  public boolean isEditModeFlag() {
    return this.editModeFlag;
  }


}
