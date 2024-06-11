package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the V_LDB2_VALUES database table.
 *
 * @author SSN9COB
 */
@Entity


@Table(name = "V_LDB2_VALUES")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2Value implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Feature ID
   */
  @Column(name = "FEATURE_ID")
  private BigInteger featureId;

  /**
   * Value ID
   */
  @Id
  @Column(name = "VALUE_ID")
  private BigInteger valueId;

  /**
   * Value Text
   */
  @Column(name = "VALUE_TEXT")
  private String valueText;

  /**
   * 
   */
  public VLdb2Value() {
    // constructor
  }

  /**
   * @return feature id
   */
  public BigInteger getFeatureId() {
    return this.featureId;
  }

  /**
   * @param featureId - feature id
   */
  public void setFeatureId(final BigInteger featureId) {
    this.featureId = featureId;
  }

  /**
   * @return value id
   */
  public BigInteger getValueId() {
    return this.valueId;
  }

  /**
   * @param valueId value id
   */
  public void setValueId(final BigInteger valueId) {
    this.valueId = valueId;
  }

  /**
   * @return value text
   */
  public String getValueText() {
    return this.valueText;
  }

  /**
   * @param valueText value text
   */
  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }

}