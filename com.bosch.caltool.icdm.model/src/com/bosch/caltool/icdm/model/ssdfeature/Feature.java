/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssdfeature;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dja7cob
 */
public class Feature implements Comparable<Feature> {

  private Long id;
  private String name;
  private Long attrId;
  private String attrName;

  /**
   * @param attrId the attrId to set
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * @param attrName the attrName to set
   */
  public void setAttrName(final String attrName) {
    this.attrName = attrName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Feature obj) {
    return obj.getId().compareTo(this.id);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getName(), ((Feature) obj).getName()) &&
          ModelUtil.isEqual(getId(), ((Feature) obj).getId());
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return ID
   */
  public Long getId() {
    return this.id;
  }


  /**
   * @param objId ID
   */
  public void setId(final Long objId) {
    this.id = objId;

  }

  /**
   * @return the featureName
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param featureName the featureName to set
   */
  public void setName(final String featureName) {
    this.name = featureName;
  }

  /**
   * @return the attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }

  /**
   * @return the attrName
   */
  public String getAttrName() {
    return this.attrName;
  }
}
