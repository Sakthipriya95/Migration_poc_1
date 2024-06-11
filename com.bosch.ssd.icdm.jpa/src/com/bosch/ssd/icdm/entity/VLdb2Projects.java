package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the V_LDB2_PROJECTS database table.
 *
 * @author SSN9COB
 */
@Entity
/**
 * Named Queries to add delete and get
 * 
 * @author SSN9COB
 */
@NamedQueries({
    @NamedQuery(name = "VLdb2Projects.AddingId", query = "Select obj from VLdb2Projects obj" +
        " where obj.villaSwproject=" + "(Select t.objectId from VLdb2ObjectTree t where " + "t.nodeId=(:parentId))"),

    @NamedQuery(name = "VLdb2Projects.DeleteProject", query = "Delete from VLdb2Projects l where l.villaSwproject=:ObjectId"),
    @NamedQuery(name = "VLdb2Projects.Id", query = "Select l from VLdb2Projects l where l.villaSwproject=:villaPrjId ") })


@NamedNativeQuery(name = "VLdb2Projects.AddingProjects", query = "INSERT into V_LDB2_PROJECTS(name,villa_swproject)" +
    "values(?,?)")
@Table(name = "V_LDB2_PROJECTS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2Projects implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  @Id
  private BigInteger id;

  /**
   * Name
   */
  private String name;

  /**
   * Villa Sw Project
   */
  @Column(name = "VILLA_SWPROJECT")
  private BigDecimal villaSwproject;

  /**
   *
   */
  public VLdb2Projects() {
    // constructor
  }

  /**
   * @return id
   */
  public BigInteger getId() {
    return this.id;
  }

  /**
   * @param id id
   */
  public void setId(final BigInteger id) {
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
   * @return project id
   */
  public BigDecimal getVillaSwproject() {
    return this.villaSwproject;
  }

  /**
   * @param villaSwproject peoject id
   */
  public void setVillaSwproject(final BigDecimal villaSwproject) {
    this.villaSwproject = villaSwproject;
  }

}