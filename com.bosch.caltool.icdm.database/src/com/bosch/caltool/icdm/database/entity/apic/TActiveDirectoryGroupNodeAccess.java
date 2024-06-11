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
 * The persistent class for the T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS database table.
 */
@Entity
@Table(name = "T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE)
@NamedQueries({
    @NamedQuery(name = TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_ID, query = "SELECT acc FROM TActiveDirectoryGroupNodeAccess acc where acc.nodeId = :nodeId"),
    @NamedQuery(name = TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_ID_GROUP_ID, query = "SELECT acc FROM TActiveDirectoryGroupNodeAccess acc where acc.nodeId = :nodeId and acc.activeDirectoryGroup = :groupId"),
    @NamedQuery(name = TActiveDirectoryGroupNodeAccess.NQ_GET_BY_GROUP_ID_SET, query = "SELECT acc FROM TActiveDirectoryGroupNodeAccess acc where acc.activeDirectoryGroup in :groupIdSet"),
    @NamedQuery(name = TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_ID_SET, query = "SELECT acc FROM TActiveDirectoryGroupNodeAccess acc where acc.nodeId in :nodeIdSet"),
    @NamedQuery(name = TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_TYPE, query = "SELECT acc FROM TActiveDirectoryGroupNodeAccess acc where acc.nodeType = :nodeType"),
    @NamedQuery(name = TActiveDirectoryGroupNodeAccess.NQ_GET_BY_USERNAME_SET, query = "SELECT acc FROM TActiveDirectoryGroupNodeAccess acc,TActiveDirectoryGroupUser grpUsr where acc.activeDirectoryGroup.adGroupId = grpUsr.activeDirectoryGroup.adGroupId and grpUsr.userName in :userName and acc.nodeType = :nodeType") })


public class TActiveDirectoryGroupNodeAccess implements Serializable {

  /**
   *
   */
  public static final String NQ_GET_BY_NODE_ID = "TActiveDirectoryGroupNodeAccess.getByNodeId";
  /**
   *
   */
  public static final String NQ_GET_BY_NODE_ID_SET = "TActiveDirectoryGroupNodeAccess.getByNodeIdSet";
  /**
   *
   */
  public static final String NQ_GET_BY_GROUP_ID_SET = "TActiveDirectoryGroupNodeAccess.getByGroupIdSet";
  /**
   *
   */
  public static final String NQ_GET_BY_NODE_ID_GROUP_ID = "TActiveDirectoryGroupNodeAccess.getByNodeIdAndGroupId";
  /**
  *
  */
  public static final String NQ_GET_BY_USERNAME_SET = "TActiveDirectoryGroupNodeAccess.getByUsernameSet";
  /**
  *
  */
  public static final String NQ_GET_BY_NODE_TYPE = "TActiveDirectoryGroupNodeAccess.getByNodeType";


  private static final long serialVersionUID = 1L;
  private long groupAccessId;
  private Long nodeId;
  private String nodeType;
  private TActiveDirectoryGroup activeDirectoryGroup;
  private String readright;
  private String grantright;
  private String writeright;
  private String owner;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private Long version;

  public TActiveDirectoryGroupNodeAccess() {}


  /**
   * @return the groupAccessId
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "GROUP_ACCESS_ID", unique = true, nullable = false)
  public long getNodeaccessId() {
    return this.groupAccessId;
  }


  /**
   * @param groupAccessId the nodeaccessId to set
   */
  public void setNodeaccessId(final long groupAccessId) {
    this.groupAccessId = groupAccessId;
  }


  /**
   * @return the nodeId
   */
  @Column(name = "NODE_ID", nullable = false, precision = 15)
  public Long getNodeId() {
    return this.nodeId;
  }


  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final Long nodeId) {
    this.nodeId = nodeId;
  }


  /**
   * @return the nodeType
   */
  @Column(name = "NODE_TYPE", nullable = false, length = 15)
  public String getNodeType() {
    return this.nodeType;
  }


  /**
   * @param nodeType the nodeType to set
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * @return the tabvApicADGrpDetails
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AD_GROUP_ID")
  public TActiveDirectoryGroup getActiveDirectoryGroup() {
    return this.activeDirectoryGroup;
  }


  /**
   * @param tActiveDirectoryGroup the tActiveDirectoryGroup to set
   */
  public void setActiveDirectoryGroup(final TActiveDirectoryGroup tActiveDirectoryGroup) {
    this.activeDirectoryGroup = tActiveDirectoryGroup;
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
   * @return the readright
   */
  @Column(length = 1)
  public String getReadright() {
    return this.readright;
  }


  /**
   * @param readright the readright to set
   */
  public void setReadright(final String readright) {
    this.readright = readright;
  }

  /**
   * @return the writeright
   */
  @Column(length = 1)
  public String getWriteright() {
    return this.writeright;
  }


  /**
   * @param writeright the writeright to set
   */
  public void setWriteright(final String writeright) {
    this.writeright = writeright;
  }

  /**
   * @return the grantright
   */
  @Column(length = 1)
  public String getGrantright() {
    return this.grantright;
  }


  /**
   * @param grantright the grantright to set
   */
  public void setGrantright(final String grantright) {
    this.grantright = grantright;
  }

  /**
   * @return the owner
   */
  @Column(length = 1)
  public String getOwner() {
    return this.owner;
  }


  /**
   * @param owner the owner to set
   */
  public void setOwner(final String owner) {
    this.owner = owner;
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