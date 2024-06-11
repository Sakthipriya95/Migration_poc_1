package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2L Responsibility Model class
 *
 * @author apj4cob
 */
public class A2lResp implements Comparable<A2lResp>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = -4610960843453352235L;
  /**
   * A2l Resp Id
   */
  private Long id;
  /**
   * Pidc A2l Id
   */
  private Long pidcA2lId;
  /**
   * Wp Type Id
   */
  private Long wpTypeId;
  /**
   * Wp Root Id
   */
  private Long wpRootId;
  /**
   * Resp Var Id
   */
  private Long respVarId;
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
   * @return pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId set pidcA2lId
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return wpTypeId
   */
  public Long getWpTypeId() {
    return this.wpTypeId;
  }

  /**
   * @param wpTypeId set wpTypeId
   */
  public void setWpTypeId(final Long wpTypeId) {
    this.wpTypeId = wpTypeId;
  }

  /**
   * @return wpRootId
   */
  public Long getWpRootId() {
    return this.wpRootId;
  }

  /**
   * @param wpRootId set wpRootId
   */
  public void setWpRootId(final Long wpRootId) {
    this.wpRootId = wpRootId;
  }

  /**
   * @return respVarId
   */
  public Long getRespVarId() {
    return this.respVarId;
  }

  /**
   * @param respVarId set respVarId
   */
  public void setRespVarId(final Long respVarId) {
    this.respVarId = respVarId;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lResp object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return ModelUtil.isEqual(getId(), ((A2lResp) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
