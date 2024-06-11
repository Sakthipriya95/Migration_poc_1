/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;

import com.bosch.caltool.apic.jpa.comparison.PIDCImportCompareResult;


/**
 * @author jvi6cob Container class used to hold parsed excel results during PIDC import
 */
public class PIDCImportParsedResult {

  private final PIDCVersion pidcVersion;

  private Map<Long, IPIDCAttribute> validImpPIDCAttrs;

  private Map<Long, Map<Long, IPIDCAttribute>> validImpVarAttrs;

  private Map<Long, Map<Long, IPIDCAttribute>> validSubVarAttr;

  private Map<Long, PIDCImportCompareResult> pidcImportCompareResultMap;

  private Map<Long, Map<Long, PIDCImportCompareResult>> varCompResMap;

  private Map<Long, Map<Long, PIDCImportCompareResult>> subVarCompResMap;

  /**
   * @param pidcVersion the PIDC being imported
   */
  public PIDCImportParsedResult(final PIDCVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the modifiedValidImportPIDCAttributes
   */
  public Map<Long, IPIDCAttribute> getModifiedValidImportPIDCAttributes() {
    return this.validImpPIDCAttrs;
  }


  /**
   * @param validImpPIDCAttrs the modifiedValidImportPIDCAttributes to set
   */
  public void setModifiedValidImportPIDCAttributes(final Map<Long, IPIDCAttribute> validImpPIDCAttrs) {
    this.validImpPIDCAttrs = validImpPIDCAttrs;
  }


  /**
   * @return the modifiedValidImportVarAttributes
   */
  public Map<Long, Map<Long, IPIDCAttribute>> getModifiedValidImportVarAttributes() {
    return this.validImpVarAttrs;
  }


  /**
   * @param validImpVarAttrs the modifiedValidImportVarAttributes to set
   */
  public void setModifiedValidImportVarAttributes(
      final Map<Long, Map<Long, IPIDCAttribute>> validImpVarAttrs) {
    this.validImpVarAttrs = validImpVarAttrs;
  }


  /**
   * @return the modifiedValidImportSubVarAttributes
   */
  public Map<Long, Map<Long, IPIDCAttribute>> getModifiedValidImportSubVarAttributes() {
    return this.validSubVarAttr;
  }


  /**
   * @param validSubVarAttr the modifiedValidImportSubVarAttributes to set
   */
  public void setModifiedValidImportSubVarAttributes(
      final Map<Long, Map<Long, IPIDCAttribute>> validSubVarAttr) {
    this.validSubVarAttr = validSubVarAttr;
  }


  /**
   * @return the pidcImportCompareResultMap
   */
  public Map<Long, PIDCImportCompareResult> getPidcImportCompareResultMap() {
    return this.pidcImportCompareResultMap;
  }


  /**
   * @param pidcImportCompareResultMap the pidcImportCompareResultMap to set
   */
  public void setPidcImportCompareResultMap(final Map<Long, PIDCImportCompareResult> pidcImportCompareResultMap) {
    this.pidcImportCompareResultMap = pidcImportCompareResultMap;
  }


  /**
   * @return the pidcImportVariantCompareResultMaps
   */
  public Map<Long, Map<Long, PIDCImportCompareResult>> getPidcImportVariantCompareResultMaps() {
    return this.varCompResMap;
  }


  /**
   * @param varCompResMap the pidcImportVariantCompareResultMaps to set
   */
  public void setPidcImportVariantCompareResultMaps(
      final Map<Long, Map<Long, PIDCImportCompareResult>> varCompResMap) {
    this.varCompResMap = varCompResMap;
  }


  /**
   * @return the pidcImportSubVariantCompareResultMaps
   */
  public Map<Long, Map<Long, PIDCImportCompareResult>> getPidcImportSubVariantCompareResultMaps() {
    return this.subVarCompResMap;
  }


  /**
   * @param subVarCompResMap the pidcImportSubVariantCompareResultMaps to set
   */
  public void setPidcImportSubVariantCompareResultMaps(
      final Map<Long, Map<Long, PIDCImportCompareResult>> subVarCompResMap) {
    this.subVarCompResMap = subVarCompResMap;
  }

  /**
   * @return the pidc
   */
  public PIDCVersion getPidcVersion() {
    return pidcVersion;
  }

}
