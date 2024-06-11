/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author bne4cob
 */
public class BaseComponent {

  private Long bcId;
  private String name;
  private String description;

  /**
   * @return the bcId
   */
  public Long getBcId() {
    return this.bcId;
  }

  /**
   * @param bcId the bcId to set
   */
  public void setBcId(final Long bcId) {
    this.bcId = bcId;
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
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "BaseComponent [bcId=" + this.bcId + ", name=" + this.name + ", description=" + this.description + "]";
  }


}
