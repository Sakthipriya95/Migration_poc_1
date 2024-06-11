package com.bosch.caltool.icdm.model.uc;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * UsecaseFavorite Model class
 *
 * @author dmo5cob
 */
public class UsecaseFavorite implements Comparable<UsecaseFavorite>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 1937043947369842318L;
  /**
   * Uc Fav Id
   */
  private Long id;
  /**
   * Group Id
   */
  private Long groupId;
  /**
   * Use Case Id
   */
  private Long useCaseId;
  /**
   * Section Id
   */
  private Long sectionId;
  /**
   * User Id
   */
  private Long userId;
  /**
   * Project Id
   */
  private Long projectId;
  /**
   * PIDC Vers ID
   */
  private Long pidcVersId;

  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
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
   * @return groupId
   */
  public Long getGroupId() {
    return this.groupId;
  }

  /**
   * @param groupId set groupId
   */
  public void setGroupId(final Long groupId) {
    this.groupId = groupId;
  }

  /**
   * @return useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }

  /**
   * @param useCaseId set useCaseId
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }

  /**
   * @return sectionId
   */
  public Long getSectionId() {
    return this.sectionId;
  }

  /**
   * @param sectionId set sectionId
   */
  public void setSectionId(final Long sectionId) {
    this.sectionId = sectionId;
  }

  /**
   * @return userId
   */
  public Long getUserId() {
    return this.userId;
  }

  /**
   * @param userId set userId
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  /**
   * @return projectId
   */
  public Long getProjectId() {
    return this.projectId;
  }

  /**
   * @param projectId set projectId
   */
  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
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
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return pidcVersId;
  }

  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final UsecaseFavorite object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((UsecaseFavorite) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
