/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.io.File;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.labfunwriter.LabFunWriter;
import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.calcomp.labfunwriter.exception.LabFunWriterException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Dialog to export selected labels as LAB/FUn file
 *
 * @author dja7cob
 */
public class LabFunExportDialog extends AbstractDialog {

  /**
   *
   */
  private static final String EXTN_SEPARATOR = ".";

  /**
  *
  */
  private static final String LAB_FILE_NAME = "Parameters";

  /**
  *
  */
  private static final String FUN_FILE_NAME = "Functions";

  /**
  *
  */
  private final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);

  /**
   * Top composite
   */
  private Composite top;

  /**
   * Composite instance for the dialog
   */
  private Composite composite;

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
   *
   */
  private OUTPUT_FILE_TYPE exportFileType;

  /**
   *
   */
  private String exportPath;

  /**
   *
   */
  private String exportDir = CommonUtils.getSystemUserTempDirPath();

  /**
   *
   */
  private String exportFileName;

  /**
   *
   */
  private Text exportPathText;

  /**
  *
  */
  private Button autoOpenDirCheckbox;

  /**
   *
   */
  private final Set<String> labels;

  /**
   *
   */
  private final Set<String> functions;

  /**
   *
   */
  private final Set<String> groups;

  /**
   * @param parentShell parent shell
   * @param labels set of labels
   * @param functions set of functions
   * @param groups set of groups
   * @param outputFileType output file type
   */
  public LabFunExportDialog(final Shell parentShell, final Set<String> labels, final Set<String> functions,
      final Set<String> groups, final OUTPUT_FILE_TYPE outputFileType) {
    super(parentShell);
    this.labels = labels;
    this.functions = functions;
    this.groups = groups;
    this.exportFileType = outputFileType;
    this.exportFileName = outputFileType == OUTPUT_FILE_TYPE.LAB ? LAB_FILE_NAME : FUN_FILE_NAME;
    this.exportPath = getExportPath();
  }

  /**
   * @return
   */
  private String getExportPath() {
    return this.exportDir + this.exportFileName + EXTN_SEPARATOR + this.exportFileType.getFileExtension();
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

    if (getExportFileType() == OUTPUT_FILE_TYPE.LAB) {
      setTitle("Export parameters");
      setMessage("Exports selected parameters as LAB file");
    }
    else {
      setTitle("Export functions");
      setMessage("Exports selected functions as FUN file");
    }

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;
  }

  /**
   * Initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
  }

  /**
   * Initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Export details");
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * Initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    createLabels(this.form.getBody(), 1, "Export Path");
    createPathControls(this.form.getBody());
    createAutoOpenDirButton(this.form.getBody());
  }

  /**
   * @param formComposite
   */
  private void createAutoOpenDirButton(final Composite formComposite) {
    createLabels(formComposite, 3, CommonUIConstants.EMPTY_STRING);

    this.autoOpenDirCheckbox = new Button(formComposite, SWT.CHECK);
    this.autoOpenDirCheckbox.setLayoutData(new GridData());
    this.autoOpenDirCheckbox.setText("Open export directory");
    this.autoOpenDirCheckbox.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // NA
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA

      }
    });
    this.autoOpenDirCheckbox.setSelection(true);
  }

  /**
   * @param formComposite
   */
  private void createLabels(final Composite formComposite, final int count, final String labelText) {
    for (int i = 0; i < count; i++) {
      this.formToolkit.createLabel(formComposite, labelText);
    }
  }

  /**
   * @param formComposite
   */
  private void createPathControls(final Composite formComposite) {
    this.exportPathText = getFormToolkit().createText(formComposite, null, SWT.SINGLE | SWT.BORDER);

    this.exportPathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.exportPathText.setEditable(false);
    this.exportPathText.setText(this.exportPath);

    final Button browseBtn = getFormToolkit().createButton(formComposite, "", SWT.PUSH);

    browseBtn.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {

        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        fileDialog.setFilterPath(LabFunExportDialog.this.exportDir);
        fileDialog.setOverwrite(true);
        fileDialog.setFilterExtensions(getExtensionBasedOnFileType());
        fileDialog.setFileName(LabFunExportDialog.this.exportFileName);

        if (null != fileDialog.open()) {
          LabFunExportDialog.this.exportDir = fileDialog.getFilterPath();
          LabFunExportDialog.this.exportFileName = FilenameUtils.getBaseName(fileDialog.getFileName());
          LabFunExportDialog.this.exportPath =
              LabFunExportDialog.this.exportDir + File.separator + fileDialog.getFileName();
          LabFunExportDialog.this.exportPathText.setText(LabFunExportDialog.this.exportPath);
        }
      }
    });
    browseBtn.setImage(this.browseImage);
    browseBtn.setToolTipText("Select export filepath");
  }

  /**
   * @return Extension
   */
  protected String[] getExtensionBasedOnFileType() {
    return new String[] { "*." + this.exportFileType.getFileExtension() };
  }

  /**
   * validates link & eng desc field
   *
   * @return
   */
  private boolean validateFields() {
    return (null != this.exportPath);
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
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    Button exportBtn = createButton(parent, IDialogConstants.OK_ID, "Export", true);
    exportBtn.setEnabled(validateFields());
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    LabFunWriter labFunWriter = new LabFunWriter(CDMLogger.getInstance());
    if (CommonUtils.isNotEmpty(this.labels)) {
      labFunWriter.setLabels(this.labels);
    }
    if (CommonUtils.isNotEmpty(this.functions)) {
      labFunWriter.setFunctions(this.functions);
    }
    if (CommonUtils.isNotEmpty(this.groups)) {
      labFunWriter.setGroups(this.groups);
    }
    labFunWriter.setOutputDir(this.exportDir);
    labFunWriter.setFileType(this.exportFileType);
    labFunWriter.setOutputFileName(this.exportFileName);

    String exportFilePath;
    try {
      exportFilePath = labFunWriter.writeToFile();
      CDMLogger.getInstance().info(this.exportFileType + " file exported successfully to : " + exportFilePath,
          Activator.PLUGIN_ID);
      if (this.autoOpenDirCheckbox.getSelection() && !CommonUtils.isEmptyString(exportFilePath) &&
          new File(exportFilePath).exists()) {
        CommonUiUtils.openFile(this.exportDir);
      }
    }
    catch (LabFunWriterException e) {
      CDMLogger.getInstance().debug(e.getMessage(), e);
    }
    super.okPressed();
  }

  /**
   * @return the exportFileType
   */
  public OUTPUT_FILE_TYPE getExportFileType() {
    return this.exportFileType;
  }

  /**
   * @param exportFileType the exportFileType to set
   */
  public void setExportFileType(final OUTPUT_FILE_TYPE exportFileType) {
    this.exportFileType = exportFileType;
  }
}
