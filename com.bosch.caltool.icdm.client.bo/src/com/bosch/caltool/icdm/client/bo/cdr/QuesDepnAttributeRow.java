
/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

package com.bosch.caltool.icdm.client.bo.cdr;


import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;


/**
 * ICDM-2075 This class is a virtual structure(used in UI) to denote attributedependency for a question
 *
 * @author dmo5cob
 */
public class QuesDepnAttributeRow implements Comparable<QuesDepnAttributeRow> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  private final Attribute attribute;
  /**
   * Question
   */
  private final Question question;

  /**
   * This flag will be true only in case of adding a new attr dependency to the already existing set of att
   * dependencies(update)
   */
  private final boolean addedToExistingOnes;

  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Attr Name
                            */
                           SORT_ATTR_NAME,
                           /**
                            * Attr desc
                            */
                           SORT_ATTR_DESC,
                           /**
                            * Unit
                            */
                           SORT_UNIT
  }

  /**
   * @param question Question
   * @param attr
   * @param isAddedToExistingOnes
   */
  public QuesDepnAttributeRow(final Question question, final Attribute attr, final boolean isAddedToExistingOnes) {
    this.question = question;
    this.attribute = attr;
    this.addedToExistingOnes = isAddedToExistingOnes;
  }


  /**
   * @return the question
   */
  public Question getQuestion() {
    return this.question;
  }


  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return this.attribute;
  }


  /**
   * @return the isAddedToExistingOnes
   */
  public boolean isAddedToExistingOnes() {
    return this.addedToExistingOnes;
  }

  /**
   * {@inheritDoc} returns compare result of two attributes
   */
  @Override
  public int compareTo(final QuesDepnAttributeRow arg0) {
    // Compare attribute name, (if english not available, then german name is compared)
    int compResult = ApicUtil.compare(getAttribute().getName(), arg0.getAttribute().getName());
    // iCDM-1039; If name is same, then compare by attribute id
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getAttribute().getId(), arg0.getAttribute().getId());
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getAttribute() == null) ? 0 : (getAttribute().getId()).hashCode());
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
    QuesDepnAttributeRow other = (QuesDepnAttributeRow) obj;
    return CommonUtils.isEqual(getAttribute().getId(), other.getAttribute().getId());
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final QuesDepnAttributeRow arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_NAME:
        // attribute name needs not to be compared because it is the default sort
        compareResult = 0;
        break;

      case SORT_ATTR_DESC:
        compareResult = ApicUtil.compare(getAttribute().getDescription(), arg0.getAttribute().getDescription());
        break;

      case SORT_UNIT:
        compareResult = ApicUtil.compare(getAttribute().getUnit(), arg0.getAttribute().getUnit());
        break;

      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }
}
