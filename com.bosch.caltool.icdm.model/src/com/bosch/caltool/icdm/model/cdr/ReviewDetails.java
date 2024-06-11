/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;


/**
 * @author bne4cob
 */
public class ReviewDetails {

  /**
   * Review ID
   */
  private long rvwID;
  /**
   * Review description
   */
  private String rvwDesc;

  /**
   * Source type. Possible values - <br>
   * &lt;REVIEWED_FILE&gt;<br>
   * &lt;FUNCTION&gt;<br>
   * &lt;LAB&gt;<br>
   * Otherwise, T_FC2WP.WP_NAME_E using FC2WP_ID
   */
  private String sourceType;
  /**
   * Date of review
   */
  private String rvwDate;

  /**
   * Is delta review - Y/N
   */
  private String deltaReview;
  /**
   * PIDC Version ID
   */
  private long pidcVersID;
  /**
   * PIDC Version Name
   */
  private String pidcVersName;
  /**
   * A2L File ID
   */
  private long a2lFileID;
  /**
   * A2L file name
   */
  private String a2lFileName;
  /**
   * SDOM PVER variant
   */
  private String sdomPverVariant;

  /**
   * Map of input files of this review rule
   * <p>
   * Key - review file ID<br>
   * Value - File name
   */
  private Map<Long, String> rvwInputFileMap;
  /**
   * variant name
   */
  private String variantName;
  /**
   * created user name
   */
  private String createdUser;

  /**
   * to indicate the review type
   */
  // ICDM-2584
  private String reviewType;

  /**
   * to indicate the lock status
   */
  // ICDM-2584
  private String lockStatus;

  /**
   * Constructor
   */
  public ReviewDetails() {
    this.rvwInputFileMap = new HashMap<>();
  }

  /**
   * @return the rvwID
   */
  public long getRvwID() {
    return this.rvwID;
  }

  /**
   * @param rvwID the rvwID to set
   */
  public void setRvwID(final long rvwID) {
    this.rvwID = rvwID;
  }

  /**
   * @return the rvwDesc
   */
  public String getRvwDesc() {
    return this.rvwDesc;
  }

  /**
   * @param rvwDesc the rvwDesc to set
   */
  public void setRvwDesc(final String rvwDesc) {
    this.rvwDesc = rvwDesc;
  }

  /**
   * @return the sourceType
   */
  public String getSourceType() {
    return this.sourceType;
  }

  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * @return the rvwDate
   */
  public String getRvwDate() {
    return this.rvwDate;
  }

  /**
   * @param rvwDate the rvwDate to set
   */
  public void setRvwDate(final String rvwDate) {
    this.rvwDate = rvwDate;
  }

  /**
   * @return the pidcVersID
   */
  public long getPidcVersID() {
    return this.pidcVersID;
  }


  /**
   * @param pidcVersID the pidcVersID to set
   */
  public void setPidcVersID(final long pidcVersID) {
    this.pidcVersID = pidcVersID;
  }


  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }


  /**
   * @param pidcVersName the pidcVersName to set
   */
  public void setPidcVersName(final String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }


  /**
   * @return the a2lFileID
   */
  public long getA2lFileID() {
    return this.a2lFileID;
  }


  /**
   * @param a2lFileID the a2lFileID to set
   */
  public void setA2lFileID(final long a2lFileID) {
    this.a2lFileID = a2lFileID;
  }


  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }


  /**
   * @param a2lFileName the a2lFileName to set
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }


  /**
   * @return the sdomPverVariant
   */
  public String getSdomPverVariant() {
    return this.sdomPverVariant;
  }


  /**
   * @param sdomPverVariant the sdomPverVariant to set
   */
  public void setSdomPverVariant(final String sdomPverVariant) {
    this.sdomPverVariant = sdomPverVariant;
  }

  /**
   * @return the deltaReview
   */
  public String getDeltaReview() {
    return this.deltaReview;
  }

  /**
   * @param deltaReview the deltaReview to set
   */
  public void setDeltaReview(final String deltaReview) {
    this.deltaReview = deltaReview;
  }

  /**
   * @return the rvwInputFileMap
   */
  public Map<Long, String> getRvwInputFileMap() {
    return this.rvwInputFileMap;
  }

  /**
   * @param rvwInputFileMap the rvwInputFileMap to set
   */
  public void setRvwInputFileMap(final Map<Long, String> rvwInputFileMap) {
    this.rvwInputFileMap = rvwInputFileMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [rvwID=" + this.rvwID + ", rvwDesc=" + this.rvwDesc + ", sourceType=" +
        this.sourceType + ", rvwDate=" + this.rvwDate + ", deltaReview=" + this.deltaReview + ", pidcVersName=" +
        this.pidcVersName + "]";
  }

  /**
   * @param variantName variantName
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }

  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }

  /**
   * @param createdUser createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;

  }

  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @return the reviewType
   */
  public String getReviewType() {
    return this.reviewType;
  }


  /**
   * @param reviewType the reviewType to set
   */
  public void setReviewType(final String reviewType) {
    this.reviewType = reviewType;
  }


  /**
   * @return the lockStatus
   */
  public String getLockStatus() {
    return this.lockStatus;
  }


  /**
   * @param lockStatus the lockStatus to set
   */
  public void setLockStatus(final String lockStatus) {
    this.lockStatus = lockStatus;
  }

}
