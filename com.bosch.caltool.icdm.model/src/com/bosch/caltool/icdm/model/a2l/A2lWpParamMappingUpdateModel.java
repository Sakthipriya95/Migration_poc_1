/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author pdh2cob
 */
public class A2lWpParamMappingUpdateModel {

  /**
   * Set of A2lWpParamMapping objects
   */
  private Set<A2lWpParamMapping> a2lWpParamMappingToBeCreated = new HashSet<>();

  /**
   * Key - A2lWpParamMapping ID, Value - A2lWpParamMapping object
   */
  private Map<Long, A2lWpParamMapping> a2lWpParamMappingToBeUpdated = new HashMap<>();

  /**
   * Key - A2lWpParamMapping ID, Value - A2lWpParamMapping object
   */
  private Map<Long, A2lWpParamMapping> a2lWpParamMappingToBeDeleted = new HashMap<>();

  /**
   * Key - A2lWpParamMapping ID, Value - A2lWpParamMapping object
   */
  private Map<Long, A2lWpParamMapping> createdA2lWpParamMapping = new HashMap<>();

  /**
   * Key - A2lWpParamMapping ID, Value - A2lWpParamMapping object
   */
  private Map<Long, A2lWpParamMapping> updatedA2lWpParamMapping = new HashMap<>();

  /**
   * Key - A2lWpParamMapping ID, Value - A2lWpParamMapping object
   */
  private Map<Long, A2lWpParamMapping> deletedA2lWpParamMapping = new HashMap<>();


  /**
   * @return the a2lWpParamMappingToBeCreated
   */
  public Set<A2lWpParamMapping> getA2lWpParamMappingToBeCreated() {
    return this.a2lWpParamMappingToBeCreated;
  }


  /**
   * @param a2lWpParamMappingToBeCreated the a2lWpParamMappingToBeCreated to set
   */
  public void setA2lWpParamMappingToBeCreated(final Set<A2lWpParamMapping> a2lWpParamMappingToBeCreated) {
    this.a2lWpParamMappingToBeCreated = a2lWpParamMappingToBeCreated;
  }


  /**
   * @return the a2lWpParamMappingToBeUpdated
   */
  public Map<Long, A2lWpParamMapping> getA2lWpParamMappingToBeUpdated() {
    return this.a2lWpParamMappingToBeUpdated;
  }


  /**
   * @param a2lWpParamMappingToBeUpdated the a2lWpParamMappingToBeUpdated to set
   */
  public void setA2lWpParamMappingToBeUpdated(final Map<Long, A2lWpParamMapping> a2lWpParamMappingToBeUpdated) {
    this.a2lWpParamMappingToBeUpdated = a2lWpParamMappingToBeUpdated;
  }


  /**
   * @return the a2lWpParamMappingToBeDeleted
   */
  public Map<Long, A2lWpParamMapping> getA2lWpParamMappingToBeDeleted() {
    return this.a2lWpParamMappingToBeDeleted;
  }


  /**
   * @param a2lWpParamMappingToBeDeleted the a2lWpParamMappingToBeDeleted to set
   */
  public void setA2lWpParamMappingToBeDeleted(final Map<Long, A2lWpParamMapping> a2lWpParamMappingToBeDeleted) {
    this.a2lWpParamMappingToBeDeleted = a2lWpParamMappingToBeDeleted;
  }


  /**
   * @return the createdA2lWpParamMapping
   */
  public Map<Long, A2lWpParamMapping> getCreatedA2lWpParamMapping() {
    return this.createdA2lWpParamMapping;
  }


  /**
   * @param createdA2lWpParamMapping the createdA2lWpParamMapping to set
   */
  public void setCreatedA2lWpParamMapping(final Map<Long, A2lWpParamMapping> createdA2lWpParamMapping) {
    this.createdA2lWpParamMapping = createdA2lWpParamMapping;
  }


  /**
   * @return the updatedA2lWpParamMapping
   */
  public Map<Long, A2lWpParamMapping> getUpdatedA2lWpParamMapping() {
    return this.updatedA2lWpParamMapping;
  }


  /**
   * @param updatedA2lWpParamMapping the updatedA2lWpParamMapping to set
   */
  public void setUpdatedA2lWpParamMapping(final Map<Long, A2lWpParamMapping> updatedA2lWpParamMapping) {
    this.updatedA2lWpParamMapping = updatedA2lWpParamMapping;
  }


  /**
   * @return the deletedA2lWpParamMapping
   */
  public Map<Long, A2lWpParamMapping> getDeletedA2lWpParamMapping() {
    return this.deletedA2lWpParamMapping;
  }


  /**
   * @param deletedA2lWpParamMapping the deletedA2lWpParamMapping to set
   */
  public void setDeletedA2lWpParamMapping(final Map<Long, A2lWpParamMapping> deletedA2lWpParamMapping) {
    this.deletedA2lWpParamMapping = deletedA2lWpParamMapping;
  }


}
