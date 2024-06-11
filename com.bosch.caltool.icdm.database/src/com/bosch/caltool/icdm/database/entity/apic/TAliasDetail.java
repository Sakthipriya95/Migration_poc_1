package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;

/**
 * The persistent class for the T_ALIAS_DETAILS database table.
 */
@Entity
@Table(name = "T_ALIAS_DETAILS")
@OptimisticLocking(cascade = true)
@NamedQuery(name = "TAliasDetail.findAll", query = "SELECT t FROM TAliasDetail t")
public class TAliasDetail implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "ALIAS_DETAILS_ID")
  private long aliasDetailsId;


  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "ALIAS_NAME")
  private String aliasName;


  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "ATTR_ID")
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "VALUE_ID")
  private TabvAttrValue tabvAttrValue;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TAliasDefinition
  @ManyToOne
  @JoinColumn(name = "AD_ID")
  private TAliasDefinition tAliasDefinition;

  public TAliasDetail() {}

  public long getAliasDetailsId() {
    return this.aliasDetailsId;
  }

  public void setAliasDetailsId(final long aliasDetailsId) {
    this.aliasDetailsId = aliasDetailsId;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
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

  public String getAliasName() {
    return this.aliasName;
  }

  public void setAliasName(final String aliasName) {
    this.aliasName = aliasName;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TAliasDefinition getTAliasDefinition() {
    return this.tAliasDefinition;
  }

  public void setTAliasDefinition(final TAliasDefinition TAliasDefinition) {
    this.tAliasDefinition = TAliasDefinition;
  }

}