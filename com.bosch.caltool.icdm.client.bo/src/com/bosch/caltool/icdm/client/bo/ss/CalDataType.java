/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.ss;


/**
 * Icdm-707 More than one param with same in Scracth pad
 * @author rgo7cob Enumeration for Cal data Types (Moved from Series Stat Info).
 */
public enum CalDataType {

  /**
   * Defines CalDataPhy Min Value
   */
  MIN("Min Value"),
  /**
   * Defines CalDataPhy Max Value
   */
  MAX("Max Value"),
  /**
   * Defines CalDataPhy Peak Value
   */
  PEAK("Most Frequent Value"),
  /**
   * Defines CalDataPhy Average Value
   */
  AVERAGE("Average Value"),
  /**
   * Defines CalDataPhy Median Value
   */
  MEDIAN("Median Value"),
  /**
   * Defines CalDataPhy Lower Quartile Value
   */
  LOWER_QUARTILE("Lower Quartile Value"),
  /**
   * Defines CalDataPhy Upper Quartile Value
   */
  UPPER_QUARTILE("Upper Quartile Value"),
  /**
   * Defines CalDataPhy Value from series statistics
   */
  VALUE("Statistics Value"),
  /**
   * Defines CalDataPhy Value from DST/CDFX
   */
  DSTVALUE("DST Value"),
  /**
   * Defines Reference Value
   */
  REF_VALUE("CDR Reference Value"),
  /**
   * Defines Check Value
   */
  CHECK_VALUE("Check Value"),
  // ICDM-1125
  /**
   * Defines loading of CDFX file
   */
  IMPORT_CDFX("Loaded from CDFX"),
  /**
   * Defines loading of DCM file
   */
  IMPORT_DCM("Loaded from DCM"),
  /**
   * Defines loading of PaCo file
   */
  IMPORT_PACO("Loaded from PaCo");

  /**
   * Label of this type
   */
  private String label;

  /**
   * @param label
   */
  CalDataType(final String label) {
    this.label = label;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return this.label;
  }

}