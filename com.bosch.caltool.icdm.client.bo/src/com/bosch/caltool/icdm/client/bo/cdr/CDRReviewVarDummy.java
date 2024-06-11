/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;


/**
 * @author rgo7cob
 */
public class CDRReviewVarDummy {

  /**
   * cdr result object
   */
  private final ReviewResultData reviewResultData;

  /**
   * hashcode constant
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * @param dataProvider dataProvider
   * @param objID objID
   * @param result result object
   */
  public CDRReviewVarDummy(final ReviewResultData result) {
    this.reviewResultData = result;
  }

  /**
   * @return
   */
  public ReviewVariantModel getReviewVariant() {

    ReviewVariantModel reviewVariant = new ReviewVariantModel();

    reviewVariant.setReviewResultData(this.reviewResultData);
    reviewVariant.setRvwVariant(new RvwVariant());

    return reviewVariant;

  }

  @Override
  public boolean equals(final Object obj) {
    // both objects same
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    // use id to check equality.
    return CommonUtils.isEqual(this.reviewResultData.getCdrReviewResult().getId(),
        ((CDRReviewVarDummy) obj).reviewResultData.getCdrReviewResult().getId());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int hashCode = 1;
    hashCode = (HASHCODE_PRIME * hashCode) + ((this.reviewResultData.getCdrReviewResult().getId() == null) ? 0
        : this.reviewResultData.getCdrReviewResult().getId().hashCode());
    return hashCode;
  }

}
