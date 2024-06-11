/**
 *
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
   * @param attribute Attribute
   */
  public AttributeValueDummy(final Attribute attribute) {
    this.attribute = attribute;
  }

  /**
   * iCDM-1317 Constructor for AttributeValue - <NOT Used> and <Used>
   *
   * @param apicDataProvider apic Data Provider
   * @param valueId valueID
   * @param attribute attribute
   */
  protected AttributeValueDummy(final Attribute attribute, final Long valueId) {
    this.attribute = attribute;
  }

  /**
   * Icdm-832 Dummy value for clearing status
   */


  @Override
  public String getClearingStatus() {
    return null;
  }

  /**
   * This is not applicable
   */

  public String getClearingStatusStr() {
    return "";
  }


  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getAttributeID()
   */

  public Long getAttrId() {
    return this.attribute.getId();
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getAttribute()
   */

  public Attribute getAttribute() {
    return this.attribute;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValue() return the value as "" normal and "USECurrentValue"
   * for sub var var value setting
   */

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

  public BigDecimal getNumberValue() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getDateValue()
   */

  @Override
  public String getDateValue() {
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
  public String getCreatedDate() {
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
  public String getModifiedDate() {
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

  public String getValueDescEng() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValueDescGer()
   */

  public String getValueDescGer() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#getValueDependencies(boolean)
   */

  public List<AttrNValueDependency> getValueDependencies() {
    return new ArrayList<>();
  }


  /*
   * (non-Javadoc)
   * @see com.bosch.caltool.apic.jpa.bo.AttributeValue#isValid(java.util.Map)
   */

  public boolean isValid() {
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

  public int compareTo() {
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
   *
   */

  public boolean isCleared() {
    return true;
  }

  /**
   *
   */

  public boolean isDelOrRej() {
    return false;
  }

  /**
   *
   */

  public CharacteristicValue getCharacteristicValue() {
    return null;
  }

  /**
   *
   */

  public String getCharValStr() {
    return "";
  }

  /**
   *
   */
  @Override
  public Long getVersion() {
    return 0L;
  }

  /**
   *
   */

  public String getValue(final boolean showUnit) {
    return "";
  }

  /**
   *
   */

  public String getValueEng() {
    return "";
  }

  /**
   *
   */

  public String getValueEngText() {
    return "";
  }

  /**
   *
   */

  public String getValueGer() {
    return "";
  }

  /**
   *
   */

  public boolean isValidValue() {
    return true;
  }

  /**
   *
   */

  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    return false;
  }

  /**
   *
   */
  @Override
  public String getName() {
    return "";
  }


  /**
   *
   */

  public boolean isValueInvalid() {
    return false;
  }

  /**
   *
   */
  @Override
  public String getChangeComment() {
    return "";
  }


}
