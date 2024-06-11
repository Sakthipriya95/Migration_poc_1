/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * BO class for SDOM PVER
 */
public class SdomPVER implements Comparable<SdomPVER> {

  /**
   * PVER Name
   */
  private String pverName;

  /**
   * Sdom PVER Name attribute value
   */
  private AttributeValue sdomPverAttrVal;

  /**
   * Parent PIDC Version of this PVER
   */
  private PidcVersion pidcVersion;

  /**
   * PVER Description
   */
  private String description;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SdomPVER obj) {
    return ModelUtil.compare(getPverName(), obj.getPverName());
  }


  /**
   * @return the pverName
   */
  public String getPverName() {
    return this.pverName;
  }


  /**
   * @param pverName the pverName to set
   */
  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
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
   * @return the sdomPverAttrVal
   */
  public AttributeValue getSdomPverAttrVal() {
    return this.sdomPverAttrVal;
  }


  /**
   * @param sdomPverAttrVal the sdomPverAttrVal to set
   */
  public void setSdomPverAttrVal(final AttributeValue sdomPverAttrVal) {
    this.sdomPverAttrVal = sdomPverAttrVal;
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
    return ModelUtil.isEqual(getPverName(), ((SdomPVER) obj).getPverName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getPverName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    String ret = "Project SdomPVER [pverName=" + this.pverName + ", description=" + this.description + ", pidcVersion=";

    if (this.pidcVersion != null) {
      ret = ret + this.pidcVersion.getName();
    }

    ret += "]";

    return ret;
  }


}
