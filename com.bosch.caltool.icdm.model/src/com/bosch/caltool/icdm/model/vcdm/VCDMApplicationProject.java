/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.vcdm;

import java.util.Map;
import java.util.TreeMap;


/**
 * A ICDM specific Class representing APRJ in VCDM
 *
 * @author jvi6cob
 */
public class VCDMApplicationProject {


  private String aprjName;

  private Map<String, VCDMProductKey> vcdmVariants = new TreeMap<>();


  /**
   * @return the aprjName
   */
  public String getAprjName() {
    return this.aprjName;
  }


  /**
   * @param aprjName the aprjName to set
   */
  public void setAprjName(final String aprjName) {
    this.aprjName = aprjName;
  }


  /**
   * @return the vcdmVariants
   */
  public Map<String, VCDMProductKey> getVcdmVariants() {
    return this.vcdmVariants;
  }


  /**
   * @param vcdmVariants the vcdmVariants to set
   */
  public void setVcdmVariants(final Map<String, VCDMProductKey> vcdmVariants) {
    this.vcdmVariants = vcdmVariants;
  }


}
