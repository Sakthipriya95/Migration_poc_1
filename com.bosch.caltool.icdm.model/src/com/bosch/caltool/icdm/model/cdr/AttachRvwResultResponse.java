/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author dja7cob
 */
public class AttachRvwResultResponse {

  private RvwVariant rvwVariant;

  private Set<RvwQnaireResponse> qaireRespSkipped = new HashSet<>();

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
   * @return the qaireRespSkipped
   */
  public Set<RvwQnaireResponse> getQaireRespSkipped() {
    return this.qaireRespSkipped;
  }


  /**
   * @param qaireRespSkipped the qaireRespSkipped to set
   */
  public void setQaireRespSkipped(final Set<RvwQnaireResponse> qaireRespSkipped) {
    this.qaireRespSkipped = qaireRespSkipped;
  }
}
