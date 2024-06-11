package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * The persistent class for the T_WPML_WP_MASTERLIST database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TWpmlWpMasterlist.NQ_GET_ALL_TWPML_WP_MASTERLIST, query = "SELECT tWpmlWpMasterlist FROM TWpmlWpMasterlist tWpmlWpMasterlist"),
    @NamedQuery(name = TWpmlWpMasterlist.NQ_GET_TWPML_WP_MASTERLIST_BY_MCRID, query = "SELECT tWpmlWpMasterlist FROM TWpmlWpMasterlist tWpmlWpMasterlist where tWpmlWpMasterlist.mcrId = :mcrId") })
@Entity
@Table(name = "T_WPML_WP_MASTERLIST")
public class TWpmlWpMasterlist implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all TWpmlWpMasterlist
   */
  public static final String NQ_GET_ALL_TWPML_WP_MASTERLIST = "TWpmlWpMasterlist.getAllTWpmlWpMasterlist";

  /**
   * Named query to get TWpmlWpMasterlist by MCR ID
   */
  public static final String NQ_GET_TWPML_WP_MASTERLIST_BY_MCRID = "TWpmlWpMasterlist.getTWpmlWpMasterlistByMcrId";


  @Id
  @Column(name = "ID")
  private long id;

  @Column(name = "COC_COMMENT")
  private String cocComment;

  @Column(name = "COC_ID")
  private Long cocId;

  @Column(name = "COMPANY")
  private String company;

  @Column(name = "CREATION_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATION_USER")
  private String createdUser;

  @Column(name = "CTD_LINK")
  private String ctdLink;

  @Column(name = "DATAREVIEW_MANDATORY")
  private String datareviewMandatory;

  @Column(name = "EMISSION_RELEVANT_CRP_PFA")
  private String emissionRelevantCrpPfa;

  @Column(name = "EMISSION_RELEVANT_FOR_TDEC")
  private String emissionRelevantForTdec;

  @Column(name = "EU6E_IMPACT")
  private String eu6eImpact;

  @Column(name = "EU6E_IMPACT_DESC")
  private String eu6eImpactDesc;

  @Column(name = "GS_ID_OLD")
  private String gsIdOld;

  @Column(name = "IS_DELETED")
  private String isDeleted;

  @Column(name = "IS_VALID")
  private String isValid;

  @Column(name = "LINK_TO_INFO")
  private String linkToInfo;

  @Column(name = "LINK_TO_WP_TRAINING")
  private String linkToWpTraining;

  @Column(name = "MANDATORY_TESTDRIVE_FIND")
  private String mandatoryTestdriveFind;

  @Column(name = "MANDATORY_TESTDRIVE_RELEASE")
  private String mandatoryTestdriveRelease;

  @Column(name = "MCR_ADDITIONAL_INFO")
  private String mcrAdditionalInfo;

  @Column(name = "MCR_ID")
  private String mcrId;

  @Column(name = "MCR_NAME_OLD")
  private String mcrNameOld;

  @Column(name = "MCR_WPI_NUMBER")
  private String mcrWpiNumber;

  @Column(name = "MOD_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MOD_USER")
  private String modifiedUser;

  @Column(name = "OBD_RELEVANT_CRP")
  private String obdRelevantCrp;

  @Column(name = "OBD_RELEVANT_PFA")
  private String obdRelevantPfa;

  @Column(name = "\"VERSION\"")
  private Long version;

  @Column(name = "WP_DESCRIPTION_EXTERNAL")
  private String wpDescriptionExternal;

  @Column(name = "WP_DESCRIPTION_INTERNAL")
  private String wpDescriptionInternal;

  @Column(name = "WP_LONGNAME")
  private String wpLongname;

  @Column(name = "WP_MCR_NAME")
  private String wpMcrName;

  @Column(name = "WP_RESP_TE_NT_USER")
  private String wpRespTeNtUser;

  // bi-directional one-to-one association to TWorkpackage
  @OneToOne(mappedBy = "tWpmlWpMasterList", fetch = FetchType.LAZY)
  private TWorkpackage tWorkpackage;

  // bi-directional many-to-one association to TPidcVariantCocWP
  @OneToMany(mappedBy = "tWpmlWpMasterList", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvUseCaseSection> tabvUsecaseSection;

  public TWpmlWpMasterlist() {
    // empty constructor
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }


  public String getCocComment() {
    return this.cocComment;
  }

  public void setCocComment(final String cocComment) {
    this.cocComment = cocComment;
  }

  public Long getCocId() {
    return this.cocId;
  }

  public void setCocId(final Long cocId) {
    this.cocId = cocId;
  }

  public String getCompany() {
    return this.company;
  }

  public void setCompany(final String company) {
    this.company = company;
  }

  public String getCtdLink() {
    return this.ctdLink;
  }

  public void setCtdLink(final String ctdLink) {
    this.ctdLink = ctdLink;
  }

  public String getDatareviewMandatory() {
    return this.datareviewMandatory;
  }

  public void setDatareviewMandatory(final String datareviewMandatory) {
    this.datareviewMandatory = datareviewMandatory;
  }

  public String getEmissionRelevantCrpPfa() {
    return this.emissionRelevantCrpPfa;
  }

  public void setEmissionRelevantCrpPfa(final String emissionRelevantCrpPfa) {
    this.emissionRelevantCrpPfa = emissionRelevantCrpPfa;
  }

  public String getEmissionRelevantForTdec() {
    return this.emissionRelevantForTdec;
  }

  public void setEmissionRelevantForTdec(final String emissionRelevantForTdec) {
    this.emissionRelevantForTdec = emissionRelevantForTdec;
  }

  public String getEu6eImpact() {
    return this.eu6eImpact;
  }

  public void setEu6eImpact(final String eu6eImpact) {
    this.eu6eImpact = eu6eImpact;
  }

  public String getEu6eImpactDesc() {
    return this.eu6eImpactDesc;
  }

  public void setEu6eImpactDesc(final String eu6eImpactDesc) {
    this.eu6eImpactDesc = eu6eImpactDesc;
  }

  public String getGsIdOld() {
    return this.gsIdOld;
  }

  public void setGsIdOld(final String gsIdOld) {
    this.gsIdOld = gsIdOld;
  }

  public String getIsDeleted() {
    return this.isDeleted;
  }

  public void setIsDeleted(final String isDeleted) {
    this.isDeleted = isDeleted;
  }

  public String getIsValid() {
    return this.isValid;
  }

  public void setIsValid(final String isValid) {
    this.isValid = isValid;
  }

  public String getLinkToInfo() {
    return this.linkToInfo;
  }

  public void setLinkToInfo(final String linkToInfo) {
    this.linkToInfo = linkToInfo;
  }

  public String getLinkToWpTraining() {
    return this.linkToWpTraining;
  }

  public void setLinkToWpTraining(final String linkToWpTraining) {
    this.linkToWpTraining = linkToWpTraining;
  }

  public String getMandatoryTestdriveFind() {
    return this.mandatoryTestdriveFind;
  }

  public void setMandatoryTestdriveFind(final String mandatoryTestdriveFind) {
    this.mandatoryTestdriveFind = mandatoryTestdriveFind;
  }

  public String getMandatoryTestdriveRelease() {
    return this.mandatoryTestdriveRelease;
  }

  public void setMandatoryTestdriveRelease(final String mandatoryTestdriveRelease) {
    this.mandatoryTestdriveRelease = mandatoryTestdriveRelease;
  }

  public String getMcrAdditionalInfo() {
    return this.mcrAdditionalInfo;
  }

  public void setMcrAdditionalInfo(final String mcrAdditionalInfo) {
    this.mcrAdditionalInfo = mcrAdditionalInfo;
  }

  public String getMcrId() {
    return this.mcrId;
  }

  public void setMcrId(final String mcrId) {
    this.mcrId = mcrId;
  }

  public String getMcrNameOld() {
    return this.mcrNameOld;
  }

  public void setMcrNameOld(final String mcrNameOld) {
    this.mcrNameOld = mcrNameOld;
  }

  public String getMcrWpiNumber() {
    return this.mcrWpiNumber;
  }

  public void setMcrWpiNumber(final String mcrWpiNumber) {
    this.mcrWpiNumber = mcrWpiNumber;
  }

  public String getObdRelevantCrp() {
    return this.obdRelevantCrp;
  }

  public void setObdRelevantCrp(final String obdRelevantCrp) {
    this.obdRelevantCrp = obdRelevantCrp;
  }

  public String getObdRelevantPfa() {
    return this.obdRelevantPfa;
  }

  public void setObdRelevantPfa(final String obdRelevantPfa) {
    this.obdRelevantPfa = obdRelevantPfa;
  }


  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getWpDescriptionExternal() {
    return this.wpDescriptionExternal;
  }

  public void setWpDescriptionExternal(final String wpDescriptionExternal) {
    this.wpDescriptionExternal = wpDescriptionExternal;
  }

  public String getWpDescriptionInternal() {
    return this.wpDescriptionInternal;
  }

  public void setWpDescriptionInternal(final String wpDescriptionInternal) {
    this.wpDescriptionInternal = wpDescriptionInternal;
  }

  public String getWpLongname() {
    return this.wpLongname;
  }

  public void setWpLongname(final String wpLongname) {
    this.wpLongname = wpLongname;
  }

  public String getWpMcrName() {
    return this.wpMcrName;
  }

  public void setWpMcrName(final String wpMcrName) {
    this.wpMcrName = wpMcrName;
  }

  public String getWpRespTeNtUser() {
    return this.wpRespTeNtUser;
  }

  public void setWpRespTeNtUser(final String wpRespTeNtUser) {
    this.wpRespTeNtUser = wpRespTeNtUser;
  }


  /**
   * @return the tWorkpackage
   */
  public TWorkpackage gettWorkpackage() {
    return this.tWorkpackage;
  }


  /**
   * @param tWorkpackage the tWorkpackage to set
   */
  public void settWorkpackage(final TWorkpackage tWorkpackage) {
    this.tWorkpackage = tWorkpackage;
  }


  /**
   * @return the tabvUsecaseSection
   */
  public List<TabvUseCaseSection> getTabvUsecaseSection() {
    return this.tabvUsecaseSection;
  }


  /**
   * @param tabvUsecaseSection the tabvUsecaseSection to set
   */
  public void setTabvUsecaseSection(final List<TabvUseCaseSection> tabvUsecaseSection) {
    this.tabvUsecaseSection = tabvUsecaseSection;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
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
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
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


}