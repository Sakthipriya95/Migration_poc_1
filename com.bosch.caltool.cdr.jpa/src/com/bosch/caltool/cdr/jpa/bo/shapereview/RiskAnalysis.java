/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.shapereview;

import java.util.HashSet;
import java.util.Set;

/**
 * Identify the BCs that are risky
 *
 * @author bne4cob
 */
public class RiskAnalysis {

  /**
   * Shape Review Data
   */
  private final ShapeReviewData data;

  /**
   * @param data ShapeReviewData
   */
  public RiskAnalysis(final ShapeReviewData data) {
    this.data = data;
  }

  /**
   * Identify the BCs that are risky
   *
   * @return
   */
  public Set<String> findRiskyBCs() {
    // Change to appropriate return type from PIDC

    // Hardcoded for testing
    Set<String> riskyBCs = new HashSet<>();

    return riskyBCs;
  }

  /**
   * @return true, if risk is present
   */
  public boolean hasRisk() {
    return true;
  }

}
