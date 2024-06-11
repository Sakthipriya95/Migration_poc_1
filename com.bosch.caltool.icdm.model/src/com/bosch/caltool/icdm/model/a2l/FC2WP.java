package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.util.ModelUtil;

// icdm-276 changes
/**
 * The Class FC2WP.
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class FC2WP implements Comparable<FC2WP> {

  /** The fc wp id. */
  private final Long fcWpId;

  /** The cal contact 1. */
  private final String calContact1;

  /** The cal contact 2. */
  private final String calContact2;

  /** The fc 2 wp type. */
  private final String fc2wpType;

  /** The wp resource. */
  private final String wpResource;

  /** The wp name E. */
  private final String wpName;

  /** The wp number. */
  private final String wpNumber;

  /** The fc name. */
  private final String fcName;

  /** Defines constant for hash code prime. */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * Instantiates a new fc2wp.
   *
   * @param objID id
   * @param calContact1 calContact1
   * @param calContact2 calContact2
   * @param fc2wpType fc2wpType
   * @param wpResource the wp resource
   * @param wpName
   * @param wpNumber wpNumber
   * @param fcName the fc name
   */
  public FC2WP(final Long objID, final String calContact1, final String calContact2, final String fc2wpType,
      final String wpResource, final String wpName, final String wpNumber, final String fcName) {
    this.wpName = wpName;
    this.fcWpId = objID;
    this.calContact1 = calContact1;
    this.calContact2 = calContact2;
    this.fc2wpType = fc2wpType;
    this.wpResource = wpResource;
    this.wpNumber = wpNumber;
    this.fcName = fcName;
  }


  /**
   * Gets the fc.
   *
   * @return the fc
   */
  public String getFc() {
    return this.fcName;
  }


  /**
   * Gets the id.
   *
   * @return the id
   */
  public Long getId() {
    return this.fcWpId;
  }


  /**
   * Gets the cal contact 1.
   *
   * @return the calContact1
   */
  public String getCalContact1() {
    return this.calContact1;
  }


  /**
   * Gets the cal contact 2.
   *
   * @return the calContact2
   */
  public String getCalContact2() {
    return this.calContact2;
  }


  /**
   * Gets the fc 2 wp type.
   *
   * @return the fc2wpType
   */
  public String getFc2wpType() {
    return this.fc2wpType;
  }


  /**
   * Gets the wp res.
   *
   * @return the wpGroup
   */
  public String getWpRes() {
    return this.wpResource;
  }

  /**
   * Gets the wp number.
   *
   * @return the wpNumber
   */
  public String getWpNumber() {
    return this.wpNumber;
  }


  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FC2WP arg0) {
    return ModelUtil.compare(getWpName(), arg0.getWpName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getId() == null) ? 0 : getId().hashCode());
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
    FC2WP other = (FC2WP) obj;
    return getId() == other.getId();
  }
}