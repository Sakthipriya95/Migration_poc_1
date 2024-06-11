package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2L WP RESPONSIBILITY Model class
 *
 * @author gge6cob
 */
public class A2lWpResp implements Comparable<A2lWpResp>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 7178339022093653991L;

  /**
   * A2l Wp Resp Id
   */
  private Long id;
  /**
   * A2l Resp Id
   */
  private Long a2lRespId;
  /**
   * WP Resp Id
   */
  private Long wpRespId;
  /**
   * WP Resp Enum
   */
  private WpRespType wpRespEnum;
  /**
   * Wp Id
   */
  private Long wpId;
  /**
   * A2l Group Id
   */
  private Long a2lGroupId;
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
   * @return wpId
   */
  public Long getWpId() {
    return this.wpId;
  }

  /**
   * @param wpId set wpId
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }

  /**
   * @return a2lGroupId
   */
  public Long getA2lGroupId() {
    return this.a2lGroupId;
  }

  /**
   * @param a2lGroupId set a2lGroupId
   */
  public void setA2lGroupId(final Long a2lGroupId) {
    this.a2lGroupId = a2lGroupId;
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
   * @return the wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }


  /**
   * @param wpRespId the wpRespId to set
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
  }


  /**
   * @return the wpRespEnum
   */
  public WpRespType getWpRespEnum() {
    return this.wpRespEnum;
  }


  /**
   * @param wpRespEnum the wpRespEnum to set
   */
  public void setWpRespEnum(final WpRespType wpRespEnum) {
    this.wpRespEnum = wpRespEnum;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWpResp object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWpResp) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
