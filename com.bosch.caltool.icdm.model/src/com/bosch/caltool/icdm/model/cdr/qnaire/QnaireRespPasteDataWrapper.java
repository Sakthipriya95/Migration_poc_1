/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;

/**
 * @author say8cob
 */
public class QnaireRespPasteDataWrapper {

  private boolean isCopiedQuesGnrlQuesEqvlnt;

  private String isCopiedQuesGnrlQuesDispMsg;

  private boolean isCopiedQuesAlreadyAvailable;

  private String isCopiedQuesAlreadyAvailableMsg;

  private boolean isCopiedQuestDivIdDiff;

  private String isCopiedQuestDivIdDiffMsg;

  private RvwQnaireResponse destGeneralQuesResp;

  private RvwQnaireResponse qnaireRespWithSameQnaireInDest;

  private QnaireRespCopyData qnaireRespCopyData;

  private A2lWorkPackage destA2lWorkpackage;

  private boolean isDestRespDeleted;


  /**
   * @return the isCopiedQuesGnrlQuesEqvlnt
   */
  public boolean isCopiedQuesGnrlQuesEqvlnt() {
    return this.isCopiedQuesGnrlQuesEqvlnt;
  }


  /**
   * @param isCopiedQuesGnrlQuesEqvlnt the isCopiedQuesGnrlQuesEqvlnt to set
   */
  public void setCopiedQuesGnrlQuesEqvlnt(final boolean isCopiedQuesGnrlQuesEqvlnt) {
    this.isCopiedQuesGnrlQuesEqvlnt = isCopiedQuesGnrlQuesEqvlnt;
  }


  /**
   * @return the isCopiedQuesGnrlQuesDispMsg
   */
  public String getIsCopiedQuesGnrlQuesDispMsg() {
    return this.isCopiedQuesGnrlQuesDispMsg;
  }


  /**
   * @param isCopiedQuesGnrlQuesDispMsg the isCopiedQuesGnrlQuesDispMsg to set
   */
  public void setIsCopiedQuesGnrlQuesDispMsg(final String isCopiedQuesGnrlQuesDispMsg) {
    this.isCopiedQuesGnrlQuesDispMsg = isCopiedQuesGnrlQuesDispMsg;
  }


  /**
   * @return the isCopiedQuesAlreadyAvailable
   */
  public boolean isCopiedQuesAlreadyAvailable() {
    return this.isCopiedQuesAlreadyAvailable;
  }


  /**
   * @param isCopiedQuesAlreadyAvailable the isCopiedQuesAlreadyAvailable to set
   */
  public void setCopiedQuesAlreadyAvailable(final boolean isCopiedQuesAlreadyAvailable) {
    this.isCopiedQuesAlreadyAvailable = isCopiedQuesAlreadyAvailable;
  }


  /**
   * @return the isCopiedQuesAlreadyAvailableMsg
   */
  public String getIsCopiedQuesAlreadyAvailableMsg() {
    return this.isCopiedQuesAlreadyAvailableMsg;
  }


  /**
   * @param isCopiedQuesAlreadyAvailableMsg the isCopiedQuesAlreadyAvailableMsg to set
   */
  public void setIsCopiedQuesAlreadyAvailableMsg(final String isCopiedQuesAlreadyAvailableMsg) {
    this.isCopiedQuesAlreadyAvailableMsg = isCopiedQuesAlreadyAvailableMsg;
  }


  /**
   * @return the isCopiedQuestDivIdDiff
   */
  public boolean isCopiedQuestDivIdDiff() {
    return this.isCopiedQuestDivIdDiff;
  }


  /**
   * @param isCopiedQuestDivIdDiff the isCopiedQuestDivIdDiff to set
   */
  public void setCopiedQuestDivIdDiff(final boolean isCopiedQuestDivIdDiff) {
    this.isCopiedQuestDivIdDiff = isCopiedQuestDivIdDiff;
  }


  /**
   * @return the isCopiedQuestDivIdDiffMsg
   */
  public String getIsCopiedQuestDivIdDiffMsg() {
    return this.isCopiedQuestDivIdDiffMsg;
  }


  /**
   * @param isCopiedQuestDivIdDiffMsg the isCopiedQuestDivIdDiffMsg to set
   */
  public void setIsCopiedQuestDivIdDiffMsg(final String isCopiedQuestDivIdDiffMsg) {
    this.isCopiedQuestDivIdDiffMsg = isCopiedQuestDivIdDiffMsg;
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
   * @return the qnaireRespCopyData
   */
  public QnaireRespCopyData getQnaireRespCopyData() {
    return this.qnaireRespCopyData;
  }


  /**
   * @param qnaireRespCopyData the qnaireRespCopyData to set
   */
  public void setQnaireRespCopyData(final QnaireRespCopyData qnaireRespCopyData) {
    this.qnaireRespCopyData = qnaireRespCopyData;
  }

  /**
   * @return the destA2lWorkpackage
   */
  public A2lWorkPackage getDestA2lWorkpackage() {
    return this.destA2lWorkpackage;
  }


  /**
   * @param destA2lWorkpackage the destA2lWorkpackage to set
   */
  public void setDestA2lWorkpackage(final A2lWorkPackage destA2lWorkpackage) {
    this.destA2lWorkpackage = destA2lWorkpackage;
  }


  /**
   * @return the isDestRespDeleted
   */
  public boolean isDestRespDeleted() {
    return this.isDestRespDeleted;
  }


  /**
   * @param isDestRespDeleted the isDestRespDeleted to set
   */
  public void setDestRespDeleted(final boolean isDestRespDeleted) {
    this.isDestRespDeleted = isDestRespDeleted;
  }


}
