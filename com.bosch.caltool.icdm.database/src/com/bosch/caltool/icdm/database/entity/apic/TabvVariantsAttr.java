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
 * The persistent class for the TABV_VARIANTS_ATTR database table.
 */
@Entity
@NamedQueries(value = {
    @NamedQuery(name = TabvVariantsAttr.GET_VARATTR_FOR_ATTRIBUTEID, query = "SELECT varAttr FROM TabvVariantsAttr varAttr where varAttr.tabvProjectVariant.variantId = :variantId and varAttr.tabvAttribute.attrId = :attrId") })
@Table(name = "TABV_VARIANTS_ATTR")
public class TabvVariantsAttr implements Serializable {

  /*
   * Named query to get pidc variant attribute for given pidc version id and attribute id
   */
  public static final String GET_VARATTR_FOR_ATTRIBUTEID = "TabvVariantsAttr.getVariantAttributeByAttributeId";

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "VAR_ATTR_ID", unique = true, nullable = false, precision = 15)
  private long varAttrId;

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

  @Column(nullable = false, length = 1)
  private String used;

  @Column(name = "IS_SUBVARIANT", nullable = false, length = 1)
  private String isSubVariant;

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

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VARIANT_ID", nullable = false)
  private TabvProjectVariant tabvProjectVariant;

  public TabvVariantsAttr() {}

  public long getVarAttrId() {
    return this.varAttrId;
  }

  public void setVarAttrId(final long varAttrId) {
    this.varAttrId = varAttrId;
  }

  public String getUsed() {
    return this.used;
  }

  public void setUsed(final String used) {
    this.used = used;
  }

  public String getIsSubVariant() {
    return this.isSubVariant;
  }

  public void setIsSubVariant(final String isSubVariant) {
    this.isSubVariant = isSubVariant;
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