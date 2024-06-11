/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmr1cob
 */
public class PidcV2 {

  private long pidcId;

  private Set<Long> useCaseIdSet;


  /**
   * @return the pidcId
   */
  public long getPidcId() {
    return this.pidcId;
  }


  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final long pidcId) {
    this.pidcId = pidcId;
  }


  /**
   * @return the useCaseIdSet
   */
  public Set<Long> getUseCaseIdSet() {
    return this.useCaseIdSet == null ? null : new HashSet<>(this.useCaseIdSet);
  }


  /**
   * @param useCaseIdSet the useCaseIdSet to set
   */
  public void setUseCaseIdSet(final Set<Long> useCaseIdSet) {
    this.useCaseIdSet = useCaseIdSet == null ? null : new HashSet<>(useCaseIdSet);
  }

}
