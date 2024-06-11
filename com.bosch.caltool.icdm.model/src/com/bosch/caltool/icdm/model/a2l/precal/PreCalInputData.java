/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l.precal;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bne4cob
 */
public class PreCalInputData {

  private Long pidcA2lId;
  /**
   * Optional, required if source has parameter dependencies at variant level
   */
  private Long variantId;

  private String sourceType;

  private Long ruleSetId;

  private boolean onlyExactMatch;
  private boolean refValues;

  private Set<String> paramSet = new HashSet<>();


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the sourceType
   */
  public String getSourceType() {
    return this.sourceType;
  }


  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }


  /**
   * @return the ruleSetId
   */
  public Long getRuleSetId() {
    return this.ruleSetId;
  }


  /**
   * @param ruleSetId the ruleSetId to set
   */
  public void setRuleSetId(final Long ruleSetId) {
    this.ruleSetId = ruleSetId;
  }


  /**
   * @return the onlyExactMatch
   */
  public boolean isOnlyExactMatch() {
    return this.onlyExactMatch;
  }


  /**
   * @param onlyExactMatch the onlyExactMatch to set
   */
  public void setOnlyExactMatch(final boolean onlyExactMatch) {
    this.onlyExactMatch = onlyExactMatch;
  }


  /**
   * @return the refValues
   */
  public boolean isRefValues() {
    return this.refValues;
  }


  /**
   * @param refValues the refValues to set
   */
  public void setRefValues(final boolean refValues) {
    this.refValues = refValues;
  }


  /**
   * @return the paramSet
   */
  public Set<String> getParamSet() {
    return this.paramSet == null ? null : this.paramSet;
  }


  /**
   * @param paramSet the paramSet to set
   */
  public void setParamSet(final Set<String> paramSet) {
    if (this.paramSet != null) {
      this.paramSet = new HashSet<>(paramSet);
    }
  }

}
