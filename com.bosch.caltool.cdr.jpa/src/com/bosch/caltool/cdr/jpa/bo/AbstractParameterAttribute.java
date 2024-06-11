/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.ApicBOUtil;
import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.rules.bo.IParameterAttribute;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author adn1cob
 */
@Deprecated
public abstract class AbstractParameterAttribute extends AbstractCdrObject implements IParameterAttribute {


  /**
   * @param cdrDataProvider cdrDataProvider
   * @param objID objId
   */
  protected AbstractParameterAttribute(final AbstractDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Attribute getAttribute();

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public abstract AbstractParameter getParameter() throws SsdInterfaceException;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAttribute().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getAttribute().getDescription();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<AttributeValue> getAttributeValues() {
    return ApicBOUtil.getFeatureMappedAttrValues(getApicDataProvider(), getAttribute());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasAttrValues() {
    return !getAttributeValues().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IParameterAttribute paramAttr) {
    return ApicUtil.compare(getAttribute().getAttributeName(), paramAttr.getAttribute().getAttributeName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IParameterAttribute arg0, final int sortColumn) {
    int compareResult;

    switch (sortColumn) {
      // Sort by Attribute Name
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(getAttribute().getAttributeName(), arg0.getAttribute().getAttributeName());
        break;
      // Sort by Attribute Description
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getAttribute().getAttributeDesc(), arg0.getAttribute().getAttributeDesc());
        break;
      // Sort by Attribute Unit
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


}
