/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;

/**
 * @author mkl2cob
 */
public class PidcVarChangeModel {

  private PidcVersion pidcVersBeforeUpdate;

  private PidcVersion pidcVersAfterUpdate;

  private PidcVariant pidcVarBeforeUpdate;

  private PidcVariant pidcVarAfterUpdate;

  private Set<PidcVariantCocWp> createdPidcVarCocWpSet = new HashSet<>();

  /**
   * @return the pidcVersBeforeUpdate
   */
  public PidcVersion getPidcVersBeforeUpdate() {
    return this.pidcVersBeforeUpdate;
  }


  /**
   * @param pidcVersBeforeUpdate the pidcVersBeforeUpdate to set
   */
  public void setPidcVersBeforeUpdate(final PidcVersion pidcVersBeforeUpdate) {
    this.pidcVersBeforeUpdate = pidcVersBeforeUpdate;
  }


  /**
   * @return the pidcVersAfterUpdate
   */
  public PidcVersion getPidcVersAfterUpdate() {
    return this.pidcVersAfterUpdate;
  }


  /**
   * @param pidcVersAfterUpdate the pidcVersAfterUpdate to set
   */
  public void setPidcVersAfterUpdate(final PidcVersion pidcVersAfterUpdate) {
    this.pidcVersAfterUpdate = pidcVersAfterUpdate;
  }


  /**
   * @return the pidcVarBeforeUpdate
   */
  public PidcVariant getPidcVarBeforeUpdate() {
    return this.pidcVarBeforeUpdate;
  }


  /**
   * @param pidcVarBeforeUpdate the pidcVarBeforeUpdate to set
   */
  public void setPidcVarBeforeUpdate(final PidcVariant pidcVarBeforeUpdate) {
    this.pidcVarBeforeUpdate = pidcVarBeforeUpdate;
  }


  /**
   * @return the pidcVarAfterUpdate
   */
  public PidcVariant getPidcVarAfterUpdate() {
    return this.pidcVarAfterUpdate;
  }


  /**
   * @param pidcVarAfterUpdate the pidcVarAfterUpdate to set
   */
  public void setPidcVarAfterUpdate(final PidcVariant pidcVarAfterUpdate) {
    this.pidcVarAfterUpdate = pidcVarAfterUpdate;
  }


  /**
   * @return the createdPidcVarCocWp
   */
  public Set<PidcVariantCocWp> getCreatedPidcVarCocWpSet() {
    return this.createdPidcVarCocWpSet;
  }


  /**
   * @param createdPidcVarCocWp the createdPidcVarCocWp to set
   */
  public void setCreatedPidcVarCocWpSet(final Set<PidcVariantCocWp> createdPidcVarCocWp) {
    this.createdPidcVarCocWpSet = createdPidcVarCocWp;
  }


}
