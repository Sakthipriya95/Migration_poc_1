/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import java.util.List;

import com.bosch.caltool.apic.jpa.bo.AttrGroup;
import com.bosch.caltool.apic.jpa.bo.AttrSuperGroup;
import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.comparator.AbstractCompareResult;


/**
 * @author jvi6cob
 */
public class PidcRevisionCompareResult extends AbstractCompareResult implements Comparable<PidcRevisionCompareResult> {

  /**
   * Holds the Attribute details
   */
  private Attribute attr;

  /**
   * Columns (used, value...) of Apic object 1
   */
  private CompareColumns firstColumnSet;

  /**
   * Columns (used, value...) of Apic object 2
   */
  private CompareColumns secondColumnSet;

  /**
   * Indicates if used flag is different ( to color in red)
   */
  private boolean isUsedFlagDiff;

  /**
   * Indicates if value is different ( to color in red)
   */
  private boolean isValueDiff;
  /**
   * Indicates if part number is different ( to color in red)
   */
  private boolean partNumberDiff;
  /**
   * Indicates if Spec Link is different ( to color in red)
   */
  private boolean specLinkDiff;
  /**
   * Indicates if Desc is different ( to color in red)
   */
  private boolean descDiff;
  /**
   * Variant Subvariant map
   */
  private List<PidcRevisionVariantCompareResult> pidcRevVarCompResults;

  /**
   * @return the Attr
   */
  public Attribute getAttribute() {
    return this.attr;
  }
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   * @param attr the Attr to set
   */
  public void setAttribute(final Attribute attr) {
    this.attr = attr;
  }

  /**
   * @return the firstColumnSet
   */
  public CompareColumns getFirstColumnSet() {
    return this.firstColumnSet;
  }


  /**
   * @param columnSet1 the firstColumnSet to set
   */
  public void setFirstColumnSet(final CompareColumns columnSet1) {
    this.firstColumnSet = columnSet1;
  }


  /**
   * @return the columnSet2
   */
  public CompareColumns getSecondColumnSet() {
    return this.secondColumnSet;
  }


  /**
   * @param columnSet2 the columnSet2 to set
   */
  public void setSecondColumnSet(final CompareColumns columnSet2) {
    this.secondColumnSet = columnSet2;
  }


  /**
   * @return the isUsedFlagDiff
   */
  public boolean isAttrUsedFlagDiff() {
    return this.isUsedFlagDiff;
  }


  /**
   * @param isUsedFlagDiff the isUsedFlagDiff to set
   */
  public void setAttrUsedFlagDiff(final boolean isUsedFlagDiff) {
    this.isUsedFlagDiff = isUsedFlagDiff;
    // If used falg is different, set the diff to true
    if (isUsedFlagDiff) {
      setDiff(true);
    }
  }


  /**
   * @return the isValueDiff
   */
  public boolean isAttrValueDiff() {
    return this.isValueDiff;
  }


  /**
   * @param isValueDiff the isValueDiff to set
   */
  public void setAttrValueDiff(final boolean isValueDiff) {
    this.isValueDiff = isValueDiff;
    // If value is different, set the diff to true
    if (isValueDiff) {
      setDiff(true);
    }
  }

  /**
   * @return the isPartNumberDiff
   */
  public boolean isPartNumberDiff() {
    return this.partNumberDiff;
  }


  /**
   * @param isPartNumberDiff the isPartNumberDiff to set
   */
  public void setPartNumberDiff(final boolean isPartNumberDiff) {
    this.partNumberDiff = isPartNumberDiff;
    // If part number is different, set the diff to true
    if (this.partNumberDiff) {
      setDiff(true);
    }
  }


  /**
   * @return the isSpecLinkDiff
   */
  public boolean isSpecLinkDiff() {
    return this.specLinkDiff;
  }


  /**
   * @param isSpecLinkDiff the isSpecLinkDiff to set
   */
  public void setSpecLinkDiff(final boolean isSpecLinkDiff) {
    this.specLinkDiff = isSpecLinkDiff;
    // If spec link is different, set the diff to true
    if (this.specLinkDiff) {
      setDiff(true);
    }
  }


  /**
   * @return the isDescDiff
   */
  public boolean isDescDiff() {
    return this.descDiff;
  }


  /**
   * @param isDescDiff the isDescDiff to set
   */
  public void setDescDiff(final boolean isDescDiff) {
    this.descDiff = isDescDiff;
    // If spec link is different, set the diff to true
    if (this.descDiff) {
      setDiff(true);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcRevisionCompareResult arg0) {
    // Enable sorting in order by - SuperGroup, Group, Atrribute names.
    // First get group and superGroup info
    AttrGroup group1 = this.attr.getAttributeGroup();
    AttrGroup group2 = arg0.getAttribute().getAttributeGroup();
    AttrSuperGroup superGroup1 = group1.getSuperGroup();
    AttrSuperGroup superGroup2 = group2.getSuperGroup();
    // use datamodel compareTo methods
    int value1 = superGroup1.compareTo(superGroup2);
    if (value1 == 0) {
      int value2 = group1.compareTo(group2);
      if (value2 == 0) {
        return this.attr.compareTo(arg0.getAttribute());
      }
      return value2;
    }
    return value1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getAttribute().getAttributeGroup().getSuperGroup() == null) ? 0 : (getAttribute().getAttributeGroup().getSuperGroup()).hashCode());
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
    PidcCompareResult other = (PidcCompareResult) obj;
    AttrGroup group1 = this.attr.getAttributeGroup();
    AttrGroup group2 = other.getAttribute().getAttributeGroup();
    AttrSuperGroup superGroup1 = group1.getSuperGroup();
    AttrSuperGroup superGroup2 = group2.getSuperGroup();
    return superGroup1.equals(superGroup2);
  }

  /**
   * @return
   */
  public List<PidcRevisionVariantCompareResult> getPidcRevisionVariantCompareResults() {
    return this.pidcRevVarCompResults;
  }


  /**
   * @param pidcRevVariantCompareResults
   */
  public void setPidcRevisionVariantCompareResults(
      final List<PidcRevisionVariantCompareResult> pidcRevVariantCompareResults) {
    this.pidcRevVarCompResults = pidcRevVariantCompareResults;
  }


}
