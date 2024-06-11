/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFocusMatrixVersType;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author svj7cob
 */
// iCDM-2614
public class PIDCChangedFMVersHistAdapterRestType extends PidcFocusMatrixVersType
    implements PIDCChangedHistAdapterRest {

  /**
   * history of old records
   */
  private final PidcChangeHistory histEntryOld;

  private final ServiceData serviceData;

  /**
   * @param histEntryOld old pidc change history
   * @param histEntryNew new pidc change history
   * @param serviceData service data
   * @throws DataException Exception in retrieving user name
   */
  public PIDCChangedFMVersHistAdapterRestType(final PidcChangeHistory histEntryOld,
      final PidcChangeHistory histEntryNew, final ServiceData serviceData) throws DataException {
    super();
    this.histEntryOld = histEntryOld;
    this.serviceData = serviceData;

    super.setPidcVersChangenumber(histEntryNew.getPidcVersVers());
    super.setPidcVersId(histEntryNew.getPidcVersId());
    super.setChangeNumber(histEntryNew.getPidcVersion());

    // if null, set the fm vers id as 0 for WSDL datatype - long for local-Fm-VersId
    super.setFmVersId(histEntryNew.getFmVersId());
    super.setFmVersModifiedDate(histEntryNew.getChangedDate());
    super.setFmVersModifiedUser(histEntryNew.getChangedUser());
    super.setFmVersVersion(histEntryNew.getFmVersVersion());

    setOldFmVersName(histEntryOld.getOldFmVersName());
    setNewFmVersName(histEntryNew.getNewFmVersName());

    setOldFmVersRvwUser(getUserName(histEntryOld.getOldFmVersReviewedUser()));
    setNewFmVersRvwUser(getUserName(histEntryNew.getNewFmVersReviewedUser()));

    setOldFmVersRvwDate(histEntryNew.getOldFmVersReviewedDate());
    setNewFmVersRvwDate(histEntryNew.getNewFmVersReviewedDate());

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
      super.setOldFmVersLink(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersLink(param);
    }
  }

  @Override
  public void setNewFmVersLink(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersLink(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersLink(param);
    }
  }

  @Override
  public void setOldFmVersName(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersName(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersName(param);
    }
  }

  @Override
  public void setNewFmVersName(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersName(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersName(param);
    }
  }

  @Override
  public void setOldFmVersRvwUser(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersRvwUser(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersRvwUser(param);
    }
  }

  @Override
  public void setNewFmVersRvwUser(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersRvwUser(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersRvwUser(param);
    }
  }

  @Override
  public void setOldFmVersStatus(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmVersStatus(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersStatus(param);
    }
  }

  @Override
  public void setNewFmVersStatus(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmVersStatus(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmVersStatus(param);
    }
  }

  @Override
  public void setOldRemark(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldRemark(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldRemark(param);
    }
  }

  @Override
  public void setNewRemark(final String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewRemark(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewRemark(param);
    }
  }

  @Override
  public void setOldFmVersRvwDate(final String param) {
    if (null == param) {
      super.setOldFmVersRvwDate(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmVersRvwDate(param);
    }
  }

  @Override
  public void setNewFmVersRvwDate(final String param) {
    if (null == param) {
      super.setNewFmVersRvwDate(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
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
    boolean flag = true;
    if (isFmVersNameLinkStatusEqual() && isFmVersRvwDetailsEqual() &&
        (CommonUtils.isEqualIgnoreNull(super.getOldRemark(), super.getNewRemark()))) {
      flag = false;
    }
    return flag;
  }

  /**
   * @return
   */
  private boolean isFmVersRvwDetailsEqual() {
    return (CommonUtils.isEqualIgnoreNull(super.getOldFmVersRvwUser(), super.getNewFmVersRvwUser())) &&
        (CommonUtils.isEqualIgnoreNull(super.getOldFmVersRvwDate(), super.getNewFmVersRvwDate()));
  }

  /**
   * @return
   */
  private boolean isFmVersNameLinkStatusEqual() {
    return (CommonUtils.isEqualIgnoreNull(super.getOldFmVersName(), super.getNewFmVersName())) &&
        (CommonUtils.isEqualIgnoreNull(super.getOldFmVersLink(), super.getNewFmVersLink())) &&
        (CommonUtils.isEqualIgnoreNull(super.getOldFmVersStatus(), super.getNewFmVersStatus()));
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException Exception in retrieving user's full name
   */
  @Override
  public void removeEquals() throws DataException {
    setFmVerNameNull();
    setFmVerLinkNull();
    setFmVerRvwUser();
    setFmVerRvwDate();
    setFmVerStatusNull();
  }

  /**
   *
   */
  private void setFmVerStatusNull() {
    if ((super.getOldFmVersStatus() != null) && (super.getNewFmVersStatus() != null) &&
        super.getOldFmVersStatus().equals(super.getNewFmVersStatus())) {
      super.setOldFmVersStatus(null);
      super.setNewFmVersStatus(null);
    }
  }

  /**
   *
   */
  private void setFmVerRvwDate() {
    if ((super.getOldFmVersRvwDate() != null) && (super.getNewFmVersRvwDate() != null)) {
      if (super.getOldFmVersRvwDate().equals(super.getNewFmVersRvwDate())) {
        super.setOldFmVersRvwDate(null);
        super.setNewFmVersRvwDate(null);
        super.setOldFmVersRvwDate(null);
        super.setNewFmVersRvwDate(null);
      }
      else {
        super.setOldFmVersRvwDate(super.getOldFmVersRvwDate());
        super.setNewFmVersRvwDate(super.getNewFmVersRvwDate());
      }
    }
    else {
      if (null != super.getOldFmVersRvwDate()) {
        super.setOldFmVersRvwDate(super.getOldFmVersRvwDate());
      }

      if (null != super.getNewFmVersRvwDate()) {
        super.setNewFmVersRvwDate(super.getNewFmVersRvwDate());
      }
    }
  }

  /**
   * @throws DataException
   */
  private void setFmVerRvwUser() throws DataException {
    if ((super.getOldFmVersRvwUser() != null) && (super.getNewFmVersRvwUser() != null)) {
      if (super.getOldFmVersRvwUser().equals(super.getNewFmVersRvwUser())) {
        super.setOldFmVersRvwUser(null);
        super.setNewFmVersRvwUser(null);
      }
      else {
        super.setOldFmVersRvwUser(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED.equals(super.getOldFmVersRvwUser())
            ? super.getOldFmVersRvwUser() : getUserFullName(super.getOldFmVersRvwUser()));
        super.setNewFmVersRvwUser(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED.equals(super.getNewFmVersRvwUser())
            ? super.getNewFmVersRvwUser() : getUserFullName(super.getNewFmVersRvwUser()));
      }
    }
  }

  /**
   *
   */
  private void setFmVerLinkNull() {
    if ((super.getOldFmVersLink() != null) && (super.getNewFmVersLink() != null) &&
        super.getOldFmVersLink().equals(super.getNewFmVersLink())) {
      super.setOldFmVersLink(null);
      super.setNewFmVersLink(null);
    }
  }

  /**
   *
   */
  private void setFmVerNameNull() {
    if ((super.getOldFmVersName() != null) && (super.getNewFmVersName() != null) &&
        super.getOldFmVersName().equals(super.getNewFmVersName())) {
      super.setOldFmVersName(null);
      super.setNewFmVersName(null);
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