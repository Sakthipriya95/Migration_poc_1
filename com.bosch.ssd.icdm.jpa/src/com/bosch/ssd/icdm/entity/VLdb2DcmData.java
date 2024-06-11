package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

import com.bosch.ssd.icdm.entity.keys.VLdb2DcmDataPK;


/**
 * The persistent class for the V_LDB2_DCM_DATA database table.
 */
@IdClass(VLdb2DcmDataPK.class)
@Entity
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@Table(name = "V_LDB2_DCM_DATA")
@NamedQuery(name = "VLdb2DcmData.findAll", query = "SELECT v FROM VLdb2DcmData v")
public class VLdb2DcmData implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Data
   */
  @Column(name = "\"DATA\"")
  private String data;

  /**
   * Historie
   */
  private String historie = "N";

  /**
   * Lab_obj_id
   */
  @Id
  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  /**
   * Rev ID
   */
  @Id
  @Column(name = "REV_ID")
  private BigDecimal revId;

  /**
   * ROW NUMBER
   */
  @Id
  @Column(name = "ROW_NUMBER")
  private BigDecimal rowNumber;

  /**
   *
   */
  public VLdb2DcmData() {
    // constructor
  }

  /**
   * @return data
   */
  public String getData() {
    return this.data;
  }

  /**
   * @param data data
   */
  public void setData(final String data) {
    this.data = data;
  }

  /**
   * @return historie
   */
  public String getHistorie() {
    return this.historie;
  }

  /**
   * @param historie historie
   */
  public void setHistorie(final String historie) {
    this.historie = historie;
  }

  /**
   * @return labObjId
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId labObjId
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return revId
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId revId
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return rownumber
   */
  public BigDecimal getRowNumber() {
    return this.rowNumber;
  }

  /**
   * @param rowNumber row
   */
  public void setRowNumber(final BigDecimal rowNumber) {
    this.rowNumber = rowNumber;
  }

}