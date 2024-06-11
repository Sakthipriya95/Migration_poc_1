/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

/**
 * @author say8cob
 */
public class ReviewResultDeleteValidation {


  private boolean hasAttchments;

  private boolean hasChildAttachment;

  private boolean isDeletable;

  private boolean canUsrDelReview;

  private StringBuilder tRvwParamSet;

  private boolean hasChildReview;

  private boolean usedInCDFXDelivery;


  /**
   * @return the hasChildReview
   */
  public boolean isHasChildReview() {
    return this.hasChildReview;
  }


  /**
   * @param hasChildReview the hasChildReview to set
   */
  public void setHasChildReview(final boolean hasChildReview) {
    this.hasChildReview = hasChildReview;
  }


  /**
   * @return the hasAttchments
   */
  public boolean isHasAttchments() {
    return this.hasAttchments;
  }


  /**
   * @param hasAttchments the hasAttchments to set
   */
  public void setHasAttchments(final boolean hasAttchments) {
    this.hasAttchments = hasAttchments;
  }


  /**
   * @return the hasChildAttachment
   */
  public boolean isHasChildAttachment() {
    return this.hasChildAttachment;
  }


  /**
   * @param hasChildAttachment the hasChildAttachment to set
   */
  public void setHasChildAttachment(final boolean hasChildAttachment) {
    this.hasChildAttachment = hasChildAttachment;
  }


  /**
   * @return the canDelete
   */
  public boolean isDeletable() {
    return this.isDeletable;
  }


  /**
   * @param canDelete the canDelete to set
   */
  public void setDeletable(final boolean canDelete) {
    this.isDeletable = canDelete;
  }


  /**
   * @return the canUsrDelReview
   */
  public boolean isCanUsrDelReview() {
    return this.canUsrDelReview;
  }


  /**
   * @param canUsrDelReview the canUsrDelReview to set
   */
  public void setCanUsrDelReview(final boolean canUsrDelReview) {
    this.canUsrDelReview = canUsrDelReview;
  }


  /**
   * @return the tRvwParamSet
   */
  public StringBuilder gettRvwParamSet() {
    return this.tRvwParamSet;
  }


  /**
   * @param tRvwParamSet the tRvwParamSet to set
   */
  public void settRvwParamSet(final StringBuilder tRvwParamSet) {
    this.tRvwParamSet = tRvwParamSet;
  }


  /**
   * @return the usedInCDFXDelivery
   */
  public boolean isUsedInCDFXDelivery() {
    return this.usedInCDFXDelivery;
  }


  /**
   * @param usedInCDFXDelivery the usedInCDFXDelivery to set
   */
  public void setUsedInCDFXDelivery(final boolean usedInCDFXDelivery) {
    this.usedInCDFXDelivery = usedInCDFXDelivery;
  }


}
