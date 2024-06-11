/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutput.adapter;

import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.caltool.icdm.model.apic.pidc.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeValue;


/**
 * @author imi2si
 */
public class IcdmPidcLogAdapter extends AttrDiffType implements Comparable<IcdmPidcLogAdapter> {

  private Long fmId;

  private Long fmVersId;

  private boolean localVariantIdTracker;

  private boolean localSubVariantIdTracker;

  private boolean localOldAttributeValueTracker;

  private boolean localNewAttributeValueTracker;

  private boolean localOldValueTracker;

  private boolean localNewValueTracker;

  /**
   * @param level
   * @param attribute
   * @param changedItem
   * @param oldAttrValue
   * @param newAttrValue
   * @param oldValue
   * @param newValue
   * @param modifiedUser
   * @param modifiedName
   * @param modifiedOn
   */
  public IcdmPidcLogAdapter(final String level, final Attribute attribute, final String changedItem,
      final AttributeValue oldAttrValue, final AttributeValue newAttrValue, final String oldValue,
      final String newValue, final String modifiedUser, final String modifiedName, final String modifiedOn,
      final Long versionId, final Long pidcVersion, final Long pidcId, final Long varId, final Long sVarId,
      final boolean attributeChange, final boolean focusMatrixChange, final Long pidcVersVersId,
      final Long useCaseSectionId, final Long useCaseId, final Long fmId, final Long fmVersId) {
    super();
    super.setLevel(level);
    super.setAttribute(attribute);
    super.setChangedItem(changedItem);
    super.setOldAttributeValue(oldAttrValue);
    super.setNewAttributeValue(newAttrValue);
    super.setOldValue(oldValue);
    super.setNewValue(newValue);
    super.setModifiedUser(modifiedUser);
    super.setModifiedName(modifiedName);
    super.setModifiedDate(modifiedOn);
    super.setVersionId(versionId);
    super.setPidcversion(pidcVersion);
    super.setPidcId(pidcId);
    super.setAttributeChange(attributeChange);
    super.setFocusMatrixChange(focusMatrixChange);
    super.setPidcVersVersId(pidcVersVersId);
    super.setUseCaseSectionId(useCaseSectionId);
    super.setUseCaseId(useCaseId);
    setVariantId(varId);
    setSubVariantId(sVarId);
    this.fmId = fmId;
    this.fmVersId = fmVersId;
    this.localOldAttributeValueTracker = oldAttrValue != null;
    this.localNewAttributeValueTracker = newAttrValue != null;
    this.localOldValueTracker = oldValue != null;
    this.localNewValueTracker = newValue != null;
  }

