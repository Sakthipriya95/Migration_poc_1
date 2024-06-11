/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.notification.INotificationListener;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * Abstract class for all data model objects
 *
 * @author he2fe
 */
@Deprecated
public abstract class AbstractDataObject implements IDataObject {

  /**
   * reference to the data provider
   */
  private final AbstractDataProvider dataProvider;
  /**
   * List of Notification listeners
   */
  private List<INotificationListener> listenerList;

  /**
   * Unique ID
   */
  private final Long objID;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @param dataProvider data provider
   * @param objID unique ID of the object. Same as the primary key of the entity which it represents
   */
  protected AbstractDataObject(final AbstractDataProvider dataProvider, final Long objID) {
    this.dataProvider = dataProvider;
    this.objID = objID;
  }

  /**
   * Returns the ID of this object. This is same as the primary key of the entity which it represents.
   *
   * @return the unique ID of this object.
   */
  @Override
  public Long getID() {
    return this.objID;
  }

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
   * Check, if the object can be modified by the current user
   *
   * @return TRUE if the object can be modified
   */
  @Override
  public boolean isModifiable() {
    return false;
  }

  /**
   * Add notification listener to this data model object
   *
   * @param listener notification listener
   */
  @Deprecated
  public final void addNotificationListener(final INotificationListener listener) {
    if (this.listenerList == null) {
      this.listenerList = new ArrayList<INotificationListener>();
    }
    this.listenerList.add(listener);
  }

  /**
   * @return the list of notification listeners
   */
  @Deprecated
  public final List<INotificationListener> getNotificationListeners() {
    if (this.listenerList == null) {
      this.listenerList = new ArrayList<INotificationListener>();
    }
    return this.listenerList;
  }

  /**
   * remove notification listener from this data model object
   *
   * @param listener notification listener
   */
  @Deprecated
  public final void removeNotificationListener(final INotificationListener listener) {
    if (this.listenerList == null) {
      return;
    }
    this.listenerList.remove(listener);
  }

  /**
   * Provides the details of this object in an map. Does not include changes in child entities, user details and date of
   * modification/creation
   *
   * @return map with key as a unique name and value as the object value
   */
  @Override
  public Map<String, String> getObjectDetails() {
    // TODO to be made as abstract method
    return new HashMap<String, String>();
  }

  /**
   * Checks whether there is any change in this data object
   *
   * @param oldObjDetails summary of old data
   * @return true/false
   */
  @Override
  public boolean isModified(final Map<String, String> oldObjDetails) {
    if (oldObjDetails == null) {
      return true;
    }
    final Map<String, String> newObjDetails = getObjectDetails();
    String newValue;
    String oldValue;
    int compareResult;
    for (Entry<String, String> entry : newObjDetails.entrySet()) {
      newValue = entry.getValue();
      oldValue = oldObjDetails.get(entry.getKey());

      compareResult = ApicUtil.compare(newValue, oldValue);
      if (compareResult != 0) {
        return true;
      }
    }
    // TODO RETURN true/false???
    return false;
  }

  /**
   * Refresh the object by resetting the properties, which are set during object's construction
   */
  @Override
  public void refresh() {
    // Default implemenation. No action taken
  }

  /**
   * @return the dataLoader
   */
  protected AbstractDataLoader getDataLoader() {
    return this.dataProvider.getDataLoader();
  }

  /**
   * @return the dataCache
   */
  protected AbstractDataCache getDataCache() {
    return this.dataProvider.getDataCache();
  }

  /**
   * @return the entityProvider
   */
  protected AbstractEntityProvider getEntityProvider() {
    return this.dataProvider.getEntityProvider();
  }

  /**
   * @return the dataProvider
   */
  protected AbstractDataProvider getDataProvider() {
    return this.dataProvider;
  }

  /**
   * @return the logger
   */
  protected final ILoggerAdapter getLogger() {
    return this.dataProvider.getLogger();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return CommonUtils.isEqual(getID(), ((AbstractDataObject) obj).getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getID() == null) ? 0 : getID().hashCode());
    return result;
  }


  /**
   * Returns tooltip as a combination of name and description. Description will be added, only if it is not
   * <code>null</code>
   * <p>
   * Format of tooltip
   * <p>
   * <code>
   * Name : &ltObject Name&gt<br>
   * Description : &ltObject Description&gt
   * </code>
   *
   * @return tooltip as a combination of name and description
   */
  // ICDM-1314
  @Override
  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder("Name : ");
    toolTip.append(getName());

    String desc = getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }
    return toolTip.toString();
  }

  /**
   * Checks whether this business object is valid by verifying the associated entity's availability in the database.
   *
   * @return true, if object is valid
   */
  @Override
  public boolean isValid() {
    boolean ret = false;
    if (getEntityType() != null) {
      Class<?> entClass = getEntityType().getEntityClass();
      if (entClass != null) {
        Object entity = getEntityProvider().getEm().find(entClass, getID());
        ret = entity != null;
      }
    }
    return ret;
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
