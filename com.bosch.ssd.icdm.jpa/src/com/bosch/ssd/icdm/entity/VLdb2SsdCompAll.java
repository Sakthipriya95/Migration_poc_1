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


/**
 * The persistent class for the V_LDB2_SSD_COMP_ALL database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "V_LDB2_SSD_COMP_ALL")
@NamedQuery(name = "VLdb2SsdCompAll.findfeaval", query = "SELECT v FROM VLdb2SsdCompAll v" +
    " where v.labObjId=:labObjId and v.revId=:revId and v.historie='N'")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@IdClass(com.bosch.ssd.icdm.entity.keys.VLdb2SsdCompAllPK.class)
public class VLdb2SsdCompAll implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Feature ID
   */
  @Column(name = "FEATURE_ID")
  @Id
  private BigDecimal featureId;

  /**
   * Feature Text
   */
  @Column(name = "FEATURE_TEXT")
  private String featureText;

  /**
   * Historie
   */
  private String historie;

  /**
   * Label ID
   */
  @Column(name = "LAB_LAB_ID")
  private BigDecimal labLabId;

  /**
   * Rule ID
   */
  @Column(name = "LAB_OBJ_ID")
  @Id
  private BigDecimal labObjId;

  /**
   * Node ID
   */
  @Column(name = "OBJ_ID_1")
  private BigDecimal objId1;

  /**
   * Revision
   */
  @Column(name = "REV_ID")
  @Id
  private BigDecimal revId;

  /**
   * Scope
   */
  @Column(name = "\"SCOPE\"")
  private BigDecimal scope;

  /**
   * SSD Relevance
   */
  @Column(name = "SSD_RELEVANCE")
  private String ssdRelevance;

  /**
   * State
   */
  @Column(name = "\"STATE\"")
  private BigDecimal state;

  /**
   * Value ID
   */
  @Column(name = "VALUE_ID")
  @Id
  private BigDecimal valueId;

  /**
   * Value Text
   */
  @Column(name = "VALUE_TEXT")
  private String valueText;

  /**
   * 
   */
  public VLdb2SsdCompAll() {
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
   * @return feature text
   */
  public String getFeatureText() {
    return this.featureText;
  }

  /**
   * @param featureText feature text
   */
  public void setFeatureText(final String featureText) {
    this.featureText = featureText;
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
   * @return lab id
   */
  public BigDecimal getLabLabId() {
    return this.labLabId;
  }

  /**
   * @param labLabId lab id
   */
  public void setLabLabId(final BigDecimal labLabId) {
    this.labLabId = labLabId;
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
   * @return obj id
   */
  public BigDecimal getObjId1() {
    return this.objId1;
  }

  /**
   * @param objId1 obj id
   */
  public void setObjId1(final BigDecimal objId1) {
    this.objId1 = objId1;
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
   * @return scpope
   */
  public BigDecimal getScope() {
    return this.scope;
  }

  /**
   * @param scope scope
   */
  public void setScope(final BigDecimal scope) {
    this.scope = scope;
  }

  /**
   * @return relevance
   */
  public String getSsdRelevance() {
    return this.ssdRelevance;
  }

  /**
   * @param ssdRelevance relevance
   */
  public void setSsdRelevance(final String ssdRelevance) {
    this.ssdRelevance = ssdRelevance;
  }

  /**
   * @return state
   */
  public BigDecimal getState() {
    return this.state;
  }

  /**
   * @param state state
   */
  public void setState(final BigDecimal state) {
    this.state = state;
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
   * @return value text
   */
  public String getValueText() {
    return this.valueText;
  }

  /**
   * @param valueText text
   */
  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }

}