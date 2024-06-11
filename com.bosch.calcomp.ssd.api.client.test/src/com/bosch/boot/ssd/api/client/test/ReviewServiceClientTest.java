/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.client.test;


import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.bosch.calcomp.ssd.api.client.model.InputReviewDetailsModel;
import com.bosch.calcomp.ssd.api.client.model.ReviewResultModel;
import com.bosch.calcomp.ssd.api.service.client.ReviewServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author TAB1JA
 */
public class ReviewServiceClientTest {

  // Server
  private final String targetUrl = "https://si-cdm01.de.bosch.com:8643/ssdapiservice/reviewservice";

  /**
   *
   */
  @Test
  public void testCreateAndUpdateReview() {
    try {
      ReviewServiceClient reviewServiceClient = new ReviewServiceClient();

      Response response = reviewServiceClient.uploadReviewFile(this.targetUrl, "1563", "Resources\\Review.pdf");

      Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());

      InputReviewDetailsModel reviewModel = buildInputJson();

      ReviewResultModel resultModel = reviewServiceClient.createAndUpdateReview(reviewModel, this.targetUrl);

      ObjectMapper objectMapper = new ObjectMapper();

      JsonNode jsonNode = objectMapper.readTree(resultModel.getJsonObject());

      Assert.assertNotNull(jsonNode.get("result").get("DirectLink"));
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   *
   */
  private InputReviewDetailsModel buildInputJson() {
    InputReviewDetailsModel inputModel = new InputReviewDetailsModel();
    inputModel.setUserId(System.getenv("USERNAME"));
    inputModel.setReviewName("Compliance SSD Rules Review");
    inputModel.setReviewRemarks("Testcase");
    inputModel.setReviewId("1563");
    String a[] = new String[] { "SMN6KOR" };
    inputModel.setReviewers(a);
    return inputModel;
  }


}
