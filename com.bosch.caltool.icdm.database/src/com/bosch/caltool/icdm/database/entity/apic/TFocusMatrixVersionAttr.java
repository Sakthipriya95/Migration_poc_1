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
 * The persistent class for the T_FOCUS_MATRIX_VERSION_ATTR database table.
 */
@Entity
@Table(name = "T_FOCUS_MATRIX_VERSION_ATTR")
@NamedQuery(name = "TFocusMatrixVersionAttr.GET_ALL_FOCUS_MATRIX_VERSION_ATTR", query = "SELECT t FROM TFocusMatrixVersionAttr t")
public class TFocusMatrixVersionAttr implements Serializable {

  /**
   * Named query to get all focus matrix version attributes
   */
  public static final String GET_ALL_FOCUS_MATRIX_VERSION_ATTR =
      "TFocusMatrixVersionAttr.GET_ALL_FOCUS_MATRIX_VERSION_ATTR";
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FMV_ATTR_ID", unique = true, nullable = false)
  private long fmvAttrId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(nullable = false, length = 1)
  private String used;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  @Column(name = "FM_ATTR_REMARK", length = 4000)
  private String fmAttrRemark;


  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID")
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvProjectSubVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SUB_VARIANT_ID")
  private TabvProjectSubVariant tabvProjectSubVariant;

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VARIANT_ID")
  private TabvProjectVariant tabvProjectVariant;

  // bi-directional many-to-one association to TFocusMatrixVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FM_VERS_ID", nullable = false)
  private TFocusMatrixVersion tFocusMatrixVersion;

  public TFocusMatrixVersionAttr() {}

  public long getFmvAttrId() {
    return this.fmvAttrId;
  }

  public void setFmvAttrId(final long fmvAttrId) {
    this.fmvAttrId = fmvAttrId;
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

  public String getUsed() {
    return this.used;
  }

  public void setUsed(final String used) {
    this.used = used;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TabvProjectSubVariant getTabvProjectSubVariant() {
    return this.tabvProjectSubVariant;
  }

  public void setTabvProjectSubVariant(final TabvProjectSubVariant tabvProjectSubVariant) {
    this.tabvProjectSubVariant = tabvProjectSubVariant;
  }

  public TabvProjectVariant getTabvProjectVariant() {
    return this.tabvProjectVariant;
  }

  public void setTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    this.tabvProjectVariant = tabvProjectVariant;
  }

  public TFocusMatrixVersion getTFocusMatrixVersion() {
    return this.tFocusMatrixVersion;
  }

  public void setTFocusMatrixVersion(final TFocusMatrixVersion tFocusMatrixVersion) {
    this.tFocusMatrixVersion = tFocusMatrixVersion;
  }

  /**
   * @return the fmAttrRemark
   */
  public String getFmAttrRemark() {
    return this.fmAttrRemark;
  }


  /**
   * @param fmAttrRemark the fmAttrRemark to set
   */
  public void setFmAttrRemark(final String fmAttrRemark) {
    this.fmAttrRemark = fmAttrRemark;
  }

}