package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

import com.bosch.ssd.icdm.constants.JPAQueryConstants;


/**
 * The persistent class for the V_LDB2_FEATURES database table.
 *
 * @author SSN9COB
 */

@Entity
@Table(name = "V_LDB2_FEATURES")
@NamedNativeQuery(name = JPAQueryConstants.CHECK_VALID_OPERATOR, query = "Select a.valid_for_logical_compare from " +
    "V_LDB2_FEATURES a where a.feature_id = ? ")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Feature ID
   */
  @Id
  @Column(name = "FEATURE_ID")
  private BigInteger featureId;

  /**
   * Feature Text
   */
  @Column(name = "FEATURE_TEXT")
  private String featureText;

  /**
   * Feature Text
   */
  @Column(name = "VALID_FOR_LOGICAL_COMPARE")
  private String validOperator;

  
  /**
   * @return the validOperator
   */
  public String getValidOperator() {
    return validOperator;
  }

  /**
   * Empty Constructor
   */
  public VLdb2Feature() {
    // constructor
  }

  /**
   * @return featureId
   */
  public BigInteger getFeatureId() {
    return this.featureId;
  }

  /**
   * @param featureId feature id
   */
  public void setFeatureId(final BigInteger featureId) {
    this.featureId = featureId;
  }

  /**
   * @return feature Text
   */
  public String getFeatureText() {
    return this.featureText;
  }

  /**
   * @param featureText -feature Text
   */
  public void setFeatureText(final String featureText) {
    this.featureText = featureText;
  }

}