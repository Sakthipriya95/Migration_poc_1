package com.bosch.boot.ssd.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the V_SDOM_BC_SSDINFO database table.
 */
@Entity
@Table(name = "V_SDOM_BC_SSDINFO")
public class VSdomBcSsdinfo implements Serializable {

  private static final long serialVersionUID = -5050753366983671721L;

  @Column(name = "EL_NAME", nullable = false, length = 50)
  private String bcName;

  @Column(name = "EL_NUMMER", nullable = false)
  private BigDecimal bcNumber;

  @Id
  private String idc;

  @Column(name = "NODE_NAME", insertable = false, updatable = false, length = 4000)
  private String nodeName;

  @Column(name = "REVISION", nullable = false)
  private BigDecimal bcRevision;

  @Column(name = "SSD_STATUS", nullable = false, length = 30)
  private String ssdStatus;

  @Column(name = "VARIANT", nullable = false, length = 32)
  private String bcVariant;

  @Column(name = "SSD", nullable = false, length = 2)
  private String varSsdStatus;

  /**
   * @return
   */
  public String getBcName() {
    return this.bcName;
  }

  /**
   * @param bcName
   */
  public void setBcName(String bcName) {
    this.bcName = bcName;
  }

  /**
   * @return
   */
  public BigDecimal getBcNumber() {
    return this.bcNumber;
  }

  /**
   * @param bcNumber
   */
  public void setBcNumber(BigDecimal bcNumber) {
    this.bcNumber = bcNumber;
  }

  /**
   * @return
   */
  public String getIdc() {
    return this.idc;
  }

  /**
   * @param idc
   */
  public void setIdc(String idc) {
    this.idc = idc;
  }

  /**
   * @return
   */
  public String getNodeName() {
    return this.nodeName;
  }

  /**
   * @param nodeName
   */
  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  /**
   * @return
   */
  public BigDecimal getBcRevision() {
    return this.bcRevision;
  }

  /**
   * @param bcRevision
   */
  public void setBcRevision(BigDecimal bcRevision) {
    this.bcRevision = bcRevision;
  }

  /**
   * @return
   */
  public String getSsdStatus() {
    return this.ssdStatus;
  }

  /**
   * @param ssdStatus
   */
  public void setSsdStatus(String ssdStatus) {
    this.ssdStatus = ssdStatus;
  }

  /**
   * @return
   */
  public String getBcVariant() {
    return this.bcVariant;
  }

  /**
   * @param bcVariant
   */
  public void setBcVariant(String bcVariant) {
    this.bcVariant = bcVariant;
  }


  /**
   * @return the varSsdStatus
   */
  public String getVarSsdStatus() {
    return this.varSsdStatus;
  }


  /**
   * @param varSsdStatus the varSsdStatus to set
   */
  public void setVarSsdStatus(final String varSsdStatus) {
    this.varSsdStatus = varSsdStatus;
  }
}