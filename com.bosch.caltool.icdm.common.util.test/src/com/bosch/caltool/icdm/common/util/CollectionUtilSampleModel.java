/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

class CollectionUtilSampleModel {

  private final int id;
  private final String name;

  /**
   * @param id id
   * @param name name
   */
  public CollectionUtilSampleModel(final int id, final String name) {
    super();
    this.id = id;
    this.name = name;
  }

  /**
   * @return the id
   */
  public int getId() {
    return this.id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [id=" + this.id + ", name=" +
        this.name + "]";
  }

}