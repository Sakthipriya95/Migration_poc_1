/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;

import javax.persistence.EntityManagerFactory;

import com.bosch.caltool.testframe.exception.InvalidTestDataException;
import com.bosch.caltool.testframe.exception.TestDataException;


/**
 * Finds the value based on the data prvovided
 * 
 * @author bne4cob
 */
class ValueFinder {

  /**
   * Date format for date fields in test data
   */
  private static final String TD_DATE_FORMAT = "dd/mm/yyyy";

  /**
   * Reference provider
   */
  private final ReferenceObjectProvider refProvider;

  /**
   * Constructor
   * 
   * @param emf EntityManagerFactory
   * @param cache EntityCache
   */
  public ValueFinder(final EntityManagerFactory emf, final EntityCache cache) {
    this.refProvider = new ReferenceObjectProvider(emf, cache);
  }

  /**
   * Convert the text value in the file to value object as required in the entity. If the text is a reference, identify
   * the referenced object
   * 
   * @param propDes PropertyDescriptor
   * @param textValue text value in file
   * @return value object
   * @throws TestDataException on error
   */
  public Object getValueObject(final PropertyDescriptor propDes, final String textValue) throws TestDataException {

    // If value is is empty, set the value as null
    if ("".equals(textValue)) {
      return null;
    }

    // Fetch the reference value
    String txtVal = textValue;
    if (EntityReference.isReferenceValue(txtVal)) {
      Object refObj = this.refProvider.getReference(propDes, txtVal);

      // If reference object is same as the property type, then return the object, pass it for creation, from the text
      // value
      if ((null == refObj) || TestUtils.isEqual(refObj.getClass().getName(), propDes.getPropertyType().getName())) {
        return refObj;
      }
      txtVal = refObj.toString();
    }


    return createValueObject(propDes, txtVal);

  }

  /**
   * Create the value object from the text value
   * 
   * @param propDes PropertyDescriptor
   * @param txtVal text value
   * @return object
   * @throws InvalidTestDataException if the property type is not supported
   */
  private Object createValueObject(final PropertyDescriptor propDes, final String txtVal)
      throws InvalidTestDataException {

    Object valObj = null;

    // Only the below types of values are supported now.
    // For any new type, add the creation as a new 'case' clause
    switch (propDes.getPropertyType().toString()) {
      case "class java.lang.String":
        valObj = txtVal;
        break;

      case "class java.math.BigDecimal":
        valObj = new BigDecimal(txtVal);
        break;

      case "class java.lang.Long":
      case "long":
        valObj = Long.valueOf(txtVal);
        break;

      case "class java.sql.Timestamp":
        valObj = TestUtils.getTimestamp(txtVal, TD_DATE_FORMAT);
        break;

      default:
        throw new InvalidTestDataException("This type of value is not supported now - '" +
            propDes.getPropertyType().toString() + "', property - '" + propDes.getName() + "'");
    }

    return valObj;

  }
}
