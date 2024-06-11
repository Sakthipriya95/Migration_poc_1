package com.bosch.caltool.apic.ui.dialogs;


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

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.pages.PidcVersionsPage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides a dialog to add pidc version details
 */
public class PIDCVersionDetailsDialog extends Dialog {

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
   * VersionsPage instance
   */
  private final PidcVersionsPage vrsnPage;


  /**
   * PIDCVersionDetailsSection instance
   */
  private PIDCVersionDetailsSection versionSection;

  /**
   * Editing of version details or not
   */
  private final boolean isEdit;


  /**
   * @param parentShell instance
   * @param vrsnpage VersionsPage
   * @param isEdit is an update or create operation
   */
  public PIDCVersionDetailsDialog(final Shell parentShell, final PidcVersionsPage vrsnpage, final boolean isEdit) {
    super(parentShell);
    this.vrsnPage = vrsnpage;
    this.isEdit = isEdit;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {

    // set dialog title
    newShell.setText("Enter PIDC version details");
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

    this.versionSection = new PIDCVersionDetailsSection(this.top, getFormToolkit(), this.isEdit, null);
    this.versionSection.createSectionPidcVersion();
    this.versionSection.setOkBtn(this.saveBtn);
    // if the pidc version is not null and if edit option is cleared
    if ((this.vrsnPage.getSelectedPidcVersion() != null) && this.isEdit) {
      this.versionSection.getVersionNameTxt().setText(this.vrsnPage.getSelectedPidcVersion().getVersionName());
      this.versionSection.getVersionDescEngTxt().setText(this.vrsnPage.getSelectedPidcVersion().getVersDescEng() == null
          ? "" : this.vrsnPage.getSelectedPidcVersion().getVersDescEng());
      this.versionSection.getVersionDescGerTxt().setText(this.vrsnPage.getSelectedPidcVersion().getVersDescGer() == null
          ? "" : this.vrsnPage.getSelectedPidcVersion().getVersDescGer());
    }
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
    // Edit mode
    if (this.isEdit) {
      try {
        PidcVersion pidcVersion = this.vrsnPage.getSelectedPidcVersion().clone();
        pidcVersion.setVersionName(this.versionSection.getVersionNameTxt().getText());
        pidcVersion.setVersDescEng(this.versionSection.getVersionDescEngTxt().getText());
        pidcVersion.setVersDescGer(this.versionSection.getVersionDescGerTxt().getText());
        PidcVersionServiceClient client = new PidcVersionServiceClient();
        client.editPidcVersion(pidcVersion);
      }
      catch (ApicWebServiceException | CloneNotSupportedException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }


    }
    else {
      PIDCActionSet actionSet = new PIDCActionSet();
      String[] versionDetails = {
          this.versionSection.getVersionNameTxt().getText(),
          this.versionSection.getVersionDescEngTxt().getText(),
          this.versionSection.getVersionDescGerTxt().getText() };
      actionSet.createNewRevision(this.vrsnPage, versionDetails);
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


}