/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class PidcRmProjCharacterExt implements Comparable<PidcRmProjCharacterExt> {

  private PidcRmProjCharacter pidRmChar;

  // Category id key and value is Risk level id. Category and risk object can be taken from the Meta Data.
  private Map<Long, Long> categoryRiskMap = new ConcurrentHashMap<>();


  /**
   * @return the categoryRiskMap
   */
  public Map<Long, Long> getCategoryRiskMap() {
    return this.categoryRiskMap;
  }


  /**
   * @param categoryRiskMap the categoryRiskMap to set
   */
  public void setCategoryRiskMap(final Map<Long, Long> categoryRiskMap) {
    this.categoryRiskMap = categoryRiskMap;
  }


  /**
   * @return the pidRmChar
   */
  public PidcRmProjCharacter getPidRmChar() {
    return this.pidRmChar;
  }


  /**
   * @param pidRmChar the pidRmChar to set
   */
  public void setPidRmChar(final PidcRmProjCharacter pidRmChar) {
    this.pidRmChar = pidRmChar;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcRmProjCharacterExt output) {
    return ModelUtil.compare(this.pidRmChar.getProjCharId(), output.getPidRmChar().getId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(this.pidRmChar.getProjCharId(), ((PidcRmProjCharacterExt) obj).getPidRmChar().getId());
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(this.pidRmChar.getProjCharId());
  }

}
