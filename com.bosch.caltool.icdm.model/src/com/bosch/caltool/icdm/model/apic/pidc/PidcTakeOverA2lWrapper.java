/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author say8cob
 */
public class PidcTakeOverA2lWrapper {

  private Long sourceWpDefVersId;

  private Set<PidcA2l> pidcA2lsToCreate = new HashSet<>();

  private boolean deriveFromFunc;

  /**
   * @return the sourceWpDefVersId
   */
  public Long getSourceWpDefVersId() {
    return this.sourceWpDefVersId;
  }


  /**
   * @param sourceWpDefVersId the sourceWpDefVersId to set
   */
  public void setSourceWpDefVersId(final Long sourceWpDefVersId) {
    this.sourceWpDefVersId = sourceWpDefVersId;
  }


  /**
   * @return the pidcA2lsToCreate
   */
  public Set<PidcA2l> getPidcA2lsToCreate() {
    return this.pidcA2lsToCreate;
  }


  /**
   * @param pidcA2lsToCreate the pidcA2lsToCreate to set
   */
  public void setPidcA2lsToCreate(final Set<PidcA2l> pidcA2lsToCreate) {
    this.pidcA2lsToCreate = pidcA2lsToCreate;
  }


  /**
   * @return the derivateFromFunc
   */
  public boolean isDeriveFromFunc() {
    return this.deriveFromFunc;
  }


  /**
   * @param deriveFromFunc the deriveFromFunc to set
   */
  public void setDeriveFromFunc(final boolean deriveFromFunc) {
    this.deriveFromFunc = deriveFromFunc;
  }


}
