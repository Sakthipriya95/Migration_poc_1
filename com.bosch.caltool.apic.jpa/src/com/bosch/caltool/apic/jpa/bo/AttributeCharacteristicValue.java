/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents Char Value as stored in the database file T_CHARACTERISTIC_VALUES
 *
 * @author rgo7cob
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
@Deprecated
public class AttributeCharacteristicValue extends ApicObject implements Comparable<AttributeCharacteristicValue> {

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }

  /**
   * Value Description German
   */
  private static final String FLD_VAL_DESC_GER = "VAL_DESC_GER";
  /**
   * Value Description Eng
   */
  private static final String FLD_VAL_DESC_ENG = "VAL_DESC_ENG";
  /**
   * Value Name German
   */
  private static final String FLD_VAL_NAME_GER = "VAL_NAME_GER";
  /**
   * Value name English
   */
  public static final String FLD_VAL_NAME_ENG = "VAL_NAME_ENG";


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param charValID ID
   */
  public AttributeCharacteristicValue(final ApicDataProvider apicDataProvider, final Long charValID) {
    super(apicDataProvider, charValID);
    getDataCache().getAllCharValMap().put(charValID, this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbCharacteristicValue(getID()).getVersion();
  }


  /**
   * Get the Characteristics Values for the Characteristic
   *
   * @return List of Char Values
   */
  public AttributeCharacteristic getAttrChar() {
    final TCharacteristic tCharacteristic = getEntityProvider().getDbCharacteristicValue(getID()).gettCharacteristic();
    if (tCharacteristic != null) {
      return getDataCache().getAllCharMap().get(tCharacteristic.getCharId());
    }
    return null;
  }

  /**
   * Get the list of Values associated with the Characteristics
   *
   * @return List of Char Values
   */
  public Set<AttributeValue> getAttrValues() {

    final Set<AttributeValue> attrValSet = new TreeSet<AttributeValue>();

    final List<TabvAttrValue> tabvAttrValues =
        getEntityProvider().getDbCharacteristicValue(getID()).getTabvAttrValues();
    if (tabvAttrValues != null) {
      for (TabvAttrValue tabvAttrValue : tabvAttrValues) {
        attrValSet.add(getDataCache().getAttrValue(tabvAttrValue.getValueId()));
      }
    }
    return attrValSet;
  }

  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  public Calendar getCreatedDate() {

    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCharacteristicValue(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  public String getCreatedUser() {

    return getEntityProvider().getDbCharacteristicValue(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  public Calendar getModifiedDate() {

    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCharacteristicValue(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  public String getModifiedUser() {
    return getEntityProvider().getDbCharacteristicValue(getID()).getModifiedUser();
  }


  /**
   * Returns the Value Name in ENGLISH
   *
   * @return Value English Name in String
   */
  public String getNameEng() {
    String returnValue = getEntityProvider().getDbCharacteristicValue(getID()).getValNameEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Value Name in GERMAN
   *
   * @return Value German Name in String
   */
  public String getNameGer() {
    String returnValue = getEntityProvider().getDbCharacteristicValue(getID()).getValNameGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * Get the description of the Value Currently only the English description will be returned. Multi language support
   * will be implementd later.
   *
   * @return The description of the Value
   */
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getDescEng(), getDescGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * Returns the Value Description in ENGLISH
   *
   * @return Value English description in String
   */
  public String getDescEng() {
    String returnValue = getEntityProvider().getDbCharacteristicValue(getID()).getDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Value Description in GERMAN
   *
   * @return Value German Description in String
   */
  public String getDescGer() {
    String returnValue = getEntityProvider().getDbCharacteristicValue(getID()).getDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final AttributeCharacteristicValue arg0) {

    return ApicUtil.compare(getName(), arg0.getName());
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
  public int compareTo(final AttributeCharacteristicValue arg0, final int sortColumn) { // NOPMD by bne4cob on 6/20/14
                                                                                        // 10:27
    // AM
    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getDescription(), arg0.getDescription());
        break;
      case ApicConstants.SORT_ATTRNAME:
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

    objDetails.put(FLD_VAL_NAME_ENG, getNameEng());
    objDetails.put(FLD_VAL_NAME_GER, getNameGer());
    objDetails.put(FLD_VAL_DESC_ENG, getDescEng());
    objDetails.put(FLD_VAL_DESC_GER, getDescGer());

    return objDetails;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getNameEng(), getNameGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.CHARACTERISTIC_VALUE;
  }


}