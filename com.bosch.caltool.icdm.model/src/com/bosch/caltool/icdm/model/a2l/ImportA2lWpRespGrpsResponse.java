/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashSet;
import java.util.Set;

/**
 * @author and4cob
 */
public class ImportA2lWpRespGrpsResponse {

  private Set<A2lWpParamMapping> a2lWpParamMappingSet = new HashSet<>();

  private Set<A2lWpResponsibility> wpRespPalSet = new HashSet<>();

  private Set<A2lResponsibility> respSet = new HashSet<>();

  private Set<A2lWorkPackage> wrkPkgSet = new HashSet<>();

  /**
   * @return the a2lWpParamMappingSet
   */
  public Set<A2lWpParamMapping> getA2lWpParamMappingSet() {
    return this.a2lWpParamMappingSet;
  }

  /**
   * @param a2lWpParamMappingSet the a2lWpParamMappingSet to set
   */
  public void setA2lWpParamMappingSet(final Set<A2lWpParamMapping> a2lWpParamMappingSet) {
    this.a2lWpParamMappingSet = a2lWpParamMappingSet;
  }

  /**
   * @return the wpRespPalSet
   */
  public Set<A2lWpResponsibility> getWpRespPalSet() {
    return this.wpRespPalSet;
  }

  /**
   * @param wpRespPalSet the wpRespPalSet to set
   */
  public void setWpRespPalSet(final Set<A2lWpResponsibility> wpRespPalSet) {
    this.wpRespPalSet = wpRespPalSet;
  }

  /**
   * @return the respSet
   */
  public Set<A2lResponsibility> getRespSet() {
    return this.respSet;
  }

  /**
   * @param respSet the respSet to set
   */
  public void setRespSet(final Set<A2lResponsibility> respSet) {
    this.respSet = respSet;
  }

  /**
   * @return the wrkPkgSet
   */
  public Set<A2lWorkPackage> getWrkPkgSet() {
    return this.wrkPkgSet;
  }

  /**
   * @param wrkPkgSet the wrkPkgSet to set
   */
  public void setWrkPkgSet(final Set<A2lWorkPackage> wrkPkgSet) {
    this.wrkPkgSet = wrkPkgSet;
  }
}
