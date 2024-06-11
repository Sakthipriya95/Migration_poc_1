/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents predefined attribute value combination as stored in the database table T_PREDEFINED_ATTR_VALUES
 *
 * @author dmo5cob
 */
public class PredefinedAttrValue extends ApicObject implements Comparable<PredefinedAttrValue> {


  /**
   * constructor
   *
   * @param apicDataProvider data provider
   * @param preDfndAttrValID ID
   */
  public PredefinedAttrValue(final ApicDataProvider apicDataProvider, final Long preDfndAttrValID) {
    super(apicDataProvider, preDfndAttrValID);
    getDataCache().getPredAttrValMap().put(preDfndAttrValID, this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbPredAttrValue(getID()).getVersion();
  }


  /**
   * Get the predefined Attribute
   *
   * @return Attribute object
   */
  public Attribute getPredefinedAttribute() {
    final TabvAttribute tAttribute = getEntityProvider().getDbPredAttrValue(getID()).getPreDefinedAttr();
    if (tAttribute != null) {
      return getDataCache().getAllAttributes().get(tAttribute.getAttrId());
    }
    return null;
  }

  /**
   * Get the predefined Attribute Value
   *
   * @return AttributeValue object
   */
  public AttributeValue getPredefinedAttributeValue() {
    final TabvAttrValue tAttributeVal = getEntityProvider().getDbPredAttrValue(getID()).getPreDefAttrVal();
    if (tAttributeVal != null) {
      return getDataCache().getAllAttrValuesMap().get(tAttributeVal.getValueId());
    }
    return null;
  }

  /**
   * Get the grouped attribute's value
   *
   * @return Attribute object
   */
  public AttributeValue getGroupedAttrValue() {
    final TabvAttrValue tAttributeVal = getEntityProvider().getDbPredAttrValue(getID()).getGrpAttrVal();
    if (tAttributeVal != null) {
      return getDataCache().getAllAttrValuesMap().get(tAttributeVal.getValueId());
    }
    return null;
  }

  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {

    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPredAttrValue(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  @Override
  public String getCreatedUser() {

    return getEntityProvider().getDbPredAttrValue(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {

    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPredAttrValue(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPredAttrValue(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final PredefinedAttrValue arg0) {
    int compResult = 0;
    if ((null != getPredefinedAttributeValue()) && (null != arg0.getPredefinedAttributeValue())) {
      compResult =
          ApicUtil.compare(getPredefinedAttributeValue().getValue(), arg0.getPredefinedAttributeValue().getValue());
    }
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), arg0.getID());
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final PredefinedAttrValue arg0, final int sortColumn) {

    int compareResult;

    switch (sortColumn) {

      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getDescription(), arg0.getDescription());
        break;
      // Value name needs not to be compared because it is the default sort
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the Value name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }

  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    return objDetails;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return null == getPredefinedAttribute() ? "" : getPredefinedAttribute().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.PREDEFND_ATTR_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return null == getPredefinedAttribute() ? "" : getPredefinedAttribute().getDescription();
  }
}