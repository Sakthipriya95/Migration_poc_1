/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;

/**
 * This class represents a feature value as stored in the SSD database
 *
 * @author dmo5cob
 */
public class FeatureValue extends ApicObject {

  private final ApicDataProvider apicDataProvider;

  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param valueId ID
   */
  public FeatureValue(final ApicDataProvider apicDataProvider, final Long valueId) {
    super(apicDataProvider, valueId);
    this.apicDataProvider = apicDataProvider;
  }


  /**
   * Get the ValueID of the value object
   *
   * @return The unique ID of the value (from the database object)
   */
  public Long getValueID() {
    return getID();
  }

  /**
   * Get the attributeID
   *
   * @return .
   */
  public Long getApicValueId() {
    TabvAttrValue tabvAttrValue = getEntityProvider().getDbFeatureValue(getID()).getTabvAttrValue();
    return null != tabvAttrValue ? tabvAttrValue.getValueId() : null;
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
    return getValueText();
  }

  // ICDM-1070
  /**
   * @return attr name for mapped feature
   */
  public String getAttributeValName() {
    String valName = "";
    if (getValueID() != null) {
      valName = this.apicDataProvider.getAttrValue(getApicValueId()).getName();
    }
    return valName;

  }


  /**
   * Icdm-1066 Return the Value Text
   *
   * @return the Value Text
   */
  public String getValueText() {
    return getEntityProvider().getDbFeatureValue(getID()).getValueText();
  }

  /**
   * @return returns the used flag Y/N
   */
  public String getUsedFlag() {
    return getEntityProvider().getDbFeatureValue(getID()).getUsedFlag();
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
    return getEntityProvider().getDbFeatureValue(getID()).getCreatedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFeatureValue(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFeatureValue(getID()).getCreatedDate(), false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFeatureValue(getID()).getModifiedDate(), false);
  }
}