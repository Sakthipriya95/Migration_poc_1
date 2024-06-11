package com.bosch.caltool.icdm.database.entity.general;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the T_USER_LOGIN_INFO database table.
 */
@Entity
@Table(name = "T_USER_LOGIN_INFO")
@NamedQueries(value = {
    @NamedQuery(name = "TUserLoginInfo.findByUserNtId", query = "SELECT t FROM TUserLoginInfo t where t.userNtId = :userNtId") })
public class TUserLoginInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * find query to get
   */
  public static final String GET_USER_LOGIN_INFO = "TUserLoginInfo.findByUserNtId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "USER_LOGIN_INFO_ID", unique = true, nullable = false)
  private long userLoginInfoId;

  @Column(name = "USER_NT_ID", unique = true, nullable = false)
  private String userNtId;

  @Column(name = "AZURE_LOGIN_COUNT")
  private Long azureLoginCount;

  @Column(name = "LDAP_LOGIN_COUNT")
  private Long ldapLoginCount;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;


  /**
   * @return the userLoginInfoId
   */
  public long getUserLoginInfoId() {
    return this.userLoginInfoId;
  }


  /**
   * @param userLoginInfoId the userLoginInfoId to set
   */
  public void setUserLoginInfoId(final long userLoginInfoId) {
    this.userLoginInfoId = userLoginInfoId;
  }


  /**
   * @return the userNtId
   */
  public String getUserNtId() {
    return this.userNtId;
  }


  /**
   * @param userNtId the userNtId to set
   */
  public void setUserNtId(final String userNtId) {
    this.userNtId = userNtId;
  }


  /**
   * @return the azureLoginCount
   */
  public Long getAzureLoginCount() {
    return this.azureLoginCount;
  }


  /**
   * @param azureLoginCount the azureLoginCount to set
   */
  public void setAzureLoginCount(final Long azureLoginCount) {
    this.azureLoginCount = azureLoginCount;
  }


  /**
   * @return the ldapLoginCount
   */
  public Long getLdapLoginCount() {
    return this.ldapLoginCount;
  }


  /**
   * @param ldapLoginCount the ldapLoginCount to set
   */
  public void setLdapLoginCount(final Long ldapLoginCount) {
    this.ldapLoginCount = ldapLoginCount;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }


}