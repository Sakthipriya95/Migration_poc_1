/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Questionnaire Model class
 *
 * @author bru2cob
 */
public class Questionnaire implements Cloneable, Comparable<Questionnaire>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 481946192943210L;
  /**
   * Questionnaire ID
   */
  private Long id;

  /**
   * Name - name + division name if not configured locally
   */
  private String name;

  /**
   * Simple Name without division name
   */
  private String nameSimple;

  /**
   * Description
   */
  private String description;
  /**
   * Name Eng
   */
  private String nameEng;
  /**
   * Name Ger
   */
  private String nameGer;
  /**
   * Desc Eng
   */
  private String descEng;
  /**
   * Desc Ger
   */
  private String descGer;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Deleted Flag
   */
  private boolean deleted;
  /**
   * Version
   */
  private Long version;
  /**
   * Wp Div Id
   */
  private Long wpDivId;

  /**
   * Division
   */
  private String divName;

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
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;

  }

  /**
   * @return the nameSimple
   */
  public String getNameSimple() {
    return this.nameSimple;
  }

  /**
   * @param nameSimple the nameSimple to set
   */
  public void setNameSimple(final String nameSimple) {
    this.nameSimple = nameSimple;
  }

  /**
   * @return nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }

  /**
   * @param nameEng set nameEng
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }

  /**
   * @return nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }

  /**
   * @param nameGer set nameGer
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
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
   * @return descEng
   */
  public String getDescEng() {
    return this.descEng;
  }

  /**
   * @param descEng set descEng
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  /**
   * @return descGer
   */
  public String getDescGer() {
    return this.descGer;
  }

  /**
   * @param descGer set descGer
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }

  /**
   * @return createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return deleted
   */
  public boolean getDeleted() {
    return this.deleted;
  }

  /**
   * @param deleted set deleted
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
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
   * {@inheritDoc}
   */
  @Override
  public Questionnaire clone() {
    try {
      return (Questionnaire) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Questionnaire object) {
    return ModelUtil.compare(getId(), object.getId());
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((Questionnaire) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the divName
   */
  public String getDivName() {
    return this.divName;
  }

  /**
   * @param divName the divName to set
   */
  public void setDivName(final String divName) {
    this.divName = divName;
  }

}
