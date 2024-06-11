/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;

/**
 * This class stores attributes of an LDAP user
 *
 * @author adn1cob
 */
public class UserInfo {


  /**
   * Holds the UserID (Account name) of the user
   */
  private String userName = "";

  /**
   * Holds the SURNAME of the user
   */
  private String surName = "";

  /**
   * Holds the FirstName of the user
   */
  private String givenName = "";

  /**
   * Hold the display name of the user
   */
  private String displayName = "";

  /**
   * Holds the distinguished name of the user
   */
  private String distinguishedName = "";

  /**
   * Holds the common name
   */
  private String commonName = "";

  /**
   * Holds the user principal name
   */
  private String userPrincipalName = "";

  /**
   * Holds the company name
   */
  private String company = "";

  /**
   * Holds the department name
   */
  private String department = "";

  /**
   * Holds the country
   */
  private String country = "";

  /**
   * Holds the location
   */
  private String location = "";

  /**
   * Holds the physical delivery address
   */
  private String phyDlvryOffName = "";

  /**
   * Holds the postal code
   */
  private String postalCode = "";

  /**
   * Holds the email address
   */
  private String emailAddress = "";

  /**
   * Holds the street address
   */
  private String streetAddress = "";

  /**
   * Holds the telephone number
   */
  private String telephoneNumber = "";

  /**
   * Holds the description (eg cost center)
   */
  private String description = "";

  /**
   * Holds the list of groups the user is member of.
   */
  private String memberOf = "";

  /**
   * get the User SID
   */
  private String userSID = "";


  /**
   * @return the userSID
   */
  public String getUserSID() {
    return this.userSID;
  }

  /**
   * Methods to get username
   *
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * Method to set username
   *
   * @param userName the userName to set
   */
  protected void setUserName(final String userName) {
    this.userName = userName;
  }

  /**
   * Method to set SurName
   *
   * @param surName the surName to set
   */
  protected void setSurName(final String surName) {
    this.surName = surName;
  }

  /**
   * Method to get SurName
   *
   * @return the surName
   */
  public String getSurName() {
    return this.surName;
  }

  /**
   * Method to set given name
   *
   * @param givenName the givenName to set
   */
  protected void setGivenName(final String givenName) {
    this.givenName = givenName;
  }

  /**
   * Method to get given name
   *
   * @return the givenName
   */
  public String getGivenName() {
    return this.givenName;
  }

  /**
   * Method to set display name
   *
   * @param displayName the displayName to set
   */
  protected void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  /**
   * Method to get display name
   *
   * @return the displayName
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * Method to set distinguished name
   *
   * @param distinguishedName the distinguishedName to set
   */
  protected void setDistinguishedName(final String distinguishedName) {
    this.distinguishedName = distinguishedName;
  }

  /**
   * Method to set common name
   *
   * @param commonName the commonName to set
   */
  protected void setCommonName(final String commonName) {
    this.commonName = commonName;
  }

  /**
   * Method to get common name
   *
   * @return the commonName
   */
  public String getCommonName() {
    return this.commonName;
  }


  /**
   * @return the userPrincipalName
   */
  public String getUserPrincipalName() {
    return this.userPrincipalName;
  }

  /**
   * Method to set UserPrincipal name
   *
   * @param userPrincipalName the userPrincipalName to set
   */
  protected void setUserPrincipalName(final String userPrincipalName) {
    this.userPrincipalName = userPrincipalName;
  }


  /**
   * Method to set company info
   *
   * @param company the company to set
   */
  protected void setCompany(final String company) {
    this.company = company;
  }

  /**
   * Method to get company info
   *
   * @return the company
   */
  public String getCompany() {
    return this.company;
  }

  /**
   * Method to set department info
   *
   * @param department the department to set
   */
  protected void setDepartment(final String department) {
    this.department = department;
  }

  /**
   * Method to get department info
   *
   * @return the department
   */
  public String getDepartment() {
    return this.department;
  }

  /**
   * Method to set the country
   *
   * @param country the country to set
   */
  protected void setCountry(final String country) {
    this.country = country;
  }

  /**
   * Method to get country
   *
   * @return the country
   */
  public String getCountry() {
    return this.country;
  }

  /**
   * Method to set location info
   *
   * @param location the location to set
   */
  protected void setLocation(final String location) {
    this.location = location;
  }

  /**
   * Method to get location info
   *
   * @return the location
   */
  public String getLocation() {
    return this.location;
  }

  /**
   * Method to set physical delivery office name
   *
   * @param physDelvryOffName the physicalDeliveryOfficeName to set
   */
  protected void setPhysicalDeliveryOfficeName(final String physDelvryOffName) {
    this.phyDlvryOffName = physDelvryOffName;
  }

  /**
   * Method to get physical delivery office info
   *
   * @return the physicalDeliveryOfficeName
   */
  public String getPhysicalDeliveryOfficeName() {
    return this.phyDlvryOffName;
  }

  /**
   * Method to set postal code
   *
   * @param postalCode the postalCode to set
   */
  protected void setPostalCode(final String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * Method to get postal code
   *
   * @return the postalCode
   */
  public String getPostalCode() {
    return this.postalCode;
  }

  /**
   * Method to set email address
   *
   * @param emailAddress the emailAddress to set
   */
  protected void setEmailAddress(final String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * Method to get email address
   *
   * @return the emailAddress
   */
  public String getEmailAddress() {
    return this.emailAddress;
  }

  /**
   * Method to set street address
   *
   * @param streetAddress the streetAddress to set
   */
  protected void setStreetAddress(final String streetAddress) {
    this.streetAddress = streetAddress;
  }

  /**
   * Method to get street address
   *
   * @return the streetAddress
   */
  public String getStreetAddress() {
    return this.streetAddress;
  }

  /**
   * Method to set telephone number
   *
   * @param telephoneNumber the telephoneNumber to set
   */
  protected void setTelephoneNumber(final String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

  /**
   * Method to get telephone number
   *
   * @return the telephoneNumber
   */
  public String getTelephoneNumber() {
    return this.telephoneNumber;
  }

  /**
   * Method to set description
   *
   * @param description the description to set
   */
  protected void setDescription(final String description) {
    this.description = description;
  }

  /**
   * Method to get description
   *
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Method to set list of memberOf details
   *
   * @param memberOf the memberOf to set
   */
  protected void setMemberOf(final String memberOf) {
    this.memberOf = memberOf;
  }

  /**
   * Method to get memberOf details
   *
   * @return the memberOf
   */
  public String getMemberOf() {
    return this.memberOf;
  }

  /**
   * Gets the fully distinguished name
   *
   * @return the distinguishedName
   */
  // iCDM-438
  public String getDistinguishedName() {
    return this.distinguishedName;
  }

  /**
   * @param userSID userSID
   */
  public void setUserSID(final String userSID) {
    this.userSID = userSID;

  }

}
