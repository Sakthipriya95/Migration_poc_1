/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.Set;

/**
 * @author DJA7COB
 */
public class ReviewResultDetailsQsr {

  /**
   * review status (I/O/C)
   */
  private String reviewStatus;

  /**
   * Pidc Version Link
   */
  private String pidcVersionLink;

  /**
   * Pidc Variant Link
   */
  private String pidcVariantLink;

  /**
   * name of variant from input cdr link
   */
  private String varNameFromInput;

  /**
   * linked variants if any
   */
  private Set<String> linkedVarUrls;

  /**
   * Value of the 'Customer' attribute in the pidc
   */
  private String customer;

  /**
   * Value of the 'fuel type' attribute in the pidc
   */
  private String fuelType;

  /**
   * Calibration Engineer for the review result
   */
  private String calibrationEngineer;

  /**
   * @return the reviewStatus
   */
  public String getReviewStatus() {
    return this.reviewStatus;
  }


  /**
   * @param reviewStatus the reviewStatus to set
   */
  public void setReviewStatus(final String reviewStatus) {
    this.reviewStatus = reviewStatus;
  }


  /**
   * @return the pidcVersionLink
   */
  public String getPidcVersionLink() {
    return this.pidcVersionLink;
  }


  /**
   * @param pidcVersionLink the cdrLink to set
   */
  public void setPidcVersionLink(final String pidcVersionLink) {
    this.pidcVersionLink = pidcVersionLink;
  }


  /**
   * @return the linkedVarUrls
   */
  public Set<String> getLinkedVarUrls() {
    return this.linkedVarUrls;
  }


  /**
   * @param linkedVarUrls the variants to set
   */
  public void setLinkedVarUrls(final Set<String> linkedVarUrls) {
    this.linkedVarUrls = linkedVarUrls;
  }


  /**
   * @return the customer
   */
  public String getCustomer() {
    return this.customer;
  }


  /**
   * @param customer the customer to set
   */
  public void setCustomer(final String customer) {
    this.customer = customer;
  }


  /**
   * @return the fuelType
   */
  public String getFuelType() {
    return this.fuelType;
  }


  /**
   * @param fuelType the fuelType to set
   */
  public void setFuelType(final String fuelType) {
    this.fuelType = fuelType;
  }


  /**
   * @return the calibrationEngineer
   */
  public String getCalibrationEngineer() {
    return this.calibrationEngineer;
  }


  /**
   * @param calibrationEngineer the calibrationEngineer to set
   */
  public void setCalibrationEngineer(final String calibrationEngineer) {
    this.calibrationEngineer = calibrationEngineer;
  }


  /**
   * @return the varNameFromInput
   */
  public String getVarNameFromInput() {
    return this.varNameFromInput;
  }


  /**
   * @param varNameFromInput the variant name to set
   */
  public void setVarNameFromInput(final String primaryVariantName) {
    this.varNameFromInput = primaryVariantName;
  }


  /**
   * @return the pidcVariantLink
   */
  public String getPidcVariantLink() {
    return this.pidcVariantLink;
  }


  /**
   * @param pidcVariantLink the pidcVariantLink to set
   */
  public void setPidcVariantLink(final String pidcVariantLink) {
    this.pidcVariantLink = pidcVariantLink;
  }
}
