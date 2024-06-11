/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.cdr.AbstractParameter;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class Parameter extends AbstractParameter implements Cloneable, Comparable<Parameter> {

  /**
   *
   */
  private static final long serialVersionUID = 4472374972980541377L;
  private Long id;
  private String name;
  private String description;

  private Long version;


  private String createdDate;
  private String createdUser;
  private String modifiedUser;
  private String modifiedDate;


  private String pClassText;

  private String custPrm;


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
  public int compareTo(final Parameter param) {
    int ret = ModelUtil.compare(getName(), param.getName());
    ret = ret == 0 ? ModelUtil.compare(getType(), param.getType()) : ret;
    return ret == 0 ? ModelUtil.compare(getId(), param.getId()) : ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Parameter clone() throws CloneNotSupportedException {
    Parameter param = null;
    try {
      param = (Parameter) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return param;
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
  public String getpClassText() {
    return this.pClassText;

  }


  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
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
  public void setCreatedUser(final String user) {
    this.createdUser = user;

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
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    this.createdDate = date;

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
  public void setModifiedDate(final String date) {
    this.modifiedDate = date;

  }


  /**
   * @return the custPrm
   */
  public String getCustPrm() {
    return this.custPrm;
  }


  /**
   * @param custPrm the custPrm to set
   */
  public void setCustPrm(final String custPrm) {
    this.custPrm = custPrm;
  }


  /**
   * @param pClassText the pClassText to set
   */
  public void setpClassText(final String pClassText) {
    this.pClassText = pClassText;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((Parameter) obj).getId()) &&
        ModelUtil.isEqual(getName(), ((Parameter) obj).getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }
}
