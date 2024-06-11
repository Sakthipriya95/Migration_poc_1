/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.adapter;

import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.apic.ws.GetPidcDiffsResponseType;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public class PIDCChangedPIDCHistAdapter extends GetPidcDiffsResponseType
    implements PIDCChangedHistAdapter, Comparable<PIDCChangedPIDCHistAdapter> {

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

  public PIDCChangedPIDCHistAdapter() {
    // TO-DO
  }

  public PIDCChangedPIDCHistAdapter(final PidcChangeHistory histEntry) throws IcdmException {
    this(histEntry, histEntry, false);
  }

  public PIDCChangedPIDCHistAdapter(final PidcChangeHistory histEntryOld, final PidcChangeHistory histEntryNew,
      final boolean fromVersion) throws IcdmException {
    super();

    super.setPidcID(histEntryNew.getPidcId());
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
    setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryNew.getChangedDate()));
    setModifyUser(histEntryNew.getChangedUser());

    setOldIsDeleted(histEntryOld.getOldDeletedFlag());
    setNewIsDeleted(histEntryNew.getNewDeletedFlag());
    // Change the Pro Rev Id to Pidc Version ID.
    if (histEntryNew.getPidcVersId() == null) {
      super.setPidcVersion(0);
    }
    else {
      super.setPidcVersion(histEntryNew.getPidcVersId());
    }
    this.histEntryOld = histEntryOld;
    this.histEntryNew = histEntryNew;
  }

  public void setModifyDate(final Date param) {
    if (param != null) {
      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.setTime(param);

      super.setModifyDate(cal);
    }
  }

  @Override
  public void setModifyUser(final java.lang.String param) {
    if (param != null) {
      super.setModifyUser(param);
    }
  }

  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  public boolean isModified() {
    boolean flag = true;
    if ((getNewChangeNumber() == getOldChangeNumber()) && equalsIgnoreNull(getNewPidcStatus(), getOldPidcStatus()) &&
        checkPidcDetails()) {
      flag = false;
    }
    return flag;
  }

  /**
   * @return
   */
  private boolean checkPidcDetails() {
    return (getNewPidcVersionNumber() == getOldPidcVersionNumber()) && (getChangedAttributes() == null) &&
        (getChangedVariants() == null);
  }

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
  public int compareTo(final PIDCChangedPIDCHistAdapter pidcHistoryAdapter) {
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

    PIDCChangedPIDCHistAdapter pidcHistoryAdapter = (PIDCChangedPIDCHistAdapter) obj;
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
