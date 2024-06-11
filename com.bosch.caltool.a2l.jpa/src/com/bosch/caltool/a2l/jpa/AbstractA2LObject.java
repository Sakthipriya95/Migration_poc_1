/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import java.util.Calendar;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;

/**
 * @author rgo7cob
 */
@Deprecated
public abstract class AbstractA2LObject extends AbstractDataObject {


  /**
   * Constructor
   *
   * @param a2lDataProvider Data Provider
   * @param objID Object ID
   */
  protected AbstractA2LObject(final A2LDataProvider a2lDataProvider, final Long objID) {
    super(a2lDataProvider, objID);

  }


  /**
   * @return the dataProvider
   */
  @Override
  public A2LDataProvider getDataProvider() {
    return (A2LDataProvider) super.getDataProvider();
  }


  /**
   * @return the datacache
   */
  @Override
  protected A2LDataCache getDataCache() {
    return (A2LDataCache) super.getDataCache();
  }


  /**
   * @return the entity provider
   */
  @Override
  protected A2LEntityProvider getEntityProvider() {
    return (A2LEntityProvider) super.getEntityProvider();
  }

  /**
   * Returns the ID of this object. This is same as the primary key of the entity which it represents.
   *
   * @return the unique ID of this object.
   */


  /**
   * Get the Version of the database object
   *
   * @return the database version counter returns 0 if no version counter is supported
   */
  @Override
  public Long getVersion() {
    return Long.valueOf(0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDateAsString() {
    return calToString(getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDateAsString() {
    return calToString(getModifiedDate());
  }

  /**
   * @param date calendar
   * @return convert calendar to string, "" if input is null
   */
  private String calToString(final Calendar date) {
    return date == null ? "" : date.getTime().toString();
  }

}
