/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.tul;

/**
 * @author TRL1COB
 */
public class TULConstants {

  /**
   * Constant for iCDM Tool
   */
  public static final String ICDM_TOOL = "iCDM";

  /**
   * Enum to hold the list of TUL Components
   */
  public enum ICDM_TUL_COMPONENT {

                             /**
                              * Component - iCDM Client
                              */
                             ICDM_CLIENT("iCDM Client"),

                             /**
                              * Component - iCDM Webservice
                              */
                             ICDM_WEBSERVICE("iCDM Webservice");

    final String name;

    ICDM_TUL_COMPONENT(final String name) {
      this.name = name;
    }

    /**
     * @return tulComponent name
     */
    public final String getName() {
      return this.name;
    }

  }

  /**
   * Enum to hold the list of TUL Feature
   */
  public enum TUL_FEATURE {

                           /**
                            * Feature - Calibration Data Analysis
                            */
                           CALIBRATION_DATA_ANALYSIS("Calibration Data Analysis");

    final String name;

    TUL_FEATURE(final String name) {
      this.name = name;
    }


    /**
     * @return tulfeature name
     */
    public final String getName() {
      return this.name;
    }


  }

}
