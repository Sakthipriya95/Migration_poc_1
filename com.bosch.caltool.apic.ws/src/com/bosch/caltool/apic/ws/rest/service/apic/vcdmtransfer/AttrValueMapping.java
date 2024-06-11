/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.vcdmtransfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.apic.vcdminterface.constants.VCDMConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.vector.easee.application.cdmservice.WSAttrMapList;

/**
 * Attribute value mapping
 *
 * @author BNE4COB
 */
public class AttrValueMapping {

  private final Map<String, Map<String, Map<String, String>>> attrValueMapping;
  private final VCDMDataStore vcdmDataStore;

  /**
   * Constructor
   *
   * @param variantAttributes variant attributes
   * @param vcdmAttributes vCDM attributes
   * @param vcdmDataStore vCDM data store
   */
  public AttrValueMapping(final Map<Attribute, Map<Long, String>> variantAttributes, final WSAttrMapList vcdmAttributes,
      final VCDMDataStore vcdmDataStore) {
    this.vcdmDataStore = vcdmDataStore;
    this.attrValueMapping = mapAttributesAndValues(variantAttributes, vcdmAttributes);

  }

  /**
   * create mapping of PIDC attributes and vCDM attributes (consider different upper/lower case) Contents of the result
   * MAP Map<PIDCAttrName, Map<vCDMAttrName, Map<PIDCValue, vCDMValue>>>
   *
   * @param variantAttributes
   * @param vcdmAttributes
   * @return
   */
  private Map<String, Map<String, Map<String, String>>> mapAttributesAndValues(
      final Map<Attribute, Map<Long, String>> variantAttributes, final WSAttrMapList vcdmAttributes) {

    // the mapping structure
    Map<String, Map<String, Map<String, String>>> attrValueMaping = new HashMap<>();


    // iterate over all PIDC variant attributes
    for (Attribute pIDCAttribute : variantAttributes.keySet()) {
      PidcVersionAttribute projAttr = this.vcdmDataStore.getPidcVrsnAttrModel().getPidcVersAttr(pIDCAttribute.getId());
      String pIDCAttributeName =

          null == this.vcdmDataStore.getMapOfAliasWithAttrId().get(projAttr.getAttrId()) ? projAttr.getName()
              : this.vcdmDataStore.getMapOfAliasWithAttrId().get(projAttr.getAttrId()).getName();
      // search variant attribute in vCDM attributes list
      for (String vCDMAttrName : vcdmAttributes.keySet()) {
        if (vCDMAttrName.equalsIgnoreCase(pIDCAttributeName)) {
          // attribute found in vCDM

          Map<String, Map<String, String>> vCDMAttrMap = new HashMap<String, Map<String, String>>();
          Map<String, String> valuesMap = new HashMap<String, String>();
          vCDMAttrMap.put(vCDMAttrName, valuesMap);

          attrValueMaping.put(pIDCAttributeName, vCDMAttrMap);

          // iterate over all values used in PIDC variants
          Map<Long, String> pidcAttrValMap = variantAttributes.get(pIDCAttribute);
          for (Entry<Long, String> pidcAttrValMapEntry : pidcAttrValMap.entrySet()) {
            String pidcAttrValue = pidcAttrValMapEntry.getValue();

            // search attribute value in vCDM
            for (String vCDMAttrValue : vcdmAttributes.get(vCDMAttrName)) {
              String pidcAttrValueString;


              if ((pidcAttrValue == null) &&
                  pidcAttrValMapEntry.getKey().equals(VCDMConstants.NOT_USED_ATTR_VALUE_ID)) {
                // attribute is NOT-USED
                pidcAttrValueString = VCDMConstants.NOT_USED_ATTR_VALUE;
              }
              else {
                pidcAttrValueString = pidcAttrValue;
              }

              if (vCDMAttrValue.equalsIgnoreCase(pidcAttrValueString)) {
                // attribute value found in vCDM

                valuesMap.put(pidcAttrValueString, vCDMAttrValue);
              }
            }

          }

        }
      }

    }
    return attrValueMaping;

  }

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
