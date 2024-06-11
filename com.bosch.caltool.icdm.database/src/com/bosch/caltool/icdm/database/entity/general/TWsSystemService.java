package com.bosch.caltool.icdm.database.entity.general;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_WS_SYSTEMS database table.
 */
@Entity
@Table(name = "T_WS_SYSTEM_SERVICES")
public class TWsSystemService implements Serializable {


  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "SERV_SYS_ID")
  private long servSysId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SYSTEM_ID")
  private TWsSystem tWsSystem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WS_SERV_ID")
  private TWsService tWsService;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  /**
   * constrcutor empty
   */
  public TWsSystemService() {
    // empty default created by framework.
  }


  /**
   * @return the servSysId
   */
  public long getServSysId() {
    return this.servSysId;
  }


  /**
   * @param servSysId the servSysId to set
   */
  public void setServSysId(final long servSysId) {
    this.servSysId = servSysId;
  }


  /**
   * @return the tWsSystem
   */
  public TWsSystem getTWsSystem() {
    return this.tWsSystem;
  }


  /**
   * @param tWsSystem the tWsSystem to set
   */
  public void setTWsSystem(final TWsSystem tWsSystem) {
    this.tWsSystem = tWsSystem;
  }


  /**
   * @return the tWsService
   */
  public TWsService getTWsService() {
    return this.tWsService;
  }


  /**
   * @param tWsService the tWsService to set
   */
  public void setTWsService(final TWsService tWsService) {
    this.tWsService = tWsService;
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
  public long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }


}