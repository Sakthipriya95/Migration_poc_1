package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Review Variants Model class
 *
 * @author bru2cob
 */
public class RvwVariant implements Comparable<RvwVariant>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 179125514682428691L;
  /**
   * Rvw Var Id
   */
  private Long id;
  /**
   * Name
   */
  private String variantName;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * Result Id
   */
  private Long resultId;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;
  /**
   * primary variant id
   */
  private Long primaryVariantId;
  /**
   * primary variant
   */
  private String primaryVariantName;
  /**
   * Result name
   */
  private String name;
  /**
   * Result description
   */
  private String description;
  /**
   * IS linked variant
   */
  private boolean linkedVariant;


  /**
   * @return the linkedVariant
   */
  public boolean isLinkedVariant() {
    return this.linkedVariant;
  }


  /**
   * @param linkedVariant the linkedVariant to set
   */
  public void setLinkedVariant(final boolean linkedVariant) {
    this.linkedVariant = linkedVariant;
  }


  /**
   * @return the primaryVariantId
   */
  public Long getPrimaryVariantId() {
    return this.primaryVariantId;
  }


  /**
   * @param primaryVariantId the primaryVariantId to set
   */
  public void setPrimaryVariantId(final Long primaryVariantId) {
    this.primaryVariantId = primaryVariantId;
  }


  /**
   * @return the primaryVariantName
   */
  public String getPrimaryVariantName() {
    return this.primaryVariantName;
  }


  /**
   * @param primaryVariantName the primaryVariantName to set
   */
  public void setPrimaryVariantName(final String primaryVariantName) {
    this.primaryVariantName = primaryVariantName;
  }


  /**
   * @return the resultName
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param resultName the resultName to set
   */
  @Override
  public void setName(final String resultName) {
    this.name = resultName;
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
   * @return the name
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param name the name to set
   */
  public void setVariantName(final String name) {
    this.variantName = name;
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
   * @return resultId
   */
  public Long getResultId() {
    return this.resultId;
  }

  /**
   * @param resultId set resultId
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
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
  public int compareTo(final RvwVariant object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwVariant) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


}
