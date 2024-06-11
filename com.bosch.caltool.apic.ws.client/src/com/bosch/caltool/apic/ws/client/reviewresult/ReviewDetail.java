/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.reviewresult;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.apic.ws.client.APICStub.ReviewResultsType;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author imi2si
 */
public class ReviewDetail implements Comparable<ReviewDetail> {

  private final ReviewResultsType wsResponse;

  /**
   * @return the wsResponse
   */
  public ReviewResultsType getWsResponse() {
    return this.wsResponse;
  }

  private final String paramName;
  private final Set<ReviewDetail> childReviews = new TreeSet<>();
  private boolean hasParent;

  /**
   * @return the hasParent
   */
  public boolean isHasParent() {
    return this.hasParent;
  }

  public ReviewDetail(final ReviewResultsType wsResponse, final String paramName) {
    this.wsResponse = wsResponse;
    this.paramName = paramName;
  }

  public static ReviewDetail createReviewDetailCopy(final ReviewDetail reviewDetail) {
    return new ReviewDetail(reviewDetail.getWsResponse(), reviewDetail.getParamName());
  }

  public void addChildDetail(final ReviewDetail reviewDetails) {
    this.childReviews.add(reviewDetails);
  }

  public boolean hasChildRevDetails() {
    return !this.childReviews.isEmpty();
  }

  public Set<ReviewDetail> getChildDetails() {
    return this.childReviews;
  }

  public long getPidcId() {
    return this.wsResponse.getPidcId().getPidcVersion().getPidcVersionId();

  }

  public String getParamName() {
    return this.paramName;
  }

  public String getPidcName() {
    return this.wsResponse.getPidcId().getName();
  }


  public Long getVariantId() {
    return this.wsResponse.isVariantIdSpecified() ? this.wsResponse.getVariantId().getId() : new Long(-1);
  }

  public String getVariantName() {
    return this.wsResponse.isVariantIdSpecified() ? this.wsResponse.getVariantId().getName() : new String("");
  }

  public Date getDateOfReview() {
    return this.wsResponse.isReviewDateSpecified() ? this.wsResponse.getReviewDate().getTime() : null;
  }

  public String getDateOfReviewString(final String dateFormat) {
    Locale.getDefault(Locale.Category.FORMAT);
    SimpleDateFormat date = new SimpleDateFormat(dateFormat, Locale.getDefault(Locale.Category.FORMAT));
    return date.format(getDateOfReview());
  }

  public String getDateOfReviewString() {
    return getDateOfReviewString("yyyy-MM-dd HH:mm");
  }

  public long getReviewResultId() {
    return this.wsResponse.getReviewId();
  }

  public String getReviewName() {
    return this.wsResponse.getReviewName();
  }

  public String getReviewComment() {
    return this.wsResponse.isCommentSpecified() ? this.wsResponse.getComment() : new String("");
  }

  public String getReviewResult() {
    return this.wsResponse.isReviewResultSpecified() ? this.wsResponse.getReviewResult() : new String("");
  }

  public String getReviewDescription() {
    return this.wsResponse.isReviewDescriptionSpecified() ? this.wsResponse.getReviewDescription() : new String("");
  }

  /**
   * @param hasParent hasParent
   */
  public void setHasParent(final boolean hasParent) {
    this.hasParent = hasParent;
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
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getParamName(), ((ReviewDetail) obj).getParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewDetail arg0) {
    String name = getParamName();
    String name2 = arg0.getParamName();
    if ((name == null) && (name2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    if (name == null) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    if (name2 == null) {
      // second String is NULL => return GREATER THAN
      return 1;
    }

    // both String are not NULL, compare them
    final Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.IDENTICAL);
    return collator.compare(name, name2);
  }


}
