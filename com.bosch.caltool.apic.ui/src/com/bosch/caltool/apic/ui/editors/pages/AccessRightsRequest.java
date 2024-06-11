/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.forms.editor.FormEditor;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapperCloseable;
import com.bosch.caltool.icdm.common.exception.MailException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.OutlookMail;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author RDP2COB
 */
public class AccessRightsRequest {

  /**
  *
  */
  private static final String LINE_BREAK = "<br>";
  /**
  *
  */
  private static final String TD_OPENING_TAG = "<td style=\"border: 1px solid #dddddd\">";
  /**
  *
  */
  private static final String TR_OPENING_TAG = "<tr style=\"border: 1px solid #dddddd\">";
  /**
  *
  */
  private static final String TD_CLOSING_TAG = "</td>";
  /**
  *
  */
  private static final String TR_CLOSING_TAG = "</tr>";
  private final FormEditor editor;
  private final NodeAccessPageDataHandler dataHandler;

  /**
   * @param editor access rights editor data
   * @param dataHandler NodeAccess Page Data Handler
   */
  public AccessRightsRequest(final FormEditor editor, final NodeAccessPageDataHandler dataHandler) {
    this.editor = editor;
    this.dataHandler = dataHandler;
  }

  /**
   * This method is used to compose the email
   */
  public void requestAccessRights() {

    OutlookMail mail = new OutlookMail(CDMLogger.getInstance());
    mail.setHighPriority(true);

    StringBuilder mailContent = new StringBuilder();
    String subject = "";
    StringBuilder emailBody = new StringBuilder();

    // Compose email
    subject = composeSubjectAndEmailBody(this.dataHandler, subject, emailBody);

    try {

      CurrentUserBO currentUserBo = new CurrentUserBO();
      boolean userHasGrant = false;

      for (NodeAccess user : this.dataHandler.getNodeAccess()) {
        if ((CommonUtils.isEqual(user.getUserId(), currentUserBo.getUserID()))) {
          if ((user.isGrant())) {
            userHasGrant = true;
          }
          break;
        }
      }


      String[] mailToAddress = computeMailToAddressMap(currentUserBo.getUserID(), userHasGrant);

      Set<String> mailToAddressSet = new HashSet<>(Arrays.asList(mailToAddress));
      // Create the content of the mail
      computeMailContent(this.dataHandler, mailContent, this.editor, emailBody, mailToAddress);

      mail.composeEmail(mailToAddressSet, subject, mailContent.toString());
    }
    catch (MailException | ApicWebServiceException e) {
      CDMLogger.getInstance().debug("Error while composing email for requesting access rights: " + e.toString(), e,
          Activator.PLUGIN_ID);
    }

  }

