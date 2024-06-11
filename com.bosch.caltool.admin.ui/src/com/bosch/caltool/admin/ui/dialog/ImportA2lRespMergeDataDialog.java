/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.wizards.A2lRespMergeWizard;
import com.bosch.caltool.apic.ui.wizards.A2lRespMergeWizardDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lRespMergeData;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author DMR1COB
 */
public class ImportA2lRespMergeDataDialog extends TitleAreaDialog {

  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Import Input Data";
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
   * Import Input data button
   */
  private Button importBtn;

  /**
   * Import button Constant
   */
  public static final String IMPORT_BUTTON_CONSTANT = "Import";

  /**
   * @param parentShell parent shell
   */
  public ImportA2lRespMergeDataDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * Configures the shell {@inheritDoc}
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
    setMessage("Paste the Execution ID below and click Import");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
//   set grid for the dialog
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
   *
   * @param composite
   */
  private void createMainComposite(final Composite composite) {

    final Composite mainComposite = getFormToolkit().createComposite(composite);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    mainComposite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    mainComposite.setLayoutData(gridData);
    // Create text field for Execution Id
    createExecutionIdTextField(mainComposite);
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
    this.executionId.addModifyListener(e -> enableOkButton());

    ControlDecoration decorator = new ControlDecoration(this.executionId, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
  }

  /**
   * Creates a text field
   *
   * @param comp parent composite
   * @return Text the new field
   */
  private Text createTextField(final Composite comp) {
//    text field and its alignment
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
//    form field label alignment
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);
  }

  /**
   * Enable OK button only if execution id is provided
   */
  private void enableOkButton() {
    if (null != this.importBtn) {
      this.importBtn.setEnabled(CommonUtils.isNotEmptyString(this.executionId.getText()));
    }
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

  /**
   * creating import button {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.importBtn = createButton(parent, IDialogConstants.OK_ID, IMPORT_BUTTON_CONSTANT, false);
    this.importBtn.setEnabled(CommonUtils.isNotEmptyString(this.executionId.getText()));
    createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
  }

  /**
   * Event triggered if import button is clicked {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    try {
      A2lRespMergeData a2lRespMergeData =
          new A2lResponsibilityServiceClient().fetchA2lRespMergeInputData(this.executionId.getText());

      A2lRespMergeWizard a2lRespMergeWizard = new A2lRespMergeWizard(a2lRespMergeData.getSelectedA2lWpRespList(),
          a2lRespMergeData.getDestA2lResponsibility(), true);
      a2lRespMergeWizard.setRetainedQnaireRespDetailsSet(a2lRespMergeData.getRetainedQnaireRespDetailsSet());
      A2lRespMergeWizardDialog a2lRespMergeWizardDialog =
          new A2lRespMergeWizardDialog(Display.getCurrent().getActiveShell(), a2lRespMergeWizard, true);
      a2lRespMergeWizardDialog.create();
      // open report dialog
      a2lRespMergeWizardDialog.open();

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    super.okPressed();
  }
}
