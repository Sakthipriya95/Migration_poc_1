/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class RvwFunctionModel {

  private Long funcId;

  private String funcName;


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
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getFuncId(), ((RvwFunctionModel) obj).getFuncId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getFuncId());
  }


}
