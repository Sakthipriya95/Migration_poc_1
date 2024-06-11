package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the TEMP_LDB2_FEAVAL database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "TEMP_LDB2_FEAVAL")
/**
 * Named Queries for Find, Trunc and get error Count
 *
 * @author SSN9COB
 */
@NamedQueries({
    @NamedQuery(name = "TempLdb2Feaval.findAll", query = "SELECT t FROM TempLdb2Feaval t"),
    @NamedQuery(name = "TempLdb2Feaval.truncTable", query = "delete from TempLdb2Feaval feaVal where feaVal.relId=:relId") })
@NamedNativeQuery(name = "VLdb2ProError.ReleaseErrorCnt", query = " SELECT  Count(*)FROM  v_ldb2_pro_errors" +
    " WHERE pro_rel_id=?" + " AND  error_nr <> 6")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@IdClass(com.bosch.ssd.icdm.entity.keys.TempLdb2FeavalPK.class)
public class TempLdb2Feaval implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Feature ID
   */
  @Id
  @Column(name = "FEATURE_ID")
  private BigDecimal featureId;

  /**
   * Rel ID
   */
  @Id
  @Column(name = "REL_ID")
  private BigDecimal relId;

  /**
   * Value ID
   */
  @Column(name = "VALUE_ID")
  private BigDecimal valueId;

  /**
   *
   */
  public TempLdb2Feaval() {
    // constructor
  }

  /**
   * @return feature id
   */
  public BigDecimal getFeatureId() {
    return this.featureId;
  }

  /**
   * @param featureId -feature id
   */
  public void setFeatureId(final BigDecimal featureId) {
    this.featureId = featureId;
  }

  /**
   * @return rel id
   */
  public BigDecimal getRelId() {
    return this.relId;
  }

  /**
   * @param relId rel id
   */
  public void setRelId(final BigDecimal relId) {
    this.relId = relId;
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

}