package com.bosch.caltool.apic.ui.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * This class provides a dialog to add password
 */
// Icdm-515
public class PasswordDialog extends Dialog {

  /**
   * Save Button
   */
  protected Button saveBtn;

  /**
   * Cancel Button
   */
  protected Button cancelBtn;

  // composite
  private Composite top;
  // form toolkit
  private FormToolkit formToolkit;
  private Composite composite;
  // section
  private Section section;
  // Form
  private Form form;

  /**
   * ControlDecoration instance for numeric
   */
  protected ControlDecoration txtNumFormatDec;

  // Text to enter password
  private Text passwordTxt;


  /**
   * @param parentShell instance
   */
  public PasswordDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set shell title
    super.configureShell(newShell);
    newShell.setText("Enter Windows Password");
  }

  /**
   * Creates the Dialog Area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = new Composite(parent, SWT.BORDER_SOLID);
    final GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.verticalSpacing = 0;
    this.top.setLayout(layout);
    this.top.setLayoutData(new GridData(GridData.FILL_BOTH));
    // create the composite
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
    gridLayout.makeColumnsEqualWidth = true;
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setSize(400, 200);
    this.composite.setLayout(gridLayout);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create the section
    createSection();
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = getFormToolkit().createSection(this.composite, SWT.NONE);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setExpanded(true);
    // create the form
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes Form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setSize(250, 100);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    createPasswordArea();
  }


  /**
   * @return the Grid data for the Password text Box
   */
  private GridData getTextFieldGridData() {
    final GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
    gridData2.grabExcessVerticalSpace = true;
    gridData2.widthHint = 250;
    return gridData2;
  }

  /**
   * This method initializes Password Area
   */
  private void createPasswordArea() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;

    getFormToolkit().createLabel(this.form.getBody(), "Password");

    this.passwordTxt = new Text(this.form.getBody(), SWT.PASSWORD | SWT.BORDER);
    this.passwordTxt.setLayoutData(getTextFieldGridData());
    this.passwordTxt.setFocus();

    // Modify listener for password field
    this.passwordTxt.addModifyListener(event -> checkSaveBtnEnable());
  }


  /**
   * This method validates text fields
   *
   * @return boolean
   */
  private boolean validateFields() {
    // Validate password field
    return this.passwordTxt.getText().length() > 0;
  }


  /**
   * Validates save button enable or disable
   */
  private void checkSaveBtnEnable() {
    if (validateFields() && (this.saveBtn != null)) {
      this.saveBtn.setEnabled(true);
    }
    else if (this.saveBtn != null) {
      this.saveBtn.setEnabled(false);
    }
  }

  // Create the Save Cancel Button
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Continue", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // validate the password and set the Password to the CurrenUserBO
    try {
      CurrentUserBO currentUserBO = new CurrentUserBO();
      new LdapAuthenticationWrapper().validate(currentUserBO.getUserName(), this.passwordTxt.getText());
      currentUserBO.setPassword(this.passwordTxt.getText());

      super.okPressed();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    catch (LdapException e) {
      // For Invalid password then give alert and place the focus in the text box
      this.passwordTxt.setText("");
      CDMLogger.getInstance().warnDialog("The password is invalid. Please provide the valid password.", e,
          Activator.PLUGIN_ID);
      this.passwordTxt.setFocus();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    // set dialog style
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

}