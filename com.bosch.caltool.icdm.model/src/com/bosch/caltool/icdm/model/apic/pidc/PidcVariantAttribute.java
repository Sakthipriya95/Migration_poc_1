/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bne4cob
 */
public class PidcVariantAttribute implements Cloneable, IProjectAttribute {

  /**
   *
   */
  private static final long serialVersionUID = 8353489769860441398L;

  private Long objId;
  private Long variantId;
  private Long pidcVersionId;
  private String variantName;
  private Long attrId;
  /**
   * Attribute name
   */
  private String name;
  /**
   * Attribute description
   */
  private String description;

  private Long valueId;
  private String value;
  private Long version;
  private String usedFlag;
  private boolean atChildLevel;
  private boolean attrHidden;
  private String additionalInfoDesc;
  private String partNumber;
  private String specLink;
  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
  private String valueType;
  private Long varNameValId;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.objId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.objId = objId;

  }

  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }

  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
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
   * @return name
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return the attrId
   */
  @Override
  public Long getAttrId() {
    return this.attrId;
  }


  /**
   * @param attrId the attrId to set
   */
  @Override
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * @return the valueId
   */
  @Override
  public Long getValueId() {
    return this.valueId;
  }


  /**
   * @param valueId the valueId to set
   */
  @Override
  public void setValueId(final Long valueId) {
    this.valueId = valueId;
  }


  /**
   * @return the value
   */
  @Override
  public String getValue() {
    return this.value;
  }


  /**
   * @param value the value to set
   */
  @Override
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * @return the usedFlag
   */
  @Override
  public String getUsedFlag() {
    return this.usedFlag;
  }

  /**
   * @param usedFlag the usedFlag to set
   */
  @Override
  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }

  /**
   * @return the atChildLevel
   */
  @Override
  public boolean isAtChildLevel() {
    return this.atChildLevel;
  }

  /**
   * @param atChildLevel the atChildLevel to set
   */
  @Override
  public void setAtChildLevel(final boolean atChildLevel) {
    this.atChildLevel = atChildLevel;
  }

  /**
   * @return the attrHidden
   */
  @Override
  public boolean isAttrHidden() {
    return this.attrHidden;
  }

  /**
   * @param attrHidden the attrHidden to set
   */
  @Override
  public void setAttrHidden(final boolean attrHidden) {
    this.attrHidden = attrHidden;
  }

  /**
   * @return the additionalInfoDesc
   */
  @Override
  public String getAdditionalInfoDesc() {
    return this.additionalInfoDesc;
  }


  /**
   * @param additionalInfoDesc the additionalInfoDesc to set
   */
  @Override
  public void setAdditionalInfoDesc(final String additionalInfoDesc) {
    this.additionalInfoDesc = additionalInfoDesc;
  }


  /**
   * @return the partNumber
   */
  @Override
  public String getPartNumber() {
    return this.partNumber;
  }


  /**
   * @param partNumber the partNumber to set
   */
  @Override
  public void setPartNumber(final String partNumber) {
    this.partNumber = partNumber;
  }


  /**
   * @return the specLink
   */
  @Override
  public String getSpecLink() {
    return this.specLink;
  }


  /**
   * @param specLink the specLink to set
   */
  @Override
  public void setSpecLink(final String specLink) {
    this.specLink = specLink;
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
  public PidcVariantAttribute clone() {
    PidcVariantAttribute varAttr = null;
    try {
      varAttr = (PidcVariantAttribute) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return varAttr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IProjectAttribute projectAttr) {
    return ModelUtil.compare(getName(), projectAttr.getName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IProjectAttribute projectAttr, final int sortColumn) {
    // TODO Auto-generated method stub
    return 0;
  }


  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }

  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
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
    PidcVariantAttribute other = (PidcVariantAttribute) obj;
    if (getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else if (!getName().equals(other.getName())) {
      return false;
    }
    else {
      if (getId() == null) {
        if (other.getId() != null) {
          return false;
        }
      }
      else if (!getId().equals(other.getId())) {
        return false;
      }
    }
    return true;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the valueType
   */
  public String getValueType() {
    return this.valueType;
  }


  /**
   * @param valueType the valueType to set
   */
  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

  /**
   * @return the varNameValId
   */
  public Long getVarNameValId() {
    return this.varNameValId;
  }

  /**
   * @param varNameValId the varNameValId to set
   */
  public void setVarNameValId(final Long varNameValId) {
    this.varNameValId = varNameValId;
  }


}
