package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_ALTERNATE_ATTR database table.
 */
@Entity
@Table(name = "T_ALTERNATE_ATTR")
@NamedQuery(name = "TAlternateAttr.findAll", query = "SELECT t FROM TAlternateAttr t")
public class TAlternateAttr implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "T_ALTERNATE_ATTR_ATTRPK_GENERATOR")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_ALTERNATE_ATTR_ATTRPK_GENERATOR")
  @Column(name = "ATTR_PK")
  private long attrPk;

  @Column(name = "ALTERNATE_ATTR_ID")
  private BigDecimal alternateAttrId;

  @Column(name = "ATTR_ID")
  private BigDecimal attrId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  private BigDecimal version;

  public TAlternateAttr() {}

  public long getAttrPk() {
    return this.attrPk;
  }

  public void setAttrPk(final long attrPk) {
    this.attrPk = attrPk;
  }

  public BigDecimal getAlternateAttrId() {
    return this.alternateAttrId;
  }

  public void setAlternateAttrId(final BigDecimal alternateAttrId) {
    this.alternateAttrId = alternateAttrId;
  }

  public BigDecimal getAttrId() {
    return this.attrId;
  }

  public void setAttrId(final BigDecimal attrId) {
    this.attrId = attrId;
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

  public BigDecimal getVersion() {
    return this.version;
  }

  public void setVersion(final BigDecimal version) {
    this.version = version;
  }

}