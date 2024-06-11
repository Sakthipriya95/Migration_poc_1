package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * PidcChangeHistory Model class
 *
 * @author dmr1cob
 */
public class PidcChangeHistory implements Comparable<PidcChangeHistory>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 316505311681998L;
  /**
   * Id
   */
  private Long id;
  /**
   * Pidc Id
   */
  private Long pidcId;
  /**
   * Var Id
   */
  private Long varId;
  /**
   * Svar Id
   */
  private Long svarId;
  /**
   * Attr Id
   */
  private Long attrId;
  /**
   * Changed Date
   */
  private String changedDate;
  /**
   * Changed User
   */
  private String changedUser;
  /**
   * Pidc Version
   */
  private Long pidcVersion;
  /**
   * Old Value Id
   */
  private Long oldValueId;
  /**
   * New Value Id
   */
  private Long newValueId;
  /**
   * Old Used
   */
  private String oldUsed;
  /**
   * New Used
   */
  private String newUsed;
  /**
   * Old Part Number
   */
  private String oldPartNumber;
  /**
   * New Part Number
   */
  private String newPartNumber;
  /**
   * Old Spec Link
   */
  private String oldSpecLink;
  /**
   * New Spec Link
   */
  private String newSpecLink;
  /**
   * Old Description
   */
  private String oldDescription;
  /**
   * New Description
   */
  private String newDescription;
  /**
   * Old Deleted Flag
   */
  private String oldDeletedFlag;
  /**
   * New Deleted Flag
   */
  private String newDeletedFlag;
  /**
   * Old Status Id
   */
  private Long oldStatusId;
  /**
   * New Status Id
   */
  private Long newStatusId;
  /**
   * Old Is Variant
   */
  private String oldIsVariant;
  /**
   * New Is Variant
   */
  private String newIsVariant;
  /**
   * Version
   */
  private Long version;
  /**
   * Old Value Desc Eng
   */
  private String oldValueDescEng;
  /**
   * New Value Desc Eng
   */
  private String newValueDescEng;
  /**
   * Old Value Desc Ger
   */
  private String oldValueDescGer;
  /**
   * New Value Desc Ger
   */
  private String newValueDescGer;
  /**
   * Old Textvalue Eng
   */
  private String oldTextvalueEng;
  /**
   * New Textvalue Eng
   */
  private String newTextvalueEng;
  /**
   * Old Textvalue Ger
   */
  private String oldTextvalueGer;
  /**
   * New Textvalue Ger
   */
  private String newTextvalueGer;
  /**
   * Pidc Vers Id
   */
  private Long pidcVersId;
  /**
   * Pidc Vers Vers
   */
  private Long pidcVersVers;
  /**
   * Old Aprj Id
   */
  private Long oldAprjId;
  /**
   * New Aprj Id
   */
  private Long newAprjId;
  /**
   * Pidc Action
   */
  private String pidcAction;
  /**
   * Old Focus Matrix Yn
   */
  private String oldFocusMatrixYn;
  /**
   * New Focus Matrix Yn
   */
  private String newFocusMatrixYn;
  /**
   * Old Transfer Vcdm Yn
   */
  private String oldTransferVcdmYn;
  /**
   * New Transfer Vcdm Yn
   */
  private String newTransferVcdmYn;
  /**
   * Fm Vers Id
   */
  private Long fmVersId;
  /**
   * Old Fm Vers Name
   */
  private String oldFmVersName;
  /**
   * New Fm Vers Name
   */
  private String newFmVersName;
  /**
   * Fm Vers Rev Num
   */
  private Long fmVersRevNum;
  /**
   * Old Fm Vers Status
   */
  private String oldFmVersStatus;
  /**
   * New Fm Vers Status
   */
  private String newFmVersStatus;
  /**
   * Old Fm Vers Reviewed User
   */
  private Long oldFmVersReviewedUser;
  /**
   * New Fm Vers Reviewed User
   */
  private Long newFmVersReviewedUser;
  /**
   * Old Fm Vers Reviewed Date
   */
  private String oldFmVersReviewedDate;
  /**
   * New Fm Vers Reviewed Date
   */
  private String newFmVersReviewedDate;
  /**
   * Old Fm Vers Link
   */
  private String oldFmVersLink;
  /**
   * New Fm Vers Link
   */
  private String newFmVersLink;
  /**
   * Old Fm Vers Rvw Status
   */
  private String oldFmVersRvwStatus;
  /**
   * New Fm Vers Rvw Status
   */
  private String newFmVersRvwStatus;
  /**
   * Fm Vers Version
   */
  private Long fmVersVersion;
  /**
   * Fm Id
   */
  private Long fmId;
  /**
   * Fm Ucpa Id
   */
  private Long fmUcpaId;
  /**
   * Old Fm Color Code
   */
  private String oldFmColorCode;
  /**
   * New Fm Color Code
   */
  private String newFmColorCode;
  /**
   * Old Fm Comments
   */
  private String oldFmComments;
  /**
   * New Fm Comments
   */
  private String newFmComments;
  /**
   * Fm Version
   */
  private Long fmVersion;
  /**
   * Old Fm Link
   */
  private String oldFmLink;
  /**
   * New Fm Link
   */
  private String newFmLink;
  /**
   * Use Case Id
   */
  private Long useCaseId;
  /**
   * Section Id
   */
  private Long sectionId;
  /**
   * Old Value Clearing Status
   */
  private String oldValueClearingStatus;
  /**
   * Old Value Clearing Status
   */
  private String newValueClearingStatus;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }


  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }


  /**
   * @return the varId
   */
  public Long getVarId() {
    return this.varId;
  }


  /**
   * @param varId the varId to set
   */
  public void setVarId(final Long varId) {
    this.varId = varId;
  }


  /**
   * @return the svarId
   */
  public Long getSvarId() {
    return this.svarId;
  }


  /**
   * @param svarId the svarId to set
   */
  public void setSvarId(final Long svarId) {
    this.svarId = svarId;
  }


  /**
   * @return the attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }


  /**
   * @param attrId the attrId to set
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * @return the changedDate
   */
  public String getChangedDate() {
    return this.changedDate;
  }


  /**
   * @param changedDate the changedDate to set
   */
  public void setChangedDate(final String changedDate) {
    this.changedDate = changedDate;
  }


  /**
   * @return the changedUser
   */
  public String getChangedUser() {
    return this.changedUser;
  }


  /**
   * @param changedUser the changedUser to set
   */
  public void setChangedUser(final String changedUser) {
    this.changedUser = changedUser;
  }


  /**
   * @return the pidcVersion
   */
  public Long getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final Long pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the oldValueId
   */
  public Long getOldValueId() {
    return this.oldValueId;
  }


  /**
   * @param oldValueId the oldValueId to set
   */
  public void setOldValueId(final Long oldValueId) {
    this.oldValueId = oldValueId;
  }


  /**
   * @return the newValueId
   */
  public Long getNewValueId() {
    return this.newValueId;
  }


  /**
   * @param newValueId the newValueId to set
   */
  public void setNewValueId(final Long newValueId) {
    this.newValueId = newValueId;
  }


  /**
   * @return the oldUsed
   */
  public String getOldUsed() {
    return this.oldUsed;
  }


  /**
   * @param oldUsed the oldUsed to set
   */
  public void setOldUsed(final String oldUsed) {
    this.oldUsed = oldUsed;
  }


  /**
   * @return the newUsed
   */
  public String getNewUsed() {
    return this.newUsed;
  }


  /**
   * @param newUsed the newUsed to set
   */
  public void setNewUsed(final String newUsed) {
    this.newUsed = newUsed;
  }


  /**
   * @return the oldPartNumber
   */
  public String getOldPartNumber() {
    return this.oldPartNumber;
  }


  /**
   * @param oldPartNumber the oldPartNumber to set
   */
  public void setOldPartNumber(final String oldPartNumber) {
    this.oldPartNumber = oldPartNumber;
  }


  /**
   * @return the newPartNumber
   */
  public String getNewPartNumber() {
    return this.newPartNumber;
  }


  /**
   * @param newPartNumber the newPartNumber to set
   */
  public void setNewPartNumber(final String newPartNumber) {
    this.newPartNumber = newPartNumber;
  }


  /**
   * @return the oldSpecLink
   */
  public String getOldSpecLink() {
    return this.oldSpecLink;
  }


  /**
   * @param oldSpecLink the oldSpecLink to set
   */
  public void setOldSpecLink(final String oldSpecLink) {
    this.oldSpecLink = oldSpecLink;
  }


  /**
   * @return the newSpecLink
   */
  public String getNewSpecLink() {
    return this.newSpecLink;
  }


  /**
   * @param newSpecLink the newSpecLink to set
   */
  public void setNewSpecLink(final String newSpecLink) {
    this.newSpecLink = newSpecLink;
  }


  /**
   * @return the oldDescription
   */
  public String getOldDescription() {
    return this.oldDescription;
  }


  /**
   * @param oldDescription the oldDescription to set
   */
  public void setOldDescription(final String oldDescription) {
    this.oldDescription = oldDescription;
  }


  /**
   * @return the newDescription
   */
  public String getNewDescription() {
    return this.newDescription;
  }


  /**
   * @param newDescription the newDescription to set
   */
  public void setNewDescription(final String newDescription) {
    this.newDescription = newDescription;
  }


  /**
   * @return the oldDeletedFlag
   */
  public String getOldDeletedFlag() {
    return this.oldDeletedFlag;
  }


  /**
   * @param oldDeletedFlag the oldDeletedFlag to set
   */
  public void setOldDeletedFlag(final String oldDeletedFlag) {
    this.oldDeletedFlag = oldDeletedFlag;
  }


  /**
   * @return the newDeletedFlag
   */
  public String getNewDeletedFlag() {
    return this.newDeletedFlag;
  }


  /**
   * @param newDeletedFlag the newDeletedFlag to set
   */
  public void setNewDeletedFlag(final String newDeletedFlag) {
    this.newDeletedFlag = newDeletedFlag;
  }


  /**
   * @return the oldStatusId
   */
  public Long getOldStatusId() {
    return this.oldStatusId;
  }


  /**
   * @param oldStatusId the oldStatusId to set
   */
  public void setOldStatusId(final Long oldStatusId) {
    this.oldStatusId = oldStatusId;
  }


  /**
   * @return the newStatusId
   */
  public Long getNewStatusId() {
    return this.newStatusId;
  }


  /**
   * @param newStatusId the newStatusId to set
   */
  public void setNewStatusId(final Long newStatusId) {
    this.newStatusId = newStatusId;
  }


  /**
   * @return the oldIsVariant
   */
  public String getOldIsVariant() {
    return this.oldIsVariant;
  }


  /**
   * @param oldIsVariant the oldIsVariant to set
   */
  public void setOldIsVariant(final String oldIsVariant) {
    this.oldIsVariant = oldIsVariant;
  }


  /**
   * @return the newIsVariant
   */
  public String getNewIsVariant() {
    return this.newIsVariant;
  }


  /**
   * @param newIsVariant the newIsVariant to set
   */
  public void setNewIsVariant(final String newIsVariant) {
    this.newIsVariant = newIsVariant;
  }


  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the oldValueDescEng
   */
  public String getOldValueDescEng() {
    return this.oldValueDescEng;
  }


  /**
   * @param oldValueDescEng the oldValueDescEng to set
   */
  public void setOldValueDescEng(final String oldValueDescEng) {
    this.oldValueDescEng = oldValueDescEng;
  }


  /**
   * @return the newValueDescEng
   */
  public String getNewValueDescEng() {
    return this.newValueDescEng;
  }


  /**
   * @param newValueDescEng the newValueDescEng to set
   */
  public void setNewValueDescEng(final String newValueDescEng) {
    this.newValueDescEng = newValueDescEng;
  }


  /**
   * @return the oldValueDescGer
   */
  public String getOldValueDescGer() {
    return this.oldValueDescGer;
  }


  /**
   * @param oldValueDescGer the oldValueDescGer to set
   */
  public void setOldValueDescGer(final String oldValueDescGer) {
    this.oldValueDescGer = oldValueDescGer;
  }


  /**
   * @return the newValueDescGer
   */
  public String getNewValueDescGer() {
    return this.newValueDescGer;
  }


  /**
   * @param newValueDescGer the newValueDescGer to set
   */
  public void setNewValueDescGer(final String newValueDescGer) {
    this.newValueDescGer = newValueDescGer;
  }


  /**
   * @return the oldTextvalueEng
   */
  public String getOldTextvalueEng() {
    return this.oldTextvalueEng;
  }


  /**
   * @param oldTextvalueEng the oldTextvalueEng to set
   */
  public void setOldTextvalueEng(final String oldTextvalueEng) {
    this.oldTextvalueEng = oldTextvalueEng;
  }


  /**
   * @return the newTextvalueEng
   */
  public String getNewTextvalueEng() {
    return this.newTextvalueEng;
  }


  /**
   * @param newTextvalueEng the newTextvalueEng to set
   */
  public void setNewTextvalueEng(final String newTextvalueEng) {
    this.newTextvalueEng = newTextvalueEng;
  }


  /**
   * @return the oldTextvalueGer
   */
  public String getOldTextvalueGer() {
    return this.oldTextvalueGer;
  }


  /**
   * @param oldTextvalueGer the oldTextvalueGer to set
   */
  public void setOldTextvalueGer(final String oldTextvalueGer) {
    this.oldTextvalueGer = oldTextvalueGer;
  }


  /**
   * @return the newTextvalueGer
   */
  public String getNewTextvalueGer() {
    return this.newTextvalueGer;
  }


  /**
   * @param newTextvalueGer the newTextvalueGer to set
   */
  public void setNewTextvalueGer(final String newTextvalueGer) {
    this.newTextvalueGer = newTextvalueGer;
  }


  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }


  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }


  /**
   * @return the pidcVersVers
   */
  public Long getPidcVersVers() {
    return this.pidcVersVers;
  }


  /**
   * @param pidcVersVers the pidcVersVers to set
   */
  public void setPidcVersVers(final Long pidcVersVers) {
    this.pidcVersVers = pidcVersVers;
  }

  /**
   * @return the oldAprjId
   */
  public Long getOldAprjId() {
    return this.oldAprjId;
  }


  /**
   * @param oldAprjId the oldAprjId to set
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
   * @param newAprjId the newAprjId to set
   */
  public void setNewAprjId(final Long newAprjId) {
    this.newAprjId = newAprjId;
  }


  /**
   * @return the pidcAction
   */
  public String getPidcAction() {
    return this.pidcAction;
  }


  /**
   * @param pidcAction the pidcAction to set
   */
  public void setPidcAction(final String pidcAction) {
    this.pidcAction = pidcAction;
  }


  /**
   * @return the oldFocusMatrixYn
   */
  public String getOldFocusMatrixYn() {
    return this.oldFocusMatrixYn;
  }


  /**
   * @param oldFocusMatrixYn the oldFocusMatrixYn to set
   */
  public void setOldFocusMatrixYn(final String oldFocusMatrixYn) {
    this.oldFocusMatrixYn = oldFocusMatrixYn;
  }


  /**
   * @return the newFocusMatrixYn
   */
  public String getNewFocusMatrixYn() {
    return this.newFocusMatrixYn;
  }


  /**
   * @param newFocusMatrixYn the newFocusMatrixYn to set
   */
  public void setNewFocusMatrixYn(final String newFocusMatrixYn) {
    this.newFocusMatrixYn = newFocusMatrixYn;
  }


  /**
   * @return the oldTransferVcdmYn
   */
  public String getOldTransferVcdmYn() {
    return this.oldTransferVcdmYn;
  }


  /**
   * @param oldTransferVcdmYn the oldTransferVcdmYn to set
   */
  public void setOldTransferVcdmYn(final String oldTransferVcdmYn) {
    this.oldTransferVcdmYn = oldTransferVcdmYn;
  }


  /**
   * @return the newTransferVcdmYn
   */
  public String getNewTransferVcdmYn() {
    return this.newTransferVcdmYn;
  }


  /**
   * @param newTransferVcdmYn the newTransferVcdmYn to set
   */
  public void setNewTransferVcdmYn(final String newTransferVcdmYn) {
    this.newTransferVcdmYn = newTransferVcdmYn;
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
   * @return the oldFmVersReviewedUser
   */
  public Long getOldFmVersReviewedUser() {
    return this.oldFmVersReviewedUser;
  }


  /**
   * @param oldFmVersReviewedUser the oldFmVersReviewedUser to set
   */
  public void setOldFmVersReviewedUser(final Long oldFmVersReviewedUser) {
    this.oldFmVersReviewedUser = oldFmVersReviewedUser;
  }


  /**
   * @return the newFmVersReviewedUser
   */
  public Long getNewFmVersReviewedUser() {
    return this.newFmVersReviewedUser;
  }


  /**
   * @param newFmVersReviewedUser the newFmVersReviewedUser to set
   */
  public void setNewFmVersReviewedUser(final Long newFmVersReviewedUser) {
    this.newFmVersReviewedUser = newFmVersReviewedUser;
  }


  /**
   * @return the oldFmVersReviewedDate
   */
  public String getOldFmVersReviewedDate() {
    return this.oldFmVersReviewedDate;
  }


  /**
   * @param oldFmVersReviewedDate the oldFmVersReviewedDate to set
   */
  public void setOldFmVersReviewedDate(final String oldFmVersReviewedDate) {
    this.oldFmVersReviewedDate = oldFmVersReviewedDate;
  }


  /**
   * @return the newFmVersReviewedDate
   */
  public String getNewFmVersReviewedDate() {
    return this.newFmVersReviewedDate;
  }


  /**
   * @param newFmVersReviewedDate the newFmVersReviewedDate to set
   */
  public void setNewFmVersReviewedDate(final String newFmVersReviewedDate) {
    this.newFmVersReviewedDate = newFmVersReviewedDate;
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


  /**
   * @return the oldValueClearingStatus
   */
  public String getOldValueClearingStatus() {
    return this.oldValueClearingStatus;
  }


  /**
   * @param oldValueClearingStatus the oldValueClearingStatus to set
   */
  public void setOldValueClearingStatus(final String oldValueClearingStatus) {
    this.oldValueClearingStatus = oldValueClearingStatus;
  }


  /**
   * @return the newValueClearingStatus
   */
  public String getNewValueClearingStatus() {
    return this.newValueClearingStatus;
  }


  /**
   * @param newValueClearingStatus the newValueClearingStatus to set
   */
  public void setNewValueClearingStatus(final String newValueClearingStatus) {
    this.newValueClearingStatus = newValueClearingStatus;
  }

  /**
   * @return the serialversionuid
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcChangeHistory object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((PidcChangeHistory) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
