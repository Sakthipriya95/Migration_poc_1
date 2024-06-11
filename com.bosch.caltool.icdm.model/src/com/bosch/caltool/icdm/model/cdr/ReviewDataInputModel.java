/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

/**
 * @author rgo7cob
 */
public class ReviewDataInputModel {

  private List<String> paramName;
  
  private String varCodedParam;
  
  /**
   * @return the varCodedParam
   */
  public String getVarCodedParam() {
    return varCodedParam;
  }

  /**
   * @param varCodedParam the varCodedParam to set
   */
  public void setVarCodedParam(String varCodedParam) {
    this.varCodedParam = varCodedParam;
  }

  /**
   * @return the paramName
   */
  public List<String> getParamName() {
    return paramName;
  }

  /**
   * @param paramName the paramName to set
   */
  public void setParamName(List<String> paramName) {
    this.paramName = paramName;
  }

}
