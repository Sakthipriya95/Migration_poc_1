/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class TopLevelEntityInternal implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = -2029501198971821597L;

  private long id;

  private String entityName;

  private Date lastModDate;

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
   * @return the entityName
   */
  public String getEntityName() {
    return this.entityName;
  }

  /**
   * @param entityName the entityName to set
   */
  public void setEntityName(final String entityName) {
    this.entityName = entityName;
  }

  /**
   * @return the lastModDate
   */
  public Date getLastModDate() {
    return this.lastModDate;
  }

  /**
   * @param lastModDate the lastModDate to set
   */
  public void setLastModDate(final Date lastModDate) {
    this.lastModDate = lastModDate;
  }

}
