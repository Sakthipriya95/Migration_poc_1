/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;


import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author NIP4COB
 */

public class ScratchPadDataFetcher {

  /**
   * PidcA2l instance
   */
  private PidcA2l a2lFile;

  /**
   * Functioninstance
   */
  private Function function;

  /**
   * PIDCard instance
   */
  private PidcVersion pidcVersion;

  /**
   * PIDCVariant instance
   */
  private PidcVariant pidcVariant;

  /**
   * PIDCSubVariant instance
   */
  private PidcSubVariant pidcSubVariant;
  /**
   * SeriesStatisticsInfo instance
   */
  private SeriesStatisticsInfo seriesStatsInfo;

  /**
   * AttributeValue instance
   */
  private AttributeValue attrVal;


  private Parameter parameter;

  /**
   * @return the a2lFile
   */
  public PidcA2l getPidcA2l() {
    return this.a2lFile;
  }


  /**
   * @param a2lFile the a2lFile to set
   */
  public void setPidcA2l(final PidcA2l a2lFile) {
    this.a2lFile = a2lFile;
  }


  /**
   * @return the function
   */
  public Function getFunction() {
    return this.function;
  }


  /**
   * @param function the function to set
   */
  public void setFunction(final Function function) {
    this.function = function;
  }


  /**
   * @return the pidcVer
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVer the pidcVer to set
   */
  public void setPidcVersion(final PidcVersion pidcVer) {
    this.pidcVersion = pidcVer;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @return the pidcSubVariant
   */
  public PidcSubVariant getPidcSubVariant() {
    return this.pidcSubVariant;
  }


  /**
   * @param pidcSubVariant the pidcSubVariant to set
   */
  public void setPidcSubVariant(final PidcSubVariant pidcSubVariant) {
    this.pidcSubVariant = pidcSubVariant;
  }


  /**
   * @return the seriesStatsInfo
   */
  public SeriesStatisticsInfo getSeriesStatsInfo() {
    return this.seriesStatsInfo;
  }


  /**
   * @param seriesStatsInfo the seriesStatsInfo to set
   */
  public void setSeriesStatsInfo(final SeriesStatisticsInfo seriesStatsInfo) {
    this.seriesStatsInfo = seriesStatsInfo;
  }


  /**
   * @return the attrVal
   */
  public AttributeValue getAttrVal() {
    return this.attrVal;
  }


  /**
   * @param attrVal the attrVal to set
   */
  public void setAttrVal(final AttributeValue attrVal) {
    this.attrVal = attrVal;
  }


  /**
   * @return the Parameter
   */
  public Parameter getParameter() {
    return this.parameter;
  }


  /**
   * @param parameter
   */
  public void setParameter(final Parameter parameter) {
    this.parameter = parameter;
  }


}
