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
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;


/**
 * The persistent class for the T_A2L_VARGRP_VARIANT_MAPPING database table.
 */
@Entity
@Table(name = "T_A2L_VARGRP_VARIANT_MAPPING")

public class TA2lVarGrpVariantMapping implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  private static final long serialVersionUID = 1L;


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "VARGRP_VAR_MAP_ID")
  private long a2lVarGrpVarId;

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

  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne
  @JoinColumn(name = "VARIANT_ID")
  private TabvProjectVariant tabvProjectVariant;

  // bi-directional many-to-one association to TA2lVariantGroup
  @ManyToOne
  @JoinColumn(name = "A2L_VAR_GRP_ID")
  private TA2lVariantGroup tA2lVariantGroup;

  public TA2lVarGrpVariantMapping() {}

  public long getA2lVarGrpVarId() {
    return this.a2lVarGrpVarId;
  }

  public void setA2lVarGrpVarId(final long a2lVarGrpVarId) {
    this.a2lVarGrpVarId = a2lVarGrpVarId;
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

  public TabvProjectVariant getTabvProjectVariant() {
    return this.tabvProjectVariant;
  }

  public void setTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    this.tabvProjectVariant = tabvProjectVariant;
  }

  public TA2lVariantGroup getTA2lVariantGroup() {
    return this.tA2lVariantGroup;
  }

  public void setTA2lVariantGroup(final TA2lVariantGroup TA2lVariantGroup) {
    this.tA2lVariantGroup = TA2lVariantGroup;
  }

}