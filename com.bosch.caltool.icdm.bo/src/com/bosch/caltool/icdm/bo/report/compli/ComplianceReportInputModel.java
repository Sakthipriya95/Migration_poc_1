/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;

/**
 * @author pdh2cob
 */
public class ComplianceReportInputModel implements Serializable {


  /**
   *
   */
  private static final long serialVersionUID = -8009063551679195301L;

  /**
   * Path of file to be generated
   */
  private String destFilePath;

  /**
   * Model with data for compliance review input summary. This is common to both hex compare and compliance reports
   */
  private CompliReviewInputMetaData compliReviewInputMetaData = new CompliReviewInputMetaData();


  /**
   * Map with id and web flow output object
   */
  private Map<Long, CompliReviewOutputData> compliReviewOutput = new HashMap<>();

  /**
   * Key - hex file index Value - pidc name
   */
  private Map<Long, String> pidcElementNameMap = new HashMap<>();

  /**
   * Key - pidc element id - pidc version/pidc variant id Value - Set of AttributeValueModel containing attribute
   * dependencies
   */
  private Map<Long, Set<AttributeValueModel>> attrValueModMap = new HashMap<>();

  /**
   * Key - param name, value - function name
   */
  private Map<String, String> paramFunctionMap = new HashMap<>();

  private String outputZipFileName;
  /**
   * execution ID
   */
  private String executionID;


  /**
   * @return the executionID
   */
  public String getExecutionID() {
    return this.executionID;
  }


  /**
   * @param executionID the executionID to set
   */
  public void setExecutionID(final String executionID) {
    this.executionID = executionID;
  }


  /**
   * @return the destFilePath
   */
  public String getDestFilePath() {
    return this.destFilePath;
  }


  /**
   * @return the compliReviewInputMetaData
   */
  public CompliReviewInputMetaData getCompliReviewInputMetaData() {
    return this.compliReviewInputMetaData;
  }


  /**
   * @return the compliReviewOutput
   */
  public Map<Long, CompliReviewOutputData> getCompliReviewOutput() {
    return this.compliReviewOutput;
  }


  /**
   * @return the pidcElementNameMap
   */
  public Map<Long, String> getPidcElementNameMap() {
    return this.pidcElementNameMap;
  }


  /**
   * @param destFilePath the destFilePath to set
   */
  public void setDestFilePath(final String destFilePath) {
    this.destFilePath = destFilePath;
  }


  /**
   * @param compliReviewInputMetaData the compliReviewInputMetaData to set
   */
  public void setCompliReviewInputMetaData(final CompliReviewInputMetaData compliReviewInputMetaData) {
    this.compliReviewInputMetaData = compliReviewInputMetaData;
  }


  /**
   * @param compliReviewOutput the compliReviewOutput to set
   */
  public void setCompliReviewOutput(final Map<Long, CompliReviewOutputData> compliReviewOutput) {
    this.compliReviewOutput = compliReviewOutput;
  }


  /**
   * @param pidcElementNameMap the pidcElementNameMap to set
   */
  public void setPidcElementNameMap(final Map<Long, String> pidcElementNameMap) {
    this.pidcElementNameMap = pidcElementNameMap;
  }


  /**
   * @return the outputZipFileName
   */
  public String getOutputZipFileName() {
    return this.outputZipFileName;
  }


  /**
   * @param outputZipFileName the outputZipFileName to set
   */
  public void setOutputZipFileName(final String outputZipFileName) {
    this.outputZipFileName = outputZipFileName;
  }


  /**
   * @return the attrValueModMap
   */
  public Map<Long, Set<AttributeValueModel>> getAttrValueModMap() {
    return this.attrValueModMap;
  }


  /**
   * @param attrValueModMap the attrValueModMap to set
   */
  public void setAttrValueModMap(final Map<Long, Set<AttributeValueModel>> attrValueModMap) {
    this.attrValueModMap = attrValueModMap;
  }


  /**
   * @return the paramFunctionMap
   */
  public Map<String, String> getParamFunctionMap() {
    return this.paramFunctionMap;
  }


  /**
   * @param paramFunctionMap the paramFunctionMap to set
   */
  public void setParamFunctionMap(final Map<String, String> paramFunctionMap) {
    this.paramFunctionMap = paramFunctionMap;
  }


}
