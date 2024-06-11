/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.adapter;

import com.bosch.caltool.apic.ws.PidcFocusMatrixType;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;

/**
 * @author svj7cob
 */
public class PIDCChangedFMHistAdapterType extends PidcFocusMatrixType implements PIDCChangedHistAdapter {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;

  public PIDCChangedFMHistAdapterType(final PidcChangeHistory histEntryOld, final PidcChangeHistory histEntryNew)
      throws IcdmException {
    super();
    this.histEntryOld = histEntryOld;
    this.histEntryNew = histEntryNew;
    this.localFmVersionId = null == histEntryNew.getFmVersId() ? 0 : histEntryNew.getFmVersId();
    this.localFmId = null == histEntryNew.getFmId() ? 0 : histEntryNew.getFmId();

    setOldFmColorCode(null == histEntryOld ? null : histEntryOld.getOldFmColorCode());
    setNewFmColorCode(histEntryNew.getNewFmColorCode());

    setOldFmLink(null == histEntryOld ? null : histEntryOld.getOldFmLink());
    setNewFmLink(histEntryNew.getNewFmLink());

    setOldComments(null == histEntryOld ? null : histEntryOld.getOldFmComments());
    setNewComments(histEntryNew.getNewFmComments());

    setOldDeletedFlag(null == histEntryOld ? null : histEntryOld.getOldDeletedFlag());
    setNewDeletedFlag(histEntryNew.getNewDeletedFlag());

    this.localAttrId = histEntryNew.getAttrId();

    this.localModifiedDate =
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryNew.getChangedDate());
    this.localModifiedUser = histEntryNew.getChangedUser();

    this.localFmVersion = histEntryNew.getFmVersion();
    this.localPidcVersChangeNum = histEntryNew.getPidcVersVers();
    this.localPidcVersId = histEntryNew.getPidcVersId();
    this.localChangeNumber = histEntryNew.getPidcVersion();

    this.localUseCaseId = histEntryNew.getUseCaseId();
    this.localSectionId = (null == histEntryNew.getSectionId()) ? 0 : histEntryNew.getSectionId();
  }

  @Override
  public void setOldFmColorCode(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmColorCode(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmColorCode(param);
    }
  }

  @Override
  public void setNewFmColorCode(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmColorCode(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmColorCode(param);
    }
  }

  @Override
  public void setOldFmLink(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmLink(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmLink(param);
    }
  }

  @Override
  public void setNewFmLink(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmLink(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmLink(param);
    }
  }

  @Override
  public void setOldComments(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldComments(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldComments(param);
    }
  }

  @Override
  public void setNewComments(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewComments(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewComments(param);
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
    if (CommonUtils.isEqualIgnoreNull(this.localOldFmColorCode, this.localNewFmColorCode) &&
        (CommonUtils.isEqualIgnoreNull(this.localOldFmLink, this.localNewFmLink)) &&
        (CommonUtils.isEqualIgnoreNull(this.localOldComments, this.localNewComments)) &&
        (isEqualRelevancy(this.localOldDeletedFlag, this.localNewDeletedFlag))) {
      flag = false;
    }
    return flag;
  }

  private boolean isEqualRelevancy(final String oldDeletedFlag, final String newDeletedFlag) {
    if ((null != oldDeletedFlag) && (null != newDeletedFlag)) {
      return oldDeletedFlag.equals(newDeletedFlag);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeEquals() {
    if ((this.localOldFmColorCode != null) && (this.localNewFmColorCode != null) &&
        this.localOldFmColorCode.equals(this.localNewFmColorCode)) {
      super.setOldFmColorCode(null);
      super.setNewFmColorCode(null);
    }

    if ((this.localOldFmLink != null) && (this.localNewFmLink != null) &&
        this.localOldFmLink.equals(this.localNewFmLink)) {
      super.setOldFmLink(null);
      super.setNewFmLink(null);
    }

    if ((this.localOldComments != null) && (this.localNewComments != null) &&
        this.localOldComments.equals(this.localNewComments)) {
      super.setOldComments(null);
      super.setNewComments(null);
    }
  }
}
