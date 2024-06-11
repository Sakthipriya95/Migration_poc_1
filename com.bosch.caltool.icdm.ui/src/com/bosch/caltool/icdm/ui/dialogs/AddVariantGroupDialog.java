package com.bosch.caltool.icdm.ui.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2lDetailsHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * This class provides a dialog to add Varaint group Information
 */
public class AddVariantGroupDialog extends Dialog {

  /**
   * Dialog width constant
   */
  private static final int DIALOG_WIDTH = 500;

  /**
   * Dialog Height constant
   */
  private static final int DIALOG_HEIGHT = 250;

  /**
   * Save Button
   */
  protected Button saveBtn;

  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Cancel Button
   */
  protected Button cancelBtn;
  /**
   * instance
   */
  private A2lDetailsHandler a2lDetailsHandler;
  /**
   * a2l data handler
   */
  private final A2LEditorDataHandler dataHandler;
  /**
   * PIDCVersionDetailsSection instance
   */
  private AddVaraintGroupSection versionSection;

  /**
   * @param parentShell instance
   * @param long1
   * @param vrsnpage VersionsPage
   * @param isEdit is an update or create operation
   */
  public AddVariantGroupDialog(final Shell parentShell, final A2LEditorDataHandler dataHandler) {
    super(parentShell);
    setA2lDetailsHandler(new A2lDetailsHandler());
    this.dataHandler = dataHandler;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {

    // set dialog title
    newShell.setText("Create New Variant Group");
    newShell.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    super.configureShell(newShell);
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
    this.top.setLayoutData(new GridData(GridData.FILL_BOTH));

    this.versionSection = new AddVaraintGroupSection(this.top, getFormToolkit());
    this.versionSection.createVarGroupSection();
    this.versionSection.setOkBtn(this.saveBtn);
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
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (null != this.dataHandler.getWpVersionId()) {
      getA2lDetailsHandler().createA2lVarGroup(this.versionSection.getVersionNameTxt().getText().toString(),
          this.versionSection.getVersionDescEngTxt().getText().toString(), this.dataHandler.getWpVersionId(),
          this.dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l());
    }
    else {
      CDMLogger.getInstance().error("Cannot create A2L Variant Grouop : Invalid WP Definition", Activator.PLUGIN_ID);
    }
    super.okPressed();
  }

  /**
   * {@inheritDoc}
   */

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    if (null != this.versionSection) {
      this.versionSection.setOkBtn(this.saveBtn);
    }
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * @return the saveBtn
   */
  public Button getSaveBtn() {
    return this.saveBtn;
  }


  /**
   * @return the a2lDetailsHandler
   */
  public A2lDetailsHandler getA2lDetailsHandler() {
    return this.a2lDetailsHandler;
  }


  /**
   * @param a2lDetailsHandler the a2lDetailsHandler to set
   */
  public void setA2lDetailsHandler(final A2lDetailsHandler a2lDetailsHandler) {
    this.a2lDetailsHandler = a2lDetailsHandler;
  }


}