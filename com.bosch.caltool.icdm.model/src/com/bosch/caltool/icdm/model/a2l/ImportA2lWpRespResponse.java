/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * The Class ImportA2lWpRespResponse.
 *
 * @author gge6cob
 */
public class ImportA2lWpRespResponse {

  private Set<A2lWpParamMapping> a2lWpParamMappingSet = new HashSet<>();

  private Set<A2lWpResponsibility> wpRespPalSet = new HashSet<>();

  private Set<A2lResponsibility> respSet = new HashSet<>();

  private Set<A2lWorkPackage> wrkPkgSet = new HashSet<>();

  private Set<String> skippedParams = new HashSet<>();

  private final Set<A2lWpDefnVersion> a2lWpDefnVersSet = new HashSet<>();

  private final Set<PidcA2l> pidcA2lSet = new HashSet<>();


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

  /**
   * @return the skippedParams
   */
  public Set<String> getSkippedParams() {
    return this.skippedParams;
  }


  /**
   * @param skippedParams the skippedParams to set
   */
  public void setSkippedParams(final Set<String> skippedParams) {
    this.skippedParams = skippedParams;
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
   * @return the a2lWpDefnVersSet
   */
  public Set<A2lWpDefnVersion> getA2lWpDefnVersSet() {
    return this.a2lWpDefnVersSet;
  }

  public Set<PidcA2l> getPidcA2lSet() {
    return this.pidcA2lSet;
  }

}