  /**
   * @param level
   * @param attribute
   * @param changedItem
   * @param oldAttrValue
   * @param newAttrValue
   * @param modifiedUser
   * @param modifiedName
   * @param modifiedOn
   * @param versionId
   * @param m
   * @param l
   */
  public IcdmPidcLogAdapter(final String level, final Attribute attribute, final String changedItem,
      final String oldValue, final String newValue, final String modifiedUser, final String modifiedName,
      final String modifiedOn, final Long versionId, final Long pidcVersion, final Long pidcId, final Long varId,
      final Long sVarId, final boolean attributeChange, final Long pidcVersVersId) {
    super();
    super.setLevel(level);
    super.setAttribute(attribute);
    super.setChangedItem(changedItem);
    super.setOldValue(oldValue);
    super.setNewValue(newValue);
    super.setModifiedUser(modifiedUser);
    super.setModifiedName(modifiedName);
    super.setModifiedDate(modifiedOn);
    super.setVersionId(versionId);
    super.setPidcversion(pidcVersion);
    super.setPidcId(pidcId);
    super.setAttributeChange(attributeChange);
    super.setFocusMatrixChange(false);
    super.setPidcVersVersId(pidcVersVersId);
    setVariantId(varId);
    setSubVariantId(sVarId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IcdmPidcLogAdapter logAdapter) {
    Long versionId = logAdapter.getVersionId() - getVersionId();
    return versionId.equals(0L) ? logAdapter.hashCode() - hashCode() : versionId.intValue();
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = (prime * result) + ((super.getChangedItem() == null) ? 0 : super.getChangedItem().hashCode());

    result = (prime * result) + ((getFmId() == null) ? 0 : getFmId().hashCode());
    result = (prime * result) + ((getFmVersId() == null) ? 0 : getFmVersId().hashCode());

    result = (prime * result) + ((super.getLevel() == null) ? 0 : super.getLevel().hashCode());
    if (super.getAttribute() != null) {
      result = (prime * result) + (int) (super.getAttribute().getId() ^ (super.getAttribute().getId() >>> 32));
    }
    result = (prime * result) + (int) (super.getVersionId() ^ (super.getVersionId() >>> 32));
    return result;
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
    AttrDiffType other = (AttrDiffType) obj;
    if (getAttribute() == null) {
      if (other.getAttribute() != null) {
        return false;
      }
    }
    else if ((getAttribute().getId() != other.getAttribute().getId())) {
      return false;
    }
    if (getChangedItem() == null) {
      if (other.getChangedItem() != null) {
        return false;
      }
    }
    else if (!getChangedItem().equals(other.getChangedItem())) {
      return false;
    }

    IcdmPidcLogAdapter icdmPidcLogAdapter = (IcdmPidcLogAdapter) obj;
    if (getFmId() == null) {
      if (icdmPidcLogAdapter.getFmId() != null) {
        return false;
      }
    }
    else if (!getFmId().equals(icdmPidcLogAdapter.getFmId())) {
      return false;
    }

    if (getFmVersId() == null) {
      if (icdmPidcLogAdapter.getFmVersId() != null) {
        return false;
      }
    }
    else if (!getFmVersId().equals(icdmPidcLogAdapter.getFmVersId())) {
      return false;
    }

    if (getLevel() == null) {
      if (other.getLevel() != null) {
        return false;
      }
    }
    else if (!getLevel().equals(other.getLevel())) {
      return false;
    }
    return getVersionId() == other.getVersionId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "IcdmPidcLogAdapter [localPidcId=" + getPidcId() + ", localVariantId=" + getVariantId() +
        ", localVariantIdTracker=" + this.localVariantIdTracker + ", localSubVariantId=" + getSubVariantId() +
        ", localSubVariantIdTracker=" + this.localSubVariantIdTracker + ", localLevel=" + getLevel() +
        ", localAttribute=" + getAttribute() + ", localChangedItem=" + getChangedItem() + ", localOldAttributeValue=" +
        getOldAttributeValue() + ", localOldAttributeValueTracker=" + this.localOldAttributeValueTracker +
        ", localNewAttributeValue=" + getNewAttributeValue() + ", localNewAttributeValueTracker=" +
        this.localNewAttributeValueTracker + ", localOldValue=" + getOldValue() + ", localOldValueTracker=" +
        this.localOldValueTracker + ", localNewValue=" + getNewValue() + ", localNewValueTracker=" +
        this.localNewValueTracker + ", localModifiedUser=" + getModifiedUser() + ", localModifiedName=" +
        getModifiedName() + ", localModifiedOn=" + getModifiedDate() + ", localVersionId=" + getVersionId() +
        ", localPidcVersion=" + getPidcversion() + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVariantId(final Long variantId) {
    if ((null == variantId) || (variantId.longValue() == 0)) {
      return;
    }
    this.localVariantIdTracker = variantId.longValue() != java.lang.Long.MIN_VALUE;
    super.setVariantId(variantId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSubVariantId(final Long subVariantId) {
    if ((null == subVariantId) || (subVariantId.longValue() == 0)) {
      return;
    }
    // setting primitive attribute tracker to true
    this.localSubVariantIdTracker = subVariantId.longValue() != java.lang.Long.MIN_VALUE;
    super.setSubVariantId(subVariantId);
  }


  /**
   * @return the fmId
   */
  public Long getFmId() {
    return this.fmId;
  }


  /**
   * @param fmId the fmId to set
   */
  public void setFmId(final Long fmId) {
    this.fmId = fmId;
  }


  /**
   * @return the fmVersId
   */
  public Long getFmVersId() {
    return this.fmVersId;
  }


  /**
   * @param fmVersId the fmVersId to set
   */
  public void setFmVersId(final Long fmVersId) {
    this.fmVersId = fmVersId;
  }
}
