package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * ActiveDirectoryGroupUser Model class
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupUser implements Cloneable, Comparable<ActiveDirectoryGroupUser>, IDataObject {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 321691405141396L;
  /**
   * Group Users Id
   */
  private Long id;
  /**
   * Ad Group Id
   */
  private ActiveDirectoryGroup adGroup;
  /**
   * Username
   */
  private String username;

  /**
   * UserId
   */
  private Long userId;

  /**
   * Is Icdm User
   */
  private String isIcdmUser;
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


  private String groupUserName;
  private String groupUserDept;

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
   * @return username
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @param username set username
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * @return isIcdmUser
   */
  public String getIsIcdmUser() {
    return this.isIcdmUser;
  }

  /**
   * @param isIcdmUser set isIcdmUser
   */
  public void setIsIcdmUser(final String isIcdmUser) {
    this.isIcdmUser = isIcdmUser;
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
  public ActiveDirectoryGroupUser clone() {
    try {
      return (ActiveDirectoryGroupUser) super.clone();
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
  public int compareTo(final ActiveDirectoryGroupUser object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return ModelUtil.isEqual(getId(), ((ActiveDirectoryGroupUser) obj).getId());
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
    return null;
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

  /**
   * @return the groupUserName
   */
  public String getGroupUserName() {
    return this.groupUserName;
  }

  /**
   * @param groupUserName the groupUserName to set
   */
  public void setGroupUserName(final String groupUserName) {
    this.groupUserName = groupUserName;
  }

  /**
   * @return the groupUserDept
   */
  public String getGroupUserDept() {
    return this.groupUserDept;
  }

  /**
   * @param groupUserDept the groupUserDept to set
   */
  public void setGroupUserDept(final String groupUserDept) {
    this.groupUserDept = groupUserDept;
  }

  /**
   * @return the userId
   */
  public Long getUserId() {
    return this.userId;
  }


  /**
   * @param userId the userId to set
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
  }

}
