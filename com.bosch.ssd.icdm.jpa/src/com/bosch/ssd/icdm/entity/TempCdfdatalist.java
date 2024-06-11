package com.bosch.ssd.icdm.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.bosch.ssd.icdm.entity.keys.TempCdfdatalistPK;


/**
 * The persistent class for the TEMP_CDFDATALIST database table.
 */
@Entity
@IdClass(TempCdfdatalistPK.class)
@Table(name = "TEMP_CDFDATALIST_COMMENTS")
@NamedQuery(name = "TempCdfdatalist.getCdfFileData", query = "select OBJ from TempCdfdatalist OBJ WHERE OBJ.uniId = :uniId ", hints = {
    @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "1000"),
    @QueryHint(name = QueryHints.MAINTAIN_CACHE, value = HintValues.FALSE) })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TempCdfdatalist implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Node Info
   */
  @Column(length = 1000, name = "NODE_INF")
  private String nodeInf;

  /**
   * Maturity
   */
  private String maturity;

  /**
   * Use Case
   */
  private String usecase;

  /**
   * Feature Value
   */
  @Column(length = 1000, name = "FEATUREVALUE")
  private String feVal;

  /**
   * Label
   */
  @Id
  @Column(length = 255)
  private String label;

  /**
   * State
   */
  @Column(length = 255)
  private String state;

  /**
   * Data Descriptio
   */
  @Column(name = "DATA_DESC")
  private String tempDataDesc;

  /**
   * Unique ID
   */
  @Id
  @Column(name = "UNI_ID", length = 100)
  private String uniId;

  /**
   *
   */
  public TempCdfdatalist() {
    // constructor
  }


  /**
   * @return label
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * @param label -set label
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  /**
   * @return state
   */
  public String getState() {
    return this.state;
  }

  /**
   * @param state - state
   */
  public void setState(final String state) {
    this.state = state;
  }

  /**
   * @return unique id
   */
  public String getUniId() {
    return this.uniId;
  }

  /**
   * @param uniId unique id
   */
  public void setUniId(final String uniId) {
    this.uniId = uniId;
  }

  /**
   * @return the tempDataDesc
   */
  public String getTempDataDesc() {
    return this.tempDataDesc;
  }

  /**
   * @param tempDataDesc the tempDataDesc to set
   */
  public void setTempDataDesc(final String tempDataDesc) {
    this.tempDataDesc = tempDataDesc;
  }


  /**
   * @return the nodeInf
   */
  public String getNodeInf() {
    return this.nodeInf;
  }


  /**
   * @param nodeInf the nodeInf to set
   */
  public void setNodeInf(final String nodeInf) {
    this.nodeInf = nodeInf;
  }


  /**
   * @return the maturity
   */
  public String getMaturity() {
    return this.maturity;
  }


  /**
   * @param maturity the maturity to set
   */
  public void setMaturity(final String maturity) {
    this.maturity = maturity;
  }


  /**
   * @return the usecase
   */
  public String getUsecase() {
    return this.usecase;
  }


  /**
   * @param usecase the usecase to set
   */
  public void setUsecase(final String usecase) {
    this.usecase = usecase;
  }


  /**
   * @return the feVal
   */
  public String getFeVal() {
    return this.feVal;
  }


  /**
   * @param feVal the feVal to set
   */
  public void setFeVal(final String feVal) {
    this.feVal = feVal;
  }

}