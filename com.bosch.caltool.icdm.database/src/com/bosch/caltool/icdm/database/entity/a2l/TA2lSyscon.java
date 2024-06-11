/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * @author pdh2cob
 */
/**
 * The persistent class for the MV_SDOM_SYSCON database table.
 */
@Entity
@Table(name = "TA2L_SYSCONSTS")
@NamedQuery(name = TA2lSyscon.NQ_GET_SYSCON, query = "SELECT distinct(t.name) from TA2lSyscon t where t.name IN :sysconlist")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TA2lSyscon implements Serializable {


  /**
   * Named query : Get system constant objects from the given input list
   *
   * @param sysconlist input collection of system constants
   */
  public static final String NQ_GET_SYSCON = "TA2lSyscon.getsyscon";

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Syscon Id
   */
  @Id
  @Column(name = "SYSCON_ID")
  private Long id;
  /**
   * Name
   */
  @Column(name = "NAME")
  private String name;
  /**
   * Value
   */
  @Column(name = "VALUE")
  private String value;
  /**
   * Module Id
   */
  @Column(name = "MODULE_ID")
  private Long moduleId;

  /**
   * {@inheritDoc}
   */
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name set name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * @param value set value
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * @return moduleId
   */
  public Long getModuleId() {
    return this.moduleId;
  }

  /**
   * @param moduleId set moduleId
   */
  public void setModuleId(final Long moduleId) {
    this.moduleId = moduleId;
  }

}
