package com.bosch.caltool.icdm.model.wp;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * WpmlWpMasterlist Model class
 *
 * @author UKT1COB
 */
public class WpmlWpMasterlist implements Comparable<WpmlWpMasterlist>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 213250988266632L;
  /**
   * Id
   */
  private Long id;
  /**
   * cocComment
   */
  private String cocComment;
  /**
   * company
   */
  private String company;
  /**
   * dataReviewMandatory
   */
  private String dataReviewMandatory;
  /**
   * eu6eImpact
   */
  private String eu6eImpact;
  /**
   * eu6eImpactDesc
   */
  private String eu6eImpactDesc;
  /**
   * isDeleted
   */
  private String isDeleted;
  /**
   * isValid
   */
  private String isValid;
  /**
   * linkToWpTraining
   */
  private String linkToWpTraining;
  /**
   * mandatoryTestdriveFind
   */
  private String mandatoryTestdriveFind;
  /**
   * mandatoryTestdriveRelease
   */
  private String mandatoryTestdriveRelease;
  /**
   * mcrAdditionalInfo
   */
  private String mcrAdditionalInfo;

  /**
   * mcrWpiNumber
   */
  private String mcrWpiNumber;
  /**
   * Wp Shortname
   */
  private String wpMcrName;
  /**
   * Wp Longname
   */
  private String wpLongname;
  /**
   * Wp Description Internal
   */
  private String wpDescriptionInternal;
  /**
   * Mcr Id
   */
  private String mcrId;
  /**
   * Mcr Name Old
   */
  private String mcrNameOld;
  /**
   * Gs Id Old
   */
  private String gsIdOld;
  /**
   * Ctd Link
   */
  private String ctdLink;
  /**
   * Link To Info
   */
  private String linkToInfo;
  /**
   * Emission Relevant Crp Pfa
   */
  private String emissionRelevantCrpPfa;
  /**
   * Emission Relevant For Tdec
   */
  private String emissionRelevantForTdec;
  /**
   * Coc Id
   */
  private Long cocId;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;
  /**
   * Wp Resp Te Nt User
   */
  private String wpRespTeNtUser;
  /**
   * Wp Description External
   */
  private String wpDescriptionExternal;
  /**
   * Obd Relevant Crp
   */
  private String obdRelevantCrp;
  /**
   * Obd Relevant Pfa
   */
  private String obdRelevantPfa;

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
   * @return wpLongname
   */
  public String getWpLongname() {
    return this.wpLongname;
  }

  /**
   * @param wpLongname set wpLongname
   */
  public void setWpLongname(final String wpLongname) {
    this.wpLongname = wpLongname;
  }

  /**
   * @return wpDescriptionInternal
   */
  public String getWpDescriptionInternal() {
    return this.wpDescriptionInternal;
  }

  /**
   * @param wpDescriptionInternal set wpDescriptionInternal
   */
  public void setWpDescriptionInternal(final String wpDescriptionInternal) {
    this.wpDescriptionInternal = wpDescriptionInternal;
  }


  /**
   * @return mcrId
   */
  public String getMcrId() {
    return this.mcrId;
  }

  /**
   * @param mcrId set mcrId
   */
  public void setMcrId(final String mcrId) {
    this.mcrId = mcrId;
  }

  /**
   * @return mcrNameOld
   */
  public String getMcrNameOld() {
    return this.mcrNameOld;
  }

  /**
   * @param mcrNameOld set mcrNameOld
   */
  public void setMcrNameOld(final String mcrNameOld) {
    this.mcrNameOld = mcrNameOld;
  }

  /**
   * @return gsIdOld
   */
  public String getGsIdOld() {
    return this.gsIdOld;
  }

  /**
   * @param gsIdOld set gsIdOld
   */
  public void setGsIdOld(final String gsIdOld) {
    this.gsIdOld = gsIdOld;
  }

  /**
   * @return ctdLink
   */
  public String getCtdLink() {
    return this.ctdLink;
  }

  /**
   * @param ctdLink set ctdLink
   */
  public void setCtdLink(final String ctdLink) {
    this.ctdLink = ctdLink;
  }

  /**
   * @return linkToInfo
   */
  public String getLinkToInfo() {
    return this.linkToInfo;
  }

  /**
   * @param linkToInfo set linkToInfo
   */
  public void setLinkToInfo(final String linkToInfo) {
    this.linkToInfo = linkToInfo;
  }

  /**
   * @return emissionRelevantCrpPfa
   */
  public String getEmissionRelevantCrpPfa() {
    return this.emissionRelevantCrpPfa;
  }

  /**
   * @param emissionRelevantCrpPfa set emissionRelevantCrpPfa
   */
  public void setEmissionRelevantCrpPfa(final String emissionRelevantCrpPfa) {
    this.emissionRelevantCrpPfa = emissionRelevantCrpPfa;
  }

  /**
   * @return emissionRelevantForTdec
   */
  public String getEmissionRelevantForTdec() {
    return this.emissionRelevantForTdec;
  }

  /**
   * @param emissionRelevantForTdec set emissionRelevantForTdec
   */
  public void setEmissionRelevantForTdec(final String emissionRelevantForTdec) {
    this.emissionRelevantForTdec = emissionRelevantForTdec;
  }

  /**
   * @return cocId
   */
  public Long getCocId() {
    return this.cocId;
  }

  /**
   * @param cocId set cocId
   */
  public void setCocId(final Long cocId) {
    this.cocId = cocId;
  }


  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return wpRespTeNtUser
   */
  public String getWpRespTeNtUser() {
    return this.wpRespTeNtUser;
  }

  /**
   * @param wpRespTeNtUser set wpRespTeNtUser
   */
  public void setWpRespTeNtUser(final String wpRespTeNtUser) {
    this.wpRespTeNtUser = wpRespTeNtUser;
  }

  /**
   * @return wpDescriptionExternal
   */
  public String getWpDescriptionExternal() {
    return this.wpDescriptionExternal;
  }

  /**
   * @param wpDescriptionExternal set wpDescriptionExternal
   */
  public void setWpDescriptionExternal(final String wpDescriptionExternal) {
    this.wpDescriptionExternal = wpDescriptionExternal;
  }

  /**
   * @return obdRelevantCrp
   */
  public String getObdRelevantCrp() {
    return this.obdRelevantCrp;
  }

  /**
   * @param obdRelevantCrp set obdRelevantCrp
   */
  public void setObdRelevantCrp(final String obdRelevantCrp) {
    this.obdRelevantCrp = obdRelevantCrp;
  }

  /**
   * @return obdRelevantPfa
   */
  public String getObdRelevantPfa() {
    return this.obdRelevantPfa;
  }

  /**
   * @param obdRelevantPfa set obdRelevantPfa
   */
  public void setObdRelevantPfa(final String obdRelevantPfa) {
    this.obdRelevantPfa = obdRelevantPfa;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WpmlWpMasterlist object) {
    return ModelUtil.compare(getId(), object.getId());
  }


  /**
   * @return the cocComment
   */
  public String getCocComment() {
    return this.cocComment;
  }


  /**
   * @param cocComment the cocComment to set
   */
  public void setCocComment(final String cocComment) {
    this.cocComment = cocComment;
  }


  /**
   * @return the company
   */
  public String getCompany() {
    return this.company;
  }


  /**
   * @param company the company to set
   */
  public void setCompany(final String company) {
    this.company = company;
  }


  /**
   * @return the dataReviewMandatory
   */
  public String getDataReviewMandatory() {
    return this.dataReviewMandatory;
  }


  /**
   * @param dataReviewMandatory the dataReviewMandatory to set
   */
  public void setDataReviewMandatory(final String dataReviewMandatory) {
    this.dataReviewMandatory = dataReviewMandatory;
  }


  /**
   * @return the eu6eImpact
   */
  public String getEu6eImpact() {
    return this.eu6eImpact;
  }


  /**
   * @param eu6eImpact the eu6eImpact to set
   */
  public void setEu6eImpact(final String eu6eImpact) {
    this.eu6eImpact = eu6eImpact;
  }


  /**
   * @return the eu6eImpactDesc
   */
  public String getEu6eImpactDesc() {
    return this.eu6eImpactDesc;
  }


  /**
   * @param eu6eImpactDesc the eu6eImpactDesc to set
   */
  public void setEu6eImpactDesc(final String eu6eImpactDesc) {
    this.eu6eImpactDesc = eu6eImpactDesc;
  }


  /**
   * @return the isDeleted
   */
  public String getIsDeleted() {
    return this.isDeleted;
  }


  /**
   * @param isDeleted the isDeleted to set
   */
  public void setIsDeleted(final String isDeleted) {
    this.isDeleted = isDeleted;
  }


  /**
   * @return the isValid
   */
  public String getIsValid() {
    return this.isValid;
  }


  /**
   * @param isValid the isValid to set
   */
  public void setIsValid(final String isValid) {
    this.isValid = isValid;
  }


  /**
   * @return the linkToWpTraining
   */
  public String getLinkToWpTraining() {
    return this.linkToWpTraining;
  }


  /**
   * @param linkToWpTraining the linkToWpTraining to set
   */
  public void setLinkToWpTraining(final String linkToWpTraining) {
    this.linkToWpTraining = linkToWpTraining;
  }


  /**
   * @return the mandatoryTestdriveFind
   */
  public String getMandatoryTestdriveFind() {
    return this.mandatoryTestdriveFind;
  }


  /**
   * @param mandatoryTestdriveFind the mandatoryTestdriveFind to set
   */
  public void setMandatoryTestdriveFind(final String mandatoryTestdriveFind) {
    this.mandatoryTestdriveFind = mandatoryTestdriveFind;
  }


  /**
   * @return the mandatoryTestdriveRelease
   */
  public String getMandatoryTestdriveRelease() {
    return this.mandatoryTestdriveRelease;
  }


  /**
   * @param mandatoryTestdriveRelease the mandatoryTestdriveRelease to set
   */
  public void setMandatoryTestdriveRelease(final String mandatoryTestdriveRelease) {
    this.mandatoryTestdriveRelease = mandatoryTestdriveRelease;
  }


  /**
   * @return the mcrAdditionalInfo
   */
  public String getMcrAdditionalInfo() {
    return this.mcrAdditionalInfo;
  }


  /**
   * @param mcrAdditionalInfo the mcrAdditionalInfo to set
   */
  public void setMcrAdditionalInfo(final String mcrAdditionalInfo) {
    this.mcrAdditionalInfo = mcrAdditionalInfo;
  }

  /**
   * @return the mcrWpiNumber
   */
  public String getMcrWpiNumber() {
    return this.mcrWpiNumber;
  }


  /**
   * @param mcrWpiNumber the mcrWpiNumber to set
   */
  public void setMcrWpiNumber(final String mcrWpiNumber) {
    this.mcrWpiNumber = mcrWpiNumber;
  }


  /**
   * @return the wpMcrName
   */
  public String getWpMcrName() {
    return this.wpMcrName;
  }


  /**
   * @param wpMcrName the wpMcrName to set
   */
  public void setWpMcrName(final String wpMcrName) {
    this.wpMcrName = wpMcrName;
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

    return ModelUtil.isEqual(getId(), ((WpmlWpMasterlist) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
