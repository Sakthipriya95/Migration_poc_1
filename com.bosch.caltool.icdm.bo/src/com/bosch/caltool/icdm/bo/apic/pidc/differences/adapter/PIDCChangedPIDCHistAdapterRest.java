/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter;

import java.util.Calendar;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;


/**
 * @author imi2si
 */
public class PIDCChangedPIDCHistAdapterRest extends PidcDiffsResponseType
    implements PIDCChangedHistAdapterRest, Comparable<PIDCChangedPIDCHistAdapterRest> {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * @return the histEntryNew
   */
  public PidcChangeHistory getHistEntryNew() {
    return this.histEntryNew;
  }

  /**
   *
   */
  public PIDCChangedPIDCHistAdapterRest() {
    // TO-DO
  }

  /**
   * @param histEntry pidc changes history
   */
  public PIDCChangedPIDCHistAdapterRest(final PidcChangeHistory histEntry) {
    this(histEntry, histEntry, false);
  }

  /**
   * @param histEntryOld old pic history
   * @param histEntryNew new pidc history
   * @param fromVersion is change from version
   */
  public PIDCChangedPIDCHistAdapterRest(final PidcChangeHistory histEntryOld, final PidcChangeHistory histEntryNew,
      final boolean fromVersion) {
    super();

    super.setPidcId(histEntryNew.getPidcId());
    if (fromVersion) {
      super.setOldChangeNumber(histEntryOld.getPidcVersVers());
      super.setNewChangeNumber(histEntryNew.getPidcVersVers());
    }
    else {
      super.setOldChangeNumber(histEntryOld.getPidcVersion());
      super.setNewChangeNumber(histEntryNew.getPidcVersion());
    }
    // set the PIDC version number
    setOldPidcVersionNumber(histEntryOld.getVersion());
    setNewPidcVersionNumber(histEntryNew.getVersion());

    // set the PIDC status
    setOldPidcStatus(String.valueOf(histEntryOld.getOldStatusId()));
    setNewPidcStatus(String.valueOf(histEntryOld.getNewStatusId()));

    // set the PIDC modification information
    setModifiedDate(histEntryNew.getChangedDate());
    setModifiedUser(histEntryNew.getChangedUser());

    setOldIsDeleted(histEntryOld.getOldDeletedFlag());
    setNewIsDeleted(histEntryNew.getNewDeletedFlag());
    // Change the Pro Rev Id to Pidc Version ID.
    super.setPidcVersion(histEntryNew.getPidcVersId());
    this.histEntryOld = histEntryOld;
    this.histEntryNew = histEntryNew;
  }

  /**
   * @param param date
   */
  public void setModifiedDate(final Calendar param) {
    if (param != null) {
      super.setModifiedDate(DateFormat.formatDateToString(param.getTime(), DateFormat.DATE_FORMAT_12));
    }
  }

  @Override
  public void setModifiedUser(final java.lang.String param) {
    if (param != null) {
      super.setModifiedUser(param);
    }
  }

  @Override
  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  @Override
  public boolean isModified() {

    return !(isChangeNumVersNumSame() && equalsIgnoreNull(getNewPidcStatus(), getOldPidcStatus()) &&
        isPidcChgdAttrVarTypeNull());
  }

  /**
   * @return
   */
  private boolean isChangeNumVersNumSame() {
    return (getNewChangeNumber().equals(getOldChangeNumber())) &&
        getNewPidcVersionNumber().equals(getOldPidcVersionNumber());
  }

  /**
   * @return
   */
  private boolean isPidcChgdAttrVarTypeNull() {
    return CommonUtils.isNull(getPidcChangedAttrTypeList()) && CommonUtils.isNull(getPidcChangedVariantTypeList());
  }

  @Override
  public void removeEquals() {

    if (equalsIgnoreNull(getNewPidcStatus(), getOldPidcStatus())) {
      super.setNewPidcStatus("");
      super.setOldPidcStatus("");
    }

  }

  private boolean equalsIgnoreNull(final String val1, final String val2) {
    if ((val1 == null) && (val2 == null)) {
      return true;
    }

    if ((val1 != null) && (val2 != null)) {
      return val1.equals(val2);
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCChangedPIDCHistAdapterRest pidcHistoryAdapter) {
    return pidcHistoryAdapter.getHistEntryNew().getPidcVersion().compareTo(this.histEntryNew.getPidcVersion());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    PIDCChangedPIDCHistAdapterRest pidcHistoryAdapter = (PIDCChangedPIDCHistAdapterRest) obj;
    return pidcHistoryAdapter.getHistEntryNew().getPidcVersion() == this.histEntryNew.getPidcVersion();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) +
        ((this.histEntryNew.getPidcVersion() == null) ? 0 : this.histEntryNew.getPidcVersion().hashCode());
    return result;
  }
}
