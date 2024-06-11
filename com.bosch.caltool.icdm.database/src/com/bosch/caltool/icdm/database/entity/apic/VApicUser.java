/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic;


/**
 * @author NIP4COB
 */

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the V_APIC_USERS database table.
 */
@Entity
@Table(name = "V_APIC_USERS")
@NamedQueries(value = {
    @NamedQuery(name = "VApicUser.findAll", query = "SELECT v FROM VApicUser v"),
    @NamedQuery(name = VApicUser.NQ_GET_USER_INFO_BY_USER_EMAIL, query = "select user from VApicUser user where user.email = :email") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VApicUser implements Serializable {

  /**
   *
   */
  public static final String NQ_GET_ALL = "VApicUsers.findAll";
  /**
   * Query to find a user by email
   */
  public static final String NQ_GET_USER_INFO_BY_USER_EMAIL = "VApicUsers.getuserinfobyuseremail";

  private static final long serialVersionUID = 1L;

  private String costcenter;

  @Column(name = "COUNTRY_CODE")
  private String countryCode;

  @Column(name = "COUNTRY_DIAL")
  private String countryDial;

  @Column(name = "COUNTRY_NAME")
  private String countryName;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  private String department;

  @Column(name = "DISCLMR_ACCEPTED_DATE")
  private Timestamp disclmrAcceptedDate;

  @Column(name = "\"DOMAIN\"")
  private String domain;

  private String email;

  private String fax;

  private String firstname;

  @Column(name = "IS_DELETED")
  private String isDeleted;

  private String lastname;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  private String phone;

  @Id
  @Column(name = "USER_ID")
  private long userId;

  private String username;

  @Column(name = "\"VERSION\"")
  private long version;

  public VApicUser() {}

  public String getCostcenter() {
    return this.costcenter;
  }

  public void setCostcenter(final String costcenter) {
    this.costcenter = costcenter;
  }

  public String getCountryCode() {
    return this.countryCode;
  }

  public void setCountryCode(final String countryCode) {
    this.countryCode = countryCode;
  }

  public String getCountryDial() {
    return this.countryDial;
  }

  public void setCountryDial(final String countryDial) {
    this.countryDial = countryDial;
  }

  public String getCountryName() {
    return this.countryName;
  }

  public void setCountryName(final String countryName) {
    this.countryName = countryName;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getDepartment() {
    return this.department;
  }

  public void setDepartment(final String department) {
    this.department = department;
  }

  public Timestamp getDisclmrAcceptedDate() {
    return this.disclmrAcceptedDate;
  }

  public void setDisclmrAcceptedDate(final Timestamp disclmrAcceptedDate) {
    this.disclmrAcceptedDate = disclmrAcceptedDate;
  }

  public String getDomain() {
    return this.domain;
  }

  public void setDomain(final String domain) {
    this.domain = domain;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getFax() {
    return this.fax;
  }

  public void setFax(final String fax) {
    this.fax = fax;
  }

  public String getFirstname() {
    return this.firstname;
  }

  public void setFirstname(final String firstname) {
    this.firstname = firstname;
  }

  public String getIsDeleted() {
    return this.isDeleted;
  }

  public void setIsDeleted(final String isDeleted) {
    this.isDeleted = isDeleted;
  }

  public String getLastname() {
    return this.lastname;
  }

  public void setLastname(final String lastname) {
    this.lastname = lastname;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public long getUserId() {
    return this.userId;
  }

  public void setUserId(final long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

}