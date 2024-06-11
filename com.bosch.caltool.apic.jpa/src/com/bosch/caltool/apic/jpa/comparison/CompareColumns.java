/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * CompareColumns.java - This class holds the members(columns) to be compared
 * 
 * @author adn1cob
 */
public class CompareColumns {

  /**
   * Used flag
   */
  private String usedFlag;

  /**
   * AttrValue as Text
   */
  private String attrValue;
  /**
   * Part Number as Text
   */
  private String partNumber;
  /**
   * Spec Link as Text
   */
  private String specLink;
  /**
   * Description as Text
   */
  private String desc;
  // ICDM-323
  /**
   * Value type
   */
  private AttributeValueType valueType;

  /**
   * @return typeId of the attribute value
   */

  public AttributeValueType getValueType() {
    return this.valueType;
  }

  /**
   * @param valueType to set the type of attribute value
   */

  public void setValueType(final AttributeValueType valueType) {
    this.valueType = valueType;
  }

  /**
   * @return the usedFlag
   */
  public String getUsedFlag() {
    return this.usedFlag;
  }


  /**
   * @param usedFlag the usedFlag to set
   */
  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }


  /**
   * @return the attrValue
   */
  public String getAttrValue() {
    return this.attrValue;
  }


  /**
   * @param attrValue the attrValue to set
   */
  public void setAttrValue(final String attrValue) {
    this.attrValue = attrValue;
  }


  /**
   * @return the partNumber
   */
  public String getPartNumber() {
    return this.partNumber;
  }


  /**
   * @param partNumber the partNumber to set
   */
  public void setPartNumber(final String partNumber) {
    this.partNumber = partNumber;
  }


  /**
   * @return the specLink
   */
  public String getSpecLink() {
    return this.specLink;
  }


  /**
   * @param specLink the specLink to set
   */
  public void setSpecLink(final String specLink) {
    this.specLink = specLink;
  }


  /**
   * @return the desc
   */
  public String getDesc() {
    return this.desc;
  }


  /**
   * @param desc the desc to set
   */
  public void setDesc(final String desc) {
    this.desc = desc;
  }


}
