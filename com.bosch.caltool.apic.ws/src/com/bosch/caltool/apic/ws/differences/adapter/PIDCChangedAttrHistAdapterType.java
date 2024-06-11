/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.adapter;

import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public class PIDCChangedAttrHistAdapterType extends ProjectIdCardChangedAttributeType
    implements PIDCChangedHistAdapter {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;
  String oldIsVariant;
  String newIsVariant;

  public PIDCChangedAttrHistAdapterType() {
    // TO-DO
  }

  public PIDCChangedAttrHistAdapterType(final PidcChangeHistory histEntry) throws IcdmException {
    this(histEntry, histEntry);
  }

  public PIDCChangedAttrHistAdapterType(final PidcChangeHistory histEntryOld, final PidcChangeHistory histEntryNew)
      throws IcdmException {
    super();

    if (histEntryNew.getAttrId() != null) {
      setAttrID(histEntryNew.getAttrId());
    }
    else {
      setAttrID(0);
    }
    setOldUsed(histEntryOld.getOldUsed());
    setNewUsed(histEntryNew.getNewUsed());

    // ICDM-2279
    setOldFocusMatrix(histEntryOld.getOldFocusMatrixYn());
    setNewFocusMatrix(histEntryNew.getNewFocusMatrixYn());

    // ICDM-2279
    setOldTransferVcdm(histEntryOld.getOldTransferVcdmYn());
    setNewTransferVcdm(histEntryNew.getNewTransferVcdmYn());

    // ICDM-1407
    if (histEntryNew.getPidcAction() != null) {
      setPidcAction(histEntryNew.getPidcAction());
    }

    setOldValueID(histEntryOld.getOldValueId());
    setNewValueID(histEntryNew.getNewValueId());
    setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, histEntryNew.getChangedDate()));
    setModifyUser(histEntryNew.getChangedUser());
    setOldDescription(histEntryOld.getOldDescription());
    setNewDescription(histEntryNew.getNewDescription());
    setOldPartNumber(histEntryOld.getOldPartNumber());
    setNewPartNumber(histEntryNew.getNewPartNumber());
    setOldSpecLink(histEntryOld.getOldSpecLink());
    setNewSpecLink(histEntryNew.getNewSpecLink());

    // In this case it is ok that for both method calls the histEntryNew is used. The WSDL doesn't define an own column
    // for the variant information. Thus ist must be encoded in the value-ID.
    setNewIsVariant(histEntryNew.getNewIsVariant());
    super.setNewIsVariant(histEntryNew.getNewIsVariant());
    setOldIsVariant(histEntryNew.getOldIsVariant());
    super.setNewIsVariant(histEntryNew.getOldIsVariant());
    setChangeNumber(histEntryNew.getPidcVersion());
    setPidcVersionChangeNum(histEntryNew.getPidcVersVers());
    setOldValueIdClearingStatus(histEntryOld.getOldValueClearingStatus());
    setNewValueIdClearingStatus(histEntryNew.getNewValueClearingStatus());
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

  @Override
  public void setChangeNumber(final long param) {

    this.localChangeNumber = param;
  }

  @Override
  public void setPidcVersionChangeNum(final long param) {

    this.localPidcVersionChangeNum = param;
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

  @Override
  public void setOldUsed(final java.lang.String param) {

    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldUsed(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldUsed(param);
    }
  }

  // ICDM-1407
  @Override
  public void setPidcAction(final java.lang.String param) {

    if (param != null) {

      super.setPidcAction(param);
    }
  }

  // ICDM-2279
  @Override
  public void setOldFocusMatrix(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldFocusMatrix(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldFocusMatrix(param);
    }
  }

  // ICDM-2279
  @Override
  public void setNewFocusMatrix(final java.lang.String param) {

    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewFocusMatrix(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFocusMatrix(param);
    }
  }

  // ICDM-2279
  @Override
  public void setOldTransferVcdm(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldTransferVcdm(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setOldTransferVcdm(param);
    }
  }

  // ICDM-2279
  @Override
  public void setNewTransferVcdm(final java.lang.String param) {

    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewTransferVcdm(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewTransferVcdm(param);
    }
  }

  @Override
  public void setNewUsed(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewUsed(PIDCChangedHistAdapter.STRING_NOT_DEFINED);
    }
    else {
      super.setNewUsed(param);
    }
  }

  public void setOldValueID(final Long param) {
    if ((param != null) && (param.longValue() != 0)) {
      super.setOldValueID(param);
    }
  }

  public void setNewValueID(final Long param) {
    if ((param != null) && (param.longValue() != 0)) {
      super.setNewValueID(param);
    }
  }

  public void setOldValueIDClearStat(final String param) {
    if (param != null) {
      super.setOldValueIdClearingStatus(param);
    }
  }

  public void setNewValueIDClearStat(final String param) {
    if (param != null) {
      super.setNewValueIdClearingStatus(param);
    }
  }

  @Override
  public void setNewDescription(final java.lang.String param) {
    if (param != null) {
      super.setNewDescription(param);
    }
  }

  @Override
  public void setOldDescription(final java.lang.String param) {
    if (param != null) {
      super.setOldDescription(param);
    }
  }

  @Override
  public void setNewPartNumber(final java.lang.String param) {
    if (param != null) {
      super.setNewPartNumber(param);
    }
  }

  @Override
  public void setOldPartNumber(final java.lang.String param) {
    if (param != null) {
      super.setOldPartNumber(param);
    }
  }

  @Override
  public void setNewSpecLink(final java.lang.String param) {
    if (param != null) {
      super.setNewSpecLink(param);
    }
  }

  @Override
  public void setOldSpecLink(final java.lang.String param) {
    if (param != null) {
      super.setOldSpecLink(param);
    }
  }

  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  public boolean isModified() {
    boolean flag = true;
    // Icdm-1518 - Change for Spec Link also captured here
    // ICdm-1519- Check for Old is Varaint and new is Varaint

    if (validateAttrDetails() && validateFlags() &&
        // ICDM-2279
        equalsIgnoreNull(this.oldIsVariant, this.newIsVariant)) {
      flag = false;
    }
    return flag;
  }

  /**
   * @return
   */
  private boolean validateAttrDetails() {
    return equalsIgnoreNull(this.localOldDescription, this.localNewDescription) &&
        equalsIgnoreNull(this.localOldPartNumber, this.localNewPartNumber) &&
        (this.localNewValueID == this.localOldValueID) &&
        equalsIgnoreNull(this.localNewSpecLink, this.localOldSpecLink);
  }

  /**
   * @return
   */
  private boolean validateFlags() {
    return equalsIgnoreNull(this.localOldUsed, this.localNewUsed) &&
        // ICDM-2279
        equalsIgnoreNull(this.localOldFocusMatrix, this.localNewFocusMatrix) &&
        equalsIgnoreNull(this.localOldTransferVcdm, this.localNewTransferVcdm);
  }

  public void removeEquals() {
    if ((this.localNewDescription != null) && (this.localOldDescription != null) &&
        this.localNewDescription.equals(this.localOldDescription)) {
      super.setNewDescription(null);
      super.setOldDescription(null);
    }

    if ((this.localNewSpecLink != null) && (this.localOldSpecLink != null) &&
        this.localNewSpecLink.equals(this.localOldSpecLink)) {
      super.setNewSpecLink(null);
      super.setOldSpecLink(null);
    }

    if ((this.localNewPartNumber != null) && (this.localOldPartNumber != null) &&
        this.localNewPartNumber.equals(this.localOldPartNumber)) {
      super.setNewPartNumber(null);
      super.setOldPartNumber(null);
    }


    if ((this.localNewUsed != null) && (this.localOldUsed != null) && this.localNewUsed.equals(this.localOldUsed)) {
      super.setNewUsed(PIDCChangedHistAdapter.STRING_NO_MODIFICATION);
      super.setOldUsed(PIDCChangedHistAdapter.STRING_NO_MODIFICATION);
    }

    // ICDM-1407
    if (this.localPidcAction == null) {
      super.setPidcAction(this.histEntryNew.getPidcAction());
    }

    // ICDM-2279
    if ((this.localNewFocusMatrix != null) && (this.localOldFocusMatrix != null) &&
        this.localNewFocusMatrix.equals(this.localOldFocusMatrix)) {
      super.setNewFocusMatrix(PIDCChangedHistAdapter.STRING_NO_MODIFICATION);
      super.setOldFocusMatrix(PIDCChangedHistAdapter.STRING_NO_MODIFICATION);
    }

    // ICDM-2279
    if ((this.localNewTransferVcdm != null) && (this.localOldTransferVcdm != null) &&
        this.localNewTransferVcdm.equals(this.localOldTransferVcdm)) {
      super.setNewTransferVcdm(PIDCChangedHistAdapter.STRING_NO_MODIFICATION);
      super.setOldTransferVcdm(PIDCChangedHistAdapter.STRING_NO_MODIFICATION);
    }

    if (this.localNewValueID == this.localOldValueID) {
      super.setOldValueID(PIDCChangedHistAdapter.LONG_NO_MODIFICATION);
      super.setNewValueID(PIDCChangedHistAdapter.LONG_NO_MODIFICATION);
    }


    if (!equalsIgnoreNull(getOldIsVariant(), getNewIsVariant())) {
      if (checkVarInfo() ||
          ((getNewIsVariant() == null) && ("Y".equals(getOldIsVariant())) && (this.histEntryNew.getSvarId() != null))) {

        super.setLevel("Moved to Variant level");

      }
      else if ((getNewIsVariant() != null) && "N".equals(getNewIsVariant()) && (null == getOldIsVariant()) &&
          (this.histEntryNew.getSvarId() == null)) {
        if (!((this.histEntryNew.getAttrId() != null) && (this.histEntryNew.getPidcId() != null) &&
            (this.histEntryNew.getVarId() == null))) {
          super.setLevel("Moved from PIDC level");

        }
      }

      else if ((getNewIsVariant() != null) && "N".equals(getNewIsVariant()) && ("Y".equals(getOldIsVariant())) &&
          (this.histEntryNew.getSvarId() == null)) {
        super.setLevel("Moved from variant level");

      }

      else if ((getNewIsVariant() == null) && ("N".equals(getOldIsVariant())) &&
          (this.histEntryNew.getSvarId() == null)) {
        super.setLevel("Moved to PIDC level");

      }


      else if ((getOldIsVariant() == null) && ("Y".equals(getNewIsVariant())) &&
          (this.histEntryNew.getSvarId() != null)) {
        super.setLevel("Moved from Variant level");
      }
    }


  }

  /**
   * @return
   */
  private boolean checkVarInfo() {
    return (getNewIsVariant() != null) && "Y".equals(getNewIsVariant()) && "N".equals(getOldIsVariant()) &&
        (this.histEntryNew.getSvarId() == null);
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
   * @return the oldIsVariant
   */
  @Override
  public String getOldIsVariant() {
    return this.oldIsVariant;
  }


  /**
   * @param oldIsVariant the oldIsVariant to set
   */
  @Override
  public void setOldIsVariant(final String oldIsVariant) {
    this.oldIsVariant = oldIsVariant;
  }


  /**
   * @return the newIsVariant
   */
  @Override
  public String getNewIsVariant() {
    return this.newIsVariant;
  }


  /**
   * @param newIsVariant the newIsVariant to set
   */
  @Override
  public void setNewIsVariant(final String newIsVariant) {
    this.newIsVariant = newIsVariant;
  }

}
