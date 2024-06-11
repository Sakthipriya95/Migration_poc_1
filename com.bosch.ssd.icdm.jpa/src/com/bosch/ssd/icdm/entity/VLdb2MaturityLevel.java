/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * The persistent class for the V_LDB2_MATURITY_LEVEL database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "V_LDB2_MATURITY_LEVEL")
@IdClass(com.bosch.ssd.icdm.entity.keys.VLdb2MaturityLevelPK.class)
@NamedQuery(name = "VLdb2MaturityLevel.findAll", query = "SELECT v FROM VLdb2MaturityLevel v")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2MaturityLevel {

  /**
   * Lab Obj Id
   */
  @Id
  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  /**
   * Rev id
   */
  @Id
  @Column(name = "REV_ID")
  private BigDecimal revId;

  /**
   * Historie
   */
  private String historie;
  /**
   * Maturity
   */
  private String maturity;

  /**
   * @return the labObjId
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId the labObjId to set
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return the revId
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId the revId to set
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return the historie
   */
  public String getHistorie() {
    return this.historie;
  }

  /**
   * @param historie the historie to set
   */
  public void setHistorie(final String historie) {
    this.historie = historie;
  }

  /**
   * @return the maturity
   */
  public String getMaturity() {
    return this.maturity;
  }

  /**
   * @param maturity the maturity to set
   */
  public void setMaturity(final String maturity) {
    this.maturity = maturity;
  }

}
