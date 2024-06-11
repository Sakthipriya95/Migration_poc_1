/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.common.exception.MailException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.OutlookMail;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author apj4cob
 */
public class SendUsecaseObjectLinkAction extends Action {

  /**
   * Action ID
   */
  public static final String ACTION_ID = "com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction";
  /**
   * IModel instance
   */
  private final IModel linkable;
  private final Map<String, String> additionalDetails = new HashMap<>();

  private final boolean isPrivateUCItem;


  /**
   * New action with the given link text and link
   *
   * @param actionText action text
   * @param linkable Linkable object
   * @param additionalDetails cotaining the other ids
   */

  public SendUsecaseObjectLinkAction(final String actionText, final IModel linkable,
      final Map<String, String> additionalDetails, final boolean isPrivateUCItem) {
    super();
    this.linkable = linkable;
    this.additionalDetails.putAll(additionalDetails);
    this.isPrivateUCItem = isPrivateUCItem;
    setId(ACTION_ID);
    super.setText(actionText);
    super.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SEND_MAIL_16X16));

  }

  /**
   * Creates the html mail using the link details and opens as a new outlook mail
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void run() {
    LinkCreator linkCreator = new LinkCreator(this.linkable, this.additionalDetails);
    // get link details
    String linkText = linkCreator.getDisplayText();
    String link = linkCreator.getUrl();

    if (this.isPrivateUCItem) {
      CDMLogger.getInstance().infoDialog(
          "NOTE: This refers to the 'common' use case item, not the private use case node !!", Activator.PLUGIN_ID);
    }

    String linkHtml = "<a href=\"" + link + "\">" + linkText + "</a>";
    String mailContents = "<html><br><br>" + linkHtml + "<br><br></html>";

    OutlookMail mail = new OutlookMail(CDMLogger.getInstance());
    try {
      mail.composeEmail("", linkText, mailContents);
      CDMLogger.getInstance().debug("Link '" + link + "' added to outlook mail");
    }
    catch (MailException exp) {
      CDMLogger.getInstance().errorDialog("Failed to create outlook mail for the link", exp, Activator.PLUGIN_ID);
    }
  }
}

