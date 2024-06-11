/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class ReviewVariantModel implements Comparable<ReviewVariantModel> {


  private RvwVariant rvwVariant;

  private ReviewResultData reviewResultData;

  private boolean deleted;

  private boolean isMappedvariant;


  /**
   * @return the reviewResult
   */
  public ReviewResultData getReviewResultData() {
    return this.reviewResultData;
  }


  /**
   * @param reviewResult the reviewResultet
   */
  public void setReviewResultData(final ReviewResultData reviewResultData) {
    this.reviewResultData = reviewResultData;
  }


  /**
   * @return the isMappedvariant
   */
  public boolean isMappedvariant() {
    return this.isMappedvariant;
  }


  /**
   * @param isMappedvariant the isMappedvariant to set
   */
  public void setMappedvariant(final boolean isMappedvariant) {
    this.isMappedvariant = isMappedvariant;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewVariantModel o) {
    if ((getRvwVariant().getId() != null) && (o.getRvwVariant().getId() != null)) {
      return ModelUtil.compare(getRvwVariant().getId(), o.getRvwVariant().getId());
    }
    return ModelUtil.compare(this.reviewResultData.getCdrReviewResult().getId(),
        o.reviewResultData.getCdrReviewResult().getId());
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
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
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
    if ((getRvwVariant().getId() != null) && (((ReviewVariantModel) obj).getRvwVariant().getId() != null)) {
      return ModelUtil.isEqual(getRvwVariant().getId(), ((ReviewVariantModel) obj).getRvwVariant().getId());
    }

    return ModelUtil.isEqual(getReviewResultData().getCdrReviewResult().getId(),
        ((ReviewVariantModel) obj).getReviewResultData().getCdrReviewResult().getId());


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getRvwVariant().getId());
  }

}
