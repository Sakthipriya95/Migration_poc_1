/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2lDepParam Model class
 *
 * @author UKT1COB
 */
public class A2lDepParam implements Cloneable, Comparable<A2lDepParam>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 405204009941909L;
  /**
   * A2l Dep Param Id
   */
  private Long id;
  /**
   * Param name
   */
  private String name;
  /**
   * Description
   */
  private String description;
  /**
   * A2l File Id
   */
  private Long a2lFileId;
  /**
   * Parameter
   */
  private String paramName;
  /**
   * Depends On Parameter
   */
  private String dependsOnParamName;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return pidcA2lId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }

  /**
   * @param a2lFileId set a2lFileId
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }

  /**
   * @return paramName
   */
  public String getParamName() {
    return this.paramName;
  }

  /**
   * @param paramName set paramName
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the dependsOnParam
   */
  public String getDependsOnParamName() {
    return this.dependsOnParamName;
  }


  /**
   * @param dependsOnParamName the dependsOnParam to set
   */
  public void setDependsOnParamName(final String dependsOnParamName) {
    this.dependsOnParamName = dependsOnParamName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public A2lDepParam clone() {

    A2lDepParam a2lDepParam = null;
    try {
      a2lDepParam = (A2lDepParam) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return a2lDepParam;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lDepParam object) {
    return ModelUtil.compare(getId(), object.getId());
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

    return ModelUtil.isEqual(getId(), ((A2lDepParam) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

}
