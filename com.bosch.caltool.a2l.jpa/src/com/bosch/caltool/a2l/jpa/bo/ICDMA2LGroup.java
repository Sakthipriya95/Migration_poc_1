/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.a2l.jpa.A2LEntityType;
import com.bosch.caltool.a2l.jpa.AbstractA2LObject;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;

/**
 * @author rgo7cob
 */
@Deprecated
public class ICDMA2LGroup extends AbstractA2LObject implements Comparable<ICDMA2LGroup> {


  /**
   * constructor
   *
   * @param dataProvider data provider
   * @param a2GrpID ID
   */
  public ICDMA2LGroup(final A2LDataProvider dataProvider, final Long a2GrpID) {
    super(dataProvider, a2GrpID);
    getDataCache().getA2lGrpMap().put(a2GrpID, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbA2lGrp(getID()).getVersion();
  }


  /**
   * @return the group Name
   */
  public String getGroupName() {
    return getEntityProvider().getDbA2lGrp(getID()).getGrpName();
  }


  /**
   * @return the group Name
   */
  public String getLongName() {
    return getEntityProvider().getDbA2lGrp(getID()).getGrpLongName();
  }


  /**
   * @return the root attr Val
   */
  public AttributeValue getRootAttrVal() {
    TabvAttrValue tabvAttrValue = getEntityProvider().getDbA2lGrp(getID()).getTabvAttrValue();
    return getDataProvider().getApicDataProvider().getAttrValue(tabvAttrValue.getValueId());

  }

  /**
   * @return the attr root val id
   */
  public Long getRootAttrValID() {
    return getRootAttrVal() == null ? null : getRootAttrVal().getValueID();

  }

  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lGrp(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbA2lGrp(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lGrp(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbA2lGrp(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();
    return objDetails;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getGroupName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return A2LEntityType.A2L_GROUP;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUserDisplayName() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ICDMA2LGroup obj) {
    return ApicUtil.compare(getID(), obj.getID());
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
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUserDisplayName() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() {
    // Not applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {

    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModified(final Map<String, String> oldObjDetails) {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    return null;
  }

}
