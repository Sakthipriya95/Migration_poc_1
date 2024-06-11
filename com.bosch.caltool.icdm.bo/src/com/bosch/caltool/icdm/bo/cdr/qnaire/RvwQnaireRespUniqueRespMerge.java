/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class RvwQnaireRespUniqueRespMerge {


  private Long qnaireRespId;

  private Long qnaireVersId;

  private Long a2lWpId;

  private Long a2lRespId;


  public RvwQnaireRespUniqueRespMerge() {

  }

  /**
   * @param qnaireRespId rvw qnaire resp id
   * @param qnaireVersId qnaire resp id
   * @param a2lWpId wp id
   * @param a2lRespId resp id
   */
  public RvwQnaireRespUniqueRespMerge(final Long qnaireRespId, final Long qnaireVersId, final Long a2lWpId,
      final Long a2lRespId) {
    this.qnaireRespId = qnaireRespId;
    this.qnaireVersId = qnaireVersId;
    this.a2lWpId = a2lWpId;
    this.a2lRespId = a2lRespId;
  }


  /**
   * @return the qnaireRespId
   */
  public Long getQnaireRespId() {
    return this.qnaireRespId;
  }


  /**
   * @param qnaireRespId the qnaireRespId to set
   */
  public void setQnaireRespId(final Long qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
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


  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }


  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }


  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    RvwQnaireRespUniqueRespMerge other = (RvwQnaireRespUniqueRespMerge) obj;

    return ModelUtil.isEqual(getQnaireVersId(), other.getQnaireVersId()) &&
        ModelUtil.isEqual(getA2lWpId(), other.getA2lWpId()) && ModelUtil.isEqual(getA2lRespId(), other.getA2lRespId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getQnaireVersId(), getA2lWpId(), getA2lRespId());
  }
}
