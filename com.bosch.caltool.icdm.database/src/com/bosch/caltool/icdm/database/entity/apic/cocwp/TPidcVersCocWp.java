package com.bosch.caltool.icdm.database.entity.apic.cocwp;

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
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;


/**
 * The persistent class for the T_PIDC_VERS_COC_WP database table.
 */
@Entity
@Table(name = "T_PIDC_VERS_COC_WP")
public class TPidcVersCocWp implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PIDC_VERS_COC_WP_ID", unique = true, nullable = false, precision = 15)
  private long pidcVersCocWpId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "IS_AT_CHILD_LEVEL", nullable = false, length = 1)
  private String isAtChildLevel;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "USED_FLAG", nullable = false, length = 1)
  private String usedFlag;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TWorkpackageDivision
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_DIV_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TWorkpackageDivision twrkpkgdiv;

  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TPidcVersion tPidcVersion;

  public TPidcVersCocWp() {}

  public long getPidcVersCocWpId() {
    return this.pidcVersCocWpId;
  }

  public void setPidcVersCocWpId(final long pidcVersCocWpId) {
    this.pidcVersCocWpId = pidcVersCocWpId;
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

  public String getIsAtChildLevel() {
    return this.isAtChildLevel;
  }

  public void setIsAtChildLevel(final String isAtChildLevel) {
    this.isAtChildLevel = isAtChildLevel;
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

  public String getUsedFlag() {
    return this.usedFlag;
  }

  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the twrkpkgdiv
   */
  public TWorkpackageDivision getTWrkpkgdiv() {
    return this.twrkpkgdiv;
  }


  /**
   * @param twrkpkgdiv the twrkpkgdiv to set
   */
  public void setTWrkpkgdiv(final TWorkpackageDivision twrkpkgdiv) {
    this.twrkpkgdiv = twrkpkgdiv;
  }


  /**
   * @return the tPidcVersion
   */
  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }


  /**
   * @param tPidcVersion the tPidcVersion to set
   */
  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }

}
