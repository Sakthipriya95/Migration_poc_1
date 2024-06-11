/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author bne4cob
 */
public class Pidc implements IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 5653807328245538796L;

  private Long objId;
  private String name;
  private String description;
  private Long nameValueId;
  private String clearingStatus;
  private boolean deleted;
  private String createdDate;
  private String createdUser;

  private String modifiedDate;
  private String modifiedUser;

  private Long aprjId;
  private String vcdmTransferUser;
  private String vcdmTransferDate;

  private String nameEng;
  private String nameGer;
  private String descEng;
  private String descGer;
  private Long version;
  private Long aliasDefId;
  private Long proRevId;
  private boolean inclRvwOfOldVers;


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.objId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.objId = objId;

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
   * @return the nameValueId
   */
  public Long getNameValueId() {
    return this.nameValueId;
  }

  /**
   * @param nameValueId the nameValueId to set
   */
  public void setNameValueId(final Long nameValueId) {
    this.nameValueId = nameValueId;
  }

  /**
   * @return the clearingStatus
   */
  public String getClearingStatus() {
    return this.clearingStatus;
  }


  /**
   * @param clearingStatus the clearingStatus to set
   */
  public void setClearingStatus(final String clearingStatus) {
    this.clearingStatus = clearingStatus;
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
   * @return the aprjId
   */
  public Long getAprjId() {
    return this.aprjId;
  }


  /**
   * @param aprjId the aprjId to set
   */
  public void setAprjId(final Long aprjId) {
    this.aprjId = aprjId;
  }


  /**
   * @return the vcdmTransferUser
   */
  public String getVcdmTransferUser() {
    return this.vcdmTransferUser;
  }


  /**
   * @param vcdmTransferUser the vcdmTransferUser to set
   */
  public void setVcdmTransferUser(final String vcdmTransferUser) {
    this.vcdmTransferUser = vcdmTransferUser;
  }


  /**
   * @return the vcdmTransferDate
   */
  public String getVcdmTransferDate() {
    return this.vcdmTransferDate;
  }


  /**
   * @param vcdmTransferDate the vcdmTransferDate to set
   */
  public void setVcdmTransferDate(final String vcdmTransferDate) {
    this.vcdmTransferDate = vcdmTransferDate;
  }


  /**
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * @param nameEng the nameEng to set
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * @return the nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }


  /**
   * @param nameGer the nameGer to set
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
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
   * @return the aliasDefId
   */
  public Long getAliasDefId() {
    return this.aliasDefId;
  }

  /**
   * @param aliasDefId the aliasDefId to set
   */
  public void setAliasDefId(final Long aliasDefId) {
    this.aliasDefId = aliasDefId;
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
   * @return the inclRvwOfOldVers
   */
  public boolean isInclRvwOfOldVers() {
    return this.inclRvwOfOldVers;
  }


  /**
   * @param inclRvwOfOldVers the inclRvwOfOldVers to set
   */
  public void setInclRvwOfOldVers(final boolean inclRvwOfOldVers) {
    this.inclRvwOfOldVers = inclRvwOfOldVers;
  }


}
