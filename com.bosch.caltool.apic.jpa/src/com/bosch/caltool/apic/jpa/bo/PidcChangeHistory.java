/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.AttributeValue.CLEARING_STATUS;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TPidcChangeHistory;


/**
 * PidcChangeHistory.java, This class is the business object of T_PIDC_CHANGE_HISTORY table
 *
 * @author dmo5cob
 */

public class PidcChangeHistory implements Comparable<PidcChangeHistory> {

  /**
   * History entity
   */
  private final TPidcChangeHistory historyRecord;

  /**
   * Constructor
   *
   * @param historyRecord historyRecord entity
   */
  public PidcChangeHistory(final TPidcChangeHistory historyRecord) {
    this.historyRecord = historyRecord;
  }

  /**
   * @return PIDC ID
   */
  public Long getID() {
    return this.historyRecord.getId();
  }

  /**
   * @return PIDC ID
   */
  public Long getPidcID() {
    return this.historyRecord.getPidcId();
  }

  /**
   * @return VAR ID
   */
  public Long getVarID() {
    return this.historyRecord.getVarId();
  }

  /**
   * @return SVAR ID
   */
  public Long getSubVarID() {
    return this.historyRecord.getSvarId();
  }

  /**
   * @return Attr ID
   */
  public Long getAttrID() {
    return this.historyRecord.getAttrId();
  }

  /**
   * @return project revision ID
   */
  public Long getPIDCVersID() {
    return this.historyRecord.getPidcVersId();
  }

  /**
   * @return changed Date
   */
  public Calendar getChangedDate() {
    return ApicUtil.timestamp2calendar(this.historyRecord.getChangedDate());
  }

  /**
   * @return changed Date
   */
  public Calendar getOldFmVersRvwDate() {
    return ApicUtil.timestamp2calendar(this.historyRecord.getOldFmVersRvwDate());
  }

  /**
   * @return changed Date
   */
  public Calendar getNewFmVersRvwDate() {
    return ApicUtil.timestamp2calendar(this.historyRecord.getNewFmVersRvwDate());
  }

  /**
   * @return changed User
   */
  public String getChangedUser() {
    return this.historyRecord.getChangedUser();
  }

  /**
   * @return version
   */
  public Long getVersion() {
    return this.historyRecord.getVersion();
  }

  /**
   * @return pidc version
   */
  public Long getPidcVersion() {
    return this.historyRecord.getPidcVersion();
  }

  /**
   * @return pidc Version version
   */
  public Long getPidcVersVersion() {
    return this.historyRecord.getPidcVerVersion();
  }

  /**
   * @return old value id
   */
  public Long getOldValueId() {
    if (this.historyRecord.getTabvAttrOldValue() != null) {
      return this.historyRecord.getTabvAttrOldValue().getValueId();
    }

    return null;
  }

  /**
   * @return new value id
   */
  public Long getNewValueId() {
    if (this.historyRecord.getTabvAttrNewValue() != null) {
      return this.historyRecord.getTabvAttrNewValue().getValueId();
    }

    return null;
  }

  /**
   * @return New Used
   */
  public String getNewUsed() {
    return this.historyRecord.getNewUsed();
  }

  /**
   * @return Old Used
   */
  public String getOldUsed() {
    return this.historyRecord.getOldUsed();
  }


  /**
   * @return Old focus matrix
   */
  // ICDM-2279
  public String getOldFocusMatrix() {
    return this.historyRecord.getOldFocusMatrix();
  }

  /**
   * @return new focus matrix
   */
  // ICDM-2279
  public String getNewFocusMatrix() {
    return this.historyRecord.getNewFocusMatrix();
  }

  /**
   * @return Old transfer vcdm
   */
  // ICDM-2279
  public String getOldTransferVcdm() {
    return this.historyRecord.getOldTransferVcdm();
  }

  /**
   * @return Old transfer vcdm
   */
  // ICDM-2279
  public String getNewTransferVcdm() {
    return this.historyRecord.getNewTransferVcdm();
  }

  /**
   * @return Old Part Number
   */
  public String getOldPartNumber() {
    return this.historyRecord.getOldPartNumber();
  }

  /**
   * @return New Part Number
   */
  public String getNewPartNumber() {
    return this.historyRecord.getNewPartNumber();
  }

  /**
   * @return Old Spec Link
   */
  public String getOldSpecLink() {
    return this.historyRecord.getOldSpecLink();
  }

  /**
   * @return New Spec Link
   */
  public String getNewSpecLink() {
    return this.historyRecord.getNewSpecLink();
  }

  /**
   * @return New Description
   */
  public String getNewDescrip() {
    return this.historyRecord.getNewDescription();
  }

