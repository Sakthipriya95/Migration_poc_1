/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.entity;

import java.sql.Timestamp;

/**
 * Interface for common database entity classes. Provides structure for similiar properties.
 *
 * @author bne4cob
 */
public interface IEntity {

  /**
   * @return Created User of this entity
   */
  String getCreatedUser();

  /**
   * @return Last Modified user of this entity
   */
  String getModifiedUser();

  /**
   * Get Created date of this object
   *
   * @return Created date of this entity
   */
  Timestamp getCreatedDate();

  /**
   * Get Last modified date of this object
   *
   * @return Last modified date of this object or <code>null</code> if not applicable or not available
   */
  Timestamp getModifiedDate();

  /**
   * Get the Version of the database object
   *
   * @return the database version counter returns 0 if no version counter is supported
   */
  long getVersion();

}
