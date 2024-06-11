/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * iCDM-1317
 *
 * @author adn1cob
 */
public class AttributeValueUsed extends AttributeValueDummy {


  /**
   * Attribute to which the dummy value belongs
   */
  private final Attribute attribute;


  /**
   * @param apicDataProvider data provider
   * @param attribute attribute
   */
  public AttributeValueUsed(final ApicDataProvider apicDataProvider, final Attribute attribute) {
    super(apicDataProvider, attribute, ApicConstants.ATTR_VAL_USED_VALUE_ID);
    this.attribute = attribute;
  }


  /**
   * Icdm-832 Dummy value for clearing status {@inheritDoc}
   */
  @Override
  public CLEARING_STATUS getClearingStatus() {
    return null;
  }

  /**
   * This is not applicable {@inheritDoc}
   */
  @Override
  public String getClearingStatusStr() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#isModifiable()
   */
  @Override
  public boolean isModifiable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getAttributeID()
   */
  @Override
  public Long getAttributeID() {
    return this.attribute.getAttributeID();
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getAttribute()
   */
  @Override
  public Attribute getAttribute() {
    return this.attribute;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValue()
   */
  @Override
  public String getValue() {
    return ApicConstants.USED;
  }


  @Override
  public String getName() {
    return ApicConstants.USED;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getUnit()
   */
  @Override
  public String getUnit() {
    return this.attribute.getUnit();
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getDescription()
   */
  @Override
  public String getDescription() {
    return ApicConstants.USED;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getTextValue()
   */
  @Override
  public String getTextValue() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getNumberValue()
   */
  @Override
  public BigDecimal getNumberValue() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getDateValue()
   */
  @Override
  public Calendar getDateValue() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#isDeleted()
   */
  @Override
  public boolean isDeleted() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getCreatedDate()
   */
  @Override
  public Calendar getCreatedDate() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getCreatedUser()
   */
  @Override
  public String getCreatedUser() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getModifiedDate()
   */
  @Override
  public Calendar getModifiedDate() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getModifiedUser()
   */
  @Override
  public String getModifiedUser() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getTextValueEng()
   */
  @Override
  public String getTextValueEng() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getTextValueGer()
   */
  @Override
  public String getTextValueGer() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValueDescEng()
   */
  @Override
  public String getValueDescEng() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValueDescGer()
   */
  @Override
  public String getValueDescGer() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValueDependencies(boolean)
   */
  @Override
  public List<AttrValueDependency> getValueDependencies(final boolean includeDeleted) {
    return new ArrayList<AttrValueDependency>();
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#isValid(java.util.Map)
   */
  @Override
  public boolean isValid(final Map<Long, AttributeValue> currentContextValuesMap) {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#compareTo(com.bosch.caltool.apic.jpa.bo.AttributeValue)
   */
  @Override
  public int compareTo(final AttributeValue attrValue2) {
    // dummy attribute values are always less than other attributes
    return 1;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#compareTo(java.lang.String)
   */
  @Override
  public int compareTo(final String stringValue2) {
    // dummy attribute values are always less than other attributes
    return 1;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#compareTo(com.bosch.caltool.apic.jpa.bo.AttributeValue,
   * com.bosch.caltool.apic.jpa.bo.AttributeValue.SortColumns)
   */
  @Override
  public int compareTo(final AttributeValue attrValue2, final SortColumns sortColumn) {
    return this.compareTo(attrValue2);
  }

}
