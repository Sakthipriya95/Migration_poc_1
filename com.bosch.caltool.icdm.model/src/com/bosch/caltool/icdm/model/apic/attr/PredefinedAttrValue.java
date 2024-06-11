package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * PredefinedAttrValue Model class
 *
 * @author PDH2COB
 */
public class PredefinedAttrValue implements Comparable<PredefinedAttrValue>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = -986215642971469313L;
  /**
   * Pre Attrvl Id
   */
  private Long id;
  /**
   * Grp Attr Val Id
   */
  private Long grpAttrValId;

  /**
   * Predefined value
   */
  private String predefinedValue;
  /**
   * Predefined Value Id
   */
  private Long predefinedValueId;
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
   * Predefined Attr Id
   */
  private Long predefinedAttrId;
  /**
   * Predefined attr name
   */
  private String predefinedAttrName;

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
   * @return predefinedValueId
   */
  public Long getPredefinedValueId() {
    return this.predefinedValueId;
  }

  /**
   * @param predefinedValueId set predefinedValueId
   */
  public void setPredefinedValueId(final Long predefinedValueId) {
    this.predefinedValueId = predefinedValueId;
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
   * @return predefinedAttrId
   */
  public Long getPredefinedAttrId() {
    return this.predefinedAttrId;
  }

  /**
   * @param predefinedAttrId set predefinedAttrId
   */
  public void setPredefinedAttrId(final Long predefinedAttrId) {
    this.predefinedAttrId = predefinedAttrId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PredefinedAttrValue object) {
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
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getPredefinedValueId(), ((PredefinedAttrValue) obj).getPredefinedValueId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getPredefinedValueId());
  }


  /**
   * @return the predefinedValue
   */
  public String getPredefinedValue() {
    return this.predefinedValue;
  }


  /**
   * @param predefinedValue the predefinedValue to set
   */
  public void setPredefinedValue(final String predefinedValue) {
    this.predefinedValue = predefinedValue;
  }


  /**
   * @return the predefinedAttrName
   */
  public String getPredefinedAttrName() {
    return this.predefinedAttrName;
  }


  /**
   * @param predefinedAttrName the predefinedAttrName to set
   */
  public void setPredefinedAttrName(final String predefinedAttrName) {
    this.predefinedAttrName = predefinedAttrName;
  }

}
