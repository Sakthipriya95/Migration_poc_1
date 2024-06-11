package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_SDOM_BCS database table.
 */
@Entity
@Table(name = "T_SDOM_BCS")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQueries({
    @NamedQuery(name = "TSdomBc.GET_ALL_DISTINCT", query = "SELECT distinct t.name,t.description FROM TSdomBc t"),
    @NamedQuery(name = "TSdomBc.GET_BY_NAME", query = "SELECT distinct t.description FROM TSdomBc t where t.name =:bcName") })
public class TSdomBc implements Serializable {

  /**
   * Constant for Get All Named Query
   */
  public static final String GET_ALL_DISTINCT_VALUES = "TSdomBc.GET_ALL_DISTINCT";

  /**
   * Constant for GET description by bc name
   */
  public static final String GET_BY_NAME = "TSdomBc.GET_BY_NAME";

  private static final long serialVersionUID = 1L;

  @Column(name = "CLASS")
  private String class_;

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATED_DATE")
  private Date createdDate;

  private String description;
  @Id
  private Long id;

  @Column(name = "LIFECYCLE_STATE")
  private String lifecycleState;

  private String name;

  private String revision;

  private String variant;

  public TSdomBc() {}

  public String getClass_() {
    return this.class_;
  }

  public void setClass_(final String class_) {
    this.class_ = class_;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getLifecycleState() {
    return this.lifecycleState;
  }

  public void setLifecycleState(final String lifecycleState) {
    this.lifecycleState = lifecycleState;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getRevision() {
    return this.revision;
  }

  public void setRevision(final String revision) {
    this.revision = revision;
  }

  public String getVariant() {
    return this.variant;
  }

  public void setVariant(final String variant) {
    this.variant = variant;
  }

}