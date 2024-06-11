/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * This is a abstract class used for constructing basic dialog having File path text, Browse button, Open Automatically
 * check box
 *
 * @author svj7cob
 */
public abstract class AbstractExcelExportDialog extends AbstractDialog {

  /**
   * file path text in Dialog
   */
  protected Text filePathText;
  /**
   * flag if checked, then excel opens automatically
   */
  protected boolean openAutomatically;
  /**
   * file extension for excel
   */
  protected String fileExtn;
  /**
   * file path
   */
  protected String filePath;
  /**
   * Constant for mentioning the file path of user home
   */
  protected static String TEMP_FILE_PATH;
  /**
   * Number of columns in the dialog area
   */
  protected static final int NO_OF_COLUMNS = 2;
  /**
   * Height hint for PIDC export dialog
   */
  private static final int HEIGHT_HINT_FOR_DIALOG = 330;
  /**
   * formToolkit
   */
  private FormToolkit formToolkit;
  /**
   * composite
   */
  private Composite composite;
  /**
   * section
   */
  private Section section;
  /**
   * creates form
   */
  protected Form form;
  /**
   * top
   */
  private Composite top;

  /**
   * custom title
   */
  protected String customTitle;
  /**
   * custom title message
   */
  protected String customTitleMessage;
  /**
   * custom shell text
   */
  protected String customShellText;

  /**
   * Constructor
   *
   * @param parentShell parentShell
   */
  protected AbstractExcelExportDialog(final Shell parentShell) {
    super(parentShell);
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
    setTitle(this.customTitle);
    // Set the message
    setMessage(this.customTitleMessage, IMessageProvider.INFORMATION);
    return contents;
  }


  /**
   * configures the shell area
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(this.customShellText);
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
   * create export features such as File Path, Browse button
   */
  protected void createExportFeature() {
    final Group exportFileGrp = new Group(this.form.getBody(), SWT.NONE);
    exportFileGrp.setText("Select the export file options");
    exportFileGrp.setLayout(new GridLayout());
    exportFileGrp.setLayoutData(GridDataUtil.getInstance().getGridData());

    Group grp = new Group(exportFileGrp, SWT.NONE);
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 3;
    grp.setLayout(gridLayout1);
    grp.setLayoutData(GridDataUtil.getInstance().getGridData());

    Label exportLabel = new Label(grp, SWT.NONE);
    exportLabel.setText("Export as : ");
    this.filePathText = new Text(grp, SWT.BORDER);
    this.filePathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    // sets initial file name
    setInitialFileName();

    this.filePathText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        AbstractExcelExportDialog.this.filePath = AbstractExcelExportDialog.this.filePathText.getText();
        AbstractExcelExportDialog.this.fileExtn = FilenameUtils.getExtension(AbstractExcelExportDialog.this.filePath);
      }
    });
    Button browseButton = new Button(grp, SWT.PUSH);
    browseButton.setText("Browse");

    // selection listener for Browse button
    browseButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        String fileName = getFileNameAlone();
        fileName = FileNameUtil.formatFileName(fileName, ApicConstants.INVALID_CHAR_PTRN);
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
        fileDialog.setText("Save Excel Report to :");
        fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
        fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
        fileDialog.setFilterIndex(0);
        fileDialog.setFileName(fileName);
        fileDialog.setOverwrite(true);
        fileDialog.setFilterPath(TEMP_FILE_PATH);
        String fileSel = fileDialog.open();
        if (null != fileSel) {
          AbstractExcelExportDialog.this.filePath = fileSel;
          AbstractExcelExportDialog.this.filePathText.setText(AbstractExcelExportDialog.this.filePath);
          AbstractExcelExportDialog.this.fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
        }
      }
    });
    setButtonLayoutData(browseButton);
    final Button automaticOpen = new Button(exportFileGrp, SWT.CHECK);
    automaticOpen.setSelection(true);
    AbstractExcelExportDialog.this.openAutomatically = true;
    automaticOpen.setText("Open automatically?");

    // selection listener for automatic open check box
    automaticOpen.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AbstractExcelExportDialog.this.openAutomatically = automaticOpen.getSelection();
      }
    });
  }

  /**
   * get Name of the file path
   *
   * @return name
   */
  private String getFileNameAlone() {
    // gets file name and its extension
    return FilenameUtils.getName(AbstractExcelExportDialog.this.filePathText.getText());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * @return if the selected pidc is opened in editor this method would return true
   */
  public boolean isEditorOpen() {
    return true;
  }

  /**
   * creates Dialog area
   */
  protected abstract void createDialogArea();

  /**
   * set initial file name
   */
  protected abstract void setInitialFileName();

}
