/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.AliasDef;

/**
 * @author dja7cob
 */
public class PidcCreationDetails {

  private Set<String> existingPidcNames = new HashSet<>();

  private Map<String, AliasDef> aliasDefMap = new HashMap<>();

  /**
   * @return the existingPidcNames
   */
  public Set<String> getExistingPidcNames() {
    return this.existingPidcNames;
  }

  /**
   * @param existingPidcNames the existingPidcNames to set
   */
  public void setExistingPidcNames(final Set<String> existingPidcNames) {
    this.existingPidcNames = existingPidcNames == null ? null : new HashSet<>(existingPidcNames);
  }

  /**
   * @return the aliasDefMap
   */
  public Map<String, AliasDef> getAliasDefMap() {
    return this.aliasDefMap;
  }

  /**
   * @param aliasDefMap the aliasDefMap to set
   */
  public void setAliasDefMap(final Map<String, AliasDef> aliasDefMap) {
    this.aliasDefMap = aliasDefMap;
  }


}
