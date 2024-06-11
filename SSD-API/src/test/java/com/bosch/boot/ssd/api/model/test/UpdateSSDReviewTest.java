/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import java.math.BigDecimal;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.UpdateSSDReview;

/**
 *
 */
public class UpdateSSDReviewTest {

  /**
   * Test UpdateSSDReview
   */
  @Test
  public void testUpdateSSDReview() {
    // Create an instance of UpdateSSDReview
    UpdateSSDReview updateSSDReview = new UpdateSSDReview();

    // Test the getters and setters for ssdReviewId
    BigDecimal ssdReviewId = new BigDecimal("12345");
    updateSSDReview.setSsdReviewId(ssdReviewId);
    Assert.assertEquals(ssdReviewId, updateSSDReview.getSsdReviewId());

    // Test the getters and setters for reviewLink
    String reviewLink = "https://example.com/review";
    updateSSDReview.setReviewLink(reviewLink);
    Assert.assertEquals(reviewLink, updateSSDReview.getReviewLink());

    // Test the getters and setters for reviewId
    String reviewId = "REVIEW123";
    updateSSDReview.setReviewId(reviewId);
    Assert.assertEquals(reviewId, updateSSDReview.getReviewId());
  }
}
