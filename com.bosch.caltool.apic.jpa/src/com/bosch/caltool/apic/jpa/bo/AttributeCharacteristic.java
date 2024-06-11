/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristicValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents Char Char as stored in the database file T_CHARACTERISTICS
 *
 * @author rgo7cob
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AttributeCharacteristic extends ApicObject implements Comparable<AttributeCharacteristic> { // NOPMD by


  /**
   * Attr Description German
   */
  private static final String FLD_CHAR_DESC_GER = "CHAR_DESC_GER";
  /**
   * Attr Description Eng
   */
  private static final String FLD_CHAR_DESC_ENG = "CHAR_DESC_ENG";


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }

  /**
   * Attr Name German
   */
  private static final String FLD_CHAR_NAME_GER = "CHAR_NAME_GER";
  /**
   * Char name English
   */
  public static final String FLD_CHAR_NAME_ENG = "CHAR_NAME_ENG";

  private SortedSet<AttributeCharacteristicValue> charValSet;


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param attrcharID ID
   */
  public AttributeCharacteristic(final ApicDataProvider apicDataProvider, final Long attrcharID) {
    super(apicDataProvider, attrcharID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbCharacteristic(getID()).getVersion();
  }


  /**
   * Icdm-954 Get the Characteristics Values for the Characteristic
   *
   * @return List of Char Values
   */
  public SortedSet<AttributeCharacteristicValue> getCharValues() {
    if (CommonUtils.isNull(this.charValSet)) {
      this.charValSet = new TreeSet<AttributeCharacteristicValue>();
      final List<TCharacteristicValue> tabvCharValues =
          getEntityProvider().getDbCharacteristic(getID()).gettCharacteristicValues();
      if (tabvCharValues != null) {
        addCharValSet(tabvCharValues);
      }
    }
    return this.charValSet;
  }


  /**
   * @param tabvCharValues
   */
  private void addCharValSet(final List<TCharacteristicValue> tabvCharValues) {
    for (TCharacteristicValue tCharacteristicValue : tabvCharValues) {
      AttributeCharacteristicValue attrCharVal =
          getDataCache().getAllCharValMap().get(tCharacteristicValue.getCharValId());
      if (attrCharVal == null) {
        attrCharVal =
            new AttributeCharacteristicValue(getDataCache().getDataProvider(), tCharacteristicValue.getCharValId());
      }
      this.charValSet.add(attrCharVal);
    }
  }

  /**
   * Get the list of Attributes associated with the Characteristics
   *
   * @return List of Char Values
   */
  public SortedSet<Attribute> getAttributes() {
    final SortedSet<Attribute> attrSet = new TreeSet<Attribute>();
    final List<TabvAttribute> tabvAttributes = getEntityProvider().getDbCharacteristic(getID()).getTabvAttributes();
    if (tabvAttributes != null) {
      for (TabvAttribute tabvAttribute : tabvAttributes) {
        attrSet.add(getDataCache().getAttribute(tabvAttribute.getAttrId()));
      }
    }
    return attrSet;
  }

  /**
   * Get the creation date of the Char
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCharacteristic(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Char
   *
   * @return The ID of the user who has created the Char
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbCharacteristic(getID()).getCreatedUser();
  }

  /**
   * Get the date when the Char has been modified the last time
   *
   * @return The date when the Char has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCharacteristic(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Char the last time
   *
   * @return The user who has modified the Char the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbCharacteristic(getID()).getModifiedUser();
  }

  /**
   * @return String
   */
  public String getFocusMatrixYN() {
    return getEntityProvider().getDbCharacteristic(getID()).getFocusMatrixYN();
  }

  /**
   * Returns the Char Name in ENGLISH
   *
   * @return Char English Name in String
   */
  public String getNameEng() {
    String returnValue = getEntityProvider().getDbCharacteristic(getID()).getCharNameEng();
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
    String returnValue = getEntityProvider().getDbCharacteristic(getID()).getCharNameGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * Get the description of the Value Currently only the English description will be returned. Multi language support
   * will be implementd later.
   *
   * @return The description of the Char
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getDescEng(), getDescGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * Returns the Char Description in ENGLISH
   *
   * @return Char English description in String
   */
  public String getDescEng() {
    String returnValue = getEntityProvider().getDbCharacteristic(getID()).getDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Char Description in GERMAN
   *
   * @return Char German Description in String
   */
  public String getDescGer() {
    String returnValue = getEntityProvider().getDbCharacteristic(getID()).getDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final AttributeCharacteristic arg0) {

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
  public int compareTo(final AttributeCharacteristic arg0, final int sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getDescription(), arg0.getDescription());
        break;
      case ApicConstants.SORT_ATTRNAME:
        // Char name needs not to be compared because it is the default sort
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the Char name
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
    objDetails.put(FLD_CHAR_NAME_ENG, getNameEng());
    objDetails.put(FLD_CHAR_NAME_GER, getNameGer());
    objDetails.put(FLD_CHAR_DESC_ENG, getDescEng());
    objDetails.put(FLD_CHAR_DESC_GER, getDescEng());

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
    return EntityType.CHARACTERISTIC;
  }


}