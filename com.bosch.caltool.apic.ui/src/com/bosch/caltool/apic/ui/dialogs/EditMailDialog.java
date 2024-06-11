/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapperCloseable;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author dja7cob Dialog for editing the mail template when a attribute value is deleted
 */
// ICDM-2217 / ICDM-2300
public class EditMailDialog extends AbstractDialog {

  /**
   * constant for delete attribute value
   */
  private static final String DELETE_ATTRIBUTE_VALUE = "Delete Attribute Value";

  /**
   * constant for current user
   */
  private static final String CURRUSER = "#CURRUSER#";

  /**
   * constant for pidc version
   */
  private static final String PIDC_AND_VERSION = "#PIDC_AND_VERSION#";

  /**
   * constant for attribute value
   */
  private static final String ATTRIBUTE_AND_VALUE = "#ATTRIBUTE_AND_VALUE#";

  /**
   * Section Description for place holders in the mail template
   */
  private static final String SECTION_DESCRIPTION =
      "Do not modify the placeholders with #<text>#\n\n1. #ATTRIBUTE_AND_VALUE#\t- Attributes and Values\n2. #PIDC_AND_VERSION#\t\t- PIDC and Version\n3. #CURRUSER#\t\t\t\t\t- Current User\n\n";

  /**
   * Initial size of String Builder for title
   */
  private static final int SB_MAIL_BODY_SIZE = 200;

  /**
   * Button instance for cancel
   */
  private Button cancelBtn;
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
   * Composite instance
   */
  private Composite top;
  /**
   * List of selected attribute values
   */
  private final List<AttributeValue> selectedValsList = new ArrayList<>();
  /**
   * mail content
   */
  private Text mailContent;
  /**
   * Instance of attribute's page
   */
  private final AttributesPage attributesPage;
  /**
   * cancelled button instance
   */
  private boolean cancelled;

  /**
   * Constructor
   *
   * @param parentShell instance
   * @param attrPage Attributes Page
   */
  public EditMailDialog(final Shell parentShell, final AttributesPage attrPage) {
    super(parentShell);
    this.attributesPage = attrPage;
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

    // Separate the attribute values with ","
    boolean isFirst = true;
    // String builder (for multiple selection)
    StringBuilder selctedAttrVal = new StringBuilder(SB_MAIL_BODY_SIZE);
    for (com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrVal : this.selectedValsList) {
      // iterate through the selected value list
      if (isFirst) {
        // do not append comma for first name in the list
        selctedAttrVal.append(attrVal.getName());
        isFirst = false;
      }
      else {
        // append comma for other attribute values in the list
        selctedAttrVal.append(", ").append(attrVal.getName());
      }
    }

    // Set the title
    setTitle(DELETE_ATTRIBUTE_VALUE);

    // Set the message
    // Title Description
    setMessage("Delete Attribute Value and send mail notification : \n" + selctedAttrVal.toString());

    return contents;
  }

  /**
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
   */
  @Override
  protected void cancelPressed() {
    setCancelPressed(true);
    super.cancelPressed();
  }

