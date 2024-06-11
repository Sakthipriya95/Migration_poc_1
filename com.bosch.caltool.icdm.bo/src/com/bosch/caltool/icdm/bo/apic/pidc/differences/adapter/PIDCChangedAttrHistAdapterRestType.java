/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;


/**
 * @author imi2si
 */
public class PIDCChangedAttrHistAdapterRestType extends PidcChangedAttrType implements PIDCChangedHistAdapterRest {

  PidcChangeHistory histEntryOld;
  PidcChangeHistory histEntryNew;
  String oldIsVariant;
  String newIsVariant;

  /**
   *
   */
  public PIDCChangedAttrHistAdapterRestType() {
    // TO-DO
  }

  /**
   * @param histEntry pidc change history
   */
  public PIDCChangedAttrHistAdapterRestType(final PidcChangeHistory histEntry) {
    this(histEntry, histEntry);
  }

  /**
   * @param histEntryOld old pidc change history entry
   * @param histEntryNew new pidc change history entry
   */
  public PIDCChangedAttrHistAdapterRestType(final PidcChangeHistory histEntryOld,
      final PidcChangeHistory histEntryNew) {
    super();

    if (histEntryNew.getAttrId() != null) {
      setAttrId(histEntryNew.getAttrId());
    }
    else {
      setAttrId(0L);
    }
    setOldUsed(histEntryOld.getOldUsed());
    setNewUsed(histEntryNew.getNewUsed());

    // ICDM-2279
    setOldFocusMatrix(histEntryOld.getOldFocusMatrixYn());
    setNewFocusMatrix(histEntryNew.getNewFocusMatrixYn());

    // ICDM-2279
    setOldTranferVcdm(histEntryOld.getOldTransferVcdmYn());
    setNewTranferVcdm(histEntryNew.getNewTransferVcdmYn());

    // ICDM-1407
    if (histEntryNew.getPidcAction() != null) {
      setPidcAction(histEntryNew.getPidcAction());
    }

    setOldValueID(histEntryOld.getOldValueId());
    setNewValueID(histEntryNew.getNewValueId());
    setModifyDate(histEntryNew.getChangedDate());
    setModifyUser(histEntryNew.getChangedUser());
    setOldDesc(histEntryOld.getOldDescription());
    setNewDesc(histEntryNew.getNewDescription());
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
    setPidcVersChangeNum(histEntryNew.getPidcVersVers());
    setOldValueIdClearingStatus(histEntryOld.getOldValueClearingStatus());
    setNewValueIdClearingStatus(histEntryNew.getNewValueClearingStatus());
    // Change the Pro Rev Id to Pidc Version ID.
    if (histEntryNew.getPidcVersId() == null) {
      super.setPidcVers(0L);
    }
    else {
      super.setPidcVers(histEntryNew.getPidcVersId());
    }
    this.histEntryOld = histEntryOld;
    this.histEntryNew = histEntryNew;
  }

