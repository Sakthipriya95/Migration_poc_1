/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity.test;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwReview;
import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwRule;

/**
 * @author TUD1COB
 */
public class TLdb2SsdCrwReviewTest {

  /**
   * Test getters and setters
   */
  @Test
  public void testGetterAndSetterMethods() {
    // Create an instance of TLdb2SsdCrwReview
    TLdb2SsdCrwReview review = new TLdb2SsdCrwReview();

    // Set values using setter methods
    BigDecimal ssdRvwId = BigDecimal.valueOf(123);
    review.setSsdRvwId(ssdRvwId);

    String createdBy = "John Doe";
    review.setCreatedBy(createdBy);

    LocalDateTime createdDate = LocalDateTime.now();
    review.setCreatedDate(createdDate);

    String modifiedBy = "Jane Smith";
    review.setModifiedBy(modifiedBy);

    LocalDateTime modifiedDate = LocalDateTime.now();
    review.setModifiedDate(modifiedDate);

    LocalDateTime reviewClosedDate = LocalDateTime.now();
    review.setReviewClosedDate(reviewClosedDate);

    String reviewCreatedBy = "John Smith";
    review.setReviewCreatedBy(reviewCreatedBy);

    String reviewId = "12345";
    review.setReviewId(reviewId);

    BigDecimal status = BigDecimal.valueOf(1);
    review.setStatus(status);

    BigDecimal decision = BigDecimal.valueOf(2);
    review.setDecision(decision);

    Set<TLdb2SsdCrwRule> crwRules = new HashSet<>();
    review.settLdb2SsdCrwRules(crwRules);

    String reviewLink = "https://example.com";
    review.setReviewLink(reviewLink);

    String statusDescription = "In progress";
    review.setStatusDescription(statusDescription);

    String errorDescription = "Error occurred";
    review.setErrorDescription(errorDescription);

    // Verify the values using getter methods
    Assert.assertEquals(ssdRvwId, review.getSsdRvwId());
    Assert.assertEquals(createdBy, review.getCreatedBy());
    Assert.assertEquals(createdDate, review.getCreatedDate());
    Assert.assertEquals(modifiedBy, review.getModifiedBy());
    Assert.assertEquals(modifiedDate, review.getModifiedDate());
    Assert.assertEquals(reviewClosedDate, review.getReviewClosedDate());
    Assert.assertEquals(reviewCreatedBy, review.getReviewCreatedBy());
    Assert.assertEquals(reviewId, review.getReviewId());
    Assert.assertEquals(status, review.getStatus());
    Assert.assertEquals(decision, review.getDecision());
    Assert.assertEquals(crwRules, review.gettLdb2SsdCrwRules());
    Assert.assertEquals(reviewLink, review.getReviewLink());
    Assert.assertEquals(statusDescription, review.getStatusDescription());
    Assert.assertEquals(errorDescription, review.getErrorDescription());
  }
}
