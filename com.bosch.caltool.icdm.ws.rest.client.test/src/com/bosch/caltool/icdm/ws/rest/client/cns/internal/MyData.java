/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;

import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class MyData implements IModel {

  private Long id;

  private String name;

  private Long version;

  /**
   * Empty constructor
   */
  public MyData() {
    // Empty constructor
  }

  /**
   * @param id id
   * @param name name
   * @param version version
   */
  public MyData(final Long id, final String name, final Long version) {
    super();
    this.id = id;
    setName(name);
    this.version = version;
  }


  /**
   * @return the id
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [id=" + this.id + ", name=" + this.name + ", version=" + this.version + "]";
  }


}
