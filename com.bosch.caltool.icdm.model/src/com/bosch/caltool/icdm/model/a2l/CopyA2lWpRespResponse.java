/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author bru2cob
 */
public class CopyA2lWpRespResponse {

  private final Set<A2lWpDefnVersion> wpDefSet = new HashSet<>();

  private Set<PidcA2l> pidcA2lSet = new HashSet<>();

  private final Set<A2lWpResponsibility> wpRespPalSet = new HashSet<>();

  private final Set<A2lWpParamMapping> a2lWpParamMappingSet = new HashSet<>();

  private final Set<A2lVariantGroup> varGrpSet = new HashSet<>();

  /**
   * @return the wpDefSet
   */
  public Set<A2lWpDefnVersion> getWpDefSet() {
    return this.wpDefSet;
  }


  /**
   * @return the pidcA2lSet
   */
  public Set<PidcA2l> getPidcA2lSet() {
    return this.pidcA2lSet;
  }


  /**
   * @param pidcA2lSet the pidcA2lSet to set
   */
  public void setPidcA2lSet(final Set<PidcA2l> pidcA2lSet) {
    this.pidcA2lSet = pidcA2lSet;
  }


  /**
   * @return
   */
  public Set<A2lWpResponsibility> getWpRespPalSet() {
    return this.wpRespPalSet;
  }


  /**
   * @return the a2lWpParamMappingSet
   */
  public Set<A2lWpParamMapping> getA2lWpParamMappingSet() {
    return this.a2lWpParamMappingSet;
  }


  /**
   * @return the varGrpSet
   */
  public Set<A2lVariantGroup> getVarGrpSet() {
    return varGrpSet;
  }


}
