/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class AttrDiffType {

  private Long pidcId;

  private Long variantId;

  private Long subVariantId;

  private String level;

  private Attribute attribute;

  private String changedItem;

  private AttributeValue oldAttributeValue;

  private AttributeValue newAttributeValue;

  private String oldValue;

  private String newValue;

  private String modifiedUser;

  private String modifiedName;

  private String modifiedDate;

  private Long versionId;

  private Long pidcversion;

  private boolean attributeChange;

  private Long pidcVersVersId;

  private boolean focusMatrixChange;

  private Long useCaseSectionId;

  private Long useCaseId;


  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }


  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
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
   * @return the subVariantId
   */
  public Long getSubVariantId() {
    return this.subVariantId;
  }


  /**
   * @param subVariantId the subVariantId to set
   */
  public void setSubVariantId(final Long subVariantId) {
    this.subVariantId = subVariantId;
  }


  /**
   * @return the level
   */
  public String getLevel() {
    return this.level;
  }


  /**
   * @param level the level to set
   */
  public void setLevel(final String level) {
    this.level = level;
  }


  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return this.attribute;
  }


  /**
   * @param attribute the attribute to set
   */
  public void setAttribute(final Attribute attribute) {
    this.attribute = attribute;
  }


  /**
   * @return the changedItem
   */
  public String getChangedItem() {
    return this.changedItem;
  }


  /**
   * @param changedItem the changedItem to set
   */
  public void setChangedItem(final String changedItem) {
    this.changedItem = changedItem;
  }


  /**
   * @return the oldAttributeValue
   */
  public AttributeValue getOldAttributeValue() {
    return this.oldAttributeValue;
  }


  /**
   * @param oldAttributeValue the oldAttributeValue to set
   */
  public void setOldAttributeValue(final AttributeValue oldAttributeValue) {
    this.oldAttributeValue = oldAttributeValue;
  }


  /**
   * @return the newAttributeValue
   */
  public AttributeValue getNewAttributeValue() {
    return this.newAttributeValue;
  }


  /**
   * @param newAttributeValue the newAttributeValue to set
   */
  public void setNewAttributeValue(final AttributeValue newAttributeValue) {
    this.newAttributeValue = newAttributeValue;
  }


  /**
   * @return the oldValue
   */
  public String getOldValue() {
    return this.oldValue;
  }


  /**
   * @param oldValue the oldValue to set
   */
  public void setOldValue(final String oldValue) {
    this.oldValue = oldValue;
  }


  /**
   * @return the newValue
   */
  public String getNewValue() {
    return this.newValue;
  }


  /**
   * @param newValue the newValue to set
   */
  public void setNewValue(final String newValue) {
    this.newValue = newValue;
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


  /**
   * @return the modifiedName
   */
  public String getModifiedName() {
    return this.modifiedName;
  }


  /**
   * @param modifiedName the modifiedName to set
   */
  public void setModifiedName(final String modifiedName) {
    this.modifiedName = modifiedName;
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
   * @return the versionId
   */
  public Long getVersionId() {
    return this.versionId;
  }


  /**
   * @param versionId the versionId to set
   */
  public void setVersionId(final Long versionId) {
    this.versionId = versionId;
  }


  /**
   * @return the pidcversion
   */
  public Long getPidcversion() {
    return this.pidcversion;
  }


  /**
   * @param pidcversion the pidcversion to set
   */
  public void setPidcversion(final Long pidcversion) {
    this.pidcversion = pidcversion;
  }


  /**
   * @return the attributeChange
   */
  public boolean isAttributeChange() {
    return this.attributeChange;
  }


  /**
   * @param attributeChange the attributeChange to set
   */
  public void setAttributeChange(final boolean attributeChange) {
    this.attributeChange = attributeChange;
  }


  /**
   * @return the pidcVersVersId
   */
  public Long getPidcVersVersId() {
    return this.pidcVersVersId;
  }


  /**
   * @param pidcVersVersId the pidcVersVersId to set
   */
  public void setPidcVersVersId(final Long pidcVersVersId) {
    this.pidcVersVersId = pidcVersVersId;
  }


  /**
   * @return the focusMatrixChange
   */
  public boolean isFocusMatrixChange() {
    return this.focusMatrixChange;
  }


  /**
   * @param focusMatrixChange the focusMatrixChange to set
   */
  public void setFocusMatrixChange(final boolean focusMatrixChange) {
    this.focusMatrixChange = focusMatrixChange;
  }


  /**
   * @return the useCaseSectionId
   */
  public Long getUseCaseSectionId() {
    return this.useCaseSectionId;
  }


  /**
   * @param useCaseSectionId the useCaseSectionId to set
   */
  public void setUseCaseSectionId(final Long useCaseSectionId) {
    this.useCaseSectionId = useCaseSectionId;
  }


  /**
   * @return the useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }


  /**
   * @param useCaseId the useCaseId to set
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }


}
