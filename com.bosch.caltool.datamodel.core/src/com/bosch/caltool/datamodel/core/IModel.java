/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;

import java.io.Serializable;

/**
 * @author bne4cob
 */
public interface IModel extends Cloneable, Serializable {

  /**
   * Returns the ID of this object. This is same as the primary key of the entity which it represents.
   *
   * @return the unique ID of this object.
   */
  Long getId();

  /**
   * Sets the ID of this object. This is same as the primary key of the entity which it represents.
   *
   * @param objId the unique ID of this object.
   */
  void setId(Long objId);

  /**
   * Get the Version of the database object
   *
   * @return the database version counter
   */
  Long getVersion();

  /**
   * Get the Version of the database object
   *
   * @param version the database version counter
   */
  void setVersion(Long version);
}
