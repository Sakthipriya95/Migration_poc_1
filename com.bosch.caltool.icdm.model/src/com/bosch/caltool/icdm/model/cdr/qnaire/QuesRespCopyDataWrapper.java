/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

/**
 * Data needed for validation from server to client before sending copy/paste qnaire resp service call
 *
 * @author UKT1COB
 */
public class QuesRespCopyDataWrapper {

  private RvwQnaireResponse qnaireRespWithSameQnaireInDest;

  private String isCopiedQuesGnrlQuesEqvlnt;

  private Long srcPidcDivId;

  private Long destPidcDivId;

  private RvwQnaireResponse destGeneralQuesResp;

  private boolean isSameCopiedQnaireLinkedInDest;

  /**
   * @return the qnaireRespWithSameQnaireInDest
   */
  public RvwQnaireResponse getQnaireRespWithSameQnaireInDest() {
    return this.qnaireRespWithSameQnaireInDest;
  }


  /**
   * @param qnaireRespWithSameQnaireInDest the qnaireRespWithSameQnaireInDest to set
   */
  public void setQnaireRespWithSameQnaireInDest(final RvwQnaireResponse qnaireRespWithSameQnaireInDest) {
    this.qnaireRespWithSameQnaireInDest = qnaireRespWithSameQnaireInDest;
  }


  /**
   * @return the isCopiedQuesGnrlQuesEqvlnt
   */
  public String getIsCopiedQuesGnrlQuesEqvlnt() {
    return this.isCopiedQuesGnrlQuesEqvlnt;
  }


  /**
   * @param isCopiedQuesGnrlQuesEqvlnt the isCopiedQuesGnrlQuesEqvlnt to set
   */
  public void setIsCopiedQuesGnrlQuesEqvlnt(final String isCopiedQuesGnrlQuesEqvlnt) {
    this.isCopiedQuesGnrlQuesEqvlnt = isCopiedQuesGnrlQuesEqvlnt;
  }


  /**
   * @return the srcPidcDivId
   */
  public Long getSrcPidcDivId() {
    return this.srcPidcDivId;
  }


  /**
   * @param srcPidcDivId the srcPidcDivId to set
   */
  public void setSrcPidcDivId(final Long srcPidcDivId) {
    this.srcPidcDivId = srcPidcDivId;
  }


  /**
   * @return the destPidcDivId
   */
  public Long getDestPidcDivId() {
    return this.destPidcDivId;
  }


  /**
   * @param destPidcDivId the destPidcDivId to set
   */
  public void setDestPidcDivId(final Long destPidcDivId) {
    this.destPidcDivId = destPidcDivId;
  }


  /**
   * @return the destGeneralQuesResp
   */
  public RvwQnaireResponse getDestGeneralQuesResp() {
    return this.destGeneralQuesResp;
  }


  /**
   * @param destGeneralQuesResp the destGeneralQuesResp to set
   */
  public void setDestGeneralQuesResp(final RvwQnaireResponse destGeneralQuesResp) {
    this.destGeneralQuesResp = destGeneralQuesResp;
  }


  /**
   * @return the isSameCopiedQnaireLinkedInDest
   */
  public boolean isSameCopiedQnaireLinkedInDest() {
    return this.isSameCopiedQnaireLinkedInDest;
  }


  /**
   * @param isSameCopiedQnaireLinkedInDest the isSameCopiedQnaireLinkedInDest to set
   */
  public void setSameCopiedQnaireLinkedInDest(final boolean isSameCopiedQnaireLinkedInDest) {
    this.isSameCopiedQnaireLinkedInDest = isSameCopiedQnaireLinkedInDest;
  }

}
