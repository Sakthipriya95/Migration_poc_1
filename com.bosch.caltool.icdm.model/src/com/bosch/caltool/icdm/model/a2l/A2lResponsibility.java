package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2lResponsibility Model class
 *
 * @author pdh2cob
 */
public class A2lResponsibility implements Cloneable, Comparable<A2lResponsibility>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 140819447711790L;
  /**
   * Id
   */
  private Long id;
  /**
   * Pidc Id
   */
  private Long projectId;
  /**
   * Cust Last Name
   */
  private String lLastName;
  /**
   * Cust First Name
   */
  private String lFirstName;
  /**
   * Cust Dept
   */
  private String lDepartment;
  /**
   * User Id
   */
  private Long userId;
  /**
   * Alias Name
   */
  private String aliasName;
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
   * Responsibility type code (e.g. R, C) Refer {@link WpRespType} for details
   */
  private String respType;

  /**
   * deleted flag for resp
   */
  private boolean deleted;
  private String name;
  private String description;


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
   * @return project
   */
  public Long getProjectId() {
    return this.projectId;
  }

  /**
   * @param projectId set pidcId
   */
  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }

  /**
   * @return custLastName
   */
  public String getLLastName() {
    return this.lLastName;
  }

  /**
   * @param lastName set LastName
   */
  public void setLLastName(final String lastName) {
    this.lLastName = lastName;
  }

  /**
   * @return First Name
   */
  public String getLFirstName() {
    return this.lFirstName;
  }

  /**
   * @param firstName set FirstName
   */
  public void setLFirstName(final String firstName) {
    this.lFirstName = firstName;
  }

  /**
   * @return department
   */
  public String getLDepartment() {
    return this.lDepartment;
  }

  /**
   * @param department set department
   */
  public void setLDepartment(final String department) {
    this.lDepartment = department;
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
   * @return aliasName
   */
  public String getAliasName() {
    return this.aliasName;
  }

  /**
   * @param aliasName set aliasName
   */
  public void setAliasName(final String aliasName) {
    this.aliasName = aliasName;
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
   * @return the respType
   */
  public String getRespType() {
    return this.respType;
  }

  /**
   * @param respTyp resp type to set
   */
  public void setRespType(final String respTyp) {
    this.respType = respTyp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lResponsibility object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lResponsibility) obj).getId()) &&
        ModelUtil.isEqual(getRespType(), ((A2lResponsibility) obj).getRespType());

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
    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A2lResponsibility clone() {

    A2lResponsibility resp = null;
    try {
      resp = (A2lResponsibility) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return resp;
  }


  /**
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


}
