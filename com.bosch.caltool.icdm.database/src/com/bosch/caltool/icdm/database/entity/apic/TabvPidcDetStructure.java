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

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the TABV_PIDC_DET_STRUCTURE database table.
 */
@NamedQuery(name = TabvPidcDetStructure.GET_ALL, query = "select a from TabvPidcDetStructure a")
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "TABV_PIDC_DET_STRUCTURE")
public class TabvPidcDetStructure implements Serializable {

  private static final long serialVersionUID = 1L;


  /**
   * Named query .
   */
  public static final String GET_ALL = "TabvPidcDetStructure.getall";
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PDS_ID", unique = true, nullable = false, precision = 15)
  private long pdsId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PID_ATTR_LEVEL")
  private Long pidAttrLevel;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TPidcVersion tPidcVersion;

  public TabvPidcDetStructure() {}

  public long getPdsId() {
    return this.pdsId;
  }

  public void setPdsId(final long pdsId) {
    this.pdsId = pdsId;
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

  public Long getPidAttrLevel() {
    return this.pidAttrLevel;
  }

  public void setPidAttrLevel(final Long pidAttrLevel) {
    this.pidAttrLevel = pidAttrLevel;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }
}