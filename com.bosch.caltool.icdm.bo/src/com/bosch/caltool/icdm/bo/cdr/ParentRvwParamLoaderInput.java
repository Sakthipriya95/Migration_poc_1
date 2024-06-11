/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.Parameter;


/**
 * ICDM-2204
 *
 * @author mkl2cob
 */
public class ParentRvwParamLoaderInput {

  /**
   * project id
   */
  private Long projectId;

  /**
   * variant id
   */
  private Long variantId;

  /**
   * set of cdr params for review
   */
  private Set<Parameter> paramSet;

  /**
   * boolean to indicate whether official and locked alone has to be considered
   */
  private boolean considerOfficialLockd;

  /**
   * boolean to indicate whether official and unlocked alone has to be considered
   */
  private boolean considerOfficial;

  /**
   * boolean to indicate whether start and locked alone has to be considered
   */
  private boolean considerStartLocked;

  /**
   * boolean to indicate whether start and unlocked alone has to be considered
   */
  private boolean considerStart;


  /**
   * @return the projectId
   */
  public Long getProjectId() {
    return this.projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the considerOfficialLockd
   */
  public boolean isConsiderOfficialLockd() {
    return this.considerOfficialLockd;
  }


  /**
   * @param considerOfficialLockd the considerOfficialLockd to set
   */
  public void setConsiderOfficialLockd(final boolean considerOfficialLockd) {
    this.considerOfficialLockd = considerOfficialLockd;
  }


  /**
   * @return the considerOfficialUnLockd
   */
  public boolean isConsiderOfficial() {
    return this.considerOfficial;
  }


  /**
   * @param considerOfficialUnLockd the considerOfficialUnLockd to set
   */
  public void setConsiderOfficial(final boolean considerOfficialUnLockd) {
    this.considerOfficial = considerOfficialUnLockd;
  }


  /**
   * @return the considerStartLocked
   */
  public boolean isConsiderStartLocked() {
    return this.considerStartLocked;
  }


  /**
   * @param considerStartLocked the considerStartLocked to set
   */
  public void setConsiderStartLocked(final boolean considerStartLocked) {
    this.considerStartLocked = considerStartLocked;
  }


  /**
   * @return the considerStartUnLocked
   */
  public boolean isConsiderStart() {
    return this.considerStart;
  }


  /**
   * @param considerStartUnLocked the considerStartUnLocked to set
   */
  public void setConsiderStart(final boolean considerStartUnLocked) {
    this.considerStart = considerStartUnLocked;
  }


  /**
   * @return the paramSet
   */
  public Set<Parameter> getParamSet() {
    return this.paramSet;
  }


  /**
   * @param paramSet the paramSet to set
   */
  public void setParamSet(final Set<Parameter> paramSet) {
    this.paramSet = paramSet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [projectId=" + this.projectId + ", variantId=" + this.variantId + ", paramSet size=" +
        (this.paramSet == null ? 0 : this.paramSet.size()) + ", considerOfficialLockd=" + this.considerOfficialLockd +
        ", considerOfficial=" + this.considerOfficial + ", considerStartLocked=" + this.considerStartLocked +
        ", considerStart=" + this.considerStart + "]";
  }

}
