/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class AttributeWithValueTypeV2 {

  private AttributeV2 attribute;

  private String used;

  private boolean isVariant;

  private AttributeValue attrValue;

  private String partNumber;

  private String specLink;

  private String description;

  private long changeNumber;


  /**
   * @return the attribute
   */
  public AttributeV2 getAttribute() {
    return attribute;
  }


  /**
   * @param attribute the attribute to set
   */
  public void setAttribute(AttributeV2 attribute) {
    this.attribute = attribute;
  }


  /**
   * @return the used
   */
  public String getUsed() {
    return used;
  }


  /**
   * @param used the used to set
   */
  public void setUsed(String used) {
    this.used = used;
  }


  /**
   * @return the isVariant
   */
  public boolean isVariant() {
    return isVariant;
  }


  /**
   * @param isVariant the isVariant to set
   */
  public void setVariant(boolean isVariant) {
    this.isVariant = isVariant;
  }


  /**
   * @return the attrValue
   */
  public AttributeValue getAttrValue() {
    return attrValue;
  }


  /**
   * @param attrValue the attrValue to set
   */
  public void setAttrValue(AttributeValue attrValue) {
    this.attrValue = attrValue;
  }


  /**
   * @return the partNumber
   */
  public String getPartNumber() {
    return partNumber;
  }


  /**
   * @param partNumber the partNumber to set
   */
  public void setPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }


  /**
   * @return the specLink
   */
  public String getSpecLink() {
    return specLink;
  }


  /**
   * @param specLink the specLink to set
   */
  public void setSpecLink(String specLink) {
    this.specLink = specLink;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * @return the changeNumber
   */
  public long getChangeNumber() {
    return changeNumber;
  }


  /**
   * @param changeNumber the changeNumber to set
   */
  public void setChangeNumber(long changeNumber) {
    this.changeNumber = changeNumber;
  }
}
