/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bne4cob
 */
public class User implements IDataObject, Comparable<User> {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = -5920170857279632784L;

  private Long id;

  /**
   * User Name - NT ID
   */
  private String name;

  /**
   * User's Display name
   */
  private String description;

  private String department;

  private String firstName;

  private String lastName;

  private String disclaimerAcceptedDate;

  private String createdUser;

  private String modifiedUser;

  private String createdDate;

  private String modifiedDate;

  private Long version;

  /**
   * Sort columns
   *
   * @author bne4cob
   */
  public enum SortColumns {
                           /**
                            * User name
                            */
                           SORT_USER_NAME,
                           /**
                            * First Name
                            */
                           SORT_FIRST_NAME,
                           /**
                            * Last Name
                            */
                           SORT_LAST_NAME,
                           /**
                            * Full Name
                            */
                           SORT_FULL_NAME,
                           /**
                            * Department
                            */
                           SORT_DEPARTMENT
  }

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the userId
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * @param userId the userId to set
   */
  @Override
  public void setId(final Long userId) {
    this.id = userId;
  }

  /**
   * @return the department
   */
  public String getDepartment() {
    return this.department;
  }

  /**
   * @param department the department to set
   */
  public void setDepartment(final String department) {
    this.department = department;
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return this.firstName;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return this.lastName;
  }

  /**
   * @param lastName the lastName to set
   */
  public void setLastName(final String lastName) {
    this.lastName = lastName;
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
  public int compareTo(final User obj) {
    return ModelUtil.compare(getName(), obj.getName());
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
    User other = (User) obj;
    return getName().equals(other.getName());
  }

  /**
   * @param user2
   * @param sortColumn
   * @return
   */
  public int compareTo(final User user2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_USER_NAME:
        // compare the user IDs
        compareResult = ModelUtil.compare(getName(), user2.getName());
        break;

      case SORT_FIRST_NAME:
        // compare the first names
        compareResult = ModelUtil.compare(getFirstName(), user2.getFirstName());
        break;

      case SORT_LAST_NAME:
        // compare the last names
        compareResult = ModelUtil.compare(getLastName(), user2.getLastName());
        break;

      case SORT_FULL_NAME:
        // compare the last names
        compareResult = ModelUtil.compare(getDescription(), user2.getDescription());
        break;

      case SORT_DEPARTMENT:
        // compare the departments
        compareResult = ModelUtil.compare(getDepartment(), user2.getDepartment());
        break;


      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the last name
      compareResult = ModelUtil.compare(getLastName(), user2.getLastName());

      if (compareResult == 0) {
        // compare result is still equal, compare the first name
        compareResult = ModelUtil.compare(getFirstName(), user2.getFirstName());

        if (compareResult == 0) {
          // compare result is still equal, compare the userId
          compareResult = ModelUtil.compare(getName(), user2.getName());
        }

      }

    }

    return compareResult;
  }

  /**
   * @return the disclaimerAcceptedDate
   */
  public String getDisclaimerAcceptedDate() {
    return this.disclaimerAcceptedDate;
  }


  /**
   * @param disclaimerAcceptedDate the disclaimerAcceptedDate to set
   */
  public void setDisclaimerAcceptedDate(final String disclaimerAcceptedDate) {
    this.disclaimerAcceptedDate = disclaimerAcceptedDate;
  }

  /**
   * User's name - NT ID
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Set user's name - NT ID
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * User's display name
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Set user's display name
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
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
