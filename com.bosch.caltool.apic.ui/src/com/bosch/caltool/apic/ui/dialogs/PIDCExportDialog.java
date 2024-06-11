package com.bosch.caltool.apic.ui.dialogs;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.jobs.ProjectIdCardExportJob;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * Icdm-743 This class provides a dialog for export pidc
 */
public class PIDCExportDialog extends AbstractDialog {

  /**
   * Constant for mentioning the file path of user home
   */
  private static final String USER_HOME_FILE_PATH = "user.home";
  /**
   * Number of columns in the dialog area
   */
  private static final int NO_OF_COLUMNS = 2;
  /**
   * Height hint for PIDC export dialog
   */
  private static final int HEIGHT_HINT_FOR_DIALOG = 330;
  /**
   * Selected Pidc
   */
  private final PidcVersion pidcVersion;

  private FormToolkit formToolkit;
  private Composite composite;
  private Section section;
  private Form form;
  private Button btnAutomaticOpen;
  private Text filePathText;
  private String fileSelect;
  private Composite top;
  private String fileExtn;
  private boolean openAutomatically;
  private boolean filteredFlag;
  private String filePath;


  /**
   * ICDM-485 Boolean to store whether it is internal or external report
   */
  private boolean isExternalFlag;


  /**
   * @param parentShell instance
   * @param pidcVer pidCard version
   */
  public PIDCExportDialog(final Shell parentShell, final PidcVersion pidcVer) {
    super(parentShell);
    this.pidcVersion = pidcVer;
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


    final String title = "Project ID Card Export";
    // Set the title
    setTitle(title);

    final String dialogMessage = "Exports the attributes of a Project ID Card";
    // Set the message
    setMessage(dialogMessage, IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Project ID Card Export");
  }

  /**
   * Creates the Dialog Area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = HEIGHT_HINT_FOR_DIALOG;
    this.top.setLayoutData(gridData);
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
   *
   * @param parent
   */
  private void createComposite() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(gridLayout);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection();
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = getFormToolkit().createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setLayout(new GridLayout());
    this.section.setExpanded(true);
    this.section.setText("Export Options");
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes Form
   */

  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    createDialogArea();
  }

