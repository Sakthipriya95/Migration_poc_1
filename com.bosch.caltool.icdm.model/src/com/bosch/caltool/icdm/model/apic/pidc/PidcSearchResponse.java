/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;

/**
 * Response object of PIDC Scout REST service
 *
 * @author bne4cob
 */
// ICDM-2326
public class PidcSearchResponse {

  /**
   * response from pidc search
   */
  private Set<PidcSearchResult> results;

  /**
   * Constructor
   */
  public PidcSearchResponse() {
    this.results = new HashSet<>();
  }


  /**
   * @return the results
   */
  public Set<PidcSearchResult> getResults() {
    return this.results;
  }

  /**
   * @param results the results to set
   */
  public void setResults(final Set<PidcSearchResult> results) {
    this.results = results == null ? null : new HashSet<>(results);
  }
}
