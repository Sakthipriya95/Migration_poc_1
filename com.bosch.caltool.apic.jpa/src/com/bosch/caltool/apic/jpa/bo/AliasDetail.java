/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;

/**
 * This class represents BO for alias detail
 *
 * @author rgo7cob
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AliasDetail extends ApicObject implements Comparable<AliasDetail> {

  /**
   * @author rgo7cob
   */
  public static enum ALIAS_DETAIL_TYPE {

                                        /**
                                         * attribute alias
                                         */
                                        ATTRIBUTE_ALIAS,
                                        /**
                                         * Value alias
                                         */
                                        VALUE_ALIAS;
  }


  // NOPMD by


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param aliasDetail ID
   */
  public AliasDetail(final ApicDataProvider apicDataProvider, final Long aliasDetail) {
    super(apicDataProvider, aliasDetail);
    getDataCache().getAliasDetailMap().put(aliasDetail, this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbAliasDetail(getID()).getVersion();
  }

  /**
   * Get the creation date of the Char
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbAliasDetail(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Char
   *
   * @return The ID of the user who has created the Char
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbAliasDetail(getID()).getCreatedUser();
  }

  /**
   * Get the date when the Char has been modified the last time
   *
   * @return The date when the Char has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbAliasDetail(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Char the last time
   *
   * @return The user who has modified the Char the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbAliasDetail(getID()).getModifiedUser();
  }

  /**
   * @return the alias definition for the alias Detail
   */
  public AliasDefinition getAliasDefinition() {
    TAliasDefinition tAliasDefinition = getEntityProvider().getDbAliasDetail(getID()).getTAliasDefinition();
    return getDataCache().getAliasDefMap().get(tAliasDefinition.getAdId());
  }

  /**
   * @return the attribute for the alias detail if it is value then return the value's attribute.
   */
  public Attribute getAttribute() {
    TabvAttribute tabvAttribute = getEntityProvider().getDbAliasDetail(getID()).getTabvAttribute();
    if (tabvAttribute == null) {
      tabvAttribute = getEntityProvider().getDbAliasDetail(getID()).getTabvAttrValue().getTabvAttribute();
    }
    return getDataCache().getAttribute(tabvAttribute.getAttrId());

  }

  /**
   * @return the attribute value for the alias detail
   */
  public AttributeValue getAttrValue() {
    TabvAttrValue tabvAttrValue = getEntityProvider().getDbAliasDetail(getID()).getTabvAttrValue();
    if (tabvAttrValue == null) {
      return null;
    }
    return getDataCache().getAttrValue(tabvAttrValue.getValueId());
  }


  /**
   * @return the attribute alias name
   */
  public String getAliasName() {
    return getEntityProvider().getDbAliasDetail(getID()).getAliasName();
  }

  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final AliasDetail arg0) {

    return ApicUtil.compare(getName(), arg0.getName());
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
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    return new ConcurrentHashMap<>();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAliasDefinition().getAliasDefName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.ALIAS_DETAIL;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return the alias detail type value or attribute alias type
   */
  public ALIAS_DETAIL_TYPE getAliasType() {
    if (getEntityProvider().getDbAliasDetail(getID()).getTabvAttribute() == null) {
      return ALIAS_DETAIL_TYPE.VALUE_ALIAS;
    }
    return ALIAS_DETAIL_TYPE.ATTRIBUTE_ALIAS;
  }


}