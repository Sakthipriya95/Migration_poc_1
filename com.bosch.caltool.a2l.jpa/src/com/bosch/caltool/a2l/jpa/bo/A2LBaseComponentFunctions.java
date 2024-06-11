/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.math.BigDecimal;

import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author dmo5cob
 */
@Deprecated
public class A2LBaseComponentFunctions implements Comparable<A2LBaseComponentFunctions> {

  private final BigDecimal functionId;

  private final String functionversion;

  private final String longidentifier;

  private final BigDecimal moduleId;

  private final String name;

  private final BigDecimal sdomBcId;


  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * @param functionId -functionId
   * @param functionversion - functionversion
   * @param longidentifier - longidentifier
   * @param moduleId - moduleId
   * @param name - name
   * @param sdomBcId - sdomBcId
   */
  public A2LBaseComponentFunctions(final BigDecimal functionId, final String functionversion,
      final String longidentifier, final BigDecimal moduleId, final String name, final BigDecimal sdomBcId) {

    this.functionId = functionId;
    this.functionversion = functionversion;
    this.longidentifier = longidentifier;
    this.moduleId = moduleId;
    this.sdomBcId = sdomBcId;
    this.name = name;
  }

  /**
   * @return the functionId
   */
  public BigDecimal getFunctionId() {
    return this.functionId;
  }


  /**
   * @return the functionversion
   */
  public String getFunctionversion() {
    return this.functionversion;
  }


  /**
   * @return the longidentifier
   */
  public String getLongidentifier() {
    return this.longidentifier;
  }


  /**
   * @return the moduleId
   */
  public BigDecimal getModuleId() {
    return this.moduleId;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @return the sdomBcId
   */
  public BigDecimal getSdomBcId() {
    return this.sdomBcId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LBaseComponentFunctions arg0) {

    return ApicUtil.compare(getName(), arg0.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    A2LBaseComponentFunctions a2lBaseCompFunc = (A2LBaseComponentFunctions) obj;
    if (getName() == null) {
      if (a2lBaseCompFunc.getName() != null) {
        return false;
      }
      return true;
    }
    return getName().equals(a2lBaseCompFunc.getName());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getName() == null) ? 0 : getName().hashCode());
    return result;
  }

}
