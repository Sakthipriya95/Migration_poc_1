/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.InputReviewDetails;

/**
 * @author TUD1COB
 */
public class InputReviewDetailsTest {

  /**
   * Test getters and setters of UserID
   */
  @Test
  public void testGetSetUserId() {
    InputReviewDetails reviewDetails = new InputReviewDetails();
    reviewDetails.setUserId("user123");
    Assert.assertEquals("user123", reviewDetails.getUserId());
  }

  /**
   * Test getters and setters ReviewId
   */
  @Test
  public void testGetSetReviewId() {
    InputReviewDetails reviewDetails = new InputReviewDetails();
    reviewDetails.setReviewId("review123");
    Assert.assertEquals("review123", reviewDetails.getReviewId());
  }

  /**
   * Test getters and setters ReviewName
   */
  @Test
  public void testGetSetReviewName() {
    InputReviewDetails reviewDetails = new InputReviewDetails();
    reviewDetails.setReviewName("Sample Review");
    Assert.assertEquals("Sample Review", reviewDetails.getReviewName());
  }

  /**
   * Test getters and setters ReviewRemarks
   */
  @Test
  public void testGetSetReviewRemarks() {
    InputReviewDetails reviewDetails = new InputReviewDetails();
    reviewDetails.setReviewRemarks("Sample remarks");
    Assert.assertEquals("Sample remarks", reviewDetails.getReviewRemarks());
  }

  /**
   * Test getters and setters Reviewers
   */
  @Test
  public void testGetSetReviewers() {
    InputReviewDetails reviewDetails = new InputReviewDetails();
    String[] reviewers = { "user1", "user2", "user3" };
    reviewDetails.setReviewers(reviewers);
    Assert.assertArrayEquals(reviewers, reviewDetails.getReviewers());
  }

  /**
   * Test getters and setters empty reviewers
   */
  @Test
  public void testEmptyReviewers() {
    InputReviewDetails reviewDetails = new InputReviewDetails();
    String[] reviewers = new String[0];
    reviewDetails.setReviewers(reviewers);
    Assert.assertArrayEquals(reviewers, reviewDetails.getReviewers());
  }
}
