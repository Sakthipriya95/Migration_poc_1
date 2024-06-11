/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;

/**
 * @author svj7cob
 */
public class PIDCChangedFMHistAdapterRestType extends com.bosch.caltool.icdm.model.apic.pidc.PidcFocusMatrixType
    implements PIDCChangedHistAdapterRest {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;

  /**
   * @param histEntryOld
   * @param histEntryNew
   * @param isFromVersion
   */
  public PIDCChangedFMHistAdapterRestType(final PidcChangeHistory histEntryOld, final PidcChangeHistory histEntryNew,
      final boolean isFromVersion) {
    super();
    this.histEntryOld = histEntryOld;
    this.histEntryNew = histEntryNew;
    super.setFmVersionId(histEntryNew.getFmVersId());
    super.setFmId(histEntryNew.getFmId());

    setOldFmColorCode(null == histEntryOld ? null : histEntryOld.getOldFmColorCode());
    setNewFmColorCode(histEntryNew.getNewFmColorCode());

    setOldFmLink(null == histEntryOld ? null : histEntryOld.getOldFmLink());
    setNewFmLink(histEntryNew.getNewFmLink());

    setOldComments(null == histEntryOld ? null : histEntryOld.getOldFmComments());
    setNewComments(histEntryNew.getNewFmComments());

    setOldDeletedFlag(null == histEntryOld ? null : histEntryOld.getOldDeletedFlag());
    setNewDeletedFlag(histEntryNew.getNewDeletedFlag());

    super.setAttrId(histEntryNew.getAttrId());

    super.setModifiedDate(histEntryNew.getChangedDate());
    super.setModifiedUser(histEntryNew.getChangedUser());

    super.setFmVersion(histEntryNew.getFmVersion());
    super.setPidcVersChangeNumber(histEntryNew.getPidcVersVers());
    super.setPidcVersId(histEntryNew.getPidcVersId());
    super.setChangeNumber(histEntryNew.getPidcVersion());

    super.setUseCaseId(histEntryNew.getUseCaseId());
    super.setSectionId(histEntryNew.getSectionId());
  }

  @Override
  public void setOldFmColorCode(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmColorCode(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmColorCode(param);
    }
  }

  @Override
  public void setNewFmColorCode(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmColorCode(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmColorCode(param);
    }
  }

  @Override
  public void setOldFmLink(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFmLink(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFmLink(param);
    }
  }

  @Override
  public void setNewFmLink(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFmLink(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFmLink(param);
    }
  }

  @Override
  public void setOldComments(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldComments(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldComments(param);
    }
  }

  @Override
  public void setNewComments(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewComments(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
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
    if (CommonUtils.isEqualIgnoreNull(super.getOldFmColorCode(), super.getNewFmColorCode()) &&
        (CommonUtils.isEqualIgnoreNull(super.getOldFmLink(), super.getNewFmLink())) &&
        (CommonUtils.isEqualIgnoreNull(super.getOldComments(), super.getNewComments())) &&
        (isEqualRelevancy(super.getOldDeletedFlag(), super.getNewDeletedFlag()))) {
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
    if ((super.getOldFmColorCode() != null) && (super.getNewFmColorCode() != null) &&
        super.getOldFmColorCode().equals(super.getNewFmColorCode())) {
      super.setOldFmColorCode(null);
      super.setNewFmColorCode(null);
    }

    if ((super.getOldFmLink() != null) && (super.getNewFmLink() != null) &&
        super.getOldFmLink().equals(super.getNewFmLink())) {
      super.setOldFmLink(null);
      super.setNewFmLink(null);
    }

    if ((super.getOldComments() != null) && (super.getNewComments() != null) &&
        super.getOldComments().equals(super.getNewComments())) {
      super.setOldComments(null);
      super.setNewComments(null);
    }
  }
}
