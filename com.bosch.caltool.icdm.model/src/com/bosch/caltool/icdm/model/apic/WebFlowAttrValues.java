/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

/**
 * @author rgo7cob
 */
public class WebFlowAttrValues implements Comparable<WebFlowAttrValues> {


  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * value id
   */
  private Long valueID;

  /**
   * value name
   */
  private String valueName;

  /**
   * value desc
   */
  private String valueDesc;


  /**
   * is alias name available
   */
  /**
   *
   */
  private boolean aliasName;


  /**
   * empty Constructor
   */
  public WebFlowAttrValues() {
    super();

  }

  /**
   * @return the valueID
   */
  public Long getValueID() {
    return this.valueID;
  }


  /**
   * @return the valueName
   */
  public String getValueName() {
    return this.valueName;
  }


  /**
   * @return the valueDesc
   */
  public String getValueDesc() {
    return this.valueDesc;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WebFlowAttrValues obj) {
    return getValueName().compareTo(obj.getValueName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WebFlowAttrValues webFlowAttrValue = (WebFlowAttrValues) obj;
    return webFlowAttrValue.getValueID() == getValueID();
  }


  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getValueID() == null) ? 0 : getValueID().hashCode());
    return result;
  }

  /**
   * @param valueID the valueID to set
   */
  public void setValueID(final Long valueID) {
    this.valueID = valueID;
  }


  /**
   * @param valueName the valueName to set
   */
  public void setValueName(final String valueName) {
    this.valueName = valueName;
  }


  /**
   * @param valueDesc the valueDesc to set
   */
  public void setValueDesc(final String valueDesc) {
    this.valueDesc = valueDesc;
  }

  /**
   * @return the aliasName
   */
  public boolean isAliasName() {
    return this.aliasName;
  }


  /**
   * @param aliasName the aliasName to set
   */
  public void setAliasName(final boolean aliasName) {
    this.aliasName = aliasName;
  }

}
