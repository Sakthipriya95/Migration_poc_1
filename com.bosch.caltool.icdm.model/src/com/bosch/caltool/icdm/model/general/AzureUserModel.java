/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;


/**
 * @author MSP5COB
 */
public class AzureUserModel {

  private String userName;
  private String firstName;
  private String lastName;
  private String department;

  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(final String userName) {
    this.userName = userName;
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

}
