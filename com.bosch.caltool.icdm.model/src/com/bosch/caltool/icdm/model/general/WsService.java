/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class WsService implements IDataObject, Comparable<WsService> {

  private static final long serialVersionUID = -4984452331545688738L;

  private Long id;

  private String name;

  private String servMethod;

  private String servUri;

  private String module;

  private String description;

  private String serviceScope;

  private boolean deleted;


  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
  private Long version;


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
   * @return the servMethod
   */
  public String getServMethod() {
    return this.servMethod;
  }


  /**
   * @param servMethod the servMethod to set
   */
  public void setServMethod(final String servMethod) {
    this.servMethod = servMethod;
  }


  /**
   * @return the servUri
   */
  public String getServUri() {
    return this.servUri;
  }


  /**
   * @param servUri the servUri to set
   */
  public void setServUri(final String servUri) {
    this.servUri = servUri;
  }


  /**
   * @return the module
   */
  public String getModule() {
    return this.module;
  }


  /**
   * @param module the module to set
   */
  public void setModule(final String module) {
    this.module = module;
  }


  /**
   * @return the serviceScope
   */
  public String getServiceScope() {
    return this.serviceScope;
  }


  /**
   * @param serviceScope the serviceScope to set
   */
  public void setServiceScope(final String serviceScope) {
    this.serviceScope = serviceScope;
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
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() == this.getClass()) {
      WsService wsObj = (WsService) obj;
      return getName().equals(wsObj.getName()) && getServiceScope().equals(wsObj.getServiceScope()) &&
          getDescription().equals(wsObj.getDescription()) && (Boolean.compare(isDeleted(), wsObj.isDeleted()) == 0);
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WsService obj) {
    if (obj == null) {
      return -1;
    }

    // Module, URI, Method comparison.
    int moduleCompareVal = ModelUtil.compare(getModule(), obj.getModule());
    int uriCompareVal = ModelUtil.compare(getServUri(), obj.getServUri());
    int methodCompareVal = ModelUtil.compare(getServMethod(), obj.getServMethod());

    // 3-level comparison using if-else block
    if (moduleCompareVal == 0) {
      return ((uriCompareVal == 0) ? methodCompareVal : uriCompareVal);
    }

    return moduleCompareVal;

  }

}
