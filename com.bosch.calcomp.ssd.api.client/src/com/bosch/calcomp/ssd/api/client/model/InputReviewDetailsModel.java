/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.client.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author TAB1JA
 */
public class InputReviewDetailsModel {

  private String userId;
  private String reviewId;
  private String reviewName;
  private String reviewRemarks;
  private String[] reviewers;


  /**
   * @return the userId
   */
  public String getUserId() {
    return this.userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(final String userId) {
    this.userId = userId;
  }

  /**
   * @return the reviewId
   */
  public String getReviewId() {
    return this.reviewId;
  }

  /**
   * @param reviewId the reviewId to set
   */
  public void setReviewId(final String reviewId) {
    this.reviewId = reviewId;
  }

  /**
   * @return the reviewName
   */
  public String getReviewName() {
    return this.reviewName;
  }

  /**
   * @param reviewName the reviewName to set
   */
  public void setReviewName(final String reviewName) {
    this.reviewName = reviewName;
  }

  /**
   * @return the reviewRemarks
   */
  public String getReviewRemarks() {
    return this.reviewRemarks;
  }

  /**
   * @param reviewRemarks the reviewRemarks to set
   */
  public void setReviewRemarks(final String reviewRemarks) {
    this.reviewRemarks = reviewRemarks;
  }

  /**
   * @return the reviewers
   */
  public String[] getReviewers() {
    return this.reviewers;
  }

  /**
   * @param reviewers the reviewers to set
   */
  public void setReviewers(final String[] reviewers) {
    this.reviewers = reviewers;
  }


  /**
   * @return
   */
  public ObjectNode getComplianceReviewJson() {

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode complianceReviewJson = mapper.createObjectNode();


    complianceReviewJson.put("userId", this.userId);
    complianceReviewJson.put("reviewName", this.reviewName);
    complianceReviewJson.put("reviewRemarks", this.reviewRemarks);
    complianceReviewJson.put("reviewId", this.reviewId);
    complianceReviewJson.putArray("reviewers").addAll(getCheckListNamesJsonArray(mapper));


    return complianceReviewJson;

  }


  /**
   * @return
   */
  private ArrayNode getCheckListNamesJsonArray(final ObjectMapper mapper) {
    ArrayNode participantArray = mapper.createArrayNode();
    for (String name : this.reviewers) {
      participantArray.add(name);
    }
    return participantArray;
  }

}
