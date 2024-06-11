/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ReviewResultType;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class ReviewDetail implements Comparable<ReviewDetail> {

  private final ReviewResultType wsResponse;

  /**
   * @return the wsResponse
   */
  public ReviewResultType getWsResponse() {
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

  public ReviewDetail(final ReviewResultType wsResponse, final String paramName) {
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
    return this.wsResponse.getPidcversion().getId();

  }

  public String getParamName() {
    return this.paramName;
  }

  public String getPidcName() {
    return this.wsResponse.getPidcversion() != null ? this.wsResponse.getPidcversion().getName() : "";
  }


  public Long getVariantId() {
    return this.wsResponse.getPidcVar() != null ? this.wsResponse.getPidcVar().getId() : Long.valueOf(-1);
  }

  public String getVariantName() {
    return this.wsResponse.getPidcVar() != null ? this.wsResponse.getPidcVar().getName() : "";
  }

  public Date getDateOfReview() {
    Date date = null;
    try {
      date = new SimpleDateFormat(DateFormat.DATE_FORMAT_15).parse(this.wsResponse.getReviewDate());
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage());
    }
    return date;

  }

  public String getDateOfReviewString(final String dateFormat) {
    SimpleDateFormat date = new SimpleDateFormat(dateFormat);
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
    return this.wsResponse.getComment() == null ? "" : this.wsResponse.getComment();
  }

  public String getReviewResult() {
    return this.wsResponse.getResult() == null ? "" : this.wsResponse.getResult();
  }

  public String getReviewDescription() {
    return this.wsResponse.getReviewDescription() == null ? "" : this.wsResponse.getReviewDescription();
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
        ModelUtil.isEqual(getParamName(), ((ReviewDetail) obj).getParamName()) &&
        ModelUtil.isEqual(getPidcId(), ((ReviewDetail) obj).getPidcId()) &&
        ModelUtil.isEqual(getReviewResultId(), ((ReviewDetail) obj).getReviewResultId());
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
