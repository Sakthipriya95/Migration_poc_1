/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmr1cob
 */
public class CompPkgParamInput {


  private Set<String> paramName;

  private Long compPkgId;


  /**
   * @return the paramName
   */
  public Set<String> getParamName() {
    return this.paramName == null ? null : new HashSet<>(this.paramName);
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final Set<String> paramName) {
    this.paramName = paramName == null ? null : new HashSet<>(paramName);
  }


  /**
   * @return the compPkgId
   */
  public Long getCompPkgId() {
    return this.compPkgId;
  }


  /**
   * @param compPkgId the compPkgId to set
   */
  public void setCompPkgId(final Long compPkgId) {
    this.compPkgId = compPkgId;
  }
}
