/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;

/**
 * @author dja7cob
 */
public class RvwResEditorInputData {

  private RvwVariant rvwVariant;

  private CDRReviewResult rvwResult;

  // TODO add workpackage and responsibility ids or objects

  private Long a2lWorkpackageId;

  private Long a2lRespId;

  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
  }


  /**
   * @return the rvwResult
   */
  public CDRReviewResult getRvwResult() {
    return this.rvwResult;
  }


  /**
   * @param rvwResult the rvwResult to set
   */
  public void setRvwResult(final CDRReviewResult rvwResult) {
    this.rvwResult = rvwResult;
  }


  /**
   * @return the a2lWorkpackageId
   */
  public Long getA2lWorkpackageId() {
    return this.a2lWorkpackageId;
  }


  /**
   * @param a2lWorkpackageId the a2lWorkpackageId to set
   */
  public void setA2lWorkpackageId(final Long a2lWorkpackageId) {
    this.a2lWorkpackageId = a2lWorkpackageId;
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

}
