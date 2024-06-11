package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_PIDC_CHANGE_HISTORY database table. (ICDM 672)
 */
@Entity
@ReadOnly
@Table(name = "T_PIDC_CHANGE_HISTORY")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TPidcChangeHistory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long id;

  @Column(name = "ATTR_ID")
  private Long attrId;

  @Column(name = "CHANGED_DATE", nullable = false)
  private Timestamp changedDate;

  @Column(name = "CHANGED_USER")
  private String changedUser;

  @Column(name = "NEW_DELETED_FLAG")
  private String newDeletedFlag;

  // iCDM-2006
  @Column(name = "NEW_DESCRIPTION", length = 4000)
  private String newDescription;

  @Column(name = "NEW_IS_VARIANT")
  private String newIsVariant;

  @Column(name = "NEW_PART_NUMBER")
  private String newPartNumber;

  // iCDM-2006
  @Column(name = "NEW_SPEC_LINK", length = 4000)
  private String newSpecLink;

  @Column(name = "NEW_STATUS_ID")
  private Long newStatusId;

  @Column(name = "NEW_USED")
  private String newUsed;

  @Column(name = "OLD_DELETED_FLAG")
  private String oldDeletedFlag;

  // iCDM-2006
  @Column(name = "OLD_DESCRIPTION", length = 4000)
  private String oldDescription;

  @Column(name = "OLD_IS_VARIANT")
  private String oldIsVariant;

  @Column(name = "OLD_PART_NUMBER")
  private String oldPartNumber;

  // iCDM-2006
  @Column(name = "OLD_SPEC_LINK", length = 4000)
  private String oldSpecLink;

  @Column(name = "OLD_STATUS_ID")
  private Long oldStatusId;

  @Column(name = "OLD_USED")
  private String oldUsed;

  @Column(name = "PIDC_ID")
  private Long pidcId;

  @Column(name = "PIDC_VERS_ID", nullable = false, precision = 15)
  private Long pidcVersId;

  @Column(name = "SVAR_ID")
  private Long svarId;

  @Column(name = "VAR_ID")
  private Long varId;

  @Column(name = "PIDC_VERSION")
  private Long pidcVersion;

  @Column(name = "\"VERSION\"")
  private Long version;

  @Column(name = "PIDC_VERS_VERS")
  private Long pidcVerVersion;

  // iCDM-2006
  @Column(name = "old_Value_Desc_Eng", length = 4000)
  private String oldValueDescEng;

  // iCDM-2006
  @Column(name = "new_Value_Desc_Eng", length = 4000)
  private String newValueDescEng;

  // iCDM-2006
  @Column(name = "old_Value_Desc_Ger", length = 4000)
  private String oldValueDescGer;

  // iCDM-2006
  @Column(name = "new_Value_Desc_Ger", length = 4000)
  private String newValueDescGer;

  @Column(name = "old_TextValue_Eng")
  private String oldTextValueEng;

  @Column(name = "new_TextValue_Eng")
  private String newTextValueEng;

  @Column(name = "old_TextValue_Ger")
  private String oldTextValueGer;

  @Column(name = "new_TextValue_Ger")
  private String newTextValueGer;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "OLD_VALUE_ID")
  private TabvAttrValue tabvAttrOldValue;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "NEW_VALUE_ID")
  private TabvAttrValue tabvAttrNewValue;

  @Column(name = "NEW_APRJ_ID")
  private Long newAprjId;

  @Column(name = "OLD_APRJ_ID")
  private Long oldAprjId;

  @Column(name = "PIDC_ACTION")
  private String pidcAction;

  // ICDM-2279
  @Column(name = "OLD_FOCUS_MATRIX_YN")
  private String oldFocusMatrix;

  // ICDM-2279
  @Column(name = "NEW_FOCUS_MATRIX_YN")
  private String newFocusMatrix;

  // ICDM-2279
  @Column(name = "OLD_TRANSFER_VCDM_YN")
  private String oldTransferVcdm;

  // ICDM-2279
  @Column(name = "NEW_TRANSFER_VCDM_YN")
  private String newTransferVcdm;

  @Column(name = "FM_VERS_ID")
  private Long fmVersId;

  @Column(name = "OLD_FM_VERS_NAME")
  private String oldFmVersName;

  @Column(name = "NEW_FM_VERS_NAME")
  private String newFmVersName;

  @Column(name = "FM_VERS_REV_NUM")
  private Long fmVersRevNum;

  @Column(name = "OLD_FM_VERS_STATUS")
  private String oldFmVersStatus;

  @Column(name = "NEW_FM_VERS_STATUS")
  private String newFmVersStatus;

  @Column(name = "OLD_FM_VERS_REVIEWED_USER", precision = 15)
  private Long oldFmVersRvwUser;

  @Column(name = "NEW_FM_VERS_REVIEWED_USER", precision = 15)
  private Long newFmVersRvwUser;

  @Column(name = "OLD_FM_VERS_REVIEWED_DATE")
  private Timestamp oldFmVersRvwDate;

  @Column(name = "NEW_FM_VERS_REVIEWED_DATE")
  private Timestamp newFmVersRvwDate;

  @Column(name = "OLD_FM_VERS_LINK")
  private String oldFmVersLink;

  @Column(name = "NEW_FM_VERS_LINK")
  private String newFmVersLink;

  @Column(name = "OLD_FM_VERS_RVW_STATUS")
  private String oldFmVersRvwStatus;

  @Column(name = "NEW_FM_VERS_RVW_STATUS")
  private String newFmVersRvwStatus;

  @Column(name = "FM_VERS_VERSION")
  private Long fmVersVersion;

  @Column(name = "FM_ID", precision = 15)
  private Long fmId;

  @Column(name = "FM_UCPA_ID", precision = 15)
  private Long fmUcpaId;

  @Column(name = "OLD_FM_COLOR_CODE")
  private String oldFmColorCode;

  @Column(name = "NEW_FM_COLOR_CODE")
  private String newFmColorCode;

  @Column(name = "OLD_FM_COMMENTS", length = 4000)
  private String oldFmComments;

  @Column(name = "NEW_FM_COMMENTS", length = 4000)
  private String newFmComments;

  @Column(name = "FM_VERSION")
  private Long fmVersion;

  @Column(name = "OLD_FM_LINK", length = 1000)
  private String oldFmLink;

  @Column(name = "NEW_FM_LINK", length = 1000)
  private String newFmLink;

  @Column(name = "USE_CASE_ID")
  private Long useCaseId;

  @Column(name = "SECTION_ID")
  private Long sectionId;

  public TPidcChangeHistory() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public Long getAttrId() {
    return this.attrId;
  }

  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }

  public Timestamp getChangedDate() {
    return this.changedDate;
  }

  public void setChangedDate(final Timestamp changedDate) {
    this.changedDate = changedDate;
  }

  public String getChangedUser() {
    return this.changedUser;
  }

  public void setChangedUser(final String changedUser) {
    this.changedUser = changedUser;
  }

  public String getNewDeletedFlag() {
    return this.newDeletedFlag;
  }

  public void setNewDeletedFlag(final String newDeletedFlag) {
    this.newDeletedFlag = newDeletedFlag;
  }

  public String getNewDescription() {
    return this.newDescription;
  }

  public void setNewDescription(final String newDescription) {
    this.newDescription = newDescription;
  }

  public String getNewIsVariant() {
    return this.newIsVariant;
  }

  public void setNewIsVariant(final String newIsVariant) {
    this.newIsVariant = newIsVariant;
  }

  public String getNewPartNumber() {
    return this.newPartNumber;
  }

  public void setNewPartNumber(final String newPartNumber) {
    this.newPartNumber = newPartNumber;
  }

  public String getNewSpecLink() {
    return this.newSpecLink;
  }

  public void setNewSpecLink(final String newSpecLink) {
    this.newSpecLink = newSpecLink;
  }

  public Long getNewStatusId() {
    return this.newStatusId;
  }

  public void setNewStatusId(final Long newStatusId) {
    this.newStatusId = newStatusId;
  }

  public String getNewUsed() {
    return this.newUsed;
  }

  public void setNewUsed(final String newUsed) {
    this.newUsed = newUsed;
  }


  public String getOldDeletedFlag() {
    return this.oldDeletedFlag;
  }

  public void setOldDeletedFlag(final String oldDeletedFlag) {
    this.oldDeletedFlag = oldDeletedFlag;
  }

  public String getOldDescription() {
    return this.oldDescription;
  }

  public void setOldDescription(final String oldDescription) {
    this.oldDescription = oldDescription;
  }

  public String getOldIsVariant() {
    return this.oldIsVariant;
  }

  public void setOldIsVariant(final String oldIsVariant) {
    this.oldIsVariant = oldIsVariant;
  }

  public String getOldPartNumber() {
    return this.oldPartNumber;
  }

  public void setOldPartNumber(final String oldPartNumber) {
    this.oldPartNumber = oldPartNumber;
  }

  public String getOldSpecLink() {
    return this.oldSpecLink;
  }

  public void setOldSpecLink(final String oldSpecLink) {
    this.oldSpecLink = oldSpecLink;
  }

  public Long getOldStatusId() {
    return this.oldStatusId;
  }

  public void setOldStatusId(final Long oldStatusId) {
    this.oldStatusId = oldStatusId;
  }

  public String getOldUsed() {
    return this.oldUsed;
  }

  public void setOldUsed(final String oldUsed) {
    this.oldUsed = oldUsed;
  }

  public Long getPidcId() {
    return this.pidcId;
  }

  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }

  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  public Long getSvarId() {
    return this.svarId;
  }

  public void setSvarId(final Long svarId) {
    this.svarId = svarId;
  }

  public Long getVarId() {
    return this.varId;
  }

  public void setVarId(final Long varId) {
    this.varId = varId;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Long getPidcVersion() {
    return this.pidcVersion;
  }

  public void setPidcVersion(final Long pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  public String getOldValueDescEng() {
    return this.oldValueDescEng;
  }

  public void setOldValueDescEng(final String oldValueDescEng) {
    this.oldValueDescEng = oldValueDescEng;
  }

  public String getNewValueDescEng() {
    return this.newValueDescEng;
  }

  public void setNewValueDescEng(final String newValueDescEng) {
    this.newValueDescEng = newValueDescEng;
  }

  public String getOldValueDescGer() {
    return this.oldValueDescGer;
  }

  public void setOldValueDescGer(final String oldValueDescGer) {
    this.oldValueDescGer = oldValueDescGer;
  }

  public String getNewValueDescGer() {
    return this.newValueDescGer;
  }

  public void setNewValueDescGer(final String newValueDescGer) {
    this.newValueDescGer = newValueDescGer;
  }

  public String getOldTextValueEng() {
    return this.oldTextValueEng;
  }

  public void setOldTextValueEng(final String oldTextValueEng) {
    this.oldTextValueEng = oldTextValueEng;
  }

  public String getNewTextValueEng() {
    return this.newTextValueEng;
  }

  public void setNewTextValueEng(final String newTextValueEng) {
    this.newTextValueEng = newTextValueEng;
  }

  public String getOldTextValueGer() {
    return this.oldTextValueGer;
  }

  public void setOldTextValueGer(final String oldTextValueGer) {
    this.oldTextValueGer = oldTextValueGer;
  }

  public String getNewTextValueGer() {
    return this.newTextValueGer;
  }

  public void setNewTextValueGer(final String newTextValueGer) {
    this.newTextValueGer = newTextValueGer;
  }

  public String getOldValueClearingStatus() {
    if (this.tabvAttrOldValue != null) {
      return this.tabvAttrOldValue.getClearingStatus();
    }

    return null;
  }

  public String getNewValueClearingStatus() {
    if (this.tabvAttrNewValue != null) {
      return this.tabvAttrNewValue.getClearingStatus();
    }

    return null;
  }


  /**
   * @return the tabvAttrOldValue
   */
  public TabvAttrValue getTabvAttrOldValue() {
    return this.tabvAttrOldValue;
  }


  /**
   * @param tabvAttrOldValue the tabvAttrOldValue to set
   */
  public void setTabvAttrOldValue(final TabvAttrValue tabvAttrOldValue) {
    this.tabvAttrOldValue = tabvAttrOldValue;
  }


  /**
   * @return the tabvAttrNewValue
   */
  public TabvAttrValue getTabvAttrNewValue() {
    return this.tabvAttrNewValue;
  }


  /**
   * @param tabvAttrNewValue the tabvAttrNewValue to set
   */
  public void setTabvAttrNewValue(final TabvAttrValue tabvAttrNewValue) {
    this.tabvAttrNewValue = tabvAttrNewValue;
  }

  /**
   * @return the pidcVerVersion
   */
  public Long getPidcVerVersion() {
    return this.pidcVerVersion;
  }


  /**
   * @param pidcVerVersion the pidcVerVersion to set
   */
  public void setPidcVerVersion(final Long pidcVerVersion) {
    this.pidcVerVersion = pidcVerVersion;
  }

  /**
   * @return the pidcAction performed
   */
  public String getPidcAction() {
    return this.pidcAction;
  }

  /**
   * @param Set the pidcAction performed
   */
  public void setPidcAction(final String pidcAction) {
    this.pidcAction = pidcAction;
  }

  /**
   * @return the oldAprjID
   */
  public Long getOldAprjId() {
    return this.oldAprjId;
  }

  /**
   * @param Set the oldAprjId
   */
  public void setOldAprjId(final Long oldAprjId) {
    this.oldAprjId = oldAprjId;
  }

  /**
   * @return the newAprjId
   */
  public Long getNewAprjId() {
    return this.newAprjId;
  }

  /**
   * @param Set the newAprjId
   */
  public void setNewAprjId(final Long newAprjId) {
    this.newAprjId = newAprjId;
  }


  /**
   * @return the oldFocusMatrix
   */
  public String getOldFocusMatrix() {
    return this.oldFocusMatrix;
  }


  /**
   * @param oldFocusMatrix the oldFocusMatrix to set
   */
  public void setOldFocusMatrix(final String oldFocusMatrix) {
    this.oldFocusMatrix = oldFocusMatrix;
  }


  /**
   * @return the newFocusMatrix
   */
  public String getNewFocusMatrix() {
    return this.newFocusMatrix;
  }


  /**
   * @param newFocusMatrix the newFocusMatrix to set
   */
  public void setNewFocusMatrix(final String newFocusMatrix) {
    this.newFocusMatrix = newFocusMatrix;
  }


  /**
   * @return the oldTransferVcdm
   */
  public String getOldTransferVcdm() {
    return this.oldTransferVcdm;
  }


  /**
   * @param oldTransferVcdm the oldTransferVcdm to set
   */
  public void setOldTransferVcdm(final String oldTransferVcdm) {
    this.oldTransferVcdm = oldTransferVcdm;
  }


  /**
   * @return the newTransferVcdm
   */
  public String getNewTransferVcdm() {
    return this.newTransferVcdm;
  }


  /**
   * @param newTransferVcdm the newTransferVcdm to set
   */
  public void setNewTransferVcdm(final String newTransferVcdm) {
    this.newTransferVcdm = newTransferVcdm;
  }


  /**
   * @return the fmVersId
   */
  public Long getFmVersId() {
    return this.fmVersId;
  }


  /**
   * @param fmVersId the fmVersId to set
   */
  public void setFmVersId(final Long fmVersId) {
    this.fmVersId = fmVersId;
  }


  /**
   * @return the oldFmVersName
   */
  public String getOldFmVersName() {
    return this.oldFmVersName;
  }


  /**
   * @param oldFmVersName the oldFmVersName to set
   */
  public void setOldFmVersName(final String oldFmVersName) {
    this.oldFmVersName = oldFmVersName;
  }


  /**
   * @return the newFmVersName
   */
  public String getNewFmVersName() {
    return this.newFmVersName;
  }


  /**
   * @param newFmVersName the newFmVersName to set
   */
  public void setNewFmVersName(final String newFmVersName) {
    this.newFmVersName = newFmVersName;
  }


  /**
   * @return the fmVersRevNum
   */
  public Long getFmVersRevNum() {
    return this.fmVersRevNum;
  }


  /**
   * @param fmVersRevNum the fmVersRevNum to set
   */
  public void setFmVersRevNum(final Long fmVersRevNum) {
    this.fmVersRevNum = fmVersRevNum;
  }


  /**
   * @return the oldFmVersStatus
   */
  public String getOldFmVersStatus() {
    return this.oldFmVersStatus;
  }


  /**
   * @param oldFmVersStatus the oldFmVersStatus to set
   */
  public void setOldFmVersStatus(final String oldFmVersStatus) {
    this.oldFmVersStatus = oldFmVersStatus;
  }


  /**
   * @return the newFmVersStatus
   */
  public String getNewFmVersStatus() {
    return this.newFmVersStatus;
  }


  /**
   * @param newFmVersStatus the newFmVersStatus to set
   */
  public void setNewFmVersStatus(final String newFmVersStatus) {
    this.newFmVersStatus = newFmVersStatus;
  }


  /**
   * @return the oldFmVersRvwUser
   */
  public Long getOldFmVersRvwUser() {
    return this.oldFmVersRvwUser;
  }


  /**
   * @param oldFmVersRvwUser the oldFmVersRvwUser to set
   */
  public void setOldFmVersRvwUser(final Long oldFmVersRvwUser) {
    this.oldFmVersRvwUser = oldFmVersRvwUser;
  }


  /**
   * @return the newFmVersRvwUser
   */
  public Long getNewFmVersRvwUser() {
    return this.newFmVersRvwUser;
  }


  /**
   * @param newFmVersRvwUser the newFmVersRvwUser to set
   */
  public void setNewFmVersRvwUser(final Long newFmVersRvwUser) {
    this.newFmVersRvwUser = newFmVersRvwUser;
  }


  /**
   * @return the oldFmVersRvwDate
   */
  public Timestamp getOldFmVersRvwDate() {
    return this.oldFmVersRvwDate;
  }


  /**
   * @param oldFmVersRvwDate the oldFmVersRvwDate to set
   */
  public void setOldFmVersRvwDate(final Timestamp oldFmVersRvwDate) {
    this.oldFmVersRvwDate = oldFmVersRvwDate;
  }


  /**
   * @return the newFmVersRvwDate
   */
  public Timestamp getNewFmVersRvwDate() {
    return this.newFmVersRvwDate;
  }


  /**
   * @param newFmVersRvwDate the newFmVersRvwDate to set
   */
  public void setNewFmVersRvwDate(final Timestamp newFmVersRvwDate) {
    this.newFmVersRvwDate = newFmVersRvwDate;
  }


  /**
   * @return the oldFmVersLink
   */
  public String getOldFmVersLink() {
    return this.oldFmVersLink;
  }


  /**
   * @param oldFmVersLink the oldFmVersLink to set
   */
  public void setOldFmVersLink(final String oldFmVersLink) {
    this.oldFmVersLink = oldFmVersLink;
  }


  /**
   * @return the newFmVersLink
   */
  public String getNewFmVersLink() {
    return this.newFmVersLink;
  }


  /**
   * @param newFmVersLink the newFmVersLink to set
   */
  public void setNewFmVersLink(final String newFmVersLink) {
    this.newFmVersLink = newFmVersLink;
  }


  /**
   * @return the oldFmVersRvwStatus
   */
  public String getOldFmVersRvwStatus() {
    return this.oldFmVersRvwStatus;
  }


  /**
   * @param oldFmVersRvwStatus the oldFmVersRvwStatus to set
   */
  public void setOldFmVersRvwStatus(final String oldFmVersRvwStatus) {
    this.oldFmVersRvwStatus = oldFmVersRvwStatus;
  }


  /**
   * @return the newFmVersRvwStatus
   */
  public String getNewFmVersRvwStatus() {
    return this.newFmVersRvwStatus;
  }


  /**
   * @param newFmVersRvwStatus the newFmVersRvwStatus to set
   */
  public void setNewFmVersRvwStatus(final String newFmVersRvwStatus) {
    this.newFmVersRvwStatus = newFmVersRvwStatus;
  }


  /**
   * @return the fmVersVersion
   */
  public Long getFmVersVersion() {
    return this.fmVersVersion;
  }


  /**
   * @param fmVersVersion the fmVersVersion to set
   */
  public void setFmVersVersion(final Long fmVersVersion) {
    this.fmVersVersion = fmVersVersion;
  }


  /**
   * @return the fmId
   */
  public Long getFmId() {
    return this.fmId;
  }


  /**
   * @param fmId the fmId to set
   */
  public void setFmId(final Long fmId) {
    this.fmId = fmId;
  }


  /**
   * @return the fmUcpaId
   */
  public Long getFmUcpaId() {
    return this.fmUcpaId;
  }


  /**
   * @param fmUcpaId the fmUcpaId to set
   */
  public void setFmUcpaId(final Long fmUcpaId) {
    this.fmUcpaId = fmUcpaId;
  }


  /**
   * @return the oldFmColorCode
   */
  public String getOldFmColorCode() {
    return this.oldFmColorCode;
  }


  /**
   * @param oldFmColorCode the oldFmColorCode to set
   */
  public void setOldFmColorCode(final String oldFmColorCode) {
    this.oldFmColorCode = oldFmColorCode;
  }


  /**
   * @return the newFmColorCode
   */
  public String getNewFmColorCode() {
    return this.newFmColorCode;
  }


  /**
   * @param newFmColorCode the newFmColorCode to set
   */
  public void setNewFmColorCode(final String newFmColorCode) {
    this.newFmColorCode = newFmColorCode;
  }


  /**
   * @return the oldFmComments
   */
  public String getOldFmComments() {
    return this.oldFmComments;
  }


  /**
   * @param oldFmComments the oldFmComments to set
   */
  public void setOldFmComments(final String oldFmComments) {
    this.oldFmComments = oldFmComments;
  }


  /**
   * @return the newFmComments
   */
  public String getNewFmComments() {
    return this.newFmComments;
  }


  /**
   * @param newFmComments the newFmComments to set
   */
  public void setNewFmComments(final String newFmComments) {
    this.newFmComments = newFmComments;
  }


  /**
   * @return the fmVersion
   */
  public Long getFmVersion() {
    return this.fmVersion;
  }


  /**
   * @param fmVersion the fmVersion to set
   */
  public void setFmVersion(final Long fmVersion) {
    this.fmVersion = fmVersion;
  }


  /**
   * @return the oldFmLink
   */
  public String getOldFmLink() {
    return this.oldFmLink;
  }


  /**
   * @param oldFmLink the oldFmLink to set
   */
  public void setOldFmLink(final String oldFmLink) {
    this.oldFmLink = oldFmLink;
  }


  /**
   * @return the newFmLink
   */
  public String getNewFmLink() {
    return this.newFmLink;
  }


  /**
   * @param newFmLink the newFmLink to set
   */
  public void setNewFmLink(final String newFmLink) {
    this.newFmLink = newFmLink;
  }


  /**
   * @return the useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }


  /**
   * @param useCaseId the useCaseId to set
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }


  /**
   * @return the sectionId
   */
  public Long getSectionId() {
    return this.sectionId;
  }


  /**
   * @param sectionId the sectionId to set
   */
  public void setSectionId(final Long sectionId) {
    this.sectionId = sectionId;
  }

}