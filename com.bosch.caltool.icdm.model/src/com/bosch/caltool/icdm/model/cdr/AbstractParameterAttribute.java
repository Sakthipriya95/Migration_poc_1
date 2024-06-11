/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

/**
 * @author rgo7cob
 */
public abstract class AbstractParameterAttribute implements IParameterAttribute {


  private String name;
  private String description;

  private String createdUser;

  private String modifiedUser;

  private String createdDate;

  private String modifiedDate;


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
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
  public String getName() {
    return this.name;
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


}
