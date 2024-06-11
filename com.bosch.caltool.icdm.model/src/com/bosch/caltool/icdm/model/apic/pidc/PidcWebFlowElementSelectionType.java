/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class PidcWebFlowElementSelectionType {

  private Long id;

  private String type;

  private String name;

  private String vcdmElementName;


  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * @return the type
   */
  public String getType() {
    return this.type;
  }


  /**
   * @param type the type to set
   */
  public void setType(final String type) {
    this.type = type;
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
   * @return the vcdmElementName
   */
  public String getVcdmElementName() {
    return this.vcdmElementName;
  }


  /**
   * @param vcdmElementName the vcdmElementName to set
   */
  public void setVcdmElementName(final String vcdmElementName) {
    this.vcdmElementName = vcdmElementName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PidcWebFlowElementSelectionType [id=" + this.id + ", type=" + this.type + ", name=" + this.name +
        ", vcdmElementName=" + this.vcdmElementName + "]";
  }

}
