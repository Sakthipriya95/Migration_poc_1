/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.ComPkgBcModel;

/**
 * @author gge6cob
 */
public class CompliReviewData {

  /**
   * Map holding parsed caldata objects
   */
  private Map<String, CalData> calDataMap;

  /**
   * Map with key - label name, value - list of warnings
   */
  private final Map<String, List<String>> parserWarningsMap;

  /**
   * file path for SSD file with compliance parameters
   */
  private String compliSSDFilePath;

  private List<String> labelList = new ArrayList<>();

  /**
   * List of ComPkgBcModel to be sent to SSD
   */
  private Set<ComPkgBcModel> comVarList = new HashSet<>();


  /**
   * Selected a2l file for review
   */
  private A2LFile selA2LFile;

  /**
   * Selected PID card of the a2l file slected
   */
  private PidcVersion selPidcVer;

  /**
   * Selected PIDC variant from the pid card
   */
  private PidcVariant selPIDCVariant;

  /**
   * a2l Char map
   */
  private Map<String, Characteristic> a2lCharMap;

  /**
   * compli param set with no rules
   */
  private Set<String> paramsWithNoRules;
  /**
   * qssd param set with no rules
   */
  private Set<String> qssdParamsWithNoRules;

  private SortedSet<A2LBaseComponents> bcInfo;

  /**
   * check SSD result map for compliance params
   */
  private Map<String, CheckSSDResultParam> checkSSDCompliParamMap;

  /**
   * SSD rules for compliance params
   */
  private Map<String, List<CDRRule>> ssdRulesForCompliance;

  /**
   * SSD rules for qssd params
   */
  private Map<String, List<CDRRule>> ssdRulesForQssd;

  private Set<String> compliCheckSSDOutputFiles;


  private String ssdErrorPath;

  /**
   * Attribute value model
   */
  private Set<AttributeValueModel> attrValueModSet;

  private final Set<AttributeValueModel> attrValModelSetInclSubVar = new HashSet<>();

  private boolean errorinSSDFile;


  private String releaseErrorString;

  private Set<String> skippedParamsList;

  private final Map<String, List<CDRRule>> ssdRulesforCompliIgnore = new HashMap<>();
  private final Map<String, List<CDRRule>> ssdRulesforQssdIgnore = new HashMap<>();

  /**
   * Instantiates a new compli review data.
   *
   * @param selPidcVer the sel pidc ver
   * @param selPIDCVariant the sel PIDC variant
   * @param a2lCharMap the a2l char map
   * @param calDataMap the caldata map
   * @param parserWarningsMap warnings map to be sent to checkssd
   * @param bcInfo the bc info
   */
  public CompliReviewData(final PidcVersion selPidcVer, final PidcVariant selPIDCVariant,
      final Map<String, Characteristic> a2lCharMap, final Map<String, CalData> calDataMap,
      final Map<String, List<String>> parserWarningsMap, final SortedSet<A2LBaseComponents> bcInfo) {
    this.bcInfo = bcInfo;
    this.selPidcVer = selPidcVer;
    this.selPIDCVariant = selPIDCVariant;
    this.a2lCharMap = a2lCharMap;
    this.calDataMap = calDataMap;
    this.parserWarningsMap = parserWarningsMap;
  }


  /**
   * @return the checkSSDCompliParamMap
   */
  public Map<String, CheckSSDResultParam> getCheckSSDCompliParamMap() {
    return this.checkSSDCompliParamMap;
  }


  /**
   * @param checkSSDCompliParamMap the checkSSDCompliParamMap to set
   */
  public void setCheckSSDCompliParamMap(final Map<String, CheckSSDResultParam> checkSSDCompliParamMap) {
    this.checkSSDCompliParamMap = checkSSDCompliParamMap;
  }


  /**
   * @return the bcInfo
   */
  public SortedSet<A2LBaseComponents> getBcInfo() {
    return this.bcInfo;
  }


  /**
   * @param bcInfo the bcInfo to set
   */
  public void setBcInfo(final SortedSet<A2LBaseComponents> bcInfo) {
    this.bcInfo = bcInfo;
  }

  /**
   * @return the paramsWithNoRules
   */
  public Set<String> getParamsWithNoRules() {
    return this.paramsWithNoRules;
  }


  /**
   * @param paramsWithNoRules the paramsWithNoRules to set
   */
  public void setParamsWithNoRules(final Set<String> paramsWithNoRules) {
    this.paramsWithNoRules = paramsWithNoRules;
  }


  /**
   * @return the qssdParamsWithNoRules
   */
  public Set<String> getQssdParamsWithNoRules() {
    return this.qssdParamsWithNoRules;
  }


  /**
   * @param qssdParamsWithNoRules the qssdParamsWithNoRules to set
   */
  public void setQssdParamsWithNoRules(final Set<String> qssdParamsWithNoRules) {
    this.qssdParamsWithNoRules = qssdParamsWithNoRules;
  }


  /**
   * @return the ssdRulesForCompliance
   */
  public Map<String, List<CDRRule>> getSsdRulesForCompliance() {
    return this.ssdRulesForCompliance;
  }


