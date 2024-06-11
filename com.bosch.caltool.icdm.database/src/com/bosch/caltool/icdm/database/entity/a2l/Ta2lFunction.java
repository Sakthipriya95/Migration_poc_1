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
 * The persistent class for the TA2L_FUNCTIONS database table.
 */
@Entity
@Table(name = "TA2L_FUNCTIONS")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class Ta2lFunction implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "FUNCTION_ID")
  private BigDecimal functionId;

  private String functionversion;

  private String longidentifier;

  @Column(name = "MODULE_ID")
  private BigDecimal moduleId;

  private String name;

  @Column(name = "A2L_BC_ID")
  private BigDecimal a2lBcId;

  public Ta2lFunction() {}

  public BigDecimal getFunctionId() {
    return this.functionId;
  }

  public void setFunctionId(final BigDecimal functionId) {
    this.functionId = functionId;
  }

  public String getFunctionversion() {
    return this.functionversion;
  }

  public void setFunctionversion(final String functionversion) {
    this.functionversion = functionversion;
  }

  public String getLongidentifier() {
    return this.longidentifier;
  }

  public void setLongidentifier(final String longidentifier) {
    this.longidentifier = longidentifier;
  }

  public BigDecimal getModuleId() {
    return this.moduleId;
  }

  public void setModuleId(final BigDecimal moduleId) {
    this.moduleId = moduleId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public BigDecimal getA2lBcId() {
    return this.a2lBcId;
  }

  public void seta2lBcId(final BigDecimal sdomBcId) {
    this.a2lBcId = sdomBcId;
  }

}