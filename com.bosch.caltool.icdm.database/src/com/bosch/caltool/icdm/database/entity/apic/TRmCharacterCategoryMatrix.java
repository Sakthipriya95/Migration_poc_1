package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_RM_CHARACTER_CATEGORY_MATRIX database table.
 */
@Entity
@Table(name = "T_RM_CHARACTER_CATEGORY_MATRIX")
@NamedQuery(name = "TRmCharacterCategoryMatrix.findAll", query = "SELECT t FROM TRmCharacterCategoryMatrix t")
public class TRmCharacterCategoryMatrix implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CCM_ID", unique = true, nullable = false)
  private long ccmId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TRmCategory
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CATEGORY_ID")
  private TRmCategory TRmCategory;

  // bi-directional many-to-one association to TRmProjectCharacter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PRJ_CHARACTER_ID")
  private TRmProjectCharacter TRmProjectCharacter;

  // bi-directional many-to-one association to TRmRiskLevel
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RISK_LEVEL_ID")
  private TRmRiskLevel TRmRiskLevel;

  public TRmCharacterCategoryMatrix() {}

  public long getCcmId() {
    return this.ccmId;
  }

  public void setCcmId(final long ccmId) {
    this.ccmId = ccmId;
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

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TRmCategory getTRmCategory() {
    return this.TRmCategory;
  }

  public void setTRmCategory(final TRmCategory TRmCategory) {
    this.TRmCategory = TRmCategory;
  }

  public TRmProjectCharacter getTRmProjectCharacter() {
    return this.TRmProjectCharacter;
  }

  public void setTRmProjectCharacter(final TRmProjectCharacter TRmProjectCharacter) {
    this.TRmProjectCharacter = TRmProjectCharacter;
  }

  public TRmRiskLevel getTRmRiskLevel() {
    return this.TRmRiskLevel;
  }

  public void setTRmRiskLevel(final TRmRiskLevel TRmRiskLevel) {
    this.TRmRiskLevel = TRmRiskLevel;
  }

}