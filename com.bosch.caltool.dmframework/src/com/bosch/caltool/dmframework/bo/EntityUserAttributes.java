/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.Timestamp;

/**
 * Stores the user attirbutes of an entity class
 *
 * @author bne4cob
 */
class EntityUserAttributes {

  /**
   * Created user - old value
   */
  private String oldCreatedUser;
  /**
   * Last modified user - old value
   */
  private String oldModifiedUser;
  /**
   * Created user - new value
   */
  private String newCreatedUser;
  /**
   * Last modified user - new value
   */
  private String newModifiedUser;

  /**
   * Created date - old value
   */
  private Timestamp oldCreationDate;
  /**
   * Last modified date - old value
   */
  private Timestamp oldModifiedDate;
  /**
   * Created date - new value
   */
  private Timestamp newCreationDate;
  /**
   * Last modified date - new value
   */
  private Timestamp newModifiedDate;


  /**
   * @return the oldCreatedUser
   */
  public String getOldCreatedUser() {
    return this.oldCreatedUser;
  }

  /**
   * @param oldCreatedUser the oldCreatedUser to set
   */
  public void setOldCreatedUser(final String oldCreatedUser) {
    this.oldCreatedUser = oldCreatedUser;
  }

  /**
   * @return the oldModfiedUser
   */
  public String getOldModifiedUser() {
    return this.oldModifiedUser;
  }

  /**
   * @param oldModifiedUser the oldModfiedUser to set
   */
  public void setOldModifiedUser(final String oldModifiedUser) {
    this.oldModifiedUser = oldModifiedUser;
  }

  /**
   * @return the newCreatedUser
   */
  public String getNewCreatedUser() {
    return this.newCreatedUser;
  }

  /**
   * @param newCreatedUser the newCreatedUser to set
   */
  public void setNewCreatedUser(final String newCreatedUser) {
    this.newCreatedUser = newCreatedUser;
  }

  /**
   * @return the newModifiedUser
   */
  public String getNewModifiedUser() {
    return this.newModifiedUser;
  }

  /**
   * @param newModifiedUser the newModifiedUser to set
   */
  public void setNewModifiedUser(final String newModifiedUser) {
    this.newModifiedUser = newModifiedUser;
  }

  /**
   * @return the oldCreationDate
   */
  public Timestamp getOldCreationDate() {
    return this.oldCreationDate != null ? (Timestamp) this.oldCreationDate.clone() : null;
  }

  /**
   * @param oldCreationDate the oldCreationDate to set
   */
  public void setOldCreationDate(final Timestamp oldCreationDate) {
    this.oldCreationDate = oldCreationDate;
  }

  /**
   * @return the oldModifiedDate
   */
  public Timestamp getOldModifiedDate() {
    return this.oldModifiedDate != null ? (Timestamp) this.oldModifiedDate.clone() : null;
  }

  /**
   * @param oldModifiedDate the oldModifiedDate to set
   */
  public void setOldModifiedDate(final Timestamp oldModifiedDate) {
    this.oldModifiedDate = oldModifiedDate;
  }

  /**
   * @return the newCreationDate
   */
  public Timestamp getNewCreationDate() {
    return this.newCreationDate != null ? (Timestamp) this.newCreationDate.clone() : null;
  }

  /**
   * @param newCreationDate the newCreationDate to set
   */
  public void setNewCreationDate(final Timestamp newCreationDate) {
    this.newCreationDate = newCreationDate;
  }

  /**
   * @return the newModifiedDate
   */
  public Timestamp getNewModifiedDate() {
    return this.newModifiedDate != null ? (Timestamp) this.newModifiedDate.clone() : null;
  }

  /**
   * @param newModifiedDate the newModifiedDate to set
   */
  public void setNewModifiedDate(final Timestamp newModifiedDate) {
    this.newModifiedDate = newModifiedDate;
  }

}
