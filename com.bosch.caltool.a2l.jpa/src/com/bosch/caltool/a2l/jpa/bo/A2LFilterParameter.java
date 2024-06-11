/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.calmodel.a2ldata.AbstractA2LObject;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * ICDM-886
 *
 * @author bru2cob
 */
@Deprecated
public class A2LFilterParameter implements Comparable<A2LFilterParameter> {

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * @author rgo7cob for Sort the Parametres
   */
  public enum SortColumns {
                           /**
                            * Sort Based On the Name
                            */
                           SORT_CHAR_NAME,
                           /**
                            * Sort Based on Unit
                            */
                           SORT_CHAR_UNIT,
                           /**
                            * Sort Based on class
                            */
                           SORT_CHAR_CLASS,
                           /**
                            * Sort Based on code
                            */
                           SORT_MIN_VAL,
                           /**
                            * Sort Based on caldataphy value
                            */
                           SORT_MAX_VAL,
  }

  // sel A2L param
  private final A2LParameter a2lParam;


  // parameter min value
  private String minValue = "-";

  // parameter max value
  private String maxValue = "-";

  // parameter A2l name
  private String paramA2LName;

  // ICDM-935
  // sel caldata
  private final CalData calData;

  // set param name
  private final String paramName;

  // set param unit
  private final String paramUnit;

  // set param class
  private String paramClass;

  // set func name
  private final String funcName;

  // set long name
  private final String longName;


  /**
   * @param a2lParam selec a2l parameter
   */
  public A2LFilterParameter(final A2LParameter a2lParam) {
    this.a2lParam = a2lParam;
    // ICDM-935
    this.calData = a2lParam.getCalData();
    this.paramName = a2lParam.getName();
    this.paramUnit = a2lParam.getUnit();
    this.paramClass = a2lParam.getPclassString();
    this.funcName = a2lParam.getDefFunction().getName();
    this.longName = a2lParam.getLongIdentifier();
  }

  /**
   * ICDM-935
   *
   * @param calData selec caldata
   */
  public A2LFilterParameter(final CalData calData) {
    this.a2lParam = null;
    this.calData = calData;
    this.paramName = calData.getShortName();
    this.paramUnit = calData.getCalDataPhy().getUnit();
    this.paramClass = "";
    this.funcName = calData.getFunctionName();
    this.longName = calData.getLongName();
  }


  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @return the longName
   */
  public String getLongName() {
    return this.longName;
  }


  /**
   * @return the maxValue
   */
  public String getMaxValue() {
    return this.maxValue;
  }


  /**
   * @param maxValue the maxValue to set
   */
  public void setMaxValue(final String maxValue) {
    this.maxValue = maxValue;
  }


  /**
   * @return the minValue
   */
  public String getMinValue() {
    return this.minValue;
  }


  /**
   * @param minValue the minValue to set
   */
  public void setMinValue(final String minValue) {
    this.minValue = minValue;
  }

  /**
   * @return the a2lParam
   */
  public A2LParameter getA2lParam() {
    return this.a2lParam;
  }

  /**
   * @return the paramA2LName
   */
  public String getParamA2LName() {
    return this.paramA2LName;
  }


  /**
   * @param paramA2LName the paramA2LName to set
   */
  public void setParamA2LName(final String paramA2LName) {
    this.paramA2LName = paramA2LName;
  }

  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }

  /**
   * @return the paramClass
   */
  public String getParamClass() {
    return this.paramClass;
  }


  /**
   * @return the calData
   */
  public CalData getCalData() {
    return this.calData;
  }

  /**
   * @return the paramUnit
   */
  public String getParamUnit() {
    return this.paramUnit;
  }

  /**
   * @param paramClass the paramClass to set
   */
  public void setParamClass(final String paramClass) {
    this.paramClass = paramClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LFilterParameter arg0) {
    return ApicUtil.compare(getParamName(), arg0.getParamName());

  }

  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return the Compare Int
   */
  public int compareTo(final A2LFilterParameter param2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {

      case SORT_CHAR_NAME:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getParamName(), param2.getParamName());
        break;

      case SORT_CHAR_UNIT:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getParamUnit(), param2.getParamUnit());
        break;

      case SORT_CHAR_CLASS:
        compareResult = AbstractA2LObject.compare(getParamClass(), param2.getParamClass());
        break;

      case SORT_MIN_VAL:
        compareResult = AbstractA2LObject.compare(getMinValue(), param2.getMinValue());
        break;

      case SORT_MAX_VAL:
        compareResult = AbstractA2LObject.compare(getMaxValue(), param2.getMaxValue());
        break;

      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = AbstractA2LObject.compare(getParamName(), param2.getParamName());
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
    A2LFilterParameter a2lFilterParam = (A2LFilterParameter) obj;
    if (getParamName() == null) {
      if (a2lFilterParam.getParamName() != null) {
        return false;
      }
      return true;
    }
    return getParamName().equals(a2lFilterParam.getParamName());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getParamName() == null) ? 0 : getParamName().hashCode());
    return result;
  }
}
