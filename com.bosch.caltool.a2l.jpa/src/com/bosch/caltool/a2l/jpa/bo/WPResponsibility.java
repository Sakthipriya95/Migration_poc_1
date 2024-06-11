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
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * @author rgo7cob
 */
public class WPResponsibility extends AbstractA2LObject implements Comparable<WPResponsibility> {


  /**
   * constructor
   *
   * @param dataProvider data provider
   * @param wpRespID ID
   */
  public WPResponsibility(final A2LDataProvider dataProvider, final Long wpRespID) {
    super(dataProvider, wpRespID);
    getDataCache().getWpRespMap().put(wpRespID, this);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbWpResp(getID()).getVersion();
  }


  /**
   * @return the Wp Response Enum.
   */
  public CDRConstants.WPResponsibilityEnum getWpRespEnum() {
    return CDRConstants.WPResponsibilityEnum.getType(getWpRespStr());
  }

  /**
   * @return the wp Response String
   */
  public String getWpRespStr() {
    return getEntityProvider().getDbWpResp(getID()).getRespName();
  }


  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbWpResp(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbWpResp(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbWpResp(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbWpResp(getID()).getModifiedUser();
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
    return getWpRespStr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return A2LEntityType.WP_RESP;
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
  public int compareTo(final WPResponsibility obj) {
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
