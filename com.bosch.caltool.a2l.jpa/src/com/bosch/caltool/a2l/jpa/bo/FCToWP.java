package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

// icdm-276 changes
/**
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class FCToWP implements Comparable<FCToWP> {

  private final Long fcWpId;

  private final String calContact1;

  private final String calContact2;

  private final String fc2wpType;

  private final String wpResource;
  private final String wpNameE;

  private final String wpNameG;
  private final String wpNumber;
  private final String fcName;

  private final Language currentLanguage;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @param a2lDataProvider A2lDataProvider
   * @param objID id
   * @param calContact1 calContact1
   * @param calContact2 calContact2
   * @param fc2wpType fc2wpType
   * @param wpGroup wpGroup
   * @param wpNameE wpNameE
   * @param wpNameG wpNameG
   * @param wpNumber wpNumber
   * @param fcName
   */
  public FCToWP(final Language language, final Long objID, final String calContact1, final String calContact2,
      final String fc2wpType, final String wpResource, final String wpNameE, final String wpNameG,
      final String wpNumber, final String fcName) {
    this.fcWpId = objID;
    this.calContact1 = calContact1;
    this.calContact2 = calContact2;
    this.fc2wpType = fc2wpType;
    this.wpResource = wpResource;
    this.wpNameE = wpNameE;
    this.wpNameG = wpNameG;
    this.wpNumber = wpNumber;
    this.fcName = fcName;
    this.currentLanguage = language;
  }


  /**
   * @return the fc
   */
  public String getFc() {
    return this.fcName;
  }


  /**
   * @return the id
   */
  public Long getId() {
    return this.fcWpId;
  }


  /**
   * @return the calContact1
   */
  public String getCalContact1() {
    return this.calContact1;
  }


  /**
   * @return the calContact2
   */
  public String getCalContact2() {
    return this.calContact2;
  }


  /**
   * @return the fc2wpType
   */
  public String getFc2wpType() {
    return this.fc2wpType;
  }


  /**
   * @return the wpGroup
   */
  public String getWpRes() {
    return this.wpResource;
  }


  /**
   * @return the wpNameE
   */
  public String getWpNameE() {
    return this.wpNameE;
  }


  /**
   * @return the wpNameG
   */
  public String getWpNameG() {
    return this.wpNameG;
  }


  /**
   * @return the wpNumber
   */
  public String getWpNumber() {
    return this.wpNumber;
  }

  /**
   * Get name based on the language
   *
   * @return WP Name
   */
  public String getName() {
    String name = null;
    if (getLanguage() == Language.ENGLISH) {
      name = getWpNameE();
      if (name == null) {
        name = getWpNameG();
      }
    }
    else if (getLanguage() == Language.GERMAN) {
      name = getWpNameG();
      if (name == null) {
        name = getWpNameE();
      }
    }

    return name == null ? ApicConstants.NAME_NOT_DEFINED : name;
  }

  /**
   * Get the current language
   *
   * @return Language
   */
  public Language getLanguage() {
    return this.currentLanguage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FCToWP arg0) {
    return ApicUtil.compare(getName(), arg0.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getId() == null) ? 0 : getId().hashCode());
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
    FCToWP other = (FCToWP) obj;
    return CommonUtils.isEqual(getId(), other.getId());
  }
}