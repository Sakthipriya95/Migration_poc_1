/**
 *
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.notification.IEntityType;

/**
 * this class is used while editing pidc/variant/sub-variant attribute value dummy attribute value implies no value for
 * the attribute
 *
 * @author hef2fe
 */
public class AttributeValueDummy extends AttributeValue {

  /**
   * Constant for dummy ID value
   */
  public static final Long VALUE_ID = 0L;

  /**
   * Attribute to which the dummy value belongs
   */
  private final Attribute attribute;

  /**
   * value for using the CurrentValue
   */
  private String value = "";


  /**
   * @param apicDataProvider apic Data Provider
   * @param attribute Attribute
   */
  public AttributeValueDummy(final ApicDataProvider apicDataProvider, final Attribute attribute) {
    super(apicDataProvider, VALUE_ID);
    this.attribute = attribute;
  }

  /**
   * iCDM-1317 Constructor for AttributeValue - <NOT Used> and <Used>
   *
   * @param apicDataProvider apic Data Provider
   * @param valueId valueID
   * @param attribute attribute
   */
  protected AttributeValueDummy(final ApicDataProvider apicDataProvider, final Attribute attribute, final Long valueId) {
    super(apicDataProvider, valueId);
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
   * return the value as "" normal and "USECurrentValue" for sub var var value setting
   */
  @Override
  public String getValue() {
    return this.value;
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
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getTextValue()
   */
  @Override
  public String getTextValue() {
    return "";
  }

  /**
   * @param value value as Current value
   */
  public void setValue(final String value) {
    this.value = value;
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
    return -1;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#compareTo(java.lang.String)
   */
  @Override
  public int compareTo(final String stringValue2) {
    // dummy attribute values are always less than other attributes
    return -1;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCleared() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDelOrRej() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeCharacteristicValue getCharacteristicValue() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCharValStr() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return 0L;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValue(final boolean showUnit) {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValueEng() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValueEngText() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValueGer() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValidValue(final Map<Long, IPIDCAttribute> refPidcAttributes, final boolean includeDeleted) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "";
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
  public boolean isValueInvalid() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getChangeComment() {
    return "";
  }


}
