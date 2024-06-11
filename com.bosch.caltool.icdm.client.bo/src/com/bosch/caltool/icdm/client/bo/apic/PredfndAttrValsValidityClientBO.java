/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;


import java.util.Map;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * This class represents predefined attribute value validity BO
 *
 * @author dmo5cob
 */
public class PredfndAttrValsValidityClientBO implements Comparable<PredfndAttrValsValidityClientBO> {


  private final Long id;
  private final PredefinedValidity preDfndValdty;
  private final Map<Long, Attribute> allAttrsMap;

  /**
   * constructor
   *
   * @param preDfndValdty PredefinedValidity
   * @param allAttributes all attrs
   */
  public PredfndAttrValsValidityClientBO(final PredefinedValidity preDfndValdty,
      final Map<Long, Attribute> allAttributes) {
    this.id = preDfndValdty.getId();
    this.preDfndValdty = preDfndValdty;
    this.allAttrsMap = allAttributes;
  }


  /**
   * Get the grouped attribute's value
   *
   * @return Attribute object
   */
  public AttributeValue getGroupedAttrValue() {
    this.preDfndValdty.getGrpAttrValId();

    return null;
  }

  /**
   * Get the validity Attribute
   *
   * @return Attribute object
   */
  public Attribute getValidityAttribute() {
    return this.allAttrsMap.get(this.preDfndValdty.getValidityAttrId());
  }

  /**
   * Get the predefined Attribute Value
   *
   * @return AttributeValue object
   */
  public AttributeValue getValidityAttributeValue() {
    return new AttributeClientBO(getValidityAttribute()).getAttrValuesMap()
        .get(this.preDfndValdty.getValidityValueId());
  }


  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final PredfndAttrValsValidityClientBO arg0) {
    return ApicUtil.compare(getName(), arg0.getName());
  }

  /**
   * @return
   */
  private String getName() {
    return getValidityAttribute().getName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      return ModelUtil.isEqual(getValidityAttributeValue().getName(),
          ((PredfndAttrValsValidityClientBO) obj).getValidityAttributeValue().getName());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getValidityAttributeValue().getName());
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final PredfndAttrValsValidityClientBO arg0, final int sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getDescription(), arg0.getDescription());
        break;
      // Value name needs not to be compared because it is the default sort
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the Value name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }


  /**
   * @return
   */
  private String getDescription() {
    return getValidityAttribute().getDescription();
  }


  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }


  /**
   * @return the preDfndValdty
   */
  public PredefinedValidity getPreDfndValdty() {
    return this.preDfndValdty;
  }


}