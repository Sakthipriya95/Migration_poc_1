/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import com.bosch.caltool.apic.jpa.bo.AttributeValue.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * @author jvi6cob
 */
public class PIDCImportCompareResult {

  private Long attributeID;
  private String attributeName;
  private String attributeDesc;
  private String usedFlagOfExistingPIDC;
  private String usedFlagOfImportedPIDC;
  private String valueOfExistingPIDC;
  private String valueOfImportedPIDC;

  private String partNumberOfExistingPIDC;
  private String partNumberOfImportedPIDC;
  private String specLinkOfExistingPIDC;
  private String specLinkOfImportedPIDC;
  private String descriptionOfExistingPIDC;
  private String descriptionOfImportedPIDC;

  private boolean invalidModify;// to display invalid modification
  private boolean newValueAdded;// to display missing entries i.e newly added values
  private boolean missingNewAttribute;
  private String diffInfo = "MODIFIED";
  private String comment = "";
  private String importStatus = "-";

  private String variantName = "";
  private String subVariantName = "";

  // Boolean value type check used to return display string as TRUE/FALSE instead
  // of T/F.Applicable only when new insertion is made via import
  private boolean valueTypeBoolean;

  /**
   * Value type
   */
  private AttributeValueType valueType;
  private CLEARING_STATUS clearingStatus;


  /**
   * @param attributeID
   * @param attributeName
   * @param attributeDesc
   */
  public PIDCImportCompareResult(final Long attributeID, final String attributeName, final String attributeDesc) {
    super();
    this.attributeID = attributeID;
    this.attributeName = attributeName;
    this.attributeDesc = attributeDesc;
  }

  /**
   * // Icdm-833 orange Style
   *
   * @return the clearingStatus
   */
  public CLEARING_STATUS getClearingStatus() {
    return this.clearingStatus;
  }

  /**
   * @return the clearingStatus
   */
  public boolean isValNotCleared() {
    if (this.clearingStatus == null) {
      return false;
    }
    return this.clearingStatus == CLEARING_STATUS.NOT_CLEARED;
  }

  /**
   * @return type of the attribute value
   */

  public AttributeValueType getValueType() {
    return this.valueType;
  }

  /**
   * @param valueType to set the type of attribute value
   */

  public void setValueType(final AttributeValueType valueType) {
    this.valueType = valueType;
    if (this.valueType == AttributeValueType.BOOLEAN) {
      this.valueTypeBoolean = true;
    }
  }

  /**
   * @return the attributeID
   */
  public Long getAttributeID() {
    return this.attributeID;
  }

  /**
   * @param attributeID the attributeID to set
   */
  public void setAttributeID(final Long attributeID) {
    this.attributeID = attributeID;
  }

  /**
   * @return the attributeName
   */
  public String getAttributeName() {
    return this.attributeName;
  }

