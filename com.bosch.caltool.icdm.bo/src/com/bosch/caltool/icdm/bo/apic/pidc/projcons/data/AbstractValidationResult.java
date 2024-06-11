/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons.data;


import java.util.Calendar;

import com.bosch.caltool.icdm.bo.apic.pidc.projcons.ErrorLevel;
import com.bosch.caltool.icdm.bo.apic.pidc.projcons.ErrorType;
import com.bosch.caltool.icdm.bo.apic.pidc.projcons.IValidationResult;

/**
 * @author bne4cob
 */
public abstract class AbstractValidationResult implements IValidationResult {

  /**
   * Error Type
   */
  private ErrorType errorType;
  /**
   * Error Level
   */
  private ErrorLevel errorLevel;
  /**
   * Created user
   */
  private String createdUser;
  /**
   * Created date
   */
  private Calendar createdDate;
  /**
   * Last modified user
   */
  private String lastModifiedUser;
  /**
   * Last modified date
   */
  private Calendar lastModifiedDate;
  /**
   * Created user of parent
   */
  private String createdUserParent;
  /**
   * Created date of parent
   */
  private Calendar createdDateParent;
  /**
   * Last modified user of parent
   */
  private String lastModifiedUserParent;
  /**
   * Last modified date of parent
   */
  private Calendar lastModifiedDateParent;

  /**
   * @param errType ErrorType
   */
  public void setErrorType(final ErrorType errType) {
    this.errorType = errType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ErrorType getErrorType() {
    return this.errorType;
  }

  /**
   * @param errorLevel the errorLevel to set
   */
  public final void setErrorLevel(final ErrorLevel errorLevel) {
    this.errorLevel = errorLevel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ErrorLevel getErrorLevel() {
    return this.errorLevel;
  }

  /**
   * @return the createdUser
   */
  public final String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser the createdUser to set
   */
  public final void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return the createdDate
   */
  public final Calendar getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public final void setCreatedDate(final Calendar createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the lastModifiedUser
   */
  public final String getLastModifiedUser() {
    return this.lastModifiedUser;
  }

  /**
   * @param lastModifiedUser the lastModifiedUser to set
   */
  public final void setLastModifiedUser(final String lastModifiedUser) {
    this.lastModifiedUser = lastModifiedUser;
  }

  /**
   * @return the lastModifiedDate
   */
  public final Calendar getLastModifiedDate() {
    return this.lastModifiedDate;
  }

  /**
   * @param lastModifiedDate the lastModifiedDate to set
   */
  public final void setLastModifiedDate(final Calendar lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  /**
   * @return the createdUserParent
   */
  public final String getCreatedUserParent() {
    return this.createdUserParent;
  }

  /**
   * @param createdUserParent the createdUserParent to set
   */
  public final void setCreatedUserParent(final String createdUserParent) {
    this.createdUserParent = createdUserParent;
  }

  /**
   * @return the createdDateParent
   */
  public final Calendar getCreatedDateParent() {
    return this.createdDateParent;
  }

  /**
   * @param createdDateParent the createdDateParent to set
   */
  public final void setCreatedDateParent(final Calendar createdDateParent) {
    this.createdDateParent = createdDateParent;
  }

  /**
   * @return the lastModifiedUserParent
   */
  public final String getLastModifiedUserParent() {
    return this.lastModifiedUserParent;
  }

  /**
   * @param lastModifiedUserParent the lastModifiedUserParent to set
   */
  public final void setLastModifiedUserParent(final String lastModifiedUserParent) {
    this.lastModifiedUserParent = lastModifiedUserParent;
  }

  /**
   * @return the lastModifiedDateParent
   */
  public final Calendar getLastModifiedDateParent() {
    return this.lastModifiedDateParent;
  }

  /**
   * @param lastModifiedDateParent the lastModifiedDateParent to set
   */
  public final void setLastModifiedDateParent(final Calendar lastModifiedDateParent) {
    this.lastModifiedDateParent = lastModifiedDateParent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSummary() {
    return getErrorLevel().getLevel() + '\t' + getErrorType();
  }

}