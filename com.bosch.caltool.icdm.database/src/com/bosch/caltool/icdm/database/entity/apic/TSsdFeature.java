package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_SSD_FEATURES database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TSsdFeature.NQ_GET_ALL, query = "SELECT t FROM TSsdFeature t"),
    @NamedQuery(name = TSsdFeature.NQ_GET_FEATURE_BY_ATTR_ID, query = "SELECT feature FROM TSsdFeature feature where feature.tabvAttribute.attrId= :attrId") })
@Entity
@Table(name = "T_SSD_FEATURES")
@OptimisticLocking(cascade = true)
public class TSsdFeature implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String NQ_GET_ALL = "TSsdFeature.findAll";

  public static final String NQ_GET_FEATURE_BY_ATTR_ID = "TSsdFeature.getFeatureByAttrId";

  @Id
  @Column(name = "FEATURE_ID")
  private long featureId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "FEATURE_TEXT")
  private String featureText;

  @Column(name = "ICDM_ONLY")
  private String icdmOnly;

  private String icdmsync;

  @Temporal(TemporalType.DATE)
  @Column(name = "LAST_USED_SSD")
  private Date lastUsedSsd;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "APIC_ATTR_ID")
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TSsdValue
  @OneToMany(mappedBy = "TSsdFeature")
  private List<TSsdValue> TSsdValues;

  public TSsdFeature() {}

  public long getFeatureId() {
    return this.featureId;
  }

  public void setFeatureId(final long featureId) {
    this.featureId = featureId;
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

  public String getFeatureText() {
    return this.featureText;
  }

  public void setFeatureText(final String featureText) {
    this.featureText = featureText;
  }

  public String getIcdmOnly() {
    return this.icdmOnly;
  }

  public void setIcdmOnly(final String icdmOnly) {
    this.icdmOnly = icdmOnly;
  }

  public String getIcdmsync() {
    return this.icdmsync;
  }

  public void setIcdmsync(final String icdmsync) {
    this.icdmsync = icdmsync;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public List<TSsdValue> getTSsdValues() {
    return this.TSsdValues;
  }

  public void setTSsdValues(final List<TSsdValue> TSsdValues) {
    this.TSsdValues = TSsdValues;
  }

  public TSsdValue addTSsdValue(final TSsdValue TSsdValue) {
    getTSsdValues().add(TSsdValue);
    TSsdValue.setTSsdFeature(this);

    return TSsdValue;
  }

  public TSsdValue removeTSsdValue(final TSsdValue TSsdValue) {
    getTSsdValues().remove(TSsdValue);
    TSsdValue.setTSsdFeature(null);

    return TSsdValue;
  }

}