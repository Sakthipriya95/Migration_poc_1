/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

/**
 * PIDC Search result
 *
 * @author bne4cob
 */
// ICDM-2326
public class PidcSearchResult extends PidcVersionInfo {

  /**
   * Variant ids
   */
  private Map<Long, PidcVariant> variantMap = new HashMap<>();
  /**
   * Sub Variant ids
   */
  private Map<Long, PidcSubVariant> subvariantMap = new HashMap<>();

  /**
   * If true, A2L files are mapped to the PIDC version
   */
  private boolean a2lFilesMapped;

  /**
   * If true, Data review are performed for this version
   */
  private boolean reviewResultsFound;

  /**
   * Has focus matrix
   */
  // ICDM-2255
  private boolean focusMatrixDefined;

  /**
   * @return the variantMap
   */
  public Map<Long, PidcVariant> getVariantMap() {
    return this.variantMap;
  }


  /**
   * @param variantMap the variantMap to set
   */
  public void setVariantMap(final Map<Long, PidcVariant> variantMap) {
    this.variantMap = variantMap;
  }


  /**
   * @param subvariantMap the subvariantMap to set
   */
  public void setSubvariantMap(final Map<Long, PidcSubVariant> subvariantMap) {
    this.subvariantMap = subvariantMap;
  }


  /**
   * @param variant the variantMap to add
   */
  public void addVariant(final PidcVariant variant) {
    this.variantMap.put(variant.getId(), variant);
  }


  /**
   * @return the subvariantMap
   */
  public Map<Long, PidcSubVariant> getSubvariantMap() {
    return this.subvariantMap;
  }


  /**
   * @param subvariant the subvariant to add
   */
  public void addSubVariant(final PidcSubVariant subvariant) {
    this.subvariantMap.put(subvariant.getId(), subvariant);
  }


  /**
   * @return the a2lFilesMapped
   */
  public boolean isA2lFilesMapped() {
    return this.a2lFilesMapped;
  }


  /**
   * @param a2lFilesMapped the a2lFilesMapped to set
   */
  public void setA2lFilesMapped(final boolean a2lFilesMapped) {
    this.a2lFilesMapped = a2lFilesMapped;
  }


  /**
   * @return the reviewResultsFound
   */
  public boolean isReviewResultsFound() {
    return this.reviewResultsFound;
  }


  /**
   * @param reviewResultsFound the reviewResultsFound to set
   */
  public void setReviewResultsFound(final boolean reviewResultsFound) {
    this.reviewResultsFound = reviewResultsFound;
  }

  /**
   * @return the isFocusMatrixDefined
   */
  // ICDM-2255
  public boolean isFocusMatrixDefined() {
    return this.focusMatrixDefined;
  }


  /**
   * @param focusMatrixDefined the isFocusMatrixMapped to set
   */
  // ICDM-2255
  public void setFocusMatrixDefined(final boolean focusMatrixDefined) {
    this.focusMatrixDefined = focusMatrixDefined;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PidcSearchResult [pidc=" + getPidc() + ", pidcVersion=" + getPidcVersion() + ", a2lFilesMapped=" +
        this.a2lFilesMapped + ", reviewResultsFound=" + this.reviewResultsFound + ", focusMatrixDefined=" +
        this.focusMatrixDefined + "]";
  }


}
