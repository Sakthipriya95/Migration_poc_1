/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;


/**
 * @author TRL1COB
 */
public class DataAssessmentWorkpackages {

  /**
   * Unique Id of Data assessment Workpackage
   */
  private Long id;
  /**
   * Id of the Work Package
   */
  private Long a2lWpId;
  /**
   * Id of the Responsibility
   */
  private Long a2lRespId;
  /**
   * Name of the Work Package
   */
  private String a2lWpName;
  /**
   * Name of the Responsibility
   */
  private String a2lRespName;
  /**
   * Responsibility type
   */
  private String a2lRespType;
  /**
   * Flag to indicate if WP is ready for prod
   */
  private boolean wpReadyForProd;
  /**
   * Flag to indicate if WP finished
   */
  private boolean wpFinished;
  /**
   * Flag to indicate if all qnaires are reviewed and baselined
   */
  private boolean qnairesReviewedAndBaselined;
  /**
   * Flag to indicate if all parameters are reviewed
   */
  private boolean parametersReviewed;
  /**
   * Flag to indicate if HEX data is equal to data reviews
   */
  private boolean hexDataEqualToReviews;

  /**
   * @return the id
   */
  public long getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final long id) {
    this.id = id;
  }

  /**
   * @return the a2lWpId
   */
  public long getA2lWpId() {
    return this.a2lWpId;
  }

  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }

  /**
   * @return the a2lRespId
   */
  public long getA2lRespId() {
    return this.a2lRespId;
  }

  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  /**
   * @return the a2lWpName
   */
  public String getA2lWpName() {
    return this.a2lWpName;
  }

  /**
   * @param a2lWpName the a2lWpName to set
   */
  public void setA2lWpName(final String a2lWpName) {
    this.a2lWpName = a2lWpName;
  }

  /**
   * @return the a2lRespName
   */
  public String getA2lRespName() {
    return this.a2lRespName;
  }

  /**
   * @param a2lRespName the a2lRespName to set
   */
  public void setA2lRespName(final String a2lRespName) {
    this.a2lRespName = a2lRespName;
  }

  /**
   * @return the a2lRespType
   */
  public String getA2lRespType() {
    return this.a2lRespType;
  }

  /**
   * @param a2lRespType the a2lRespType to set
   */
  public void setA2lRespType(final String a2lRespType) {
    this.a2lRespType = a2lRespType;
  }

  /**
   * @return the wpReadyForProd
   */
  public boolean isWpReadyForProd() {
    return this.wpReadyForProd;
  }

  /**
   * @param wpReadyForProd the wpReadyForProd to set
   */
  public void setWpReadyForProd(final boolean wpReadyForProd) {
    this.wpReadyForProd = wpReadyForProd;
  }

  /**
   * @return the wpFinished
   */
  public boolean isWpFinished() {
    return this.wpFinished;
  }

  /**
   * @param wpFinished the wpFinished to set
   */
  public void setWpFinished(final boolean wpFinished) {
    this.wpFinished = wpFinished;
  }

  /**
   * @return the qnairesReviewedAndBaselined
   */
  public boolean isQnairesReviewedAndBaselined() {
    return this.qnairesReviewedAndBaselined;
  }

  /**
   * @param qnairesReviewedAndBaselined the qnairesReviewedAndBaselined to set
   */
  public void setQnairesReviewedAndBaselined(final boolean qnairesReviewedAndBaselined) {
    this.qnairesReviewedAndBaselined = qnairesReviewedAndBaselined;
  }

  /**
   * @return the parametersReviewed
   */
  public boolean isParametersReviewed() {
    return this.parametersReviewed;
  }

  /**
   * @param parametersReviewed the parametersReviewed to set
   */
  public void setParametersReviewed(final boolean parametersReviewed) {
    this.parametersReviewed = parametersReviewed;
  }

  /**
   * @return the hexDataEqualToReviews
   */
  public boolean isHexDataEqualToReviews() {
    return this.hexDataEqualToReviews;
  }

  /**
   * @param hexDataEqualToReviews the hexDataEqualToReviews to set
   */
  public void setHexDataEqualToReviews(final boolean hexDataEqualToReviews) {
    this.hexDataEqualToReviews = hexDataEqualToReviews;
  }


}
