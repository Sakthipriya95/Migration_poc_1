/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;

/**
 * @author dja7cob
 */
public class PidcTreeRvwVariant {

  private PidcVariant rvwVar;

  private Map<String, Map<Long, CDRReviewResult>> grpWpCdrMap = new HashMap<>();

  /**
   * @return the rvwVar
   */
  public PidcVariant getRvwVar() {
    return this.rvwVar;
  }


  /**
   * @param rvwVar the rvwVar to set
   */
  public void setRvwVar(final PidcVariant rvwVar) {
    this.rvwVar = rvwVar;
  }


  /**
   * @return the grpWpCdrMap
   */
  public Map<String, Map<Long, CDRReviewResult>> getGrpWpCdrMap() {
    return this.grpWpCdrMap;
  }


  /**
   * @param grpWpCdrMap the grpWpCdrMap to set
   */
  public void setGrpWpCdrMap(final Map<String, Map<Long, CDRReviewResult>> grpWpCdrMap) {
    this.grpWpCdrMap = grpWpCdrMap;
  }


}
