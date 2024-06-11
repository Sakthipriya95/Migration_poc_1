/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.admin.NodeAccessOutput;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author say8cob
 */
public class NodeImportDialog extends TitleAreaDialog {

  /**
   * Diallog title
   */
  private static final String DIALOG_TITLE = "Import Node(s) from file";

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
   * constant for file extensions
   */
  private static final String[] SUPPORTED_FILE_TYPE_EXTNS = new String[] { "*.fun", "*.lab", "*.lab;*.fun" };

  /**
   * constant for file names
   */
  private static final String[] SUPPORTED_FILE_TYPE_NAMES =
      new String[] { "FUN files (*.fun)", "LAB files (*.lab)", "LAB/FUN files (*.lab,*.fun)" };

  private Map<Long, NodeAccessInfo> nodeAccessInfoMap = new HashMap<>();
  /**
   * Get the eclipse preference store
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /**
   * Form toolkit
   */
  private FormToolkit formToolkit;

  private Text txtImportFileName;

  /**
   * Add new user button instance
   */
  private Button okBtn;

  /**
   * @param parentShell parent shell
   */
  public NodeImportDialog(final Shell parentShell) {
    super(parentShell);
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
   * @return FormToolkit
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
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set title
    setTitle(DIALOG_TITLE);

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
    createAddFilesTextField(mainComposite);

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
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "OK", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
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

  private void enableOkButton() {
    this.okBtn.setEnabled(CommonUtils.isNotEmptyString(this.txtImportFileName.getText()));
  }

  /**
   * Create Execution Id text field
   *
   * @param comp parent composite
   */
  private void createAddFilesTextField(final Composite comp) {
    createLabelControl(comp, "Selected File ");
    this.txtImportFileName = createTextField(comp);
    this.txtImportFileName.setEnabled(true);
    this.txtImportFileName.setEditable(true);


    ControlDecoration decorator = new ControlDecoration(this.txtImportFileName, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();

    this.txtImportFileName.addModifyListener(e -> {
      decorator.hide();
      enableOkButton();
    });

    Button browseBtn = new Button(comp, SWT.PUSH);
    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    browseBtn.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

    browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        createFunLabelDialog();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // No Implementation
      }
    });

  }

  /**
   * create the file dialog to select the lab file dialog for the Lab fun file selection
   */
  protected void createFunLabelDialog() {
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    fileDialog.setText("Select the file");
    // icdm=1131
    CommonUtils.swapArrayElement(SUPPORTED_FILE_TYPE_EXTNS,
        getPreference().getString(CommonUtils.IMPORT_LAB_FUN_FILES_EXTN), 0);

    CommonUtils.swapArrayElement(SUPPORTED_FILE_TYPE_NAMES,
        getPreference().getString(CommonUtils.IMPORT_LAB_FUN_FILES_NAME), 0);
    fileDialog.setFilterExtensions(SUPPORTED_FILE_TYPE_EXTNS);
    fileDialog.setFilterNames(SUPPORTED_FILE_TYPE_NAMES);
    final String fileSelected = fileDialog.open();
    // store preferences
    getPreference().setValue(CommonUtils.IMPORT_LAB_FUN_FILES_EXTN,
        SUPPORTED_FILE_TYPE_EXTNS[fileDialog.getFilterIndex()]);

    getPreference().setValue(CommonUtils.IMPORT_LAB_FUN_FILES_NAME,
        SUPPORTED_FILE_TYPE_NAMES[fileDialog.getFilterIndex()]);
    if (fileSelected == null) {
      return;
    }
    this.txtImportFileName.setText(fileSelected);

  }

  @Override
  protected void okPressed() {
    try {
      NodeAccessOutput nodeAccessOutput =
          new NodeAccessServiceClient().findNodeAccessForFunFile(this.txtImportFileName.getText());
      if (CommonUtils.isNotEmpty(nodeAccessOutput.getMissingNodes())) {
        StringBuilder infoMsg = new StringBuilder();
        infoMsg.append("Node not found for the below input(s) in selected file :\n");
        nodeAccessOutput.getMissingNodes().forEach(nodes -> {
          infoMsg.append(nodes);
          infoMsg.append("\n");
        });
        CDMLogger.getInstance().infoDialog(infoMsg.toString(), Activator.PLUGIN_ID);
      }
      setNodeAccessInfoMap(nodeAccessOutput.getNodeAccessInfoMap());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    super.okPressed();
  }

  /**
   * @return the preference
   */
  private IPreferenceStore getPreference() {
    return this.preference;
  }


  /**
   * @return the nodeAccessInfoMap
   */
  public Map<Long, NodeAccessInfo> getNodeAccessInfoMap() {
    return this.nodeAccessInfoMap;
  }


  /**
   * @param nodeAccessInfoMap the nodeAccessInfoMap to set
   */
  private void setNodeAccessInfoMap(final Map<Long, NodeAccessInfo> nodeAccessInfoMap) {
    this.nodeAccessInfoMap = nodeAccessInfoMap;
  }
}
