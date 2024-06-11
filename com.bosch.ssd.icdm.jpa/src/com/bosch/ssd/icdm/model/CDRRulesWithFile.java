/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author apl1cob model to hole list of CDR rules along with ssd file location
 */
public class CDRRulesWithFile {

  private Map<String, List<CDRRule>> cdrRules;

  private String ssdFilePath;

  private List<String> validRuleParameters;
  /**
   * New SSDMessage - to send status to iCDM when both values are null or either is null or both has values (rules &
   * SSDFile)
   */
  private SSDMessage ssdMessage;

  /**
   * @return the cdrRules
   */
  public Map<String, List<CDRRule>> getCdrRules() {
    return this.cdrRules;
  }

  /**
   * @param cdrRules the cdrRules to set
   */
  public void setCdrRules(final Map<String, List<CDRRule>> cdrRules) {
    this.cdrRules = cdrRules;
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
   * @return the ssdMessage
   */
  public SSDMessage getSsdMessage() {
    return this.ssdMessage;
  }

  /**
   * @param ssdMessage the ssdMessage to set
   */
  public void setSsdMessage(final SSDMessage ssdMessage) {
    this.ssdMessage = ssdMessage;
  }

  /**
   * @return the validRuleParameters
   */
  public List<String> getValidRuleParameters() {
    return new ArrayList<>(this.validRuleParameters);
  }

  /**
   * @param validRuleParameters the validRuleParameters to set
   */
  public void setValidRuleParameters(final List<String> validRuleParameters) {
    this.validRuleParameters = new ArrayList<>(validRuleParameters);
  }

}
