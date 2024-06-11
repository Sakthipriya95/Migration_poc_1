/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class AttrNValueDependency implements IDataObject, Comparable<AttrNValueDependency> {

  /**
   *
   */
  private static final long serialVersionUID = 2116990183676261598L;


  private Long id;


  private String name;
  private String description;
  private String value;
  private Long version;

  private Long attributeId;

  private Long valueId;
  private Long dependentAttrId;
  private Long dependentValueId;
  private static final int OBJ_EQUAL_CHK_VAL = 0;
  private String changeComment;
  private String createdDate;


  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
  private boolean deleted;


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
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the value
   */
  public String getValue() {
    return this.value;
  }


  /**
   * @param value the value to set
   */
  public void setValue(final String value) {
    this.value = value;
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
   * @return the attributeId
   */
  public Long getAttributeId() {
    return this.attributeId;
  }


  /**
   * @param attributeId the attributeId to set
   */
  public void setAttributeId(final Long attributeId) {
    this.attributeId = attributeId;
  }


  /**
   * @return the valueId
   */
  public Long getValueId() {
    return this.valueId;
  }


  /**
   * @param valueId the valueId to set
   */
  public void setValueId(final Long valueId) {
    this.valueId = valueId;
  }


  /**
   * @return the dependentAttrId
   */
  public Long getDependentAttrId() {
    return this.dependentAttrId;
  }


  /**
   * @param dependentAttrId the dependentAttrId to set
   */
  public void setDependentAttrId(final Long dependentAttrId) {
    this.dependentAttrId = dependentAttrId;
  }


  /**
   * @return the dependentValueId
   */
  public Long getDependentValueId() {
    return this.dependentValueId;
  }


  /**
   * @param dependentValueId the dependentValueId to set
   */
  public void setDependentValueId(final Long dependentValueId) {
    this.dependentValueId = dependentValueId;
  }

  /**
   * @return the changeComment
   */
  public String getChangeComment() {
    return this.changeComment;
  }


  /**
   * @param changeComment the changeComment to set
   */
  public void setChangeComment(final String changeComment) {
    this.changeComment = changeComment;
  }


  /**
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrNValueDependency arg0) {

    int compResult = ModelUtil.compare(getName(), arg0.getName());
    // If name is same, then compare by attribute id
    if (compResult == OBJ_EQUAL_CHK_VAL) {
      compResult = ModelUtil.compare(getValue(), arg0.getValue());
    }


    return compResult;
  }

  /**
   * @return the createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
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
    return ModelUtil.isEqual(getId(), ((AttrNValueDependency) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }
}
