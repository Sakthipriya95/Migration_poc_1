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
public class CompliReviewResult implements IModel, Comparable<CompliReviewResult> {

  /**
   *
   */
  private static final long serialVersionUID = -5290084283154744893L;
  /** The id. */
  private Long resultId;
  /** The a2l file id. */
  private Long a2lFileId;
  /** The a2l file name. */
  private String a2lFileName;
  /** The numCompli. */
  private Long numCompli;

  /**
   * Number of qssd parameters
   */
  private Long numQssd;

  /** The sdompvername. */
  private String sdomPverName;
  /** The sdompvervar. */
  private String sdomPverVariant;
  /** pidc revision id */
  private Long sdomPverRevision;
  /** created date **/
  private Date createdDate;
  /** The version. */
  private Long version;
  /** The time used for the complete check incl. PDF creation (in milli secs) */
  private Long timeUsed;
  /** Completed or failed */
  private String status;

  private Long icdmFileId;
  /**
   * webFlow JobId * eg WF091236 It will be start with "WF"
   */
  private String webFlowJobId;
  /** pidc version id */
  private Long pidcVersionId;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompliReviewResult file) {
    return ModelUtil.compare(getResultId(), file.getResultId());
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
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }


  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }


  /**
   * @param a2lFileName the a2lFileName to set
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }


  /**
   * @return the numCompli
   */
  public Long getNumCompli() {
    return this.numCompli;
  }


  /**
   * @param numCompli the numCompli to set
   */
  public void setNumCompli(final Long numCompli) {
    this.numCompli = numCompli;
  }


  /**
   * @return the sdomPverName
   */
  public String getSdomPverName() {
    return this.sdomPverName;
  }


  /**
   * @param sdomPverName the sdomPverName to set
   */
  public void setSdomPverName(final String sdomPverName) {
    this.sdomPverName = sdomPverName;
  }


  /**
   * @return the sdomPverRevision
   */
  public Long getSdomPverRevision() {
    return this.sdomPverRevision;
  }


  /**
   * @param sdomPverRevision the sdomPverRevision to set
   */
  public void setSdomPverRevision(final Long sdomPverRevision) {
    this.sdomPverRevision = sdomPverRevision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.resultId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.resultId = objId;
  }


  /**
   * @return the timeUsed
   */
  public Long getTimeUsed() {
    return this.timeUsed;
  }


  /**
   * @param timeUsed the timeUsed to set
   */
  public void setTimeUsed(final Long timeUsed) {
    this.timeUsed = timeUsed;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return this.status;
  }


  /**
   * @param status the status to set
   */
  public void setStatus(final String status) {
    this.status = status;
  }


  /**
   * @return the webFlowJobId
   */
  public String getWebFlowJobId() {
    return this.webFlowJobId;
  }


  /**
   * @param webFlowJobId the webFlowJobId to set
   */
  public void setWebFlowJobId(final String webFlowJobId) {
    this.webFlowJobId = webFlowJobId;
  }


  /**
   * @return the sdomPverVariant
   */
  public String getSdomPverVariant() {
    return this.sdomPverVariant;
  }


  /**
   * @param sdomPverVariant the sdomPverVariant to set
   */
  public void setSdomPverVariant(final String sdomPverVariant) {
    this.sdomPverVariant = sdomPverVariant;
  }


  /**
   * @return the icdmFileId
   */
  public Long getIcdmFileId() {
    return this.icdmFileId;
  }


  /**
   * @param icdmFileId the icdmFileId to set
   */
  public void setIcdmFileId(final Long icdmFileId) {
    this.icdmFileId = icdmFileId;
  }


  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the numQssd
   */
  public Long getNumQssd() {
    return this.numQssd;
  }


  /**
   * @param numQssd the numQssd to set
   */
  public void setNumQssd(final Long numQssd) {
    this.numQssd = numQssd;
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
    return ModelUtil.isEqual(getResultId(), ((CompliReviewResult) obj).getResultId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getResultId());
  }
}
