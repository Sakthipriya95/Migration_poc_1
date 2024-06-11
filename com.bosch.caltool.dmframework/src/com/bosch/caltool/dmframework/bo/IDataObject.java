/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.util.Calendar;
import java.util.Map;

import com.bosch.caltool.dmframework.notification.IEntityType;


/**
 * Interface of a data object
 *
 * @author bne4cob
 */
// ICDM-1830
@Deprecated
public interface IDataObject extends IBasicObject {

  /**
   * Returns the ID of this object. This is same as the primary key of the entity which it represents.
   *
   * @return the unique ID of this object.
   */
  Long getID();

  /**
   * @return Created User of this object or <code>null</code> if not applicable
   */
  // ICDM-1314
  String getCreatedUser();

  /**
   * @return Display name of created user of this object or <code>""</code> if not applicable
   */
  String getCreatedUserDisplayName();

  /**
   * @return Last Modified user of this object, or <code>null</code> if not applicable
   */
  // ICDM-1314
  String getModifiedUser();

  /**
   * @return Display name of last modified user of this object, or <code>""</code> if not applicable
   */
  String getModifiedUserDisplayName();

  /**
   * Get Created date of this object
   *
   * @return Created date of this object or <code>null</code> if not applicable
   */
  // ICDM-1314
  Calendar getCreatedDate();

  /**
   * Get created date as string
   *
   * @return the created date of this object as string. <code>""</code> if not applicable
   */
  String getCreatedDateAsString();

  /**
   * Get Last modified date of this object
   *
   * @return Last modified date of this object or <code>null</code> if not applicable or not available
   */
  // ICDM-1314
  Calendar getModifiedDate();

  /**
   * Get last modified date as string
   *
   * @return the last modified date of this object as string. <code>""</code> if not applicable or not available
   */
  String getModifiedDateAsString();


  /**
   * Get the Version of the database object
   *
   * @return the database version counter returns 0 if no version counter is supported
   */
  Long getVersion();

  /**
   * Checks whether this business object is valid by verifying the associated entity's availability in the database.
   *
   * @return true, if object is valid
   */
  boolean isValid();

  /**
   * Refresh the object by resetting the attributes, which are set during object's construction
   */
  void refresh();

  /**
   * Check, if the object can be modified by the current user
   *
   * @return TRUE if the object can be modified
   */
  boolean isModifiable();

  /**
   * Provides the details of this object in an map. Does not include changes in child entities, user details and date of
   * modification/creation
   *
   * @return map with key as a unique name and value as the object value
   */
  Map<String, String> getObjectDetails();

  /**
   * @return EntityType
   */
  IEntityType<?, ?> getEntityType();

  /**
   * Checks whether there is any change in this data object
   *
   * @param oldObjDetails summary of old data
   * @return true/false
   */
  boolean isModified(final Map<String, String> oldObjDetails);

}
