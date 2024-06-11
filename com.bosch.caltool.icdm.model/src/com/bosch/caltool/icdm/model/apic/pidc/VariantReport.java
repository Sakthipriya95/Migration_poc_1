/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Class used to define Variant for JSON output
 * 
 * @author jvi6cob
 */
public class VariantReport {

  private String name;
  private String description;
  private Long variantID;
  private Map<String, String> pidcVarAttrMap = new TreeMap<>();
  private List<SubVariantReport> subvariants;


  /**
   * @return the pidcVarAttrMap
   */
  public Map<String, String> getPidcVarAttrMap() {
    return this.pidcVarAttrMap;
  }

  /**
   * @param pidcVarAttrMap the pidcVarAttrMap to set
   */
  public void setPidcVarAttrMap(final Map<String, String> pidcVarAttrMap) {
    this.pidcVarAttrMap = pidcVarAttrMap;
  }

  /**
   * @return the subvariants
   */
  public List<SubVariantReport> getSubvariants() {
    return this.subvariants;
  }

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
   * @param subvariants the subvariants to set
   */
  public void setSubvariants(final List<SubVariantReport> subvariants) {
    this.subvariants = subvariants;
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


  /**
   * @return the variantID
   */
  public Long getVariantID() {
    return this.variantID;
  }


  /**
   * @param variantID the variantID to set
   */
  public void setVariantID(final Long variantID) {
    this.variantID = variantID;
  }


}
