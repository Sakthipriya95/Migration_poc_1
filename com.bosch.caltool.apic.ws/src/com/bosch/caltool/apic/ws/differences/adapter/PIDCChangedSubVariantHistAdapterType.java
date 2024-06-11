package com.bosch.caltool.apic.ws.differences.adapter;

import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


public class PIDCChangedSubVariantHistAdapterType extends ProjectIdCardChangedSubVarType
    implements PIDCChangedHistAdapter {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;

  String oldIsDeleted;
  String newIsDeleted;
  String oldDesc;
  String newDesc;

  public PIDCChangedSubVariantHistAdapterType() {
    // TO-DO
  }

  public PIDCChangedSubVariantHistAdapterType(final PidcChangeHistory histEntry) throws IcdmException {
    this(histEntry, histEntry);
  }

  public PIDCChangedSubVariantHistAdapterType(final PidcChangeHistory histEntryOld,
      final PidcChangeHistory histEntryNew) throws IcdmException {
    super();

    setSubVariantID(histEntryOld.getSvarId());
    setOldValueID(histEntryOld.getOldValueId());
    setNewValueID(histEntryNew.getNewValueId());
    setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryNew.getChangedDate()));
    setModifyUser(histEntryNew.getChangedUser());
    setOldIsDeleted(histEntryOld.getOldDeletedFlag());
    setNewIsDeleted(histEntryNew.getNewDeletedFlag());

    setNewChangeNumber(histEntryNew.getPidcVersion());
    setOldChangeNumber(histEntryNew.getPidcVersion());
    setPidcVersionChangNum(histEntryNew.getPidcVersVers());

    if ((histEntryNew.getNewTextvalueEng() == null) && (histEntryNew.getOldTextvalueEng() == null)) {
      this.newIsDeleted = histEntryNew.getNewDeletedFlag();
      this.oldIsDeleted = histEntryNew.getOldDeletedFlag();
    }

    setNewDesc(histEntryNew.getNewTextvalueEng());
    setOldDesc(histEntryNew.getOldTextvalueEng());
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

  public void setOldValueID(final Long param) {
    if (param != null) {
      super.setOldValueID(param);
    }
  }

  public void setNewValueID(final Long param) {
    if (param != null) {
      super.setNewValueID(param);
    }
  }

  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  /**
   * @return the oldIsDeleted
   */
  @Override
  public String getOldIsDeleted() {
    return this.oldIsDeleted;
  }

  /**
   * @param oldIsDeleted the oldIsDeleted to set
   */
  @Override
  public void setOldIsDeleted(final String param) {
    this.oldIsDeleted = param;
  }

  /**
   * @param newIsDeleted the newIsDeleted to set
   */
  @Override
  public void setNewIsDeleted(final String param) {
    this.newIsDeleted = param;
  }

  /**
   * @return the newIsDeleted
   */
  @Override
  public String getNewIsDeleted() {
    return this.newIsDeleted;
  }

  /**
   * @param newDesc the newDesc to set
   */
  public void setNewDesc(final String param) {
    this.newDesc = param;
  }

  /**
   * @return the newDesc
   */
  public String getNewDesc() {
    return this.newDesc;
  }

  /**
   * @param oldDesc the oldDesc to set
   */
  public void setOldDesc(final String param) {
    this.oldDesc = param;
  }

  /**
   * @return the oldDesc
   */
  public String getOldDesc() {
    return this.oldDesc;
  }

  public boolean isModified() {
    boolean flag = true;
    if (this.localNewValueID == this.localOldValueID) {
      flag = false;
    }
    return flag;
  }

  public void removeEquals() {

    if (this.localNewValueID == this.localOldValueID) {
      super.setOldValueID(PIDCChangedHistAdapter.LONG_NO_MODIFICATION);
      super.setNewValueID(PIDCChangedHistAdapter.LONG_NO_MODIFICATION);
    }

    // Consider setting the deleted flag
    if (!equalsIgnoreNull(getNewIsDeleted(), getOldIsDeleted())) {
      if ((getNewIsDeleted() != null) && getNewIsDeleted().equals(ApicConstants.CODE_YES)) {
        super.setNewValueID(-1);
      }

      if ((getOldIsDeleted() != null) && getOldIsDeleted().equals(ApicConstants.CODE_YES)) {
        super.setOldValueID(-1);
      }
    }

    // Consider change of description
    if (((getNewDesc() != null) || (getOldDesc() != null)) && (this.histEntryOld.getNewValueId() != null)) {
      super.setNewValueID(this.histEntryOld.getNewValueId());
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
}
