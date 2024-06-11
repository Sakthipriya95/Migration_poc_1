/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.vcdm;

import java.util.Map;
import java.util.TreeMap;


/**
 * A ICDM specific Class representing Product key in VCDM
 *
 * @author jvi6cob
 */
public class VCDMProductKey {

  String variantName;

  Map<String, VCDMProgramkey> programKeys = new TreeMap<>();


  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }


  /**
   * @return the programKeys
   */
  public Map<String, VCDMProgramkey> getProgramKeys() {
    return this.programKeys;
  }


  /**
   * @param programKeys the programKeys to set
   */
  public void setProgramKeys(final Map<String, VCDMProgramkey> programKeys) {
    this.programKeys = programKeys;
  }

}
