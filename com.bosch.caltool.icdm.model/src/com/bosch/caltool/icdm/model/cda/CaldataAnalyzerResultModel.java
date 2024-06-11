/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerResultModel {

  /**
   * Directory of result files
   */
  private String outputDirectory;

  /**
   * List of result files - .xls, .xlsx, .dcm files
   */
  private List<CaldataAnalyzerResultFileModel> resultFiles = new ArrayList<>();

  /**
   * List of json files
   */
  private List<CaldataAnalyzerResultFileModel> jsonFiles = new ArrayList<>();

  private CaldataAnalyzerResultSummary summaryModel = new CaldataAnalyzerResultSummary();

  /**
   * @return the outputDirectory
   */
  public String getOutputDirectory() {
    return this.outputDirectory;
  }


  /**
   * @param outputDirectory the outputDirectory to set
   */
  public void setOutputDirectory(final String outputDirectory) {
    this.outputDirectory = outputDirectory;
  }


  /**
   * @return the resultFiles
   */
  public List<CaldataAnalyzerResultFileModel> getResultFiles() {
    return this.resultFiles;
  }


  /**
   * @param resultFiles the resultFiles to set
   */
  public void setResultFiles(final List<CaldataAnalyzerResultFileModel> resultFiles) {
    this.resultFiles = resultFiles;
  }


  /**
   * @return the jsonFiles
   */
  public List<CaldataAnalyzerResultFileModel> getJsonFiles() {
    return this.jsonFiles;
  }


  /**
   * @param jsonFiles the jsonFiles to set
   */
  public void setJsonFiles(final List<CaldataAnalyzerResultFileModel> jsonFiles) {
    this.jsonFiles = jsonFiles;
  }


  /**
   * @return the summaryModel
   */
  public CaldataAnalyzerResultSummary getSummaryModel() {
    return this.summaryModel;
  }


  /**
   * @param summaryModel the summaryModel to set
   */
  public void setSummaryModel(final CaldataAnalyzerResultSummary summaryModel) {
    this.summaryModel = summaryModel;
  }


}