  /**
   * @return Old Description
   */
  public String getOldDescrip() {
    return this.historyRecord.getOldDescription();
  }

  /**
   * @return New deleted flag
   */
  public String getNewDeletedflag() {
    return this.historyRecord.getNewDeletedFlag();
  }

  /**
   * @return Old deleted flag
   */
  public String getOldDeletedflag() {
    return this.historyRecord.getOldDeletedFlag();
  }


  /**
   * @return old status ID
   */
  public Long getOldStatusID() {
    return this.historyRecord.getOldStatusId();
  }

  /**
   * @return new status ID
   */
  public Long getNewStatusID() {
    return this.historyRecord.getNewStatusId();
  }


  /**
   * @return PIDC history id
   */
  public Long getPidcHistID() {
    return this.historyRecord.getId();
  }

  /**
   * @return Old isVariant
   */
  public String getOldIsVariant() {
    return this.historyRecord.getOldIsVariant();
  }


  /**
   * @return new isVariant
   */
  public String getNewisVariant() {
    return this.historyRecord.getNewIsVariant();
  }

  /**
   * @return the oldValueDescEng
   */
  public String getOldValueDescEng() {
    return this.historyRecord.getOldValueDescEng();
  }

  /**
   * @return the newValueDescEng
   */
  public String getNewValueDescEng() {
    return this.historyRecord.getNewValueDescEng();
  }

  /**
   * @return the oldValueDescGer
   */
  public String getOldValueDescGer() {
    return this.historyRecord.getOldValueDescGer();
  }

  /**
   * @return the newValueDescGer
   */
  public String getNewValueDescGer() {
    return this.historyRecord.getNewValueDescGer();
  }

  /**
   * @return the oldTextValueEng
   */
  public String getOldTextValueEng() {
    return this.historyRecord.getOldTextValueEng();
  }

  /**
   * @return the newTextValueEng
   */
  public String getNewTextValueEng() {
    return this.historyRecord.getNewTextValueEng();
  }

  /**
   * @return the oldTextValueGer
   */
  public String getOldTextValueGer() {
    return this.historyRecord.getOldTextValueGer();
  }

  /**
   * @return the newTextValueGer
   */
  public String getNewTextValueGer() {
    return this.historyRecord.getNewTextValueGer();
  }

  /**
   * @return New Value of Clearing Status
   */
  public String getNewValueClearingStatus() {
    if (this.historyRecord.getNewValueClearingStatus() != null) {
      return CLEARING_STATUS.getClearingStatus(this.historyRecord.getNewValueClearingStatus()).toString();
    }

    return null;
  }

  /**
   * @return Old Value of Clearing Status
   */
  public String getOldValueClearingStatus() {
    if (this.historyRecord.getOldValueClearingStatus() != null) {
      return CLEARING_STATUS.getClearingStatus(this.historyRecord.getOldValueClearingStatus()).toString();
    }

    return null;
  }

  // ICDM-1407
  /**
   * @return vcdm transfer history
   */
  public String getPidcAction() {
    return this.historyRecord.getPidcAction();
  }

  public Long getFmVersId() {
    return this.historyRecord.getFmVersId();
  }

  public Long getFmVersRevNum() {
    return this.historyRecord.getFmVersRevNum();
  }

  public Long getOldFmVersRvwUser() {
    return this.historyRecord.getOldFmVersRvwUser();
  }

  public Long getNewFmVersRvwUser() {
    return this.historyRecord.getNewFmVersRvwUser();
  }

  public Long getFmVersVersion() {
    return this.historyRecord.getFmVersVersion();
  }

  public Long getFmId() {
    return this.historyRecord.getFmId();
  }

  public Long getFmUcpaId() {
    return this.historyRecord.getFmUcpaId();
  }

  public Long getFmVersion() {
    return this.historyRecord.getFmVersion();
  }

  public Long getUseCaseId() {
    return this.historyRecord.getUseCaseId();
  }

  public Long getSectionId() {
    return this.historyRecord.getSectionId();
  }

  public String getOldFmVersName() {
    return this.historyRecord.getOldFmVersName();
  }

  public String getNewFmVersName() {
    return this.historyRecord.getNewFmVersName();
  }

  public String getOldFmVersStatus() {
    return this.historyRecord.getOldFmVersRvwStatus();
  }

  public String getNewFmVersStatus() {
    return this.historyRecord.getNewFmVersRvwStatus();
  }

  public String getOldFmVersLink() {
    return this.historyRecord.getOldFmVersLink();
  }

  public String getNewFmVersLink() {
    return this.historyRecord.getNewFmVersLink();
  }

  public String getOldFmVersRvwStatus() {
    return this.historyRecord.getOldFmVersRvwStatus();
  }

