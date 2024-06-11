/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.util.regex.Pattern;


/**
 * ICDM-2263
 *
 * @author mkl2cob
 */
public class VersionValidator {

  /**
   * boolean whether to consider the version as minimum version or reference version
   */
  private final boolean considerMinVersComparsion;

  /**
   * base version
   */
  private final String version;

  /**
   * zeroth index of version
   */
  private static final int VERSION_STR_ZEROTH_INDEX = 0;

  /**
   * first index of version
   */
  private static final int VERSION_STR_FIRST_INDEX = 1;

  /**
   * second index of version
   */
  private static final int VERSION_STR_SECOND_INDEX = 2;

  /**
   * weightage value for first index
   */
  private static final int VERSION_FIRST_WEIGHTAGE_VALUE = 100;

  /**
   * weightage value for second index
   */
  private static final int VERSION_SEC_WEIGHTAGE_VALUE = 10000;

  /**
   * Constructor for minimum version comparison
   *
   * @param version String
   * @param considerMinVersComparsion boolean
   */
  public VersionValidator(final String version, final boolean considerMinVersComparsion) {
    this.version = version;
    this.considerMinVersComparsion = considerMinVersComparsion;
  }


  /**
   * @param inputVersion String
   * @return true if the inputVersion is valid
   */
  public boolean validateVersions(final String inputVersion) {

    long factrOfInputVersNumber = getVersionAsNumber(inputVersion);

    long factrOfBaseVersNumber = getVersionAsNumber(this.version);

    if (this.considerMinVersComparsion) {
      return factrOfInputVersNumber >= factrOfBaseVersNumber;
    }
    return factrOfInputVersNumber == factrOfBaseVersNumber;
  }

  /**
   * @param inputVersNumber long
   * @return true if the inputVersionNumber is valid
   */
  public boolean validateVersionFactoredNos(final long inputVersNumber) {
    long factrOfBaseVersNumber = getVersionAsNumber(this.version);
    if (this.considerMinVersComparsion) {
      return inputVersNumber >= factrOfBaseVersNumber;
    }
    return inputVersNumber == factrOfBaseVersNumber;
  }

  /**
   * @param inputVersion inputVersion
   * @return long of factorial value
   */
  public long getVersionAsNumber(final String inputVersion) {
    String[] versionSegments = inputVersion.split(Pattern.quote("."));
    return Long.parseLong(versionSegments[VERSION_STR_SECOND_INDEX]) +
        (Long.parseLong(versionSegments[VERSION_STR_FIRST_INDEX]) * VERSION_FIRST_WEIGHTAGE_VALUE) +
        (Long.parseLong(versionSegments[VERSION_STR_ZEROTH_INDEX]) * VERSION_SEC_WEIGHTAGE_VALUE);
  }


}
