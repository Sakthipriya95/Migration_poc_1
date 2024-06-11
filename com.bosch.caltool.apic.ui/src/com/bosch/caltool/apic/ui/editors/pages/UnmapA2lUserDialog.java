/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author hnu1cob
 */
public class UnmapA2lUserDialog extends AbstractDialog {

  private static final String LINE_BREAK = "<br>";
  private final PidcA2lFileExt selA2LFile;
  private FormToolkit formToolkit;
  private final UnmapA2LResponse unmapA2LResponse;

  /**
   * @param parentShell , parent shell
   * @param selA2LFiles ,PidcA2lFileExt of selected a2l file
   * @param unmapA2LResponse UnmapA2LResponse
   */
  public UnmapA2lUserDialog(final Shell parentShell, final List<PidcA2lFileExt> selA2LFiles,
      final UnmapA2LResponse unmapA2LResponse) {
    super(parentShell);
    this.unmapA2LResponse = unmapA2LResponse;
    // get the first element as there will be only one selection
    this.selA2LFile = selA2LFiles.get(0);
  }


  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Request to unmap A2L from PIDC Version");
    // Set the message
    setMessage(
        "The below related records will be deleted during Unmapping.\nUpon confirmation, an email will be sent to iCDM Hotline to process the request.");
    return contents;
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Request A2L Unmapping");
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

    GridData gridData = GridDataUtil.getInstance().getGridData();
    GridLayout gridLayout = new GridLayout();
    // create scrolled composite
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.V_SCROLL);

    Composite topComp = new Composite(scrollComp, SWT.NONE);
    topComp.setLayoutData(gridData);
    topComp.setLayout(gridLayout);

    UnmapA2lDetailsComposite detailsComp =
        new UnmapA2lDetailsComposite(topComp, getFormToolkit(), this.selA2LFile, false, this.unmapA2LResponse);
    detailsComp.setLayout(gridLayout);
    detailsComp.setLayoutData(gridData);

    topComp.layout(true, true);

    scrollComp.setLayout(gridLayout);
    scrollComp.setLayoutData(gridData);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setContent(topComp);
    scrollComp.setMinSize(topComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    return parent;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
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
    // send mail


    MailHotline mailHotline;
    try {
      mailHotline = getHotlineNotifier();
      mailHotline.setSubject(new CommonDataBO().getParameterValue(CommonParamKey.UNMAP_A2L_SUBJECT));
      mailHotline.setContent(formatMailContent());
      boolean sent = mailHotline.sendMail();
      if (sent) {
        CDMLogger.getInstance().infoDialog("Request sent to Hotline!", Activator.PLUGIN_ID);
      }
    }
    catch (LdapException | ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    super.okPressed();
  }

  /**
   * hotline Notifier
   */
  private MailHotline getHotlineNotifier() throws LdapException, ApicWebServiceException {
    String fromAddr =
        new LdapAuthenticationWrapper().getUserDetails(new CurrentUserBO().getUserName()).getEmailAddress();
    String status = null;
    String toAddr = null;
    try {
      // get the HOTLINE address
      toAddr = new CommonDataBO().getParameterValue(CommonParamKey.ICDM_HOTLINE_TO);
      // get notification status
      status = new CommonDataBO().getParameterValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    if (ApicUtil.compare(status, ApicConstants.CODE_YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(fromAddr, toAddr, true/* automatic notification enabled */);
    }
    // Set details and send mail
    return new MailHotline(fromAddr, toAddr, false/* automatic notification disabled */);
  }

  /**
   * @return
   */
  private String formatMailContent() {
    // get a2l link
    LinkCreator linkCreator = new LinkCreator(this.selA2LFile.getPidcA2l());
    String link = linkCreator.getUrl();
    String linkText = linkCreator.getDisplayText();

    StringBuilder builder = new StringBuilder();
    builder.append("<html>");
    builder.append("<p>");
    builder.append("Hello Hotline-Clearing Team,");
    builder.append(LINE_BREAK);
    builder.append(LINE_BREAK);
    builder.append("Please unmap the below A2L from the PIDC and delete all the mentioned related records.");
    builder.append(LINE_BREAK);
    builder.append(LINE_BREAK);
    builder.append("<a href=\"" + link + "\">" + linkText + "</a>");
    builder.append(LINE_BREAK);
    builder.append(LINE_BREAK);
    builder.append("Thank You!");
    builder.append(LINE_BREAK);
    builder.append(LINE_BREAK);
    builder.append("This is an auto generated mail from iCDM client");
    builder.append("</p>");
    builder.append("</html>");
    return builder.toString();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, "Confirm and Send Email", true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }
}
