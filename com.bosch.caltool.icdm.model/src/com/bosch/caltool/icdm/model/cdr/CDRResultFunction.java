/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * CDR Result Function Model class
 *
 * @author BRU2COB
 */
public class CDRResultFunction implements Comparable<CDRResultFunction>, IModel, IBasicObject {

  /**
   *
   */
  private static final long serialVersionUID = -1103750649017586355L;
  /**
   * Rvw Fun Id
   */
  private Long id;
  /**
   * Func name
   */
  private String name;


  private String description;
  /**
   * Result Id
   */
  private Long resultId;
  /**
   * Function Id
   */
  private Long functionId;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;
  /**
   * Function Vers
   */
  private String functionVers;

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
   * @return resultId
   */
  public Long getResultId() {
    return this.resultId;
  }

  /**
   * @param resultId set resultId
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
  }

  /**
   * @return functionId
   */
  public Long getFunctionId() {
    return this.functionId;
  }

  /**
   * @param functionId set functionId
   */
  public void setFunctionId(final Long functionId) {
    this.functionId = functionId;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
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
   * @return functionVers
   */
  public String getFunctionVers() {
    return this.functionVers;
  }

  /**
   * @param functionVers set functionVers
   */
  public void setFunctionVers(final String functionVers) {
    this.functionVers = functionVers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRResultFunction object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((CDRResultFunction) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


}
