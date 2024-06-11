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
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

import com.bosch.ssd.icdm.entity.keys.VLdb2CompPK;


/**
 * The persistent class for the V_LDB2_COMP database table.
 */
@Entity
@Table(name = "V_LDB2_COMP")
@IdClass(VLdb2CompPK.class)
@NamedQuery(name = "VLdb2Comp.findAll", query = "SELECT v FROM VLdb2Comp v")
/**
 * Stored peocedure call - LDB2_SSD_CASES_HIST
 * 
 * @author SSN9COB
 */
@NamedStoredProcedureQuery(name = "VLdb2Comp.prcCaseHist", procedureName = "Ldb2_Ssd_Cases_Hist", parameters = {
    @StoredProcedureParameter(queryParameter = "labObjId", name = "pn_lab_obj_id", type = BigDecimal.class, direction = Direction.IN),
    @StoredProcedureParameter(queryParameter = "revId", name = "pn_rev_id", type = BigDecimal.class, direction = Direction.IN),
    @StoredProcedureParameter(queryParameter = "cases", name = "pc_case", type = String.class, direction = Direction.IN) })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2Comp implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Feature ID
   */
  @Id
  @Column(name = "FEATURE_ID")
  private BigDecimal featureId;

  /**
   * Historie
   */
  private String historie = "N";

  /**
   * LAB_OBJ_ID
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
   * Value ID
   */
  @Id
  @Column(name = "VALUE_ID")
  private BigDecimal valueId;

  /**
   * Comparison Operator ID
   */
  @Column(name = "COMPARE_OPR_ID")
  private BigDecimal operatorId;
  
  /**
   *
   */
  public VLdb2Comp() {
    // constructor
  }

  /**
   * @return feature id
   */
  public BigDecimal getFeatureId() {
    return this.featureId;
  }

  /**
   * @param featureId feature id
   */
  public void setFeatureId(final BigDecimal featureId) {
    this.featureId = featureId;
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
   * @return labobjid
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId lab obj id
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return rev id
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId rev id
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return value id
   */
  public BigDecimal getValueId() {
    return this.valueId;
  }

  /**
   * @param valueId value id
   */
  public void setValueId(final BigDecimal valueId) {
    this.valueId = valueId;
  }
  
  /**
   * @return the operatorId
   */
  public BigDecimal getOperatorId() {
    return this.operatorId;
  }
  
  /**
   * @param operatorId the operatorId to set
   */
  public void setOperatorId(BigDecimal operatorId) {
  
    this.operatorId = operatorId;
  }
}