  /**
   * @param param date
   */
  public void setModifyDate(final Calendar param) {
    if (param != null) {
      Date date = param.getTime();
      SimpleDateFormat dateFormat =
          new SimpleDateFormat(DateFormat.DATE_FORMAT_12, Locale.getDefault(Locale.Category.FORMAT));
      String strDate = dateFormat.format(date);

      super.setModifyDate(strDate);
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
      super.setOldUsed(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
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
      super.setOldFocusMatrix(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
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
      super.setNewFocusMatrix(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewFocusMatrix(param);
    }
  }

  // ICDM-2279
  @Override
  public void setOldTranferVcdm(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setOldTranferVcdm(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setOldTranferVcdm(param);
    }
  }

  // ICDM-2279
  @Override
  public void setNewTranferVcdm(final java.lang.String param) {

    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewTranferVcdm(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewTranferVcdm(param);
    }
  }

  @Override
  public void setNewUsed(final java.lang.String param) {
    if (param == null) {
      // Can't be null due to WSDL specification
      super.setNewUsed(PIDCChangedHistAdapterRest.STRING_NOT_DEFINED);
    }
    else {
      super.setNewUsed(param);
    }
  }

  /**
   * @param param old value id
   */
  public void setOldValueID(final Long param) {
    if ((param != null) && (param.longValue() != 0)) {
      super.setOldValueId(param);
    }
  }

  /**
   * @param param new value id
   */
  public void setNewValueID(final Long param) {
    if ((param != null) && (param.longValue() != 0)) {
      super.setNewValueId(param);
    }
  }

  /**
   * @param param old value clearing status
   */
  public void setOldValueIDClearStat(final String param) {
    if (param != null) {
      super.setOldValueIdClearingStatus(param);
    }
  }

  /**
   * @param param new value clearing status
   */
  public void setNewValueIDClearStat(final String param) {
    if (param != null) {
      super.setNewValueIdClearingStatus(param);
    }
  }

  @Override
  public void setNewDesc(final java.lang.String param) {
    if (param != null) {
      super.setNewDesc(param);
    }
  }

  @Override
  public void setOldDesc(final java.lang.String param) {
    if (param != null) {
      super.setOldDesc(param);
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

  @Override
  public PidcChangeHistory getHistEntryOld() {
    return this.histEntryOld;
  }

  @Override
  public boolean isModified() {
    boolean flag = true;
    // Icdm-1518 - Change for Spec Link also captured here
    // ICdm-1519- Check for Old is Varaint and new is Varaint
    if (isDescPartNumUsedFlagEqual() && isFmVcdmFlagEqual() && isValSpeclinkVarFlagEqual()) {
      flag = false;
    }
    return flag;
  }

  /**
   * @return
   */
  private boolean isValSpeclinkVarFlagEqual() {
    return equalsIgnoreNull(super.getNewValueId(), super.getOldValueId()) &&
        equalsIgnoreNull(super.getNewSpecLink(), super.getOldSpecLink()) &&
        equalsIgnoreNull(this.oldIsVariant, this.newIsVariant);
  }

  /**
   * @return
   */
  private boolean isFmVcdmFlagEqual() {
    return equalsIgnoreNull(super.getOldFocusMatrix(), super.getNewFocusMatrix()) &&
        equalsIgnoreNull(super.getOldTranferVcdm(), super.getNewTranferVcdm());
  }

  /**
   * @return
   */
  private boolean isDescPartNumUsedFlagEqual() {
    return equalsIgnoreNull(super.getOldDesc(), super.getNewDesc()) &&
        equalsIgnoreNull(super.getOldPartNumber(), super.getNewPartNumber()) &&
        equalsIgnoreNull(super.getOldUsed(), super.getNewUsed());
  }

  @Override
  public void removeEquals() {
    setDescNull();
    setSpecLinkNull();
    setPartNumberNull();
    setUsedFlagNoModification();

    // ICDM-1407
    if (super.getPidcAction() == null) {
      super.setPidcAction(this.histEntryNew.getPidcAction());
    }

    setFocusMatrixNoModification();
    setTransferToVcdmNoModification();
    setValueIdNoModification();
    setLevel();
  }

  /**
   *
   */
  private void setLevel() {
    if (!equalsIgnoreNull(getOldIsVariant(), getNewIsVariant())) {
      if (isNewIsVarYOldIsVarN() || isOldIsVarY()) {
        super.setLevel("Moved to Variant level");
      }
      else {
        setLevelInfo();
      }
    }
  }

  /**
   * @return
   */
  private boolean isOldIsVarY() {
    return (getNewIsVariant() == null) && (ApicConstants.CODE_YES.equals(getOldIsVariant())) &&
        (this.histEntryNew.getSvarId() != null);
  }

  /**
   * @return
   */
  private boolean isNewIsVarYOldIsVarN() {
    return (getNewIsVariant() != null) && ApicConstants.CODE_YES.equals(getNewIsVariant()) &&
        ApicConstants.CODE_NO.equals(getOldIsVariant()) && (this.histEntryNew.getSvarId() == null);
  }

  /**
   *
   */
  private void setLevelInfo() {
    if (validateIsVariantFlags() && (this.histEntryNew.getSvarId() == null)) {
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

  /**
   *
   */
  private void setValueIdNoModification() {
    if (equalsIgnoreNull(super.getNewValueId(), super.getOldValueId())) {
      super.setOldValueId(PIDCChangedHistAdapterRest.LONG_NO_MODIFICATION);
      super.setNewValueId(PIDCChangedHistAdapterRest.LONG_NO_MODIFICATION);
    }
  }

  /**
   *
   */
  private void setTransferToVcdmNoModification() {
    // ICDM-2279
    if ((super.getNewTranferVcdm() != null) && (super.getOldTranferVcdm() != null) &&
        super.getNewTranferVcdm().equals(super.getOldTranferVcdm())) {
      super.setNewTranferVcdm(PIDCChangedHistAdapterRest.STRING_NO_MODIFICATION);
      super.setOldTranferVcdm(PIDCChangedHistAdapterRest.STRING_NO_MODIFICATION);
    }
  }

  /**
   *
   */
  private void setFocusMatrixNoModification() {
    // ICDM-2279
    if ((super.getNewFocusMatrix() != null) && (super.getOldFocusMatrix() != null) &&
        super.getNewFocusMatrix().equals(super.getOldFocusMatrix())) {
      super.setNewFocusMatrix(PIDCChangedHistAdapterRest.STRING_NO_MODIFICATION);
      super.setOldFocusMatrix(PIDCChangedHistAdapterRest.STRING_NO_MODIFICATION);
    }
  }

  /**
   *
   */
  private void setUsedFlagNoModification() {
    if ((super.getNewUsed() != null) && (super.getOldUsed() != null) && super.getNewUsed().equals(super.getOldUsed())) {
      super.setNewUsed(PIDCChangedHistAdapterRest.STRING_NO_MODIFICATION);
      super.setOldUsed(PIDCChangedHistAdapterRest.STRING_NO_MODIFICATION);
    }
  }

  /**
   *
   */
  private void setPartNumberNull() {
    if ((super.getNewPartNumber() != null) && (super.getOldPartNumber() != null) &&
        super.getNewPartNumber().equals(super.getOldPartNumber())) {
      super.setNewPartNumber(null);
      super.setOldPartNumber(null);
    }
  }

  /**
   *
   */
  private void setSpecLinkNull() {
    if ((super.getNewSpecLink() != null) && (super.getOldSpecLink() != null) &&
        super.getNewSpecLink().equals(super.getOldSpecLink())) {
      super.setNewSpecLink(null);
      super.setOldSpecLink(null);
    }
  }

  /**
   *
   */
  private void setDescNull() {
    if ((super.getNewDesc() != null) && (super.getOldDesc() != null) && super.getNewDesc().equals(super.getOldDesc())) {
      super.setNewDesc(null);
      super.setOldDesc(null);
    }
  }

  /**
   * @return
   */
  private boolean validateIsVariantFlags() {
    return (getNewIsVariant() != null) && "N".equals(getNewIsVariant()) && (null == getOldIsVariant());
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
