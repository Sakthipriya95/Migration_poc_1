package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


/**
 * The persistent class for the T_SSD_VALUES database table.
 */
@Entity
@Table(name = "T_SSD_VALUES")
@NamedQueries(value = {
    @NamedQuery(name = TSsdValue.NQ_GET_ALL, query = "SELECT t FROM TSsdValue t"),
    @NamedQuery(name = TSsdValue.NQ_GET_DONT_CARE_VALUES, query = "SELECT t FROM TSsdValue t where t.valueText=:dontCareValue") })

public class TSsdValue implements Serializable {

  private static final long serialVersionUID = 1L;
  
  /**
   * Named query for getting all ssd values
   */
  public static final String NQ_GET_ALL = "TSsdValue.getAll";

  /**
   * Named query for getting dont care values
   */
  public static final String NQ_GET_DONT_CARE_VALUES = "TSsdValue.getDontCareValues";

  @Id
  @Column(name = "VALUE_ID")
  private long valueId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "ICDM_ONLY")
  private String icdmOnly;

  @Temporal(TemporalType.DATE)
  @Column(name = "LAST_USED_SSD")
  private Date lastUsedSsd;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "USED_FLAG")
  private String usedFlag;

  @Column(name = "VALUE_TEXT")
  private String valueText;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "APIC_VALUE_ID")
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TSsdFeature
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FEATURE_ID")
  private TSsdFeature TSsdFeature;

  public TSsdValue() {}

  public long getValueId() {
    return this.valueId;
  }

  public void setValueId(final long valueId) {
    this.valueId = valueId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getIcdmOnly() {
    return this.icdmOnly;
  }

  public void setIcdmOnly(final String icdmOnly) {
    this.icdmOnly = icdmOnly;
  }

  public Date getLastUsedSsd() {
    return this.lastUsedSsd;
  }

  public void setLastUsedSsd(final Date lastUsedSsd) {
    this.lastUsedSsd = lastUsedSsd;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getUsedFlag() {
    return this.usedFlag;
  }

  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }

  public String getValueText() {
    return this.valueText;
  }

  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TSsdFeature getTSsdFeature() {
    return this.TSsdFeature;
  }

  public void setTSsdFeature(final TSsdFeature TSsdFeature) {
    this.TSsdFeature = TSsdFeature;
  }

}