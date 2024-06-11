/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.ReviewResult;

/**
 *
 */
public class ReviewResultTest {

  /**
   * Test getters and setters of Review Result
   */
  @Test
  public void testReviewResult() {
    // Create an instance of ReviewResult
    ReviewResult reviewResult = new ReviewResult();

    // Test the getters and setters for ssdId
    String ssdId = "12345";
    reviewResult.setSsdId(ssdId);
    Assert.assertEquals(ssdId, reviewResult.getSsdId());

    // Test the getters and setters for reviewIDStr
    String reviewIDStr = "98765";
    reviewResult.setReviewIDStr(reviewIDStr);
    Assert.assertEquals(reviewIDStr, reviewResult.getReviewIDStr());

    // Test the getters and setters for returnMsg
    String returnMsg = "Success";
    reviewResult.setReturnMsg(returnMsg);
    Assert.assertEquals(returnMsg, reviewResult.getReturnMsg());

    // Test the getters and setters for directLink
    String directLink = "https://example.com";
    reviewResult.setDirectLink(directLink);
    Assert.assertEquals(directLink, reviewResult.getDirectLink());
  }
}

