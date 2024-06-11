/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Cache to store the entity objects created
 * 
 * @author bne4cob
 */
final class EntityCache {

  /**
   * Map to track the entities, for reference
   */
  private final Map<String, List<Object>> entityMap = new ConcurrentHashMap<>();

  /**
   * Clear the cache
   */
  public void clear() {
    this.entityMap.clear();
  }

  /**
   * Adds the entity to the cache
   * 
   * @param type type of entity
   * @param entity the entity
   */
  public void addEntity(final String type, final Object entity) {
    getEntityList(type).add(entity);
  }

  /**
   * Returns the entities of the given type
   * 
   * @param type entity type
   * @return list of entities
   */
  public List<Object> getEntities(final String type) {
    return new ArrayList<Object>(getEntityList(type));
  }

  /**
   * Gets the entity list. List is created if not done already
   * 
   * @param type entity type
   * @return list of entities
   */
  private List<Object> getEntityList(final String type) {
    List<Object> thisEntList = this.entityMap.get(type);

    // Create a new list if list is null
    if (thisEntList == null) {
      thisEntList = new ArrayList<>();
      this.entityMap.put(type, thisEntList);
    }

    return thisEntList;
  }

}
