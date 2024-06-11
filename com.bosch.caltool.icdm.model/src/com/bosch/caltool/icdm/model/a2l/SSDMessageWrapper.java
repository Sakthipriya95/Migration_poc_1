/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author rgo7cob
 */
public class SSDMessageWrapper {


  private String description;

  private int code;


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
   * @return the code
   */
  public int getCode() {
    return this.code;
  }


  /**
   * @param code the code to set
   */
  public void setCode(final int code) {
    this.code = code;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "SSDMessageWrapper [description=" + this.description + ", code=" + this.code + "]";
  }

}
