/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Attribute value types
 *
 * @author bne4cob
 */
public enum AttributeValueType {

                                /**
                                 * Text
                                 */
                                TEXT(ApicConstants.ATTR_VALUE_TYPE_TEXT, "TEXT"),
                                /**
                                 * Number
                                 */
                                NUMBER(ApicConstants.ATTR_VALUE_TYPE_NUMBER, "NUMBER"),
                                /**
                                 * Date
                                 */
                                DATE(ApicConstants.ATTR_VALUE_TYPE_DATE, "DATE"),
                                /**
                                 * Boolean
                                 */
                                BOOLEAN(ApicConstants.ATTR_VALUE_TYPE_BOOLEAN, "BOOLEAN"),
                                /**
                                 * Hyperlink
                                 */
                                HYPERLINK(ApicConstants.ATTR_VALUE_TYPE_HYPERLINK, "HYPERLINK"),
                                /**
                                 * ICDM_USER
                                 */
                                ICDM_USER(ApicConstants.ATTR_VALUE_TYPE_ICDM_USER, "ICDM_USER");

  /**
   * Type ID
   */
  private long typeID;
  /**
   * Display Text of type
   */
  private String displayText;

  /**
   * Constructor to initialize the fields
   *
   * @param typeID type ID
   * @param displayText display Text
   */
  AttributeValueType(final int typeID, final String displayText) {
    this.typeID = typeID;
    this.displayText = displayText;
  }

  /**
   * @return type ID
   */
  public long getValueTypeID() {
    return this.typeID;
  }

  /**
   * @return Display text
   */
  public String getDisplayText() {
    return this.displayText;
  }

  /**
   * Find type <code>AttributeValueType</code> enum value using type ID
   *
   * @param typeID input type ID
   * @return enum value
   */
  public static AttributeValueType getType(final long typeID) {
    AttributeValueType ret = null;
    for (AttributeValueType type : AttributeValueType.values()) {
      if (type.typeID == typeID) {
        ret = type;
        break;
      }
    }
    return ret;
  }

  /**
   * Find type <code>AttributeValueType</code> enum value using display text
   *
   * @param displayText input display text
   * @return enum value
   */
  public static AttributeValueType getType(final String displayText) {
    AttributeValueType ret = null;
    for (AttributeValueType type : AttributeValueType.values()) {
      if (type.displayText.equalsIgnoreCase(displayText)) {
        ret = type;
        break;
      }
    }
    return ret;
  }
}
