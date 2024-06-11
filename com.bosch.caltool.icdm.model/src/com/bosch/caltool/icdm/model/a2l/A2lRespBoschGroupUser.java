package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2L Responsibility Bosch Group User Model class
 *
 * @author PDH2COB
 */
public class A2lRespBoschGroupUser implements Comparable<A2lRespBoschGroupUser>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 239172895438134L;
  /**
   * A2lresp Bshgrp Usr Id
   */
  private Long id;
  /**
   * A2l Resp Id
   */
  private Long a2lRespId;
  /**
   * User Id
   */
  private Long userId;

  /**
   * NT ID of user
   */
  private String name;

  /**
   * Pidc Id
   */
  private Long projectId;

  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
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
   * @return the projectId
   */
  public Long getProjectId() {
    return this.projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }

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
   * @return a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }

  /**
   * @param a2lRespId set a2lRespId
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
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
  public int compareTo(final A2lRespBoschGroupUser obj) {
    return ModelUtil.compare(getUserId(), obj.getUserId());
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lRespBoschGroupUser) obj).getId()) &&
        ModelUtil.isEqual(getA2lRespId(), ((A2lRespBoschGroupUser) obj).getA2lRespId()) &&
        ModelUtil.isEqual(getUserId(), ((A2lRespBoschGroupUser) obj).getUserId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId(), getA2lRespId(), getUserId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // NA

  }

}
