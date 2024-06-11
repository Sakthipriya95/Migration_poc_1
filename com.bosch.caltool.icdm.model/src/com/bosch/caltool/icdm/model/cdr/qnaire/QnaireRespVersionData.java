/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

/**
 * @author OZY1KOR
 */
public class QnaireRespVersionData {

  /**
   * revision number
   */
  private Long revisionNum;

  /**
   * version Name
   */
  private String versionName;

  /**
   * qnaire response name
   */
  private String qnaireRespName;

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
    return this.versionName;
  }

  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
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
}