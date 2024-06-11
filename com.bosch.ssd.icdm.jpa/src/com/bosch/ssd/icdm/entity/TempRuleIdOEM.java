/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * @author SSN9COB The persistent class for the TEMP_RULE_ID_OEM database table.
 */
@Entity
@Table(name = "TEMP_RULE_ID_OEM")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
/**
 * Delete all rule info
 * 
 * @author SSN9COB
 */
@NamedNativeQuery(name = "TempRuleIdOEM.deleteTempValues", query = "delete from TEMP_RULE_ID_OEM ")
public class TempRuleIdOEM implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Lab Obj Id
   */
  @Id
  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  /**
   * Rev_id
   */
  @Column(name = "REV_ID")
  private BigDecimal revId;

  /**
   *
   */
  public TempRuleIdOEM() {
    // Auto-generated constructor stub
  }

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

}
