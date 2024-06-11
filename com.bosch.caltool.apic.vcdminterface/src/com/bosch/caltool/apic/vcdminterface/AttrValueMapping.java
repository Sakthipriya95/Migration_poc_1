/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.vcdminterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Attribute value mapping
 *
 * @author BNE4COB
 */
public class AttrValueMapping {

  private Map<String, Map<String, Map<String, String>>> attrValueMapping;


  /**
   * @param pidcAttributeName attribute name in iCDM
   * @return vCDM attribute mapped to this attribute
   */
  public String getVCDMAttribute(final String pidcAttributeName) {

    /*
     * The attrValueMapping has an entry for the PIDC attribute, if a mapping is available, otherwise the attribute is
     * not existing in vCDM. If the attribute is available in vCDM, the one and only entry in the map is having the vCDM
     * attribute name as the key.
     */
    if (this.attrValueMapping.get(pidcAttributeName) == null) {
      return null;
    }
    return this.attrValueMapping.get(pidcAttributeName).keySet().iterator().next();
  }

  /**
   * Get the vCDM attribute value for the value of a PIDC attribute
   *
   * @param pidcAttributeName attribute name in iCDM
   * @param pidcAttributeValue attribute value in iCDM
   * @return the vCDM attribute value or NULL if not existing in vCDM
   */
  public String getVCDMAttributeValue(final String pidcAttributeName, final String pidcAttributeValue) {

    String vCDMAttrValue = null;

    String vCDMAttrName = getVCDMAttribute(pidcAttributeName);

    if (vCDMAttrName != null) {
      // attribute is available in vCDM
      vCDMAttrValue = this.attrValueMapping.get(pidcAttributeName).get(vCDMAttrName).get(pidcAttributeValue);

    }

    return vCDMAttrValue;

  }

  /**
   * Add attribute to the mapping list
   *
   * @param pidcAttributeName attribute name in iCDM
   * @param vCDMAttributeName attribute name in vCDM
   */
  public void addVCDMAttribute(final String pidcAttributeName, final String vCDMAttributeName) {

    if (this.attrValueMapping.get(pidcAttributeName) == null) {
      Map<String, Map<String, String>> vCDMAttrMap = new HashMap<String, Map<String, String>>();
      Map<String, String> valuesMap = new HashMap<String, String>();
      vCDMAttrMap.put(vCDMAttributeName, valuesMap);

      this.attrValueMapping.put(pidcAttributeName, vCDMAttrMap);
    }

  }

  /**
   * Add attribute value to mapping
   *
   * @param pidcAttributeName attribute name in PIDC
   * @param pidcAttrValue attribute value in ICDM
   * @param vCDMAttrValue attribute value in vCDM
   */
  public void addVCDMAttrValue(final String pidcAttributeName, final String pidcAttrValue, final String vCDMAttrValue) {

    Map<String, Map<String, String>> vCDMAttrMap = this.attrValueMapping.get(pidcAttributeName);

    if (vCDMAttrMap != null) {
      Map<String, String> valuesMap = vCDMAttrMap.values().iterator().next();

      valuesMap.put(pidcAttrValue, vCDMAttrValue);
    }

  }
}
