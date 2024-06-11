/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.sorters;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Locale;

import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRule.SortColumns;


/**
 * @author rgo7cob
 */
public class ReviewRuleComparator {


  /**
   * Compare two String values Consider a NULL value as LESS THAN a not NULL value.
   *
   * @param name String
   * @param name2 String
   * @return compare result
   */
  public static int compare(final String name, final String name2) {

    if ((name == null) && (name2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    else if (name == null) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    else if (name2 == null) {
      // second String is NULL => return GREATER THAN
      return 1;
    }
    else {
      // both String are not NULL, compare them
      final Collator collator = Collator.getInstance(Locale.GERMAN);
      collator.setStrength(Collator.SECONDARY);
      return collator.compare(name, name2);
    }

  }

  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return int
   */
  public int compareTo(final ReviewRule rule1, final ReviewRule rule2, final SortColumns sortColumn) { // NOPMD by
                                                                                                       // mkl2cob on
                                                                                                       // 5/2/14 9:50 AM
    int compareResult;

    switch (sortColumn) {

      case SORT_PARAM_NAME:
        // use compare method for Strings
        compareResult = compare(rule1.getParameterName(), rule2.getParameterName());
        break;

      case SORT_VALUE_TYPE:
        // use compare method for Strings
        compareResult = compare(rule1.getValueType(), rule2.getValueType());
        break;

      case SORT_LOWER_LIMIT:
        // use compare method for Strings
        compareResult = compareBigDecimal(rule1.getLowerLimit(), rule2.getLowerLimit());
        break;

      case SORT_UPPER_LIMIT:
        // use compare method for Strings
        compareResult = compareBigDecimal(rule1.getUpperLimit(), rule2.getUpperLimit());
        break;
      case SORT_BITWISE_LIMIT:
        // use compare method for Strings
        compareResult = compare(rule1.getBitWiseRule(), rule2.getBitWiseRule());
        break;
      case SORT_REF_VALUE:
        // use compare method for Strings
        compareResult = compareBigDecimal(rule1.getRefValue(), rule2.getRefValue());
        break;
      case SORT_REVISION_ID:
        // use compare method for Strings
        compareResult = rule1.getRevId().compareTo(rule2.getRevId());
        break;
      case SORT_CREATED_USER:
        // use compare method for Strings
        compareResult = compare(rule1.getRuleCreatedUser(), rule2.getRuleCreatedUser());
        break;
      case SORT_CREATED_DATE:
        // use compare method for Strings
        compareResult = rule1.getRuleCreatedDate().compareTo(rule2.getRuleCreatedDate());
        break;
      case SORT_REVIEW_METHOD:
        // use compare method for Strings
        compareResult = compare(rule1.getReviewMethod(), rule2.getReviewMethod());
        break;
      case SORT_UNIT:
        // use compare method for strings
        compareResult = compare(rule1.getUnit(), rule2.getUnit());
        break;
      case SORT_EXACT_MATCH:
        // use compare method for boolean
        compareResult = compareBoolean(rule1.isDcm2ssd(), rule2.isDcm2ssd());
        break;
      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = compare(rule1.getParameterName(), rule2.getParameterName());
    }

    return compareResult;
  }

  /**
   * @param obj1 BigDecimal
   * @param obj2 BigDecimal
   * @return compare result
   */
  private int compareBigDecimal(final BigDecimal obj1, final BigDecimal obj2) {
    if ((obj1 == null) && (obj2 == null)) {
      // both object are NULL => return EQUAL
      return 0;
    }
    if (obj1 == null) {
      // first object is NULL => return LESS THAN
      return -1;
    }
    if (obj2 == null) {
      // second object is NULL => return GREATER THAN
      return 1;
    }
    return obj1.compareTo(obj2);
  }

  /**
   * Compare two boolean values Consider TRUE as less than FALSE this will order TRUE to top in ascending order
   *
   * @param value1 value1
   * @param value2 value2
   * @return compare result
   */
  public static int compareBoolean(final boolean value1, final boolean value2) {
    if (value1 == value2) {
      return 0;
    }
    else if (value1) {
      return -1;
    }
    else {
      return 1;
    }
  }

}
