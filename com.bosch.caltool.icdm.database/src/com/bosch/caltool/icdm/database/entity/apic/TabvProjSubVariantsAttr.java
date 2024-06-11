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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * The persistent class for the TABV_PROJ_SUB_VARIANTS_ATTR database table.
 */
@Entity
@NamedQueries(value = {
    @NamedQuery(name = TabvProjSubVariantsAttr.GET_SUB_VARATTR_FOR_ATTRIBUTEID, query = "SELECT subVarAttr FROM TabvProjSubVariantsAttr subVarAttr where subVarAttr.tabvProjectSubVariant.subVariantId = :subVariantId and subVarAttr.tabvAttribute.attrId = :attrId") })
@Table(name = "TABV_PROJ_SUB_VARIANTS_ATTR")
public class TabvProjSubVariantsAttr implements Serializable {

  private static final long serialVersionUID = 1L;


  /*
   * Named query to get pidc subvariant attribute for given pidc version id and attribute id
   */
  public static final String GET_SUB_VARATTR_FOR_ATTRIBUTEID =
      "TabvProjSubVariantsAttr.getSubVariantAttributeByAttributeId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "SUB_VAR_ATTR_ID", unique = true, nullable = false, precision = 15)
  private long subVarAttrId;

  @Column(nullable = false, length = 1)
  private String used;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "PART_NUMBER", length = 255)
  private String partNumber;

  @Column(name = "SPEC_LINK", length = 1000)
  private String specLink;

  @Column(name = "DESCRIPTION", length = 4000)
  private String description;


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID", nullable = false)
  private TPidcVersion tPidcVersion;

  // bi-directional many-to-one association to TabvProjectSubVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SUB_VARIANT_ID", nullable = false)
  private TabvProjectSubVariant tabvProjectSubVariant;

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VARIANT_ID", nullable = false)
  private TabvProjectVariant tabvProjectVariant;

  public TabvProjSubVariantsAttr() {}

  public long getSubVarAttrId() {
    return this.subVarAttrId;
  }

  public void setSubVarAttrId(final long subVarAttrId) {
    this.subVarAttrId = subVarAttrId;
  }

  public String getUsed() {
    return this.used;
  }

  public void setUsed(final String used) {
    this.used = used;
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

  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getPartNumber() {
    return this.partNumber;
  }

  public void setPartNumber(final String partNumber) {
    this.partNumber = partNumber;
  }

  public String getSpecLink() {
    return this.specLink;
  }

  public void setSpecLink(final String specLink) {
    this.specLink = specLink;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }
}