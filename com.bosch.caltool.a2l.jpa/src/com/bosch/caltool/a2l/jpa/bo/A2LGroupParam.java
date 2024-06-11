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
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;

/**
 * @author rgo7cob
 */
public class A2LGroupParam extends AbstractA2LObject implements Comparable<A2LGroupParam> {


  /**
   * constructor
   *
   * @param dataProvider data provider
   * @param grpParamID ID
   */
  public A2LGroupParam(final A2LDataProvider dataProvider, final Long grpParamID) {
    super(dataProvider, grpParamID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbA2lGrpParam(getID()).getVersion();
  }


  /**
   * @return the A2l Group object
   */
  public ICDMA2LGroup getA2lGrp() {
    TA2lGroup ta2lGroup = getEntityProvider().getDbA2lGrpParam(getID()).getTA2lGroup();
    return getDataCache().getA2lGrpMap().get(ta2lGroup.getGroupId());
  }

  /**
   * @return the parameter id beacuse
   */
  public Long getParamID() {
    TParameter tParameter = getEntityProvider().getDbA2lGrpParam(getID()).getTParameter();
    return tParameter.getId();
  }

  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lGrpParam(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbA2lGrpParam(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lGrpParam(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbA2lGrpParam(getID()).getModifiedUser();
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
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return A2LEntityType.A2L_GRP_PARAM;
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
  public int compareTo(final A2LGroupParam obj) {
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
  public String getCreatedUserDisplayName() {
    // Not applicable
    return null;
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