  public String getNewFmVersRvwStatus() {
    return this.historyRecord.getNewFmVersRvwStatus();
  }

  public String getOldFmColorCode() {
    return this.historyRecord.getOldFmColorCode();
  }

  public String getNewFmColorCode() {
    return this.historyRecord.getNewFmColorCode();
  }

  public String getOldFmComments() {
    return this.historyRecord.getOldFmComments();
  }

  public String getNewFmComments() {
    return this.historyRecord.getNewFmComments();
  }

  public String getOldFmLink() {
    return this.historyRecord.getOldFmLink();
  }

  public String getNewFmLink() {
    return this.historyRecord.getNewFmLink();
  }

  /**
   * {@inheritDoc} compare result of versions
   */
  @Override
  public int compareTo(final PidcChangeHistory arg0) {

    int compareResult = ApicUtil.compareLong(getPidcVersion(), arg0.getPidcVersion());
    if (compareResult == 0) {
      compareResult = ApicUtil.compareLong(getID(), arg0.getID());
    }
    return compareResult;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PidcChangeHistory [getPidcID()=").append(getPidcID()).append(", getVarID()=").append(getVarID())
        .append(", getSubVarID()=").append(getSubVarID()).append(", getAttrID()=").append(getAttrID())
        .append(", getPIDCVersID()=").append(getPIDCVersID()).append(", getChangedDate()=")
        .append(getChangedDate().getTime()).append(", getChangedUser()=").append(getChangedUser())
        .append(", getVersion()=").append(getVersion()).append(", getPidcVersion()=").append(getPidcVersion())
        .append(", getOldValueId()=").append(getOldValueId()).append(", getNewValueId()=").append(getNewValueId())
        .append(", getNewUsed()=").append(getNewUsed()).append(", getOldUsed()=").append(getOldUsed())
        .append(", getPidcAction()=").append(getPidcAction()).append(", getNewFocusMatrix()=")
        .append(getNewFocusMatrix()).append(", getOldFocusMatrix()=").append(getOldFocusMatrix())
        .append(", getNewTransferVcdm()=").append(getNewTransferVcdm()).append(", getOldTransferVcdm()=")
        .append(getOldTransferVcdm()).append(", getOldPartNumber()=").append(getOldPartNumber())
        .append(", getNewPartNumber()=").append(getNewPartNumber()).append(", getOldSpecLink()=")
        .append(getOldSpecLink()).append(", getNewSpecLink()=").append(getNewSpecLink()).append(", getNewDescrip()=")
        .append(getNewDescrip()).append(", getOldDescrip()=").append(getOldDescrip()).append(", getNewDeletedflag()=")
        .append(getNewDeletedflag()).append(", getOldDeletedflag()=").append(getOldDeletedflag())
        .append(", getOldStatusID()=").append(getOldStatusID()).append(", getNewStatusID()=").append(getNewStatusID())
        .append(", getOldIsVariant()=").append(getOldIsVariant()).append(", getNewisVariant()=")
        .append(getNewisVariant())

        .append(", getFmVersId()=").append(getFmVersId()).append(", getFmVersRevNum()=").append(getFmVersRevNum())
        .append(", getOldFmVersRvwUser()=").append(getOldFmVersRvwUser()).append(", getNewFmVersRvwUser()=")
        .append(getNewFmVersRvwUser()).append(", getFmVersVersion()=").append(getFmVersVersion()).append(", getFmId()=")
        .append(getFmId()).append(", getFmUcpaId()=").append(getFmUcpaId()).append(", getFmVersion()=")
        .append(getFmVersion()).append(", getUseCaseId()=").append(getUseCaseId()).append(", getSectionId()=")
        .append(getSectionId()).append(", getOldFmVersName()=").append(getOldFmVersName())
        .append(", getNewFmVersName()=").append(getNewFmVersName()).append(", getOldFmVersStatus()=")
        .append(getOldFmVersStatus()).append(", getNewFmVersStatus()=").append(getNewFmVersStatus())
        .append(", getOldFmVersLink()=").append(getOldFmVersLink()).append(", getNewFmVersLink()=")
        .append(getNewFmVersLink()).append(", getOldFmVersRvwStatus()=").append(getOldFmVersRvwStatus())
        .append(", getNewFmVersRvwStatus()=").append(getNewFmVersRvwStatus()).append(", getOldFmColorCode()=")
        .append(getOldFmColorCode()).append(", getNewFmColorCode()=").append(getNewFmColorCode())
        .append(", getOldFmComments()=").append(getOldFmComments()).append(", getNewFmComments()=")
        .append(getNewFmComments()).append(", getOldFmLink()=").append(getOldFmLink()).append(", getNewFmLink()=")
        .append(getNewFmLink()).append("]");
    return builder.toString();
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
