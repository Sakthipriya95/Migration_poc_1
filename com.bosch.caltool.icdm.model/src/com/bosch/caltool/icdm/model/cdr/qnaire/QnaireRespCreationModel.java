/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;


/**
 * @author say8cob
 */
public class QnaireRespCreationModel {

  /**
   * Selected pidc version
   */
  private Long pidcVersionId;
  /**
   * selected pidc variant id
   */
  private Long pidcVariantId;
  /**
   * selected wp id
   */
  private Long selWpId;
  /**
   * selected resp id
   */
  private Long selRespId;
  /**
   * active qnaire vers id
   */
  private Long qnaireVersId;

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
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }

  /**
   * @param pidcVariantId the pidcVariantId to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }

  /**
   * @return the selWpId
   */
  public Long getSelWpId() {
    return this.selWpId;
  }

  /**
   * @param selWpId the selWpId to set
   */
  public void setSelWpId(final Long selWpId) {
    this.selWpId = selWpId;
  }

  /**
   * @return the selRespId
   */
  public Long getSelRespId() {
    return this.selRespId;
  }

  /**
   * @param selRespId the selRespId to set
   */
  public void setSelRespId(final Long selRespId) {
    this.selRespId = selRespId;
  }

  /**
   * @return the qnaireVersId
   */
  public Long getQnaireVersId() {
    return this.qnaireVersId;
  }

  /**
   * @param qnaireVersId the qnaireVersId to set
   */
  public void setQnaireVersId(final Long qnaireVersId) {
    this.qnaireVersId = qnaireVersId;
  }


}