  /**
   * @param ssdRulesForCompliance the ssdRulesForCompliance to set
   */
  public void setSsdRulesForCompliance(final Map<String, List<CDRRule>> ssdRulesForCompliance) {
    this.ssdRulesForCompliance = ssdRulesForCompliance;
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
   * @return the labelList
   */
  public List<String> getLabelList() {
    return this.labelList;
  }


  /**
   * @param labelList the labelList to set
   */
  public void setLabelList(final List<String> labelList) {
    this.labelList = labelList;
  }


  /**
   * @return the comVarList
   */
  public Set<ComPkgBcModel> getComVarList() {
    return this.comVarList;
  }


  /**
   * @param comVarList the comVarList to set
   */
  public void setComVarList(final Set<ComPkgBcModel> comVarList) {
    this.comVarList = comVarList;
  }


  /**
   * @return the compliSSDFilePath
   */
  public String getCompliSSDFilePath() {
    return this.compliSSDFilePath;
  }


  /**
   * @param compliSSDFilePath the compliSSDFilePath to set
   */
  public void setCompliSSDFilePath(final String compliSSDFilePath) {
    this.compliSSDFilePath = compliSSDFilePath;
  }


  /**
   * @return the selA2LFile
   */
  public A2LFile getSelA2LFile() {
    return this.selA2LFile;
  }


  /**
   * @param selA2LFile the selA2LFile to set
   */
  public void setSelA2LFile(final A2LFile selA2LFile) {
    this.selA2LFile = selA2LFile;
  }


  /**
   * @return the selPidcVer
   */
  public PidcVersion getSelPidcVer() {
    return this.selPidcVer;
  }


  /**
   * @param selPidcVer the selPidcVer to set
   */
  public void setSelPidcVer(final PidcVersion selPidcVer) {
    this.selPidcVer = selPidcVer;
  }


  /**
   * @return the selPIDCVariant
   */
  public PidcVariant getSelPIDCVariant() {
    return this.selPIDCVariant;
  }


  /**
   * @param selPIDCVariant the selPIDCVariant to set
   */
  public void setSelPIDCVariant(final PidcVariant selPIDCVariant) {
    this.selPIDCVariant = selPIDCVariant;
  }


  /**
   * @return the a2lCharMap
   */
  public Map<String, Characteristic> getA2lCharMap() {
    return this.a2lCharMap;
  }


  /**
   * @param a2lCharMap the a2lCharMap to set
   */
  public void setA2lCharMap(final Map<String, Characteristic> a2lCharMap) {
    this.a2lCharMap = a2lCharMap;
  }


  /**
   * @return the compliCheckSSDOutputFiles
   */
  public Set<String> getCompliCheckSSDOutputFiles() {
    return this.compliCheckSSDOutputFiles;
  }


  /**
   * @param compliCheckSSDOutputFiles the compliCheckSSDOutputFiles to set
   */
  public void setCompliCheckSSDOutputFiles(final Set<String> compliCheckSSDOutputFiles) {
    this.compliCheckSSDOutputFiles = compliCheckSSDOutputFiles;
  }


  /**
   * @return the attrValueModSet
   */
  public Set<AttributeValueModel> getAttrValueModSet() {
    return this.attrValueModSet;
  }


  /**
   * @param attrValueModSet the attrValueModSet to set
   */
  public void setAttrValueModSet(final Set<AttributeValueModel> attrValueModSet) {
    this.attrValueModSet = attrValueModSet;
  }


  /**
   * @return the ssdErrorPath
   */
  public String getSsdErrorPath() {
    return this.ssdErrorPath;
  }


  /**
   * @param ssdErrorPath the ssdErrorPath to set
   */
  public void setSsdErrorPath(final String ssdErrorPath) {
    this.ssdErrorPath = ssdErrorPath;
  }


  /**
   * @param errorinSSDFile errorinSSDFile
   */
  public void setErrorInSSDfile(final boolean errorinSSDFile) {
    this.errorinSSDFile = errorinSSDFile;

  }

  /**
   * @return the errorinSSDFile
   */
  public boolean isErrorinSSDFile() {
    return this.errorinSSDFile;
  }


  /**
   * @return the releaseErrorString
   */
  public String getReleaseErrorString() {
    return this.releaseErrorString;
  }


  /**
   * @param releaseErrorString the releaseErrorString to set
   */
  public void setReleaseErrorString(final String releaseErrorString) {
    this.releaseErrorString = releaseErrorString;
  }

  /**
   * @return the skippedParamsList
   */
  public Set<String> getSkippedParamsList() {
    return this.skippedParamsList;
  }


  /**
   * @param skippedParamsList the skippedParamsList to set
   */
  public void setSkippedParamsList(final Set<String> skippedParamsList) {
    this.skippedParamsList = skippedParamsList;
  }


  /**
   * @return map of cdr rules
   */
  public Map<String, List<CDRRule>> getSsdRulesForComplianceCaseIgnore() {
    return this.ssdRulesforCompliIgnore;
  }


  /**
   * @return the ssdRulesForQssd
   */
  public Map<String, List<CDRRule>> getSsdRulesForQssd() {
    return this.ssdRulesForQssd;
  }


  /**
   * @param ssdRulesForQssd the ssdRulesForQssd to set
   */
  public void setSsdRulesForQssd(final Map<String, List<CDRRule>> ssdRulesForQssd) {
    this.ssdRulesForQssd = ssdRulesForQssd;
  }


  /**
   * @return the ssdRulesforQssdIgnore
   */
  public Map<String, List<CDRRule>> getSsdRulesforQssdIgnore() {
    return this.ssdRulesforQssdIgnore;
  }


  /**
   * @return the attrValModelSetInclSubVar
   */
  public Set<AttributeValueModel> getAttrValModelSetInclSubVar() {
    return new HashSet<>(this.attrValModelSetInclSubVar);
  }


  /**
   * @param valModel value model set
   */
  public void addToAttrValModelSetInclSubVar(final Set<AttributeValueModel> valModel) {
    this.attrValModelSetInclSubVar.addAll(valModel);
  }


  /**
   * @return the warningsMap
   */
  public Map<String, List<String>> parserWarningsMap() {
    return this.parserWarningsMap;
  }


}
