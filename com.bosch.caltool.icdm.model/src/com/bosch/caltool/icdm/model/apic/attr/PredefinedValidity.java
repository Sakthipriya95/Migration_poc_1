package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * PredefinedValidity Model class
 *
 * @author pdh2cob
 */
public class PredefinedValidity implements Comparable<PredefinedValidity>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 3218680133775135478L;
  /**
   * Pre Valdty Id
   */
  private Long id;
  /**
   * Grp Attr Val Id
   */
  private Long grpAttrValId;
  /**
   * Validity Value Id
   */
  private Long validityValueId;
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
   * Validity Attr Id
   */
  private Long validityAttrId;

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
   * @return grpAttrValId
   */
  public Long getGrpAttrValId() {
    return this.grpAttrValId;
  }

  /**
   * @param grpAttrValId set grpAttrValId
   */
  public void setGrpAttrValId(final Long grpAttrValId) {
    this.grpAttrValId = grpAttrValId;
  }

  /**
   * @return validityValueId
   */
  public Long getValidityValueId() {
    return this.validityValueId;
  }

  /**
   * @param validityValueId set validityValueId
   */
  public void setValidityValueId(final Long validityValueId) {
    this.validityValueId = validityValueId;
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
   * @return validityAttrId
   */
  public Long getValidityAttrId() {
    return this.validityAttrId;
  }

  /**
   * @param validityAttrId set validityAttrId
   */
  public void setValidityAttrId(final Long validityAttrId) {
    this.validityAttrId = validityAttrId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PredefinedValidity object) {
    return ModelUtil.compare(getValidityValueId(), object.getValidityValueId());
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
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getValidityValueId(), ((PredefinedValidity) obj).getValidityValueId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getValidityValueId());
  }

}
