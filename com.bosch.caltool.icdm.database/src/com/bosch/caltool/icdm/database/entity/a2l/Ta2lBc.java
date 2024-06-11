package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the TA2L_BCS database table.
 */
@Entity
@Table(name = "TA2L_BCS")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class Ta2lBc implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  private BigDecimal id;

  @Column(name = "MODULE_ID")
  private BigDecimal moduleId;

  @Column(name = "SDOM_BC_ID")
  private BigDecimal sdomBcId;

  public Ta2lBc() {}

  public BigDecimal getId() {
    return this.id;
  }

  public void setId(final BigDecimal id) {
    this.id = id;
  }

  public BigDecimal getModuleId() {
    return this.moduleId;
  }

  public void setModuleId(final BigDecimal moduleId) {
    this.moduleId = moduleId;
  }

  public BigDecimal getSdomBcId() {
    return this.sdomBcId;
  }

  public void setSdomBcId(final BigDecimal sdomBcId) {
    this.sdomBcId = sdomBcId;
  }

}