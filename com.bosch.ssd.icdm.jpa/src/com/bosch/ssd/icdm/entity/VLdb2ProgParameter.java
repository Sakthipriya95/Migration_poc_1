package com.bosch.ssd.icdm.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

import com.bosch.ssd.icdm.entity.keys.VLDB2ProgParameterPK;


/**
 * The persistent class for the V_LDB2_PROG_PARAMETER database table.
 *
 * @author SSN9COB
 */
@IdClass(VLDB2ProgParameterPK.class)
@Entity
@Table(name = "V_LDB2_PROG_PARAMETER")
@NamedQueries({
    @NamedQuery(name = "VLdb2ProgParameter.findParameter", query = "SELECT par.wert " + "FROM VLdb2ProgParameter par " +
        "WHERE par.typ = :typ ") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2ProgParameter implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Type
   */
  @Id
  private String typ;

  /**
   * UserName
   */
  @Column(name = "USER_NAME")
  @Id
  private String userName;

  /**
   * Wert
   */
  private String wert;

  /**
   * Default Constructor
   */
  public VLdb2ProgParameter() {
    // constructor
  }

  /**
   * @return type
   */
  public String getTyp() {
    return this.typ;
  }

  /**
   * @param typ type
   */
  public void setTyp(final String typ) {
    this.typ = typ;
  }

  /**
   * @return user name
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @param userName user name
   */
  public void setUserName(final String userName) {
    this.userName = userName;
  }

  /**
   * @return wert
   */
  public String getWert() {
    return this.wert;
  }

  /**
   * @param wert wert
   */
  public void setWert(final String wert) {
    this.wert = wert;
  }

}