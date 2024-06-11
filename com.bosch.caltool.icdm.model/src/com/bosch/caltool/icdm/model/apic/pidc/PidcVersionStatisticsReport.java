/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Date;


/**
 * @author rgo7cob
 */
public class PidcVersionStatisticsReport {

  /**
   * total attributes
   */
  private int totalAttributes;
  /**
   * used attributes
   */
  private int usedAttributes;
  /**
   * not used attributes
   */
  private int notUsedAttributes;

  /**
   * not defined attributes
   */
  private int notDefinedAttribute;

  /**
   * last modified date
   */
  private Date lastModifiedDate;

  /**
   * project use cases
   */
  private int projectUseCases;

  /**
   * used flag set Imported attributes
   */
  private int mandateOrProjectUcAttrUsedCount;

  /**
   * mandatory or Project Use case attributes
   */
  private int mandateOrProjectUcAttr;

  /**
   * focus matrix applicable attributes
   */
  private int focusMatrixApplicabeAttributes;


  /**
   * focus matrix applicable attributes
   */
  private int focusMatrixUnratedAttributes;


  /**
   * new defined attributes
   */
  private int newAttributes;


  /**
   * rated focus matrix attribute.
   */
  private int focusMatrixRatedAttributes;

  // Task 234241
  /**
   * Mandatory attributes count only without considering project use case
   */
  private int totalMandatoryAttrCountOnly;

  // Task 234241
  /**
   * Used attributes only, without considering project use case
   */
  private int totalMandtryAttrUsedCountOnly;

  /**
   * @return the totalAttributes
   */
  public int getTotalAttributes() {
    return this.totalAttributes;
  }


  /**
   * @param totalAttributes the totalAttributes to set
   */
  public void setTotalAttributes(final int totalAttributes) {
    this.totalAttributes = totalAttributes;
  }


  /**
   * @return the usedAttributes
   */
  public int getUsedAttributes() {
    return this.usedAttributes;
  }


  /**
   * @param usedAttributes the usedAttributes to set
   */
  public void setUsedAttributes(final int usedAttributes) {
    this.usedAttributes = usedAttributes;
  }


  /**
   * @return the notUsedAttributes
   */
  public int getNotUsedAttributes() {
    return this.notUsedAttributes;
  }


  /**
   * @param notUsedAttributes the notUsedAttributes to set
   */
  public void setNotUsedAttributes(final int notUsedAttributes) {
    this.notUsedAttributes = notUsedAttributes;
  }


  /**
   * @return the notDefinedAttribute
   */
  public int getNotDefinedAttribute() {
    return this.notDefinedAttribute;
  }


  /**
   * @param notDefinedAttribute the notDefinedAttribute to set
   */
  public void setNotDefinedAttribute(final int notDefinedAttribute) {
    this.notDefinedAttribute = notDefinedAttribute;
  }


  /**
   * @return the lastModifiedDate
   */
  public Date getLastModifiedDate() {
    return this.lastModifiedDate;
  }


  /**
   * @param lastModifiedDate the lastModifiedDate to set
   */
  public void setLastModifiedDate(final Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }


  /**
   * @return the projectUseCases
   */
  public int getProjectUseCases() {
    return this.projectUseCases;
  }


  /**
   * @param projectUseCases the projectUseCases to set
   */
  public void setProjectUseCases(final int projectUseCases) {
    this.projectUseCases = projectUseCases;
  }


  /**
   * @return the mandateOrProjectUcAttrUsedCount
   */
  public int getMandateOrProjectUcAttrUsedCount() {
    return this.mandateOrProjectUcAttrUsedCount;
  }


  /**
   * @param mandateOrProjectUcAttrUsedCount the mandateOrProjectUcAttrUsedCount to set
   */
  public void setMandateOrProjectUcAttrUsedCount(final int mandateOrProjectUcAttrUsedCount) {
    this.mandateOrProjectUcAttrUsedCount = mandateOrProjectUcAttrUsedCount;
  }


  /**
   * @return the mandateOrProjectUcAttr
   */
  public int getMandateOrProjectUcAttr() {
    return this.mandateOrProjectUcAttr;
  }


  /**
   * @param mandateOrProjectUcAttr the mandateOrProjectUcAttr to set
   */
  public void setMandateOrProjectUcAttr(final int mandateOrProjectUcAttr) {
    this.mandateOrProjectUcAttr = mandateOrProjectUcAttr;
  }


  /**
   * @return the focusMatrixApplicabeAttributes
   */
  public int getFocusMatrixApplicabeAttributes() {
    return this.focusMatrixApplicabeAttributes;
  }


  /**
   * @param focusMatrixApplicabeAttributes the focusMatrixApplicabeAttributes to set
   */
  public void setFocusMatrixApplicabeAttributes(final int focusMatrixApplicabeAttributes) {
    this.focusMatrixApplicabeAttributes = focusMatrixApplicabeAttributes;
  }


  /**
   * @return the focusMatrixRatedAttributes
   */
  public int getFocusMatrixRatedAttributes() {
    return this.focusMatrixRatedAttributes;
  }


  /**
   * @param focusMatrixRatedAttributes the focusMatrixRatedAttributes to set
   */
  public void setFocusMatrixRatedAttributes(final int focusMatrixRatedAttributes) {
    this.focusMatrixRatedAttributes = focusMatrixRatedAttributes;
  }


  /**
   * @return the focusMatrixUnratedAttributes
   */
  public int getFocusMatrixUnratedAttributes() {
    return this.focusMatrixUnratedAttributes;
  }


  /**
   * @param focusMatrixUnratedAttributes the focusMatrixUnratedAttributes to set
   */
  public void setFocusMatrixUnratedAttributes(final int focusMatrixUnratedAttributes) {
    this.focusMatrixUnratedAttributes = focusMatrixUnratedAttributes;
  }


  /**
   * @return the newAttributes
   */
  public int getNewAttributes() {
    return this.newAttributes;
  }


  /**
   * @param newAttributes the newAttributes to set
   */
  public void setNewAttributes(final int newAttributes) {
    this.newAttributes = newAttributes;
  }


  /**
   * @return the totalMandatoryAttrCountOnly
   */
  public int getTotalMandatoryAttrCountOnly() {
    return this.totalMandatoryAttrCountOnly;
  }


  /**
   * @param totalMandatoryAttrCountOnly the totalMandatoryAttrCountOnly to set
   */
  public void setTotalMandatoryAttrCountOnly(final int totalMandatoryAttrCountOnly) {
    this.totalMandatoryAttrCountOnly = totalMandatoryAttrCountOnly;
  }


  /**
   * @return the totalMandtryAttrUsedCountOnly
   */
  public int getTotalMandtryAttrUsedCountOnly() {
    return this.totalMandtryAttrUsedCountOnly;
  }


  /**
   * @param totalMandtryAttrUsedCountOnly the totalMandtryAttrUsedCountOnly to set
   */
  public void setTotalMandtryAttrUsedCountOnly(final int totalMandtryAttrUsedCountOnly) {
    this.totalMandtryAttrUsedCountOnly = totalMandtryAttrUsedCountOnly;
  }


}
