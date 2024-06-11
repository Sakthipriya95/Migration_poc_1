package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_RM_RISK_LEVEL database table.
 */
@Entity
@Table(name = "T_RM_RISK_LEVEL")
@NamedQuery(name = TRmRiskLevel.GET_ALL, query = "SELECT t FROM TRmRiskLevel t")
public class TRmRiskLevel implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TRmRiskLevel.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RISK_LEVEL_ID")
  private long riskLevelId;

  @Column(name = "RISK_LVL_CODE")
  private String riskCode;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "RISK_DESC_ENG")
  private String riskDescEng;

  @Column(name = "RISK_DESC_GER")
  private String riskDescGer;

  @Column(name = "RISK_TEXT_ENG")
  private String riskTextEng;

  @Column(name = "RISK_TEXT_GER")
  private String riskTextGer;

  @Column(name = "RISK_WEIGHT")
  private long riskWeight;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TRmCategoryMeasure
  @OneToMany(mappedBy = "TRmRiskLevel")
  private Set<TRmCategoryMeasure> TRmCategoryMeasures;

  // bi-directional many-to-one association to TRmCharacterCategoryMatrix
  @OneToMany(mappedBy = "TRmRiskLevel")
  private Set<TRmCharacterCategoryMatrix> TRmCharacterCategoryMatrixs;

  public TRmRiskLevel() {}

  public long getRiskLevelId() {
    return this.riskLevelId;
  }

  public void setRiskLevelId(final long riskLevelId) {
    this.riskLevelId = riskLevelId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getRiskDescEng() {
    return this.riskDescEng;
  }

  public void setRiskDescEng(final String riskDescEng) {
    this.riskDescEng = riskDescEng;
  }

  public String getRiskDescGer() {
    return this.riskDescGer;
  }

  public void setRiskDescGer(final String riskDescGer) {
    this.riskDescGer = riskDescGer;
  }

  public String getRiskTextEng() {
    return this.riskTextEng;
  }

  public void setRiskTextEng(final String riskTextEng) {
    this.riskTextEng = riskTextEng;
  }

  public String getRiskTextGer() {
    return this.riskTextGer;
  }

  public void setRiskTextGer(final String riskTextGer) {
    this.riskTextGer = riskTextGer;
  }

  public long getRiskWeight() {
    return this.riskWeight;
  }

  public void setRiskWeight(final long riskWeight) {
    this.riskWeight = riskWeight;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TRmCategoryMeasure> getTRmCategoryMeasures() {
    return this.TRmCategoryMeasures;
  }

  public void setTRmCategoryMeasures(final Set<TRmCategoryMeasure> TRmCategoryMeasures) {
    this.TRmCategoryMeasures = TRmCategoryMeasures;
  }

  public TRmCategoryMeasure addTRmCategoryMeasure(final TRmCategoryMeasure TRmCategoryMeasure) {
    getTRmCategoryMeasures().add(TRmCategoryMeasure);
    TRmCategoryMeasure.setTRmRiskLevel(this);

    return TRmCategoryMeasure;
  }

  public TRmCategoryMeasure removeTRmCategoryMeasure(final TRmCategoryMeasure TRmCategoryMeasure) {
    getTRmCategoryMeasures().remove(TRmCategoryMeasure);
    TRmCategoryMeasure.setTRmRiskLevel(null);

    return TRmCategoryMeasure;
  }

  public Set<TRmCharacterCategoryMatrix> getTRmCharacterCategoryMatrixs() {
    return this.TRmCharacterCategoryMatrixs;
  }

  public void setTRmCharacterCategoryMatrixs(final Set<TRmCharacterCategoryMatrix> TRmCharacterCategoryMatrixs) {
    this.TRmCharacterCategoryMatrixs = TRmCharacterCategoryMatrixs;
  }

  public TRmCharacterCategoryMatrix addTRmCharacterCategoryMatrix(
      final TRmCharacterCategoryMatrix TRmCharacterCategoryMatrix) {
    getTRmCharacterCategoryMatrixs().add(TRmCharacterCategoryMatrix);
    TRmCharacterCategoryMatrix.setTRmRiskLevel(this);

    return TRmCharacterCategoryMatrix;
  }

  public TRmCharacterCategoryMatrix removeTRmCharacterCategoryMatrix(
      final TRmCharacterCategoryMatrix TRmCharacterCategoryMatrix) {
    getTRmCharacterCategoryMatrixs().remove(TRmCharacterCategoryMatrix);
    TRmCharacterCategoryMatrix.setTRmRiskLevel(null);

    return TRmCharacterCategoryMatrix;
  }


  /**
   * @return the riskCode
   */
  public String getRiskCode() {
    return this.riskCode;
  }


  /**
   * @param riskCode the riskCode to set
   */
  public void setRiskCode(final String riskCode) {
    this.riskCode = riskCode;
  }

}