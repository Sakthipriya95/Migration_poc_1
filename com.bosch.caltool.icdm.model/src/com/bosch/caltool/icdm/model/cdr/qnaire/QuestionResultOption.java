/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * QuestionResult Model class
 *
 * @author say8cob
 */
public class QuestionResultOption implements Cloneable, Comparable<QuestionResultOption>, IDataObject {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 212889007382182L;
  /**
   * Q Result Id
   */
  private Long id;
  /**
   * Q Id
   */
  private Long qId;
  /**
   * Q Result Name
   */
  private String qResultName;
  /**
   * Q Result Type
   */
  private String qResultType;
  
  /**
   * Q Result Allow Finishing of WP
   */
  private boolean qResultAlwFinishWP;
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
   * @return qId
   */
  public Long getQId() {
    return this.qId;
  }

  /**
   * @param qId set qId
   */
  public void setQId(final Long qId) {
    this.qId = qId;
  }

  /**
   * @return qResultName
   */
  public String getQResultName() {
    return this.qResultName;
  }

  /**
   * @param qResultName set qResultName
   */
  public void setQResultName(final String qResultName) {
    this.qResultName = qResultName;
  }

  /**
   * @return qResultType
   */
  public String getQResultType() {
    return this.qResultType;
  }

  /**
   * @param qResultType set qResultType
   */
  public void setQResultType(final String qResultType) {
    this.qResultType = qResultType;
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
  public QuestionResultOption clone() {
    try {
      return (QuestionResultOption) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionResultOption object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if ((obj == null) || (this.getClass() != obj.getClass())) {
      return false;
    }

    return ModelUtil.isEqual(getQResultName(), ((QuestionResultOption) obj).getQResultName()) &&
        ModelUtil.isEqual(getQResultType(), ((QuestionResultOption) obj).getQResultType());
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
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // NA

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
    // TODO Auto-generated method stub

  }

  
  /**
   * @return the qResultAlwFinishWP
   */
  public boolean isqResultAlwFinishWP() {
    return qResultAlwFinishWP;
  }

  
  /**
   * @param qResultAlwFinishWP the qResultAlwFinishWP to set
   */
  public void setqResultAlwFinishWP(boolean qResultAlwFinishWP) {
    this.qResultAlwFinishWP = qResultAlwFinishWP;
  }

  
}
