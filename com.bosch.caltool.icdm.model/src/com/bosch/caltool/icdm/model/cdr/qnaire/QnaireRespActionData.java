/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author OZY1KOR
 */
public class QnaireRespActionData {

  /**
   * Selected pidc version
   */
  private PidcVersion targetPidcVersion;
  /**
   * selected pidc variant id
   */
  private PidcVariant targetPidcVariant;
  /**
   * selected wp id
   */
  private Long targetWpId;
  /**
   * selected resp id
   */
  private Long targetRespId;

  /**
   * Existing Qnaire resp obj
   */
  private RvwQnaireResponse existingTargetQnaireResp;

  /**
   * true if it is called from data assessment
   */
  private boolean baselineCreationFromDataAssmnt = false;

  /**
   * @return the targetPidcVersion
   */
  public PidcVersion getTargetPidcVersion() {
    return this.targetPidcVersion;
  }

  /**
   * @param targetPidcVersion the targetPidcVersion to set
   */
  public void setTargetPidcVersion(final PidcVersion targetPidcVersion) {
    this.targetPidcVersion = targetPidcVersion;
  }

  /**
   * @return the targetPidcVariant
   */
  public PidcVariant getTargetPidcVariant() {
    return this.targetPidcVariant;
  }

  /**
   * @param targetPidcVariant the targetPidcVariant to set
   */
  public void setTargetPidcVariant(final PidcVariant targetPidcVariant) {
    this.targetPidcVariant = targetPidcVariant;
  }

  /**
   * @return the targetWpId
   */
  public Long getTargetWpId() {
    return this.targetWpId;
  }

  /**
   * @param targetWpId the targetWpId to set
   */
  public void setTargetWpId(final Long targetWpId) {
    this.targetWpId = targetWpId;
  }

  /**
   * @return the targetRespId
   */
  public Long getTargetRespId() {
    return this.targetRespId;
  }

  /**
   * @param targetRespId the targetRespId to set
   */
  public void setTargetRespId(final Long targetRespId) {
    this.targetRespId = targetRespId;
  }

  /**
   * @return the existingQnaireResp
   */
  public RvwQnaireResponse getExistingTargetQnaireResp() {
    return this.existingTargetQnaireResp;
  }


  /**
   * @param existingQnaireResp the existingQnaireResp to set
   */
  public void setExistingTargetQnaireResp(final RvwQnaireResponse existingQnaireResp) {
    this.existingTargetQnaireResp = existingQnaireResp;
  }


  /**
   * @return the baselineCreationFromDataAssmnt
   */
  public boolean isBaselineCreationFromDataAssmnt() {
    return this.baselineCreationFromDataAssmnt;
  }


  /**
   * @param baselineCreationFromDataAssmnt the baselineCreationFromDataAssmnt to set
   */
  public void setBaselineCreationFromDataAssmnt(final boolean baselineCreationFromDataAssmnt) {
    this.baselineCreationFromDataAssmnt = baselineCreationFromDataAssmnt;
  }

}