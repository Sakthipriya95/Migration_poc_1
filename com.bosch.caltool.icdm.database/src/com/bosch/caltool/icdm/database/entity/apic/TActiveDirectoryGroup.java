package com.bosch.caltool.icdm.database.entity.apic;

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
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * The persistent class for the T_ACTIVE_DIRECTORY_GROUPS database table.
 */
@Entity
@Table(name = "T_ACTIVE_DIRECTORY_GROUPS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE)
@NamedQueries({
    @NamedQuery(name = TActiveDirectoryGroup.NQ_GET_ALL, query = "SELECT acc FROM TActiveDirectoryGroup acc"),
    @NamedQuery(name = TActiveDirectoryGroup.NQ_GET_BY_GRP_SID_NAME, query = "SELECT acc FROM TActiveDirectoryGroup acc where acc.groupName = :grpName") })
public class TActiveDirectoryGroup implements Serializable {

  public static final String NQ_GET_ALL = "TActiveDirectoryGroup.getALL";
  public static final String NQ_GET_BY_GRP_SID_NAME = "TActiveDirectoryGroup.getByGroupNameSID";
  private static final long serialVersionUID = 1L;
  private long adGroupId;
  private String groupName;
  private String groupSid;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private Long version;

  public TActiveDirectoryGroup() {}


  /**
   * @return the adGroupId
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "AD_GROUP_ID", unique = true, nullable = false)
  public long getAdGroupId() {
    return this.adGroupId;
  }


  /**
   * @param adGroupId the adGroupId to set
   */
  public void setAdGroupId(final long adGroupId) {
    this.adGroupId = adGroupId;
  }

  /**
   * @return the groupName
   */
  @Column(name = "GROUP_NAME", length = 4000)
  public String getGroupName() {
    return this.groupName;
  }


  /**
   * @param groupName the adGroupName to set
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }

  /**
   * @return the adGroupName
   */
  @Column(name = "GROUP_SID", unique = true, nullable = false, length = 4000)
  public String getGroupSid() {
    return this.groupSid;
  }


  /**
   * @param groupSid the groupSid to set
   */
  public void setGroupSid(final String groupSid) {
    this.groupSid = groupSid;
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