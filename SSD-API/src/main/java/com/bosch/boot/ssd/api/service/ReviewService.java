/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bosch.boot.ssd.api.model.ComplianceReview;
import com.bosch.boot.ssd.api.model.InputReviewDetails;
import com.bosch.boot.ssd.api.model.Participant;
import com.bosch.boot.ssd.api.model.ReviewDetails;
import com.bosch.boot.ssd.api.model.ReviewResult;
import com.bosch.boot.ssd.api.util.MessageConstants;
import com.bosch.boot.ssd.api.util.SSDAPIUtil;

/**
 * @author ICP1COB
 */
@Service
public class ReviewService {

  @Value("${createReview.url}")
  private String createReview;

  @Value("${review.author}")
  private int authorId;

  @Value("${review.reviewer}")
  private int reviewerId;

  @Value("${review.buId}")
  private Integer buId;

  @Value("${review.lean}")
  private String leanReview;

  /**
   * @param input
   * @return
   */
  public ResponseEntity<ReviewResult> callCreateReviewService(final InputReviewDetails input) {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:68.0) Gecko/20100101 Firefox/68.0");
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<ComplianceReview> entity = new HttpEntity<>(setComplianceReview(input), headers);

    String createReviewUrl = createReview.concat(input.getUserId()).concat("/").concat(input.getReviewId());
    URI uri = URI.create(createReviewUrl);
      return restTemplate.exchange(uri, HttpMethod.POST, entity, ReviewResult.class);
  }



  private ComplianceReview setComplianceReview(final InputReviewDetails input) {
    ReviewDetails reviewDetailsBean = new ReviewDetails();
    reviewDetailsBean.setReviewName(input.getReviewName());
    reviewDetailsBean.setReviewPlanStrtDt(SSDAPIUtil.getCurrentDate());
    reviewDetailsBean.setReviewRemarks(input.getReviewRemarks());
    reviewDetailsBean.setIsChklstCnfReq(this.leanReview);

    List<Participant> participants = new ArrayList<>();

    Participant author = new Participant();
    author.setParticipantUserNTID(input.getUserId());
    author.setParticipantTypeId(this.authorId);
    participants.add(author);

    for (String reviewerName : input.getReviewers()) {
      Participant reviewer = new Participant();
      reviewer.setParticipantUserNTID(reviewerName);
      reviewer.setParticipantTypeId(this.reviewerId);
      participants.add(reviewer);
    }

    ComplianceReview complianceReview = new ComplianceReview();
    complianceReview.setBuId(this.buId);
    complianceReview.setCommonReviewDetail(reviewDetailsBean);
    complianceReview.setParticipantsList(participants);
    complianceReview.setCheckListNames(Arrays.asList(MessageConstants.CHECKLIST_NAMES));

    return complianceReview;
  }
}
