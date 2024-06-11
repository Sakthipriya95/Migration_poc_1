/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter;

import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedVariantType;


public class PIDCChangedVariantHistAdapterRestType extends PidcChangedVariantType
    implements PIDCChangedHistAdapterRest {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;

  String oldIsDeleted;
  String newIsDeleted;
  String oldDesc;
  String newDesc;

  public PIDCChangedVariantHistAdapterRestType() {
    // TO-DO
  }

  public PIDCChangedVariantHistAdapterRestType(final PidcChangeHistory histEntry) {
    this(histEntry, histEntry);
  }

  public PIDCChangedVariantHistAdapterRestType(final PidcChangeHistory histEntryOld,
      final PidcChangeHistory histEntryNew) {
    super();

    setVariantId(histEntryOld.getVarId());
    setOldValueID(histEntryOld.getOldValueId());
    setNewValueID(histEntryNew.getNewValueId());
    setOldIsDeleted(histEntryOld.getOldDeletedFlag());
    setNewIsDeleted(histEntryNew.getNewDeletedFlag());
    setModifiedDate(histEntryNew.getChangedDate());
    setModifiedUser(histEntryNew.getChangedUser());

    setNewChangeNumber(histEntryNew.getPidcVersion());
    setOldChangeNumber(histEntryNew.getPidcVersion());
    setPidcVersChangeNumber(histEntryNew.getPidcVersVers());

    setOldTextValueEng(histEntryNew.getOldTextvalueEng());
    setOldTextValueGer(histEntryNew.getOldTextvalueGer());
    setNewTextValueEng(histEntryNew.getNewTextvalueEng());
    setNewTextValueGer(histEntryNew.getNewTextvalueGer());


    if ((histEntryNew.getNewTextvalueEng() == null) && (histEntryNew.getOldTextvalueEng() == null)) {
      this.newIsDeleted = histEntryNew.getNewDeletedFlag();
      this.oldIsDeleted = histEntryNew.getOldDeletedFlag();
    }

    setNewDesc(histEntryNew.getNewTextvalueEng());
    setOldDesc(histEntryNew.getOldTextvalueEng());
    // Change the Pro Rev Id to Pidc Version ID.
    super.setPidcVersion(histEntryNew.getPidcVersId());
    this.histEntryOld = histEntryOld;
    this.histEntryNew = histEntryNew;
  }

  public void setModifyDate(final Date param) {
    if (param != null) {
      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.setTime(param);

      super.setModifiedDate(DateFormat.formatDateToString(cal.getTime(), DateFormat.DATE_FORMAT_12));
    }
  }

  @Override
  public void setModifiedUser(final java.lang.String param) {
    if (param != null) {
      super.setModifiedUser(param);
    }
  }

  public void setOldValueID(final Long param) {
    if (param != null) {
      super.setOldValueId(param);
    }
  }

  public void setNewValueID(final Long param) {
    if (param != null) {
      super.setNewValueId(param);
    }
  }

  /**
   * @return the oldIsDeleted
   */
  @Override
  public String getOldIsDeleted() {
    return this.oldIsDeleted;
  }

  /**
   * @param param the oldIsDeleted to set
   */
  @Override
  public void setOldIsDeleted(final String param) {
    this.oldIsDeleted = param;
  }

  /**
   * @param param the newIsDeleted to set
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
   * @param param the newDesc to set
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
   * @param param the oldDesc to set
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

  @Override
  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  public boolean isModified() {
    boolean flag = true;
    if (equalsIgnoreNull(super.getNewValueId(), super.getOldValueId())) {
      flag = false;
    }
    return flag;
  }

  public void removeEquals() {

    if (equalsIgnoreNull(super.getNewValueId(), super.getOldValueId())) {
      super.setOldValueId(PIDCChangedHistAdapterRest.LONG_NO_MODIFICATION);
      super.setNewValueId(PIDCChangedHistAdapterRest.LONG_NO_MODIFICATION);
    }

    // Consider setting the deleted flag
    if (!equalsIgnoreNull(getNewIsDeleted(), getOldIsDeleted())) {
      if ((getNewIsDeleted() != null) && "Y".equals(getNewIsDeleted())) {
        super.setNewValueId(-1L);
      }

      if ((getOldIsDeleted() != null) && "Y".equals(getOldIsDeleted())) {
        super.setOldValueId(-1L);
      }
    }

    // Consider change of description
    if (((getNewDesc() != null) || (getOldDesc() != null)) && (this.histEntryOld.getNewValueId() != null)) {
      super.setNewValueId(this.histEntryOld.getNewValueId());
    }
  }

  private boolean equalsIgnoreNull(final Object val1, final Object val2) {
    if ((val1 == null) && (val2 == null)) {
      return true;
    }

    if ((val1 != null) && (val2 != null)) {
      return val1.equals(val2);
    }

    return false;
  }
}
