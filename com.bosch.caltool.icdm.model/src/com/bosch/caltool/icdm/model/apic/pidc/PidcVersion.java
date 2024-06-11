/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Objects;

import com.bosch.caltool.icdm.model.apic.IPastableItem;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author mkl2cob
 */
public class PidcVersion implements Cloneable, IProjectObject, Comparable<PidcVersion>, IPastableItem {

  /**
   *
   */
  private static final long serialVersionUID = 7899156196000205838L;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  private Long id;

  private String name;
  private String versionName;
  private String lastConfirmationDate;

  private Long version;

  private String versDescEng;

  private String versDescGer;

  private String description;
  private Long pidcId;

  private Long proRevId;

  private boolean deleted;

  private String createdUser;
  private String modifiedUser;
  private String createdDate;
  private String modifiedDate;

  private String lastValidDate;

  private boolean upToDate;

  private String pidStatus;
  private Long parentPidcVerId;

  /**
   * @return the pidStatus
   */
  public String getPidStatus() {
    return this.pidStatus;
  }


  /**
   * @param pidStatus the pidStatus to set
   */
  public void setPidStatus(final String pidStatus) {
    this.pidStatus = pidStatus;
  }


  /**
   * @return the upToDate
   */
  public boolean isUpToDate() {
    return this.upToDate;
  }


  /**
   * @param upToDate the upToDate to set
   */
  public void setUpToDate(final boolean upToDate) {
    this.upToDate = upToDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcVersion obj) {
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
    PidcVersion other = (PidcVersion) obj;
    return Objects.equals(getId(), other.getId());
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
   * @return the lastConfirmationDate
   */
  public String getLastConfirmationDate() {
    return this.lastConfirmationDate;
  }


  /**
   * @param lastConfirmationDate the lastConfirmationDate to set
   */
  public void setLastConfirmationDate(final String lastConfirmationDate) {
    this.lastConfirmationDate = lastConfirmationDate;
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
   * {@inheritDoc}
   */
  @Override
  public boolean isDeleted() {
    return this.deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;

  }


  /**
   * @return the versionName
   */
  public String getVersionName() {
    return this.versionName;
  }


  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }


  /**
   * @return the proRevId
   */
  public Long getProRevId() {
    return this.proRevId;
  }


  /**
   * @param proRevId the proRevId to set
   */
  public void setProRevId(final Long proRevId) {
    this.proRevId = proRevId;
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
   * @return the lastValidDate
   */
  public String getLastValidDate() {
    return this.lastValidDate;
  }

  /**
   * @param lastValidDate the lastValidDate to set
   */
  public void setLastValidDate(final String lastValidDate) {
    this.lastValidDate = lastValidDate;
  }


  /**
   * @return the versDescEng
   */
  public String getVersDescEng() {
    return this.versDescEng;
  }


  /**
   * @param versDescEng the versDescEng to set
   */
  public void setVersDescEng(final String versDescEng) {
    this.versDescEng = versDescEng;
  }


  /**
   * @return the versDescGer
   */
  public String getVersDescGer() {
    return this.versDescGer;
  }


  /**
   * @param versDescGer the versDescGer to set
   */
  public void setVersDescGer(final String versDescGer) {
    this.versDescGer = versDescGer;
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


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    return true;
  }


  /**
   * @return the parentPidcVerId
   */
  public Long getParentPidcVerId() {
    return this.parentPidcVerId;
  }


  /**
   * @param parentPidcVerId the parentPidcVerId to set
   */
  public void setParentPidcVerId(final Long parentPidcVerId) {
    this.parentPidcVerId = parentPidcVerId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcVersion clone() throws CloneNotSupportedException {

    PidcVersion vers = null;
    try {
      vers = (PidcVersion) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return vers;
  }
}
