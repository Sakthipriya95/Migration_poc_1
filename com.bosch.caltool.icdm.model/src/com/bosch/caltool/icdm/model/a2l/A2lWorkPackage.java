package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2l Workpackage Model class
 *
 * @author pdh2cob
 */
public class A2lWorkPackage implements Cloneable, Comparable<A2lWorkPackage>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 48540901204081L;
  /**
   * A2l Wp Pal Id
   */
  private Long id;
  /**
   * Wp Name
   */
  private String name;
  /**
   * Wp Desc
   */
  private String description;
  /**
   * Wp Name Cust
   */
  private String nameCustomer;
  /**
   * Pidc Version Id
   */
  private Long pidcVersId;

  /**
   * Id of parent A2lWpId
   */
  private Long parentA2lWpId;

  /**
   * Version
   */
  private Long version;
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
   * @return pidcVersionId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersionId set pidcVersionId
   */
  public void setPidcVersId(final Long pidcVersionId) {
    this.pidcVersId = pidcVersionId;
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
  public A2lWorkPackage clone() {
    A2lWorkPackage wp = null;
    try {
      wp = (A2lWorkPackage) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return wp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWorkPackage object) {
    return ModelUtil.compare(getName(), object.getName());
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWorkPackage) obj).getId());

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
   * @return the nameCustomer
   */
  public String getNameCustomer() {
    return this.nameCustomer;
  }


  /**
   * @param nameCustomer the nameCustomer to set
   */
  public void setNameCustomer(final String nameCustomer) {
    this.nameCustomer = nameCustomer;
  }


  /**
   * @return the parentA2lWpId
   */
  public Long getParentA2lWpId() {
    return this.parentA2lWpId;
  }


  /**
   * @param parentA2lWpId the parentA2lWpId to set
   */
  public void setParentA2lWpId(final Long parentA2lWpId) {
    this.parentA2lWpId = parentA2lWpId;
  }


}
