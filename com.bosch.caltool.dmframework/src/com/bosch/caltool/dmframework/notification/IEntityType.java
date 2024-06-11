/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;


/**
 * Interface for entity types
 *
 * @param <D> implementation of abstract data object
 * @param <P> implementation of abstract data provider
 * @author bne4cob
 */
public interface IEntityType<D extends AbstractDataObject, P extends AbstractDataProvider> {

  /**
   * @return the entity class
   */
  Class<?> getEntityClass();

  /**
   * Returns the data object identified by the primary key
   *
   * @param dataProvider
   * @param primaryKey
   * @return the data object
   */
  D getDataObject(P dataProvider, Long primaryKey);

  /**
   * @return the order number
   */
  int getOrder();

  /**
   * Returns the version of the entity identified by the entity ID
   *
   * @param dataProvider the data provider
   * @param entityID the unique entity ID
   * @return the version
   */
  long getVersion(P dataProvider, Long entityID);

  /**
   * @return Entity type name
   */
  String getEntityTypeString();


  /**
   * @return true, if the command execution should be blocked when this entity is updated by another client
   */
  boolean stopCommandForEntityUpdate();

}
