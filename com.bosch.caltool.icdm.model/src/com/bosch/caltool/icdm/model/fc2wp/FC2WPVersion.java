/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.fc2wp;

import java.util.Objects;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class FC2WPVersion implements IDataObject, Comparable<FC2WPVersion> {

  /**
   *
   */
  private static final long serialVersionUID = 3047537385397677506L;
  private Long id;
  private Long fcwpDefId;
  private String name;
  private String description;
  private String descEng;
  private String descGer;
  private String versionName;
  private Long majorVersNo;
  private Long minorVersNo;
  private String archReleaseSdom;
  private boolean active;
  private boolean workingSet;

  private String createdUser;
  private String modifiedUser;

  private String createdDate;
  private String modifiedDate;

  private Long version;


  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the fcwpVerId
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param fcwpVerId the fcwpVerId to set
   */
  @Override
  public void setId(final Long fcwpVerId) {
    this.id = fcwpVerId;
  }


  /**
   * @return the fcwpDefId
   */
  public Long getFcwpDefId() {
    return this.fcwpDefId;
  }


  /**
   * @param fcwpDefId the fcwpDefId to set
   */
  public void setFcwpDefId(final Long fcwpDefId) {
    this.fcwpDefId = fcwpDefId;
  }


  /**
   * @return the archReleaseSdom
   */
  public String getArchReleaseSdom() {
    return this.archReleaseSdom;
  }


  /**
   * @param archReleaseSdom the archReleaseSdom to set
   */
  public void setArchReleaseSdom(final String archReleaseSdom) {
    this.archReleaseSdom = archReleaseSdom;
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
   * @return the desc
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * @param desc the desc to set
   */
  @Override
  public void setDescription(final String desc) {
    this.description = desc;
  }

  /**
   * @return the descEng
   */
  public String getDescEng() {
    return this.descEng;
  }

  /**
   * @param descEng the descEng to set
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  /**
   * @return the descGer
   */
  public String getDescGer() {
    return this.descGer;
  }

  /**
   * @param descGer the descGer to set
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }

  /**
   * @return the versionname
   */
  public String getVersionName() {
    return this.versionName;
  }

  /**
   * @param versionname the versionname to set
   */
  public void setVersionName(final String versionname) {
    this.versionName = versionname;
  }

  /**
   * @return the majorVersNo
   */
  public Long getMajorVersNo() {
    return this.majorVersNo;
  }

  /**
   * @param majorVersNo the majorVersNo to set
   */
  public void setMajorVersNo(final Long majorVersNo) {
    this.majorVersNo = majorVersNo;
  }

  /**
   * @return the minorVersNo
   */
  public Long getMinorVersNo() {
    return this.minorVersNo;
  }

  /**
   * @param minorVersNo the minorVersNo to set
   */
  public void setMinorVersNo(final Long minorVersNo) {
    this.minorVersNo = minorVersNo;
  }

  /**
   * @return the isActive
   */
  public boolean isActive() {
    return this.active;
  }

  /**
   * @param isActive the isActive to set
   */
  public void setActive(final boolean isActive) {
    this.active = isActive;
  }

  /**
   * @return the isWorkingSet
   */
  public boolean isWorkingSet() {
    return this.workingSet;
  }

  /**
   * @param isWorkingSet the isWorkingSet to set
   */
  public void setWorkingSet(final boolean isWorkingSet) {
    this.workingSet = isWorkingSet;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FC2WPVersion obj) {
    // TODO Auto-generated method stub
    return ModelUtil.compare(getName(), obj.getName());
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
    FC2WPVersion other = (FC2WPVersion) obj;
    return Objects.equals(getName(), other.getName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getName() == null) ? 0 : getName().hashCode());
    return result;
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
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
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
}
