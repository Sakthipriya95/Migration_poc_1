/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.ComplianceReview;
import com.bosch.boot.ssd.api.model.Participant;
import com.bosch.boot.ssd.api.model.ReviewDetails;

/**
 *
 */
public class ComplianceReviewTest {

  /**
   * Test getter and setter of BuId
   */
  @Test
  public void testGetSetBuId() {
    ComplianceReview review = new ComplianceReview();
    review.setBuId(123);
    Assert.assertEquals(Integer.valueOf(123), review.getBuId());
  }

  /**
   * Test getter and setter
   */
  @Test
  public void testGetSetCommonReviewDetailWithEmptyReviewDetails() {
    ComplianceReview review = new ComplianceReview();
    ReviewDetails details = new ReviewDetails();
    review.setCommonReviewDetail(details);
    Assert.assertEquals(details, review.getCommonReviewDetail());
  }

  /**
   * Test getter and setter of ParticipantsList
   */
  @Test
  public void testGetSetParticipantsList() {
    ComplianceReview review = new ComplianceReview();
    List<Participant> participants = new ArrayList<>();
    Participant participant1 = new Participant();
    participant1.setParticipantTypeId(1);
    participant1.setParticipantUserNTID("TUD1COB");
    participants.add(participant1);
    review.setParticipantsList(participants);
    Assert.assertEquals(participants, review.getParticipantsList());
  }

  /**
   * Test getter and setter of checklist names
   */
  @Test
  public void testGetSetCheckListNames() {
    ComplianceReview review = new ComplianceReview();
    List<String> checkListNames = Arrays.asList("Checklist 1", "Checklist 2");
    review.setCheckListNames(checkListNames);
    Assert.assertEquals(checkListNames, review.getCheckListNames());
  }

  /**
   * Test EmptyParticipantsListEmptyCheck
   */
  @Test
  public void testEmptyParticipantsListEmptyCheck() {
    ComplianceReview review = new ComplianceReview();
    List<Participant> participants = new ArrayList<>();
    review.setParticipantsList(participants);
    Assert.assertTrue(review.getParticipantsList().isEmpty());
  }

  /**
   * Test CommonReviewDetail null check
   */
  @Test
  public void testNullCommonReviewDetailEmptyCheck() {
    ComplianceReview review = new ComplianceReview();
    review.setCommonReviewDetail(null);
    Assert.assertNull(review.getCommonReviewDetail());
  }

  /**
   * Test Empty check list names
   */
  @Test
  public void testEmptyCheckListNames() {
    ComplianceReview review = new ComplianceReview();
    List<String> checkListNames = new ArrayList<>();
    review.setCheckListNames(checkListNames);
    Assert.assertTrue(review.getCheckListNames().isEmpty());
  }
}
