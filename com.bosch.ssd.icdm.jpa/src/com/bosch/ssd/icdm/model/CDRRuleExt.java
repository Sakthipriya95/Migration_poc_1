/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

/**
 * This model contains all the additional information of a specified rule
 *
 * @author SSN9COB
 */
public class CDRRuleExt extends CDRRule {

  /**
   * Contact - Rule Owner
   */
  private String ruleOwner;
  /**
   * Contact - CoC
   */
  private String coc;
  /**
   * Internal Adaptation Description
   */
  private String internalAdaptationDescription;
  /**
   * Data Description
   */
  private String dataDescription;
  /**
   * Historie Description
   */
  private String historieDescription;


  /**
   * @return the ruleOwner
   */
  public String getRuleOwner() {
    return this.ruleOwner;
  }


  /**
   * @param ruleOwner the ruleOwner to set
   */
  public void setRuleOwner(final String ruleOwner) {
    this.ruleOwner = ruleOwner;
  }


  /**
   * @return the coc
   */
  public String getCoc() {
    return this.coc;
  }


  /**
   * @param coc the coc to set
   */
  public void setCoc(final String coc) {
    this.coc = coc;
  }


  /**
   * @return the internalAdaptationDescription
   */
  public String getInternalAdaptationDescription() {
    return this.internalAdaptationDescription;
  }


  /**
   * @param internalAdaptationDescription the internalAdaptationDescription to set
   */
  public void setInternalAdaptationDescription(final String internalAdaptationDescription) {
    this.internalAdaptationDescription = internalAdaptationDescription;
  }


  /**
   * @return the dataDescription
   */
  public String getDataDescription() {
    return this.dataDescription;
  }


  /**
   * @param dataDescription the dataDescription to set
   */
  public void setDataDescription(final String dataDescription) {
    this.dataDescription = dataDescription;
  }


  /**
   * @return the historieDescription
   */
  public String getHistorieDescription() {
    return this.historieDescription;
  }


  /**
   * @param historieDescription the historieDescription to set
   */
  public void setHistorieDescription(final String historieDescription) {
    this.historieDescription = historieDescription;
  }
}
