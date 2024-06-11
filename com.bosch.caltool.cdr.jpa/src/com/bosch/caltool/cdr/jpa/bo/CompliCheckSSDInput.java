/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Map;
import java.util.Set;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;

/**
 * @author rgo7cob
 */
public class CompliCheckSSDInput {


  private String hexFileName;

  private Map<String, CalData> calDataMap;

  private A2LFileInfo a2lFileContents;

  private String ssdFilePath;

  private boolean isPrimaryRule;

  private String serverOrTempPath;

  private Set<String> paramSet;


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


}