  /**
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Create confirm and cancel buttons
    Button saveBtn = createButton(parent, IDialogConstants.OK_ID, "Delete and Notify", true);
    saveBtn.setEnabled(true);
    setCancelBtn(createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Dialog title
    newShell.setText(DELETE_ATTRIBUTE_VALUE);
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * (non-Javadoc)
   *
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
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method creates the dialog ui
   *
   * @param top
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    // Create section for mail content Text field
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Review/Edit Mail");
    createForm();
    // Set description for place holders in the mail template
    this.section.setDescription(EditMailDialog.SECTION_DESCRIPTION);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Mail template:");
    this.mailContent =
        getFormToolkit().createText(this.form.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    GridData mailTemplateGridData = GridDataUtil.getInstance().createGridData();
    mailTemplateGridData.grabExcessVerticalSpace = false;
    this.mailContent.setSize(this.mailContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    this.mailContent.setLayoutData(mailTemplateGridData);
    this.mailContent.setFocus();

    // Fetch the mail template from Database
    this.mailContent.setText(getMailContent());

    // Allow editing the text field(mail template)
    this.mailContent.addModifyListener(modifyevent -> {
      // Modify listener
    });
  }

  /**
   * @return
   */
  private String getMailContent() {
    String mailTemplate = "";
    byte[] files = null;
    try {
      files = new AttributeValueServiceClient().getMailContent(CommonUtils.getSystemUserTempDirPath());
      Map<String, byte[]> filesUnZipped = ZipUtils.unzip(files);
      // create mail template
      mailTemplate = new String(filesUnZipped.get(ApicConstants.DEL_ATTRVAL_MAIL_TEMPLATE));
    }
    catch (ApicWebServiceException | IOException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return mailTemplate;
  }

  /**
  *
  */
  @Override
  protected void okPressed() {
    boolean cmdSuccess = false;
    boolean bConfirm = false;

    // Check whether all place holders are present in the mail template
    if (validatePlaceHolders()) {

      for (AttributeValue attrVal : this.selectedValsList) {
        AttributeValueClientBO valBO = new AttributeValueClientBO(attrVal);

        try {
          AttributeValueServiceClient serClient = new AttributeValueServiceClient();
          attrVal.setDeleted(true);
          attrVal = serClient.update(attrVal);
          notifyOwners(valBO, getMailTemplateString());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        // Handle enable and disable of actions in the attributes page
        this.attributesPage.getActionSet().getEditValDepAction().setEnabled(false);
        this.attributesPage.getActionSet().getDeleteValueDepnAction().setEnabled(false);
        this.attributesPage.getActionSet().getDeleteValueAction().setEnabled(false);
        this.attributesPage.getActionSet().getEditValAction().setEnabled(false);
        this.attributesPage.getActionSet().getUndeleteValueAction().setEnabled(true);
        this.attributesPage.getActionSet().getAddValueDepAction().setEnabled(false);
        SortedSet<AttributeValue> values = this.attributesPage.getSelectedValuesMap().get(valBO.getAttribute());
        if (null != values) {
          if (values.contains(attrVal)) {
            values.remove(attrVal);
          }
          values.add(attrVal);

        }
        this.attributesPage.getValueTableViewer().setInput(values);
        this.attributesPage.getValueTableViewer().refresh();
        if (valBO.getClearingStatus() == AttributeValueClientBO.CLEARING_STATUS.REJECTED) {
          // Confirm only once, in case of multiple delete
          bConfirm = checkCmdSuccess(cmdSuccess, bConfirm);
          if (bConfirm) {
            // Open outlook
            createOutlookMail(valBO);
          }
        }
        // when atleast one cmd succeded ,its eligible to send notification
        cmdSuccess = true;
      }
      super.close();
    }
  }


  /**
   * iCDM-890 <br>
   * Trigger mail in Background (without user inervention) thread for the pidc owners *
   */
  private void notifyOwners(final AttributeValueClientBO valBO, final String mailTemplateValue) {
    // Run the operation in background
    final Thread job = new Thread("NotifyPIDCOwners") {

      /**
       * Notify owners of each PIDC
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // ICDM-1505
        notifyPIDCOwners(valBO, mailTemplateValue);
      }
    };
    job.start();
  }

  /**
   * @param valBO
   * @param mailTemplateValue
   */
  private void notifyPIDCOwners(final AttributeValueClientBO valBO, final String mailTemplateValue) {
    Map<String, Map<String, Map<String, Long>>> userPidcMap;
    try {
      userPidcMap = new PidcServiceClient().getPidcUsersUsingAttrValue(valBO.getAttrValue().getId());

      if (!userPidcMap.isEmpty()) {
        Map<String, Map<String, Map<String, Long>>> toAddrPidc = new ConcurrentHashMap<>();
        try (LdapAuthenticationWrapperCloseable ldap = new LdapAuthenticationWrapperCloseable()) {
          // Iterate users
          for (Entry<String, Map<String, Map<String, Long>>> userName : userPidcMap.entrySet()) {
            fillEmailAddress(toAddrPidc, ldap, userName);
          }
        }
        // Trigger mail to the receipients
        if (!toAddrPidc.isEmpty()) {
          triggerMailNotification(toAddrPidc, valBO, mailTemplateValue);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private void fillEmailAddress(final Map<String, Map<String, Map<String, Long>>> toAddrPidc,
      final LdapAuthenticationWrapperCloseable ldap, final Entry<String, Map<String, Map<String, Long>>> userName) {

    // get the email address and store in map
    try {
      UserInfo userInfo = ldap.getUserDetails(userName.getKey());
      if (userInfo != null) {
        toAddrPidc.put(userInfo.getEmailAddress(), userName.getValue());
      }
    }
    catch (LdapException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * iCDM-890 <br>
   * Triggers mail notification , grouped by receipient address with their PIDC's
   *
   * @param toAddrPidc receipients with pidcs
   * @throws ApicWebServiceException
   */
  private void triggerMailNotification(final Map<String, Map<String, Map<String, Long>>> toAddrPidc,
      final AttributeValueClientBO valBO, final String mailTemplateValue)
      throws ApicWebServiceException {
    SortedSet<String> addr;

    CurrentUserBO currentUserBO = new CurrentUserBO();
    for (Entry<String, Map<String, Map<String, Long>>> toAddr : toAddrPidc.entrySet()) {
      addr = new TreeSet<>(); // NOPMD by adn1cob on 8/1/14 10:47 AM
      addr.add(toAddr.getKey());
      final MailHotline mailHotline = getUserNotifier(addr);
      mailHotline.notifyDeletionToOwner(valBO.getAttribute().getName(), valBO.getAttrValue().getName(),
          toAddr.getValue(), currentUserBO.getFullName(), currentUserBO.getDepartment(), mailTemplateValue);
    }
  }

  /**
   * @param cmdSuccess
   * @param bConfirm
   * @return
   */
  private boolean checkCmdSuccess(final boolean cmdSuccess, boolean bConfirm) {
    if (!cmdSuccess) {
      bConfirm = MessageDialogUtils.getQuestionMessageDialog("Send Outlook Mail Notification",
          "Attribute value(s) marked as deleted!\n\nDo you want to notify the owner of this value? ");
    }
    return bConfirm;
  }

  /**
   * @return
   */
  private String getMailTemplateString() {

    return this.mailContent.getText();
  }

  /**
   * @return
   */
  private boolean validatePlaceHolders() {

    StringBuilder message = new StringBuilder(SB_MAIL_BODY_SIZE);

    message.append("Pace holder(s) missing : \n");

    // Check for the place holders
    if (getMailTemplateString().contains(ATTRIBUTE_AND_VALUE) && getMailTemplateString().contains(PIDC_AND_VERSION) &&
        getMailTemplateString().contains(CURRUSER)) {

      return true;
    }
    // If not available, display error message

    if (!(getMailTemplateString().contains(ATTRIBUTE_AND_VALUE))) {

      message.append("\n" + ATTRIBUTE_AND_VALUE);
    }
    if (!(getMailTemplateString().contains(PIDC_AND_VERSION))) {

      message.append("\n" + PIDC_AND_VERSION);
    }
    if (!(getMailTemplateString().contains(CURRUSER))) {

      message.append("\n" + CURRUSER);
    }

    MessageDialogUtils.getErrorMessageDialog(DELETE_ATTRIBUTE_VALUE, message.toString());

    return false;
  }

  /**
   * Create Outlook mail notification for the deleted value
   *
   * @param valBO AttrValue
   */
  // Creates outlook mail
  private void createOutlookMail(final AttributeValueClientBO valBO) {
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, (final IProgressMonitor monitor) -> {
        monitor.beginTask("Opening outlook mail. Please wait...", IProgressMonitor.UNKNOWN);

        notifyUnclearedVal(valBO);

        monitor.done();
        if (monitor.isCanceled()) {
          CDMLogger.getInstance().info("Opening outlook mail was cancelled", Activator.PLUGIN_ID);
        }
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param valBO
   */
  private void notifyUnclearedVal(final AttributeValueClientBO valBO) {
    try {
      Display.getDefault().asyncExec(() -> notifyUnclearedAttrValDel(valBO));
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  private void notifyUnclearedAttrValDel(final AttributeValueClientBO valBO) {
    if ((valBO.getClearingStatus() == AttributeValueClientBO.CLEARING_STATUS.IN_CLEARING) ||
        (valBO.getClearingStatus() == AttributeValueClientBO.CLEARING_STATUS.NOT_CLEARED)) {

      try {
        UserInfo userInfo = new LdapAuthenticationWrapper().getUserDetails(valBO.getAttrValue().getCreatedUser());
        final Set<String> toAddr = new TreeSet<>();
        if (userInfo != null) {
          toAddr.add(userInfo.getEmailAddress());
          final MailHotline mailHotline = getUserNotifier(toAddr);
          mailHotline.notifyRejection(valBO.getAttribute().getName(), valBO.getAttrValue().getName());
        }
      }
      catch (LdapException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param toAddr
   * @return
   */
  private MailHotline getUserNotifier(final Set<String> toAddr) {
    CommonDataBO commonBO = new CommonDataBO();
    // get the HOTLINE address from table
    String fromAddr = null;
    String status = null;
    try {
      fromAddr = commonBO.getParameterValue(CommonParamKey.ICDM_HOTLINE_TO);
      status = commonBO.getParameterValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // get notification status // icdm-946
    if (ApicUtil.compare(status, ApicConstants.CODE_YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(fromAddr, toAddr, true/* automatic notification enabled */);
    }
    // Create the mail sender with from and to addresses
    return new MailHotline(fromAddr, toAddr, false/* automatic notification disabled */);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * @return the cancelBtn
   */
  public Button getCancelBtn() {
    return this.cancelBtn;
  }

  /**
   * @param cancelBtn the cancelBtn to set
   */
  public void setCancelBtn(final Button cancelBtn) {
    this.cancelBtn = cancelBtn;
  }

  /**
   * @return the cancelPressed
   */
  public boolean isCancelPressed() {
    return this.cancelled;
  }

  /**
   * @param cancelPressed the cancelPressed to set
   */
  public void setCancelPressed(final boolean cancelPressed) {
    this.cancelled = cancelPressed;
  }


  /**
   * @return the selectedValsList
   */
  public List<com.bosch.caltool.icdm.model.apic.attr.AttributeValue> getSelectedValsList() {
    return this.selectedValsList;
  }
}
