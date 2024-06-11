/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.compli;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmo5cob
 */
public class CompliReviewResultHex implements IModel, Comparable<CompliReviewResultHex> {

  /**
   *
   */
  private static final long serialVersionUID = -1382156730360670995L;
  /** The id. */
  private Long compliRvwHexId;
  /** The result id. */
  private Long resultId;
  /** The number of compli failures due to no rule. */
  private Long compliNoRule;
  /** The hex file name. */
  private String hexFileName;
  /** The number of cssd failures */
  private Long cssdFail;
  /** The number success */
  private Long resultOk;
  /** seq number */
  private Long indexNum;
  /** number of failures */
  private Long ssd2rvFail;

  /**
   * Number of QSSD failures
   */
  private Long qssdFail;

  /**
   * Number of QSSD OK
   */
  private Long qssdOk;

  /**
   * Number of QSSD No Rule
   */
  private Long qssdNoRule;

  /** created date **/
  private Date createdDate;
  /** The version. */
  private Long version;
  /** VARIANT_ID if no variant is present, this needs to be the PIDC_VERS_ID */
  private Long variantId;
  /** SSD file name used for Hex Compli Review */
  private String ssdFileName;
  /** Generated check SSD Excel report Name */
  private String checkSsdReportName;
  /** SSD Release Id */
  private Long releaseId;
  /** Hex File CheckSum value calculated using CRC32 */
  private String hexChecksum;
  /** vCDM DST ID */
  private Long vcdmDstId;


  /**
   * @return the vcdmDstId
   */
  public Long getVcdmDstId() {
    return this.vcdmDstId;
  }


  /**
   * @param vcdmDstId the vcdmDstId to set
   */
  public void setVcdmDstId(final Long vcdmDstId) {
    this.vcdmDstId = vcdmDstId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompliReviewResultHex file) {
    return ModelUtil.compare(getId(), file.getId());
  }


  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;

  }


  /**
   * @return the resultId
   */
  public Long getResultId() {
    return this.resultId;
  }


  /**
   * @param resultId the resultId to set
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.compliRvwHexId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.compliRvwHexId = objId;
  }


  /**
   * @return the hexFileName
   */
  public String getHexFileName() {
    return this.hexFileName;
  }


  /**
   * @param hexFileName the hexFileName to set
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }


  /**
   * @return the cssdFail
   */
  public Long getCssdFail() {
    return this.cssdFail;
  }


  /**
   * @param cssdFail the cssdFail to set
   */
  public void setCssdFail(final Long cssdFail) {
    this.cssdFail = cssdFail;
  }


  /**
   * @return the compliNoRule
   */
  public Long getCompliNoRule() {
    return this.compliNoRule;
  }


  /**
   * @param compliNoRule the compliNoRule to set
   */
  public void setCompliNoRule(final Long compliNoRule) {
    this.compliNoRule = compliNoRule;
  }


  /**
   * @return the indexNum
   */
  public Long getIndexNum() {
    return this.indexNum;
  }


  /**
   * @param indexNum the indexNum to set
   */
  public void setIndexNum(final Long indexNum) {
    this.indexNum = indexNum;
  }


  /**
   * @return the ssd2rvFail
   */
  public Long getSsd2rvFail() {
    return this.ssd2rvFail;
  }

  /**
   * @param ssd2rvFail the ssd2rvFail to set
   */
  public void setSsd2rvFail(final Long ssd2rvFail) {
    this.ssd2rvFail = ssd2rvFail;
  }


  /**
   * @return the resultOk
   */
  public Long getResultOk() {
    return this.resultOk;
  }


  /**
   * @param resultOk the resultOk to set
   */
  public void setResultOk(final Long resultOk) {
    this.resultOk = resultOk;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the ssdFileName
   */
  public String getSsdFileName() {
    return this.ssdFileName;
  }


  /**
   * @param ssdFileName the ssdFileName to set
   */
  public void setSsdFileName(final String ssdFileName) {
    this.ssdFileName = ssdFileName;
  }


  /**
   * @return the checkSsdReportName
   */
  public String getCheckSsdReportName() {
    return this.checkSsdReportName;
  }


  /**
   * @param checkSsdReportName the checkSsdReportName to set
   */
  public void setCheckSsdReportName(final String checkSsdReportName) {
    this.checkSsdReportName = checkSsdReportName;
  }


  /**
   * @return the releaseId
   */
  public Long getReleaseId() {
    return this.releaseId;
  }


  /**
   * @param releaseId the releaseId to set
   */
  public void setReleaseId(final Long releaseId) {
    this.releaseId = releaseId;
  }


  /**
   * @return the hexChecksum
   */
  public String getHexChecksum() {
    return this.hexChecksum;
  }


  /**
   * @param hexChecksum the hexChecksum to set
   */
  public void setHexChecksum(final String hexChecksum) {
    this.hexChecksum = hexChecksum;
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
    return ModelUtil.isEqual(getId(), ((CompliReviewResultHex) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the qssdFail
   */
  public Long getQssdFail() {
    return this.qssdFail;
  }


  /**
   * @param qssdFail the qssdFail to set
   */
  public void setQssdFail(final Long qssdFail) {
    this.qssdFail = qssdFail;
  }


  /**
   * @return the qssdOk
   */
  public Long getQssdOk() {
    return this.qssdOk;
  }


  /**
   * @param qssdOk the qssdOk to set
   */
  public void setQssdOk(final Long qssdOk) {
    this.qssdOk = qssdOk;
  }


  /**
   * @return the qssdNoRule
   */
  public Long getQssdNoRule() {
    return this.qssdNoRule;
  }


  /**
   * @param qssdNoRule the qssdNoRule to set
   */
  public void setQssdNoRule(final Long qssdNoRule) {
    this.qssdNoRule = qssdNoRule;
  }


}
