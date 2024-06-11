/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * CDRFuncVersion.java, This class is the business object of the Calibration data review FUNCTION VERSION
 *
 * @author adn1cob
 */
// iCDM-471
public class CDRFuncVersion implements Comparable<CDRFuncVersion> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   * CDR Function
   */
  private final CDRFunction cdrFunction;

  /**
   * Defines function version (1.1.2, 2.3.0. etc)
   */
  private final String version;

  /**
   * All PARAMETERS of this function version
   */
  private Map<String, CDRFuncParameter> allParamsMap;

  /**
   * Version seperator .
   */
  private static final String VER_SEP_DOT = ".";

  /**
   * Version seperator _
   */
  private static final String VER_SEP_UNDERSCORE = "_";


  /**
   * This Constructor is protected and triggered from DataCache/DataLoader only
   *
   * @param cdrFunction function obj
   * @param funcVer function version
   */
  protected CDRFuncVersion(final CDRFunction cdrFunction, final String funcVer) {
    this.cdrFunction = cdrFunction;
    this.version = funcVer;
  }

  /**
   * Get all PARAMETER object for this function version
   *
   * @return set of parameter objects of this function version
   * @throws SsdInterfaceException
   */
  // iCDM-605
  public Map<String, CDRFuncParameter> getParameters() throws SsdInterfaceException {
    if (this.allParamsMap == null) {
      // iCDM-605
      // Filter variant coded params
      this.allParamsMap = this.cdrFunction
          .filterVariantCodedParams(getDataProvider().getDataLoader().fetchParameters(getFunctionName(), getVersion()));
    }
    return this.allParamsMap;
  }

  // iCDM-845
  /**
   * Get all PARAMETERS for the function variant. (pass 'false' to search by ALTERNATIVE)
   *
   * @param byVariant true to filter by variant, false to filter by alternative
   * @return collection of CDRFuncParamater
   * @throws SsdInterfaceException
   */
  public Map<String, CDRFuncParameter> getParameters(final boolean byVariant) throws SsdInterfaceException {
    return this.cdrFunction.filterVariantCodedParams(
        getDataProvider().getDataLoader().fetchParameters(getFunctionName(), getVersion(), byVariant));
  }

  /**
   * Get Sorted set of CDRFuncParameters
   *
   * @return CDRFuncParameters set
   */
  public SortedSet<CDRFuncParameter> getSortedParameters() {
    SortedSet<CDRFuncParameter> sortedParams = new TreeSet<CDRFuncParameter>();
    if (this.allParamsMap != null) {
      sortedParams.addAll(this.allParamsMap.values());
    }
    return sortedParams;
  }

  /**
   * @return the name
   */
  public String getFunctionName() {
    return this.cdrFunction.getName();
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return this.version;
  }

  /**
   * Get data provider
   *
   * @return Apic data provider
   */
  protected CDRDataProvider getDataProvider() {
    return this.cdrFunction.getDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRFuncVersion other) {
    int maxLen =
        getVersion().length() > other.getVersion().length() ? getVersion().length() : other.getVersion().length();
    // Normalize the strings before comparison
    String thisVersion = getNormalisedVersion(getVersion(), maxLen);
    String otherVersion = getNormalisedVersion(other.getVersion(), maxLen);
    return ApicUtil.compare(thisVersion, otherVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((this.version == null) ? 0 : this.version.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CDRFuncVersion other = (CDRFuncVersion) obj;
    int maxLen =
        getVersion().length() > other.getVersion().length() ? getVersion().length() : other.getVersion().length();
    // Normalize the strings before comparison
    String thisVersion = getNormalisedVersion(getVersion(), maxLen);
    String otherVersion = getNormalisedVersion(other.getVersion(), maxLen);
    return CommonUtils.isEqual(thisVersion, otherVersion);
  }

  /**
   * Normalize the string to enable version comparison
   *
   * @param ver version
   * @param maxLen length
   * @return normalized string
   */
  private String getNormalisedVersion(final String ver, final int maxLen) {
    StringBuilder nVersion = new StringBuilder();
    return getNormalizedVersion(ver, VER_SEP_DOT, maxLen, nVersion);
  }

  /**
   * Normalize the version with delimiter as '.'
   *
   * @param ver version
   * @param sep seperator as '.'
   * @param maxWidth width for formatting
   * @param nVersion
   * @return normalized string
   */
  private String getNormalizedVersion(final String ver, final String sep, final int maxWidth,
      final StringBuilder nVersion) {

    String[] split = Pattern.compile(sep, Pattern.LITERAL).split(ver);
    for (String str : split) {
      // Consider handling underscore character also
      if (str.contains(VER_SEP_UNDERSCORE)) {
        nVersion.append(getNormalizedVersion(str, VER_SEP_UNDERSCORE, maxWidth, nVersion));
      }
      String format = "%" + maxWidth + 's';
      nVersion.append(String.format(format, str));
    }
    return nVersion.toString();
  }

}
