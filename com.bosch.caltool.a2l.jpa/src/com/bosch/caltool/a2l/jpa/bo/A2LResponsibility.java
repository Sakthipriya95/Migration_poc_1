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
import com.bosch.caltool.apic.jpa.bo.PIDCA2l;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;

/**
 * @author rgo7cob
 */
public class A2LResponsibility extends AbstractA2LObject implements Comparable<A2LResponsibility> {


  /**
   * constructor
   *
   * @param dataProvider data provider
   * @param a2lRespID ID
   */
  public A2LResponsibility(final A2LDataProvider dataProvider, final Long a2lRespID) {
    super(dataProvider, a2lRespID);
    getDataCache().getA2lRespMap().put(a2lRespID, this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbA2lResp(getID()).getVersion();
  }


  /**
   * @return the pidcA2l object. Already loaded in cache.
   */
  public PIDCA2l getPidcA2l() {
    TPidcA2l tPidcA2l = getEntityProvider().getDbA2lResp(getID()).getTPidcA2l();
    return getDataProvider().getApicDataProvider().getAllPidcA2lMap().get(tPidcA2l.getPidcA2lId());

  }


  /**
   * @return the wp Type long
   */
  public Long getWpTypeId() {
    return getEntityProvider().getDbA2lResp(getID()).getWpTypeId();
  }


  /**
   * @return the wp root value.
   */
  public Long getWpRootId() {
    return getEntityProvider().getDbA2lResp(getID()).getWpRootId();
  }


  /**
   * @return the wp root value.
   */
  public Long getRespVarId() {
    return getEntityProvider().getDbA2lResp(getID()).getRespVarId();
  }

  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lResp(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbA2lResp(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lResp(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbA2lResp(getID()).getModifiedUser();
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
    return A2LEntityType.A2L_RESP;
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
  public int compareTo(final A2LResponsibility obj) {
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
