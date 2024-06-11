package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the V_QICAT_USERS database table.
 */
@Entity
@Table(name = "V_QICAT_USERS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQuery(name = VQicatUser.NQ_GET_BY_NTID_LIST, query = "SELECT user FROM VQicatUser user,GttObjectName temp where temp.objName = user.username")

public class VQicatUser implements Serializable {

  /**
   *
   */
  public static final String NQ_GET_BY_NTID_LIST = "VQicatUser.getByUserName";
  private static final long serialVersionUID = 1L;

  @Column(name = "CI_DEPARTMENT")
  private String ciDepartment;

  @Column(name = "CI_FIRSTNAME")
  private String ciFirstname;

  @Column(name = "CI_GENDER")
  private String ciGender;

  @Column(name = "CI_LASTNAME")
  private String ciLastname;

  @Column(name = "CI_TITLE")
  private String ciTitle;

  private String costcenter;

  @Column(name = "COUNTRY_CODE")
  private String countryCode;

  @Column(name = "COUNTRY_DIAL")
  private String countryDial;

  @Column(name = "COUNTRY_NAME")
  private String countryName;

  @Column(name = "\"DOMAIN\"")
  private String domain;

  private String email;

  private String fax;

  private String phone;

  @Id
  private String username;

  public VQicatUser() {}

  public String getCiDepartment() {
    return this.ciDepartment;
  }

  public void setCiDepartment(final String ciDepartment) {
    this.ciDepartment = ciDepartment;
  }

  public String getCiFirstname() {
    return this.ciFirstname;
  }

  public void setCiFirstname(final String ciFirstname) {
    this.ciFirstname = ciFirstname;
  }

  public String getCiGender() {
    return this.ciGender;
  }

  public void setCiGender(final String ciGender) {
    this.ciGender = ciGender;
  }

  public String getCiLastname() {
    return this.ciLastname;
  }

  public void setCiLastname(final String ciLastname) {
    this.ciLastname = ciLastname;
  }

  public String getCiTitle() {
    return this.ciTitle;
  }

  public void setCiTitle(final String ciTitle) {
    this.ciTitle = ciTitle;
  }

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

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }


  public String getUsername() {
    return this.username;
  }


  public void setUsername(final String username) {
    this.username = username;
  }

}