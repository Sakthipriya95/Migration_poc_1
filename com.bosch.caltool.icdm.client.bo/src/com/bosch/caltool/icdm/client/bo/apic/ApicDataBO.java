/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author dja7cob
 */
public class ApicDataBO {

  /**
   * Get the pidc structure attribute max level
   *
   * @return integer (max level)
   */
  public int getPidcStructMaxLvl() {
    return ApicDataCache.INSTANCE.getPidcStructMaxLvl();
  }

  /**
   * Get level attributes (key - level, value - attribute)
   *
   * @return
   */
  public Map<Long, Attribute> getAllLvlAttrByLevel() {
    return ApicDataCache.INSTANCE.getAllLvlAttrByLevel();
  }

  /**
   * All PIDC tree structure attribute IDs
   *
   * @return Map. Key - level, value - Attribute ID
   */
  public Map<Long, Long> getAllPidcTreeAttrIds() {
    return ApicDataCache.INSTANCE.getAllPidcTreeAttrIds();
  }

  /**
   * Get level attributes (key - attr id, value - set of attr val)
   *
   * @return map of level attribute values
   */
  public Map<Long, Map<Long, AttributeValue>> getAllPidTreeLvlAttrValueMap() {
    return ApicDataCache.INSTANCE.getPidTreeLvlAttrValMap();
  }

  /**
   * Get level attributes (key - attr id, value - set of attr val)
   *
   * @return Map<Long, Map<Long, Long>>
   */
  public Map<Long, Map<Long, Set<Long>>> getLvlAttrValDepMap() {
    return ApicDataCache.INSTANCE.getValDepnMap();
  }

  /**
   * @return PIDC Name attribute ID
   */
  public Long getPidcNameAttrId() {
    return ApicDataCache.INSTANCE.getAllLvlAttrByLevel().get((long) ApicConstants.PROJECT_NAME_ATTR).getId();
  }

  /**
   * @return PIDC Variant Name attribute ID
   */
  public Long getPidcVariantNameAttrId() {
    return ApicDataCache.INSTANCE.getAllLvlAttrByLevel().get((long) ApicConstants.VARIANT_CODE_ATTR).getId();
  }

  /**
   * @param pidcVersion
   * @return
   */
  public boolean isPidcUnlockedInSession(final PidcVersion pidcVersion) {
    return ApicDataCache.INSTANCE.getUnlockedPidcsInSession().contains(pidcVersion.getPidcId());
  }

  /**
   * @param pidcVersion
   */
  public void setPidcLockedInSession(final PidcVersion pidcVersion) {
    ApicDataCache.INSTANCE.getUnlockedPidcsInSession().remove(pidcVersion.getPidcId());
  }

  /**
   * @param pidcId
   */
  public void setPidcUnLockedInSession(final PidcVersion pidcVersion) {
    ApicDataCache.INSTANCE.getUnlockedPidcsInSession().add(pidcVersion.getPidcId());
  }
}
