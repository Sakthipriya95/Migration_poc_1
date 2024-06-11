/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.ss;

import com.bosch.calmodel.caldata.CalData;


/**
 * @author mga1cob
 */
// ICDM-226
public class SeriesStatisticsInfo {

  /**
   * CalData instance to add scratch pad
   */
  private final CalData calData;

  /**
   * Defines CalDataPhyValueType such as Min|Max|Peak value
   */
  private final CalDataType calDataPhyValType;

  /**
   * ICDM-935 Parameter class name
   */
  private String className = "";

  /**
   * Display name of this object
   */
  private String dataSetName = "";


  /**
   * @param calData instance
   * @param calDataPhyValType instance
   */
  public SeriesStatisticsInfo(final CalData calData, final CalDataType calDataPhyValType) {
    this.calData = calData;
    this.calDataPhyValType = calDataPhyValType;
  }

  /**
   * @return the dataSetName
   */
  public String getDataSetName() {
    return this.dataSetName;
  }

  /**
   * @return the calData
   */
  public CalData getCalData() {
    return this.calData;
  }

  /**
   * @return the calDataPhyValType
   */
  public CalDataType getCalDataPhyValType() {
    return this.calDataPhyValType;
  }

  /**
   * @return the className
   */
  public String getClassName() {
    return this.className;
  }


  /**
   * @param className the className to set
   */
  public void setClassName(final String className) {
    this.className = className;
  }


  /**
   * Shows the Additional info like Data #Set name , Attr Dependencies, A2l File name
   * 
   * @param dataSetName dataSetName gives the information of Data Set
   */
  public void setDataSetName(final String dataSetName) {
    this.dataSetName = dataSetName;
  }
}
