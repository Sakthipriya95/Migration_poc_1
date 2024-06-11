/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmr1cob
 */
public class ParameterInput {

  private Set<String> labelSet;

  private Set<String> a2lLabelset;


  /**
   * @return the labelSet
   */
  public Set<String> getLabelSet() {
    return this.labelSet == null ? null : new HashSet<>(this.labelSet);
  }


  /**
   * @param labelSet the labelSet to set
   */
  public void setLabelSet(final Set<String> labelSet) {
    if (labelSet != null) {
      this.labelSet = new HashSet<>(labelSet);
    }
  }


  /**
   * @return the a2lLabelset
   */
  public Set<String> getA2lLabelset() {
    return this.a2lLabelset == null ? null : new HashSet<>(this.a2lLabelset);
  }


  /**
   * @param a2lLabelset the a2lLabelset to set
   */
  public void setA2lLabelset(final Set<String> a2lLabelset) {
    if (a2lLabelset != null) {
      this.a2lLabelset = new HashSet<>(a2lLabelset);
    }
  }


}
