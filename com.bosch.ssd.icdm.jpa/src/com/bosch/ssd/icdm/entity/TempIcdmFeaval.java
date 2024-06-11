/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * Temp ICDM Fea Val Table
 * 
 * @author mrf5cob
 */
@Entity
@Table(name = "K5ESK_LDB2.TEMP_ICDM_FEAVAL")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
/*
 * Delete All from TempIcdmFeaVal
 */
@NamedNativeQuery(name = "TempIcdmFeaval.deleteAll", query = "delete from TEMP_ICDM_FEAVAL")
/*
 * Find ALL from TempiCDMFeaVal
 */
@NamedQuery(name = "TempIcdmFeaval.findAll", query = "SELECT t FROM TempIcdmFeaval t")
@IdClass(com.bosch.ssd.icdm.entity.keys.TSsd2TempFeavalPK.class)
public class TempIcdmFeaval {

  /**
   * Feature ID
   */
  @Id
  @Column(name = "FEATURE_ID")
  private BigDecimal featureId;

  /**
   * Value ID
   */
  @Id
  @Column(name = "VALUE_ID")
  private BigDecimal valueId;

  /**
   * Label ID
   */
  @Column(name = "LAB_ID")
  private BigDecimal labId;

  /**
   * @return the featureId
   */
  public BigDecimal getFeatureId() {
    return this.featureId;
  }


  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(final BigDecimal featureId) {
    this.featureId = featureId;
  }


  /**
   * @return the relId
   */
  public BigDecimal getValueId() {
    return this.valueId;
  }


  /**
   * @param valueId the valueId to set
   */
  public void setValueId(final BigDecimal valueId) {
    this.valueId = valueId;
  }


  /**
   * @return the labId
   */
  public BigDecimal getLabId() {
    return this.labId;
  }


  /**
   * @param labId the labId to set
   */
  public void setLabId(final BigDecimal labId) {
    this.labId = labId;
  }
}
