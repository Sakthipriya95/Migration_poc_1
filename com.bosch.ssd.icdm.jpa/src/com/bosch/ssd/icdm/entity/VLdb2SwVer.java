package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * The persistent class for the V_LDB2_SW_VERS database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "V_LDB2_SW_VERS")
@NamedQuery(name = "VLdb2SwVer.findVersId", query = "SELECT sw.versId " + "FROM VLdb2SwVer sw, VLdb2Projects prj " +
    "WHERE prj.id = sw.id " + "AND prj.villaSwproject = :villaSwproject " + "AND sw.villaSwversId = :villaSwversId ")
@NamedQueries({
    @NamedQuery(name = "VLdb2SwVer.CountingSwVer", query = "SELECT count(l) FROM VLdb2SwVer l " +
        "where l.villaSwversId=:objectId and l.id=:Id "),

    @NamedQuery(name = "VLdb2SwVer.DeleteVersion", query = "Delete from VLdb2SwVer l " +
        "where l.id=(:Id) and l.villaSwversId=(:ObjectId)"),

    @NamedQuery(name = "VLdb2SwVer.Id", query = "Select l from VLdb2SwVer l where l.id=:projectId  and " +
        "l.villaSwversId=:villaSwId")

})
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2SwVer implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private BigDecimal id;

  /**
   * Name
   */
  private String name;

  /**
   * Vers ID
   */
  @Column(name = "VERS_ID")
  @Id
  private BigInteger versId;

  /**
   * Villa SW Vers ID
   */
  @Column(name = "VILLA_SWVERS_ID")
  private BigDecimal villaSwversId;

  /**
   * Constructor
   */
  public VLdb2SwVer() {
    // constructor
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
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return version id
   */
  public BigInteger getVersId() {
    return this.versId;
  }

  /**
   * @param versId version id
   */
  public void setVersId(final BigInteger versId) {
    this.versId = versId;
  }

  /**
   * @return version id
   */
  public BigDecimal getVillaSwversId() {
    return this.villaSwversId;
  }

  /**
   * @param villaSwversId version id
   */
  public void setVillaSwversId(final BigDecimal villaSwversId) {
    this.villaSwversId = villaSwversId;
  }

}