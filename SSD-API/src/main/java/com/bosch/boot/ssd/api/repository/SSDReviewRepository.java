/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwReview;


/**
 * @author GDH9COB
 *
 */
@Repository
public interface SSDReviewRepository extends JpaRepository<TLdb2SsdCrwReview, BigDecimal> {
  
  /**
   * @param status
   * @param reviewId
   */
  @Modifying
  @Query("update TLdb2SsdCrwReview u set u.status = :status where u.reviewId = :reviewId")
  void updateReviewStatus(@Param("status") BigDecimal status, @Param("reviewId") String reviewId);
  
  /**
   * @param status
   * @param reviewClosedDate
   * @param reviewId
   */
  @Modifying
  @Query("update TLdb2SsdCrwReview u set u.status = :status, u.reviewClosedDate =:reviewClosedDate where u.reviewId = :reviewId")
  void updateReviewStatusAndReviewClosedDate(@Param("status") BigDecimal status, @Param("reviewClosedDate") LocalDateTime reviewClosedDate, @Param("reviewId") String reviewId);

  /**
   * @param status
   * @param reviewClosedDate
   * @param decision
   * @param reviewId
   */
  @Modifying
  @Query("update TLdb2SsdCrwReview u set u.status = :status, u.reviewClosedDate =:reviewClosedDate, u.decision=:decision where u.reviewId = :reviewId")
  void updateReviewStatusAndReviewClosedDateAndDecision(@Param("status") BigDecimal status, 
      @Param("reviewClosedDate") LocalDateTime reviewClosedDate, @Param("decision")BigDecimal decision, @Param("reviewId") String reviewId);

  /**
   * @param reviewId
   * @return
   */
  public Optional<TLdb2SsdCrwReview> findByReviewId(String reviewId);
  
  
  /**
   * @param password for the new user created
   */
  @Procedure(name = "UpdateUserAccess")
  public void createSSDUserProc(@Param("p1_password")String password);

}
