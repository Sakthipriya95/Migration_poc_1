package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Focus Matrix Version Attribute Model class
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionAttr implements Comparable<FocusMatrixVersionAttr>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 8579521318864453477L;
  /**
   * Fmv Attr Id
   */
  private Long id;
  /**
   * Fm Vers Id
   */
  private Long fmVersId;
  /**
   * Attr Id
   */
  private Long attrId;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * Sub Variant Id
   */
  private Long subVariantId;
  /**
   * Used
   */
  private String used;
  /**
   * Value Id
   */
  private Long valueId;
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
   * fm attr remarks
   */
  private String fmAttrRemarks;

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
   * @return fmVersId
   */
  public Long getFmVersId() {
    return this.fmVersId;
  }

  /**
   * @param fmVersId set fmVersId
   */
  public void setFmVersId(final Long fmVersId) {
    this.fmVersId = fmVersId;
  }

  /**
   * @return attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }

  /**
   * @param attrId set attrId
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
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
   * @return subVariantId
   */
  public Long getSubVariantId() {
    return this.subVariantId;
  }

  /**
   * @param subVariantId set subVariantId
   */
  public void setSubVariantId(final Long subVariantId) {
    this.subVariantId = subVariantId;
  }

  /**
   * @return used
   */
  public String getUsed() {
    return this.used;
  }

  /**
   * @param used set used
   */
  public void setUsed(final String used) {
    this.used = used;
  }

  /**
   * @return valueId
   */
  public Long getValueId() {
    return this.valueId;
  }

  /**
   * @param valueId set valueId
   */
  public void setValueId(final Long valueId) {
    this.valueId = valueId;
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
  public int compareTo(final FocusMatrixVersionAttr object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((FocusMatrixVersionAttr) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return the fmAttrRemarks
   */
  public String getFmAttrRemarks() {
    return this.fmAttrRemarks;
  }

  /**
   * @param fmAttrRemarks the fmAttrRemarks to set
   */
  public void setFmAttrRemarks(final String fmAttrRemarks) {
    this.fmAttrRemarks = fmAttrRemarks;
  }


}