  /**
   * @param dataHandler
   * @param subject
   * @param emailBody
   * @return
   */
  private static String composeSubjectAndEmailBody(final NodeAccessPageDataHandler dataHandler,
      final String emailSubject, final StringBuilder emailBody) {
    String subject = emailSubject;

    // If access rights is requested for alias definition
    if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.ALIAS_DEFINITION.getTypeClass()) &&
        (CommonUtils.isNotNull(((AliasDef) dataHandler.getNode()).getName()))) {
      subject = "#ALIAS DEFINITION ACCESS REQUEST - " + ((AliasDef) dataHandler.getNode()).getName();
      emailBody
          .append("Kindly provide access to the Alias Definition - " + ((AliasDef) dataHandler.getNode()).getName());
    }

    // If access rights is requested for PIDC
    else if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.PIDC.getTypeClass()) &&
        (CommonUtils.isNotNull(((Pidc) dataHandler.getNode()).getName()))) {
      subject = "#PIDC ACCESS REQUEST- " + ((Pidc) dataHandler.getNode()).getName();
      emailBody.append("Kindly provide access to the PIDC - " + ((Pidc) dataHandler.getNode()).getName());
    }

    // If access rights is requested for questionnaire
    else if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.QUESTIONNAIRE.getTypeClass()) &&
        (CommonUtils.isNotNull(((Questionnaire) dataHandler.getNode()).getName()))) {
      subject = "#QUESTIONNAIRE ACCESS REQUEST- " + ((Questionnaire) dataHandler.getNode()).getName();
      emailBody
          .append("Kindly provide access to the QUESTIONNAIRE - " + ((Questionnaire) dataHandler.getNode()).getName());
    }

    // If access rights is requested for alias definition
    else if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.CDR_FUNCTION.getTypeClass()) &&
        (CommonUtils.isNotNull(((Function) dataHandler.getNode()).getName()))) {
      subject = "#FUNCTION ACCESS REQUEST- " + ((Function) dataHandler.getNode()).getName();
      emailBody.append("Kindly provide access to the FUNCTION - " + ((Function) dataHandler.getNode()).getName());
    }

    // If access rights is requested for Rule set
    else if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.CDR_RULE_SET.getTypeClass()) &&
        (CommonUtils.isNotNull(((RuleSet) dataHandler.getNode()).getName()))) {
      subject = "#RULESET ACCESS REQUEST- " + ((RuleSet) dataHandler.getNode()).getName();
      emailBody.append("Kindly provide access to the RULESET - " + ((RuleSet) dataHandler.getNode()).getName());
    }

    // If access rights is requested for FC2WP Definition
    else if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.FC2WP_DEF.getTypeClass()) &&
        (CommonUtils.isNotNull(((FC2WPDef) dataHandler.getNode()).getName()))) {
      subject = "#FC2WPDefinition ACCESS REQUEST- " + ((FC2WPDef) dataHandler.getNode()).getName();
      emailBody
          .append("Kindly provide access to the FC2WPDefinition - " + ((FC2WPDef) dataHandler.getNode()).getName());
    }

    // If access rights is requested for Use case
    else if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.USE_CASE.getTypeClass()) &&
        (CommonUtils.isNotNull(((UseCase) dataHandler.getNode()).getName()))) {
      subject = "#USECASE ACCESS REQUEST- " + ((UseCase) dataHandler.getNode()).getName();
      emailBody.append("Kindly provide access to the USECASE - " + ((UseCase) dataHandler.getNode()).getName());
    }
    return subject;
  }

  private String[] computeMailToAddressMap(final Long currentUserID, final boolean userHasGrant)
      throws ApicWebServiceException {

    HashMap<Long, String> mailToAdressMap = new HashMap<>();

    if (CommonUtils.isNotNull(currentUserID)) {
      if (!userHasGrant) {
        // Fetch the mail id of users with owner and grant access
        fetchGrantAndOwnerAccessMailIds(mailToAdressMap);
      }
      else {
        // if the user already has grant access, fetch only the mail id of users with owner access
        fetchOwnerAccessMailIds(mailToAdressMap);
      }
    }

    String[] mailToAddress;
    if (CommonUtils.isNotEmpty(mailToAdressMap)) {
      mailToAddress = new ArrayList<String>(mailToAdressMap.values()).toArray(new String[mailToAdressMap.size()]);
    }
    else {
      // if there are no mail ids fetched, then send the request email to hotline
      mailToAddress = new CommonDataBO().getParameterValue(CommonParamKey.COMPLI_ISSUE_REPORT_TO).split(";");
    }
    return mailToAddress;
  }

  /**
   * @param mailToAdressMap
   */
  private void fetchOwnerAccessMailIds(final HashMap<Long, String> mailToAdressMap) {
    try (LdapAuthenticationWrapperCloseable ldapCloseable = new LdapAuthenticationWrapperCloseable()) {
      CurrentUserBO currentUserBO = new CurrentUserBO();
      for (NodeAccess user : this.dataHandler.getNodeAccess()) {
        try {
          if ((CommonUtils.isNotEqual(currentUserBO.getUserID(), user.getUserId())) && user.isOwner()) {
            fillUserInfoMap(mailToAdressMap, user, ldapCloseable);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().debug(
              "Error while fetching the email address of users for requesting access rights: " + e.toString(), e,
              Activator.PLUGIN_ID);
        }
      }
    }
  }


  /**
   * @param mailToAdressMap
   */
  private void fetchGrantAndOwnerAccessMailIds(final HashMap<Long, String> mailToAdressMap) {
    try (LdapAuthenticationWrapperCloseable ldapCloseable = new LdapAuthenticationWrapperCloseable()) {
      CurrentUserBO currentUserBO = new CurrentUserBO();
      for (NodeAccess user : this.dataHandler.getNodeAccess()) {
        try {
          if ((CommonUtils.isNotEqual(currentUserBO.getUserID(), user.getUserId())) &&
              (user.isGrant() || user.isOwner())) {
            fillUserInfoMap(mailToAdressMap, user, ldapCloseable);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().debug("Error while composing email for requesting access rights: " + e.toString(), e,
              Activator.PLUGIN_ID);
        }
      }
    }
  }

  private void computeMailContent(final NodeAccessPageDataHandler dataHandler, final StringBuilder mailContent,
      final FormEditor editor, final StringBuilder emailBody, final String[] mailToAddress)
      throws ApicWebServiceException {
    mailContent.append("<html>");
    mailContent.append("Hello");

    if (mailToAddress.length > 1) {
      mailContent.append(" together");
    }

    mailContent.append(",");
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);

    mailContent.append(emailBody);

    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);

    mailContent.append("(Please mention the kind of access you need to perform activity).");
    mailContent.append(LINE_BREAK);
    mailContent.append("Type of access needed : WRITE/GRANT/OWNER ");

    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);

    mailContent.append("(Please mention here the activity for which the access is requested for ) :");

    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);

    mailContent.append("<span style=\"font-weight:bold\">User ID : </span>");
    mailContent.append(new CurrentUserBO().getUserName());

    mailContent.append(LINE_BREAK);

    mailContent.append("</p>");

    mailContent.append(TR_OPENING_TAG);
    mailContent.append(TR_OPENING_TAG);

    if (CommonUtils.isEqual(dataHandler.getNode().getClass(), MODEL_TYPE.PIDC.getTypeClass())) {
      mailContent.append("PIDC : ");

      mailContent.append(TD_CLOSING_TAG);
      mailContent.append(TD_OPENING_TAG);

      String pidcVersLink = null == ((PIDCEditor) editor).getPidcVersion() ? ""
          : "icdm:pidvid," + ((PIDCEditor) editor).getPidcVersion().getId();
      mailContent.append("<a href =" + pidcVersLink + ">");
      mailContent.append(
          null == ((PIDCEditor) editor).getPidcVersion() ? "" : ((PIDCEditor) editor).getPidcVersion().getName());
    }

    mailContent.append("</a>");
    mailContent.append(TD_CLOSING_TAG);
    mailContent.append(TR_CLOSING_TAG);
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);
    mailContent.append("Thank You!");
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);
    mailContent.append("</html>");
  }

  private static void fillUserInfoMap(final HashMap<Long, String> mailToAdressMap, final NodeAccess user,
      final LdapAuthenticationWrapperCloseable ldap) {
    try {
      mailToAdressMap.put(user.getUserId(), ldap.getUserDetails(user.getName()).getEmailAddress());
    }
    catch (LdapException e) {
      CDMLogger.getInstance().debug(
          "Error while fetching the user details : " + user.getName() + " - " + e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }
}
