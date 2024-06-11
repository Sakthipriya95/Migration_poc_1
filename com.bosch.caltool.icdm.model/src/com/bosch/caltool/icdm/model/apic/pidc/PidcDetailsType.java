/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class PidcDetailsType {

  private Long id;

  private String name;

  private Long changeNumber;

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
   * @return the changeNumber
   */
  public Long getChangeNumber() {
    return this.changeNumber;
  }


  /**
   * @param changeNumber the changeNumber to set
   */
  public void setChangeNumber(final Long changeNumber) {
    this.changeNumber = changeNumber;
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
    return "PidcDetailsType [id=" + this.id + ", name=" + this.name + ", changeNumber=" + this.changeNumber +
        ", vcdmElementName=" + this.vcdmElementName + "]";
  }


}
