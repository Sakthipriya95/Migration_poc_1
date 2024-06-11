/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * Entity Class for V_LDB2_VILLA_SWVERS
 *
 * @author mrf5cob
 */
@Entity

@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedNativeQueries(

@NamedNativeQuery(name = "VLdb2VillaSwver.VillaSwver", query = "select OT.NODE_ID,SV.VERSIONNUMBER,SV.DESCRIPTION as NAME,OT.DESCRIPTION AS DESCRIPTION,SV.ID from V_LDB2_VILLA_SWVERS  sv inner join V_LDB2_OBJECT_TREE OT on sv.ID=OT.object_id WHERE SCOPE=8 and HIDE='N' and OT.PARENT_ID=?")

)

@Table(name = "V_LDB2_VILLA_SWVERS")
public class VLdb2VillaSwver implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Description
   */
  @OrderBy
  private String description;

  /**
   * Project ID
   */
  @Column(name = "FK_PROJECTID")
  private BigDecimal fkProjectid;

  /**
   * ID
   */
  @Id
  private BigDecimal id;

  /**
   * Version Number
   */
  private String versionnumber;

  /**
   *
   */
  public VLdb2VillaSwver() {
    // constructor
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return project id
   */
  public BigDecimal getFkProjectid() {
    return this.fkProjectid;
  }

  /**
   * @param fkProjectid project id
   */
  public void setFkProjectid(final BigDecimal fkProjectid) {
    this.fkProjectid = fkProjectid;
  }

  /**
   * @return id
   */
  public BigDecimal getId() {
    return this.id;
  }

  /**
   * @param id id
   */
  public void setId(final BigDecimal id) {
    this.id = id;
  }

  /**
   * @return version number
   */
  public String getVersionnumber() {
    return this.versionnumber;
  }

  /**
   * @param versionnumber version number
   */
  public void setVersionnumber(final String versionnumber) {
    this.versionnumber = versionnumber;
  }

}
