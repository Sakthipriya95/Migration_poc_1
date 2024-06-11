/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * ICDM-1836 Business object for mandatory attribute
 * 
 * @author mkl2cob
 */
public class MandatoryAttr extends ApicObject {


  /**
   * @param apicDataProvider AbstractDataProvider
   * @param objID Long
   * @param valueId Long
   */
  protected MandatoryAttr(final AbstractDataProvider apicDataProvider, final Long objID, final Long valueId) {
    super(apicDataProvider, objID);
    // fill the mandatory attr objects map in data cache
    getDataCache().getMandatoryAttrObjectsMap().put(valueId, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getEntityProvider().getDbMandatoryAttr(getID()).getTabvAttribute().getAttrDescEng();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.MANDATORY_ATTR;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getEntityProvider().getDbMandatoryAttr(getID()).getTabvAttribute().getAttrDescEng();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbMandatoryAttr(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbMandatoryAttr(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbMandatoryAttr(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbMandatoryAttr(getID()).getModifiedDate());
  }

  /**
   * @return the attrVal
   */
  public AttributeValue getDefAttrVal() {
    return getDataCache().getAttrValue(getEntityProvider().getDbMandatoryAttr(getID()).getTabvAttrValue().getValueId());
  }

  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return getDataCache().getAttribute(getEntityProvider().getDbMandatoryAttr(getID()).getTabvAttribute().getAttrId());
  }
}
