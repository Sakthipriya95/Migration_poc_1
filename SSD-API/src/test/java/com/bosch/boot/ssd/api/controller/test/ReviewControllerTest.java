/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bosch.boot.ssd.api.controller.ReviewController;
import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwReview;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.InputReviewDetails;
import com.bosch.boot.ssd.api.model.ReviewResult;
import com.bosch.boot.ssd.api.repository.SSDReviewRepository;
import com.bosch.boot.ssd.api.service.ReviewService;

/**
 * @author TAB1JA
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ReviewControllerTest {

  @InjectMocks
  private ReviewController reviewController;
  
  @Mock
  private SSDReviewRepository ssdReviewRepository;
  
  @Mock
  private ReviewService reviewService;
  
  private TLdb2SsdCrwReview tLdb2SsdCrwReview=new TLdb2SsdCrwReview();
  
  private InputReviewDetails inputReviewDetails=new InputReviewDetails();
  
  private ReviewResult mockReviewResult=new ReviewResult();
  /**
   * 
   */
  @Before()
  public void setUp() {
    this.mockReviewResult.setDirectLink("Sample_link");
    this.mockReviewResult.setReturnMsg("Success");
    this.mockReviewResult.setReviewIDStr("RT1234");
    this.mockReviewResult.setSsdId("1234");
    
    
    this.inputReviewDetails.setUserId("ABC");
    this.inputReviewDetails.setReviewName("Compliance SSD Rules Review");
    this.inputReviewDetails.setReviewId("1234");
    this.inputReviewDetails.setReviewRemarks("Review Created from SSD API TEST");
    this.inputReviewDetails.setReviewers(new String[] {"DEF","GHI"});
   
  }
  
  
  /**
   * 
   */
  @Test
  public void updateReviewStatusTest() { 
    try {
      Optional<TLdb2SsdCrwReview> oReview= Optional.of(this.tLdb2SsdCrwReview);
      when(this.ssdReviewRepository.findByReviewId("1234")).thenReturn(oReview);
      ResponseEntity<ReviewResult> reviewResult=this.reviewController.updateReviewStatus("1234", BigDecimal.valueOf(5), BigDecimal.valueOf(1));
      assertEquals(200, reviewResult.getStatusCodeValue());
    }
    catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  @Test
  public void updateReviewStatusWithoutStatusTest() { 
    try {
      Optional<TLdb2SsdCrwReview> oReview= Optional.of(this.tLdb2SsdCrwReview);
      when(this.ssdReviewRepository.findByReviewId("1234")).thenReturn(oReview);
      ResponseEntity<ReviewResult> reviewResult=this.reviewController.updateReviewStatus("1234", null, BigDecimal.valueOf(1));
      assertEquals(500, reviewResult.getStatusCodeValue());
    }
    catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 
   */
  @Test
  public void updateReviewStatusWithoutDecisionTest() { 
    try {
      Optional<TLdb2SsdCrwReview> oReview= Optional.of(this.tLdb2SsdCrwReview);
      when(this.ssdReviewRepository.findByReviewId("1234")).thenReturn(oReview);
      ResponseEntity<ReviewResult> reviewResult=this.reviewController.updateReviewStatus("1234", BigDecimal.valueOf(5), null);
      assertEquals(500, reviewResult.getStatusCodeValue());
    }
    catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 
   */
  @Test
  public void createAndUpdateReviewTest() { 
     try {
       Optional<TLdb2SsdCrwReview> oReview= Optional.of(this.tLdb2SsdCrwReview);
       ResponseEntity<ReviewResult> response= new ResponseEntity<>(this.mockReviewResult,HttpStatus.OK);
       when(this.reviewService.callCreateReviewService(this.inputReviewDetails)).thenReturn(response);  
       when(this.ssdReviewRepository.findById(new BigDecimal(this.inputReviewDetails.getReviewId()))).thenReturn(oReview);    
       ReviewResult result=this.reviewController.createAndUpdateReview(this.inputReviewDetails);
       assertEquals("Success",result.getReturnMsg());
       
    }
    catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
  }
  
}
