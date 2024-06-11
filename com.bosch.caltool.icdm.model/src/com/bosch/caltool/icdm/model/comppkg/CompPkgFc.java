package com.bosch.caltool.icdm.model.comppkg;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * TCompPkgBcFc Model class
 *
 * @author say8cob
 */
public class CompPkgFc implements Comparable<CompPkgFc>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 6382116339237742191L;
  /**
   * Comp Bc Fc Id
   */
  private Long id;
  /**
   * Comp Bc Id
   */
  private Long compBcId;
  /**
   * Fc Name
   */
  private String fcName;
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
   * @return compBcId
   */
  public Long getCompBcId() {
    return this.compBcId;
  }

  /**
   * @param compBcId set compBcId
   */
  public void setCompBcId(final Long compBcId) {
    this.compBcId = compBcId;
  }

  /**
   * @return fcName
   */
  public String getFcName() {
    return this.fcName;
  }

  /**
   * @param fcName set fcName
   */
  public void setFcName(final String fcName) {
    this.fcName = fcName;
  }

  /**
   * @return createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgFc object) {
    return ModelUtil.compare(getFcName(), object.getFcName());
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(this.id, ((CompPkgFc) obj).id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(this.id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.fcName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.fcName = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getFcName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // FcName is being used as description
  }

}
