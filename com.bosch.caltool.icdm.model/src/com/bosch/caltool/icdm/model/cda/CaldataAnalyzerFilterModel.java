/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerFilterModel {

  private List<ParameterFilterLabel> paramFilterList = new ArrayList<>();

  private List<FunctionFilter> functionFilterList = new ArrayList<>();

  private List<SystemConstantFilter> sysconFilterList = new ArrayList<>();

  private CustomerFilterModel customerFilterModel = new CustomerFilterModel();

  private PlatformFilterModel platformFilterModel = new PlatformFilterModel();


  /**
   * @return the platformFilterModel
   */
  public PlatformFilterModel getPlatformFilterModel() {
    return this.platformFilterModel;
  }


  /**
   * @param platformFilterModel the platformFilterModel to set
   */
  public void setPlatformFilterModel(final PlatformFilterModel platformFilterModel) {
    this.platformFilterModel = platformFilterModel;
  }


  /**
   * @return the customerFilterModel
   */
  public CustomerFilterModel getCustomerFilterModel() {
    return this.customerFilterModel;
  }


  /**
   * @param customerFilterModel the customerFilterModel to set
   */
  public void setCustomerFilterModel(final CustomerFilterModel customerFilterModel) {
    this.customerFilterModel = customerFilterModel;
  }


  /**
   * @return the functionFilterList
   */
  public List<FunctionFilter> getFunctionFilterList() {
    return this.functionFilterList;
  }


  /**
   * @param functionFilterList the functionFilterList to set
   */
  public void setFunctionFilterList(final List<FunctionFilter> functionFilterList) {
    this.functionFilterList = functionFilterList;
  }


  /**
   * @return the paramFilterList
   */
  public List<ParameterFilterLabel> getParamFilterList() {
    return this.paramFilterList;
  }


  /**
   * @param paramFilterList the paramFilterList to set
   */
  public void setParamFilterList(final List<ParameterFilterLabel> paramFilterList) {
    this.paramFilterList = paramFilterList;
  }


  /**
   * @return the sysconFilterList
   */
  public List<SystemConstantFilter> getSysconFilterList() {
    return this.sysconFilterList;
  }


  /**
   * @param sysconFilterList the sysconFilterList to set
   */
  public void setSysconFilterList(final List<SystemConstantFilter> sysconFilterList) {
    this.sysconFilterList = sysconFilterList;
  }


}
