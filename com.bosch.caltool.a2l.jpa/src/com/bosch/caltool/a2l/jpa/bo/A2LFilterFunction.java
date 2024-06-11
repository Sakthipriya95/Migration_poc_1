/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.calmodel.a2ldata.AbstractA2LObject;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author dmo5cob
 */
@Deprecated
public class A2LFilterFunction implements Comparable<A2LFilterFunction> {

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * for Sort the Functions
   */
  public enum SortColumns {
                           /**
                            * Sort Based On the Name
                            */
                           SORT_FUNC_NAME,
                           /**
                            * Sort Based on Unit
                            */
                           SORT_FUNC_VRSN

  }

  // sel Function
  private final Function a2lFunction;
  private String name;
  private String longIdentifier;
  private String functionVersion;


  /**
   * @param a2lFunction selec a2l parameter
   */
  public A2LFilterFunction(final Function a2lFunction) {
    this.a2lFunction = a2lFunction;
    this.name = this.a2lFunction.getName();
    this.functionVersion = this.a2lFunction.getFunctionVersion() == null ? "*" : this.a2lFunction.getFunctionVersion();
    this.longIdentifier = this.a2lFunction.getLongIdentifier();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LFilterFunction arg0) {
    return ApicUtil.compare(getName(), arg0.getName());

  }

  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return the Compare Int
   */
  public int compareTo(final A2LFilterFunction param2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {

      case SORT_FUNC_NAME:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getName(), param2.getName());
        break;

      case SORT_FUNC_VRSN:
        // use compare method for Strings
        compareResult = -AbstractA2LObject.compare(getFunctionVersion(), param2.getFunctionVersion());
        break;
      default:
        compareResult = 0;
        break;
    }


    if (compareResult == 0) {
      // compare result is equal, compare the name
      compareResult = AbstractA2LObject.compare(getName(), param2.getName());
    }

    return compareResult;
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
    A2LFilterFunction a2lFilterFunction = (A2LFilterFunction) obj;
    if (getName() == null) {
      if (a2lFilterFunction.getName() != null) {
        return false;
      }
      return true;
    }
    return getName().equals(a2lFilterFunction.getName());
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

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the longIdentifier
   */
  public String getLongIdentifier() {
    return this.longIdentifier;
  }


  /**
   * @param longIdentifier the longIdentifier to set
   */
  public void setLongIdentifier(final String longIdentifier) {
    this.longIdentifier = longIdentifier;
  }


  /**
   * @return the functionVersion
   */
  public String getFunctionVersion() {
    return this.functionVersion;
  }


  /**
   * @param functionVersion the functionVersion to set
   */
  public void setFunctionVersion(final String functionVersion) {
    this.functionVersion = functionVersion.isEmpty() ? "*" : functionVersion;

  }

  /**
   * @return the a2lFunction
   */
  public Function getA2lFunction() {
    return this.a2lFunction;
  }


}
