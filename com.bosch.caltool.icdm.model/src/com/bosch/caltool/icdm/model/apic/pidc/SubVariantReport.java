/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Map;
import java.util.TreeMap;


/**
 * Class used to define Sub-Variant for JSON output
 * 
 * @author jvi6cob
 */
public class SubVariantReport {

  private String name;
  private String description;
  private Long sVariantID;
  private Map<String, String> pidcSubVarAttrMap = new TreeMap<>();

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the pidcSubVarAttrMap
   */
  public Map<String, String> getPidcSubVarAttrMap() {
    return this.pidcSubVarAttrMap;
  }

  /**
   * @param pidcSubVarAttrMap the pidcSubVarAttrMap to set
   */
  public void setPidcSubVarAttrMap(final Map<String, String> pidcSubVarAttrMap) {
    this.pidcSubVarAttrMap = pidcSubVarAttrMap;
  }


  /**
   * @return the sVariantID
   */
  public Long getsVariantID() {
    return this.sVariantID;
  }


  /**
   * @param sVariantID the sVariantID to set
   */
  public void setsVariantID(final Long sVariantID) {
    this.sVariantID = sVariantID;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }

}
