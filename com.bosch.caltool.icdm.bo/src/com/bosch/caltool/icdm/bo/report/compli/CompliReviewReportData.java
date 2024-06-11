/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.user.User;

/**
 * Data model class to hold data for generating pdf report
 *
 * @author pdh2cob
 * @deprecated not used
 */
@Deprecated
public class CompliReviewReportData {


  /**
   * Map with id and web flow output object
   */
  private Map<Long, CompliReviewOutputData> reviewOutput;

  private CompliReviewInputMetaData reviewInput;

  /**
   * Key - hex file index Value - pidc name
   */
  private final Map<Long, String> pidcElementNameMap = new HashMap<>();


  private int numberOfParamsInA2l;

  private String reportFilePath;

  private User user;

  /**
   * @param reviewOutput - review output
   * @param reviewInput - review input
   * @param reportFilePath - report file name
   * @param user - requesting user details
   */
  public CompliReviewReportData(final Map<Long, CompliReviewOutputData> reviewOutput,
      final CompliReviewInputMetaData reviewInput, final String reportFilePath, final User user) {
    this.reportFilePath = reportFilePath;
    this.reviewInput = reviewInput;
    this.reviewOutput = reviewOutput;
    this.user = user;
  }


  /**
   * @return the reviewOutput
   */
  public Map<Long, CompliReviewOutputData> getReviewOutput() {
    return this.reviewOutput;
  }

  /**
   * @param reviewOutput - output
   */
  public void setReviewOutput(final Map<Long, CompliReviewOutputData> reviewOutput) {
    this.reviewOutput = reviewOutput;

  }

  /**
   * @return the reviewInput
   */
  public CompliReviewInputMetaData getReviewInput() {
    return this.reviewInput;
  }

  /**
   * @param reviewInput - input
   */
  public void setReviewInput(final CompliReviewInputMetaData reviewInput) {
    this.reviewInput = reviewInput;

  }

  /**
   * @return the reportFilePath
   */
  public String getReportFilePath() {
    return this.reportFilePath;
  }

  /**
   * @param reportFilePath - filePath
   */
  public void setReportFilePath(final String reportFilePath) {
    this.reportFilePath = reportFilePath;
  }


  /**
   * @return the numberOfParamsInA2l
   */
  public int getNumberOfParamsInA2l() {
    return this.numberOfParamsInA2l;
  }


  /**
   * @param numberOfParamsInA2l the numberOfParamsInA2l to set
   */
  public void setNumberOfParamsInA2l(final int numberOfParamsInA2l) {
    this.numberOfParamsInA2l = numberOfParamsInA2l;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return this.user;
  }


  /**
   * @param user the user to set
   */
  public void setUser(final User user) {
    this.user = user;
  }

  /**
   * @return the pidcElementNameMap
   */
  public Map<Long, String> getPidcElementNameMap() {
    return this.pidcElementNameMap;
  }

}
