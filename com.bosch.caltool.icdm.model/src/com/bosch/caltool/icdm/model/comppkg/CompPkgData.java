/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author say8cob
 */
public class CompPkgData {

  private Set<CompPkgBc> bcSet = new HashSet<CompPkgBc>();

  private Map<Long, Set<CompPkgFc>> fcMap = new HashMap<Long, Set<CompPkgFc>>();


  /**
   * @return the bcSet
   */
  public Set<CompPkgBc> getBcSet() {
    return this.bcSet == null ? null : new TreeSet<>(this.bcSet);
  }


  /**
   * @param bcSet the bcSet to set
   */
  public void setBcSet(final Set<CompPkgBc> bcSet) {
    this.bcSet = bcSet == null ? null : new TreeSet<>(bcSet);
  }


  /**
   * @return the fcMap
   */
  public Map<Long, Set<CompPkgFc>> getFcMap() {
    return this.fcMap;
  }


  /**
   * @param fcMap the fcMap to set
   */
  public void setFcMap(final Map<Long, Set<CompPkgFc>> fcMap) {
    this.fcMap = fcMap;
  }


}
