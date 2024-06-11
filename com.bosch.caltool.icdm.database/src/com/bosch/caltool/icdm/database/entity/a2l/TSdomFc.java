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
 * The persistent class for the T_SDOM_FCS database table.
 */
@Entity
@Table(name = "T_SDOM_FCS")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQueries({
    @NamedQuery(name = TSdomFc.GET_ALL, query = "SELECT t FROM TSdomFc t"),
    @NamedQuery(name = TSdomFc.GET_BY_BC_NAME, query = "SELECT DISTINCT fc.name FROM TSdomBc bc, TSdomFc fc, TSdomFc2bc fc2bc " +
        "where bc.name =:bcName and bc.id = fc2bc.bcId and fc.id = fc2bc.fcId") })
public class TSdomFc implements Serializable {

  /**
   * Constant for GET ALL Named Query
   */
  public static final String GET_ALL = "TSdomFc.GET_ALL";

  /**
   * Constant for GET_BY_BC_NAME Named Query
   */
  public static final String GET_BY_BC_NAME = "TSdomFc.GET_BY_BC_NAME";

  private static final long serialVersionUID = 1L;

  @Column(name = "\"CLASS\"")
  private String class_;

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATED_DATE")
  private Date createdDate;

  @Id
  private Long id;

  private String name;

  private String revision;

  private String variant;

  public TSdomFc() {}

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

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
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