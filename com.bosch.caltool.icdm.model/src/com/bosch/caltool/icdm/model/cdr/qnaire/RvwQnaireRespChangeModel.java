/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.List;

import com.bosch.caltool.icdm.model.general.Link;

/**
 * @author UKT1COB
 */
public class RvwQnaireRespChangeModel {

  private RvwQnaireResponse pastedRvwQnaireResp;
  private RvwQnaireResponse existingTargetRvwQnaireResp;
  private RvwQnaireResponse destGeneralQuesRespBeforeCopy;
  private RvwQnaireResponse destGeneralQuesRespAfterCopy;
  private RvwQnaireRespVersion baselinedRvwQnaireRespVersion;
  private RvwQnaireRespVersion deletedRvwQnaireRespWSVersion;
  private List<RvwQnaireRespVersion> copiedRvwQnaireRespVersionList;
  private List<RvwQnaireAnswer> copiedRvwQnaireAnswerList;
  private List<RvwQnaireAnswerOpl> copiedRvwQnaireAnswerOplList;
  private List<Link> copiedRvwQnaireAnswerLinkList;


  /**
   * @return the pastedRvwQnaireResp
   */
  public RvwQnaireResponse getPastedRvwQnaireResp() {
    return this.pastedRvwQnaireResp;
  }

  /**
   * @param pastedRvwQnaireResp the pastedRvwQnaireResp to set
   */
  public void setPastedRvwQnaireResp(final RvwQnaireResponse pastedRvwQnaireResp) {
    this.pastedRvwQnaireResp = pastedRvwQnaireResp;
  }

  /**
   * @return the existingTargetRvwQnaireResp
   */
  public RvwQnaireResponse getExistingTargetRvwQnaireResp() {
    return this.existingTargetRvwQnaireResp;
  }

  /**
   * @param existingTargetRvwQnaireResp the existingTargetRvwQnaireResp to set
   */
  public void setExistingTargetRvwQnaireResp(final RvwQnaireResponse existingTargetRvwQnaireResp) {
    this.existingTargetRvwQnaireResp = existingTargetRvwQnaireResp;
  }

  /**
   * @return the destGeneralQuesRespBeforeCopy
   */
  public RvwQnaireResponse getDestGeneralQuesRespBeforeCopy() {
    return this.destGeneralQuesRespBeforeCopy;
  }

  /**
   * @param destGeneralQuesRespBeforeCopy the destGeneralQuesRespBeforeCopy to set
   */
  public void setDestGeneralQuesRespBeforeCopy(final RvwQnaireResponse destGeneralQuesRespBeforeCopy) {
    this.destGeneralQuesRespBeforeCopy = destGeneralQuesRespBeforeCopy;
  }

  /**
   * @return the destGeneralQuesRespAfterCopy
   */
  public RvwQnaireResponse getDestGeneralQuesRespAfterCopy() {
    return this.destGeneralQuesRespAfterCopy;
  }

  /**
   * @param destGeneralQuesRespAfterCopy the destGeneralQuesRespAfterCopy to set
   */
  public void setDestGeneralQuesRespAfterCopy(final RvwQnaireResponse destGeneralQuesRespAfterCopy) {
    this.destGeneralQuesRespAfterCopy = destGeneralQuesRespAfterCopy;
  }

  /**
   * @return the baselinedRvwQnaireRespVersion
   */
  public RvwQnaireRespVersion getBaselinedRvwQnaireRespVersion() {
    return this.baselinedRvwQnaireRespVersion;
  }

  /**
   * @param baselinedRvwQnaireRespVersion the baselinedRvwQnaireRespVersion to set
   */
  public void setBaselinedRvwQnaireRespVersion(final RvwQnaireRespVersion baselinedRvwQnaireRespVersion) {
    this.baselinedRvwQnaireRespVersion = baselinedRvwQnaireRespVersion;
  }

  /**
   * @return the deletedRvwQnaireRespWSVersion
   */
  public RvwQnaireRespVersion getDeletedRvwQnaireRespWSVersion() {
    return this.deletedRvwQnaireRespWSVersion;
  }

  /**
   * @param deletedRvwQnaireRespWSVersion the deletedRvwQnaireRespWSVersion to set
   */
  public void setDeletedRvwQnaireRespWSVersion(final RvwQnaireRespVersion deletedRvwQnaireRespWSVersion) {
    this.deletedRvwQnaireRespWSVersion = deletedRvwQnaireRespWSVersion;
  }

  /**
   * @return the copiedRvwQnaireRespVersionList
   */
  public List<RvwQnaireRespVersion> getCopiedRvwQnaireRespVersionList() {
    return this.copiedRvwQnaireRespVersionList;
  }

  /**
   * @param copiedRvwQnaireRespVersionList the copiedRvwQnaireRespVersionList to set
   */
  public void setCopiedRvwQnaireRespVersionList(final List<RvwQnaireRespVersion> copiedRvwQnaireRespVersionList) {
    this.copiedRvwQnaireRespVersionList = copiedRvwQnaireRespVersionList;
  }

  /**
   * @return the copiedRvwQnaireAnswerList
   */
  public List<RvwQnaireAnswer> getCopiedRvwQnaireAnswerList() {
    return this.copiedRvwQnaireAnswerList;
  }


  /**
   * @param copiedRvwQnaireAnswerList the copiedRvwQnaireAnswerList to set
   */
  public void setCopiedRvwQnaireAnswerList(final List<RvwQnaireAnswer> copiedRvwQnaireAnswerList) {
    this.copiedRvwQnaireAnswerList = copiedRvwQnaireAnswerList;
  }


  /**
   * @return the copiedRvwQnaireAnswerOplList
   */
  public List<RvwQnaireAnswerOpl> getCopiedRvwQnaireAnswerOplList() {
    return this.copiedRvwQnaireAnswerOplList;
  }


  /**
   * @param copiedRvwQnaireAnswerOplList the copiedRvwQnaireAnswerOplList to set
   */
  public void setCopiedRvwQnaireAnswerOplList(final List<RvwQnaireAnswerOpl> copiedRvwQnaireAnswerOplList) {
    this.copiedRvwQnaireAnswerOplList = copiedRvwQnaireAnswerOplList;
  }

  /**
   * @return the copiedRvwQnaireAnswerLinkList
   */
  public List<Link> getCopiedRvwQnaireAnswerLinkList() {
    return this.copiedRvwQnaireAnswerLinkList;
  }


  /**
   * @param copiedRvwQnaireAnswerLinkList the copiedRvwQnaireAnswerLinkList to set
   */
  public void setCopiedRvwQnaireAnswerLinkList(final List<Link> copiedRvwQnaireAnswerLinkList) {
    this.copiedRvwQnaireAnswerLinkList = copiedRvwQnaireAnswerLinkList;
  }

}
