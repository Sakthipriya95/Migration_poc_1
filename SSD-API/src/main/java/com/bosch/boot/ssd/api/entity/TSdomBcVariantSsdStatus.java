package com.bosch.boot.ssd.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the T_SDOM_BC_VARIANT_SSD_STATUS database table.
 */
@Entity
@Table(name = "T_SDOM_BC_VARIANT_SSD_STATUS")
public class TSdomBcVariantSsdStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Long id;

  @Column(name = "CREATED_BY", length = 30)
  private String createdBy;

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATED_DATE")
  private Date createdDate;

  @Column(length = 1)
  private String deleted;

  @Column(name = "DELETED_BY", length = 30)
  private String deletedBy;

  @Temporal(TemporalType.DATE)
  @Column(name = "DELETED_DATE")
  private Date deletedDate;

  @Column(name = "EL_NUMMER", nullable = false)
  private BigDecimal elNummer;

  @Column(length = 1)
  private String ssd;

  @Column(length = 255)
  private String variant;

  /**
   * 
   */
  public TSdomBcVariantSsdStatus() {
    // public constructor
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getDeleted() {
    return this.deleted;
  }

  public void setDeleted(final String deleted) {
    this.deleted = deleted;
  }

  public String getDeletedBy() {
    return this.deletedBy;
  }

  public void setDeletedBy(final String deletedBy) {
    this.deletedBy = deletedBy;
  }

  public Date getDeletedDate() {
    return this.deletedDate;
  }

  public void setDeletedDate(final Date deletedDate) {
    this.deletedDate = deletedDate;
  }

  public BigDecimal getElNummer() {
    return this.elNummer;
  }

  public void setElNummer(final BigDecimal elNummer) {
    this.elNummer = elNummer;
  }

  public String getSsd() {
    return this.ssd;
  }

  public void setSsd(final String ssd) {
    this.ssd = ssd;
  }

  public String getVariant() {
    return this.variant;
  }

  public void setVariant(final String variant) {
    this.variant = variant;
  }

}