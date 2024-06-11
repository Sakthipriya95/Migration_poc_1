/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.cdfx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.cdr.CdrReport;

/**
 * @author pdh2cob
 */
public class CDFxDeliveryWrapper {

  /**
   * Input model
   */
  private CdfxExportInput input = new CdfxExportInput();

  /**
   * json output will be constructed based on the model
   */
  private CdfxExportOutput output = new CdfxExportOutput();

  private Map<String, WpRespLabelResponse> relevantWpRespLabelMap = new HashMap<>();

  private CdrReport cdrReport;

  private Long cdfxDeliveryId;

  private String outputZipFileName;

  private String serverTempDir;

  private final Set<String> wpRespParamSet = new HashSet<>();

  private final Map<Integer, Set<String>> paramReviewStatusMap = new HashMap<>();

  private String serviceDirZipFilePath;

  private String fileWritingTime;

  /**
   * Key: VariantId & Value: ParamName, WpRespLabelResponse
   */
  private Map<Long, Map<String, WpRespLabelResponse>> variantWpRespLabelRespMap = new HashMap<>();

  /**
   * Key: VariantId & Value: CdrReport
   */
  private Map<Long, CdrReport> cdrReportMap = new HashMap<>();


  /**
   * @return the input
   */
  public CdfxExportInput getInput() {
    return this.input;
  }


  /**
   * @param input the input to set
   */
  public void setInput(final CdfxExportInput input) {
    this.input = input;
  }


  /**
   * @return the output
   */
  public CdfxExportOutput getOutput() {
    return this.output;
  }


  /**
   * @param output the output to set
   */
  public void setOutput(final CdfxExportOutput output) {
    this.output = output;
  }


  /**
   * @return the relevantWpRespLabelMap
   */
  public Map<String, WpRespLabelResponse> getRelevantWpRespLabelMap() {
    return this.relevantWpRespLabelMap;
  }


  /**
   * @param relevantWpRespLabelMap the relevantWpRespLabelMap to set
   */
  public void setRelevantWpRespLabelMap(final Map<String, WpRespLabelResponse> relevantWpRespLabelMap) {
    this.relevantWpRespLabelMap = relevantWpRespLabelMap;
  }


  /**
   * @return the cdrReport
   */
  public CdrReport getCdrReport() {
    return this.cdrReport;
  }


  /**
   * @param cdrReport the cdrReport to set
   */
  public void setCdrReport(final CdrReport cdrReport) {
    this.cdrReport = cdrReport;
  }


  /**
   * @return the cdfxDeliveryId
   */
  public Long getCdfxDeliveryId() {
    return this.cdfxDeliveryId;
  }


  /**
   * @param cdfxDeliveryId the cdfxDeliveryId to set
   */
  public void setCdfxDeliveryId(final Long cdfxDeliveryId) {
    this.cdfxDeliveryId = cdfxDeliveryId;
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
   * @return the serverTempDir
   */
  public String getServerTempDir() {
    return this.serverTempDir;
  }


  /**
   * @param serverTempDir the serverTempDir to set
   */
  public void setServerTempDir(final String serverTempDir) {
    this.serverTempDir = serverTempDir;
  }

  /**
   * @return the wpRespParamSet
   */
  public Set<String> getWpRespParamSet() {
    return this.wpRespParamSet;
  }


  /**
   * @return the paramReviewStatusMap
   */
  public Map<Integer, Set<String>> getParamReviewStatusMap() {
    return this.paramReviewStatusMap;
  }


  /**
   * @return the serviceDirZipFilePath
   */
  public String getServiceDirZipFilePath() {
    return this.serviceDirZipFilePath;
  }


  /**
   * @param serviceDirZipFilePath the serviceDirZipFilePath to set
   */
  public void setServiceDirZipFilePath(final String serviceDirZipFilePath) {
    this.serviceDirZipFilePath = serviceDirZipFilePath;
  }


  /**
   * @return the fileWritingTime
   */
  public String getFileCreationDate() {
    return this.fileWritingTime;
  }


  /**
   * @param fileWritingTime the fileWritingTime to set
   */
  public void setFileCreationDate(final String fileWritingTime) {
    this.fileWritingTime = fileWritingTime;
  }

  /**
   * @return the variantWpRespLabelRespMap
   */
  public Map<Long, Map<String, WpRespLabelResponse>> getVariantWpRespLabelRespMap() {
    return this.variantWpRespLabelRespMap;
  }


  /**
   * @param variantWpRespLabelRespMap the variantWpRespLabelRespMap to set
   */
  public void setVariantWpRespLabelRespMap(
      final Map<Long, Map<String, WpRespLabelResponse>> variantWpRespLabelRespMap) {
    this.variantWpRespLabelRespMap = variantWpRespLabelRespMap;
  }

  /**
   * @return the cdrReportMap
   */
  public Map<Long, CdrReport> getCdrReportMap() {
    return this.cdrReportMap;
  }


  /**
   * @param cdrReportMap the cdrReportMap to set
   */
  public void setCdrReportMap(final Map<Long, CdrReport> cdrReportMap) {
    this.cdrReportMap = cdrReportMap;
  }

}