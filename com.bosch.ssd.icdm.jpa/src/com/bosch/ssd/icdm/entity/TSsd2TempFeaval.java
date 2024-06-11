package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

import com.bosch.ssd.icdm.constants.JPAQueryConstants;
import com.bosch.ssd.icdm.entity.keys.TSsd2TempFeavalPK;


/**
 * The persistent class for the T_LDB2_TEMP_FEAVAL database table.
 */
@Entity
@Table(name = "T_SSD2_TEMP_FEAVAL")
@IdClass(TSsd2TempFeavalPK.class)
/**
 * Named Query to find all and delete all
 *
 * @author SSN9COB
 */
@NamedQuery(name = "TSsd2TempFeaval.findAll", query = "SELECT t FROM TSsd2TempFeaval t")
@NamedNativeQuery(name = "TSsd2TempFeaval.deleteAll", query = "delete FROM T_SSD2_TEMP_FEAVAL")

@NamedStoredProcedureQueries({
    /**
     * Stored Procedure Query for Check_Dependency_Valid
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "TSsd2TempFeaval.chkDependencyValid", procedureName = "CHK_DEPENDENCY_VALID", parameters = {
        @StoredProcedureParameter(queryParameter = "labObjId", name = "p_lab_obj_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "lablabId", name = "P_LAB_LAB_ID", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "objId1", name = "P_OBJ_ID_1", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "scope", name = "P_SCOPE", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "result", name = "N_RESULT", type = Integer.class, direction = Direction.OUT) }),
    /**
     * Stored procedure query for Populate Temp SSD
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = JPAQueryConstants.T_SSD2_TEMP_FEAVAL_POPULATE_TEMP_SSD, procedureName = "POPULATE_TEMP_SSD", parameters = {
        @StoredProcedureParameter(queryParameter = "objId1", name = "P_OBJ_ID_1", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "result", name = "N_RESULT", type = Integer.class, direction = Direction.OUT) }) })

@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TSsd2TempFeaval implements Serializable {

  /*
   * Serialize token
   */
  private static final long serialVersionUID = 1L;

  /*
   * Feature ID Column
   */
  @Id
  @Column(name = "FEATURE_ID")
  private BigDecimal featureId;

  /*
   * LAB_OBJ_ID Column
   */
  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  /*
   * Value Id Column
   */
  @Id
  @Column(name = "VALUE_ID")
  private BigDecimal valueId;
  /*
   * OPERATOR ID Column
   */
  @Column(name = "OPERATOR_ID")
  private BigDecimal operatorId;


  /**
   *
   */
  public TSsd2TempFeaval() {
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
   * @return lab obj id
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
  public void setOperatorId(final BigDecimal operatorId) {
    this.operatorId = operatorId;
  }
}