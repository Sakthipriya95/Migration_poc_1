/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;


/**
 * This class holds BC informations need for node configuration
 * 
 * @author gue1cob -
 */
public class ComPkgBcModel {

  /**
   * BC element
   */
  private String bcName;
  /**
   * BC Version for the bc element
   */
  private String bvVersion;
  /**
   * level of the nodes for the corresponding bc's
   */
  private BigDecimal level;

  /**
   * @return the bcName
   */
  public String getBcName() {
    return this.bcName;
  }

  /**
   * @param bcName the bcName to set
   */
  public void setBcName(final String bcName) {
    this.bcName = bcName;
  }

  /**
   * @return the bvVersion
   */
  public String getBvVersion() {
    return this.bvVersion;
  }

  /**
   * @param bvVersion the bvVersion to set
   */
  public void setBvVersion(final String bvVersion) {
    this.bvVersion = bvVersion;
  }

  /**
   * @return the level
   */
  public BigDecimal getLevel() {
    return this.level;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(final BigDecimal level) {
    this.level = level;
  }

  /**
   * SSD-399 {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.bcName == null) ? 0 : this.bcName.hashCode());
    result = (prime * result) + ((this.bvVersion == null) ? 0 : this.bvVersion.hashCode());
    return result;
  }

  /**
   * SSD-399 {@inheritDoc}
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
    ComPkgBcModel other = (ComPkgBcModel) obj;
    if ((this.bcName != null) && (this.bvVersion != null) && (other.bcName != null) && (other.bvVersion != null)) {
      return (this.bcName.equals(other.bcName) && this.bvVersion.equals(other.bvVersion));
    }
    return false;

  }


}
