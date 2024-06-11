/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dja7cob Model to hold the division Id & function list of a data review
 */
public class RvwFuncDetails {

  private Set<String> funcSet = new HashSet<>();

  private Long divId;

  /**
   * @return the divId
   */
  public Long getDivId() {
    return this.divId;
  }

  /**
   * @param divId the divId to set
   */
  public void setDivId(final Long divId) {
    this.divId = divId;
  }

  /**
   * @return the funcSet
   */
  public Set<String> getFuncSet() {
    return this.funcSet;
  }

  /**
   * @param funcSet the funcSet to set
   */
  public void setFuncSet(final Set<String> funcSet) {
    this.funcSet = funcSet == null ? null : new HashSet<>(funcSet);
  }

}
