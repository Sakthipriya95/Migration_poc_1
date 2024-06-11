/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.model.cdr.ExcelReportTypeEnum;

/**
 * @author rgo7cob
 */
public class CompliCheckSSDInput {


  private String hexFileName;

  private Map<String, CalData> calDataMap;

  private ExcelReportTypeEnum dataFileOption;

  private boolean predecessorCheck;
  /**
   * Key - label name, value - list of warnings
   */
  private Map<String, List<String>> parserWarningsMap;

  private A2LFileInfo a2lFileContents;

  private String ssdFilePath;

  private boolean isPrimaryRule;

  private String serverOrTempPath;

  private Set<String> paramSet;

  private Long pidcA2l;

  private Long variantId;

  private Map<Long, Long> hexFilePidcElement = new TreeMap<>();


  /**
   * @return the hexFilePidcElement
   */
  public Map<Long, Long> getHexFilePidcElement() {
    return this.hexFilePidcElement;
  }


  /**
   * @param hexFilePidcElement the hexFilePidcElement to set
   */
  public void setHexFilePidcElement(final Map<Long, Long> hexFilePidcElement) {
    this.hexFilePidcElement = hexFilePidcElement;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the pidcA2l
   */
  public Long getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final Long pidcA2l) {
    this.pidcA2l = pidcA2l;
  }


  /**
   * @return the parserWarningsMap
   */
  public Map<String, List<String>> getParserWarningsMap() {
    return this.parserWarningsMap;
  }


  /**
   * @param parserWarningsMap the parserWarningsMap to set
   */
  public void setParserWarningsMap(final Map<String, List<String>> parserWarningsMap) {
    this.parserWarningsMap = parserWarningsMap;
  }


  /**
   * @return the hexFileName
   */
  public String getHexFileName() {
    return this.hexFileName;
  }


  /**
   * @param hexFileName the hexFileName to set
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }


  /**
   * @return the calDataMap
   */
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }


  /**
   * @param calDataMap the calDataMap to set
   */
  public void setCalDataMap(final Map<String, CalData> calDataMap) {
    this.calDataMap = calDataMap;
  }


  /**
   * @return the a2lFileContents
   */
  public A2LFileInfo getA2lFileContents() {
    return this.a2lFileContents;
  }


  /**
   * @param a2lFileContents the a2lFileContents to set
   */
  public void setA2lFileContents(final A2LFileInfo a2lFileContents) {
    this.a2lFileContents = a2lFileContents;
  }


  /**
   * @return the ssdFilePath
   */
  public String getSsdFilePath() {
    return this.ssdFilePath;
  }


  /**
   * @param ssdFilePath the ssdFilePath to set
   */
  public void setSsdFilePath(final String ssdFilePath) {
    this.ssdFilePath = ssdFilePath;
  }


  /**
   * @return the isPrimaryRule
   */
  public boolean isPrimaryRule() {
    return this.isPrimaryRule;
  }


  /**
   * @param isPrimaryRule the isPrimaryRule to set
   */
  public void setPrimaryRule(final boolean isPrimaryRule) {
    this.isPrimaryRule = isPrimaryRule;
  }


  /**
   * @return the serverOrTempPath
   */
  public String getServerOrTempPath() {
    return this.serverOrTempPath;
  }


  /**
   * @param serverOrTempPath the serverOrTempPath to set
   */
  public void setServerOrTempPath(final String serverOrTempPath) {
    this.serverOrTempPath = serverOrTempPath;
  }


  /**
   * @return the paramSet
   */
  public Set<String> getParamSet() {
    return this.paramSet;
  }


  /**
   * @param paramSet the paramSet to set
   */
  public void setParamSet(final Set<String> paramSet) {
    this.paramSet = paramSet;
  }

  /**
   * @return the predecessorCheck
   */
  public boolean isPredecessorCheck() {
    return this.predecessorCheck;
  }


  /**
   * @param predecessorCheck the predecessorCheck to set
   */
  public void setPredecessorCheck(final boolean predecessorCheck) {
    this.predecessorCheck = predecessorCheck;
  }


  /**
   * @return the dataFileOption
   */
  public ExcelReportTypeEnum getDataFileOption() {
    return this.dataFileOption;
  }


  /**
   * @param dataFileOption the dataFileOption to set
   */
  public void setDataFileOption(final ExcelReportTypeEnum dataFileOption) {
    this.dataFileOption = dataFileOption;
  }


}
