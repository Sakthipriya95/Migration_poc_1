/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * Changed data object. Keeps all information of the data like type, primary key etc.
 *
 * @author BNE4COB
 */
public class ChangedData implements Comparable<ChangedData> {

  /**
   * number for hashcode generation
   */
  private static final int HASHCODE_KEY2 = 32;

  /**
   * Prime number for hashcode generation
   */
  private static final int PRIME = 31;

  /**
   * Primary key
   */
  private final long primaryKey;
  /**
   * Change type
   */
  private final ChangeType changeType;
  /**
   * Entity class
   */
  private final Class<?> entityClass;
  /**
   * Entity type
   */
  @SuppressWarnings("rawtypes")
  private IEntityType entityType;

  /**
   * Old data details
   */
  private Map<String, String> oldDataDetails;

  /**
   * Whether the change is directly on the entity
   */
  private boolean entityModified;

  /**
   * Source of this change
   */
  private final DisplayEventSource source;

  /**
   * Creates a new instance of this class
   *
   * @param changeType type of change - insert/update/delete
   * @param primaryKey the primary key
   * @param entityClass entity class
   * @param source source of this change
   */
  public ChangedData(final ChangeType changeType, final Long primaryKey, final Class<?> entityClass,
      final DisplayEventSource source) {
    super();
    this.primaryKey = primaryKey;
    this.changeType = changeType;
    for (IEntityType<?, ?> entType : ObjectStore.getInstance().getEntityTypes()) {
      if (entType.getEntityClass() == entityClass) {
        this.entityType = entType;
        break;
      }
    }

    this.entityClass = entityClass;
    this.source = source;
  }

  /**
   * @return the primaryKey
   */
  public final long getPrimaryKey() {
    return this.primaryKey;
  }

  /**
   * @return the changeType
   */
  public final ChangeType getChangeType() {
    return this.changeType;
  }

  /**
   * @return the entityClass
   */
  public final Class<?> getEntityClass() {
    return this.entityClass;
  }


  /**
   * @return the entityType
   */
  @SuppressWarnings("rawtypes")
  public final IEntityType getEntityType() {
    return this.entityType;
  }

  /**
   * Set the summary of the data prior to the change. To be used if the data is being updated
   *
   * @param oldDataDetails summary of old data
   */
  public void setOldDataDetails(final Map<String, String> oldDataDetails) {
    this.oldDataDetails = oldDataDetails;
  }


  /**
   * Get the old data details
   *
   * @return the oldDataDetails
   */
  public Map<String, String> getOldDataDetails() {
    return this.oldDataDetails;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (PRIME * result) + (int) (this.primaryKey ^ (this.primaryKey >>> HASHCODE_KEY2));
    return result;
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
    final ChangedData other = (ChangedData) obj;
    return this.primaryKey == other.primaryKey;
  }


  /**
   * Returns whether there are direct changes in the entity.
   *
   * @param dataProvider Data Provider
   * @return whether the change is in this entity or in one of its children
   */
  public boolean hasDirectChanges(final AbstractDataProvider dataProvider) {
    boolean returnVal;

    if (this.changeType == ChangeType.UPDATE) {
      if (this.entityModified) {
        returnVal = true;
      }
      else {
        @SuppressWarnings("unchecked")
        final AbstractDataObject dataObj = this.entityType.getDataObject(dataProvider, this.primaryKey);
        returnVal = dataObj == null ? true : dataObj.isModified(this.oldDataDetails);
      }

    }
    else {
      returnVal = true;
    }

    return returnVal;

  }

  /**
   * Returns the changes in this data object. If the change type is <code>INSERT</code> or <code>DELETE</code>, or if
   * <code>entityModified</code> field is set to <code>true</code>, the method returns <code>null</code>.
   *
   * @param dataProvider the data provider
   * @return map of changes. The key is the field id which has been updated, value is an array of string, with
   *         element[0] old value and element[1] the new value
   */
  public Map<String, String[]> getDirectChanges(final AbstractDataProvider dataProvider) {

    if (this.changeType == ChangeType.UPDATE) {
      if (this.entityModified) {
        return null;
      }

      @SuppressWarnings("unchecked")
      final AbstractDataObject dataObj = this.entityType.getDataObject(dataProvider, this.primaryKey);
      final Map<String, String> newDetails = dataObj.getObjectDetails();
      final ConcurrentMap<String, String[]> directChanges = new ConcurrentHashMap<String, String[]>();

      for (Entry<String, String> entry : newDetails.entrySet()) {
        if (ApicUtil.compare(entry.getValue(), this.oldDataDetails.get(entry.getKey())) != 0) {
          directChanges.put(entry.getKey(), new String[] { this.oldDataDetails.get(entry.getKey()), entry.getValue() }); // NOPMD
        }
      }
      return directChanges;

    }

    return null;


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ChangedData [Key=" + this.primaryKey + ", Type=" + this.entityType + ", Change Type=" + this.changeType +
        ", entityModified=" + this.entityModified + ", oldDataDetails=" + this.oldDataDetails + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ChangedData other) {

    if ((getEntityType() == null) && (other.getEntityType() == null)) {
      return ApicUtil.compareLong(getPrimaryKey(), other.getPrimaryKey());
    }
    else if (getEntityType() == null) {
      return -1;
    }
    else if (other.getEntityType() == null) {
      return 1;
    }

    int result = ApicUtil.compareLong(getEntityType().getOrder(), other.getEntityType().getOrder());
    if (result == 0) {
      result = ApicUtil.compareLong(getPrimaryKey(), other.getPrimaryKey());
    }
    return result;
  }


  /**
   * @return the source
   */
  public DisplayEventSource getSource() {
    return this.source;
  }

}
