/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.general;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author gge6cob
 */
public class CurrentUserBO {


  /**
   * Get the user object for current user
   *
   * @return the user object
   * @throws ApicWebServiceException error during service call
   */
  public User getUser() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getCurrentUser();
  }

  /**
   * Get the unique ID of the user This is the primary key in the database
   *
   * @return the unique ID of the user
   * @throws ApicWebServiceException error during service call
   */
  public Long getUserID() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getApicAccessRightCurrentUser().getUserId();
  }

  /**
   * Check if the user has write access to the APIC database A user with write access has full access to the database
   *
   * @return TRUE if the user has write access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasApicWriteAccess() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getApicAccessRightCurrentUser().isApicWrite();
  }

  /**
   * Check if the user has read access to the APIC database
   *
   * @return TRUE if the user has read access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasApicReadAccess() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getApicAccessRightCurrentUser().isApicRead();
  }

  /**
   * Check if the user has write access to the APIC database A user with write access has full access to the database
   *
   * @return TRUE if the user has write access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasApicReadAllAccess() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getApicAccessRightCurrentUser().isApicReadAll();
  }

  /**
   * Check, if the user has a special APIC access right
   *
   * @return the accessRight of current user
   * @throws ApicWebServiceException error during service call
   */
  public String getApicAccessRight() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getApicAccessRightCurrentUser().getAccessRight();
  }

  /**
   * Check if the user has PIDC write access to the APIC database A user with PIDC write access can create new PIDC, but
   * can not maintain attributes and values
   *
   * @return TRUE if the user has PIDC write access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasPidcWriteAccess() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getApicAccessRightCurrentUser().isPidcWrite();
  }

  /**
   * Get the name of the user (the Windows username)
   *
   * @return The user name
   * @throws ApicWebServiceException error during service call
   */
  public String getUserName() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getCurrentUser().getName();
  }

  /**
   * Get the firstName of the user
   *
   * @return the firstName of the user
   * @throws ApicWebServiceException error during service call
   */
  public String getFirstName() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getCurrentUser().getFirstName();
  }

  /**
   * Get the lastName of the user
   *
   * @return the lastName of the user
   * @throws ApicWebServiceException error during service call
   */
  public String getLastName() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getCurrentUser().getLastName();
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @return the users fullName
   * @throws ApicWebServiceException error during service call
   */
  public String getFullName() throws ApicWebServiceException {
    final StringBuilder fullName = new StringBuilder();

    if (!CommonUtils.isEmptyString(getLastName())) {
      fullName.append(getLastName()).append(", ");
    }

    if (!CommonUtils.isEmptyString(getFirstName())) {
      fullName.append(getFirstName());
    }

    if (CommonUtils.isEmptyString(fullName.toString())) {
      fullName.append(getUserName());
    }

    return fullName.toString();
  }

  /**
   * Get the display name of this user
   *
   * @return display name as <Last name> <first name>(<department>)
   * @throws ApicWebServiceException error during service call
   */
  public String getDisplayName() throws ApicWebServiceException {
    final StringBuilder displayName = new StringBuilder();
    displayName.append(getFullName());

    if (!CommonUtils.isEmptyString(getDepartment())) {
      displayName.append(" (").append(getDepartment()).append(")");
    }

    return displayName.toString();
  }

  /**
   * Get the display name of this user, in a different format
   *
   * @return display name as <Last name> <first name>(<department>)
   * @throws ApicWebServiceException error during service call
   */
  public String getDisplayName2() throws ApicWebServiceException {
    return getDisplayName();
  }

  /**
   * Get the department of the user
   *
   * @return the department of the user
   * @throws ApicWebServiceException error during service call
   */
  public String getDepartment() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getCurrentUser().getDepartment();
  }

  /**
   * Get node access of current user
   *
   * @param nodeId Long
   * @return node access object
   * @throws ApicWebServiceException error during service call
   */
  public NodeAccess getNodeAccessRight(final Long nodeId) throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getNodeAccessCurrentUser().get(nodeId);
  }

  /**
   * Get preference value of the preference
   *
   * @param prefKey - preference Key
   * @return preference value
   * @throws ApicWebServiceException error during service call
   */
  public UserPreference getUserPreference(final String prefKey) throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getUserPreferenceValue(prefKey);
  }

  /**
   * @return the password
   * @throws ApicWebServiceException during intialization of client cache
   */
  public String getPassword() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getPassword();
  }

  /**
   * @return the current user encryped password
   * @throws ApicWebServiceException during intialization of client cache
   */
  public String getEncPassword() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getEncPassword();
  }

  /**
   * @return true if the password is aleady available
   * @throws ApicWebServiceException during intialization of client cache
   */
  public boolean hasPassword() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getEncPassword() != null;
  }

  /**
   * @param password the password to set
   * @throws ApicWebServiceException during intialization of client cache
   */
  public void setPassword(final String password) throws ApicWebServiceException {
    GeneralClientCache.getInstance().setPassword(password);
  }

  /**
   * Check if the user has OWNER access to the NODE given. A user with owner access has full access to the node.
   *
   * @param nodeId nodeID
   * @return TRUE if the user has OWNER access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasNodeOwnerAccess(final Long nodeId) throws ApicWebServiceException {
    return (getNodeAccessRight(nodeId) != null) && getNodeAccessRight(nodeId).isOwner();
  }

  /**
   * Check if the user has GRANT access to the NODE given.
   *
   * @param nodeId nodeID
   * @return TRUE if the user has GRANT access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasNodeGrantAccess(final Long nodeId) throws ApicWebServiceException {
    return (getNodeAccessRight(nodeId) != null) && getNodeAccessRight(nodeId).isGrant();
  }

  /**
   * Check if the user has WRITE access to the NODE given.
   *
   * @param nodeId nodeID
   * @return TRUE if the user has write access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasNodeWriteAccess(final Long nodeId) throws ApicWebServiceException {
    return (getNodeAccessRight(nodeId) != null) && getNodeAccessRight(nodeId).isWrite();
  }

  /**
   * Check if the user has READ access to the NODE given. node.
   *
   * @param nodeId nodeID
   * @return TRUE if the user has read access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasNodeReadAccess(final Long nodeId) throws ApicWebServiceException {
    return (getNodeAccessRight(nodeId) != null) && getNodeAccessRight(nodeId).isRead();
  }

  /**
   * Check, if the user is allowed to create SuperGroups Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create SuperGroups
   * @throws ApicWebServiceException error during service call
   */
  public boolean canCreateSuperGroup() throws ApicWebServiceException {
    return hasApicWriteAccess();
  }

  /**
   * Check, if the user is allowed to create Groups Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create Groups
   * @throws ApicWebServiceException error during service call
   */
  public boolean canCreateGroup() throws ApicWebServiceException {
    return hasApicWriteAccess();
  }

  /**
   * Check, if the user is allowed to create Attributes Currently, this is only possible with ApicWriteAccess
   *
   * @return TRUE if the user can create Attributes
   * @throws ApicWebServiceException error during service call
   */
  public boolean canCreateAttribute() throws ApicWebServiceException {
    return hasApicWriteAccess();
  }

  /**
   * Check, if the user is allowed to create PIDC Currently, this is only possible with PidcWriteAccess or
   * ApicWriteAccess
   *
   * @return TRUE if the user can create SuperGroups
   * @throws ApicWebServiceException error during service call
   */
  public boolean canCreatePIDC() throws ApicWebServiceException {
    return hasApicWriteAccess() || hasPidcWriteAccess();
  }

  /**
   * Checks, if the user is allowed to create UseCase Group or UseCase, this is possibly only if the user has
   * ApicWriteAccess
   *
   * @return TRUE if the user can create UseCaseGroups or UseCase
   * @throws ApicWebServiceException error during service call
   */
  public boolean canCreateUseCase() throws ApicWebServiceException {
    return hasApicWriteAccess();
  }

  /**
   * Checks, if the user is allowed to create Component Package, this is possibly only if the user has ApicWriteAccess
   *
   * @return TRUE if the user can create Component Package
   * @throws ApicWebServiceException error during service call
   */
  public boolean canCreateCompPackage() throws ApicWebServiceException {
    return hasApicWriteAccess();
  }

  /**
   * @return true, if disclaimer acceptance has expired
   * @throws ApicWebServiceException error in fetching current user details
   * @throws IcdmException error during date format conversion
   */
  public boolean isDisclaimerAcceptanceExpired() throws ApicWebServiceException, IcdmException {
    String lastAcceptedDate = getUser().getDisclaimerAcceptedDate();

    if (null == lastAcceptedDate) {
      return true;
    }

    long diffInDays;
    diffInDays = TimeUnit.MILLISECONDS.toDays(Math.abs(Calendar.getInstance().getTimeInMillis() -
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, lastAcceptedDate).getTimeInMillis()));
    Long intervalDays = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.DISCLAIMER_VALID_INTERVAL));

    return diffInDays >= intervalDays;

  }

  /**
   * @return true, if minimum access is available
   * @throws ApicWebServiceException error from web service
   */
  public boolean hasMinimumAccess() throws ApicWebServiceException {
    String acc = getApicAccessRight();
    return !CommonUtils.isEmptyString(acc) && !ApicConstants.NO_ACCESS.equals(acc);
  }


  /**
   * Checks whether current user can download artifacts from the given PIDC
   *
   * @param pidcId PIDC ID
   * @return true, if artifacts can be downloaded
   * @throws ApicWebServiceException error from web service
   */
  public boolean canDownloadArtifacts(final Long pidcId) throws ApicWebServiceException {
    boolean ret = hasApicWriteAccess();
    if (!ret) {
      NodeAccess acc = getNodeAccessRight(pidcId);
      ret = (acc != null) && acc.isRead();
    }
    return ret;
  }

  /**
   * Checks whether current user can export a2l with work package
   *
   * @param pidcId PIDC ID
   * @return true, if a2l can be exported
   * @throws ApicWebServiceException error from web service
   */
  public boolean canExportA2L(final Long pidcId) throws ApicWebServiceException {
    return canDownloadArtifacts(pidcId);
  }

  /**
   * Check whether current user has access to read compli review files. The user with this access can use Download and
   * Import functionality in Compliance Review Dialog
   *
   * @return true if user has WRITE access and CMPLI_RVW_READ node access
   * @throws ApicWebServiceException error from web service
   */
  public boolean canReadCompliReviewFiles() throws ApicWebServiceException {
    Long nodeId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.COMPLI_REPORT_ACCESS_NODE_ID));
    NodeAccess nodeAccess = getNodeAccessRight(nodeId);
    return hasApicWriteAccess() || (null != nodeAccess);
  }

  /**
   * Check if the user has Special Admin access
   *
   * @return TRUE if the user has Admin Node access
   * @throws ApicWebServiceException error during service call
   */
  public boolean hasSpecialAdminAccess() throws ApicWebServiceException {
    Long adminNodeId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ADMIN_ACCESS_NODE_ID));
    return hasNodeReadAccess(adminNodeId);
  }


  /**
   * Check if current user has 100% CDFx delivery creation privileges
   *
   * @param pidcId PIDC ID
   * @return true if privileges are available
   * @throws ApicWebServiceException data retrieval error from WS
   */
  public boolean canPerformCdfxDelivery(final Long pidcId) throws ApicWebServiceException {
    return hasNodeReadAccess(pidcId) || hasApicWriteAccess();
  }

  /**
   * @return boolean whether the user can access citi tool hotline support True - If the citi option is configured as
   *         'A' - visible to all in DB (or) If the citi option is configures as 'P' - power users in DB and the current
   *         users has APIC write access False- If the citi option is configures as 'L' - Limited in DB (or) If the citi
   *         option is configures as 'P' - power users in DB and the user does not have APIC write access
   * @throws ApicWebServiceException Exception from service
   */
  public boolean canAccessCitiToolHotline() throws ApicWebServiceException {
    String citiBotVisibility = new CommonDataBO().getParameterValue(CommonParamKey.ICDM_TOOL_SUPPORT_VISIBILITY);

    return ApicConstants.CITI_ACCESS_VISIBLE.equals(citiBotVisibility) ||
        (hasApicWriteAccess() && ApicConstants.CITI_ACCESS_LIMITED.equals(citiBotVisibility));
  }

  /**
   * @throws ApicWebServiceException exception
   */
  public void clearCurrentUserCacheADGroupDelete() throws ApicWebServiceException {
    GeneralClientCache.getInstance().refreshCurrentUserCache();
  }
}
