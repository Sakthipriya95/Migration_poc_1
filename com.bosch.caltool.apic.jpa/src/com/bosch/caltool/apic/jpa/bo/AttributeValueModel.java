/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * ICDM-1063 - This class holds the Attribute and AttributeValue objects corresponding to the feature and featureValue
 * in SSD
 *
 * @author dmo5cob
 */
public class AttributeValueModel implements Comparable<AttributeValueModel> {

  /**
   * Attribute
   */
  private final Attribute attribute;
  /**
   * Attribute Value
   */
  private final AttributeValue attrValue;

  /**
   * Error msg string ICDM-2019
   */
  private String errorMsg;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return this.attribute;
  }

  /**
   * @return the attrValue
   */
  public AttributeValue getAttrValue() {
    return this.attrValue;
  }


  /**
   * @param attr attr
   * @param attrValue attrValue
   */
  public AttributeValueModel(final Attribute attr, final AttributeValue attrValue) {
    this.attribute = attr;
    this.attrValue = attrValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttributeValueModel arg0) {

    int compareResult;

    compareResult = getAttribute().compareTo(arg0.getAttribute());
    // skip comparison of Dummy,Used,Not Used attributeValue objects
    if ((compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) && CommonUtils.isNotNull(arg0.getAttrValue()) &&
        (!arg0.getAttrValue().getID().equals(ApicConstants.ATTR_VAL_USED_VALUE_ID) &&
            !arg0.getAttrValue().getID().equals(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID))) {
      compareResult = getAttrValue().compareTo(arg0.getAttrValue());
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
        (((getAttribute() == null) || (getAttrValue() == null)) ? 0 : getAttrValue().getID().hashCode());
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
    boolean compareResult = CommonUtils.isEqual(getAttribute(), other.getAttribute());
    // skip comparison of Dummy,Used,Not Used attributeValue objects
    if (compareResult && CommonUtils.isNotNull(other.getAttrValue()) &&
        (!other.getAttrValue().getID().equals(ApicConstants.ATTR_VAL_USED_VALUE_ID) &&
            !other.getAttrValue().getID().equals(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID))) {
      compareResult = CommonUtils.isEqual(getAttrValue(), other.getAttrValue());
    }
    return compareResult;
  }

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


}
