/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.io.Serializable;
import java.util.Objects;

import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author rgo7cob
 */
public class AttributeValueModel implements Serializable, Comparable<AttributeValueModel> {

  /**
   *
   */
  private static final long serialVersionUID = -7036185797384012733L;

  private Attribute attr;

  private AttributeValue value;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * Error msg string ICDM-2019
   */
  private String errorMsg;

  /**
   * @return the errorMsg
   */
  public String getErrorMsg() {
    return this.errorMsg;
  }


  /**
   * @param errorMsg the errorMsg to set
   */
  public void setErrorMsg(final String errorMsg) {
    this.errorMsg = errorMsg;
  }

  /**
   * @return the attr
   */
  public Attribute getAttr() {
    return this.attr;
  }


  /**
   * @return the value
   */
  public AttributeValue getValue() {
    return this.value;
  }


  /**
   * @param attr the attr to set
   */
  public void setAttr(final Attribute attr) {
    this.attr = attr;
  }


  /**
   * @param value the value to set
   */
  public void setValue(final AttributeValue value) {
    this.value = value;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttributeValueModel attrValModel) {
    int compareResult;

    compareResult = getAttr().compareTo(attrValModel.getAttr());

    // skip comparison of Dummy,Used,Not Used attributeValue objects
    if ((compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) && (attrValModel.getValue() != null) &&
        (!attrValModel.getValue().getId().equals(ApicConstants.ATTR_VAL_USED_VALUE_ID) &&
            !attrValModel.getValue().getId().equals(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID))) {
      compareResult = getValue().compareTo(attrValModel.getValue());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) +
        (((getAttr() == null) || (getValue() == null)) ? 0 : getValue().getId().hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AttributeValueModel other = (AttributeValueModel) obj;

    boolean compareResult = Objects.equals(getAttr(), other.getAttr());
    // skip comparison of Dummy,Used,Not Used attributeValue objects
    if (compareResult && (other.getValue() != null) &&
        (!other.getValue().getId().equals(ApicConstants.ATTR_VAL_USED_VALUE_ID) &&
            !other.getValue().getId().equals(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID))) {
      compareResult = Objects.equals(getValue(), other.getValue());
    }
    return compareResult;
  }

}
