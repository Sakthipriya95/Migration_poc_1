/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.adapter;

import java.util.Calendar;

import com.bosch.caltool.apic.ws.PidcFocusMatrixVersType;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author svj7cob
 */
// iCDM-2614
public class PIDCChangedFMVersHistAdapterType extends PidcFocusMatrixVersType implements PIDCChangedHistAdapter {

  /**
   * history of old records
   */
  private final PidcChangeHistory histEntryOld;

  private final ServiceData serviceData;

  /**
   * @param histEntryOld
   * @param histEntryNew
   * @throws IcdmException
   */
  public PIDCChangedFMVersHistAdapterType(final PidcChangeHistory histEntryOld, final PidcChangeHistory histEntryNew,
      final ServiceData serviceData) throws IcdmException {
    super();
    this.histEntryOld = histEntryOld;
    this.serviceData = serviceData;

    this.localPidcVersChangeNum = histEntryNew.getPidcVersVers();
    this.localPidcVersId = histEntryNew.getPidcVersId();
    this.localChangeNumber = histEntryNew.getPidcVersion();

    // if null, set the fm vers id as 0 for WSDL datatype - long for local-Fm-VersId
    this.localFmVersId = (null == histEntryNew.getFmVersId()) ? 0 : histEntryNew.getFmVersId();
    this.localFmVersModifiedDate =
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryNew.getChangedDate());
    this.localFmVersModifiedUser = histEntryNew.getChangedUser();
    this.localFmVersVersion = histEntryNew.getFmVersVersion();

    setOldFmVersName(histEntryOld.getOldFmVersName());
    setNewFmVersName(histEntryNew.getNewFmVersName());

    setOldFmVersRvwUser(getUserName(histEntryOld.getOldFmVersReviewedUser()));
    setNewFmVersRvwUser(getUserName(histEntryNew.getNewFmVersReviewedUser()));

    setOldFmVersRvwDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryOld.getOldFmVersReviewedDate()));
    setNewFmVersRvwDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryNew.getNewFmVersReviewedDate()));

    setOldFmVersLink(histEntryOld.getOldFmVersLink());
    setNewFmVersLink(histEntryNew.getNewFmVersLink());

    setOldRemark(histEntryOld.getOldDescription());
    setNewRemark(histEntryNew.getNewDescription());

    setOldFmVersStatus(histEntryOld.getOldFmVersStatus());
    setNewFmVersStatus(histEntryNew.getNewFmVersStatus());


  }

  @Override
  public void setOldFmVersLink(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersLink(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersLink(param);
    }
  }

  @Override
  public void setNewFmVersLink(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersLink(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersLink(param);
    }
  }

  @Override
  public void setOldFmVersName(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersName(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersName(param);
    }
  }

  @Override
  public void setNewFmVersName(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersName(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersName(param);
    }
  }

  @Override
  public void setOldFmVersRvwUser(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersRvwUser(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersRvwUser(param);
    }
  }

  @Override
  public void setNewFmVersRvwUser(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersRvwUser(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersRvwUser(param);
    }
  }

  @Override
  public void setOldFmVersStatus(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersStatus(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersStatus(param);
    }
  }

  @Override
  public void setNewFmVersStatus(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersStatus(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersStatus(param);
    }
  }

  @Override
  public void setOldRemark(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldRemark(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldRemark(param);
    }
  }

  @Override
  public void setNewRemark(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewRemark(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewRemark(param);
    }
  }

  @Override
  public void setOldFmVersRvwDate(final Calendar param) {
    if (null == param) {
      super.setOldFmVersRvwDate(null);
      super.setOldFmVersRvwDateStr(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersRvwDate(param);
    }
  }

  @Override
  public void setNewFmVersRvwDate(final Calendar param) {
    if (null == param) {
      super.setNewFmVersRvwDate(null);
      super.setNewFmVersRvwDateStr(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersRvwDate(param);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModified() {

    return !(checkOldFmVersNameVerStatus() && checkOldFmVersRvwDetails());
  }

  /**
   * @return
   */
  private boolean checkOldFmVersRvwDetails() {
    return (CommonUtils.isEqualIgnoreNull(this.localOldFmVersRvwUser, this.localNewFmVersRvwUser)) &&
        (CommonUtils.isEqualIgnoreNull(this.localOldFmVersRvwDate, this.localNewFmVersRvwDate)) &&
        (CommonUtils.isEqualIgnoreNull(this.localOldRemark, this.localNewRemark));
  }

  /**
   * @return
   */
  private boolean checkOldFmVersNameVerStatus() {
    return (CommonUtils.isEqualIgnoreNull(this.localOldFmVersName, this.localNewFmVersName)) &&
        (CommonUtils.isEqualIgnoreNull(this.localOldFmVersLink, this.localNewFmVersLink)) &&
        (CommonUtils.isEqualIgnoreNull(this.localOldFmVersStatus, this.localNewFmVersStatus));
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  public void removeEquals() throws DataException {
    if ((this.localOldFmVersName != null) && (this.localNewFmVersName != null)) {
      if (this.localOldFmVersName.equals(this.localNewFmVersName)) {
        super.setOldFmVersName(null);
        super.setNewFmVersName(null);
      }
    }

    if ((this.localOldFmVersLink != null) && (this.localNewFmVersLink != null)) {
      if (this.localOldFmVersLink.equals(this.localNewFmVersLink)) {
        super.setOldFmVersLink(null);
        super.setNewFmVersLink(null);
      }
    }

    if ((this.localOldFmVersRvwUser != null) && (this.localNewFmVersRvwUser != null)) {
      if (this.localOldFmVersRvwUser.equals(this.localNewFmVersRvwUser)) {
        super.setOldFmVersRvwUser(null);
        super.setNewFmVersRvwUser(null);
      }
      else {
        super.setOldFmVersRvwUser(PIDCChangedHistAdapter.STRING_NOT_DEFINED.equals(this.localOldFmVersRvwUser)
            ? this.localOldFmVersRvwUser : getUserFullName(this.localOldFmVersRvwUser));
        super.setNewFmVersRvwUser(PIDCChangedHistAdapter.STRING_NOT_DEFINED.equals(this.localNewFmVersRvwUser)
            ? this.localNewFmVersRvwUser : getUserFullName(this.localNewFmVersRvwUser));
      }
    }

    if ((this.localOldFmVersRvwDate != null) && (this.localNewFmVersRvwDate != null)) {
      if (this.localOldFmVersRvwDate.equals(this.localNewFmVersRvwDate)) {
        super.setOldFmVersRvwDate(null);
        super.setNewFmVersRvwDate(null);
        super.setOldFmVersRvwDateStr(null);
        super.setNewFmVersRvwDateStr(null);
      }
      else {
        super.setOldFmVersRvwDateStr(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, this.localOldFmVersRvwDate));
        super.setNewFmVersRvwDateStr(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, this.localNewFmVersRvwDate));
      }
    }
    else {
      if (null != this.localOldFmVersRvwDate) {
        super.setOldFmVersRvwDateStr(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, this.localOldFmVersRvwDate));
      }

      if (null != this.localNewFmVersRvwDate) {
        super.setNewFmVersRvwDateStr(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, this.localNewFmVersRvwDate));
      }
    }

    if ((this.localOldFmVersStatus != null) && (this.localNewFmVersStatus != null)) {
      if (this.localOldFmVersStatus.equals(this.localNewFmVersStatus)) {
        super.setOldFmVersStatus(null);
        super.setNewFmVersStatus(null);
      }
    }

  }

  /**
   * Returns the nt-user-id of the user
   *
   * @param userId userId
   * @return the nt-user-id
   * @throws DataException
   */
  private String getUserName(final Long userId) throws DataException {
    if (null == userId) {
      return null;
    }
    UserLoader loader = new UserLoader(this.serviceData);
    return loader.getUsernameById(userId);
  }

  /**
   * Returns the full name of user
   *
   * @param ntUserId ntUserId
   * @return full name
   * @throws DataException
   */
  private String getUserFullName(final String ntUserId) throws DataException {
    if (null == ntUserId) {
      return null;
    }
    UserLoader loader = new UserLoader(this.serviceData);
    User user = loader.getDataObjectByUserName(ntUserId);
    return getFullName(user);
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @return the users fullName
   */
  private String getFullName(final User user) {
    if (null != user) { // the userID is valid, means getDbApicUser does not return null

      final StringBuilder fullName = new StringBuilder();
      if (!CommonUtils.isEmptyString(user.getLastName())) {
        fullName.append(user.getLastName()).append(", ");
      }
      if (!CommonUtils.isEmptyString(user.getFirstName())) {
        fullName.append(user.getFirstName());
      }

      if (CommonUtils.isEmptyString(fullName.toString())) {
        fullName.append(user.getName());
      }

      return fullName.toString();
    }

    return null;

  }
}