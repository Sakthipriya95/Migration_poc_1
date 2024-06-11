package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * The persistent class for the T_ACTIVE_DIRECTORY_GROUP_USERS database table.
 */
@Entity
@Table(name = "T_ACTIVE_DIRECTORY_GROUP_USERS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE)
@NamedQueries({
    @NamedQuery(name = TActiveDirectoryGroupUser.NQ_GET_ALL, query = "SELECT acc FROM TActiveDirectoryGroupUser acc"),
    @NamedQuery(name = TActiveDirectoryGroupUser.NQ_GET_BY_USER_ID, query = "SELECT acc FROM TActiveDirectoryGroupUser acc where acc.userName = :userId"),
    @NamedQuery(name = TActiveDirectoryGroupUser.NQ_GET_BY_GROUP_ID, query = "SELECT acc FROM TActiveDirectoryGroupUser acc where acc.activeDirectoryGroup = :groupId"),
    @NamedQuery(name = TActiveDirectoryGroupUser.NQ_DELETE_BY_GROUP_ID, query = "DELETE FROM TActiveDirectoryGroupUser acc where acc.activeDirectoryGroup = :groupId") })

public class TActiveDirectoryGroupUser implements Serializable {

  public static final String NQ_GET_ALL = "TActiveDirectoryGroupUser.getAll";
  public static final String NQ_GET_BY_GROUP_ID = "TActiveDirectoryGroupUser.getByGroupId";
  public static final String NQ_GET_BY_USER_ID = "TActiveDirectoryGroupUser.getByUserId";
  public static final String NQ_DELETE_BY_GROUP_ID = "TActiveDirectoryGroupUser.deleteByGroupId";

  private static final long serialVersionUID = 1L;
  private Long groupUsersId;
  private String userName;
  private TActiveDirectoryGroup activeDirectoryGroup;
  private String isIcdmUser;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private Long version;


  public TActiveDirectoryGroupUser() {}


  /**
   * @return the groupUsersId
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "GROUP_USERS_ID", nullable = false)
  public Long getGroupUsersId() {
    return this.groupUsersId;
  }


  /**
   * @param groupUsersId the groupUsersId to set
   */
  public void setGroupUsersId(final Long groupUsersId) {
    this.groupUsersId = groupUsersId;
  }

  /**
   * @return the userName
   */
  @Column(name = "USERNAME", nullable = false, length = 30)

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
   * @return the TActiveDirectoryGroup
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AD_GROUP_ID")
  public TActiveDirectoryGroup getActiveDirectoryGroup() {
    return this.activeDirectoryGroup;
  }


  /**
   * @param tActiveDirectoryGroup the TActiveDirectoryGroup to set
   */
  public void setActiveDirectoryGroup(final TActiveDirectoryGroup tActiveDirectoryGroup) {
    this.activeDirectoryGroup = tActiveDirectoryGroup;
  }

  /**
   * @return the userName
   */
  @Column(name = "IS_ICDM_USER")

  public String getIsIcdmUser() {
    return this.isIcdmUser;
  }


  /**
   * @param userName the userName to set
   */
  public void setIsIcdmUser(final String isIcdmUser) {
    this.isIcdmUser = isIcdmUser;
  }

  /**
   * @return the createdDate
   */
  @Column(name = "CREATED_DATE", nullable = false)
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
  @Column(name = "CREATED_USER", nullable = false, length = 15)

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
  @Column(name = "MODIFIED_DATE")
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
  @Column(name = "MODIFIED_USER", length = 30)
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
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
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