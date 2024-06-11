/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dja7cob
 */
public class PidcImportExcelData {

  /**
   * Selected pidc version for import
   */
  private PidcVersion pidcVersion;

  /**
   * Map of key - attr id, val- pidc version attr obj
   */
  private Map<Long, PidcVersionAttribute> pidcImportAttrMap = new HashMap<>();

  /**
   * Map of key - variant name value id, val- map of attr id, var attr obj
   */
  private Map<Long, Map<Long, PidcVariantAttribute>> varImportAttrMap = new HashMap<>();

  /**
   * key - var name value id, value- Map of key - subvar name value id, val- map of attr id, subvar attr obj
   */
  private Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap = new HashMap<>();


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
   * @return the pidcImportAttrMap
   */
  public Map<Long, PidcVersionAttribute> getPidcImportAttrMap() {
    return this.pidcImportAttrMap;
  }


  /**
   * @param pidcImportAttrMap the pidcImportAttrMap to set
   */
  public void setPidcImportAttrMap(final Map<Long, PidcVersionAttribute> pidcImportAttrMap) {
    this.pidcImportAttrMap = pidcImportAttrMap;
  }


  /**
   * @return the varImportAttrMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getVarImportAttrMap() {
    return this.varImportAttrMap;
  }


  /**
   * @param varImportAttrMap the varImportAttrMap to set
   */
  public void setVarImportAttrMap(final Map<Long, Map<Long, PidcVariantAttribute>> varImportAttrMap) {
    this.varImportAttrMap = varImportAttrMap;
  }


  /**
   * @return the subvarImportAttrMap
   */
  public Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> getSubvarImportAttrMap() {
    return this.subvarImportAttrMap;
  }


  /**
   * @param subvarImportAttrMap the subvarImportAttrMap to set
   */
  public void setSubvarImportAttrMap(
      final Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap) {
    this.subvarImportAttrMap = subvarImportAttrMap;
  }

}
