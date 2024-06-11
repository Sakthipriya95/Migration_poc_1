/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class PidcFavourite implements IModel, IBasicObject {

  /**
   *
   */
  private static final long serialVersionUID = -7628285485524461693L;
  private Long id;
  private String name;
  private String description;
  private Long pidcId;
  private Long userId;

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
  public void setId(final Long objId) {
    this.id = objId;

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
   * @return name
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
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * @return the userId
   */
  public Long getUserId() {
    return this.userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
  }

}
