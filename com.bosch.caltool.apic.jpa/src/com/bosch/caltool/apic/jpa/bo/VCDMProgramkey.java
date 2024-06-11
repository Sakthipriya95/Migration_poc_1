/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Deprecated
/**
 * A ICDM specific Class representing ProgramKey in VCDM
 *
 * @author jvi6cob
 */
public class VCDMProgramkey {

  private String programKeyName;


  Map<BigDecimal, VCDMDSTRevision> vCDMDSTRevisions = new TreeMap<>();


  /**
   * @return the programKeyName
   */
  public String getProgramKeyName() {
    return this.programKeyName;
  }


  /**
   * @param programKeyName the programKeyName to set
   */
  public void setProgramKeyName(final String programKeyName) {
    this.programKeyName = programKeyName;
  }


  /**
   * @return the vCDMDSTRevisions
   */
  public Map<BigDecimal, VCDMDSTRevision> getvCDMDSTRevisions() {
    return this.vCDMDSTRevisions;
  }


  /**
   * @param vCDMDSTRevisions the vCDMDSTRevisions to set
   */
  public void setvCDMDSTRevisions(final Map<BigDecimal, VCDMDSTRevision> vCDMDSTRevisions) {
    this.vCDMDSTRevisions = vCDMDSTRevisions;
  }


}
