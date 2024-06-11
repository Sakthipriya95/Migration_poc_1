/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmr1cob
 */
public class FunctionInput {

  private Set<String> functionSet;

  private Set<String> a2lFunctionset;


  /**
   * @return the functionSet
   */
  public Set<String> getFunctionSet() {
    return this.functionSet == null ? null : new HashSet<>(this.functionSet);
  }


  /**
   * @param functionSet the functionSet to set
   */
  public void setFunctionSet(final Set<String> functionSet) {
    if (functionSet != null) {
      this.functionSet = new HashSet<>(functionSet);
    }
  }


  /**
   * @return the a2lFunctionset
   */
  public Set<String> getA2lFunctionset() {
    return this.a2lFunctionset == null ? null : new HashSet<>(this.a2lFunctionset);
  }


  /**
   * @param a2lFunctionset the a2lFunctionset to set
   */
  public void setA2lFunctionset(final Set<String> a2lFunctionset) {
    if (a2lFunctionset != null) {
      this.a2lFunctionset = new HashSet<>(a2lFunctionset);
    }
  }


}
