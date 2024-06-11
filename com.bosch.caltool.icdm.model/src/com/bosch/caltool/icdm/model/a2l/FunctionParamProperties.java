/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class FunctionParamProperties implements Comparable<FunctionParamProperties> {


  private Long funcId;

  private String functionName;

  private Long paramId;

  private String paramName;

  private String paramLongName;

  private String paramLongNameGer;

  private String paramType;


  /**
   * @return the funcId
   */
  public Long getFuncId() {
    return this.funcId;
  }


  /**
   * @param funcId the funcId to set
   */
  public void setFuncId(final Long funcId) {
    this.funcId = funcId;
  }


  /**
   * @return the functionName
   */
  public String getFunctionName() {
    return this.functionName;
  }


  /**
   * @param functionName the functionName to set
   */
  public void setFunctionName(final String functionName) {
    this.functionName = functionName;
  }


  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }


  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the paramLongName
   */
  public String getParamLongName() {
    return this.paramLongName;
  }


  /**
   * @param paramLongName the paramLongName to set
   */
  public void setParamLongName(final String paramLongName) {
    this.paramLongName = paramLongName;
  }


  /**
   * @return the paramLongNameGer
   */
  public String getParamLongNameGer() {
    return this.paramLongNameGer;
  }


  /**
   * @param paramLongNameGer the paramLongNameGer to set
   */
  public void setParamLongNameGer(final String paramLongNameGer) {
    this.paramLongNameGer = paramLongNameGer;
  }


  /**
   * @return the paramType
   */
  public String getParamType() {
    return this.paramType;
  }


  /**
   * @param paramType the paramType to set
   */
  public void setParamType(final String paramType) {
    this.paramType = paramType;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FunctionParamProperties paramProperties) {
    return paramProperties.getParamName().compareTo(getParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {

    return super.hashCode() + getParamName().hashCode();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getParamName(), ((FunctionParamProperties) obj).getParamName());
  }


}
