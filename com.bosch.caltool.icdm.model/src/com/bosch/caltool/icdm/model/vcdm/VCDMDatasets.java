package com.bosch.caltool.icdm.model.vcdm;


import java.math.BigDecimal;
import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Dataset Model class
 *
 * @author apj4cob
 */
public class VCDMDatasets implements Comparable<VCDMDatasets>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 38937735103077L;

  private long easeedstId;
  private Date creDate;
  private String creUser;
  private String customerName;
  private String deptName;
  private Date easeedstCreDate;
  private String easeedstCreUser;
  private Date easeedstModDate;
  private String easeedstModUser;
  private String easeedstState;
  private Long elementId;
  private String elementName;
  private Date modDate;
  private String modUser;
  private String prodKey;
  private String progKey;
  private BigDecimal revision;
  private String variant;
  private String versionName;
  private Long calibratedLabels;
  private Long changedLabels;
  private Long checkedLabels;
  private Long completedLabels;
  private Long prelimcalibratedLabels;
  private Long noStateLabels;
  private Long totalLabels;
  private Long partitionLabel;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // id to be set

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // version to be set

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VCDMDatasets o) {

    return 0;
  }

  /**
   * @return the easeedstId
   */
  public long getEaseedstId() {
    return this.easeedstId;
  }

  /**
   * @param easeedstId the easeedstId to set
   */
  public void setEaseedstId(final long easeedstId) {
    this.easeedstId = easeedstId;
  }

  /**
   * @return the creDate
   */
  public Date getCreDate() {
    return this.creDate;
  }

  /**
   * @param creDate the creDate to set
   */
  public void setCreDate(final Date creDate) {
    this.creDate = creDate;
  }

  /**
   * @return the creUser
   */
  public String getCreUser() {
    return this.creUser;
  }

  /**
   * @param creUser the creUser to set
   */
  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }

  /**
   * @return the customerName
   */
  public String getCustomerName() {
    return this.customerName;
  }

  /**
   * @param customerName the customerName to set
   */
  public void setCustomerName(final String customerName) {
    this.customerName = customerName;
  }

  /**
   * @return the deptName
   */
  public String getDeptName() {
    return this.deptName;
  }

  /**
   * @param deptName the deptName to set
   */
  public void setDeptName(final String deptName) {
    this.deptName = deptName;
  }

  /**
   * @return the easeedstCreDate
   */
  public Date getEaseedstCreDate() {
    return this.easeedstCreDate;
  }

  /**
   * @param easeedstCreDate the easeedstCreDate to set
   */
  public void setEaseedstCreDate(final Date easeedstCreDate) {
    this.easeedstCreDate = easeedstCreDate;
  }

  /**
   * @return the easeedstCreUser
   */
  public String getEaseedstCreUser() {
    return this.easeedstCreUser;
  }

  /**
   * @param easeedstCreUser the easeedstCreUser to set
   */
  public void setEaseedstCreUser(final String easeedstCreUser) {
    this.easeedstCreUser = easeedstCreUser;
  }

  /**
   * @return the easeedstModDate
   */
  public Date getEaseedstModDate() {
    return this.easeedstModDate;
  }

  /**
   * @param easeedstModDate the easeedstModDate to set
   */
  public void setEaseedstModDate(final Date easeedstModDate) {
    this.easeedstModDate = easeedstModDate;
  }

  /**
   * @return the easeedstModUser
   */
  public String getEaseedstModUser() {
    return this.easeedstModUser;
  }

  /**
   * @param easeedstModUser the easeedstModUser to set
   */
  public void setEaseedstModUser(final String easeedstModUser) {
    this.easeedstModUser = easeedstModUser;
  }

  /**
   * @return the easeedstState
   */
  public String getEaseedstState() {
    return this.easeedstState;
  }

  /**
   * @param easeedstState the easeedstState to set
   */
  public void setEaseedstState(final String easeedstState) {
    this.easeedstState = easeedstState;
  }

  /**
   * @return the elementId
   */
  public Long getElementId() {
    return this.elementId;
  }

  /**
   * @param elementId the elementId to set
   */
  public void setElementId(final Long elementId) {
    this.elementId = elementId;
  }

  /**
   * @return the elementName
   */
  public String getElementName() {
    return this.elementName;
  }

  /**
   * @param elementName the elementName to set
   */
  public void setElementName(final String elementName) {
    this.elementName = elementName;
  }

  /**
   * @return the modDate
   */
  public Date getModDate() {
    return this.modDate;
  }

  /**
   * @param modDate the modDate to set
   */
  public void setModDate(final Date modDate) {
    this.modDate = modDate;
  }

  /**
   * @return the modUser
   */
  public String getModUser() {
    return this.modUser;
  }

  /**
   * @param modUser the modUser to set
   */
  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }

  /**
   * @return the prodKey
   */
  public String getProdKey() {
    return this.prodKey;
  }

  /**
   * @param prodKey the prodKey to set
   */
  public void setProdKey(final String prodKey) {
    this.prodKey = prodKey;
  }

  /**
   * @return the progKey
   */
  public String getProgKey() {
    return this.progKey;
  }

  /**
   * @param progKey the progKey to set
   */
  public void setProgKey(final String progKey) {
    this.progKey = progKey;
  }

  /**
   * @return the revision
   */
  public BigDecimal getRevision() {
    return this.revision;
  }

  /**
   * @param revision the revision to set
   */
  public void setRevision(final BigDecimal revision) {
    this.revision = revision;
  }

  /**
   * @return the variant
   */
  public String getVariant() {
    return this.variant;
  }

  /**
   * @param variant the variant to set
   */
  public void setVariant(final String variant) {
    this.variant = variant;
  }

  /**
   * @return the versionName
   */
  public String getVersionName() {
    return this.versionName;
  }

  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }

  /**
   * @return the calibratedLabels
   */
  public Long getCalibratedLabels() {
    return this.calibratedLabels;
  }

  /**
   * @param calibratedLabels the calibratedLabels to set
   */
  public void setCalibratedLabels(final Long calibratedLabels) {
    this.calibratedLabels = calibratedLabels;
  }

  /**
   * @return the changedLabels
   */
  public Long getChangedLabels() {
    return this.changedLabels;
  }

  /**
   * @param changedLabels the changedLabels to set
   */
  public void setChangedLabels(final Long changedLabels) {
    this.changedLabels = changedLabels;
  }

  /**
   * @return the checkedLabels
   */
  public Long getCheckedLabels() {
    return this.checkedLabels;
  }

  /**
   * @param checkedLabels the checkedLabels to set
   */
  public void setCheckedLabels(final Long checkedLabels) {
    this.checkedLabels = checkedLabels;
  }

  /**
   * @return the completedLabels
   */
  public Long getCompletedLabels() {
    return this.completedLabels;
  }

  /**
   * @param completedLabels the completedLabels to set
   */
  public void setCompletedLabels(final Long completedLabels) {
    this.completedLabels = completedLabels;
  }

  /**
   * @return the prelimcalibratedLabels
   */
  public Long getPrelimcalibratedLabels() {
    return this.prelimcalibratedLabels;
  }

  /**
   * @param prelimcalibratedLabels the prelimcalibratedLabels to set
   */
  public void setPrelimcalibratedLabels(final Long prelimcalibratedLabels) {
    this.prelimcalibratedLabels = prelimcalibratedLabels;
  }

  /**
   * @return the noStateLabels
   */
  public Long getNoStateLabels() {
    return this.noStateLabels;
  }

  /**
   * @param noStateLabels the noStateLabels to set
   */
  public void setNoStateLabels(final Long noStateLabels) {
    this.noStateLabels = noStateLabels;
  }

  /**
   * @return the totalLabels
   */
  public Long getTotalLabels() {
    return this.totalLabels;
  }

  /**
   * @param totalLabels the totalLabels to set
   */
  public void setTotalLabels(final Long totalLabels) {
    this.totalLabels = totalLabels;
  }

  /**
   * @return the partitionLabel
   */
  public Long getPartitionLabel() {
    return this.partitionLabel;
  }

  /**
   * @param partitionLabel the partitionLabel to set
   */
  public void setPartitionLabel(final Long partitionLabel) {
    this.partitionLabel = partitionLabel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return ModelUtil.isEqual(getId(), ((VCDMDatasets) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


}
