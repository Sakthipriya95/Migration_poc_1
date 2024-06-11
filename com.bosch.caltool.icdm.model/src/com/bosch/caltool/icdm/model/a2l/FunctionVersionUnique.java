/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Objects;
import java.util.regex.Pattern;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author rgo7cob
 */
public class FunctionVersionUnique implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = -7359791607194154255L;

  /**
   * Version seperator .
   */
  private static final String VER_SEP_DOT = ".";

  /**
   * Version seperator _
   */
  private static final String VER_SEP_UNDERSCORE = "_";


  private Long id;


  private String funcName;


  private String funcVersion;

  private String funcNameUpper;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;

  }


  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
  }


  /**
   * @return the funcVersion
   */
  public String getFuncVersion() {
    return this.funcVersion;
  }


  /**
   * @param funcVersion the funcVersion to set
   */
  public void setFuncVersion(final String funcVersion) {
    this.funcVersion = funcVersion;
  }


  /**
   * @return the funcNameUpper
   */
  public String getFuncNameUpper() {
    return this.funcNameUpper;
  }


  /**
   * @param funcNameUpper the funcNameUpper to set
   */
  public void setFuncNameUpper(final String funcNameUpper) {
    this.funcNameUpper = funcNameUpper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // TODO Auto-generated method stub

  }

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
    FunctionVersionUnique other = (FunctionVersionUnique) obj;
    int maxLen = getFuncVersion().length() > other.getFuncVersion().length() ? getFuncVersion().length()
        : other.getFuncVersion().length();
    // Normalize the strings before comparison
    String thisVersion = getNormalisedVersion(getFuncVersion(), maxLen);
    String otherVersion = getNormalisedVersion(other.getFuncVersion(), maxLen);
    return Objects.equals(thisVersion, otherVersion);
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((this.funcVersion == null) ? 0 : this.funcVersion.hashCode());
    return result;
  }
}
