package com.bosch.caltool.icdm.model.apic;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Webflow Element Model class
 *
 * @author dja7cob
 */
public class WebflowElement implements Comparable<WebflowElement>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 304783939952065L;
  /**
   * Webflow Id
   */
  private Long id;
  /**
   * Element Id
   */
  private Long elementId;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * Is Deleted
   */
  private boolean isDeleted;

  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
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
   * @return elementId
   */
  public Long getElementId() {
    return this.elementId;
  }

  /**
   * @param elementId set elementId
   */
  public void setElementId(final Long elementId) {
    this.elementId = elementId;
  }

  /**
   * @return variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }

  /**
   * @param variantId set variantId
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }

  /**
   * @return isDeleted
   */
  public boolean getIsDeleted() {
    return this.isDeleted;
  }

  /**
   * @param isDeleted set isDeleted
   */
  public void setIsDeleted(final boolean isDeleted) {
    this.isDeleted = isDeleted;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WebflowElement object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((WebflowElement) obj).getId());
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
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

}
