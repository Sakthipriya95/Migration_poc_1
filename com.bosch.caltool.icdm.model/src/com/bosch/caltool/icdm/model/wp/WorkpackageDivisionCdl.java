/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.wp;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * WPDivCdl Model class
 *
 * @author apj4cob
 */
public class WorkpackageDivisionCdl implements Comparable<WorkpackageDivisionCdl>, IDataObject {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 57338083768703L;
  /**
   * Wp Div Cdl Id
   */
  private Long id;
  /**
   * Region Id
   */
  private Long regionId;
  /**
   * Wp Div Id
   */
  private Long wpDivId;
  /**
   * User Id
   */
  private Long userId;
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
   * @return regionId
   */
  public Long getRegionId() {
    return this.regionId;
  }

  /**
   * @param regionId set regionId
   */
  public void setRegionId(final Long regionId) {
    this.regionId = regionId;
  }

  /**
   * @return wpDivId
   */
  public Long getWpDivId() {
    return this.wpDivId;
  }

  /**
   * @param wpDivId set wpDivId
   */
  public void setWpDivId(final Long wpDivId) {
    this.wpDivId = wpDivId;
  }

  /**
   * @return userId
   */
  public Long getUserId() {
    return this.userId;
  }

  /**
   * @param userId set userId
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
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
  public int compareTo(final WorkpackageDivisionCdl object) {
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
    WorkpackageDivisionCdl other = (WorkpackageDivisionCdl) obj;
    return getRegionId().equals(other.getRegionId());

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
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // Not applicable
  }
}