  /**
   * @param attributeName the attributeName to set
   */
  public void setAttributeName(final String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * @return the attributeDesc
   */
  public String getAttributeDesc() {
    return this.attributeDesc;
  }

  /**
   * @param attributeDesc the attributeDesc to set
   */
  public void setAttributeDesc(final String attributeDesc) {
    this.attributeDesc = attributeDesc;
  }

  /**
   * @return the usedFlagOfExistingPIDC
   */
  public String getUsedFlagOfExistingPIDC() {
    return this.usedFlagOfExistingPIDC;
  }

  /**
   * @param usedFlagOfExistingPIDC the usedFlagOfExistingPIDC to set
   */
  public void setUsedFlagOfExistingPIDC(final String usedFlagOfExistingPIDC) {
    this.usedFlagOfExistingPIDC = usedFlagOfExistingPIDC;
  }

  /**
   * @return the usedFlagOfImportedPIDC
   */
  public String getUsedFlagOfImportedPIDC() {
    return this.usedFlagOfImportedPIDC;
  }

  /**
   * @param usedFlagOfImportedPIDC the usedFlagOfImportedPIDC to set
   */
  public void setUsedFlagOfImportedPIDC(final String usedFlagOfImportedPIDC) {
    this.usedFlagOfImportedPIDC = usedFlagOfImportedPIDC;
  }

  /**
   * @return the valueOfExistingPIDC
   */
  public String getValueOfExistingPIDC() {
    return this.valueOfExistingPIDC;
  }

  /**
   * @param valueOfExistingPIDC the valueOfExistingPIDC to set
   */
  public void setValueOfExistingPIDC(final String valueOfExistingPIDC) {
    this.valueOfExistingPIDC = valueOfExistingPIDC;
  }

  /**
   * @return the valueOfImportedPIDC
   */
  public String getValueOfImportedPIDC() {
    return this.valueOfImportedPIDC;
  }

  /**
   * Method used for displaying imported value in Excel sheet Applicable only for values of Boolean type since displayed
   * value is TRUE/FALSE which is different from persisted value i.e(T/F)
   *
   * @return the valueOfImportedPIDC
   */
  public String getDisplayValueOfImportedPIDC() {
    if (this.valueTypeBoolean) {
      if (this.valueOfImportedPIDC.equals(ApicConstants.BOOLEAN_TRUE_DB_STRING) ||
          this.valueOfImportedPIDC.equals(ApicConstants.BOOLEAN_TRUE_STRING)) {
        return ApicConstants.BOOLEAN_TRUE_STRING;
      }
      else if (this.valueOfImportedPIDC.equals(ApicConstants.BOOLEAN_FALSE_DB_STRING) ||
          this.valueOfImportedPIDC.equals(ApicConstants.BOOLEAN_FALSE_STRING)) {
        return ApicConstants.BOOLEAN_FALSE_STRING;
      }
      return this.valueOfImportedPIDC;
    }
    return this.valueOfImportedPIDC;
  }

  /**
   * @param valueOfImportedPIDC the valueOfImportedPIDC to set
   */
  public void setValueOfImportedPIDC(final String valueOfImportedPIDC) {
    this.valueOfImportedPIDC = valueOfImportedPIDC;
  }

  /**
   * @return the isInvalidModify
   */
  public boolean isInvalidModify() {
    return this.invalidModify;
  }

  /**
   * @param isInvalidModify the isInvalidModify to set
   */
  public void setIsInvalidModify(final boolean isInvalidModify) {
    this.invalidModify = isInvalidModify;
    setDiffInfo("INVALID");
  }

  /**
   * @return the isNewValueAdded
   */
  public Boolean isNewValueAdded() {
    return this.newValueAdded;
  }

  /**
   * @param isNewValueAdded the isNewValueAdded to set
   */
  public void setIsNewValueAdded(final Boolean isNewValueAdded) {
    this.newValueAdded = isNewValueAdded;
  }


  /**
   * @return the invalidMessageStr
   */
  public String getComment() {
    return this.comment;
  }


  /**
   * @param invalidMessageStr the invalidMessageStr to set
   */
  public void setComment(final String invalidMessageStr) {
    this.comment += invalidMessageStr + "; ";
  }


  /**
   * @return the missingNewAttribute
   */
  public boolean isMissingNewAttribute() {
    return this.missingNewAttribute;
  }


  /**
   * @param missingNewAttribute the missingNewAttribute to set
   */
  public void setMissingNewAttribute(final boolean missingNewAttribute) {
    this.missingNewAttribute = missingNewAttribute;
    setDiffInfo("NEW");
  }


  /**
   * @return the diffInfo
   */
  public String getDiffInfo() {
    return this.diffInfo;
  }


  /**
   * @param diffInfo the diffInfo to set
   */
  public void setDiffInfo(final String diffInfo) {
    this.diffInfo = diffInfo;
  }


  /**
   * @return the importStatus
   */
  public String getImportStatus() {
    return this.importStatus;
  }


  /**
   * @param importStatus the importStatus to set
   */
  public void setImportStatus(final String importStatus) {
    this.importStatus = importStatus;
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
   * @return the subVariantName
   */
  public String getSubVariantName() {
    return this.subVariantName;
  }


  /**
   * @param subVariantName the subVariantName to set
   */
  public void setSubVariantName(final String subVariantName) {
    this.subVariantName = subVariantName;
  }


  /**
   * @return the partNumberOfExistingPIDC
   */
  public String getPartNumberOfExistingPIDC() {
    return this.partNumberOfExistingPIDC;
  }


  /**
   * @param partNumberOfExistingPIDC the partNumberOfExistingPIDC to set
   */
  public void setPartNumberOfExistingPIDC(final String partNumberOfExistingPIDC) {
    this.partNumberOfExistingPIDC = partNumberOfExistingPIDC;
  }


  /**
   * @return the partNumberOfImportedPIDC
   */
  public String getPartNumberOfImportedPIDC() {
    return this.partNumberOfImportedPIDC;
  }


  /**
   * @param partNumberOfImportedPIDC the partNumberOfImportedPIDC to set
   */
  public void setPartNumberOfImportedPIDC(final String partNumberOfImportedPIDC) {
    this.partNumberOfImportedPIDC = partNumberOfImportedPIDC;
  }


  /**
   * @return the specLinkOfExistingPIDC
   */
  public String getSpecLinkOfExistingPIDC() {
    return this.specLinkOfExistingPIDC;
  }


  /**
   * @param specLinkOfExistingPIDC the specLinkOfExistingPIDC to set
   */
  public void setSpecLinkOfExistingPIDC(final String specLinkOfExistingPIDC) {
    this.specLinkOfExistingPIDC = specLinkOfExistingPIDC;
  }


  /**
   * @return the specLinkOfImportedPIDC
   */
  public String getSpecLinkOfImportedPIDC() {
    return this.specLinkOfImportedPIDC;
  }


  /**
   * @param specLinkOfImportedPIDC the specLinkOfImportedPIDC to set
   */
  public void setSpecLinkOfImportedPIDC(final String specLinkOfImportedPIDC) {
    this.specLinkOfImportedPIDC = specLinkOfImportedPIDC;
  }


  /**
   * @return the descriptionOfExistingPIDC
   */
  public String getDescriptionOfExistingPIDC() {
    return this.descriptionOfExistingPIDC;
  }


  /**
   * @param descriptionOfExistingPIDC the descriptionOfExistingPIDC to set
   */
  public void setDescriptionOfExistingPIDC(final String descriptionOfExistingPIDC) {
    this.descriptionOfExistingPIDC = descriptionOfExistingPIDC;
  }


  /**
   * @return the descriptionOfImportedPIDC
   */
  public String getDescriptionOfImportedPIDC() {
    return this.descriptionOfImportedPIDC;
  }


  /**
   * @param descriptionOfImportedPIDC the descriptionOfImportedPIDC to set
   */
  public void setDescriptionOfImportedPIDC(final String descriptionOfImportedPIDC) {
    this.descriptionOfImportedPIDC = descriptionOfImportedPIDC;
  }

  /**
   * // Icdm-833 clearing status
   *
   * @param clearingStatus clearingStatus
   */
  public void setClearingStatus(final CLEARING_STATUS clearingStatus) {
    this.clearingStatus = clearingStatus;

  }


}
