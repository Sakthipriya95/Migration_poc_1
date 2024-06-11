/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.ReviewDetails;

/**
 * @author TUD1COB
 */
public class ReviewDetailsTest {

  /**
   * Test getters and setters of ReviewName
   */
  @Test
  public void testGetSetReviewName() {
    ReviewDetails reviewDetails = new ReviewDetails();
    reviewDetails.setReviewName("Sample Review");
    Assert.assertEquals("Sample Review", reviewDetails.getReviewName());
  }

  /**
   * Test getters and setters ReviewPlanStrtDt
   */
  @Test
  public void testGetSetReviewPlanStrtDt() {
    ReviewDetails reviewDetails = new ReviewDetails();
    reviewDetails.setReviewPlanStrtDt("2023-06-01");
    Assert.assertEquals("2023-06-01", reviewDetails.getReviewPlanStrtDt());
  }

  /**
   * Test getters and setters IsChklstCnfReq
   */
  @Test
  public void testGetSetIsChklstCnfReq() {
    ReviewDetails reviewDetails = new ReviewDetails();
    reviewDetails.setIsChklstCnfReq("Yes");
    Assert.assertEquals("Yes", reviewDetails.getIsChklstCnfReq());
  }

  /**
   * Test getters and setters ReviewRemarks
   */
  @Test
  public void testGetSetReviewRemarks() {
    ReviewDetails reviewDetails = new ReviewDetails();
    reviewDetails.setReviewRemarks("Sample remarks");
    Assert.assertEquals("Sample remarks", reviewDetails.getReviewRemarks());
  }
}
