package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

import com.bosch.ssd.icdm.entity.keys.VLdb2ConfigReleasePK;


/**
 * The persistent class for the V_LDB2_CONFIG_RELEASE database table.
 */
@Entity
@IdClass(VLdb2ConfigReleasePK.class)
@Table(name = "V_LDB2_CONFIG_RELEASE")
@NamedQueries({
    @NamedQuery(name = "VLdb2ConfigRelease.findAll", query = "SELECT v FROM VLdb2ConfigRelease v"),
    @NamedQuery(name = "VLdb2ConfigRelease.deleteConfigRelease", query = "DELETE FROM VLdb2ConfigRelease obj WHERE obj.proRelId = :NewProRelId") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2ConfigRelease implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * PRE REL ID
   */
  @Id
  @Column(name = "PRO_REL_ID")
  private BigDecimal proRelId;

  /**
   * SW Level
   */
  @Column(name = "SW_LEVEL")
  private BigDecimal swLevel;

  /**
   * SW Position
   */
  @Column(name = "SW_POSITION")
  private BigDecimal swPosition;
  /**
   * SW PRO REV ID
   */
  @Id
  @Column(name = "SW_PRO_REV_ID")
  private BigDecimal swProRevId;

  /**
   * SW Vers ID
   */
  @Column(name = "SW_VERSION_ID")
  private BigDecimal swVersionId;

  /**
   *
   */
  public VLdb2ConfigRelease() {
    // constructor
  }

  /**
   * @return proRelId
   */
  public BigDecimal getProRelId() {
    return this.proRelId;
  }

  /**
   * @param proRelId proRelId
   */
  public void setProRelId(final BigDecimal proRelId) {
    this.proRelId = proRelId;
  }

  /**
   * @return swLevel
   */
  public BigDecimal getSwLevel() {
    return this.swLevel;
  }

  /**
   * @param swLevel swLevel
   */
  public void setSwLevel(final BigDecimal swLevel) {
    this.swLevel = swLevel;
  }

  /**
   * @return swPosition
   */
  public BigDecimal getSwPosition() {
    return this.swPosition;
  }

  /**
   * @param swPosition swPosition
   */
  public void setSwPosition(final BigDecimal swPosition) {
    this.swPosition = swPosition;
  }

  /**
   * @return swProRevId
   */
  public BigDecimal getSwProRevId() {
    return this.swProRevId;
  }

  /**
   * @param swProRevId swProRevId
   */
  public void setSwProRevId(final BigDecimal swProRevId) {
    this.swProRevId = swProRevId;
  }

  /**
   * @return swVersionId
   */
  public BigDecimal getSwVersionId() {
    return this.swVersionId;
  }

  /**
   * @param swVersionId swVersionId
   */
  public void setSwVersionId(final BigDecimal swVersionId) {
    this.swVersionId = swVersionId;
  }

}