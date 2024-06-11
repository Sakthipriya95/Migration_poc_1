/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class Attribute implements IDataObject, Comparable<Attribute> {

  /**
   *
   */
  private static final long serialVersionUID = -1724080044998466422L;

  private static final int OBJ_EQUAL_CHK_VAL = 0;

  private Long id;
  private Long attrGrpId;
  private String name;
  private String nameEng;
  private String nameGer;
  private String description;
  private String descriptionEng;
  private String descriptionGer;
  private boolean normalized;
  private boolean mandatory;
  private String format;
  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
  private Long level;
  private boolean deleted;
  private boolean moveDown;
  private String valueType;
  private Long valueTypeId;
  private String unit;

  private boolean external;
  private boolean externalValue;
  private String changeComment;
  private String eadmName;
  private boolean groupedAttr;
  private boolean withPartNumber;
  private boolean withSpecLink;
  private boolean withCharValue;
  private Long characteristicId;
  private String charStr;
  private Long version;
  private boolean delCharFlag;
  private boolean addValByUserFlag;

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
  public void setId(final Long objId) {
    this.id = objId;

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
   * @return the level
   */
  public Long getLevel() {
    if (this.level != null) {
      return this.level;
    }
    return 0L;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(final Long level) {
    this.level = level;
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

  /**
   * @return the moveDown
   */
  public boolean isMoveDown() {
    return this.moveDown;
  }

  /**
   * @param moveDown the moveDown to set
   */
  public void setMoveDown(final boolean moveDown) {
    this.moveDown = moveDown;
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
   * @return the unit
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * @param unit the unit to set
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }


  /**
   * @return the attrGrpId
   */
  public Long getAttrGrpId() {
    return this.attrGrpId;
  }


  /**
   * @param attrGrpId the attrGrpId to set
   */
  public void setAttrGrpId(final Long attrGrpId) {
    this.attrGrpId = attrGrpId;
  }


  /**
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * @param nameEng the nameEng to set
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * @return the nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }


  /**
   * @param nameGer the nameGer to set
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }


  /**
   * @return the descriptionEng
   */
  public String getDescriptionEng() {
    return this.descriptionEng;
  }


  /**
   * @param descriptionEng the descriptionEng to set
   */
  public void setDescriptionEng(final String descriptionEng) {
    this.descriptionEng = descriptionEng;
  }


  /**
   * @return the descriptionGer
   */
  public String getDescriptionGer() {
    return this.descriptionGer;
  }


  /**
   * @param descriptionGer the descriptionGer to set
   */
  public void setDescriptionGer(final String descriptionGer) {
    this.descriptionGer = descriptionGer;
  }


  /**
   * @return the normalized
   */
  public boolean isNormalized() {
    return this.normalized;
  }


  /**
   * @param normalized the normalized to set
   */
  public void setNormalized(final boolean normalized) {
    this.normalized = normalized;
  }


  /**
   * @return the mandatory
   */
  public boolean isMandatory() {
    return this.mandatory;
  }


  /**
   * @param mandatory the mandatory to set
   */
  public void setMandatory(final boolean mandatory) {
    this.mandatory = mandatory;
  }


  /**
   * @return the format
   */
  public String getFormat() {
    return this.format;
  }


  /**
   * @param format the format to set
   */
  public void setFormat(final String format) {
    this.format = format;
  }


  /**
   * @return the createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the external
   */
  public boolean isExternal() {
    return this.external;
  }


  /**
   * @param external the external to set
   */
  public void setExternal(final boolean external) {
    this.external = external;
  }


  /**
   * @return the externalValue
   */
  public boolean isExternalValue() {
    return this.externalValue;
  }


  /**
   * @param externalValue the externalValue to set
   */
  public void setExternalValue(final boolean externalValue) {
    this.externalValue = externalValue;
  }


  /**
   * @return the changeComment
   */
  public String getChangeComment() {
    return this.changeComment;
  }


  /**
   * @param changeComment the changeComment to set
   */
  public void setChangeComment(final String changeComment) {
    this.changeComment = changeComment;
  }


  /**
   * @return the eadmName
   */
  public String getEadmName() {
    return this.eadmName;
  }


  /**
   * @param eadmName the eadmName to set
   */
  public void setEadmName(final String eadmName) {
    this.eadmName = eadmName;
  }


  /**
   * @return the groupedAttr
   */
  public boolean isGroupedAttr() {
    return this.groupedAttr;
  }


  /**
   * @param groupedAttr the groupedAttr to set
   */
  public void setGroupedAttr(final boolean groupedAttr) {
    this.groupedAttr = groupedAttr;
  }


  /**
   * @return the withPartNumber
   */
  public boolean isWithPartNumber() {
    return this.withPartNumber;
  }


  /**
   * @param withPartNumber the withPartNumber to set
   */
  public void setWithPartNumber(final boolean withPartNumber) {
    this.withPartNumber = withPartNumber;
  }


  /**
   * @return the withSpecLink
   */
  public boolean isWithSpecLink() {
    return this.withSpecLink;
  }


  /**
   * @param withSpecLink the withSpecLink to set
   */
  public void setWithSpecLink(final boolean withSpecLink) {
    this.withSpecLink = withSpecLink;
  }


  /**
   * @return the characteristicId
   */
  public Long getCharacteristicId() {
    return this.characteristicId;
  }


  /**
   * @param characteristicId the characteristicId to set
   */
  public void setCharacteristicId(final Long characteristicId) {
    this.characteristicId = characteristicId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Attribute attr) {
    // Compare attribute name, (if english not available, then german name is compared)
    int compResult = ModelUtil.compare(getName(), attr.getName());
    // If name is same, then compare by attribute id
    if (compResult == OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getId(), attr.getId());
    }
    return compResult;
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final Attribute arg0, final int sortColumn) { // NOPMD by bne4cob on 6/20/14 10:27 AM

    int compareResult = 0;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        // attribute name needs not to be compared because it is the default sort
        compareResult = 0;
        break;

      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ModelUtil.compare(getDescription(), arg0.getDescription());
        break;

      case ApicConstants.SORT_SUPERGROUP:
        compareResult = 0;
        break;

      case ApicConstants.SORT_GROUP:
        break;

      case ApicConstants.SORT_LEVEL:
        break;

      case ApicConstants.SORT_UNIT:
        compareResult = ModelUtil.compare(getUnit(), arg0.getUnit());
        break;

      case ApicConstants.SORT_VALUETYPE:
        compareResult = ModelUtil.compare(getValueType(), arg0.getValueType());
        break;
      // ICDM-179
      case ApicConstants.SORT_MANDATORY:
        break;
      // ICDM-860
      case ApicConstants.SORT_NORMALIZED_FLAG:
        break;
      case ApicConstants.SORT_FORMAT:
        compareResult = ModelUtil.compare(getFormat(), arg0.getFormat());
        break;
      case ApicConstants.SORT_PART_NUMBER:
        break;
      case ApicConstants.SORT_SPEC_LINK:
        break;
      // ICdm-480
      case ApicConstants.SORT_ATTR_SEC:
        break;
      case ApicConstants.SORT_ATTR_VAL_SEC:
        break;
      case ApicConstants.SORT_CHAR:
        compareResult = ModelUtil.compare(getCharStr(), arg0.getCharStr());
        break;
      // ICDM-1560
      case ApicConstants.SORT_ATTR_EADM_NAME:
        break;
      case ApicConstants.SORT_ATTR_CREATED_DATE_PIDC:
        break;
      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
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
    if (obj.getClass() == this.getClass()) {
      return ModelUtil.isEqual(getId(), ((Attribute) obj).getId());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return the valueTypeId
   */
  public Long getValueTypeId() {
    return this.valueTypeId;
  }


  /**
   * @param valueTypeId the valueTypeId to set
   */
  public void setValueTypeId(final Long valueTypeId) {
    this.valueTypeId = valueTypeId;
  }


  /**
   * @return the withCharValue
   */
  public boolean isWithCharValue() {
    return this.withCharValue;
  }


  /**
   * @param withCharValue the withCharValue to set
   */
  public void setWithCharValue(final boolean withCharValue) {
    this.withCharValue = withCharValue;
  }


  /**
   * @return the charStr
   */
  public String getCharStr() {
    return this.charStr;
  }


  /**
   * @param charStr the charStr to set
   */
  public void setCharStr(final String charStr) {
    this.charStr = charStr;
  }


  /**
   * @return the delCharFlag
   */
  public boolean isDelCharFlag() {
    return this.delCharFlag;
  }


  /**
   * @param delCharFlag the delCharFlag to set
   */
  public void setDelCharFlag(final boolean delCharFlag) {
    this.delCharFlag = delCharFlag;
  }

  /**
   * @return the addValByUserFlag
   */
  public boolean isAddValByUserFlag() {
    return this.addValByUserFlag;
  }


  /**
   * @param addValByUserFlag the addValByUserFlag to set
   */
  public void setAddValByUserFlag(final boolean addValByUserFlag) {
    this.addValByUserFlag = addValByUserFlag;
  }
}
