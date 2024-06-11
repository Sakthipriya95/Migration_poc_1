/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author mkl2cob
 */
public class QnaireRespStatusData {

  /**
   * workpackage name
   */
  private String wpName;

  /**
   * responsibility name
   */
  private String respName;

  /**
   * qnaire response name
   */
  private String qnaireRespName;

  /**
   * questionnaire response id
   */
  private Long quesRespId;

  /**
   * status of qnaire response (from working set)
   */
  private String status;

  /**
   * primary variant name
   */
  private String primaryVarName;
  /**
   * revision number
   */
  private Long revisionNum;
  /**
   * version Name
   */
  private String versionName;

  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * @return the respName
   */
  public String getRespName() {
    return this.respName;
  }


  /**
   * @param respName the respName to set
   */
  public void setRespName(final String respName) {
    this.respName = respName;
  }


  /**
   * @return the qnaireRespName
   */
  public String getQnaireRespName() {
    return this.qnaireRespName;
  }


  /**
   * @param qnaireRespName the qnaireRespName to set
   */
  public void setQnaireRespName(final String qnaireRespName) {
    this.qnaireRespName = qnaireRespName;
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
   * @return the quesRespId
   */
  public Long getQuesRespId() {
    return this.quesRespId;
  }


  /**
   * @param quesRespId the quesRespId to set
   */
  public void setQuesRespId(final Long quesRespId) {
    this.quesRespId = quesRespId;
  }


  /**
   * @return the primaryVarName
   */
  public String getPrimaryVarName() {
    return this.primaryVarName;
  }


  /**
   * @param primaryVarName the primaryVarName to set
   */
  public void setPrimaryVarName(final String primaryVarName) {
    this.primaryVarName = primaryVarName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    final QnaireRespStatusData qnaireRespData = (QnaireRespStatusData) obj;
    return getQuesRespId().equals(qnaireRespData.getQuesRespId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getQuesRespId());
  }


  /**
   * @return the revisionNum
   */
  public Long getRevisionNum() {
    return this.revisionNum;
  }


  /**
   * @param revisionNum the revisionNum to set
   */
  public void setRevisionNum(final Long revisionNum) {
    this.revisionNum = revisionNum;
  }


  /**
   * @return the versionName
   */
  public String getVersionName() {
    return versionName;
  }


  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }
}
