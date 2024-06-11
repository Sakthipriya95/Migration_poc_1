/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author rgo7cob
 */
public class WpRespLabelResponse {


  private WpRespModel wpRespModel;

  private long paramId;

  private String paramName;

  private String paramDescription;


  /**
   * @return the wpRespModel
   */
  public WpRespModel getWpRespModel() {
    return this.wpRespModel;
  }


  /**
   * @param wpRespModel the wpRespModel to set
   */
  public void setWpRespModel(final WpRespModel wpRespModel) {
    this.wpRespModel = wpRespModel;
  }


  /**
   * @return the paramId
   */
  public long getParamId() {
    return this.paramId;
  }


  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final long paramId) {
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
   * @return the paramDescription
   */
  public String getParamDescription() {
    return this.paramDescription;
  }


  /**
   * @param paramDescription the paramDescription to set
   */
  public void setParamDescription(final String paramDescription) {
    this.paramDescription = paramDescription;
  }


}
