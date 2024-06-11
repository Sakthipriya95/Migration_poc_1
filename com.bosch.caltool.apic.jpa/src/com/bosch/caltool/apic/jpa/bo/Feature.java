/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TSsdValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents a feature as stored in the SSD database
 *
 * @author dmo5cob
 */
public class Feature extends ApicObject {

  private final ApicDataProvider apicDataProvider;

  /**
   * Icdm-1066 map for Storing the Features Values with FV ID as key
   */
  private Map<Long, FeatureValue> fvMap;

  /**
   * Icdm-1066 map for Storing the Features Values with Attr Val ID as key
   */
  private Map<Long, FeatureValue> attrValFvMap;

  /**
   * Icdm-1066 boolean for Checking Value loaded
   */
  private boolean valuesLoaded;


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param featureID ID
   */
  public Feature(final ApicDataProvider apicDataProvider, final Long featureID) {
    super(apicDataProvider, featureID);
    this.apicDataProvider = apicDataProvider;

  }


  /**
   * Get the FeatureID of the Feature object
   *
   * @return The unique ID of the Feature (from the database object)
   */
  public Long getFeatureID() {
    return getID();
  }

  /**
   * Get the attributeID
   *
   * @return .
   */
  public Long getAttributeID() {
    TabvAttribute tabvAttribute = getEntityProvider().getDbFeature(getID()).getTabvAttribute();
    return null != tabvAttribute ? tabvAttribute.getAttrId() : null;
  }

  /**
   * Get the attributeID
   *
   * @return .
   */
  public String getFeatureText() {
    return getEntityProvider().getDbFeature(getID()).getFeatureText();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {

    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    String returnValue = getEntityProvider().getDbFeature(getID()).getFeatureText();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  // ICDM-1070
  /**
   * @return attr name for mapped feature
   */
  public String getAttributeName() {
    String attrName = "";
    if (getAttributeID() != null) {
      attrName = this.apicDataProvider.getAttribute(getAttributeID()).getAttributeName();
    }
    return attrName;

  }


  /**
   * Load the Feature Values in three Maps Map1 - Key: Feature Value ID, Value : featureValObj ,Map2 - Key: Attr Value
   * ID ,Map3 - Key: Feature ID , Value : featureValObj (Map3 : Will store the feature value objects whose used flag =
   * 'N'
   */
  private void loadFeatureValues() {
    if (!this.valuesLoaded) {
      this.fvMap = new ConcurrentHashMap<Long, FeatureValue>();
      this.attrValFvMap = new ConcurrentHashMap<Long, FeatureValue>();
      // read ssd mapping
      for (TSsdValue dbFeatureVal : getEntityProvider().getDbFeature(getID()).getTSsdValues()) {
        FeatureValue featureValObj = new FeatureValue(this.apicDataProvider, dbFeatureVal.getValueId());
        this.fvMap.put(dbFeatureVal.getValueId(), featureValObj);
        fillFeatureValMap(dbFeatureVal, featureValObj);
      }
      this.valuesLoaded = true;
    }
  }


  /**
   * Fills feature val map based on USED flag configuration in T_SSD_VALUES (USED_FLAG)
   *
   * @param dbFeatureVal
   * @param featureValObj
   */
  private void fillFeatureValMap(final TSsdValue dbFeatureVal, final FeatureValue featureValObj) {
    if ((null == dbFeatureVal.getTabvAttrValue()) && (null != dbFeatureVal.getUsedFlag())) {
      // add NOT-USED value if mapped
      if (dbFeatureVal.getUsedFlag().equals(ApicConstants.CODE_NO)) {
        this.attrValFvMap.put(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID, featureValObj);
      } // add USED value if mapped
      else if (dbFeatureVal.getUsedFlag().equals(ApicConstants.YES)) {
        this.attrValFvMap.put(ApicConstants.ATTR_VAL_USED_VALUE_ID, featureValObj);
      }
      else if (dbFeatureVal.getUsedFlag().equals("?")) {
        this.attrValFvMap.put(ApicConstants.ATTR_VAL_NOT_SET_VALUE_ID, featureValObj);
      }
    } // other mapped values
    else if (dbFeatureVal.getTabvAttrValue() != null) {
      this.attrValFvMap.put(dbFeatureVal.getTabvAttrValue().getValueId(), featureValObj);
    }
  }

  /**
   * iCDM-1316,1317 Check if the NOT-USED flag is mapped in SSD
   *
   * @return true if mapped
   */
  public boolean isNotUsedFlagMapped() {
    return (this.attrValFvMap.get(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID) == null) ? false : true;
  }

  /**
   * Check if the USED flag is mapped in SSD
   *
   * @return true if mapped
   */
  public boolean isUsedFlagMapped() {
    return (this.attrValFvMap.get(ApicConstants.ATTR_VAL_USED_VALUE_ID) == null) ? false : true;
  }

  /**
   * @return the Feature Value map.
   */
  public Map<Long, FeatureValue> getFeatureValues() {
    loadFeatureValues();
    return this.fvMap;
  }

  /**
   * @return the Feature Value map.
   */
  public Map<Long, FeatureValue> getFeatureValAttrMap() {
    loadFeatureValues();
    return this.attrValFvMap;
  }

  /**
   * iCDM-1316
   *
   * @return the FeatureValue with used flag = N
   */
  public FeatureValue getNotUsedFeatureValue() {
    loadFeatureValues();
    return this.attrValFvMap.get(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID);
  }

  /**
   * iCDM-1317
   *
   * @return FeatureValue with used flag = Y
   */
  public FeatureValue getUsedFeatureValue() {
    loadFeatureValues();
    return this.attrValFvMap.get(ApicConstants.ATTR_VAL_USED_VALUE_ID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbFeature(getID()).getCreatedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFeature(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFeature(getID()).getCreatedDate(), false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFeature(getID()).getModifiedDate(), false);
  }

}