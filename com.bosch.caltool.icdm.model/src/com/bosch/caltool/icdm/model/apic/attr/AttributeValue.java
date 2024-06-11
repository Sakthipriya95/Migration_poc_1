/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.IPastableItem;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class AttributeValue implements IDataObject, Comparable<AttributeValue>, IPastableItem {

  /**
   *
   */
  private static final long serialVersionUID = 2596636916036223908L;
  private Long id;
  private String name;
  private String nameRaw;
  private String description;
  private Long version;

  private Long attributeId;

  private String textValueEng;
  private String textValueGer;
  private BigDecimal numValue;
  private String boolvalue;
  private String dateValue;
  private String otherValue;
  private String valueType;

  private String descriptionEng;
  private String descriptionGer;

  private String changeComment;

  private String clearingStatus;
  private boolean deleted;

  private String createdUser;
  private String createdDate;
  private String modifiedUser;
  private String modifiedDate;
  private String unit;

  private Long characteristicValueId;
  private String charStr;
  private long userId;

  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Value column
                            */
                           SORT_ATTR_VAL,
                           /**
                            * Unit column
                            */
                           SORT_ATTR_VAL_UNIT,
                           /**
                            * Description column
                            */
                           SORT_ATTR_VAL_DESC,
                           /**
                            * Status column
                            */
                           SORT_ATTR_CLEAR_STATUS,

                           /**
                            * Icdm-955 sort char value
                            */
                           SORT_CHAR_VALUE
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
   * @return the nameRaw
   */
  public String getNameRaw() {
    return this.nameRaw;
  }

  /**
   * @param nameRaw the nameRaw to set
   */
  public void setNameRaw(final String nameRaw) {
    this.nameRaw = nameRaw;
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
   * @return the attributeId
   */
  public Long getAttributeId() {
    return this.attributeId;
  }

  /**
   * @param attributeId the attributeId to set
   */
  public void setAttributeId(final Long attributeId) {
    this.attributeId = attributeId;
  }


  /**
   * @return the textValueEng
   */
  public String getTextValueEng() {
    return this.textValueEng;
  }


  /**
   * @param textValueEng the textValueEng to set
   */
  public void setTextValueEng(final String textValueEng) {
    this.textValueEng = textValueEng;
  }


  /**
   * @return the textValueGer
   */
  public String getTextValueGer() {
    return this.textValueGer;
  }


  /**
   * @param textValueGer the textValueGer to set
   */
  public void setTextValueGer(final String textValueGer) {
    this.textValueGer = textValueGer;
  }


  /**
   * @return the numValue
   */
  public BigDecimal getNumValue() {
    return this.numValue;
  }


  /**
   * @param numValue the numValue to set
   */
  public void setNumValue(final BigDecimal numValue) {
    this.numValue = numValue;
  }


  /**
   * @return the boolvalue
   */
  public String getBoolvalue() {
    return this.boolvalue;
  }


  /**
   * @param boolvalue the boolvalue to set
   */
  public void setBoolvalue(final String boolvalue) {
    this.boolvalue = boolvalue;
  }


  /**
   * @return the dateValue
   */
  public String getDateValue() {
    return this.dateValue;
  }


  /**
   * @param dateValue the dateValue to set
   */
  public void setDateValue(final String dateValue) {
    this.dateValue = dateValue;
  }


  /**
   * @return the otherValue
   */
  public String getOtherValue() {
    return this.otherValue;
  }


  /**
   * @param otherValue the otherValue to set
   */
  public void setOtherValue(final String otherValue) {
    this.otherValue = otherValue;
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
   * @return the userId
   */
  public long getUserId() {
    return this.userId;
  }


  /**
   * @param userId the userId to set
   */
  public void setUserId(final long userId) {
    this.userId = userId;
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
   * @return the clearingStatus
   */
  public String getClearingStatus() {
    return this.clearingStatus;
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
   * @param clearingStatus the clearingStatus to set
   */
  public void setClearingStatus(final String clearingStatus) {
    this.clearingStatus = clearingStatus;
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
   * Compare objects with sort column
   *
   * @param arg0 other vlaue
   * @param sortColumn sort column
   * @return int
   */
  public int compareTo(final AttributeValue arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_VAL:
        compareResult = compareTo(arg0);
        break;
      case SORT_ATTR_VAL_UNIT:
        compareResult = getUnit().compareTo(arg0.getUnit());
        break;
      case SORT_ATTR_VAL_DESC:
        compareResult = getDescription().compareTo(arg0.getDescription());
        break;
      // Icdm-830 Data Model Changes
      case SORT_ATTR_CLEAR_STATUS:
      case SORT_CHAR_VALUE:
        compareResult = getCharStr().compareTo(arg0.getCharStr());
        break;
      default:
        compareResult = 0;
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
  public int compareTo(final AttributeValue attrValue2) {
    int compareResult = 0;
    if (null != getTextValueEng()) {
      compareResult = ModelUtil.compare(getTextValueEng(), attrValue2.getTextValueEng());
    }
    else if (null != getNumValue()) {
      compareResult = ModelUtil.compare(getNumValue(), attrValue2.getNumValue());
    }
    else if (null != getDateValue()) {
      // TODO - Deepthi
      compareResult = ModelUtil.compare(getDateValue(), attrValue2.getDateValue());
    }
    else if (null != getBoolvalue()) {
      compareResult = ModelUtil.compare(getBoolvalue(), attrValue2.getBoolvalue());
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute names
      compareResult = ModelUtil.compare(getAttributeId(), attrValue2.getAttributeId());
    }
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compareResult = ModelUtil.compare(getId(), attrValue2.getId());
    }

    return compareResult;
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
   * @param unit
   */
  public void setUnit(final String unit) {
    this.unit = unit;

  }


  /**
   * @return the unit
   */
  public String getUnit() {
    return this.unit;
  }


  /**
   * @return the characteristicValueId
   */
  public Long getCharacteristicValueId() {
    return this.characteristicValueId;
  }


  /**
   * @param characteristicValueId the characteristicValueId to set
   */
  public void setCharacteristicValueId(final Long characteristicValueId) {
    this.characteristicValueId = characteristicValueId;
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
      return ModelUtil.isEqual(getId(), ((AttributeValue) obj).getId());
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
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    return true;
  }


}
