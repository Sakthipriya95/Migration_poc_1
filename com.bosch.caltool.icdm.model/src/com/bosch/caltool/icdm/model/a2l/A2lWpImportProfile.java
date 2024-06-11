/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author and4cob
 */
public class A2lWpImportProfile implements Comparable<A2lWpImportProfile>, IDataObject {


  /**
   * Serial UID
   */
  private static final long serialVersionUID = 314303069698599L;
  /**
   * Profile Id
   */
  private Long id;
  /**
   * Profile Name
   */
  private String profileName;
  /**
   * Profile Order
   */
  private Long profileOrder;
  /**
   * Profile Details
   */
  private A2lImportProfileDetails profileDetails;
  /**
   * Version
   */
  private Long version;
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
   * @return profileName
   */
  public String getProfileName() {
    return this.profileName;
  }

  /**
   * @param profileName set profileName
   */
  public void setProfileName(final String profileName) {
    this.profileName = profileName;
  }

  /**
   * @return profileOrder
   */
  public Long getProfileOrder() {
    return this.profileOrder;
  }

  /**
   * @param profileOrder set profileOrder
   */
  public void setProfileOrder(final Long profileOrder) {
    this.profileOrder = profileOrder;
  }

  /**
   * @return profileDetails
   */
  public A2lImportProfileDetails getProfileDetails() {
    return this.profileDetails;
  }

  /**
   * @param profileDetails set profileDetails
   */
  public void setProfileDetails(final A2lImportProfileDetails profileDetails) {
    this.profileDetails = profileDetails;
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
  public int compareTo(final A2lWpImportProfile object) {
    return ModelUtil.compare(getProfileOrder(), object.getProfileOrder());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWpImportProfile) obj).getId());

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
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // TODO Auto-generated method stub

  }
}
