/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.Calendar;
import java.util.Map;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Icdm-1032 Data model object for mapping between parameter and attribute
 *
 * @author rgo7cob Spelling Change made
 */
public class ParameterAttribute extends AbstractParameterAttribute implements Comparable<ParameterAttribute> {


  /**
   * the one and only constructor
   *
   * @param cdrDataProvider data provider
   * @param paramAttrID ID
   */
  public ParameterAttribute(final CDRDataProvider cdrDataProvider, final Long paramAttrID) {
    super(cdrDataProvider, paramAttrID);
  }


  /**
   * return the version {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbParamAttr(getID()).getVersion();
  }


  /**
   * Icdm-1032 Get the Attribute for the Param Attribute
   *
   * @return List of Char Values
   */
  @Override
  public Attribute getAttribute() {
    final TabvAttribute tabvAttribute = getEntityProvider().getDbParamAttr(getID()).getTabvAttribute();
    // Since Attribute Id cannot be null. (Not null for Attr ID)
    return getApicDataProvider().getAttribute(tabvAttribute.getAttrId());

  }


  /**
   * Get the Cdr Function paramter
   *
   * @return function
   */
  @Override
  public CDRFuncParameter getParameter() {
    final TParameter tParameter = getEntityProvider().getDbParamAttr(getID()).getTParameter();
    // Since param Id cannot be null.((Not null for param ID))
    return getDataCache().getCDRFuncParameter(tParameter.getName(), tParameter.getPtype());

  }


  /**
   * Get the creation date of the Char
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbParamAttr(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Char
   *
   * @return The ID of the user who has created the Char
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbParamAttr(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Get the date when the Char has been modified the last time
   *
   * @return The date when the Char has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbParamAttr(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Char the last time
   *
   * @return The user who has modified the Char the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbParamAttr(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final ParameterAttribute paramAttr) {
    return ApicUtil.compare(getAttribute().getAttributeName(), paramAttr.getAttribute().getAttributeName());
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final ParameterAttribute arg0, final int sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(getAttribute().getAttributeName(), arg0.getAttribute().getAttributeName());
        break;
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getAttribute().getAttributeDesc(), arg0.getAttribute().getAttributeDesc());
        break;
      case ApicConstants.SORT_UNIT:
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

  /**
   * {@inheritDoc} return object details in Map Not implemanted as of now.
   */
  @Override
  public Map<String, String> getObjectDetails() {
    // Not implemanted as of now.
    return null;
  }


  /**
   * {@inheritDoc} Entity Type Param Attr
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.PARAMETER_ATTR;
  }


}