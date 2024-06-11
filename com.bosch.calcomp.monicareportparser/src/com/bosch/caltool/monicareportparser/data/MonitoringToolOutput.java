/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.monicareportparser.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author rgo7cob
 */
public class MonitoringToolOutput {

  /**
   * a2l file name
   */
  private String a2lFilePath;


  /**
   * dcm file path
   */
  private String dcmFilePath;
  /**
   * reviewStatus
   */
  private String reviewStatus;
  /**
   * ParameterInfo map
   */
  Map<String, ParameterInfo> paramInfoMap = new ConcurrentHashMap<>();

  /**
   * @return the a2lFilePath
   */
  public String getA2lFilePath() {
    return this.a2lFilePath;
  }


  /**
   * @param a2lFilePath the a2lFilePath to set
   */
  public void setA2lFilePath(final String a2lFilePath) {
    this.a2lFilePath = a2lFilePath;
  }

  /**
   * @return the dcmFilePath
   */
  public String getDcmFilePath() {
    return this.dcmFilePath;
  }

  /**
   * @param dcmFilePath the dcmFilePath to set
   */
  public void setDcmFilePath(final String dcmFilePath) {
    this.dcmFilePath = dcmFilePath;
  }

  /**
   * @return the reviewStatus
   */
  public String getReviewStatus() {
    return this.reviewStatus;
  }

  /**
   * @param reviewStatus the reviewStatus to set
   */
  public void setReviewStatus(final String reviewStatus) {
    this.reviewStatus = reviewStatus;
  }

  /**
   * @return the paramInfoMap
   */
  public Map<String, ParameterInfo> getParamInfoMap() {
    return this.paramInfoMap;
  }

  /**
   * @param paramInfoMap the paramInfoMap to set
   */
  public void setParamInfoMap(final Map<String, ParameterInfo> paramInfoMap) {
    this.paramInfoMap = paramInfoMap;
  }


}
