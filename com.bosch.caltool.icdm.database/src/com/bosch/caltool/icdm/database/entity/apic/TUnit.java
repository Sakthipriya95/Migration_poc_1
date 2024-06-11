package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_UNITS database table.
 */
@Entity
@Table(name = "T_UNITS")
@NamedQueries(value = { @NamedQuery(name = TUnit.GET_ALL, query = "SELECT t FROM TUnit t") })
public class TUnit implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Get all A2L Resp
   */
  public static final String GET_ALL = "TUnit.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "ID")
  private long id;

  @Column(name = "UNIT")
  private String unit;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "CREATED_DATE")
  private String createdDate;

  /**
   * @return the unitId
   */
  public long getUnitId() {
    return this.id;
  }

  /**
   * @param unitId the unitId to set
   */
  public void setUnitId(final long unitId) {
    this.id = unitId;
  }

  /**
   * Defualt Constructor
   */
  public TUnit() {}

  /**
   * @return the Unit value
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * Set the Unit Not used
   *
   * @param unit
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

}