package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * Icdm-870 new temp table for storing param name,type and Function name
 * The persistent class for the GTT_FUNCPARAMS database table.
 */
@Entity
@Table(name = "GTT_FUNCPARAMS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class GttFuncparam implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long id;

  @Column(name = "FUN_NAME")
  private String funName;

  @Column(name = "PARAM_NAME")
  private String paramName;

  @Column(name = "\"TYPE\"")
  private String type;

  public GttFuncparam() {
    // Empty Constructor
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getFunName() {
    return this.funName;
  }

  public void setFunName(final String funName) {
    this.funName = funName;
  }

  public String getParamName() {
    return this.paramName;
  }

  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

}