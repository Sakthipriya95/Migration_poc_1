/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.ActiveDirectoryGroupNodeAccessServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author ssn9cob
 */
public class AddNewADGroupAccessDialog extends AbstractDialog {


  /**
   *
   */
  private static final String WARNING = "Warning";
  /**
   *
   */
  private static final String ACCESS_RIGHTS_GROUP_ALREADY_ASSIGNED = "ACCESS_RIGHTS.GROUP_ALREADY_ASSIGNED";
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;

  /**
   * AD Group name text
   */
  private Text adGroupNameText;

  /**
   * Instance of nodeAccessRightsPage
   */
  private final NodeAccessRightsPage nodeAccessRightsPage;

  private final NodeAccessPageDataHandler nodeAccessPageDataHandler;
  /**
   * Constant for dummy ID value
   */
  public static final Long VALUE_ID = 0L;

  private final String pidcName;
  private ActiveDirectoryGroupNodeAccess nodeAccess;

  /**
   * @param shell shell
   * @param nodeAccessBO BO
   * @param nodeAccessRightsPage page instance
   * @param pidcName name
   */
  public AddNewADGroupAccessDialog(final Shell shell, final NodeAccessPageDataHandler nodeAccessBO,
      final NodeAccessRightsPage nodeAccessRightsPage, final String pidcName) {
    // calling parent constructor
    super(shell);
    this.nodeAccessRightsPage = nodeAccessRightsPage;
    this.nodeAccessPageDataHandler = nodeAccessBO;
    this.pidcName = pidcName;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Add New Active Directory Group");
    // Set the message
    setMessage("Assigning new group access for PIDC :" + this.pidcName, IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Enter AD Group Name");
    // calling parent
    super.configureShell(newShell);

  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite on parent comp
    createComposite();
    return this.top;
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "Enter Display Name of Active Directory Group", ExpandableComposite.TITLE_BAR);
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // Create Input field to get the Group Name
    createADGroupFields();
    // set the layout
    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * Create Input field to get the AD Group from user
   */
  private void createADGroupFields() {
    this.adGroupNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    this.adGroupNameText.setLayoutData(txtGrid);
    this.adGroupNameText.setEditable(true);
    this.adGroupNameText.setEnabled(true);
    this.adGroupNameText.setMessage("Enter Name of the Active Directory Group");

    Label adGroupExample = getFormToolkit().createLabel(this.form.getBody(), " ", SWT.FILL);
    adGroupExample.setLayoutData(txtGrid);
    adGroupExample.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
    adGroupExample.setText("eg., PS-EC/XXX3 Internal Employees / WOM.PS-EC_XXX3-Int");

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
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    try {
      String adGrpName = getGroupNameFromUser();
      // Check if any input is given in text field
      if (CommonUtils.isNotEmptyString(adGrpName)) {

        UserInfo groupInfo = getGroupInfoFromDispName(adGrpName);
        if ((groupInfo != null)) {
          boolean status = createADGroupAccess(groupInfo);
          if (!status) {
            // check if an already existing group is entered by user
            // "Group already exists in Access Rights List. Kindly add a different group"
            MessageDialog.openWarning(Display.getDefault().getActiveShell(), WARNING,
                this.nodeAccessPageDataHandler.getCommonData().getMessage("ACCESS_RIGHTS", "GROUP_ALREADY_ASSIGNED"));
          }
          else {
            this.nodeAccessRightsPage.setNewlyCreatedAccess(this.nodeAccess);
            super.okPressed();
          }
        }
        else {
          MessageDialog.openWarning(Display.getDefault().getActiveShell(), WARNING,
              "Please enter a valid Active Directory Group Name");
        }

      }
      else {
        MessageDialog.openWarning(Display.getDefault().getActiveShell(), WARNING,
            "Enter an Active Directory Group name to valdiate!");
      }

    }
    catch (Exception e) {
      CDMLogger.getInstance().debug("Error occured in Add New AD Group - " + e.toString(), e, Activator.PLUGIN_ID);
      MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
          "An error occurred when granting access rights to PIDC. Please contact iCDM-Hotline Team. Error - " +
              e.getLocalizedMessage());
    }

  }

  /**
   * @throws ApicWebServiceException
   */
  private boolean createADGroupAccess(final UserInfo groupInfo) throws ApicWebServiceException {

    ActiveDirectoryGroupNodeAccessServiceClient client = new ActiveDirectoryGroupNodeAccessServiceClient();
    try {
      String displayName = ((groupInfo.getDisplayName() != null) && !groupInfo.getDisplayName().isEmpty())
          ? groupInfo.getDisplayName() : groupInfo.getCommonName();
      this.nodeAccess = client.createADGroupAccess(displayName, groupInfo.getCommonName(),
          this.nodeAccessPageDataHandler.getNodeId(), this.nodeAccessPageDataHandler.getNodeType().getTypeCode());
      return true;
    }
    catch (ApicWebServiceException e) {
      if (e.getErrorCode().equals(ACCESS_RIGHTS_GROUP_ALREADY_ASSIGNED)) {
        return false;
      }
      throw e;
    }
  }

  /**
   * @return
   */
  private String getGroupNameFromUser() {
    return this.adGroupNameText.getText().trim();
  }


  /**
  *
  */
  private UserInfo getGroupInfoFromDispName(final String groupName) {
    try {
      return new LdapAuthenticationWrapper().getGroupInfo(groupName.trim());
    }
    catch (LdapException e) {
      CDMLogger.getInstance().debug("Error occured in Fetching data from LDAP - " + e.toString(), e,
          Activator.PLUGIN_ID);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
  }


}
