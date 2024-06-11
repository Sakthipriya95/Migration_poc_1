/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.Set;

import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author bru2cob
 */
public class CompliReviewUsingHexData {

  private Set<String> pidcSdomPverSet;
  private Pidc selPidc;


  private Set<PidcVariant> pidcVariants;
  private PidcVersion pidcVersion;
  private boolean isVcdmAprjValSet;


  /**
   * @return the isVcdmAprjValSet
   */
  public boolean isVcdmAprjValSet() {
    return this.isVcdmAprjValSet;
  }


  /**
   * @param isVcdmAprjValSet the isVcdmAprjValSet to set
   */
  public void setVcdmAprjValSet(final boolean isVcdmAprjValSet) {
    this.isVcdmAprjValSet = isVcdmAprjValSet;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the pidcSdomPverSet
   */
  public Set<String> getPidcSdomPverSet() {
    return this.pidcSdomPverSet;
  }

  /**
   * @param pidcSdomPverSet the pidcSdomPverSet to set
   */
  public void setPidcSdomPverSet(final Set<String> pidcSdomPverSet) {
    this.pidcSdomPverSet = pidcSdomPverSet;
  }

  /**
   * @return the selPidc
   */
  public Pidc getSelPidc() {
    return this.selPidc;
  }

  /**
   * @param selPidc the selPidc to set
   */
  public void setSelPidc(final Pidc selPidc) {
    this.selPidc = selPidc;
  }


  /**
   * @return the pidcVariants
   */
  public Set<PidcVariant> getPidcVariants() {
    return this.pidcVariants;
  }

  /**
   * @param pidcVariants the pidcVariants to set
   */
  public void setPidcVariants(final Set<PidcVariant> pidcVariants) {
    this.pidcVariants = pidcVariants;
  }


}
