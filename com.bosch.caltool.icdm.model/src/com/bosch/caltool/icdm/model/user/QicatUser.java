/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import com.bosch.caltool.datamodel.core.IBasicObject;

/**
 * @author nip4cob
 */
public class QicatUser implements IBasicObject {

  private String ciDepartment;
  private String ciFirstname;
  private String ciGender;
  private String ciLastname;
  private String ciTitle;
  private String costcenter;
  private String countryCode;
  private String countryDial;
  private String countryName;
  private String domain;
  private String email;
  private String fax;
  private String phone;
  private String name;
  private String description;

  /**
   * @return the ciDepartment
   */
  public String getCiDepartment() {
    return this.ciDepartment;
  }

  /**
   * @param ciDepartment the ciDepartment to set
   */
  public void setCiDepartment(final String ciDepartment) {
    this.ciDepartment = ciDepartment;
  }

  /**
   * @return the ciFirstname
   */
  public String getCiFirstname() {
    return this.ciFirstname;
  }

  /**
   * @param ciFirstname the ciFirstname to set
   */
  public void setCiFirstname(final String ciFirstname) {
    this.ciFirstname = ciFirstname;
  }

  /**
   * @return the ciGender
   */
  public String getCiGender() {
    return this.ciGender;
  }

  /**
   * @param ciGender the ciGender to set
   */
  public void setCiGender(final String ciGender) {
    this.ciGender = ciGender;
  }

  /**
   * @return the ciLastname
   */
  public String getCiLastname() {
    return this.ciLastname;
  }

  /**
   * @param ciLastname the ciLastname to set
   */
  public void setCiLastname(final String ciLastname) {
    this.ciLastname = ciLastname;
  }

  /**
   * @return the ciTitle
   */
  public String getCiTitle() {
    return this.ciTitle;
  }

  /**
   * @param ciTitle the ciTitle to set
   */
  public void setCiTitle(final String ciTitle) {
    this.ciTitle = ciTitle;
  }

  /**
   * @return the costcenter
   */
  public String getCostcenter() {
    return this.costcenter;
  }

  /**
   * @param costcenter the costcenter to set
   */
  public void setCostcenter(final String costcenter) {
    this.costcenter = costcenter;
  }

  /**
   * @return the countryCode
   */
  public String getCountryCode() {
    return this.countryCode;
  }

  /**
   * @param countryCode the countryCode to set
   */
  public void setCountryCode(final String countryCode) {
    this.countryCode = countryCode;
  }

  /**
   * @return the countryDial
   */
  public String getCountryDial() {
    return this.countryDial;
  }

  /**
   * @param countryDial the countryDial to set
   */
  public void setCountryDial(final String countryDial) {
    this.countryDial = countryDial;
  }

  /**
   * @return the countryName
   */
  public String getCountryName() {
    return this.countryName;
  }

  /**
   * @param countryName the countryName to set
   */
  public void setCountryName(final String countryName) {
    this.countryName = countryName;
  }

  /**
   * @return the domain
   */
  public String getDomain() {
    return this.domain;
  }

  /**
   * @param domain the domain to set
   */
  public void setDomain(final String domain) {
    this.domain = domain;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(final String email) {
    this.email = email;
  }

  /**
   * @return the fax
   */
  public String getFax() {
    return this.fax;
  }

  /**
   * @param fax the fax to set
   */
  public void setFax(final String fax) {
    this.fax = fax;
  }

  /**
   * @return the phone
   */
  public String getPhone() {
    return this.phone;
  }

  /**
   * @param phone the phone to set
   */
  public void setPhone(final String phone) {
    this.phone = phone;
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


}
