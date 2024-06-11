package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * ActiveDirectoryGroupNodeAccess Model class
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupNodeAccess
    implements Cloneable, Comparable<ActiveDirectoryGroupNodeAccess>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 321338525189905L;
  /**
   * Group Access Id
   */
  private Long id;
  /**
   * Node Id
   */
  private Long nodeId;
  /**
   * Node Type
   */
  private String nodeType;
  /**
   * Ad Group
   */
  private ActiveDirectoryGroup adGroup;
  /**
   * Readright
   */
  private boolean read;
  /**
   * Writeright
   */
  private boolean write;
  /**
   * Grantright
   */
  private boolean grant;
  /**
   * Owner
   */
  private boolean owner;
  /**
   * Is Set By System
   */
  private String isSetBySystem;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return nodeId
   */
  public Long getNodeId() {
    return this.nodeId;
  }

  /**
   * @param nodeId set nodeId
   */
  public void setNodeId(final Long nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * @return nodeType
   */
  public String getNodeType() {
    return this.nodeType;
  }

  /**
   * @param nodeType set nodeType
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * @return adGroupId
   */
  public ActiveDirectoryGroup getAdGroup() {
    return this.adGroup;
  }

  /**
   * @param adGroup set adGroup
   */
  public void setAdGroup(final ActiveDirectoryGroup adGroup) {
    this.adGroup = adGroup;
  }

  /**
   * @return readright
   */
  public boolean isRead() {
    return this.read;
  }

  /**
   * @param readright set readright
   */
  public void setRead(final boolean readright) {
    this.read = readright;
  }

  /**
   * @return writeright
   */
  public boolean isWrite() {
    return this.write;
  }

  /**
   * @param writeright set writeright
   */
  public void setWrite(final boolean writeright) {
    this.write = writeright;
  }

  /**
   * @return grantright
   */
  public boolean isGrant() {
    return this.grant;
  }

  /**
   * @param grantright set grantright
   */
  public void setGrant(final boolean grantright) {
    this.grant = grantright;
  }

  /**
   * @return owner
   */
  public boolean isOwner() {
    return this.owner;
  }

  /**
   * @param owner set owner
   */
  public void setOwner(final boolean owner) {
    this.owner = owner;
  }

  /**
   * @return isSetBySystem
   */
  public String getIsSetBySystem() {
    return this.isSetBySystem;
  }

  /**
   * @param isSetBySystem set isSetBySystem
   */
  public void setIsSetBySystem(final String isSetBySystem) {
    this.isSetBySystem = isSetBySystem;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActiveDirectoryGroupNodeAccess clone() {
    try {
      return (ActiveDirectoryGroupNodeAccess) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // TODO Exception Logging
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ActiveDirectoryGroupNodeAccess object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return ModelUtil.isEqual(getId(), ((ActiveDirectoryGroupNodeAccess) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return getAdGroup().getGroupName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // TODO Auto-generated method stub

  }

}