  /**
   * This method initializes Password Area
   */
  private void createDialogArea() {

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COLUMNS;


    // ICDM-485
    createIntExtRadioBtns(gridLayout);


    createFilteredCompleteRadioBtns(gridLayout);

    final Group exportFileGrp = new Group(this.form.getBody(), SWT.NONE);
    exportFileGrp.setText("Select the export file options");

    exportFileGrp.setLayout(new GridLayout());
    exportFileGrp.setLayoutData(GridDataUtil.getInstance().getGridData());

    Group grp = new Group(exportFileGrp, SWT.NONE);
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 3;
    grp.setLayout(gridLayout1);
    grp.setLayoutData(GridDataUtil.getInstance().getGridData());
    Label exportToLbl = new Label(grp, SWT.NONE);
    exportToLbl.setText("Export as : ");
    this.filePathText = new Text(grp, SWT.BORDER);
    this.filePathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.filePathText.addModifyListener(event -> {
      PIDCExportDialog.this.filePath = PIDCExportDialog.this.filePathText.getText().replace(getNameAlone(), "");
      PIDCExportDialog.this.fileSelect = getNameAlone();
    });

    Button btnBrowse = new Button(grp, SWT.PUSH);
    btnBrowse.setText("Browse");
    btnBrowse.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        setExportFilePath();
      }
    });
    setButtonLayoutData(btnBrowse);
    this.btnAutomaticOpen = new Button(exportFileGrp, SWT.CHECK);
    this.btnAutomaticOpen.setText("Open automatically?");
    this.btnAutomaticOpen.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        PIDCExportDialog.this.openAutomatically = PIDCExportDialog.this.btnAutomaticOpen.getSelection();
      }
    });
  }

  /**
   *
   */
  private void setExportFilePath() {
    PIDCExportDialog.this.fileSelect = getNameAlone();
    String fileName = PIDCExportDialog.this.fileSelect;
    fileName = FileNameUtil.formatFileName(fileName, ApicConstants.INVALID_CHAR_PTRN);
    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
    fileDialog.setText("Save Excel Report to :");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXTNS);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    fileDialog.setOverwrite(true);
    fileDialog.setFilterPath(System.getProperty(USER_HOME_FILE_PATH));
    PIDCExportDialog.this.filePath = fileDialog.open();
    if (null != PIDCExportDialog.this.filePath) {
      PIDCExportDialog.this.filePathText.setText(PIDCExportDialog.this.filePath);
    }
    if (PIDCExportDialog.this.fileSelect != null) {
      PIDCExportDialog.this.fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
    }
  }

  /**
   * @param gridLayout
   */
  private void createFilteredCompleteRadioBtns(final GridLayout gridLayout) {
    final Group buttGroup = new Group(this.form.getBody(), SWT.NONE);
    buttGroup.setText("Select the range of attributes to be exported");

    buttGroup.setLayout(gridLayout);
    buttGroup.setLayoutData(GridDataUtil.getInstance().getGridData());
    Button btnComplete = new Button(buttGroup, SWT.RADIO);
    btnComplete.setText("All attributes");
    btnComplete.setSelection(true);

    Button btnFiltered = new Button(buttGroup, SWT.RADIO);
    btnFiltered.setText("Filtered attributes");

    btnComplete.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        generateFilePath(PIDCExportDialog.this.isExternalFlag, false);
      }

    });
    btnFiltered.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        generateFilePath(PIDCExportDialog.this.isExternalFlag, true);
      }
    });

    btnFiltered.setEnabled(isEditorOpen());

  }

  /**
   * @param gridLayout
   */
  private void createIntExtRadioBtns(final GridLayout gridLayout) {
    final Group intAttrBtnGroup = new Group(this.form.getBody(), SWT.NONE);
    intAttrBtnGroup.setText("Select the type of report");

    intAttrBtnGroup.setLayout(gridLayout);
    intAttrBtnGroup.setLayoutData(GridDataUtil.getInstance().getGridData());

    Button btnInternalAttr = new Button(intAttrBtnGroup, SWT.RADIO);
    btnInternalAttr.setText("Internal report");
    Button btnExternalAttr = new Button(intAttrBtnGroup, SWT.RADIO);
    btnExternalAttr.setText("External report");

    btnInternalAttr.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        generateFilePath(false, PIDCExportDialog.this.filteredFlag);
      }
    });

    btnExternalAttr.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        generateFilePath(true, PIDCExportDialog.this.filteredFlag);
      }
    });
  }

  /**
   * This method genenrates the file path of PIDC Export excel
   *
   * @param isExternal true for external report
   * @param isFiltered true for report with filtered attributes
   */
  private void generateFilePath(final boolean isExternal, final boolean isFiltered) {

    String exportType = "Internal";
    String attrType = "Complete";
    if (isExternal) {
      exportType = "External";
    }
    if (isFiltered) {
      attrType = "Filtered";
    }
    String pidcVersionName = PIDCExportDialog.this.pidcVersion.getName();
    pidcVersionName = pidcVersionName.replace('\\', '_');
    PIDCExportDialog.this.fileSelect = pidcVersionName + "_" + exportType + "_" + attrType;
    if (null == PIDCExportDialog.this.filePath) {
      PIDCExportDialog.this.filePathText
          .setText(System.getProperty(USER_HOME_FILE_PATH) + "\\" + PIDCExportDialog.this.fileSelect);
    }
    else {
      PIDCExportDialog.this.filePathText.setText(PIDCExportDialog.this.filePath + PIDCExportDialog.this.fileSelect);
    }
    this.filteredFlag = isFiltered;
    this.isExternalFlag = isExternal;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    if (this.fileSelect != null) {
      // ICDM-169
      // Call for export the PIDC
      if (null == this.fileExtn) {
        this.fileExtn = "xlsx";
      }
      String oldFileName = this.fileSelect;
      this.fileSelect = this.fileSelect.replaceAll("[\\/:*?\"<>|]+", "_");
      if (CommonUtils.isNotEqual(this.fileSelect, oldFileName)) {
        CDMLogger.getInstance().warnDialog(
            "A file name can't contain any of the following characters: \\ / : * ? \" < > |. They will be replaced by an underscore.",
            Activator.PLUGIN_ID);
      }

      String folderPath = this.filePath + this.fileSelect;
      String fileFullPath;
      // ICDM-199
      if (folderPath.contains(".xlsx") || folderPath.contains(".xls")) {
        fileFullPath = folderPath;
      }
      else {
        fileFullPath = folderPath + "." + this.fileExtn;
      }
      try (OutputStream fileOutputStream = new FileOutputStream(fileFullPath)) {
        // Dummy opening, to check whethe file can be created
      }
      catch (FileNotFoundException exp) {
        CDMLogger.getInstance().errorDialog(
            "The File is already open. Please close the file and try re-exporting or select a different folder.", exp,
            Activator.PLUGIN_ID);
        return;
      }
      catch (IOException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        return;
      }

      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);

      ProjectIdCardExportJob job =
          new ProjectIdCardExportJob(new MutexRule(), this.pidcVersion, this.filePath + this.fileSelect, this.fileExtn,
              PIDCExportDialog.this.openAutomatically, this.filteredFlag, this.isExternalFlag);

      job.schedule();

      super.okPressed();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   *
   */
  private String getNameAlone() {
    StringTokenizer fileName = new StringTokenizer(PIDCExportDialog.this.filePathText.getText(), "\\");
    String name = null;
    while (fileName.hasMoreTokens()) {
      name = fileName.nextToken();
    }
    return name;
  }

  /**
   * @return if the selected pidc is opened in editor this method would return true
   */
  public boolean isEditorOpen() {
    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
      for (IWorkbenchPage page : window.getPages()) {
        for (IEditorReference editor : page.getEditorReferences()) {
          if (editor.getEditor(false) instanceof PIDCEditor) {
            IEditorPart editorPart = editor.getEditor(false);
            if (((PIDCEditor) editorPart).getPidcPage().getSelectedPidcVersion().getId()
                .equals(this.pidcVersion.getId())) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }


}