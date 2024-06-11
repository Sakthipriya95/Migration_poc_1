package com.bosch.caltool.icdm.database.entity.a2l;

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

import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;


/**
 * The persistent class for the T_A2L_RESPONSIBLITY_BSHGRP_USR database table.
 */
@Entity
@Table(name = "T_A2L_RESPONSIBLITY_BSHGRP_USR")
@NamedQuery(name = TA2lResponsiblityBshgrpUsr.GET_ALL, query = "SELECT t FROM TA2lResponsiblityBshgrpUsr t")
public class TA2lResponsiblityBshgrpUsr implements Serializable {


  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TA2lResponsiblityBshgrpUsr.findAll";

  public TA2lResponsiblityBshgrpUsr() {
    //
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2LRESP_BSHGRP_USR_ID")
  private long a2lrespBshgrpUsrId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private TabvApicUser tabvApicUser;

  // bi-directional many-to-one association to TA2lResponsibility
  @ManyToOne
  @JoinColumn(name = "A2L_RESP_ID")
  private TA2lResponsibility tA2lResponsibility;

  public long getA2lrespBshgrpUsrId() {
    return this.a2lrespBshgrpUsrId;
  }

  public void setA2lrespBshgrpUsrId(final long a2lrespBshgrpUsrId) {
    this.a2lrespBshgrpUsrId = a2lrespBshgrpUsrId;
  }


  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }

  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }

  public TA2lResponsibility getTA2lResponsibility() {
    return this.tA2lResponsibility;
  }

  public void setTA2lResponsibility(final TA2lResponsibility tA2lResponsibility) {
    this.tA2lResponsibility = tA2lResponsibility;
  }

}